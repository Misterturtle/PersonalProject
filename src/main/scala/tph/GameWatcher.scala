package tph

import akka.actor.{ActorSystem, ActorRef, Actor}
import akka.actor.Actor.Receive
import tph.LogFileReader.NumOptions


object GameWatcher {

  var inTurn = false

  case object TurnStarted
  case object TurnEnded


}

class GameWatcher(system:ActorSystem, playerName:String, listener:ActorRef) extends Actor {
  import GameWatcher._

  var inTurn = false

  def receive: Receive = {
    case NumOptions ("PowerTaskList", p, n) if p == playerName && ! inTurn =>
      inTurn = true
      listener ! TurnStarted

    case NumOptions("PowerTaskList", p, n) if p != playerName && inTurn =>
      inTurn = false
      listener ! TurnEnded

    case _ => // ignore
  }
}
