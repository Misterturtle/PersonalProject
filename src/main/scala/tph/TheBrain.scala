package tph

import java.io.File
import java.util.concurrent.TimeUnit

import akka.actor.{ActorSystem, ActorRef, Props, Actor}
import akka.event.LoggingReceive
import com.typesafe.config.ConfigFactory
import tph.StateManagement._

/**
  * Created by Harambe on 1/24/2017.
  */
class TheBrain {

  //Create All Systems

  val config = ConfigFactory.load()
  val system = ActorSystem("TwitchPlaysHearthstone")
  val mouseClicker = new MouseClicker()
  val voteManager = new VoteManager()
  //ircBot has disabled functionality for test mode. Remember to remove comments.
  val ircBot = new IrcBot(voteManager, this)
  val gameStatus = new GameStatus(this)
  val hearthstone = new Hearthstone(this)
  val ircLogic = new ircLogic(this, hearthstone)
  val logFileReader = new LogFileReader(new File("testsituations/blank.txt"), gameStatus, this)

  //Create All States
  val initState = new InitState(this)
  val inGame = new InGame(this)
  val myTurn = new MyTurn(ircLogic)
  val hisTurn = new HisTurn(ircLogic)
  val inMenu = new InMenu(this)
  val inMulligan = new InMulligan(this)


  var currentMenu = "Uninit"
  var previousMenu = "Uninit"
  var currentStatus: State = initState


  def Init(): Unit = {
    logFileReader.Init()
    ChangeMenu("inGame")
    hearthstone.Start()
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
    ircLogic.StartGame()
    ChangeMenu(Constants.MenuNames.IN_GAME)
    ChangeState(inGame)
  }

  def StartMulligan(): Unit = {

    ChangeState(inMulligan)

  }

  def AddMulliganOption(): Unit = {
    inMulligan.mulliganOptions += 1
  }

  def SetDiscoverOptions(numOfOptions: Int): Unit = {
    inGame.discoverOptions = numOfOptions
  }

  def GameOver(): Unit = {
    ircLogic.GameOver()
    gameStatus.Reset()
    ChangeMenu(previousMenu)
    ChangeState(inMenu)
  }


  def VoteEntry(vote: Vote): Unit = {
    ircLogic.VoteEntry(vote)
  }


}
