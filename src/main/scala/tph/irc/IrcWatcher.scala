package tph.irc

import akka.actor.{ActorRef, Actor}
import akka.event.LoggingReceive
import tph.Actors

object IrcWatcher {

  trait Command

  object Exit extends Command
  case class Hello(name:String) extends Command

  val EXIT = """:+\s*exit\s*""".r
  val HELLO = """:+\s*hello\s+(\S+)\s*""".r
}

class IrcWatcher(listeners: Actors) extends Actor with akka.actor.ActorLogging {

  import IrcWatcher._

  override def receive: Receive = LoggingReceive({
    case IrcBot.Message(_, _, _, _, message) =>
      parseCommand(message) match {
        case Some(x) =>
          listeners ! x
        case None =>
      }
  })


  def parseCommand(command: String): Option[Command] = {
    command match {
      case EXIT() => Some(Exit)
      case HELLO(name) => Some(Hello(name))

      case _ =>
        log.warning(s"Unexpected command: $command")
        None
    }
  }
}
