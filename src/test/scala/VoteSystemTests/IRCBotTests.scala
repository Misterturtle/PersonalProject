package VoteSystemTests

import GameState.GameState
import Logic.IRCState
import VoteSystem._
import org.scalatest.{FreeSpec, FlatSpec, Matchers}
import tph.Constants.ActionVotes.{NormalAttack, ActionVote, CardPlay, ActionUninit}
import tph.Constants.Vote
import tph.IRCBot
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
      val ai = new VoteAI(vs, gs)
      val ircState = new IRCState()
      val validator = new VoteValidator(gs)
      val mockVM = new VoteManager(gs, vs, ai, ircState, validator) {
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
      val ai = new VoteAI(vs, gs)
      val ircState = new IRCState()
      val validator = new VoteValidator(gs)
      val mockVM = new VoteManager(gs, vs, ai, ircState, validator) {
        override def voteEntry(voterName: String, vote: Vote): Unit = {
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
      val ai = new VoteAI(vs, gs)
      val ircState = new IRCState()
      val validator = new VoteValidator(gs)
      val mockVM = new VoteManager(gs, vs, ai, ircState, validator) {
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
      val ai = new VoteAI(vs, gs)
      val ircState = new IRCState()
      val validator = new VoteValidator(gs)
      val mockVM = new VoteManager(gs, vs, ai, ircState, validator) {
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
      val ai = new VoteAI(vs, gs)
      val ircState = new IRCState()
      val validator = new VoteValidator(gs)
      val mockVM = new VoteManager(gs, vs, ai, ircState, validator) {
        override def voteEntry(voterName: String, vote: Vote): Unit = {
          voteCounter += 1
        }
      }
      val ircBot = new IRCBot(mockVM)
      ircBot.onMessage("None", sender, "None", "None", "!c1, f1>e4, f3  > e1, any string, ")
      voteCounter shouldBe 3
    }


    "report the voteList when asked" in {
      var voteListRetrieved = false
      var voterParam = ""
      val vs = new VoteState()
      val gs = new GameState
      val ai = new VoteAI(vs,gs)
      val ircState = new IRCState
      val voteValidator = new VoteValidator(gs)
      val vm = new VoteManager(gs,vs,ai,ircState,voteValidator){
        override def getVoteListAsString(voterName: String): String = {
          voteListRetrieved = true
          voterParam = voterName
          voterName
        }
      }
      val vp = new VoteParser()
      val ircBot = new IRCBot(vm, vp)


      ircBot.onMessage("None", sender, "None", "None", "!votelist")


      voterParam shouldBe sender
      voteListRetrieved shouldBe true
    }


    "Remove votes on command" in {
      val voterParamList = ListBuffer[String]()
      val removedVoteList = ListBuffer[ActionVote]()
      val vs = new VoteState()
      val gs = new GameState
      val ai = new VoteAI(vs,gs)
      val ircState = new IRCState
      val voteValidator = new VoteValidator(gs)
      val vm = new VoteManager(gs,vs,ai,ircState,voteValidator){
        override def removeVote(voterName: String, vote: ActionVote): Unit = {
          voterParamList.append(voterName)
          removedVoteList.append(vote)
        }
      }
      case class MockActionVote()extends ActionVote {
        override def copyVote(card: Int, friendlyTarget: Int, enemyTarget: Int, position: Int): ActionVote = this
      }
      val vp = new VoteParser(){
        override def createVote(sender: String, command: String): Vote = {
          MockActionVote()
        }
      }
      val ircBot = new IRCBot(vm, vp)


      ircBot.onMessage("None", sender, "None", "None", "!remove c1,f1>e1")


      voterParamList shouldBe ListBuffer[String](sender,sender)
      removedVoteList shouldBe ListBuffer[ActionVote](MockActionVote(), MockActionVote())
    }


    "Accept mulligan votes" in {
      var mulliganVoteEntered = false
      val vs = new VoteState()
      val gs = new GameState
      val ai = new VoteAI(vs,gs)
      val ircState = new IRCState
      val voteValidator = new VoteValidator(gs)
      val vm = new VoteManager(gs,vs,ai,ircState,voteValidator){
        override def voteEntry(voterName: String, vote: Vote): Unit = {
          mulliganVoteEntered = true
        }
      }
      case class MockActionVote()extends ActionVote {
        override def copyVote(card: Int, friendlyTarget: Int, enemyTarget: Int, position: Int): ActionVote = this
      }
      val vp = new VoteParser(){
        override def parseMulligan(sender: String, cards: String): ActionVote = {
          MockActionVote()
        }
      }
      val ircBot = new IRCBot(vm, vp)


      ircBot.onMessage("None", sender, "None", "None", "!mulligan 1,2,3")


      mulliganVoteEntered shouldBe true
    }







  }
}