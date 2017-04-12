package AcceptanceTests

import FileReaders.HSDataBase
import Logic.IRCState
import VoteSystem._
import org.scalatest.{FreeSpec, FlatSpec, Matchers}
import tph.Constants.ActionVotes._
import tph._

import scala.util.Random

/**
  * Created by Harambe on 3/15/2017.
  */
class PatternDetector extends FreeSpec with Matchers {


  //todo As the AI, I need to be able to identify "or patterns", because a decision with multiple options only executes one.
  //todo As the AI, I need to be able to give more vote power to votes that are immediately after the previous decision, because order of cards play is important.
  //todo As the AI, I need to be able to identify "enhancement combos", because after you buff a minion you normally want to do something with it.
  //todo As the AI, I need to be able to increase the vote power of the buff card in a detected "enhancement combo", because the buff card should come first.
  //todo As the AI, I need to be able to decrease the vote power of the minion action in a detected "enhancement combo", because you don't want to use the minion before the buff.
  //todo As the AI, I need to be able to identify "heal combos", because order is particularly important for "heal combos".
  //todo As the AI, I need to be able to be very accurate on the number of actions to execute, because you don't always want to play every available option.
  //todo As the AI, I need to be able to identify "charge combos", because you almost never want to ignore charge if available.
  //todo *** As the AI, I need to be able to identify which card will cause future dependencies, so I decide to begin partial execution.
  //todo *** As the AI, I need to be able to give each vote appropriate properties based on the card it is referring to, so that I can be more intelligent.
  //---------Timing---------//
  //todo As the AI, I need to be able to give a warning before preemptively executing a vote, so that slow users can send their commands.
  //todo As the AI, I need to be able to decide that a preemptive decision should not be executed, because slow users should be taken into consideration.
  //todo As the AI, I need to be able to give a warning before executing an "end turn vote series", so that slow users can prevent me from ending the turn if not appropriate.
  //todo As the AI, I need to be able to decide that an "end turn vote series" should not be executed, because slow users should be taken into consideration.
  //todo As the AI, I need to know the maximum amount of time left in a turn, so I don't let time run out.
  //todo As the AI, I need to know roughly how long my execution will take, so that I don't let time run out.
  //todo As the AI, I need to know when the dragon card that reduces time is played, so that I can behave quite differently.


  def multipleVotersCastVote(startVoterNumber: Int, endVoterNumber: Int, messageVote: String, iRCBot: IRCBot): Unit = {
    for (a <- startVoterNumber to endVoterNumber) {
      val name = s"Twitch User $a"
      iRCBot.onMessage("None", s"Twitch User $a", "None", "None", messageVote)
    }
  }

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


  // a -40
  // abf - 15
  // acf - 15
  // adf - 15
  // fae - 15


  //todo As the AI, I need to be able to only give vote power to the second vote if the first was already used, because you almost never use a windfury once.
  "As the pattern detector, I need to be able to" - {
    "Decide the correct vote sequence with these steps." - {

      //      1.) Find the most popular individual vote (e - 172)
      //      2.) Find the most popular next order vote containing the lower order vote (e, f - 86)
      //      3.) If the higher order vote is 50% or more than the lower order vote, repeat steps 2 and 3 (True)
      //      ------2-2.)  Find the most popular next order vote containing the lower order vote (e, f, b - 40)
      //      ------3.2) If the higher order vote is 50% or more than the lower order vote, repeat steps 2 and 3 (False)
      //      **Logically at this point, we have the most popular pattern.
      //      **That pattern should now be considered a unique vote that is different from the parts that make it up (See "Votes with EF:" section)
      //      4.) Out of all votes that contain the deduced pattern, find the most popular individual vote(b - 72)
      //      5.) Out of all the votes that contain the deduced pattern, find the most popular next order vote containing the lower order vote (b, h - 21)
      //      6.) If the higher order vote is 50% or more than the lower order vote, repeat steps 4 and 5 (False)
      //      **Logically at this point, we have the two most popular patterns.
      //        **Now we need to figure out how to combine them, or even if we should.
      //      7.)If the 2nd pattern is 50% or more than the first pattern, continue, else the first pattern is the final vote.
      //      8.)Combine the two patterns based on the most popular offset relative to their starting index's.
      //      9.)Repeat 4-6 with this new deduced vote order
      //      ------4.2) (h-21)
      //      ------5.2) (Nothing)
      //      ------6.2) (False)

      val gs = new GameState()
      val vs = new VoteState()
      val ai = new VoteAI(vs,gs)
      val ircState = new IRCState()
      val validator = new VoteValidator(gs)
      val vm = new VoteManager(gs, vs, ai, ircState, validator)
      val ircBot = new IRCBot(vm)

      multipleVotersCastVote(1, 19, "!c4, c5, f1>e0, f0 > e0", ircBot)
      multipleVotersCastVote(20, 38, "!f1>e0, c4, c5, f0 > e0", ircBot)
      multipleVotersCastVote(39, 55, "!f1> e0, c4, c5", ircBot)
      multipleVotersCastVote(56, 72, "!c4, c5, f1 > e0", ircBot)
      multipleVotersCastVote(73, 82, "!c4, c1, f1>e0", ircBot)
      multipleVotersCastVote(83, 92, "!c4, f1>e0, c1", ircBot)
      multipleVotersCastVote(93, 102, "!f1 > e0, c4, c1", ircBot)
      multipleVotersCastVote(103, 115, "!c1, f1 > e0, c4", ircBot)
      multipleVotersCastVote(116, 128, "!f1 > e0, c1, c4", ircBot)
      multipleVotersCastVote(129, 150, "!c1, c4", ircBot)
      multipleVotersCastVote(151, 159, "!c1, f1 > e0", ircBot)
      multipleVotersCastVote(160, 168, "!f1 > e0, c1", ircBot)
      multipleVotersCastVote(169, 175, "!f1> e1, c4, c5", ircBot)
      multipleVotersCastVote(176, 182, "!f1>e2, c4, c5", ircBot)
      multipleVotersCastVote(183, 184, "!c1", ircBot)
      multipleVotersCastVote(185, 186, "!f1 > e1, c1", ircBot)
      multipleVotersCastVote(187, 188, "!c1, f1> e1", ircBot)
      multipleVotersCastVote(189, 190, "!f1 > e2, c1", ircBot)
      multipleVotersCastVote(191, 192, "!c1, f1 > e2", ircBot)
      multipleVotersCastVote(193, 194, "!f1 > e2, c4, c1", ircBot)
      multipleVotersCastVote(195, 196, "!f1 > e1, c4, c1", ircBot)
      multipleVotersCastVote(197, 198, "!c4, c1", ircBot)
      multipleVotersCastVote(199, 199, "!f1 > e1, c1, c4", ircBot)
      multipleVotersCastVote(200, 200, "!c1, f1 > e2, c4", ircBot)

/*
Contains c4,c5:
After f1>e0:

f0>e0 = 19

Before f1>e0:



-c4, c5, f1>e0, f0>e0 (19)
-f1>e0, c4, c5, f0>e0  (19)
-f1>e0, c4, c5 (17)
-c4, c5, f1>e0 (17)
-c4, c1, f1>e0 (10)
-c4, f1>e0, c1 (10)
-f1>e0, c4, c1 (10)
-c1, f1>e0, c4 (13)
-f1>e0, c1, c4 (13)
-c1, c4 (22)
-c1, f1>e0 (9)
-f1>e0, c1 (9)
-f1>e1, c4, c5 (7)
-f1>e2, c4, c5 (7)
-c1 (2)
- f1>e1, c1 (2)
-c1, f1>e1,(2)
-f1>e2, c1 (2)
-c1, f1>e2 (2)
-f1>e2, c4, c1 (2)
-f1>e1, c4, c1 (2)
-c4, c1 (2)
-f1>e1, c1, c4 (1)
-c1, f1>e2, c4 (1)
       */






      "Step 1: Find the most popular individual vote" in {

        /*
      c1 = 10 + 10 + 10 + 13 + 13 + 22 + 9 + 9 + 2 + 2 + 2 + 2 + 2 + 2 + 2 + 2 + 1 + 1 = 114
      c4 = 19 + 19 + 17 + 17 + 10 + 10 + 10 + 13 + 13 + 22 + 7 + 7 + 2 + 2 + 2 + 1 + 1 = 172
      c5 = 19 + 19 + 17 + 17 + 7 + 7 = 86
      f1>e0 = 19 + 19 + 17 + 17 + 10 + 10 + 10 + 13 + 13 + 9 + 9 = 146
      f1>e1 = 7  + 2 + 2 + 2 + 1 = 14
      f1>e2 = 7 + 2 + 2 + 2 + 1 = 14
      f0>e0 = 19 + 19 = 38


       */

        ai.findIndividualVotes(Pattern(Nil)) shouldBe List(
          (CardPlay(4), 172.0),
          (NormalAttack(0,0), 38.0),
          (CardPlay(1), 114.0),
          (NormalAttack(1,1), 14.0),
          (CardPlay(5), 86.0),
          (NormalAttack(1,2), 14.0),
          (NormalAttack(1,0), 146.0)
        )

        ai.findHighestValue(ai.findIndividualVotes(Pattern(Nil))) shouldBe CardPlay(4)
      }

      "Step 2: Find the most popular next order vote containing the lower order vote" in{



        //makes a base pattern from the most popular individual vote
        val mostPopularFirstOrderVote = ai.findHighestValue(ai.findIndividualVotes(Pattern(Nil)))
        val firstOrderPattern = Pattern(List((mostPopularFirstOrderVote, 0)))
        ai.findNextOrder(firstOrderPattern, Pattern(Nil)) shouldBe Pattern(List((CardPlay(4), 0), (CardPlay(5), 1)))



      }


      "Step 3: If the higher order vote is 50% or more than the lower order vote, repeat steps 2 and 3 (True)" in {

        ai.buildPattern(Pattern(Nil)) shouldBe Pattern(List((CardPlay(4), 0), (CardPlay(5), 1)))
      }


      "Step 4: Out of all votes that contain the deduced pattern, find the most popular individual vote" in {

        val firstPattern = ai.buildPattern(Pattern(Nil))
        ai.findHighestValue(ai.findIndividualVotes(firstPattern)) shouldBe NormalAttack(1,0)
      }

      "Step 5: Out of all the votes that contain the deduced pattern, find the most popular next order vote containing the lower order vote" in {

        val firstPattern = ai.buildPattern(Pattern(Nil))
        val firstOrderPattern = Pattern(List((ai.findHighestValue(ai.findIndividualVotes(firstPattern)),0)))
        ai.findNextOrder(firstOrderPattern, firstPattern) shouldBe Pattern(List((NormalAttack(1,0),0), (NormalAttack(0,0),1)))
      }

      "Step 6: If the higher order vote is 50% or more than the lower order vote, repeat steps 4 and 5" in {

        ai.buildPattern(Pattern(List((CardPlay(4), 0), (CardPlay(5), 1)))) shouldBe Pattern(List((NormalAttack(1,0),0)))
      }

      "Step 7: If the 2nd pattern is 50% or more than the first pattern, continue, else the first pattern is the final vote." in {

        val decision = ai.buildDecision()
        decision shouldBe Pattern(List((CardPlay(4), 0), (CardPlay(5), 1), (NormalAttack(1,0), 2), (NormalAttack(0,0), 3)))
      }


      "Step 8: Find the most popular position of the 2nd pattern compared to the first" in {

        val firstPattern = Pattern(List((CardPlay(4), 0), (CardPlay(5), 1)))
        val secondPattern = Pattern(List((NormalAttack(1,0), 0)))
        ai.combinePatterns(firstPattern, secondPattern) shouldBe Pattern(List((CardPlay(4), 0), (CardPlay(5), 1), (NormalAttack(1,0),2)))
      }

      "IT IS FINISHED! COMPLETE TEST:" in {
        ai.buildDecision() shouldBe Pattern(List((CardPlay(4), 0), (CardPlay(5), 1), (NormalAttack(1,0),2), (NormalAttack(0,0),3)))

      }





    }
  }
}
