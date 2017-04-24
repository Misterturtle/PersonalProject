package tph

import java.io.{File, FileWriter, PrintWriter}

import VoteSystem.{VoteManager, VoteParser}
import com.typesafe.config.ConfigFactory
import org.jibble.pircbot.PircBot
import tph.Constants.ActionVotes._
import tph.Constants.Vote

import scala.util.matching.Regex

/**
  * Created by Harambe on 2/22/2017.
  */

class IRCBot(voteManager: VoteManager, voteParser: VoteParser = new VoteParser) extends PircBot {


  val config = ConfigFactory.load()
  val hostName = config.getString("tph.irc.host")
  val channel = config.getString("tph.irc.channel")
  val nickname = "TPHBot"
  val writer = new PrintWriter(new FileWriter(new File(config.getString("tph.writerFiles.voteLog"))))
  val GENERAL_COMMAND = """!(.+)""".r
  val REMOVE_COMMAND = """!remove(.+)""".r
  val VOTE_LIST_COMMAND = "votelist"
  val MULLIGAN_VOTE = """mulligan(.+)""".r

  def init(): Unit = {
    setName(nickname)
    connect(hostName)
  }

  override def onConnect(): Unit = {
    joinChannel(channel)
  }

  override def onMessage(channel: String, sender: String, login: String, hostName: String, message: String): Unit = {
    message.toLowerCase.replaceAll("\\s+","") match {

      case REMOVE_COMMAND(command) =>
        val commands = command.split(',').map(_.trim)
        commands foreach {
          case singleCommand =>
            val vote = voteParser.createVote(sender, singleCommand)
            vote match {
              case ActionUninit() =>
              case actionVote: ActionVote =>
                voteManager.removeVote(sender, actionVote)
            }
        }


      case GENERAL_COMMAND(command) =>
        command match {
          case VOTE_LIST_COMMAND =>
            val voteListMessage = voteManager.getVoteListAsString(sender)
            sendMessage(sender, voteListMessage)

          case MULLIGAN_VOTE(cards) =>
            val vote = voteParser.parseMulligan(sender, cards)
            if(vote != ActionUninit())
            voteManager.voteEntry(sender, vote)


          case _ =>

            val commands = command.split(',').map(_.trim)
            commands foreach {
              case singleCommand =>
                val vote = voteParser.createVote(sender, singleCommand)
                vote match {
                  case ActionUninit() =>
                  case _ =>
                    voteManager.voteEntry(sender, vote)
                }
            }
        }



      case _ =>
    }
  }




}
