package spikes.pircbot

import org.jibble.pircbot.PircBot

/**
  * Created by rconaway on 1/27/16.
  */
object Sender extends App {
  val bot = new SenderBot()
  bot.setVerbose(true)
  bot.connect("irc.freenode.net")
  bot.joinChannel("#pircbot")
  //bot.changeNick("Trebor")

  bot.sendMessage("#pircbot", "time")

}

class SenderBot extends PircBot {
  setName("SenderBot")
}