package tph.irc

import java.io.File

import akka.actor.Actor.Receive
import akka.actor._
import akka.event.LoggingReceive
import rob.IrcBot
import tph.Actors
import tph.research.Reporter
import scala.concurrent.duration._
import scala.concurrent.{Future, Await}
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

object IrcDemo extends App {

  val system = ActorSystem("IrcMain")
  val reporter = system.actorOf(Props[Reporter], "reporter")
  val ircDemo = system.actorOf(Props[IrcDemo])

  val ircWatcher = system.actorOf(Props(new IrcWatcher(Actors(reporter, ircDemo))), "ircWatcher")
  val ircBot = new IrcBot("irc.freenode.net", "#pircbot", ircWatcher)

  def stop(): Unit = {
    reporter ! Messages.Stop
    ircBot.disconnect()
    ircBot.dispose()

    system.terminate() onFailure {
      case t => println(s"failed to stop: $t")
    }

  }
}

class IrcDemo extends Actor with akka.actor.ActorLogging {
  override def receive: Receive = LoggingReceive ({
    case IrcWatcher.Exit =>
      IrcDemo.stop()
    case _ =>
  })
}