package tph.research

import akka.actor.Actor

class Reporter extends Actor with akka.actor.ActorLogging{
  override def receive: Receive = {
    case x => log.info(x.toString)
  }
}