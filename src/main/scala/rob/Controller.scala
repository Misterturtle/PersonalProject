package rob

import java.io.File

import akka.actor.Actor.Receive
import akka.actor._
import akka.event.LoggingReceive
import com.typesafe.config.{Config, ConfigFactory}
import tph.LogFileReader

object Controller {

  trait Message
  object Start extends Message

}

class Controller(hearthstone:Hearthstone) extends Actor with ActorLogging {
  import Controller._

  override def receive: Receive = LoggingReceive({
    case Start => hearthstone.initialize()
    case log:GameLogReader.Message => hearthstone.message(log)
    case IrcBot.EndTurn => hearthstone.clickEndTurn
  })
}


