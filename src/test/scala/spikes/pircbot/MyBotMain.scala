package spikes.pircbot

import org.jibble.pircbot.PircBot

/**
  * Created by rconaway on 1/27/16.
  */
object MyBotMain extends App {

  val bot = new MyBot()
    bot.setVerbose(true)
  bot.connect("irc.freenode.net")
  bot.joinChannel("#pircbot")
}

class MyBot extends PircBot {
  setName("MyBot")

  override def onMessage(channel:String, sender:String, login:String, hostname:String, message:String):Unit = {
    if (message.equalsIgnoreCase("time")) {
      val time = new java.util.Date().toString
      sendMessage(channel, sender + ": The time is now " + time)
    }
  }

}
