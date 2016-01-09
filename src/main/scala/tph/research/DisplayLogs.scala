package tph.research

import java.io.File

import akka.actor.{Actor, Props, ActorRef, ActorSystem}
import tph.{LogFileReader}

object DisplayLogs extends App {

  val system = ActorSystem("TwitchPlaysHearthstone")
  val reporter:ActorRef = system.actorOf(Props[Reporter], "reporter")
  val logFileReader = system.actorOf(Props(new LogFileReader(system, new File("""C:\Program Files (x86)\Hearthstone\Hearthstone_Data\output_log.txt"""), reporter)), "logFileReader")
  logFileReader ! LogFileReader.START

}