package rob

import java.io.File

import akka.actor.Actor.Receive
import akka.actor._
import com.typesafe.config.{Config, ConfigFactory}
import tph.LogFileReader

object Controller extends App {
  val config = ConfigFactory.load()
  val system = ActorSystem("TwitchPlaysHearthstone")
  val hearthstone = makeHearthstone(config,system)

  val controller = new Controller(hearthstone)
  val controllerRef = system.actorOf(Props(controller), "controller")

  makeIrcBotFromConfig(config, controllerRef)

  def makeHearthstone(config:Config, system:ActorSystem):Hearthstone = {
    val screenScraper = new ScreenScraper()
    val mouseClicker = new MouseClicker()
    val logParser = new LogParser()

    val hearthstone = new Hearthstone(screenScraper, mouseClicker, logParser)
    val hearthstoneRef = system.actorOf(Props(hearthstone))

    val gameLogFile = new File(config.getString("tph.game-log.file"))
    val gameLogReader = new GameLogReader(gameLogFile, hearthstoneRef, system.dispatcher)

    hearthstone
  }

  def makeIrcBotFromConfig(config:Config, listener:ActorRef):IrcBot = {
    val ircHost = config.getString("tph.irc.host")
    val ircChannel = config.getString("tph.irc.channel")
    new IrcBot(ircHost, ircChannel, listener)
  }

}

class Controller(hearthstone:Hearthstone) extends Actor with ActorLogging {
  override def receive: Receive = ???
}


