package tph

import java.io.{FileReader, BufferedReader, File}

import scala.concurrent.duration._
import akka.actor.{ActorRef, Props, ActorSystem, Actor}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.matching.Regex


object LogFileReader {


  // messages in
  val START = "LogFileReader.start"
  val POLL = "LogFileReader.poll"

  // messages to listener
  case class LogLine(line: String)

  val DEBUG_PRINT_POWER = """^\[Power\] (\S+).DebugPrintPower\(\) - (\s*)(.*)$""".r

  case class DebugPrintPower(source: String, pad: String, text: String)

  val FRIENDLY_CARD_DRAWN = """^.+id=\d+ local=False \[name=(.+) id=(\d+) zone=HAND zonePos=(\d+) cardId=\S+ player=1\] pos from 0 -> \d+""".r

  case class FriendlyCardDrawn(name: String, id: Int, position: Int)

  val ENEMY_CARD_DRAWN = """^.+id=\d+ local=.+ \[id=(\d+) cardId=.+type=.+zone=HAND zonePos=(\d+) player=2\] pos from \d+ -> \d+""".r

  case class EnemyCardDrawn(id: Int, position: Int)

  val BOARD_CHANGE = """^\[Power\] PowerTaskList.+TAG_CHANGE Entity=\[name=(.+) id=(\d+) zone=PLAY zonePos=(\d+) cardId=.+ player=(\d)\] tag=ZONE_POSITION value=(\d+)""".r

  case class BoardChange(name: String, id: Int, zonePos: Int, player: Int, position: Int)


  val FRIENDLY_HAND_CHANGE = """^\[Power\] PowerTaskList.+TAG_CHANGE Entity=\[name=(.+) id=(\d+) zone=HAND zonePos=(\d+) cardId=.+ player=(\d+)\] tag=ZONE_POSITION value=(\d+)$""".r

  case class FriendlyHandChange(name: String, id: Int, zonePos: Int, player: Int, dstPos: Int)

  val ENEMY_HAND_CHANGE = """^\[Power\] PowerTaskList.+TAG_CHANGE Entity=\[id=(\d+) cardID=.+type=INVALID zone=HAND zonePos=(\d+) player=(\d+)\] tag=ZONE_POSITION value=(\d+)""".r

  case class EnemyHandChange(id: Int, zonePos: Int, player: Int, dstPos: Int)


  //Zone Changes
  val FRIENDLY_ZONE_CHANGE = """^\[Power\] PowerTaskList.+TAG_CHANGE Entity=\[name=(.+) id=(\d+) zone=(.+) zonePos=(\d+) cardId=.+ player=1] tag=ZONE value=(.+)""".r

  case class FriendlyZoneChange(name: String, id: Int, zone: String, zonePos: Int, dstZone: String)

  val ENEMY_PLAYS_CARD = """^\[Power\] PowerTaskList.+ACTION_START Entity=\[id=(\d+).+zone=HAND zonePos=(\d+) player=(\d+)\] BlockType=PLAY.+""".r

  case class EnemyPlaysCard(id: Int, zonePos: Int, player: Int)

  val ENEMY_CARD_RETURN = """^\[Power\] PowerTaskList.+TAG_CHANGE Entity=\[name=.+ id=(\d+) zone=(.+) zonePos=(\d+).+ player=2\] tag=ZONE value=(.+)""".r

  case class EnemyCardReturn(id: Int, zone: String, zonePos: Int, dstZone: Int)


  //End Zone Changes

  val TAG_CHANGE = """^\s*TAG_CHANGE Entity=(.+) tag=(.+) value=(.+)$""".r

  case class TagChange(entity: String, tag: String, value: String)


  case class NumOptions(source: String, entity: String, value: String)


  case class ConfirmOutput(message: String)



  val EMPTY_LINE = """^\s*$""".r
  val FILE_NAME = """\(Filename: .*""".r
}


class LogFileReader(system: ActorSystem, file: File, listener: ActorRef) extends Actor with akka.actor.ActorLogging {

  import LogFileReader._
  import LogFileEvents._

  val reader = new BufferedReader(new FileReader(file))

  def receive = {
    case START => poll()
    case POLL => poll()
  }

  def poll(): Unit = {
    while (reader.ready()) {
      val line = reader.readLine()
      line match {


        case FRIENDLY_CARD_DRAWN(name, id, position) =>
          log.info("Friendly Card Drawn: " + line)
          listener ! FriendlyCardDrawnEvent(name, id.toInt, position.toInt)

        case ENEMY_CARD_DRAWN(id, position) =>
          log.info("Enemy Card Drawn: " + line)
          listener ! EnemyCardDrawnEvent(id.toInt, position.toInt)

        case BOARD_CHANGE(name, id, zonePos, player, dstPos) =>
          log.info("Board Change: " + line)
          listener ! BoardChangeEvent(name, id.toInt, zonePos.toInt, player.toInt, dstPos.toInt)

        case FRIENDLY_HAND_CHANGE(name, id, zonePos, player, dstPos) =>
          log.info("Friendly Hand Change: " + line)
          listener ! FriendlyHandChangeEvent(name, id.toInt, zonePos.toInt, player.toInt, dstPos.toInt)

        case ENEMY_HAND_CHANGE(id, zonePos, player, dstPos) =>
          log.info("Enemy Hand Change: " + line)
          listener ! EnemyHandChangeEvent(id.toInt, zonePos.toInt, player.toInt, dstPos.toInt)

        case ENEMY_PLAYS_CARD(id, zonePos, player) =>
          log.info("Enemy Plays Card: " + line)
          listener ! EnemyPlaysCardEvent(id.toInt, zonePos.toInt, player.toInt)

        case FRIENDLY_ZONE_CHANGE(name, id, zone, zonePos, dstZone) =>
          log.info("Friendly Zone Change: " + line)
          listener ! FriendlyZoneChangeEvent(name, id.toInt, zone, zonePos.toInt, dstZone)

        case ENEMY_CARD_RETURN(id, zone, zonePos, dstZone) =>
          log.info("Enemy Card Return: " + line)
          listener ! EnemyCardReturnEvent(id.toInt, zone, zonePos.toInt, dstZone)

        case DEBUG_PRINT_POWER(source, pad, text) =>
          text match {

            // Changing turn status
            case TAG_CHANGE(entity, tag, value) if source == "GameState" && entity == "GameEntity" && tag == "TURN" =>

              listener ! TurnStartEvent(value.toInt)


            case TAG_CHANGE(entity, tag, value) if tag == "PLAYER_ID" && value == "1" =>

              listener ! FriendlyDefinedEvent(entity)

            case TAG_CHANGE(entity, tag, value) if tag == "PLAYER_ID" && value == "2" =>

              listener ! EnemyDefinedEvent(entity)

            case TAG_CHANGE(entity, tag, value) if tag == "FIRST_PLAYER" && value == "1" =>

              listener ! PlaysFirstEvent(entity)


            case TAG_CHANGE(entity, tag, value) if entity == "GameEntity" && value == "FINAL_GAMEOVER" =>
              listener ! GameOver()

            case TAG_CHANGE(entity, tag, value) if tag == "NUM_OPTIONS" =>
              log.debug(NumOptions(source, entity, value).toString())

            case TAG_CHANGE(entity, tag, value) =>
              log.debug(TagChange(entity, tag, value).toString())

            case _ => log.debug(DebugPrintPower(source, pad, text).toString())
          }


        case FILE_NAME() => // ignore
        case EMPTY_LINE() => // ignore
        case x => listener ! ("??? " + x)
      }
    }

    system.scheduler.scheduleOnce(100.millis, this.self, POLL)
  }


}
