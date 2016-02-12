package rob

import java.util.concurrent.ConcurrentLinkedQueue

import org.jibble.pircbot.PircBot

/**
  * Created by rconaway on 2/4/16.
  */
class ScalaPircBot extends PircBot {

  private var _messageHandler: Option[(String, String, String, String, String) => Unit] = None

  def messageHandler_=(messageHandler: (String, String, String, String, String) => Unit): Unit = _messageHandler = Option(messageHandler)

  def name_=(name: String) = setName(name)

  def verbose_=(verbose: Boolean) = setVerbose(verbose)

  override def onMessage(channel: String, sender: String, login: String, hostName: String, message: String): Unit = {
    assert(_messageHandler.isDefined)
    _messageHandler.get(channel, sender, login, hostName, message)
  }

}
