package tph

import java.io._

import akka.actor.{Actor, ActorRef, ActorSystem}
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import tph.IrcMessages.ChangeReaderFile

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

  // messages to listener


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
  val CLEAR_STATUS = "CLEAR_STATUS"
}


class LogFileReader(system: ActorSystem, file: File, listener: ActorRef, controller: ActorRef) extends Actor with akka.actor.ActorLogging {

  import LogFileEvents._
  import LogFileReader._

  var complete = false
  var readerIdle = true
  var reader = new BufferedReader(new FileReader(file))
  var readerFileName = file.getName
  val writer = new PrintWriter(new FileWriter("debug.log"))
  val config = ConfigFactory.load()

  def receive = {
    case START => poll("")
    case POLL => poll("")
    case ChangeReaderFile(fileName) =>
      val regex = """testsituations/(.+)""".r
      fileName match {
        case regex(extractedFileName) =>
          val trueFileName = extractedFileName.toString()
          if(readerFileName != trueFileName) {
            val newReader = new BufferedReader(new FileReader(new File(fileName)))
            reader = newReader
            readerFileName = trueFileName
            log.debug("Reader file changed to "+fileName)
          }
        case _ =>

      }





    case CLEAR_STATUS =>
      listener ! GameOver()
      log.debug("Game Status Cleared")

    case "IsComplete" => sender ! complete



    case "GetGameStatus" =>

      if (!reader.ready()) {
        if (readerIdle) {
          implicit val timeout = Timeout(5 seconds)
          val future = listener ? "GetGameStatus"
          val result = Await.result(future, timeout.duration)
          sender ! result
          readerIdle = false
        }
        if (!readerIdle) {
          readerIdle = true
          sender ! None
        }
      }
      if (reader.ready())
        sender ! None
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
          log.info("Minion Controlled: " + line)
          listener ! FriendlyMinionControlled(name, id.toInt)

        //Enemy Events
        case ENEMY_CARD_DRAWN(id, position, player) =>
          log.info("Enemy Card Drawn: " + line)
          listener ! EnemyCardDrawnEvent(id.toInt, position.toInt, player.toInt)


        //Neutral Events

        case DISCOVER_OPTION(option) =>
          log.info("Discover Option: " + option)
          controller ! DiscoverOption(option.toInt)

        case FACE_ATTACK_VALUE(player, value) =>
          log.info("Face Value Changed: " + line)
          listener ! FaceAttackValueEvent(player.toInt, value.toInt)

        case WEAPON(id, player) =>
          log.info("Weapon Played: " + line)
          listener ! WeaponPlayedEvent(id.toInt, player.toInt)

        case SECRET_PLAYED(id, player) =>
          log.info("Secret Played: " + line)
          listener ! SecretPlayedEvent(id.toInt, player.toInt)

        case OLD_ZONE_CHANGE(id, zone, player, dstZone) if dstZone != "GRAVEYARD" =>
          log.info("Old Zone Change: " + line)
          listener ! OldZoneChangeEvent(id.toInt, zone, player.toInt, dstZone)

        case ZONE_CHANGE(id, player, zone, dstZone) =>
          log.info("Zone Change: " + line)
          listener ! ZoneChangeEvent(id.toInt, player.toInt, zone, dstZone)


        case KNOWN_CARD_DRAWN(name, id, position, player) =>
          log.info("Known Card Drawn: " + line)
          listener ! KnownCardDrawn(name, id.toInt, position.toInt, player.toInt)

        case SAP(name, id, player) =>
          log.info("Sap: " + line)
          listener ! Sap(name, id.toInt, player.toInt)

        case DEFINE_PLAYERS(friendlyPlayerID) =>
          if (friendlyPlayerID.toInt == 1) {
            log.info("Friendly Player: 1, Enemy Player: 2")
          }
          if (friendlyPlayerID.toInt == 2) {
            log.info("Friendly Player: 2, Enemy Player: 1")
          }
          listener ! DefinePlayers(friendlyPlayerID.toInt)

        case CARD_PLAYED(name, id, dstPos, player) =>
          log.info("Card Played: " + line)
          listener ! CardPlayed(name, id.toInt, dstPos.toInt, player.toInt)


        case HAND_POSITION_CHANGE(id, pos, player, dstPos) =>
          log.info("Hand_Position_Change: " + line)
          listener ! HandPositionChange(id.toInt, pos.toInt, player.toInt, dstPos.toInt)

        case BOARD_POSITION_CHANGE(id, player, dstPos) =>
          log.info("Board Position Change: " + line)
          listener ! BoardPositionChange(id.toInt, player.toInt, dstPos.toInt)

        case CARD_DEATH(name, id, zone, zonePos, player) =>
          log.info("Card Death: " + line)
          listener ! CardDeath(name, id.toInt, zonePos.toInt, player.toInt)

        case MINION_SUMMONED(name, id, zonePos, player) =>
          log.info("Minion Summoned: " + line)
          listener ! MinionSummoned(name, id.toInt, zonePos.toInt, player.toInt)

        case TRANSFORM(oldId, newId) if newId != 0 =>
          log.info("Transform: " + line)
          listener ! Transform(oldId.toInt, newId.toInt)

        case HEX(name, id, player, zonePos) =>
          log.info("HEX: " + line)
          listener ! Hex(name, id.toInt, player.toInt, zonePos.toInt)



        //Irc Logic Events
        case MULLIGAN_START =>
          controller ! "Mulligan"

        case MULLIGAN_OPTION() =>
          controller ! "NewMulliganOption"

        case TURN_START(entity) if entity == config.getString("tph.hearthstone.player") =>
          controller ! "Turn Start"

        case TURN_END(entity) if entity == config.getString("tph.hearthstone.player") =>
          controller ! "Turn End"


        //Debug Events
        case "DEBUGPOINT" =>
          None

        case DEBUG_PRINT_POWER(source, pad, text) =>
          text match {

            case TAG_CHANGE(entity, tag, value) if source == "GameState" && entity == "GameEntity" && tag == "TURN" =>
              listener ! TurnStartEvent(value.toInt)

            case TAG_CHANGE(entity, tag, value) if entity == "GameEntity" && value == "FINAL_GAMEOVER" && tag == "STEP" =>
              listener ! GameOver()
              controller ! "Game Over"

            case TAG_CHANGE(entity, tag, value) if tag == "NUM_OPTIONS" =>
              log.debug(NumOptions(source, entity, value).toString())

            case TAG_CHANGE(entity, tag, value) =>
              log.debug(TagChange(entity, tag, value).toString())

            case _ => log.debug(DebugPrintPower(source, pad, text).toString())
          }


        case PRINT_STATE =>
          listener ! PrintState(file.getName())


        case "ENDOFDOCUMENT" =>
          complete = true


        case FILE_NAME() => // ignore
        case EMPTY_LINE() => // ignore
        case x =>
      }
    }
  system.scheduler.scheduleOnce(100.millis, this.self, POLL)}

}
