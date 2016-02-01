package tph

import akka.actor.ActorRef

case class Actors(actors:ActorRef*) {

  def !(message:AnyRef): Unit = {
    actors.foreach(_ ! message)
  }

}
