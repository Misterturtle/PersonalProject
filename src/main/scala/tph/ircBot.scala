package tph

import java.io.{File, FileWriter, PrintWriter}

import VoteSystem.{Vote, VoteManager, VoteParser}
import com.typesafe.config.ConfigFactory
import org.jibble.pircbot.PircBot
import tph.Constants.ActionVotes.ActionUninit

import scala.util.matching.Regex

/**
  * Created by Harambe on 2/22/2017.
  */

class IRCBot(voteManager: VoteManager) extends PircBot {


  val config = ConfigFactory.load()
  val hostName = config.getString("tph.irc.host")
  val channel = config.getString("tph.irc.channel")
  val nickname = "TPHBot"
  val writer = new PrintWriter(new FileWriter(new File(config.getString("tph.writerFiles.voteLog"))))
  val GENERAL_COMMAND = """!(.+)""".r

  def init(): Unit = {
    setName(nickname)
    setVerbose(false)
    connect(hostName)
  }

  override def onConnect(): Unit = {
    joinChannel(channel)
  }

  def identifyTwitchInput(sender:String, message:String, voteParser:VoteParser): Vote = {
    voteParser.createVote(sender, message)
  }

  def parseMultipleCommands(twitchInput: String, accumlator:List[String] = List[String]()): List[String] = {
    val headAndTail = """(^[^,]*),(.+)""".r
    twitchInput match {
      case headAndTail(head, tail) =>
        parseMultipleCommands(tail, head :: accumlator)
      case _ =>
        (twitchInput :: accumlator).reverse
    }
  }

  override def onMessage(channel: String, sender: String, login: String, hostName: String, message: String): Unit = {
        message match{
          case GENERAL_COMMAND(command) =>
            val commands = parseMultipleCommands(command)
            commands foreach {
              case singleCommand =>
              val vote = identifyTwitchInput(sender, singleCommand, new VoteParser())
              if (vote != ActionUninit())
                voteManager.voteEntry(sender, vote)
            }

          case _ =>
        }
    }

}
