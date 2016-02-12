package rob

import java.util.concurrent.ConcurrentLinkedQueue

import com.typesafe.scalalogging.LazyLogging
import org.jibble.pircbot.PircBot

object Player {
  val TIMEOUT_MS = 3000L
}
class Player(host:String, channel:String, nickname:String) extends LazyLogging {
  import Player._

  case class Message()

  val messages = new ConcurrentLinkedQueue[String]


  logger.debug(s"host=$host, channel=$channel, nickname=$nickname")
  val bot = new PlayerBot(host, channel, nickname, messages)

  def clear():Player = {
    messages.clear()
    this
  }

  def send(s: String):Player = {
    bot.sendMessage(channel, s)
    this
  }

  def waitFor(s: String):Player = {
    val timeout = System.currentTimeMillis() + TIMEOUT_MS
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

class PlayerBot(host:String, channel:String, nickname:String, messages:ConcurrentLinkedQueue[String]) extends PircBot with LazyLogging {
  logger.debug(s"host=$host, channel=$channel, nickname=$nickname")
  setName(nickname)
  setVerbose(false)
  connect(host)
  joinChannel(channel)

  override def onMessage(channel:String, sender:String, login:String, hostName:String, message:String): Unit = {
    messages.add(message)
  }

}