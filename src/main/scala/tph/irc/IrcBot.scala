package tph.irc

import akka.actor.ActorRef
import org.jibble.pircbot.PircBot

object IrcBot {
  val NICKNAME = "Trebor"
  case class Message(channel:String, sender:String, login:String, hostName:String, message:String)
}

class IrcBot(hostName:String, channel:String, watcher:ActorRef) extends PircBot {
  import IrcBot._

  setName(NICKNAME)
  setVerbose(false)
  connect(hostName)
  joinChannel(channel)

  override def onMessage(channel:String, sender:String, login:String, hostName:String, message:String): Unit = {
      watcher ! Message(channel, sender, login, hostName, message)
  }
}
