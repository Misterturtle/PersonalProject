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
  case class DebugPrintPower(source:String, pad:String, text:String)

  val TAG_CHANGE = """^\s*TAG_CHANGE Entity=(.+) tag=(.+) value=(.+)$""".r
  case class TagChange(source:String, pad:String, entity:String, tag:String, value:String)

  case class NumOptions(source:String, entity:String, value:String)

  val EMPTY_LINE = """^\s*$""".r
  val FILE_NAME = """\(Filename: .*""".r

}


class LogFileReader(system: ActorSystem, file: File, listener: ActorRef) extends Actor {

  import LogFileReader._

  val reader = new BufferedReader(new FileReader(file))

  def receive = {
    case START => poll()
    case POLL => poll()
  }

  def poll(): Unit = {
    while (reader.ready()) {
      reader.readLine() match {
        case DEBUG_PRINT_POWER(source, pad, text)  =>
          text match {
            case TAG_CHANGE(entity, tag, value) if tag == "NUM_OPTIONS" =>
              listener ! NumOptions(source, entity, value)
            case TAG_CHANGE(entity, tag, value) =>
              listener ! TagChange(source, pad, entity, tag, value)
            case _ => listener ! DebugPrintPower(source, pad, text)
          }
        case FILE_NAME() => // ignore
        case EMPTY_LINE() => // ignore
        case x => listener ! ("??? " + x)
      }
    }

    system.scheduler.scheduleOnce(100.millis, this.self, POLL)
  }

}
