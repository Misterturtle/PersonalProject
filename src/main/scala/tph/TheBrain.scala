package tph

import java.io.File

import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import tph.GUI.GUIDebugInterface
import tph.Main._
import tph.StateManagement._
import tph.tests.MockHearthstone

import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.stage.Stage
import scalafxml.core.{NoDependencyResolver, FXMLLoader}
import scalafx.Includes._

/**
  * Created by Harambe on 1/24/2017.
  */
class TheBrain(testModeActive: Boolean) extends LazyLogging {

  //Create All Systems

  val testMode = testModeActive
  val config = ConfigFactory.load()
  val voteManager = new VoteManager()
  //ircBot has disabled functionality for test mode. Remember to remove comments.
  val ircBot = new IrcBot(voteManager, this)
  val gameStatus = new GameStatus(this)
  val hearthstone = new Hearthstone(this)
  val ircLogic = new ircLogic(this, hearthstone)
  val logFileReader = new LogFileReader(new File(config.getString("tph.game-log.file")), gameStatus, this)
  val mockHearthstone = new MockHearthstone(this)
  val GUI = CreateGUI()

  //Create All States
  lazy val initState = new InitState(this)
  lazy val inGame = new InGame(this)
  lazy val myTurn = new MyTurn(ircLogic, this)
  lazy val hisTurn = new HisTurn(ircLogic, this)
  lazy val inMenu = new InMenu(this, ircLogic)
  lazy val inMulligan = new InMulligan(this)


  var mulliganComplete = false
  var currentMenu = "Uninit"
  var previousMenu = "Uninit"
  var currentStatus: State = initState


  def CreateGUI(): Stage = {

    println("TPH finished setting up. Starting GUI Debug Window")

    val loader = new FXMLLoader(getClass.getResource("/GUIDebugWindow.fxml"), NoDependencyResolver)
    loader.load()

    val root = loader.getRoot[javafx.scene.Parent]
    println(s"root is $root")
    println("root class is " + root.getClass())


    val controller = loader.getController[GUIDebugInterface]
    println("controller is " + controller)



    stage = new JFXApp.PrimaryStage() {

      println("Setting stage and scene")
      title = "Twitch Plays Hearthstone"
      scene = new Scene(root)
    }

    stage
  }


  def GetGameStatus(): FrozenGameStatus = {

    val currentGameStatus = new Array[Player](2)
    currentGameStatus(0) = gameStatus.me
    currentGameStatus(1) = gameStatus.him

    val frozenGameStatus = new FrozenGameStatus(currentGameStatus)

    frozenGameStatus
  }

  def ChangeMenu(newMenu: String): Unit = {

    previousMenu = currentMenu
    currentMenu = newMenu

  }

  def ChangeState(state: State): Unit = {
    currentStatus.active = false
    currentStatus = state

    currentStatus.Activate()

  }

  def StartGame(): Unit = {
    if (logFileReader.readerReady) {
      logger.debug("The brain is starting the game")
      ChangeState(inGame)
    }
  }

  def StartMulligan(): Unit = {
    if (logFileReader.readerReady) {
      logger.debug("The brain is starting the mulligan")
      ChangeState(inMulligan)
    }

  }

  def AddMulliganOption(): Unit = {
    if (logFileReader.readerReady) {
      logger.debug("The brain is adding a mulligan option")
      inMulligan.mulliganOptions += 1
    }
  }

  def SetDiscoverOptions(numOfOptions: Int): Unit = {
    if (logFileReader.readerReady) {
      logger.debug("The brain is setting numOfOptions")
      inGame.discoverOptions = numOfOptions
    }
  }

  def GameOver(): Unit = {
    if (logFileReader.readerReady) {
      logger.debug("The brain has received a Game Over.")
      ircLogic.GameOver()
      gameStatus.Reset()

      ChangeMenu(previousMenu)
      ChangeState(inMenu)
    }

  }

  def Reset(): Unit = {

    logger.debug("Resetting the brain.")
    gameStatus.Reset()
    voteManager.Reset()
    currentMenu = "Uninit"
    previousMenu = "Uninit"
  }


  def VoteEntry(vote: Vote): Unit = {
    if (logFileReader.readerReady)
    ircLogic.VoteEntry(vote)
  }


}
