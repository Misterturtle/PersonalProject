package rob

import java.io.File

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

/**
  * Created by rconaway on 2/12/16.
  */
object Main extends App {
  val config = ConfigFactory.load()
  val system = ActorSystem("AcceptanceTests")
  val screenScraper = new ScreenScraper()
  val mouseClicker = new MouseClicker()
  val hearthstone = new Hearthstone(config, screenScraper, mouseClicker)
  val controller = system.actorOf(Props(new Controller(hearthstone)), "Controller")

  val gameLogFile = new File(config.getString("tph.game-log.file"))
  val gameLogReader = new GameLogReader(gameLogFile, controller, system.dispatcher)

  val ircHost = config.getString("tph.irc.host")
  val ircChannel = config.getString("tph.irc.channel")
  val ircBot = new IrcBot(ircHost, ircChannel, controller)

}
