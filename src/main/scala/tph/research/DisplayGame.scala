package tph.research

import java.io.File

import akka.actor.{ActorRef, ActorSystem, Props}
import tph.{GameWatcher, LogFileReader}

object DisplayGame extends App {

  val system = ActorSystem("TwitchPlaysHearthstone")
  val reporter:ActorRef = system.actorOf(Props[Reporter], "reporter")
  val gameWatcher:ActorRef = system.actorOf(Props(new GameWatcher(system, "Trebor", reporter)), "GameWatcher")
  val logFileReader = system.actorOf(Props(new LogFileReader(system, new File("/Users/rconaway/Library/Logs/Unity/Player.log"), gameWatcher)), "logFileReader")

  logFileReader ! LogFileReader.START

}