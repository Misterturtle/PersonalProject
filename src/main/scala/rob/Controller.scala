package rob

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorSystem}

/**
  * Created by rconaway on 1/30/16.
  */
class Controller(system: ActorSystem, screenScraper: ScreenScraper, mouseClicker: MouseClicker, logParser: LogParser) extends Actor with akka.actor.ActorLogging {
  override def receive: Receive = ???
}
