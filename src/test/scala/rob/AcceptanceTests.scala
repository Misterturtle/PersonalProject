package rob

import org.jibble.pircbot.PircBot
import org.scalatest.{FreeSpec, Matchers}

/**
  * Created by rconaway on 1/30/16.
  */
class AcceptanceTests extends FreeSpec with Matchers {

  val HOST = "irc.freenode.net"
  val CHANNEL = "#pircbot"

  "Wait for turn then end turn" in {
    start()
    waitForOpponentTurn()

    startBotToVoteEndTurn("Lucy")
    startBotToVotePlayCard("Ricky")
    startBotToVoteEndTurn("Ethel")

    waitForMyTurn()
    waitForOpponentTurn()
    end()
  }

  def startBotToVoteEndTurn(name:String) =
    new PircBot {
      setName(name)
      setVerbose(false)
      connect(HOST)
      joinChannel(CHANNEL)

      override def onMessage(channel: String, sender: String, login: String, hostName: String, message: String): Unit = {
        if (message == "?vote?") {
          this.sendMessage("tph", ":end turn")
          this.disconnect()
          this.dispose()
        }
      }
    }

  def startBotToVotePlayCard(name:String) =
    new PircBot {
      setName("Ricky")
      setVerbose(false)
      connect(HOST)
      joinChannel(CHANNEL)

      override def onMessage(channel: String, sender: String, login: String, hostName: String, message: String): Unit = {
        if (message == "?vote?") {
          this.sendMessage("tph", ":play 1")
          this.disconnect()
          this.dispose()
        }
      }
    }

  def start() = ???

  def waitForMyTurn() = ???

  def waitForOpponentTurn() = ???

  def end() = ???

}
