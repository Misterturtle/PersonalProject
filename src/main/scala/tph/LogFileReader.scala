package tph

import java.io._

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
  val FRIENDLY_CARD_DRAWN = """^.+id=\d+ local=False \[name=(.+) id=(\d+) zone=HAND zonePos=(\d+) cardId=\S+ player=(\d+)\] pos from \d+ -> \d+""".r
  //Pos from \d+ -> \d+ due to mulligan (Instead of 0 -> \d+

  // Replaced with POSITION_CHANGE under Neutral
  //val FRIENDLY_HAND_CHANGE = """^\[Power\] PowerTaskList.+TAG_CHANGE Entity=\[name=(.+) id=(\d+) zone=HAND zonePos=(\d+) cardId=.+ player=(\d+)\] tag=ZONE_POSITION value=(\d+)$""".r
  val FRIENDLY_PLAYS_CARD = """^.+triggerEntity=\[name=(.+) id=(\d+) zone=.+ srcZone=(.+) srcPos=(\d+) dstZone=(.+) dstPos=(\d+)""".r
  val FRIENDLY_ZONE_CHANGE = """^\[Power\] PowerTaskList.+TAG_CHANGE Entity=\[name=(.+) id=(\d+) zone=(.+) zonePos=.+ player=(\d+)\] tag=ZONE value=(.+)""".r
  val FRIENDLY_CARD_RETURN = """^.+processing index=\d+ change=powerTask=\[power=\[type=TAG_CHANGE entity=\[id=\d+.+ entity=\[name=(.+) id=(\d+) zone=HAND zonePos=(\d+).+ player=(\d+).+ dstPos=(\d+)""".r
  val FRIENDLY_MINION_CONTROLLED = """^.+zone from OPPOSING PLAY -> FRIENDLY PLAY""".r

  //Enemy Strings
  val ENEMY_CARD_DRAWN = """^.+id=\d+ local=.+ \[id=(\d+) cardId=.+type=.+zone=HAND zonePos=(\d+) player=(\d+)\] pos from 0 -> \d+""".r
  val ENEMY_PLAYS_CARD = """^.+processing index=\d+ change=powerTask=\[power=\[type=TAG_CHANGE.+ tag=JUST_PLAYED.+ entity=\[name=(.+) id=(\d+) zone=PLAY zonePos=(\d+).+ player=(\d+).+""".r
  //Replaced with POSITION_CHANGE under Neutral
//  val ENEMY_HAND_CHANGE = """^\[Power\] PowerTaskList.+TAG_CHANGE Entity=\[id=(\d+).+ zone=HAND zonePos=(\d+) player=(\d+)\] tag=ZONE_POSITION value=(\d+)""".r
  val ENEMY_CARD_RETURN = """^\[Power\] PowerTaskList.+TAG_CHANGE Entity=\[name=(.+) id=(\d+) zone=(.+) zonePos=(\d+).+ player=(\d+)\] tag=ZONE value=HAND""".r


  //Possible zone_position change: .+TAG_CHANGE Entity=\[name=(.+) id=(\d+).+player=(\d+)\] tag=ZONE_POSITION value=(\d+)
  //Neutral Strings
  //Replaced with POSITION_CHANGE
//  val BOARD_CHANGE = """^\[Power\] PowerTaskList.+TAG_CHANGE Entity=\[name=(.+) id=(\d+) zone=PLAY zonePos=(\d+) cardId=.+ player=(\d)\] tag=ZONE_POSITION value=(\d+)""".r
  //val POSITION_CHANGE = """\[Power\] GameState.DebugPrintPower.+TAG_CHANGE Entity=\[name=.+ id=(\d+).+player=(\d+)\] tag=ZONE_POSITION value=(\d+)""".r\
  val POSITION_CHANGE = """\[Zone\] ZoneChangeList.ProcessChanges.+ \[name=.+ id=(\d+) zone=.+ player=(\d+)\] pos from \d+ -> (\d+)""".r
  val CARD_DEATH = """^\[Power\] PowerTaskList.+TAG_CHANGE Entity=\[name=(.+) id=(\d+) zone=(.+) zonePos=(\d+).+ player=(\d+).+ tag=ZONE value=GRAVEYARD""".r
  val MINION_SUMMONED = """^.+FULL_ENTITY - Updating \[name=(.+) id=(\d+) zone=PLAY zonePos=(\d+).+player=(\d+).+""".r
  val POLYMORPH = """\[Zone\] ZoneChangeList.ProcessChanges.+power=\[type=TAG_CHANGE entity=\[id=\d+.+name=.+\] tag=LINKEDCARD value=(\d+)\] complete=False\] entity=\[name=(.+) id=(\d+) zone=PLAY zonePos=(\d+).+ player=(\d+)\].+""".r


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

  val writer = new PrintWriter(new FileWriter("debug.log"))
  def receive = {
    case START => poll()
    case POLL => poll()
  }

  def poll(): Unit = {
    while (reader.ready()) {
      val line = reader.readLine()
      writer.println(line)
      writer.flush()
      line match {


          // Friendly Events
        case FRIENDLY_CARD_DRAWN(name, id, position,player) =>
          log.info("Friendly Card Drawn: " + line)
          listener ! FriendlyCardDrawnEvent(name, id.toInt, position.toInt,player.toInt)

        case FRIENDLY_PLAYS_CARD(name, id,srcZone,srcPos,dstZone,dstPos) =>
          log.info("Friendly Plays Card: " + line)
          listener ! FriendlyPlaysCardEvent(name, id.toInt, srcZone, srcPos.toInt,dstZone,dstPos.toInt)

//        case FRIENDLY_HAND_CHANGE(name, id, zonePos, player, dstPos) =>
//          log.info("Friendly Hand Change: " + line)
//          listener ! FriendlyHandChangeEvent(name, id.toInt, zonePos.toInt, player.toInt, dstPos.toInt)

        case FRIENDLY_ZONE_CHANGE(name, id, zone, player, dstZone) if dstZone != "GRAVEYARD"  =>
          log.info("Friendly Zone Change: " + line)
          listener ! FriendlyZoneChangeEvent(name, id.toInt, zone, player.toInt, dstZone)

        case FRIENDLY_CARD_RETURN(name, id,zonePos, player, dstPos) =>
          log.info("Friendly Card Return: " + line)
          listener ! FriendlyCardReturnEvent(name, id.toInt, zonePos.toInt,player.toInt, dstPos.toInt)

        case FRIENDLY_MINION_CONTROLLED(name, id) =>
          log.info("Minion Controlled: " + line)
          listener ! FriendlyMinionControlled(name, id.toInt)




          //Enemy Events
        case ENEMY_CARD_DRAWN(id, position,player) =>
          log.info("Enemy Card Drawn: " + line)
          listener ! EnemyCardDrawnEvent(id.toInt, position.toInt,player.toInt)

        case ENEMY_PLAYS_CARD(name, id, dstPos,player) =>
          log.info("Enemy Plays Card: " + line)
          listener ! EnemyPlaysCardEvent(name, id.toInt,dstPos.toInt,player.toInt)

//        case ENEMY_HAND_CHANGE(id, zonePos, player, dstPos) =>
//          log.info("Enemy Hand Change: " + line)
//          listener ! EnemyHandChangeEvent(id.toInt, zonePos.toInt, player.toInt, dstPos.toInt)

        case ENEMY_CARD_RETURN(name,id,zone,zonePos,player) =>
          log.info("Enemy Card Return: " + line)
          listener ! EnemyCardReturnEvent(name,id.toInt,zone,zonePos.toInt,player.toInt)



          //Neutral Events

//        case BOARD_CHANGE(name, id, zonePos, player, dstPos) =>
//          log.info("Board Change: " + line)
//          listener ! BoardChangeEvent(name, id.toInt, zonePos.toInt, player.toInt, dstPos.toInt)

        case POSITION_CHANGE(id, player, dstPos) =>
          log.info("Position Change: " + line)
          listener ! PositionChange(id.toInt, player.toInt, dstPos.toInt)

        case CARD_DEATH(name, id, zone, zonePos, player) =>
          log.info("Card Death: " + line)
          listener ! CardDeath(name, id.toInt, zone, zonePos.toInt, player.toInt)

        case MINION_SUMMONED(name, id, zonePos, player) =>
          log.info("Minion Summoned: " +line)
          listener ! MinionSummoned(name, id.toInt, zonePos.toInt, player.toInt)

        case POLYMORPH(newId, name, id, zonePos, player) =>
          log.info("Polymorph: " +line)
          listener ! Polymorph(newId.toInt,name,id.toInt,zonePos.toInt,player.toInt)




          //Debug Events

        case DEBUG_PRINT_POWER(source, pad, text) =>
          text match {

            case TAG_CHANGE(entity, tag, value) if source == "GameState" && entity == "GameEntity" && tag == "TURN" =>

              listener ! TurnStartEvent(value.toInt)

            case TAG_CHANGE(entity, tag, value) if  tag == "TEAM_ID" =>

              listener ! PlayerDefinedEvent(entity, value.toInt)


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
