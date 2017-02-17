package tph.Immutable

import java.io.{FileReader, BufferedReader, File}
import java.util
import java.util.concurrent.TimeUnit

import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import tph.Immutable.HSAction.{CardDrawn, HSAction}

import scala.util.matching.Regex

/**
  * Created by Harambe on 2/14/2017.
  */

object LogFileReader {


  val FRIENDLY_MINION_CONTROLLED =
    """^.+\[name=(.+) id=(\d+) zone=PLAY zonePos=(\d+) .+ zone from FRIENDLY PLAY -> OPPOSING PLAY""".r

  //Enemy Strings
  val ENEMY_MINION_CONTROLLED =
    """^.+\[name=(.+) id=(\d+) zone=PLAY zonePos=(\d+) .+ zone from OPPOSING PLAY -> FRIENDLY PLAY""".r
  val ENEMY_CARD_DRAWN = """^.+id=\d+ local=.+ \[id=(\d+) cardId=.+type=.+zone=HAND zonePos=(\d+) player=(\d+)\] pos from \d+ -> \d+""".r
  val ENEMY_CARD_RETURN = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - id=.+ local=.+ \[name=(.+) id=(\d+) zone=HAND zonePos=.+ cardId=.+ player=(\d+)\] zone from OPPOSING PLAY -> OPPOSING HAND""".r

  //Neutral Strings
  val FACE_ATTACK_VALUE =
    """\[Power\] PowerTaskList.DebugPrintPower\(\) -     TAG_CHANGE Entity=\[name=.+ id=\d+ zone=PLAY zonePos=0 cardId=HERO.+ player=(\d+)] tag=ATK value=(\d+)""".r
  val SECRET_PLAYED = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - TRANSITIONING card .+id=(\d+).+zone=SECRET zonePos=\d+.+player=(\d+)\] to .+ SECRET""".r
  val OLD_ZONE_CHANGE = """^\[Power\] PowerTaskList.+TAG_CHANGE Entity=.+id=(\d+).+zone=(.+) zonePos=.+ player=(\d+)\] tag=ZONE value=(.+)""".r
  val ZONE_CHANGE = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - id=\d+ local=False .+id=(\d+) zone=.+ zonePos=\d+ cardId=.+ player=(\d+)\] zone from (.+) -> (.+)$""".r
  val KNOWN_CARD_DRAWN = """^.+id=\d+ local=False \[name=(.+) id=(\d+) zone=HAND zonePos=(\d+) cardId=.+ player=(\d+)\] pos from \d+ -> \d+""".r
  val CARD_PLAYED = """\[Power\] PowerProcessor.DoTaskListForCard\(\) - unhandled BlockType PLAY for sourceEntity \[name=(.+) id=(\d+) zone=PLAY zonePos=(\d+) cardId=.+ player=(\d+)\]""".r
  val BOARD_POSITION_CHANGE = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - processing index=\d+ change=powerTask=\[power=\[type=TAG_CHANGE entity=\[id=\d+ cardId=.+ name=.+\] tag=ZONE_POSITION value=\d+\] complete=False\] entity=\[name=.+ id=(\d+) zone=PLAY zonePos=\d+ cardId=.+ player=(\d+)] srcZoneTag=INVALID srcPos= dstZoneTag=INVALID dstPos=(\d+)""".r
  val HAND_POSITION_CHANGE = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - processing index=\d+ change=powerTask=\[power=\[type=TAG_CHANGE entity=\[id=\d+ cardId=.+name=.+tag=ZONE_POSITION value=\d+\] complete=False\] entity=.+id=(\d+).+zone=HAND zonePos=(\d+).+player=(\d+).+dstPos=(\d+)""".r
  val CARD_DEATH = """^\[Power\] PowerTaskList.+TAG_CHANGE Entity=\[name=(.+) id=(\d+) zone=.+ zonePos=.+ player=(\d+).+ tag=ZONE value=GRAVEYARD""".r
  val MINION_SUMMONED = """^.+FULL_ENTITY - Updating \[name=(.+) id=(\d+) zone=PLAY zonePos=(\d+).+player=(\d+).+""".r
  val TRANSFORM ="""\[Power\] .+.DebugPrintPower\(\) -     TAG_CHANGE Entity=.+id=(\d+) zone=PLAY.+tag=LINKED_ENTITY value=(\d+)""".r
  val HEX = """\[Zone\] ZoneChangeList.+ \[name=(.+) id=(\d+) zone=PLAY zonePos=.+ cardId=hexfrog player=(\d+)\] pos from 0 -> (\d)""".r
  val DEFINE_PLAYERS = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - TRANSITIONING card \[name=.+ id=.+ zone=PLAY zonePos=0 cardId=.+ player=(\d+)\] to FRIENDLY PLAY \(Hero\)""".r
  val SAP = """\[Power\] PowerTaskList.DebugPrintPower\(\) - BLOCK_START BlockType=POWER Entity=\[name=Sap id=.+ zone=PLAY zonePos=.+ cardId=.+ player=.+\] EffectCardId= EffectIndex=.+ Target=\[name=(.+) id=(\d+) zone=PLAY zonePos=.+ cardId=.+ player=(\d+)\]""".r
  val WEAPON = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - TRANSITIONING card \[name=.+ id=(\d+) zone=PLAY zonePos=0 cardId=.+ player=(\d+)] to .+ PLAY \(Weapon\)""".r

  //IrcLogic Strings
  val MULLIGAN_OPTION =
    """\[Power\] PowerTaskList.DebugPrintPower\(\) -     SHOW_ENTITY - Updating Entity=\[name=UNKNOWN ENTITY \[cardType=INVALID\] id=\d+ zone=DECK zonePos=0 cardId= player=1\] CardID=.+""".r


  //"""\[Zone\] ZoneChangeList.ProcessChanges\(\) - processing index=\d+ change=powerTask=\[power=\[type=SHOW_ENTITY entity=\[id=\d+ cardId=.+ name=\[id=\d+ cardId= type=INVALID zone=DECK zonePos=0 player=\d+\]\] tags=System.Collections.Generic.List`1\[Network\+Entity\+Tag\]\] complete=False\] entity=\[id=\d+ cardId= type=INVALID zone=DECK zonePos=0 player=\d+\] srcZoneTag=INVALID srcPos= dstZoneTag=HAND dstPos=""".r
  val MULLIGAN_START = "[Power] PowerTaskList.DebugPrintPower() -     TAG_CHANGE Entity=GameEntity tag=STEP value=BEGIN_MULLIGAN"
  val DISCOVER_OPTION = """\[Power\] GameState.DebugPrintEntityChoices\(\) -   Entities\[(\d+)\]=\[name=.+ id=\d+ zone=SETASIDE zonePos=0 cardId=.+ player=\d+\]""".r
  val TURN_START = """\[Power\] PowerTaskList.DebugPrintPower\(\) -     TAG_CHANGE Entity=(.+) tag=CURRENT_PLAYER value=1""".r
  val TURN_END = """\[Power\] PowerTaskList.DebugPrintPower\(\) -     TAG_CHANGE Entity=(.+) tag=CURRENT_PLAYER value=0""".r


  //Controller Strings

  val PLAY_MENU = "FSM not Preprocessed: Hero_Armor(Clone) : FSM"
  val COLLECTION_MENU = "FSM not Preprocessed: DeckGlowAll : FSM"
  val START_UP = "FSM not Preprocessed: Startup_Hub : FSM"


}


case class LogFileReader() extends LazyLogging {


  val config = ConfigFactory.load()
  var reader = new BufferedReader(new FileReader(new File(config.getString("tph.game-log.file"))))


  def GetPlayerValues(playerValueList: List[(Int, Int)]): (Int, Int) = {


    playerValueList.last match {

      case (-5, -5) =>
        logger.debug("No players defined in LogFileReader.GetPlayerValues")
        return (1, 2)

      case (1, 2) =>
        (1, 2)

      case (2, 1) =>
        (2, 1)
    }
  }


  def ParseReaderFile(reader: BufferedReader): Unit = {

    //Large section of code here. All regex values to be parsed are going to be defined here


    val streams = Stream.continually(reader.readLine()).takeWhile(_ != null)

    streams.foreach {

      case KNOWN_CARD_DRAWN(name, id, position, player) =>
        GameStatus.(name, id.toInt, position.toInt, player.toInt).Execute()

      case FRIENDLY_MINION_CONTROLLED(name, id, zonePos) =>
        logger.info("Friendly_Minion Controlled: " + line)
        GameStatus.FriendlyMinionControlled(name, id.toInt, zonePos.toInt)

      //Enemy Events
      case ENEMY_CARD_DRAWN(id, position, player) =>
        logger.info("Enemy Card Drawn: " + line)
        gameStatus.EnemyCardDrawnEvent(id.toInt, position.toInt, player.toInt)

      case ENEMY_MINION_CONTROLLED(name, id, zonePos) =>
        logger.info("Enemy Card Controlled: " + line)
        gameStatus.EnemyMinionControlled(name, id.toInt, zonePos.toInt)


      //Neutral Events

      case DISCOVER_OPTION(option) =>
        logger.info("Discover Option: " + option)
        theBrain.SetDiscoverOptions(option.toInt)

      case FACE_ATTACK_VALUE(player, value) =>
        logger.info("Face Value Changed: " + line)
        gameStatus.FaceAttackValueEvent(player.toInt, value.toInt)

      case WEAPON(id, player) =>
        logger.info("Weapon Played: " + line)
        gameStatus.WeaponPlayedEvent(id.toInt, player.toInt)

      case SECRET_PLAYED(id, player) =>
        logger.info("Secret Played: " + line)
        gameStatus.SecretPlayedEvent(id.toInt, player.toInt)

      case OLD_ZONE_CHANGE(id, zone, player, dstZone) if dstZone != "GRAVEYARD" =>
        logger.info("Old Zone Change: " + line)
        gameStatus.OldZoneChangeEvent(id.toInt, zone, player.toInt, dstZone)

      case ZONE_CHANGE(id, player, zone, dstZone) =>
        logger.info("Zone Change: " + line)
        gameStatus.ZoneChangeEvent(id.toInt, player.toInt, zone, dstZone)


      case KNOWN_CARD_DRAWN(name, id, position, player) =>
        logger.info("Known Card Drawn: " + line)
        gameStatus.KnownCardDrawn(name, id.toInt, position.toInt, player.toInt)

      case SAP(name, id, player) =>
        logger.info("Sap: " + line)
        gameStatus.Sap(name, id.toInt, player.toInt)

      case DEFINE_PLAYERS(friendlyPlayerID) =>
        if (friendlyPlayerID.toInt == 1) {
          logger.info("Friendly Player: 1, Enemy Player: 2")
        }
        if (friendlyPlayerID.toInt == 2) {
          logger.info("Friendly Player: 2, Enemy Player: 1")
        }
        gameStatus.DefinePlayers(friendlyPlayerID.toInt)

      case CARD_PLAYED(name, id, dstPos, player) =>
        logger.info("Card Played: " + line)
        gameStatus.CardPlayed(name, id.toInt, dstPos.toInt, player.toInt)


      case HAND_POSITION_CHANGE(id, pos, player, dstPos) =>
        logger.info("Hand_Position_Change: " + line)
        gameStatus.HandPositionChange(id.toInt, pos.toInt, player.toInt, dstPos.toInt)

      case BOARD_POSITION_CHANGE(id, player, dstPos) =>
        logger.info("Board Position Change: " + line)
        gameStatus.BoardPositionChange(id.toInt, player.toInt, dstPos.toInt)

      case CARD_DEATH(name, id, player) =>
        logger.info("Card Death: " + line)
        gameStatus.CardDeath(name, id.toInt, player.toInt)

      case MINION_SUMMONED(name, id, zonePos, player) =>
        logger.info("Minion Summoned: " + line)
        gameStatus.MinionSummoned(name, id.toInt, zonePos.toInt, player.toInt)

      case TRANSFORM(oldId, newId) if newId != 0 =>
        logger.info("Transform: " + line)
        gameStatus.Transform(oldId.toInt, newId.toInt)

      case HEX(name, id, player, zonePos) =>
        logger.info("HEX: " + line)
        gameStatus.Hex(name, id.toInt, player.toInt, zonePos.toInt)


      case _ =>
        HSAction.HSActionError()

    }
  }

  reader.ready() match {

    case true =>
      val line = reader.readLine()
      ParseLine(line)

    case false =>
      HSAction.HSActionError()
  }

  def AddParsedLine(previousAH: List[HSAction], line: String): List[HSAction] = {

    previousAH ::: List(ParseLine(line))
  }


  def ParseLine(line: String): HSAction = {


    line match {


    }


  }


  def UpdateActionHistory(previousActionHistory: List[HSAction], newAction: HSAction): List[HSAction] = {
    CombineHSActionLists(previousActionHistory, List(newAction))
  }

  def CombineHSActionLists(list1: List[HSAction], list2: List[HSAction]): List[HSAction] = {
    list1 ::: list2
  }
}

}
