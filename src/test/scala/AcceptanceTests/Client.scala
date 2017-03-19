package AcceptanceTests

import FileReaders.HSDataBase
import GUI.Overlay
import Logic.{IRCAI, IRCState}
import VoteSystem.{Vote, Voter, VoteManager}
import org.scalatest.tagobjects.Slow
import org.scalatest.{FreeSpec, FlatSpec, Matchers}
import tph.Constants.ActionVotes._
import tph._

import scala.util.Random

/**
  * Created by Harambe on 3/15/2017.
  */
class Client extends FreeSpec with Matchers {

def multipleVotersCastVote(voterAmount:Int, vote: Vote, voteManager: VoteManager): Unit = {
  for(a<-1 to voterAmount){
    val voter = new Voter(s"Twitch User $a", List(vote))
    voteManager.voterMap = voteManager.voterMap + (s"Twitch User $a" -> voter)
  }
}

  def getRandomVote():Vote = {
    def randomVoteList:List[Vote] = List[Vote](
      Discover(Random.nextInt(3)+1),
      CardPlayWithFriendlyTargetWithPosition(Random.nextInt(10)+1, Random.nextInt(11), Random.nextInt(10)+1),
      CardPlayWithEnemyTargetWithPosition(Random.nextInt(10)+1, Random.nextInt(11), Random.nextInt(10)+1),
      CardPlayWithPosition(Random.nextInt(10)+1, Random.nextInt(10)+1),
      CardPlay(Random.nextInt(10)+1),
      CardPlayWithFriendlyTarget(Random.nextInt(10)+1, Random.nextInt(11)),
      CardPlayWithEnemyTarget(Random.nextInt(10)+1, Random.nextInt(11)),
      HeroPower(),
      HeroPowerWithFriendlyTarget(Random.nextInt(11)),
      HeroPowerWithEnemyTarget(Random.nextInt(11)),
      NormalAttack(Random.nextInt(11), Random.nextInt(11)))

    randomVoteList(Random.nextInt(11))
  }

  def mockVoteManagerVoterMap(voterAmount: Int, votesPerVoter: Int, voteManager: VoteManager): VoteManager = {
        for(a<- 1 until voterAmount){
          var voteList: List[Vote] = Nil
          for(b<- 0 until votesPerVoter){
            voteList = getRandomVote() :: voteList
          }
      val voter = new Voter(s"Twitch User $a", voteList)
          voteManager.voterMap = voteManager.voterMap + (s"Twitch User $a" -> voter)
    }
    voteManager
  }

  def mockIRCState(previousVoters: List[List[Voter]] = Nil, isMyTurn: Boolean = false, isHisTurn: Boolean = false, )


  //todo As the client, I do not want hurry to be executed prematurely, so that people have time to cast votes.
  //todo As the client, I do not want hurry to be executed too late, so that people have time to cast more votes after the hurry.
  //todo As the client, I want to be able to identify invalid plays, so that my program doesn't get confused.
  //todo As the client, I want to be able to identify voters that encouraged an invalid play, so that I can detect trolls.
  //todo As the client, I want to be able to identify which users are trolls, so that I can reduce their voting power.
  //todo As the client, I want to be able to identify thoughtful users, so that I can increase their voting power.
  //todo As the client, I want to be able to influence the vote results, so that I can implement AI logic that detects combos and patterns.
  //todo As the client, I want to be able to store voter information, so that I can award achievements.
  //todo As the client, I want to give subscribers more voting power, so that I can encourage people to subscribe.
  //todo As the client, I want subscribers to be able to create decks, so that I can encourage people to subscribe.
  //todo As the client, I want subscribers to be able to open packs, so that I can encourage people to subscribe.
  //todo As the client, I want donors to be able to pick a song, so that I can encourage people to donate.
  //todo As the client, I want donors to be able to display a message on the stream, so that I can encourage people to donate.


  "As the Client, I want" - {

    "users to not have to worry about specifying between a target command and a battlecry command, so that they can be lazy." ignore {

      val battlecryCard = new Card("Lance Carrier", 1, 1, Constants.INT_UNINIT, 1, "AT_084")
      val nonbattlecryCard = new Card("Wisp", 2, 2, Constants.INT_UNINIT, 1, "CS2_231")
      val friendly = new Player(1, hand = List(battlecryCard, nonbattlecryCard), board = List(Constants.TestConstants.createFriendlyBoardCard(1)))
      val gs = new GameState()
      val ircLogic = new IRCState()
      val decisionWithBC = new CardPlayWithFriendlyTarget(1,1)
      val decisionWithoutBC = new CardPlayWithFriendlyTarget(2,1)
      val hsDataBase = new HSDataBase

      ircLogic.isValidDecision(gs, decisionWithBC) shouldBe Some(CardPlayWithFriendlyTargetWithPosition(1,1,1))
      ircLogic.isValidDecision(gs, decisionWithoutBC) shouldBe Some(CardPlayWithFriendlyTarget(1,1))
    }


    "users to not be able to case the same vote, so that people don't abuse the vote system" - {

      "normal scenario" ignore {

        val friendly = new Player(1, hand = List(new Card("Wisp", 1, 1, Constants.INT_UNINIT, 1, "CS2_231")))
        val gs = new GameState()
        gs.friendlyPlayer = friendly
        val vm = new VoteManager(gs)
        val vote = CardPlay(1)
        vm.voteEntry("A Twitch User", vote)
        vm.voteEntry("A Twitch User", vote)

        vm.voterMap("A Twitch User").actionVoteList shouldBe List(CardPlay(1))
      }

      "windfury exception - a 2nd vote is allowed, but no more." ignore {
        val friendly = new Player(1, hand = List(new Card("Young Dragonhawk", 1, 1, Constants.INT_UNINIT, 1, "CS2_169")))
        val gs = new GameState()
        gs.friendlyPlayer = friendly
        val vm = new VoteManager(gs)
        val vote = CardPlay(1)
        vm.voteEntry("A Twitch User", vote)
        vm.voteEntry("A Twitch User", vote)
        vm.voteEntry("A Twitch User", vote)

        vm.voterMap("A Twitch User").actionVoteList shouldBe List(CardPlay(1), CardPlay(1))
      }
    }



    "end turn to be executed at the correct time, so that people have time to cast votes." - {

      "prevent premature execution" - {


        "If end turn is spammed before minimum turn time has been reached" taggedAs Slow ignore {
          var turnEnded = false
          val gs = new GameState()
          val vm = new VoteManager(gs)
          val hs = new HearthStone(gs)
          val mockOverlay = new Overlay(gs){override def displayEndTurnWarning():Unit = {turnEnded = true}}
          val ircAI = new IRCAI(vm, gs)
          val ircState = new IRCState(ircAI, hs, mockOverlay)
          ircState.startTurn()
          Thread.sleep(1000)
          multipleVotersCastVote(100, EndTurn(), vm)
          Thread.sleep(1000)
          turnEnded shouldBe false
        }

        "If end turn is spammed before a certain amount of voters have voted" taggedAs Slow ignore {
          var turnEnded = false
          val gs = new GameState()
          val vm = new VoteManager(gs)
          val hs = new HearthStone(gs)
          val mockOverlay = new Overlay(gs){override def displayEndTurnWarning():Unit = {turnEnded = true}}
          val ircAI = new IRCAI(vm, gs)
          val ircState = new IRCState(ircAI, hs, mockOverlay)
          ircState.startTurn()
          Thread.sleep(1000)
          multipleVotersCastVote(100, EndTurn(), vm)
          Thread.sleep(1000)
          turnEnded shouldBe false


        }


      }

      "prevent late execution" in {


      }





    }




  }
}


