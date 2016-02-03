package rob

import java.io.File

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorRef, ActorSystem}
import org.apache.commons.io.input.{Tailer, TailerListenerAdapter, TailerListener}

import scala.concurrent.{Future, ExecutionContext}

object GameLogReader {
  case class Message(line:String)
}

class GameLogReader(file: File, listener: ActorRef, executionContext:ExecutionContext) {
  import GameLogReader._

  val tailListener = new TailerListenerAdapter {
    override def handle(line:String):Unit = {
      println(s"handle $line")
      listener ! Message(line)
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
