package rob

import java.io.File

import akka.actor.{Actor, ActorRef, ActorSystem}

class GameLogReader(file: File, listener: ActorRef) extends Actor with akka.actor.ActorLogging {
  override def receive: Receive = ???
}
