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
  val hearthstone = Controller.makeHearthstone(config, system)

  val ircHost = config.getString("tph.irc.host")
  val ircChannel = config.getString("tph.irc.channel")

  val admin = new Player(ircHost, ircChannel, "admin")
  val lucy = new Player(ircHost, ircChannel, "lucy")
  val ricky = new Player(ircHost, ircChannel, "ricky")
  val ethel = new Player(ircHost, ircChannel, "ethel")
  val fred = new Player(ircHost, ircChannel, "fred")

  override def beforeEach() = {
    if (hearthstone.isPlaying)
      hearthstone.resign()
  }

  override def afterAll() = {
    if (hearthstone.isPlaying)
      hearthstone.resign()

    admin.close()
    lucy.close()
    ricky.close()
    ethel.close()
    fred.close()
  }

  "Wait for turn then end turn" in {
    lucy.waitFor("?vote?").send(":end turn")
    ricky.waitFor("?vote?").send(":play 1")
    ethel.waitFor("?vote?").send(":end turn")

    Controller    // starts game

    admin.waitFor("?vote?")
  }


}
