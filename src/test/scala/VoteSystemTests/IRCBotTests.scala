package VoteSystemTests

import VoteSystem.{VoteParser, Vote, VoteManager}
import org.scalatest.{FlatSpec, Matchers}
import tph.Constants.ActionVotes.{CardPlay, ActionUninit}
import tph.{GameState, IRCBot}
import org.scalatest.tagobjects.Slow

import scala.collection.mutable.ListBuffer

/**
  * Created by Harambe on 3/10/2017.
  */


class IRCBotTests extends FlatSpec with Matchers {


  val sender = "IRCBotTests"
  val baseIRCBot = new IRCBot(new VoteManager(new GameState()))

  "The IRCBot" should "identify twitch input" in {
    val mockParser = new VoteParser() {
      override def createVote(sender: String, command: String): Vote = {
        // TODO assert parameters
        CardPlay(1)
      }
    }
    val ircBot = new IRCBot(new VoteManager(new GameState()))
    ircBot.identifyTwitchInput(sender, "!Any string", mockParser) shouldBe new CardPlay(1)
  }

  it should "pass a vote into VoteManager to be entered when receiving a message" in {
    var voteEntered = false
    val mockVM = new VoteManager(new GameState()) {
      override def voteEntry(voterName: String, vote: Vote): Unit = {
        voteEntered = true
      }
    }
    val ircBot = new IRCBot(mockVM){
      // TODO
      override def identifyTwitchInput(sender:String, message:String, voteParser: VoteParser): Vote = {
        new CardPlay(1)
      }
    }
    ircBot.onMessage("None", sender, "None", "None", "!Any String")
    voteEntered shouldBe true
  }

  it should "not try to identify a string that doesn't start with a bang" in {
    var voteIdentified = false

    val ircBot = new IRCBot(new VoteManager(new GameState())) {
      override def identifyTwitchInput(sender: String, message: String, voteParser: VoteParser): Vote = {
        voteIdentified = true
        new CardPlay(2)
      }
    }
    ircBot.onMessage("None", sender, "None", "None", "Any String")
    voteIdentified shouldBe false
  }

  it should "not pass an ActionUninit to voteManager" in {
    var voteEntered = false
    val mockVM = new VoteManager(new GameState()) {
      override def voteEntry(voterName: String, vote: Vote): Unit = {
        voteEntered = true
      }
    }

    val bot = new IRCBot(mockVM) {
      override def identifyTwitchInput(sender: String, message: String, voteParser: VoteParser): Vote = {
        ActionUninit()
      }
    }
    bot.onMessage("None", sender, "None", "None", "!Any string")
    voteEntered shouldBe false
  }

  it should "parse apart multiple commands from one input" in {
    val vm = new VoteManager(new GameState())
    val bot = new IRCBot(vm)
    bot.parseMultipleCommands("c1, f1>e4, f3  > e1, any string, ") shouldBe List[String]("c1", " f1>e4", " f3  > e1", " any string", " ")
  }

  it should "split multiple votes and pass multiple votes to VoteManager to be entered" in {
    var voteCounter = 0
    val mockVM = new VoteManager(new GameState()){
      override def voteEntry(voterName:String, vote:Vote): Unit = {
        voteCounter += 1
      }
    }
    val ircBot = new IRCBot(mockVM)
    ircBot.onMessage("None", sender, "None", "None", "!c1, f1>e4, f3  > e1, any string, ")
    voteCounter shouldBe 3
  }
}
