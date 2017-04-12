package VoteSystemTests

import Logic.IRCState
import VoteSystem._
import org.scalatest.{FreeSpec, FlatSpec, Matchers}
import tph.Constants.ActionVotes.{CardPlay, ActionUninit}
import tph.Constants.Vote
import tph.{GameState, IRCBot}
import org.scalatest.tagobjects.Slow

import scala.collection.mutable.ListBuffer

/**
  * Created by Harambe on 3/10/2017.
  */


class IRCBotTests extends FreeSpec with Matchers {


  val sender = "IRCBotTests"

  "The IRCBot should" - {

     "pass a vote into VoteManager to be entered when receiving a message" in {

      var voteEntered = false
       val gs = new GameState()
       val vs = new VoteState()
       val ai = new VoteAI(vs,gs)
       val ircState = new IRCState()
       val validator = new VoteValidator(gs)
      val mockVM = new VoteManager(gs,vs,ai, ircState, validator) {
        override def voteEntry(voterName: String, vote: Vote): Unit = {
          voteEntered = true
        }
      }
      val ircBot = new IRCBot(mockVM)
      ircBot.onMessage("None", sender, "None", "None", "!c1")
      voteEntered shouldBe true
    }

     "not try to identify a string that doesn't start with a bang" in {
      var voteIdentified = false
       val gs = new GameState()
       val vs = new VoteState()
       val ai = new VoteAI(vs,gs)
       val ircState = new IRCState()
       val validator = new VoteValidator(gs)
       val mockVM = new VoteManager(gs,vs,ai, ircState, validator) {
        override def voteEntry(voterName:String, vote:Vote):Unit = {
          voteIdentified = true
        }
        
      }
      val ircBot = new IRCBot(mockVM)
      ircBot.onMessage("None", sender, "None", "None", "Any String")
      voteIdentified shouldBe false
    }

     "not pass an ActionUninit to voteManager" in {
      var voteEntered = false
       val gs = new GameState()
       val vs = new VoteState()
       val ai = new VoteAI(vs,gs)
       val ircState = new IRCState()
       val validator = new VoteValidator(gs)
       val mockVM = new VoteManager(gs,vs,ai, ircState, validator) {
        override def voteEntry(voterName: String, vote: Vote): Unit = {
          voteEntered = true
        }
      }
      val bot = new IRCBot(mockVM)
      bot.onMessage("None", sender, "None", "None", "!Any string")
      voteEntered shouldBe false
    }

     "parse apart multiple commands from one input" in {
      var voteCounter = 0
       val gs = new GameState()
       val vs = new VoteState()
       val ai = new VoteAI(vs,gs)
       val ircState = new IRCState()
       val validator = new VoteValidator(gs)
       val mockVM = new VoteManager(gs,vs,ai, ircState, validator) {
        override def voteEntry(voterName: String, vote: Vote): Unit = {
          voteCounter += 1
        }
      }
      val bot = new IRCBot(mockVM)
      bot.onMessage("None", sender, "None", "None", "!c1, f1>e4, f3  > e1, any string, ")
      voteCounter shouldBe 3

    }

     "split multiple votes and pass multiple votes to VoteManager to be entered" in {
      var voteCounter = 0
       val gs = new GameState()
       val vs = new VoteState()
       val ai = new VoteAI(vs,gs)
       val ircState = new IRCState()
       val validator = new VoteValidator(gs)
       val mockVM = new VoteManager(gs,vs,ai, ircState, validator) {
        override def voteEntry(voterName: String, vote: Vote): Unit = {
          voteCounter += 1
        }
      }
      val ircBot = new IRCBot(mockVM)
      ircBot.onMessage("None", sender, "None", "None", "!c1, f1>e4, f3  > e1, any string, ")
      voteCounter shouldBe 3
    }
  }
}