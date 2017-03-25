package AcceptanceTests

import FileReaders.HSDataBase
import Logic.{IRCAI, IRCPatternDetector}
import VoteSystem.{ActionVote, Vote, Voter, VoteManager}
import org.scalatest.{FreeSpec, FlatSpec, Matchers}
import tph.Constants.ActionVotes._
import tph.{Constants, Card, Player, GameState}

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


  def multipleVotersCastVote(startVoterNumber: Int, endVoterNumber: Int, vote: ActionVote, voteManager: VoteManager): Unit = {
    for (a <- startVoterNumber to endVoterNumber) {
      val name = s"Twitch User $a"
      voteManager.voteEntry(name, vote)
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


  //todo As the AI, I need to be able to only give vote power to the second vote if the first was already used, because you almost never use a windfury once.
  "As the pattern detector, I need to be able to" - {

    "identify windfury patterns, because you almost never use a windfury minion once" - {

      "windfury detected when previous decision minion source is not the windfury minion" in {


        val windfuryMinion = new Card("Young Dragonhawk", 1, Constants.INT_UNINIT, 1, 1, "CS2_169")
        val wisp = new Card("Wisp", 21, Constants.INT_UNINIT, 1, 2, "CS2_231")
        val friendly = new Player(1, board = List(windfuryMinion))
        val enemy = new Player(2, board = List(wisp))
        val gs = new GameState()
        gs.friendlyPlayer = friendly
        gs.enemyPlayer = enemy
        val vm = new VoteManager(gs)
        val hsDataBase = new HSDataBase()
        val pd = new IRCPatternDetector(gs, vm, hsDataBase)
        val voter1 = new Voter("Twitch User 1", List(NormalAttack(1, 1), NormalAttack(1, 1)))
        val voter2 = new Voter("Twitch User 2", List(NormalAttack(1, 1), NormalAttack(1, 1)))
        val voter3 = new Voter("Twitch User 3", List(NormalAttack(1, 1), NormalAttack(1, 1)))
        val voter4 = new Voter("Twitch User 4", List(NormalAttack(1, 1), NormalAttack(1, 1)))
        val voter5 = new Voter("Twitch User 5", List(NormalAttack(1, 1), NormalAttack(1, 1)))
        //pd.detectWindfuryPatterns()


        vm.voterMap = Map()


      }
    }


    "Decide the correct votes in these scenarios" - {

      "Scenario 1: Simple single vote" in {
        val gs = new GameState()
        gs.friendlyPlayer = new Player(1, hand = List(Card("Friendly Card1", 1, 1, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT)))
        val vm = new VoteManager(gs)
        val ai = new IRCAI(vm, gs)

        //100 votes total
        //c1
        multipleVotersCastVote(1, 100, CardPlay(1), vm)
        ai.makeDecision() shouldBe List(CardPlay(1))
      }






      "Scenario 2: Simple Order" in {
        val gs = new GameState()
        val friendlyHand = List(Card("Friendly Card1", 1, 1, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT))
        val friendlyBoard = List(Card("Friendly Minion1", 11, Constants.INT_UNINIT, 1, 1, Constants.STRING_UNINIT))
        val friendly = new Player(1, hand = friendlyHand, board = friendlyBoard)
        gs.friendlyPlayer = friendly
        val enemyBoard = List(Card("Enemy Minion 1", 31, Constants.INT_UNINIT, 1, 2, Constants.STRING_UNINIT), Card("Enemy Minion 2", 32, Constants.INT_UNINIT, 2, 2, Constants.STRING_UNINIT))
        val enemy = new Player(2, board = enemyBoard)
        gs.enemyPlayer = enemy
        val vm = new VoteManager(gs)
        val ai = new IRCAI(vm, gs)

        //100 votes total
        // 60 - f1 > e1, c1 > f1
        // 40 - f1 > e2, c1 > f1

        multipleVotersCastVote(1, 60, NormalAttack(1, 1), vm)
        multipleVotersCastVote(1, 60, CardPlayWithFriendlyTarget(1, 1), vm)
        multipleVotersCastVote(61, 100, NormalAttack(1, 2), vm)
        multipleVotersCastVote(61, 100, CardPlayWithFriendlyTarget(1, 1), vm)

        ai.makeDecision() shouldBe List(NormalAttack(1, 1), CardPlayWithFriendlyTarget(1, 1))
      }


      "Scenario 3: Simple mana pattern" in {
        val gs = new GameState()
        val friendlyHand = List(
          Card("Emperor Thaurissan", 1, 1, Constants.INT_UNINIT, 1, "BRM_028"),
          Card("Eaglehorn Bow", 2, 2, Constants.INT_UNINIT, 1, "EX1_536"),
          Card("Eaglehorn Bow", 3, 3, Constants.INT_UNINIT, 1, "EX1_536"))
        val friendly = new Player(1, hand = friendlyHand)
        gs.friendlyPlayer = friendly
        val vm = new VoteManager(gs)
        val ai = new IRCAI(vm, gs)
/*

        Votes:
        40 - c1
        25 - c2, c3
        35 - c3, c2

        Decision Explanation:
        c1 has the highest amount of votes
        however, more people think that 5 mana is better spent on c2, c3 OR on c3,c2

         */
        multipleVotersCastVote(1, 40, CardPlay(1), vm)
        multipleVotersCastVote(41, 65, CardPlay(2), vm)
        multipleVotersCastVote(41, 65, CardPlay(3), vm)
        multipleVotersCastVote(66, 100, CardPlay(3), vm)
        multipleVotersCastVote(66, 100, CardPlay(2), vm)

        ai.makeDecision() shouldBe List(CardPlay(3), CardPlay(2))


      }






    }
  }


}
