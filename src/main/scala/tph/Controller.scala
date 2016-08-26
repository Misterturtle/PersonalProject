package tph

//

import akka.actor._
import akka.event.LoggingReceive
import akka.pattern.ask
import akka.util.Timeout
import tph.LogFileEvents._

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._


object Controller {

  trait Message
  object Start extends Message

}

class Controller(system: ActorSystem, hearthstone: ActorRef, logFileReader: ActorRef, ircLogic: ActorRef) extends Actor with ActorLogging {

  override def receive: Receive = LoggingReceive({

    case "init" => // Entry point to start everything
      hearthstone ! "Start"

    case "Start Game" =>
      ircLogic ! "Start Game"
    case "Game Over" =>
      ircLogic ! "Game Over"

    // Messages from LogFileReader
    case DiscoverOption(option) =>
      ircLogic ! DiscoverOption(option)


    case "GetGameStatus" =>
      implicit val timeout = Timeout(30 seconds)
      val future = logFileReader ? "GetGameStatus"
      val result = Await.result(future, timeout.duration).asInstanceOf[Array[Player]]

      if (result.length == 0)
        system.scheduler.scheduleOnce(2000.milli, this.self, "GetGameStatus")
      else
        sender ! result
  })
}


