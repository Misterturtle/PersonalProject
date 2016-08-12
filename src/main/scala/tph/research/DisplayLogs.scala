package tph.research

import java.io.File
import akka.actor.{Actor, Props, ActorRef, ActorSystem}
import com.oracle.deploy.update.Updater
import tph.{LogFileReader, GameStatus, TDD}


object DisplayLogs extends App {

  val testMode = false
  val system = ActorSystem("TwitchPlaysHearthstone")
  val gameStatus: ActorRef = system.actorOf(Props(new GameStatus(system)), "gameStatus")

if (testMode == false) {
  val logFileReader = system.actorOf(Props(new LogFileReader(system, new File( """C:\\Program Files (x86)\\Hearthstone\\Hearthstone_Data\\output_log.txt"""), gameStatus)), "logFileReader")
  logFileReader ! LogFileReader.START
}


  //TEST MODE
  if (testMode == true) {
    val TDD: ActorRef = system.actorOf(Props(new TDD(system)), "TDD")
    TDD ! "FULL_TEST"
  }





  gameStatus ! "Display Status"
}





