package rob

import akka.actor.ActorRef
import com.typesafe.scalalogging.LazyLogging
import org.jibble.pircbot.PircBot

object IrcBot {
  val NICKNAME = "Trebor"
  trait Message
  object EndTurn extends Message

}

import IrcBot._
class IrcBot(hostName:String, channel:String, watcher:ActorRef, nickname:String=IrcBot.NICKNAME) extends PircBot with LazyLogging {

  setName(nickname)
  logger.debug(s"nickname = $nickname")
  setVerbose(false)
  connect(hostName)
  logger.debug(s"connected to $hostName")
  joinChannel(channel)
  logger.debug(s"joined $channel")

  override def onMessage(channel:String, sender:String, login:String, hostName:String, message:String): Unit = {
    logger.debug(s"IRC message: sender=$sender, message=$message")
      message.toLowerCase match {
        case "end turn" => watcher ! EndTurn
        case _ =>
      }
  }
}
