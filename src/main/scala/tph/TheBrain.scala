package tph

import java.io.File
import java.util.concurrent.TimeUnit

import akka.actor.{ActorSystem, ActorRef, Props, Actor}
import akka.event.LoggingReceive
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import tph.StateManagement._
import tph.tests.MockHearthstone

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
