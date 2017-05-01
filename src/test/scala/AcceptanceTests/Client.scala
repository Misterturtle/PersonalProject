package AcceptanceTests

import FileReaders.HSDataBase
import GUI.Display
import GameState.{Player, GameState}
import Logic.IRCState
import VoteSystem._
import org.scalatest.tagobjects.Slow
import org.scalatest.{FreeSpec, FlatSpec, Matchers}
import tph.Constants.ActionVotes._
import tph.Constants.Vote
import tph._

import scala.util.Random

/**
  * Created by Harambe on 3/15/2017.
  */
class Client extends FreeSpec with Matchers {


  def getRandomVote(excludedVote: ActionVote = ActionUninit()): ActionVote = {
    def randomIndexExcept(exception: Int): Int = {
      val ri = Random.nextInt(11)
      if (ri == exception)
        randomIndexExcept(exception)
      else
        ri
    }

    def randomVoteList: List[ActionVote] = List[ActionVote](
      Discover(Random.nextInt(3) + 1),
      CardPlayWithFriendlyTargetWithPosition(Random.nextInt(10) + 1, Random.nextInt(11), Random.nextInt(10) + 1),
      CardPlayWithEnemyTargetWithPosition(Random.nextInt(10) + 1, Random.nextInt(11), Random.nextInt(10) + 1),
      CardPlayWithPosition(Random.nextInt(10) + 1, Random.nextInt(10) + 1),
      CardPlay(Random.nextInt(10) + 1),
      CardPlayWithFriendlyTarget(Random.nextInt(10) + 1, Random.nextInt(11)),
      CardPlayWithEnemyTarget(Random.nextInt(10) + 1, Random.nextInt(11)),
      HeroPower(),
      HeroPowerWithFriendlyTarget(Random.nextInt(11)),
      HeroPowerWithEnemyTarget(Random.nextInt(11)),
      NormalAttack(Random.nextInt(11), Random.nextInt(11)))

    excludedVote match {
      case x: Discover =>
        randomVoteList(randomIndexExcept(1))
      case x: CardPlayWithFriendlyTargetWithPosition =>
        randomVoteList(randomIndexExcept(2))
      case x: CardPlayWithEnemyTargetWithPosition =>
        randomVoteList(randomIndexExcept(3))
      case x: CardPlayWithPosition =>
        randomVoteList(randomIndexExcept(4))
      case x: CardPlay =>
        randomVoteList(randomIndexExcept(5))
      case x: CardPlayWithFriendlyTarget =>
        randomVoteList(randomIndexExcept(6))
      case x: CardPlayWithEnemyTarget =>
        randomVoteList(randomIndexExcept(7))
      case x: HeroPower =>
        randomVoteList(randomIndexExcept(8))
      case x: HeroPowerWithFriendlyTarget =>
        randomVoteList(randomIndexExcept(9))
      case x: HeroPowerWithEnemyTarget =>
        randomVoteList(randomIndexExcept(10))
      case x: NormalAttack =>
        randomVoteList(randomIndexExcept(11))
    }
  }

  def mockVoterMap(voterAmount: Int, votesPerVoter: Int, excludedVote: ActionVote = ActionUninit()): Map[String, Voter] = {
    var voterMap: Map[String, Voter] = Map()
    for (a <- 1 until voterAmount) {
      var voteList: List[ActionVote] = Nil
      for (b <- 0 until votesPerVoter) {
        voteList = getRandomVote(excludedVote) :: voteList
      }
      val voter = new Voter(s"Twitch User $a", voteList)
      voterMap = voterMap + (s"Twitch User $a" -> voter)
    }
    voterMap
  }


  //todo As the client, I want to be able to store voter information, so that I can award achievements.
  //todo As the client, I want to give subscribers more voting power, so that I can encourage people to subscribe.
  //todo As the client, I want subscribers to be able to create decks, so that I can encourage people to subscribe.
  //todo As the client, I want subscribers to be able to open packs, so that I can encourage people to subscribe.
  //todo As the client, I want donors to be able to pick a song, so that I can encourage people to donate.
  //todo As the client, I want donors to be able to display a message on the stream, so that I can encourage people to donate.
  //todo As the client, I want to identify invalid votes that are state dependant, so that non-trolls won't be punish just because the order of cards played.
  //todo Record voter history
  //todo Detect invalid votes due to mechanics


  "As the Client, I want" - {

    "users to not have to worry about specifying between a target command and a battlecry command, so that they can be lazy." ignore {

      //val tb = new TheBrain
      val battlecryCard = new Card("Lance Carrier", 1, 1, Constants.INT_UNINIT, 1, "AT_084")
      val nonbattlecryCard = new Card("Wisp", 2, 2, Constants.INT_UNINIT, 1, "CS2_231")
      val friendlyTarget = Constants.TestConstants.createFriendlyBoardCard(1)
      val vs = new VoteState
      val gs = new GameState
      val ai = new VoteAI(vs,gs)

      ai.checkForBattlecryTarget(List((CardPlayWithFriendlyTarget(1,1), (battlecryCard, friendlyTarget)))) shouldBe (CardPlayWithFriendlyTargetWithPosition(1, 1, 1), (battlecryCard, friendlyTarget))
      ai.checkForBattlecryTarget(List((CardPlayWithFriendlyTarget(1,1), (nonbattlecryCard, friendlyTarget)))) shouldBe (CardPlayWithFriendlyTarget(1, 1), (nonbattlecryCard, friendlyTarget))
    }


    "users to not be able to case the same vote, so that people don't abuse the vote system" - {

      "normal scenario" ignore {

      }

      "windfury exception - a 2nd vote is allowed, but no more." ignore {
      }
    }



    "end turn to be executed at the correct time, so that people have time to cast votes." - {

      "prevent premature execution" - {
        "If end turn is spammed before minimum turn time has been reached" ignore {
        }

        "If end turn is spammed before a certain amount of voters have voted" ignore {
        }

        "prevent late execution" - {

          "If enough voters have voted to end the turn" ignore {
          }
        }
      }


      "hurry to be executed at the correct time, so that people have time to cast votes." - {

        val hurryEnabledCard = new Card("Arcane Intellect", 1, 1, Constants.INT_UNINIT, 1, "CS2_023")

        "prevent premature execution" - {
          "If a hurry-enabled card is spammed before minimum turn time has been reached" ignore {
          }

          "If hurryEnableCard is spammed before a certain amount of voters have voted" ignore {
          }
        }

        "prevent late execution" - {

          "If enough voters have voted to play a hurryEnabledCard" ignore {
          }
        }
      }


      "to be able to eliminate voting power for voters that has been declared a troll" in {
        val gs = new GameState()
        val vs = new VoteState()
        val ai = new VoteAI(vs, gs)
        val mockVoteAccuracy = List(.3, .3, .3, .3, .3, .3, .3, .3, .3, .3, .3, .3, .3, .3, .3, .3, .3, .3, .3, .3)
        val validVoteList = List(CardPlay(1))
        val troll = new Voter("Troll", validVoteList, voteAccuracy = mockVoteAccuracy)
        vs.voterMap = Map[String, Voter]("Troll" -> troll)
        vs.voterMap("Troll").getTotalVoteValues shouldBe Map[Vote, Double]()
      }
    }
  }
}


