package tph

import java.io._

import akka.util.Timeout
import akka.pattern.ask

import scala.concurrent.Await
import scala.concurrent.duration._
import akka.actor.{ActorRef, Props, ActorSystem, Actor}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.matching.Regex


object LogFileReader {
//BUGS:
  // # of cards in his hand messes up sometimes. (Sometimes it fixes itself; maybe after the bugged card is played)
  // Need a weapon system
  //Hex doesn't work
  //Hand/Board indexing gets confused when many actions happen quickly. If program is re-ran, issue is fixed. (Board clear/vanish)

  // messages in
  val START = "LogFileReader.start"
  val POLL = "LogFileReader.poll"

  // messages to listener
  case class LogLine(line: String)


  //Friendly Strings
  //val FRIENDLY_CARD_RETURN = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - id=.+ local=.+ \[name=(.+) id=(\d+) zone=HAND zonePos=.+ cardId=.+ player=(\d+)\] zone from FRIENDLY PLAY -> FRIENDLY HAND""".r
  val FRIENDLY_MINION_CONTROLLED = """^.+zone from OPPOSING PLAY -> FRIENDLY PLAY""".r


  //Enemy Strings
  val ENEMY_CARD_DRAWN = """^.+id=\d+ local=.+ \[id=(\d+) cardId=.+type=.+zone=HAND zonePos=(\d+) player=(\d+)\] pos from \d+ -> \d+""".r
  //val ENEMY_CARD_RETURN = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - id=.+ local=.+ \[name=(.+) id=(\d+) zone=HAND zonePos=.+ cardId=.+ player=(\d+)\] zone from OPPOSING PLAY -> OPPOSING HAND""".r

  //Neutral Strings
  val SECRET_PLAYED = """\[Power\] PowerTaskList.DebugPrintPower\(\) -     TAG_CHANGE Entity=.+id=(\d+).+zone=HAND zonePos=\d+ player=(\d+)\] tag=SECRET value=1""".r
  val OLD_ZONE_CHANGE = """^\[Power\] PowerTaskList.+TAG_CHANGE Entity=.+id=(\d+).+zone=(.+) zonePos=.+ player=(\d+)\] tag=ZONE value=(.+)""".r
  val ZONE_CHANGE = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - id=\d+ local=False .+id=(\d+) zone=.+ zonePos=\d+ cardId=.+ player=(\d+)\] zone from (.+) -> (.+)$""".r
  val KNOWN_CARD_DRAWN = """^.+id=\d+ local=False \[name=(.+) id=(\d+) zone=HAND zonePos=(\d+) cardId=\S+ player=(\d+)\] pos from \d+ -> \d+""".r
  val CARD_PLAYED = """\[Power\] PowerProcessor.DoTaskListForCard\(\) - unhandled BlockType PLAY for sourceEntity \[name=(.+) id=(\d+) zone=PLAY zonePos=(\d+) cardId=.+ player=(\d+)\]""".r
  val BOARD_POSITION_CHANGE = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - processing index=\d+ change=powerTask=\[power=\[type=TAG_CHANGE entity=\[id=\d+ cardId=.+ name=.+\] tag=ZONE_POSITION value=\d+\] complete=False\] entity=\[name=.+ id=(\d+) zone=PLAY zonePos=\d+ cardId=.+ player=(\d+)] srcZoneTag=INVALID srcPos= dstZoneTag=INVALID dstPos=(\d+)""".r
  val HAND_POSITION_CHANGE = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - processing index=\d+ change=powerTask=\[power=\[type=TAG_CHANGE entity=\[id=\d+ cardId=.+name=.+tag=ZONE_POSITION value=\d+\] complete=False\] entity=.+id=(\d+).+zone=HAND zonePos=(\d+).+player=(\d+).+dstPos=(\d+)""".r
  val CARD_DEATH = """^\[Power\] PowerTaskList.+TAG_CHANGE Entity=\[name=(.+) id=(\d+) zone=(.+) zonePos=(\d+).+ player=(\d+).+ tag=ZONE value=GRAVEYARD""".r
  val MINION_SUMMONED = """^.+FULL_ENTITY - Updating \[name=(.+) id=(\d+) zone=PLAY zonePos=(\d+).+player=(\d+).+""".r
  val POLYMORPH = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - processing index=\d+ change=powerTask=\[power=\[type=TAG_CHANGE entity=\[id=\d+ cardId=.+ name=.+\] tag=LINKED_ENTITY value=(\d+)\] complete=False\] entity=\[name=.+ id=(\d+) zone=PLAY zonePos=\d+ cardId=.+ player=(\d+)\] srcZoneTag=INVALID srcPos= dstZoneTag=INVALID dstPos=""".r
  val HEX = """\[Zone\] ZoneChangeList.+ \[name=(.+) id=(\d+) zone=PLAY zonePos=.+ cardId=hexfrog player=(\d+)\] pos from 0 -> (\d)""".r
  val DEFINE_PLAYERS = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - TRANSITIONING card \[name=.+ id=.+ zone=PLAY zonePos=0 cardId=.+ player=(\d+)\] to FRIENDLY PLAY \(Hero\)""".r
  val SAP = """\[Power\] PowerTaskList.DebugPrintPower\(\) - BLOCK_START BlockType=POWER Entity=\[name=Sap id=.+ zone=PLAY zonePos=.+ cardId=.+ player=.+\] EffectCardId= EffectIndex=.+ Target=\[name=(.+) id=(\d+) zone=PLAY zonePos=.+ cardId=.+ player=(\d+)\]""".r


  //Debug Strings
  val DEBUG_PRINT_POWER = """^\[Power\] (\S+).DebugPrintPower\(\) - (\s*)(.*)$""".r
  val TAG_CHANGE = """^\s*TAG_CHANGE Entity=(.+) tag=(.+) value=(.+)$""".r
  val PRINT_STATE = "PRINTTODEBUG"
  val END_OF_DOCUMENT = "ENDOFDOCUMENT"
  val EMPTY_LINE = """^\s*$""".r
  val FILE_NAME = """\(Filename: .*""".r
  val CLEAR_STATUS = "CLEAR_STATUS"
}


class LogFileReader(system: ActorSystem, file: File, listener: ActorRef) extends Actor with akka.actor.ActorLogging {

  import LogFileReader._
  import LogFileEvents._

  var complete = false
  val reader = new BufferedReader(new FileReader(file))
  val writer = new PrintWriter(new FileWriter("debug.log"))

  def receive = {
    case START => poll()
    case POLL => poll()
    case CLEAR_STATUS => listener ! GameOver()
    case "IsComplete" => sender ! complete
  }


  def poll(): Unit = {
      while (reader.ready()) {
        val line = reader.readLine()
        writer.println(line)
        writer.flush()
        line match {


          // Friendly Events
          //        case FRIENDLY_CARD_DRAWN(name, id, position,player) =>
          //          log.info("Friendly Card Drawn: " + line)
          //          listener ! FriendlyCardDrawnEvent(name, id.toInt, position.toInt,player.toInt)

//          case FRIENDLY_CARD_RETURN(name, id, player) =>
//            log.info("Friendly Card Return: " + line)
//            listener ! FriendlyCardReturnEvent(name, id.toInt, player.toInt)


          case FRIENDLY_MINION_CONTROLLED(name, id) =>
            log.info("Minion Controlled: " + line)
            listener ! FriendlyMinionControlled(name, id.toInt)




          //Enemy Events
          case ENEMY_CARD_DRAWN(id, position, player) =>
            log.info("Enemy Card Drawn: " + line)
            listener ! EnemyCardDrawnEvent(id.toInt, position.toInt, player.toInt)

//          case ENEMY_CARD_RETURN(name, id, player) =>
//            log.info("Enemy Card Return: " + line)
//            listener ! EnemyCardReturnEvent(name, id.toInt, player.toInt)



          //Neutral Events
          case SECRET_PLAYED(id, player) =>
            log.info("Secret Played: " +line)
            listener ! SecretPlayedEvent(id.toInt, player.toInt)

          case OLD_ZONE_CHANGE(id, zone, player, dstZone) if dstZone != "GRAVEYARD" =>
            log.info("Zone Change: " + line)
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
            if (friendlyPlayerID == 1) {
              log.info("Friendly Player: 1, Enemy Player: 2")
            }
            if (friendlyPlayerID == 2) {
              log.info("Friendly Player: 2, Enemy Player: 1")
            }
            listener ! DefinePlayers(friendlyPlayerID.toInt)

          case CARD_PLAYED(name, id, dstPos, player) =>
            log.info("Card Played: " + line)
            listener ! CardPlayed(name, id.toInt, dstPos.toInt, player.toInt)


          case HAND_POSITION_CHANGE(id, pos, player, dstPos)=>
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

          case POLYMORPH(newId, oldId, player) =>
            log.info("Polymorph: " + line)
            listener ! Polymorph(newId.toInt, oldId.toInt, player.toInt)

          case HEX(name, id, player, zonePos) =>
            log.info("HEX: " + line)
            listener ! Hex(name, id.toInt, player.toInt, zonePos.toInt)




          //Debug Events

          case DEBUG_PRINT_POWER(source, pad, text) =>
            text match {

              case TAG_CHANGE(entity, tag, value) if source == "GameState" && entity == "GameEntity" && tag == "TURN" =>

                listener ! TurnStartEvent(value.toInt)


              case TAG_CHANGE(entity, tag, value) if entity == "GameEntity" && value == "FINAL_GAMEOVER" =>
                //listener ! GameOver()

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
          case x => listener ! ("??? " + x)
        }
      }
  system.scheduler.scheduleOnce(100.millis, this.self, POLL)}

}
