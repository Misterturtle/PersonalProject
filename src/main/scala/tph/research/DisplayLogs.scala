package tph.research

import java.io.File

import akka.actor.{Actor, Props, ActorRef, ActorSystem}
import tph.{LogFileReader}

object DisplayLogs extends App {

  val system = ActorSystem("TwitchPlaysHearthstone")
  val reporter:ActorRef = system.actorOf(Props[Reporter], "reporter")
  val logFileReader = system.actorOf(Props(new LogFileReader(system, new File("/Users/rconaway/Library/Logs/Unity/Player.log"), reporter)), "logFileReader")
  logFileReader ! LogFileReader.START

}