package tph.research

import akka.actor.Actor
import akka.event.LoggingReceive

class Reporter extends Actor with akka.actor.ActorLogging{
  override def receive: Receive = LoggingReceive({
    case x => log.info(x.toString)
  })
}