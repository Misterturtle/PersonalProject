package AcceptanceTests

import java.util.Scanner

import GUI.{Display}
import GameState.GameState
import Logic.IRCState
import VoteSystem._
import com.typesafe.config.ConfigFactory
import org.jibble.pircbot.PircBot
import org.scalatest.tagobjects.Slow
import org.scalatest.{Tag, FreeSpec, Matchers}
import tph.Constants.ActionVotes._
import tph._


//todo Accept hurry votes

class TwitchUser extends FreeSpec with Matchers {


  "As a Twitch User, I want to" - {

    "be able to type the commands 'c1-10' to refer to my hand, so that I can quickly send votes" in {
      val vs = new VoteState()
      val gs = new GameState()
      val ai = new VoteAI(vs,gs)
      val ircState = new IRCState()
      val validator = new VoteValidator(gs)
      val vm = new VoteManager(gs, vs, ai, ircState, validator)
      val ircBot = new IRCBot(vm)
      for(a<- 1 to 10)
      ircBot.onMessage("None", "A Twitch User", "None", "None", s"!c$a")
      vs.voterMap("A Twitch User").actionVoteList shouldBe List[ActionVote](
        CardPlay(1),
        CardPlay(2),
        CardPlay(3),
        CardPlay(4),
        CardPlay(5),
        CardPlay(6),
        CardPlay(7),
        CardPlay(8),
        CardPlay(9),
        CardPlay(10))}

    "be able to type the commands '?c1-10' to refer to future cards my hand, so that I can vote to play cards that are not yet in my hand" in {
      val vs = new VoteState()
      val gs = new GameState()
      val ai = new VoteAI(vs,gs)
      val ircState = new IRCState()
      val validator = new VoteValidator(gs)
      val vm = new VoteManager(gs, vs, ai, ircState, validator)
      val ircBot = new IRCBot(vm)
      for(a<- 1 to 10)
        ircBot.onMessage("None", "A Twitch User", "None", "None", s"!?c$a")
      vs.voterMap("A Twitch User").actionVoteList shouldBe List[ActionVote](
        FutureCardPlay(1),
        FutureCardPlay(2),
        FutureCardPlay(3),
        FutureCardPlay(4),
        FutureCardPlay(5),
        FutureCardPlay(6),
        FutureCardPlay(7),
        FutureCardPlay(8),
        FutureCardPlay(9),
        FutureCardPlay(10))}


    "be able to type the command 'f0-10' to refer to my hero and board, so that I can quickly send votes" in {
      val vs = new VoteState()
      val gs = new GameState()
      val ai = new VoteAI(vs,gs)
      val ircState = new IRCState()
      val validator = new VoteValidator(gs)
      val vm = new VoteManager(gs, vs, ai, ircState, validator)
      val ircBot = new IRCBot(vm)
      for(a<- 0 to 10)
        ircBot.onMessage("None", "A Twitch User", "None", "None", s"!c1 > f$a")
      vs.voterMap("A Twitch User").actionVoteList shouldBe List[ActionVote](
        CardPlayWithFriendlyTarget(1,0),
        CardPlayWithFriendlyTarget(1,1),
        CardPlayWithFriendlyTarget(1,2),
        CardPlayWithFriendlyTarget(1,3),
        CardPlayWithFriendlyTarget(1,4),
        CardPlayWithFriendlyTarget(1,5),
        CardPlayWithFriendlyTarget(1,6),
        CardPlayWithFriendlyTarget(1,7),
        CardPlayWithFriendlyTarget(1,8),
        CardPlayWithFriendlyTarget(1,9),
        CardPlayWithFriendlyTarget(1,10))}


    "be able to type the command 'e0-10' to refer to the future enemy hero and board, so that I can quickly send votes" in {
      val vs = new VoteState()
      val gs = new GameState()
      val ai = new VoteAI(vs,gs)
      val ircState = new IRCState()
      val validator = new VoteValidator(gs)
      val vm = new VoteManager(gs, vs, ai, ircState, validator)
      val ircBot = new IRCBot(vm)
      for(a<- 0 to 10)
        ircBot.onMessage("None", "A Twitch User", "None", "None", s"!c1 > e$a")
      vs.voterMap("A Twitch User").actionVoteList shouldBe List[ActionVote](
        CardPlayWithEnemyTarget(1,0),
        CardPlayWithEnemyTarget(1,1),
        CardPlayWithEnemyTarget(1,2),
        CardPlayWithEnemyTarget(1,3),
        CardPlayWithEnemyTarget(1,4),
        CardPlayWithEnemyTarget(1,5),
        CardPlayWithEnemyTarget(1,6),
        CardPlayWithEnemyTarget(1,7),
        CardPlayWithEnemyTarget(1,8),
        CardPlayWithEnemyTarget(1,9),
        CardPlayWithEnemyTarget(1,10))}


    "be able to type the command 'hp' to refer to my hero power, so that I can quickly send votes " in {
      val vs = new VoteState()
      val gs = new GameState()
      val ai = new VoteAI(vs,gs)
      val ircState = new IRCState()
      val validator = new VoteValidator(gs)
      val vm = new VoteManager(gs, vs, ai, ircState, validator)
      val ircBot = new IRCBot(vm)
        ircBot.onMessage("None", "A Twitch User", "None", "None", "!hp")
      vs.voterMap("A Twitch User").actionVoteList shouldBe List[ActionVote](
        HeroPower())}



    "be able to type the command '>' when I want some card to target another card, so that I quickly send votes." in {
      val vs = new VoteState()
      val gs = new GameState()
      val ai = new VoteAI(vs,gs)
      val ircState = new IRCState()
      val validator = new VoteValidator(gs)
      val vm = new VoteManager(gs, vs, ai, ircState, validator)
      val ircBot = new IRCBot(vm)
      ircBot.onMessage("None", "A Twitch User", "None", "None", "!c1 > f1")
      vs.voterMap("A Twitch User").actionVoteList shouldBe List[ActionVote](
        CardPlayWithFriendlyTarget(1,1))}


    "be able to type the command '>>' when I want to specify what position to play the card at, so that I can quickly send votes." in {
      val vs = new VoteState()
      val gs = new GameState()
      val ai = new VoteAI(vs,gs)
      val ircState = new IRCState()
      val validator = new VoteValidator(gs)
      val vm = new VoteManager(gs, vs, ai, ircState, validator)
      val ircBot = new IRCBot(vm)
      ircBot.onMessage("None", "A Twitch User", "None", "None", "!c1 >> f1")
      vs.voterMap("A Twitch User").actionVoteList shouldBe List[ActionVote](
        CardPlayWithPosition(1,1))}

    "be able to type the command '>>' and '>' when I want to specify what position to play the card at AND target, so that I can quickly send votes." in {
      val vs = new VoteState()
      val gs = new GameState()
      val ai = new VoteAI(vs,gs)
      val ircState = new IRCState()
      val validator = new VoteValidator(gs)
      val vm = new VoteManager(gs, vs, ai, ircState, validator)
      val ircBot = new IRCBot(vm)
      ircBot.onMessage("None", "A Twitch User", "None", "None", "!c1 >> f1 > f1")
      vs.voterMap("A Twitch User").actionVoteList shouldBe List[ActionVote](
        CardPlayWithFriendlyTargetWithPosition(1,1,1))}

    "be able to send all my commands in one line, so that I can quickly send votes." in {
      val vs = new VoteState()
      val gs = new GameState()
      val ai = new VoteAI(vs,gs)
      val ircState = new IRCState()
      val validator = new VoteValidator(gs)
      val vm = new VoteManager(gs, vs, ai, ircState, validator)
      val ircBot = new IRCBot(vm)
      ircBot.onMessage("None", "A Twitch User", "None", "None", "!c1 > f1, c2 > f2, f1 > e1")
      vs.voterMap("A Twitch User").actionVoteList shouldBe List[ActionVote](
        CardPlayWithFriendlyTarget(1,1),
        CardPlayWithFriendlyTarget(2,2),
        NormalAttack(1,1))}


    "be able to send my votes with multiple messages, so that I can be more intuitive with my commands." in {
      val vs = new VoteState()
      val gs = new GameState()
      val ai = new VoteAI(vs,gs)
      val ircState = new IRCState()
      val validator = new VoteValidator(gs)
      val vm = new VoteManager(gs, vs, ai, ircState, validator)
      val ircBot = new IRCBot(vm)
      ircBot.onMessage("None", "A Twitch User", "None", "None", "!c1 > f1")
      ircBot.onMessage("None", "A Twitch User", "None", "None", "!c2 > f2")
      ircBot.onMessage("None", "A Twitch User", "None", "None", "!f1 > e1")
      vs.voterMap("A Twitch User").actionVoteList shouldBe List[ActionVote](
        CardPlayWithFriendlyTarget(1,1),
        CardPlayWithFriendlyTarget(2,2),
        NormalAttack(1,1))}


    "be able to type the command 'end turn' when I am finished with my commands for that turn, so that I don't have to wait the full length every time." in {
      val vs = new VoteState()
      val gs = new GameState()
      val ai = new VoteAI(vs,gs)
      val ircState = new IRCState()
      val validator = new VoteValidator(gs)
      val vm = new VoteManager(gs, vs, ai, ircState, validator)
      val ircBot = new IRCBot(vm)
      ircBot.onMessage("None", "A Twitch User", "None", "None", "!c1 > f1, end turn")
      vs.voterMap("A Twitch User").actionVoteList shouldBe List[ActionVote](
        CardPlayWithFriendlyTarget(1,1),
        EndTurn())}

    "be able to type the command 'et' when I am finished with my commands for that turn, so that I don't have to wait the full length every time." in {
      val vs = new VoteState()
      val gs = new GameState()
      val ai = new VoteAI(vs,gs)
      val ircState = new IRCState()
      val validator = new VoteValidator(gs)
      val vm = new VoteManager(gs, vs, ai, ircState, validator)
      val ircBot = new IRCBot(vm)
      ircBot.onMessage("None", "A Twitch User", "None", "None", "!c1 > f1, et")
      vs.voterMap("A Twitch User").actionVoteList shouldBe List[ActionVote](
        CardPlayWithFriendlyTarget(1,1),
        EndTurn())}




    "not have to worry about case sensitivity, so that I don't make easy mistakes" in {
      val vs = new VoteState()
      val gs = new GameState()
      val ai = new VoteAI(vs,gs)
      val ircState = new IRCState()
      val validator = new VoteValidator(gs)
      val vm = new VoteManager(gs, vs, ai, ircState, validator)
      val ircBot = new IRCBot(vm)
      ircBot.onMessage("None", "A Twitch User", "None", "None", "!C1 > F1, eNd tUrn")
      vs.voterMap("A Twitch User").actionVoteList shouldBe List[ActionVote](
        CardPlayWithFriendlyTarget(1,1),
        EndTurn())}


    "a visual number that tells me which position the card is at, so that I can be lazy and not count the cards each time." ignore {

      val gs = new GameState()

      val overlay = new Display()
      overlay.display()

      val inputScanner = new Scanner(System.in)
      println("Is there a number 1 being displayed on the screen? Y/N")
      inputScanner.next() shouldBe "Y"
    }


    "have a visual number that tells me how long until the next decision, so that I know my time limit to send the votes." ignore {
      val gs = new GameState()
      val overlay = new Display()
      overlay.displayTimeRemaining()
    }

    "be able to remove a previous vote I sent, so that I can change my mind" in {
      val vs = new VoteState()
      val gs = new GameState()
      val ai = new VoteAI(vs,gs)
      val ircState = new IRCState()
      val validator = new VoteValidator(gs)
      val vm = new VoteManager(gs, vs, ai, ircState, validator)
      val ircBot = new IRCBot(vm)
      ircBot.onMessage("None", "A Twitch User", "None", "None", "!c1, c2")
      vs.voterMap("A Twitch User").actionVoteList shouldBe List[ActionVote](CardPlay(1), CardPlay(2))
      ircBot.onMessage("None", "A Twitch User", "None", "None", "!remove c1")
      vs.voterMap("A Twitch User").actionVoteList shouldBe List[ActionVote](CardPlay(2))
    }


    "be able to remove multiple previous vote I sent in one command, so that I can change my mind easily" in {
      val vs = new VoteState()
      val gs = new GameState()
      val ai = new VoteAI(vs,gs)
      val ircState = new IRCState()
      val validator = new VoteValidator(gs)
      val vm = new VoteManager(gs, vs, ai, ircState, validator)
      val ircBot = new IRCBot(vm)
      ircBot.onMessage("None", "A Twitch User", "None", "None", "!c1, c2, c3")
      vs.voterMap("A Twitch User").actionVoteList shouldBe List[ActionVote](CardPlay(1), CardPlay(2), CardPlay(3))
      ircBot.onMessage("None", "A Twitch User", "None", "None", "!remove c1, c2")
      vs.voterMap("A Twitch User").actionVoteList shouldBe List[ActionVote](CardPlay(3))
    }

    "be able to remove the last command I sent, so that I can change my mind easily" in {
      val vs = new VoteState()
      val gs = new GameState()
      val ai = new VoteAI(vs,gs)
      val ircState = new IRCState()
      val validator = new VoteValidator(gs)
      val vm = new VoteManager(gs, vs, ai, ircState, validator)
      val ircBot = new IRCBot(vm)
      ircBot.onMessage("None", "A Twitch User", "None", "None", "!c1, c2")
      vs.voterMap("A Twitch User").actionVoteList shouldBe List[ActionVote](CardPlay(1), CardPlay(2))
      ircBot.onMessage("None", "A Twitch User", "None", "None", "!remove last")
      vs.voterMap("A Twitch User").actionVoteList shouldBe List[ActionVote](CardPlay(1))
    }


    "be able to remove the all the commands I sent, so that I can change my mind easily" in {
      val vs = new VoteState()
      val gs = new GameState()
      val ai = new VoteAI(vs,gs)
      val ircState = new IRCState()
      val validator = new VoteValidator(gs)
      val vm = new VoteManager(gs, vs, ai, ircState, validator)
      val ircBot = new IRCBot(vm)
      ircBot.onMessage("None", "A Twitch User", "None", "None", "!c1, c2, c3")
      vs.voterMap("A Twitch User").actionVoteList shouldBe List[ActionVote](CardPlay(1), CardPlay(2), CardPlay(3))
      ircBot.onMessage("None", "A Twitch User", "None", "None", "!remove all")
      vs.voterMap("A Twitch User").actionVoteList shouldBe Nil
    }

    "be able to see my vote list, so that I can easily modify my commands" ignore {
      val config = ConfigFactory.load()
      val vs = new VoteState()
      val gs = new GameState()
      val ai = new VoteAI(vs,gs)
      val ircState = new IRCState()
      val validator = new VoteValidator(gs)
      val vm = new VoteManager(gs, vs, ai, ircState, validator)
      val twitchUserBot = new PircBot {
        override def onPrivateMessage(sender: String, login: String, hostname: String, message: String): Unit ={
          message shouldBe "VoteList: c1, , c3,"
        }
        setName("ATwitchUser")
        connect(config.getString("tph.irc.host"))
        joinChannel("#tph")
      }

      val ircBot = new IRCBot(vm)
      ircBot.init()
      Thread.sleep(10000)
      twitchUserBot.sendMessage("#tph", "!c1,c2,c3")
      Thread.sleep(1000)
      vs.voterMap("ATwitchUser").actionVoteList shouldBe List[ActionVote](CardPlay(1), CardPlay(2), CardPlay(3))
      twitchUserBot.sendMessage("#tph", "!votelist")
      Thread.sleep(2000)
    }
  }
}
