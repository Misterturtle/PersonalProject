package tph.research

import java.io.File
import akka.actor.{Actor, Props, ActorRef, ActorSystem}
import com.oracle.deploy.update.Updater
import tph.{LogFileReader, GameStatus}

object DisplayLogs extends App {

  val system = ActorSystem("TwitchPlaysHearthstone")
  val gameStatus: ActorRef = system.actorOf(Props(new GameStatus(system)), "gameStatus")
  val logFileReader = system.actorOf(Props(new LogFileReader(system, new File( """C:\\Program Files (x86)\\Hearthstone\\Hearthstone_Data\\output_log.txt"""), gameStatus)), "logFileReader")
  logFileReader ! LogFileReader.START
  gameStatus ! "poll"

}