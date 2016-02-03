package rob

import akka.actor.Actor
import akka.actor.Actor.Receive

class Hearthstone(scraper: ScreenScraper, clicker:MouseClicker, logParser:LogParser) extends Actor with akka.actor.ActorLogging {

  def start():Unit = ???

  def isPlaying:Boolean = ???

  def resign():Unit = ???


  override def receive: Receive = ???
}
