package rob

import java.io.File

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorRef, ActorSystem}
import org.apache.commons.io.input.{Tailer, TailerListenerAdapter, TailerListener}

import scala.concurrent.{Future, ExecutionContext}

object GameLogReader {

  trait Message

  val TAG_CHANGE = """\[Power\] (\S+)\.DebugPrintPower\(\) - TAG_CHANGE Entity=(.+) tag=(.+) value=(.+)""".r
  case class PlayState(entity: String, value: String) extends Message

  def parse(line: String): Option[Message] = {
    line match {
      case TAG_CHANGE(source, entity, tag, value) =>
        if (source == "GameState" && tag == "PLAYSTATE") Some(PlayState(entity, value))
        else None
      case _ => None
    }
  }
}

class GameLogReader(file: File, listener: ActorRef, executionContext: ExecutionContext) {

  import GameLogReader._

  val tailListener = new TailerListenerAdapter {
    override def handle(line: String): Unit = {
      parse(line).foreach(x => listener ! x)
    }
  }

  val tailer = Tailer.create(file, tailListener, 100)

  def start(): Unit = {
    //Future { tailer.run() } (executionContext)
  }

  def stop(): Unit = {
    tailer.stop()
  }

}
