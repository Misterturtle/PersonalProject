package tph

import java.io.File
import java.util.concurrent.TimeUnit

import akka.actor.{ActorRef, ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import tph.IrcMessages.ChangeMenu
import tph.research.ircLogicTests

/**
  * Created by rconaway on 2/12/16.
  */
object Main extends App {

  var testMode = true
  val config = ConfigFactory.load()
  val system = ActorSystem("TwitchPlaysHearthstone")
  val screenScraper = new ScreenScraper()
  screenScraper.GetScreen()

  val mouseClicker = new MouseClicker()






  if (!testMode) {



    val ircHost = config.getString("tph.irc.host")
    val ircChannel = config.getString("tph.irc.channel")
    lazy val ircLogic = system.actorOf(Props(new ircLogic(system, controller, hearthstone)), "ircLogic")
    lazy val ircBot = new IrcBot(ircHost, ircChannel, ircLogic)



    lazy val ircLogicTests: ActorRef = system.actorOf(Props(new ircLogicTests(system, controller, ircLogic, gameStatus, logFileReader,ircBot)))
    lazy val controller: ActorRef = system.actorOf(Props(new Controller(system, hearthstone, logFileReader, ircLogic, ircLogicTests)), "Controller")
    lazy val hearthstone: ActorRef = system.actorOf(Props(new Hearthstone(system, controller, config, screenScraper, mouseClicker)), "hearthStone")
    lazy val gameStatus: ActorRef = system.actorOf(Props(new GameStatus(system)), "gameStatus")
    lazy val logFileReader = system.actorOf(Props(new LogFileReader(system, gameLogFile, gameStatus, controller)), "logFileReader")
    lazy val gameLogFile = new File(config.getString("tph.game-log.file"))





    controller ! "init"
    TimeUnit.SECONDS.sleep(15)
    controller ! ChangeMenu("playMenu", "inGame")
    ircLogic ! "Start Game"
    ircLogic ! "Skip Mulligan"
    ircLogic ! "Turn Start"
   // gameStatus ! "Display Status"
  }

  //TEST MODE
  if (testMode) {


    val ircHost = config.getString("tph.irc.host")
    val ircChannel = config.getString("tph.irc.channel")
    //ircBot has disabled functionality for test mode. Remember to remove comments.
    lazy val ircBot = new IrcBot(ircHost, ircChannel, ircLogic)
    lazy val ircLogic = system.actorOf(Props(new ircLogic(system, controller, ircLogicTests)), "ircLogic")


    lazy val ircLogicTests: ActorRef = system.actorOf(Props(new ircLogicTests(system, controller, ircLogic, gameStatus, logFileReader, ircBot)))
    lazy val logFileReader = system.actorOf(Props(new LogFileReader(system, new File("testsituations/blank.txt"), gameStatus, controller)), "logFileReader")
    lazy val controller: ActorRef = system.actorOf(Props(new Controller(system, ircLogicTests, logFileReader, ircLogic, ircLogicTests)), "Controller")
    lazy val gameStatus: ActorRef = system.actorOf(Props(new GameStatus(system)), "gameStatus")






    ircLogicTests ! "Start"
}



}