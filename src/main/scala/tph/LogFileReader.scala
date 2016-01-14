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


  //Friendly Strings
  val FRIENDLY_CARD_DRAWN = """^.+id=\d+ local=False \[name=(.+) id=(\d+) zone=HAND zonePos=(\d+) cardId=\S+ player=1\] pos from 0 -> \d+""".r
  val FRIENDLY_HAND_CHANGE = """^\[Power\] PowerTaskList.+TAG_CHANGE Entity=\[name=(.+) id=(\d+) zone=HAND zonePos=(\d+) cardId=.+ player=(\d+)\] tag=ZONE_POSITION value=(\d+)$""".r
  val FRIENDLY_PLAYS_CARD = """^.+triggerEntity=\[name=(.+) id=(\d+) zone=.+ srcZone=(.+) srcPos=(\d+) dstZone=(.+) dstPos=(\d+)""".r
  val FRIENDLY_CARD_RETURN = """processing index=\d+ change=powerTask=\[power=\[type=TAG_CHANGE entity=\[id=\d+.+ entity=\[name=(.+) id=(\d+) zone=HAND zonePos=(\d+).+ player=1.+ dstPos=(\d+)""".r

  //Enemy Strings
  val ENEMY_CARD_DRAWN = """^.+id=\d+ local=.+ \[id=(\d+) cardId=.+type=.+zone=HAND zonePos=(\d+) player=2\] pos from 0 -> \d+""".r
  val ENEMY_PLAYS_CARD = """^.+processing index=\d+ change=powerTask=\[power=\[type=TAG_CHANGE entity=\[id=\d+ cardId=.+ entity=\[name=(.+) id=(\d+) zone=PLAY zonePos=(\d+) cardId=.+ player=2\] .+ dstPos=(\d+)""".r
  val ENEMY_HAND_CHANGE = """^\[Power\] PowerTaskList.+TAG_CHANGE Entity=\[id=(\d+).+ zone=HAND zonePos=(\d+) player=(\d+)\] tag=ZONE_POSITION value=(\d+)""".r
  val ENEMY_CARD_RETURN = """^\[Power\] PowerTaskList.+TAG_CHANGE Entity=\[name=.+ id=(\d+) zone=(.+) zonePos=(\d+).+ player=2\] tag=ZONE value=HAND""".r

  //Neutral Strings
  val BOARD_CHANGE = """^\[Power\] PowerTaskList.+TAG_CHANGE Entity=\[name=(.+) id=(\d+) zone=PLAY zonePos=(\d+) cardId=.+ player=(\d)\] tag=ZONE_POSITION value=(\d+)""".r
  val CARD_DEATH = """^\[Power\] PowerTaskList.+TAG_CHANGE Entity=\[name=(.+) id=(\d+) zone=(.+) zonePos=(\d+).+ player=(\d+).+ tag=ZONE value=GRAVEYARD""".r

  //Debug Strings
  val DEBUG_PRINT_POWER = """^\[Power\] (\S+).DebugPrintPower\(\) - (\s*)(.*)$""".r
  val TAG_CHANGE = """^\s*TAG_CHANGE Entity=(.+) tag=(.+) value=(.+)$""".r

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


          // Friendly Events
        case FRIENDLY_CARD_DRAWN(name, id, position) =>
          log.info("Friendly Card Drawn: " + line)
          listener ! FriendlyCardDrawnEvent(name, id.toInt, position.toInt)

        case FRIENDLY_PLAYS_CARD(name, id,srcZone,srcPos,dstZone,dstPos) =>
          log.info("Friendly Plays Card: " + line)
          listener ! FriendlyPlaysCardEvent(name, id.toInt, srcZone, srcPos.toInt,dstZone,dstPos.toInt)

        case FRIENDLY_HAND_CHANGE(name, id, zonePos, player, dstPos) =>
          log.info("Friendly Hand Change: " + line)
          listener ! FriendlyHandChangeEvent(name, id.toInt, zonePos.toInt, player.toInt, dstPos.toInt)

        case FRIENDLY_CARD_RETURN(name, id,zonePos, dstPos) =>
          log.info("Friendly Zone Change: " + line)
          listener ! FriendlyCardReturnEvent(name, id.toInt, zonePos.toInt, dstPos.toInt)




          //Enemy Events
        case ENEMY_CARD_DRAWN(id, position) =>
          log.info("Enemy Card Drawn: " + line)
          listener ! EnemyCardDrawnEvent(id.toInt, position.toInt)

        case ENEMY_PLAYS_CARD(name, id, zonePos,dstPos) =>
          log.info("Enemy Plays Card: " + line)
          listener ! EnemyPlaysCardEvent(name, id.toInt, zonePos.toInt, dstPos.toInt)

        case ENEMY_HAND_CHANGE(id, zonePos, player, dstPos) =>
          log.info("Enemy Hand Change: " + line)
          listener ! EnemyHandChangeEvent(id.toInt, zonePos.toInt, player.toInt, dstPos.toInt)

        case ENEMY_CARD_RETURN(name,id,zone,zonePos) =>
          log.info("Enemy Card Return: " + line)
          listener ! EnemyCardReturnEvent(name,id.toInt,zone,zonePos.toInt)



          //Neutral Events

        case BOARD_CHANGE(name, id, zonePos, player, dstPos) =>
          log.info("Board Change: " + line)
          listener ! BoardChangeEvent(name, id.toInt, zonePos.toInt, player.toInt, dstPos.toInt)

        case CARD_DEATH(name, id, zone, zonePos, player) =>
          log.info("Card Death: " + line)
          listener ! CardDeath(name, id.toInt, zone, zonePos.toInt, player.toInt)


          //Debug Events

        case DEBUG_PRINT_POWER(source, pad, text) =>
          text match {

            case TAG_CHANGE(entity, tag, value) if source == "GameState" && entity == "GameEntity" && tag == "TURN" =>

              listener ! TurnStartEvent(value.toInt)

            case TAG_CHANGE(entity, tag, value) if tag == "PLAYER_ID" && value == "1" =>

              listener ! FriendlyDefinedEvent(entity)

            case TAG_CHANGE(entity, tag, value) if tag == "PLAYER_ID" && value == "2" =>

              listener ! EnemyDefinedEvent(entity)


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
