package tph

import java.io._
import java.util.concurrent.{TimeUnit, ScheduledThreadPoolExecutor}

import akka.actor.{Actor, ActorRef, ActorSystem}
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._


object LogFileReader {
//BUGS:
  // Shadowcaster needs tested (Add a copy to your hand)
  // Renounce Darkness needs tested (Replaces entire deck with new cards)
  // Lord Jaraxxus needs tested (Is played, but replaces your hero instead of being played on the board)
  //Shifter Zerus needs tested (Shifts into a new card every turn)
  //Golden Monkey needs tested (Replaces hand and deck)


  // messages in
  val START = "LogFileReader.start"
  val POLL = "LogFileReader.poll"
  val scheduler = new ScheduledThreadPoolExecutor(1)

  // messages to gameStatus


  //Friendly Strings
  //val FRIENDLY_CARD_RETURN = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - id=.+ local=.+ \[name=(.+) id=(\d+) zone=HAND zonePos=.+ cardId=.+ player=(\d+)\] zone from FRIENDLY PLAY -> FRIENDLY HAND""".r
  val FRIENDLY_MINION_CONTROLLED = """^.+zone from OPPOSING PLAY -> FRIENDLY PLAY""".r


  //Enemy Strings
  val ENEMY_CARD_DRAWN = """^.+id=\d+ local=.+ \[id=(\d+) cardId=.+type=.+zone=HAND zonePos=(\d+) player=(\d+)\] pos from \d+ -> \d+""".r
  //val ENEMY_CARD_RETURN = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - id=.+ local=.+ \[name=(.+) id=(\d+) zone=HAND zonePos=.+ cardId=.+ player=(\d+)\] zone from OPPOSING PLAY -> OPPOSING HAND""".r

  //Neutral Strings


  val FACE_ATTACK_VALUE = """\[Power\] PowerTaskList.DebugPrintPower\(\) -     TAG_CHANGE Entity=\[name=.+ id=\d+ zone=PLAY zonePos=0 cardId=HERO.+ player=(\d+)] tag=ATK value=(\d+)""".r
  val SECRET_PLAYED = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - TRANSITIONING card .+id=(\d+).+zone=SECRET zonePos=\d+.+player=(\d+)\] to .+ SECRET""".r
  val OLD_ZONE_CHANGE = """^\[Power\] PowerTaskList.+TAG_CHANGE Entity=.+id=(\d+).+zone=(.+) zonePos=.+ player=(\d+)\] tag=ZONE value=(.+)""".r
  val ZONE_CHANGE = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - id=\d+ local=False .+id=(\d+) zone=.+ zonePos=\d+ cardId=.+ player=(\d+)\] zone from (.+) -> (.+)$""".r
  val KNOWN_CARD_DRAWN = """^.+id=\d+ local=False \[name=(.+) id=(\d+) zone=HAND zonePos=(\d+) cardId=\S+ player=(\d+)\] pos from \d+ -> \d+""".r
  val CARD_PLAYED = """\[Power\] PowerProcessor.DoTaskListForCard\(\) - unhandled BlockType PLAY for sourceEntity \[name=(.+) id=(\d+) zone=PLAY zonePos=(\d+) cardId=.+ player=(\d+)\]""".r
  val BOARD_POSITION_CHANGE = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - processing index=\d+ change=powerTask=\[power=\[type=TAG_CHANGE entity=\[id=\d+ cardId=.+ name=.+\] tag=ZONE_POSITION value=\d+\] complete=False\] entity=\[name=.+ id=(\d+) zone=PLAY zonePos=\d+ cardId=.+ player=(\d+)] srcZoneTag=INVALID srcPos= dstZoneTag=INVALID dstPos=(\d+)""".r
  val HAND_POSITION_CHANGE = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - processing index=\d+ change=powerTask=\[power=\[type=TAG_CHANGE entity=\[id=\d+ cardId=.+name=.+tag=ZONE_POSITION value=\d+\] complete=False\] entity=.+id=(\d+).+zone=HAND zonePos=(\d+).+player=(\d+).+dstPos=(\d+)""".r
  val CARD_DEATH = """^\[Power\] PowerTaskList.+TAG_CHANGE Entity=\[name=(.+) id=(\d+) zone=(.+) zonePos=(\d+).+ player=(\d+).+ tag=ZONE value=GRAVEYARD""".r
  val MINION_SUMMONED = """^.+FULL_ENTITY - Updating \[name=(.+) id=(\d+) zone=PLAY zonePos=(\d+).+player=(\d+).+""".r
  val TRANSFORM ="""\[Power\] .+.DebugPrintPower\(\) -     TAG_CHANGE Entity=.+id=(\d+) zone=PLAY.+tag=LINKED_ENTITY value=(\d+)""".r
  val HEX = """\[Zone\] ZoneChangeList.+ \[name=(.+) id=(\d+) zone=PLAY zonePos=.+ cardId=hexfrog player=(\d+)\] pos from 0 -> (\d)""".r
  val DEFINE_PLAYERS = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - TRANSITIONING card \[name=.+ id=.+ zone=PLAY zonePos=0 cardId=.+ player=(\d+)\] to FRIENDLY PLAY \(Hero\)""".r
  val SAP = """\[Power\] PowerTaskList.DebugPrintPower\(\) - BLOCK_START BlockType=POWER Entity=\[name=Sap id=.+ zone=PLAY zonePos=.+ cardId=.+ player=.+\] EffectCardId= EffectIndex=.+ Target=\[name=(.+) id=(\d+) zone=PLAY zonePos=.+ cardId=.+ player=(\d+)\]""".r
  val WEAPON = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - TRANSITIONING card \[name=.+ id=(\d+) zone=PLAY zonePos=0 cardId=.+ player=(\d+)] to .+ PLAY (Weapon)""".r


  //IrcLogic Strings
  val MULLIGAN_OPTION =
    """\[Zone\] ZoneChangeList.ProcessChanges\(\) - processing index=\d+ change=powerTask=\[power=\[type=SHOW_ENTITY entity=\[id=\d+ cardId=.+ name=\[id=\d+ cardId= type=INVALID zone=DECK zonePos=0 player=\d+\]\] tags=System.Collections.Generic.List`1\[Network\+Entity\+Tag\]\] complete=False\] entity=\[id=\d+ cardId= type=INVALID zone=DECK zonePos=0 player=\d+\] srcZoneTag=INVALID srcPos= dstZoneTag=HAND dstPos=""".r
  val MULLIGAN_START = "[Power] PowerTaskList.DebugPrintPower() -     TAG_CHANGE Entity=GameEntity tag=STEP value=BEGIN_MULLIGAN"
  val DISCOVER_OPTION = """\[Power\] GameState.DebugPrintEntityChoices\(\) -   Entities\[(\d+)\]=\[name=.+ id=\d+ zone=SETASIDE zonePos=0 cardId=.+ player=\d+\]""".r
  val TURN_START = """\[Power\] PowerTaskList.DebugPrintPower\(\) -     TAG_CHANGE Entity=(.+) tag=CURRENT_PLAYER value=1""".r
  val TURN_END = """\[Power\] PowerTaskList.DebugPrintPower\(\) -     TAG_CHANGE Entity=(.+) tag=CURRENT_PLAYER value=0""".r


  //Controller Strings

  val PLAY_MENU = "FSM not Preprocessed: Hero_Armor(Clone) : FSM"
  val COLLECTION_MENU = "FSM not Preprocessed: DeckGlowAll : FSM"
  val START_UP = "FSM not Preprocessed: Startup_Hub : FSM"





  //Debug Strings
  val DEBUG_PRINT_POWER = """^\[Power\] (\S+).DebugPrintPower\(\) - (\s*)(.*)$""".r
  val TAG_CHANGE = """^\s*TAG_CHANGE Entity=(.+) tag=(.+) value=(.+)$""".r
  val PRINT_STATE = "PRINTTODEBUG"
  val END_OF_DOCUMENT = "ENDOFDOCUMENT"
  val EMPTY_LINE = """^\s*$""".r
  val FILE_NAME = """\(Filename: .*""".r
}


class LogFileReader(file: File, gameStatus: GameStatus, theBrain: TheBrain) extends LazyLogging {

  import LogFileEvents._
  import LogFileReader._

  var complete = false
  var readerIdle = true
  var reader = new BufferedReader(new FileReader(file))
  var readerFileName = file.getName
  val writer = new PrintWriter(new FileWriter("debug.log"))
  val config = ConfigFactory.load()

  def Init(): Unit = {
    poll("")
  }

  def ChangeReaderFile(fileName: String): Unit = {
      val regex = """testsituations/(.+)""".r
      fileName match {
        case regex(extractedFileName) =>
          val trueFileName = extractedFileName.toString()
          if(readerFileName != trueFileName) {
            val newReader = new BufferedReader(new FileReader(new File(fileName)))
            reader = newReader
            readerFileName = trueFileName
            logger.debug("Reader file changed to " + fileName)
          }
        case _ =>

      }
  }


  def IsComplete(): Boolean = {
    complete
  }


  def poll(testMessage:String): Unit = {
      while (reader.ready()) {
        readerIdle = false
        val line = reader.readLine()
        writer.println(line)
        writer.flush()



      line match {


        // Friendly Events

        case FRIENDLY_MINION_CONTROLLED(name, id) =>
          logger.info("Minion Controlled: " + line)
          gameStatus.FriendlyMinionControlled(name, id.toInt)

        //Enemy Events
        case ENEMY_CARD_DRAWN(id, position, player) =>
          logger.info("Enemy Card Drawn: " + line)
          gameStatus.EnemyCardDrawnEvent(id.toInt, position.toInt, player.toInt)


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

        case CARD_DEATH(name, id, zone, zonePos, player) =>
          logger.info("Card Death: " + line)
          gameStatus.CardDeath(name, id.toInt, zonePos.toInt, player.toInt)

        case MINION_SUMMONED(name, id, zonePos, player) =>
          logger.info("Minion Summoned: " + line)
          gameStatus.MinionSummoned(name, id.toInt, zonePos.toInt, player.toInt)

        case TRANSFORM(oldId, newId) if newId != 0 =>
          logger.info("Transform: " + line)
          gameStatus.Transform(oldId.toInt, newId.toInt)

        case HEX(name, id, player, zonePos) =>
          logger.info("HEX: " + line)
          gameStatus.Hex(name, id.toInt, player.toInt, zonePos.toInt)



        //Irc Logic Events
        case MULLIGAN_START =>
          theBrain.StartMulligan()

        case MULLIGAN_OPTION() =>
          theBrain.AddMulliganOption()

        case TURN_START(entity) if entity == config.getString("tph.hearthstone.player") =>
          theBrain.StartTurn()

        case TURN_END(entity) if entity == config.getString("tph.hearthstone.player") =>
          theBrain.EndTurn()


        //Debug Events
        case "DEBUGPOINT" =>
          None

        case DEBUG_PRINT_POWER(source, pad, text) =>
          text match {

            case TAG_CHANGE(entity, tag, value) if source == "GameState" && entity == "GameEntity" && tag == "TURN" =>
              gameStatus.TurnStartEvent(value.toInt)

            case TAG_CHANGE(entity, tag, value) if entity == "GameEntity" && value == "FINAL_GAMEOVER" && tag == "STEP" =>
              theBrain.GameOver()

            case TAG_CHANGE(entity, tag, value) if tag == "NUM_OPTIONS" =>
              logger.debug(NumOptions(source, entity, value).toString())

            case TAG_CHANGE(entity, tag, value) =>
              logger.debug(TagChange(entity, tag, value).toString())

            case _ => logger.debug(DebugPrintPower(source, pad, text).toString())
          }


        case PRINT_STATE =>
          gameStatus.PrintState(file.getName())


        case "ENDOFDOCUMENT" =>
          complete = true


        case FILE_NAME() => // ignore
        case EMPTY_LINE() => // ignore
        case x =>
      }
    }
    val decide = new Runnable {
      def run() = poll("")
    }
    scheduler.schedule(decide, 100, TimeUnit.MILLISECONDS)

  }

}
