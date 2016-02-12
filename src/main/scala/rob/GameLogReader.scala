package rob

import java.io.File

import akka.actor.Actor.Receive
import akka.actor.{Props, Actor, ActorRef, ActorSystem}
import akka.event.LoggingReceive
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import org.apache.commons.io.input.{Tailer, TailerListenerAdapter, TailerListener}

import scala.concurrent.{Future, ExecutionContext}

object GameLogReader extends LazyLogging {

  trait Message

  val TAG_CHANGE = """\[Power\]\s+(\S+)\.DebugPrintPower\(\)\s+-\s+TAG_CHANGE\s+Entity=(.+)\s+tag=(.+)\s+value=(.+)""".r
  case class PlayState(entity: String, value: String) extends Message

  def parse(line: String): Option[Message] = {
    line match {
      case TAG_CHANGE(source, entity, tag, value) =>
        if (source == "GameState" && tag == "PLAYSTATE") {
          val msg = Some(PlayState(entity, value))
          msg
        }
        else {
          None
        }
      case x =>
        None
    }
  }
}

class GameLogReader(file: File, listener: ActorRef, executionContext: ExecutionContext) extends LazyLogging {

  import GameLogReader._

  val tailListener = new TailerListenerAdapter {
    override def handle(line: String): Unit = {
      val parsed = parse(line)
      if (parsed.isDefined) {
        listener ! parsed.get
      }

    }
  }

  val tailer = Tailer.create(file, tailListener, 100)

  def start(): Unit = {
    Future { tailer.run() } (executionContext)
  }

  def stop(): Unit = {
    tailer.stop()
  }

}

object GameLogReaderDemo extends App with LazyLogging {
  val config = ConfigFactory.load()
  val system = ActorSystem("GameLogReaderDemo")
  val target = system.actorOf(Props[Target])
  val gameLogFile = new File(config.getString("tph.game-log.file"))
  val gameLogReader = new GameLogReader(gameLogFile, target, system.dispatcher)
  gameLogReader.start()

  class Target extends Actor with akka.actor.ActorLogging {
    override def receive = LoggingReceive ({
      case x => 
    })
  }

}

