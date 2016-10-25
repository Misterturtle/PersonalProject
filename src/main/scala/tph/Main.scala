package tph

import java.io.File
import java.util.concurrent.TimeUnit

import akka.actor.{ActorRef, ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import tph.IrcMessages.ChangeMenu

/**
  * Created by rconaway on 2/12/16.
  */
object Main extends App {

  var testMode = false
  val config = ConfigFactory.load()
  val system = ActorSystem("TwitchPlaysHearthstone")
  val screenScraper = new ScreenScraper()
  val mouseClicker = new MouseClicker()


  lazy val controller: ActorRef = system.actorOf(Props(new Controller(system, hearthstone, logFileReader, ircLogic)), "Controller")
  lazy val hearthstone: ActorRef = system.actorOf(Props(new Hearthstone(system, controller, config, screenScraper, mouseClicker)), "hearthStone")
  lazy val gameStatus: ActorRef = system.actorOf(Props(new GameStatus(system)), "gameStatus")
  lazy val logFileReader = system.actorOf(Props(new LogFileReader(system, gameLogFile, gameStatus, controller)), "logFileReader")
  lazy val gameLogFile = new File(config.getString("tph.game-log.file"))


  val ircHost = config.getString("tph.irc.host")
  val ircChannel = config.getString("tph.irc.channel")
  val ircLogic = system.actorOf(Props(new ircLogic(system, controller, hearthstone)), "ircLogic")
  val ircBot = new IrcBot(ircHost, ircChannel, ircLogic)

  if (!testMode) {
    controller ! "init"
    TimeUnit.SECONDS.sleep(15)
    controller ! ChangeMenu("playMenu", "inGame")
    ircLogic ! "Start Game"
    ircLogic ! "Skip Mulligan"
    ircLogic ! "Turn Start"
   // gameStatus ! "Display Status"
  }

  //TEST MODE
  if (testMode == true) {
    
    val testValue = "Delete Me"

    val TDD: ActorRef = system.actorOf(Props(new TDD(system)), "TDD")
    TDD ! "FULL_TEST"
  }



  //gittest
}