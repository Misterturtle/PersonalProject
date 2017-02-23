package tph

import java.io.{File, FileWriter, PrintWriter}

import com.typesafe.config.ConfigFactory
import org.jibble.pircbot.PircBot

import scala.util.matching.Regex

/**
  * Created by Harambe on 2/22/2017.
  */

object IRCBot {
  //Emote Type
  val GREETINGS = "!greetings"
  val THANKS = "!thanks"
  val WELL_PLAYED = "!well played"
  val WOW = "!wow"
  val OOPS = "!oops"
  val THREATEN = "!threaten"

  //In Game Always Type
  val WAIT = "!wait"
  val HURRY = "!hurry"
  val CONCEDE = "!concede"
  val NO_CONCEDE = "!noconcede"
  val END_TURN = "!end turn"
  val BIND = "!bind"
  val FUTURE = "!future"

  //Parsing
  val ONE_PART_COMMAND =
    """!(.+)""".r
  val TWO_PART_COMMAND = """!(.+), (.+)""".r
  val THREE_PART_COMMAND = """!(.+), (.+), (.+)""".r
  val PLAY_COMMAND = """play (\d+)""".r
  val ATTACK_COMMAND = """att (.+)""".r
  val MY_REGEX_NUMBER = """my (\d+)""".r
  val HIS_REGEX_NUMBER = """his (\d+)""".r
  val SPOT_COMMAND = """spot (\d+)""".r
  val TARGET_COMMAND = """target (.+)""".r
  val BATTLECRY_COMMAND = """battlecry (.+)""".r
  val HERO_POWER_COMMAND = """hero power (.+)""".r
  val DISCOVER_COMMAND = """discover (\d+)""".r
  val MULLIGAN = """!mulligan(.*)""".r

  //Main Menu
  val PLAY = "!play"
  val COLLECTION = "!collection"
  val OPEN_PACKS = "!open packs"
  val SHOP = "!shop"
  val QUEST_LOG = "!quest log"

  //Play Menu
  val BACK = "!back"
  val CASUAL = "!casual"
  val RANKED = "!ranked"
  val DECK = """!deck (\d+)""".r
  val FIRST_PAGE = "!first page"
  val SECOND_PAGE = "!second page"

  //Quest Menu
  val QUEST =
    """!quest (\d+)""".r

  //All of CollectionMenu will be automated at subscribers request


}

class IRCBot extends PircBot {

  import IRCBot._
  val config = ConfigFactory.load()


  val hostName = config.getString("tph.irc.host")
  val channel = config.getString("tph.irc.channel")
  val nickname = "TPHBot"
  val writer = new PrintWriter(new FileWriter(new File(config.getString("tph.voteLog.path"))))

  setName(nickname)
  setVerbose(false)
  connect(hostName)
  joinChannel(channel)

  override def onMessage(channel: String, sender: String, login: String, hostName: String, message: String): Unit = {



    }
}
