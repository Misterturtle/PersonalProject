package rob

import java.util.concurrent.ConcurrentLinkedQueue

import org.jibble.pircbot.PircBot


class Player(host:String, channel:String, nickname:String) {

  case class Message()

  val messages = new ConcurrentLinkedQueue[String]

  val bot = new PircBot {
    setName(nickname)
    setVerbose(false)
    connect(host)
    joinChannel(channel)

    override def onMessage(channel:String, sender:String, login:String, hostName:String, message:String): Unit = {
      messages.add(message)
    }

  }

  def clear():Player = {
    messages.clear()
    this
  }

  def send(s: String):Player = {
    bot.sendMessage(channel, s)
    this
  }

  def waitFor(s: String):Player = {
    val timeout = System.currentTimeMillis() + 30000L
    while (timeout > System.currentTimeMillis()) {
      Option(messages.poll()) match {
        case Some(x) if x == s => return this
        case None => Thread.sleep(100)
      }
    }

    throw new RuntimeException(s"$nickname timed out waiting for $s")
  }

  def close():Player = {
    bot.disconnect()
    bot.dispose()
    this
  }

}
