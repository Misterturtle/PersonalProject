package rob

import java.io.File

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.{Config, ConfigFactory}
import tph.LogFileReader

object Main extends App {
  val config = ConfigFactory.load()
  new Main(config)
}

class Main(config: Config) {

  val ircHost = config.getString("tph.irc.host")
  val ircChannel = config.getString("tph.irc.channel")
  val gameLogFile = new File(config.getString("tph.game-log.file"))

  val system = ActorSystem("TwitchPlaysHearthstone")

  // reads screen state on demand
  val screenScraper = new ScreenScraper()

  // clicks mouse on demand
  val mouseClicker = new MouseClicker()

  // parses log line on demand
  val logParser = new LogParser()

  // actor: stores game state, handles interactions
  val controller = new Controller(system, screenScraper, mouseClicker, logParser)
  val controllerRef = system.actorOf(Props(controller), "controller")

  // actor: reads game log, sends text to controller
  val gameLogReader = new GameLogReader(gameLogFile, controllerRef)
  val logFileReaderRef = system.actorOf(Props(gameLogReader), "logFileReader")

  // async: reads irc, sends text to Controller
  val ircBot = new IrcBot(ircHost, ircChannel, controllerRef)
}


