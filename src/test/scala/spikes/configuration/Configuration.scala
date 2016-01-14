package spikes.configuration

import com.typesafe.config.{ConfigObject, ConfigFactory}
import org.scalatest.{FreeSpec, Matchers}
import collection.JavaConversions._

/**
  * Created by rconaway on 1/1/16.
  */
class Configuration extends FreeSpec with Matchers {

  val config = ConfigFactory.load()

  "reads akka config" in {
    config.getString("akka.loglevel") should be("DEBUG")
  }

  "read screens" in {
    config.getInt("tph.screens.quests.upper-left.x") should be (827)
  }

}
