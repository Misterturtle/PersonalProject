package rob

import java.io.File

import akka.actor.{Props, ActorSystem}
import com.typesafe.config.ConfigFactory
import org.jibble.pircbot.PircBot
import org.scalatest.{BeforeAndAfterEach, BeforeAndAfterAll, FreeSpec, Matchers}

/**
  * Created by rconaway on 1/30/16.
  */
class AcceptanceTests extends FreeSpec with Matchers with BeforeAndAfterAll with BeforeAndAfterEach {

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

  val admin = new Player(ircHost, ircChannel, "tadmin")
  val lucy = new Player(ircHost, ircChannel, "tlucy")
  val ricky = new Player(ircHost, ircChannel, "tricky")
  val ethel = new Player(ircHost, ircChannel, "tethel")
  val fred = new Player(ircHost, ircChannel, "tfred")

  override def beforeAll() = {
    hearthstone.initialize()
  }

  override def beforeEach() = {
    if (hearthstone.isPlaying)
      hearthstone.resign()
    controller ! Controller.Start
  }

  override def afterAll() = {
    if (hearthstone.isPlaying)
      hearthstone.resign()

//    admin.close()
//    lucy.close()
//    ricky.close()
//    ethel.close()
//    fred.close()
  }

  "Wait for turn then end turn" in {
//    lucy.waitFor("?vote?").send(":end turn")
//    ricky.waitFor("?vote?").send(":play 1")
//    ethel.waitFor("?vote?").send(":end turn")
//
//    admin.waitFor("?vote?")
  }

}
