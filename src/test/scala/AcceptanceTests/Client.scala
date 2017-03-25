package AcceptanceTests

import FileReaders.HSDataBase
import GUI.Overlay
import Logic.{IRCAI, IRCState}
import VoteSystem.{ActionVote, Vote, Voter, VoteManager}
import org.scalatest.tagobjects.Slow
import org.scalatest.{FreeSpec, FlatSpec, Matchers}
import tph.Constants.ActionVotes._
import tph._

import scala.util.Random

/**
  * Created by Harambe on 3/15/2017.
  */
class Client extends FreeSpec with Matchers {

  def multipleVotersCastVote(voterAmount: Int, vote: ActionVote, voteManager: VoteManager): Unit = {
    for (a <- 1 to voterAmount) {
      val voter = new Voter(s"Twitch User $a", List(vote))
      voteManager.voterMap = voteManager.voterMap + (s"Twitch User $a" -> voter)
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




  //todo As the client, I want to be able to store voter information, so that I can award achievements.
  //todo As the client, I want to give subscribers more voting power, so that I can encourage people to subscribe.
  //todo As the client, I want subscribers to be able to create decks, so that I can encourage people to subscribe.
  //todo As the client, I want subscribers to be able to open packs, so that I can encourage people to subscribe.
  //todo As the client, I want donors to be able to pick a song, so that I can encourage people to donate.
  //todo As the client, I want donors to be able to display a message on the stream, so that I can encourage people to donate.
  //todo As the client, I want to identify invalid votes that are state dependant, so that non-trolls won't be punish just because the order of cards played.


  "As the Client, I want" - {

    "users to not have to worry about specifying between a target command and a battlecry command, so that they can be lazy." ignore {

      val battlecryCard = new Card("Lance Carrier", 1, 1, Constants.INT_UNINIT, 1, "AT_084")
      val nonbattlecryCard = new Card("Wisp", 2, 2, Constants.INT_UNINIT, 1, "CS2_231")
      val friendly = new Player(1, hand = List(battlecryCard, nonbattlecryCard), board = List(Constants.TestConstants.createFriendlyBoardCard(1)))
      val gs = new GameState()
      val ircAI = new IRCAI(new VoteManager(gs), gs)
      val ircLogic = new IRCState(ircAI, new HearthStone(gs), new Overlay(gs))
      val decisionWithBC = new CardPlayWithFriendlyTarget(1, 1)
      val decisionWithoutBC = new CardPlayWithFriendlyTarget(2, 1)
      val hsDataBase = new HSDataBase

      ircLogic.isValidDecision(gs, decisionWithBC) shouldBe Some(CardPlayWithFriendlyTargetWithPosition(1, 1, 1))
      ircLogic.isValidDecision(gs, decisionWithoutBC) shouldBe Some(CardPlayWithFriendlyTarget(1, 1))
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


        "If end turn is spammed before minimum turn time has been reached" ignore {
          var turnEnded = false
          val gs = new GameState()
          val vm = new VoteManager(gs)
          val hs = new HearthStone(gs)
          val overlay = new Overlay(gs)
          val ircAI = new IRCAI(vm, gs)
          val ircState = new IRCState(ircAI, hs, overlay) {
            override def endTurnWarning: Unit = {
              turnEnded = true
            }
          }
          ircState.startTurn()
          Thread.sleep(1000)
          multipleVotersCastVote(100, EndTurn(), vm)
          Thread.sleep(1000)
          ircState.currentTurnClock < Constants.IRCState.minimumDelayBeforeExecution shouldBe true
          turnEnded shouldBe false
        }

        "If end turn is spammed before a certain amount of voters have voted" ignore {
          import Constants.InfluenceFactors
          var turnEnded = false
          val gs = new GameState()
          var previousVoterList = List[Map[String, Voter]]()
          for (a <- 0 to InfluenceFactors.maximumVoterHistory) {
            previousVoterList = mockVoterMap(100, 4) :: previousVoterList
          }
          val vm = new VoteManager(gs) {
            voterHistory = previousVoterList
          }
          val hs = new HearthStone(gs)
          val overlay = new Overlay(gs) {}
          val ircAI = new IRCAI(vm, gs)
          val ircState = new IRCState(ircAI, hs, overlay) {
            myTurn = true

            override def endTurnWarning: Unit = {
              turnEnded = true
            }

            currentTurnClock = 5
          }

          val notEnoughVoters = (vm.averageVotersFromHistory * InfluenceFactors.substantialVoterFactor).toInt - 1
          multipleVotersCastVote(notEnoughVoters, EndTurn(), vm)
          Thread.sleep(1000)
          //In this test, averageVoterHistory is 100
          notEnoughVoters < 100 * InfluenceFactors.substantialVoterFactor shouldBe true
          turnEnded shouldBe false
        }
      }

      "prevent late execution" - {

        "If enough voters have voted to end the turn" ignore {
          import Constants.InfluenceFactors
          var turnEnded = false
          val gs = new GameState()
          var previousVoterList = List[Map[String, Voter]]()
          for (a <- 0 to InfluenceFactors.maximumVoterHistory) {
            previousVoterList = mockVoterMap(100, 4) :: previousVoterList
          }
          val vm = new VoteManager(gs) {
            voterHistory = previousVoterList
          }
          val hs = new HearthStone(gs)
          val overlay = new Overlay(gs) {}
          val ircAI = new IRCAI(vm, gs)
          val ircState = new IRCState(ircAI, hs, overlay) {
            override def endTurnWarning: Unit = {
              turnEnded = true
            }

            myTurn = true
            currentTurnClock = 5
          }

          val enoughVoters = (vm.averageVotersFromHistory * InfluenceFactors.substantialVoterFactor).toInt
          Thread.sleep(1000)
          multipleVotersCastVote(enoughVoters, EndTurn(), vm)
          Thread.sleep(1000)
          //In this test, averageVoterHistory is 100
          (enoughVoters >= 100 * InfluenceFactors.substantialVoterFactor) shouldBe true
          turnEnded shouldBe true
        }
      }
    }


    "hurry to be executed at the correct time, so that people have time to cast votes." - {

      val hurryEnabledCard = new Card("Arcane Intellect", 1, 1, Constants.INT_UNINIT, 1, "CS2_023")

      "prevent premature execution" - {
        "If a hurry-enabled card is spammed before minimum turn time has been reached" ignore {
          var hurryExecuted = false
          val gs = new GameState()
          gs.friendlyPlayer = new Player(1, hand = List(hurryEnabledCard))
          val vm = new VoteManager(gs)
          val hs = new HearthStone(gs)
          val overlay = new Overlay(gs)
          val ircAI = new IRCAI(vm, gs)
          val ircState = new IRCState(ircAI, hs, overlay) {
            override def hurryExecutionWarning: Unit = {
              hurryExecuted = true
            }
          }
          ircState.startTurn()
          Thread.sleep(1000)
          multipleVotersCastVote(100, CardPlay(1), vm)
          Thread.sleep(1000)
          ircState.currentTurnClock < Constants.IRCState.minimumDelayBeforeExecution shouldBe true
          hurryExecuted shouldBe false
        }

        "If hurryEnableCard is spammed before a certain amount of voters have voted" ignore {
          import Constants.InfluenceFactors
          var hurryExecuted = false
          val gs = new GameState()
          gs.friendlyPlayer = new Player(1, hand = List(hurryEnabledCard))
          var previousVoterList = List[Map[String, Voter]]()
          for (a <- 0 to InfluenceFactors.maximumVoterHistory) {
            previousVoterList = mockVoterMap(100, 4, CardPlay(1)) :: previousVoterList
          }
          val vm = new VoteManager(gs) {
            voterHistory = previousVoterList
          }
          val hs = new HearthStone(gs)
          val overlay = new Overlay(gs) {}
          val ircAI = new IRCAI(vm, gs)
          val ircState = new IRCState(ircAI, hs, overlay) {
            myTurn = true

            override def endTurnWarning: Unit = {
              hurryExecuted = true
            }

            currentTurnClock = 5
          }

          val notEnoughVoters = (vm.averageVotersFromHistory * InfluenceFactors.substantialVoterFactor).toInt - 1
          multipleVotersCastVote(notEnoughVoters, CardPlay(1), vm)
          Thread.sleep(1000)
          //In this test, averageVoterHistory is 100
          notEnoughVoters < 100 * InfluenceFactors.substantialVoterFactor shouldBe true
          hurryExecuted shouldBe false
        }
      }

      "prevent late execution" in {

        "If enough voters have voted to play a hurryEnabledCard" ignore {
          import Constants.InfluenceFactors
          var hurryExecuted = false
          val gs = new GameState()
          gs.friendlyPlayer = new Player(1, hand = List(hurryEnabledCard))
          var previousVoterList = List[Map[String, Voter]]()
          for (a <- 0 to InfluenceFactors.maximumVoterHistory) {
            previousVoterList = mockVoterMap(100, 4, CardPlay(1)) :: previousVoterList
          }
          val vm = new VoteManager(gs) {
            voterHistory = previousVoterList
          }
          val hs = new HearthStone(gs)
          val overlay = new Overlay(gs)
          val ircAI = new IRCAI(vm, gs)
          val ircState = new IRCState(ircAI, hs, overlay) {
            override def hurryExecutionWarning: Unit = {
              hurryExecuted = true
            }

             myTurn = true
             currentTurnClock = 5
          }

          val enoughVoters = (vm.averageVotersFromHistory * InfluenceFactors.substantialVoterFactor).toInt
          Thread.sleep(1000)
          multipleVotersCastVote(enoughVoters, CardPlay(1), vm)
          Thread.sleep(1000)
          //In this test, averageVoterHistory is 100
          (enoughVoters >= 100 * InfluenceFactors.substantialVoterFactor) shouldBe true
          hurryExecuted shouldBe true
        }
      }
    }

    "my program to detect invalid plays, so my program doesn't get confused" - {


      "REQ_TARGET_MIN_ATTACK - Shadow Word: Death" - {

        val card = new Card("Shadow Word: Death", 1, 1, Constants.INT_UNINIT, 1, "EX1_622")
        val wispCard = new Card("Wisp", 11, Constants.INT_UNINIT, 1, 1, "CS2_231")
        val psgCard = new Card("Piloted Sky Golem", 12, Constants.INT_UNINIT, 2, 1, "GVG_105")

        "valid" in {
          val gs = new GameState()
          val friendly = new Player(1, hand = List(card), board = List(wispCard, psgCard))
          gs.friendlyPlayer = friendly
          val voter = new Voter("A Twitch User").voteEntry(CardPlayWithFriendlyTarget(1, 2))
          voter.actionVoteList shouldBe List(CardPlayWithFriendlyTarget(1, 2))
          voter.invalidVoteList shouldBe Nil
        }

        "invalid" in {
          val gs = new GameState()
          val friendly = new Player(1, hand = List(card), board = List(wispCard, psgCard))
          gs.friendlyPlayer = friendly
          val voter = new Voter("A Twitch User").voteEntry(CardPlayWithFriendlyTarget(1, 1))
          voter.actionVoteList shouldBe Nil
          voter.invalidVoteList shouldBe List(CardPlayWithFriendlyTarget(1, 1))
        }
      }

      "REQ_LEGENDARY_TARGET - Rend Blackhand" - {

        val card = new Card("Rend Blackhand", 1, 1, Constants.INT_UNINIT, 1, "BRM_029")
        val wispCard = new Card("Wisp", 11, Constants.INT_UNINIT, 1, 1, "CS2_231")
        val chillCard = new Card("Chillmaw", 12, Constants.INT_UNINIT, 2, 1, "AT_123")
        val dragonCard = new Card("Hungry Dragon", 2, 2, Constants.INT_UNINIT, 1, "BRM_026")

        "valid" in {
          val gs = new GameState()
          val friendly = new Player(1, hand = List(card, dragonCard), board = List(wispCard, chillCard))
          gs.friendlyPlayer = friendly
          val voter = new Voter("A Twitch User").voteEntry(CardPlayWithFriendlyTarget(1, 2))
          voter.actionVoteList shouldBe List(CardPlayWithFriendlyTarget(1, 2))
          voter.invalidVoteList shouldBe Nil
        }

        "invalid" in {
          val gs = new GameState()
          val friendly = new Player(1, hand = List(card, dragonCard), board = List(wispCard, chillCard))
          gs.friendlyPlayer = friendly
          val voter = new Voter("A Twitch User").voteEntry(CardPlayWithFriendlyTarget(1, 1))
          voter.actionVoteList shouldBe Nil
          voter.invalidVoteList shouldBe List(CardPlayWithFriendlyTarget(1, 1))
        }
      }

      "REQ_TARGET_IF_AVAILABLE_AND_MINIMUM_FRIENDLY_SECRETS - Medivh's Valet" - {

        val card = new Card("Medivh's Valet", 1, 1, Constants.INT_UNINIT, 1, "KAR_092")
        val wispCard = new Card("Wisp", 11, Constants.INT_UNINIT, 1, 1, "CS2_231")


        "valid" in {
          val gs = new GameState()
          val friendly = new Player(1, hand = List(card), board = List(wispCard), secretsInPlay = 1)
          gs.friendlyPlayer = friendly
          val voter = new Voter("A Twitch User").voteEntry(CardPlayWithFriendlyTarget(1, 1))
          voter.actionVoteList shouldBe List(CardPlayWithFriendlyTarget(1, 1))
          voter.invalidVoteList shouldBe Nil
        }

        "invalid" in {
          val gs = new GameState()
          val friendly = new Player(1, hand = List(card), board = List(wispCard), secretsInPlay = 0)
          gs.friendlyPlayer = friendly
          val voter = new Voter("A Twitch User").voteEntry(CardPlayWithFriendlyTarget(1, 1))
          voter.actionVoteList shouldBe Nil
          voter.invalidVoteList shouldBe List(CardPlayWithFriendlyTarget(1, 1))
        }
      }


      "REQ_TARGET_IF_AVAILABLE_AND_DRAGON_IN_HAND - Blackwing Corruptor" - {

        val card = new Card("Blackwing Corruptor", 1, 1, Constants.INT_UNINIT, 1, "BRM_034")
        val wispCard = new Card("Wisp", 11, Constants.INT_UNINIT, 1, 1, "CS2_231")
        val dragonCard = new Card("Hungry Dragon", 2, 2, Constants.INT_UNINIT, 1, "BRM_026")


        "valid" in {
          val gs = new GameState()
          val friendly = new Player(1, hand = List(card, dragonCard), board = List(wispCard))
          gs.friendlyPlayer = friendly
          val voter = new Voter("A Twitch User").voteEntry(CardPlayWithFriendlyTarget(1, 1))
          voter.actionVoteList shouldBe List(CardPlayWithFriendlyTarget(1, 1))
          voter.invalidVoteList shouldBe Nil
        }

        "invalid" in {
          val gs = new GameState()
          val friendly = new Player(1, hand = List(card), board = List(wispCard))
          gs.friendlyPlayer = friendly
          val voter = new Voter("A Twitch User").voteEntry(CardPlayWithFriendlyTarget(1, 1))
          voter.actionVoteList shouldBe Nil
          voter.invalidVoteList shouldBe List(CardPlayWithFriendlyTarget(1, 1))
        }
      }

      "REQ_STEALTHED_TARGET - Shadow Sensei" - {

        val card = new Card("Shadow Sensei", 1, 1, Constants.INT_UNINIT, 1, "CFM_694")
        val wispCard = new Card("Wisp", 11, Constants.INT_UNINIT, 1, 1, "CS2_231")
        val stealthedCard = new Card("Mini-Mage", 12, Constants.INT_UNINIT, 2, 1, "GVG_109")

        "valid" in {
          val gs = new GameState()
          val friendly = new Player(1, hand = List(card), board = List(wispCard, stealthedCard))
          gs.friendlyPlayer = friendly
          val voter = new Voter("A Twitch User").voteEntry(CardPlayWithFriendlyTarget(1, 2))
          voter.actionVoteList shouldBe List(CardPlayWithFriendlyTarget(1, 2))
          voter.invalidVoteList shouldBe Nil
        }

        "invalid" in {
          val gs = new GameState()
          val friendly = new Player(1, hand = List(card), board = List(wispCard))
          gs.friendlyPlayer = friendly
          val voter = new Voter("A Twitch User").voteEntry(CardPlayWithFriendlyTarget(1, 1))
          voter.actionVoteList shouldBe Nil
          voter.invalidVoteList shouldBe List(CardPlayWithFriendlyTarget(1, 1))
        }
      }

      "REQ_DAMAGED_TARGET - Execute" - {

        val card = new Card("Execute", 1, 1, Constants.INT_UNINIT, 1, "CS2_108")
        val damagedCard = new Card("Hungry Dragon", 32, 2, Constants.INT_UNINIT, 2, "BRM_026", isDamaged = true)
        val nondamagedCard = new Card("Hungry Dragon", 32, 2, Constants.INT_UNINIT, 2, "BRM_026")

        "valid" in {
          val gs = new GameState()
          val friendly = new Player(1, hand = List(card))
          val enemy = new Player(2, board = List(damagedCard))
          gs.friendlyPlayer = friendly
          gs.enemyPlayer = enemy
          val voter = new Voter("A Twitch User").voteEntry(CardPlayWithEnemyTarget(1, 1))
          voter.actionVoteList shouldBe List(CardPlayWithEnemyTarget(1, 1))
          voter.invalidVoteList shouldBe Nil
        }

        "invalid" in {
          val gs = new GameState()
          val friendly = new Player(1, hand = List(card), board = List(nondamagedCard))
          val enemy = new Player(2, board = List(nondamagedCard))
          gs.friendlyPlayer = friendly
          gs.enemyPlayer = enemy
          val voter = new Voter("A Twitch User").voteEntry(CardPlayWithFriendlyTarget(1, 1))
          voter.actionVoteList shouldBe Nil
          voter.invalidVoteList shouldBe List(CardPlayWithFriendlyTarget(1, 1))
        }
      }


      "REQ_FROZEN_TARGET - Shatter" - {

        val card = new Card("Shatter", 1, 1, Constants.INT_UNINIT, 1, "OG_081")
        val frozenCard = new Card("Wisp", 11, Constants.INT_UNINIT, 1, 1, "CS2_231", isFrozen = true)
        val nonfrozenCard = new Card("Wisp", 11, Constants.INT_UNINIT, 1, 1, "CS2_231")

        "valid" in {
          val gs = new GameState()
          val friendly = new Player(1, hand = List(card), board = List(frozenCard))
          gs.friendlyPlayer = friendly
          val voter = new Voter("A Twitch User").voteEntry(CardPlayWithFriendlyTarget(1, 1))
          voter.actionVoteList shouldBe List(CardPlayWithFriendlyTarget(1, 1))
          voter.invalidVoteList shouldBe Nil
        }

        "invalid" in {
          val gs = new GameState()
          val friendly = new Player(1, hand = List(card), board = List(nonfrozenCard))
          gs.friendlyPlayer = friendly
          val voter = new Voter("A Twitch User").voteEntry(CardPlayWithFriendlyTarget(1, 1))
          voter.actionVoteList shouldBe Nil
          voter.invalidVoteList shouldBe List(CardPlayWithFriendlyTarget(1, 1))
        }
      }

      "REQ_UNDAMAGED_TARGET - Shadow Strike" - {

        val card = new Card("Shadow Strike", 1, 1, Constants.INT_UNINIT, 1, "OG_176")
        val damagedCard = new Card("Hungry Dragon", 32, 2, Constants.INT_UNINIT, 2, "BRM_026", isDamaged = true)
        val nondamagedCard = new Card("Hungry Dragon", 32, 2, Constants.INT_UNINIT, 2, "BRM_026")

        "valid" in {
          val gs = new GameState()
          val friendly = new Player(1, hand = List(card))
          val enemy = new Player(2, board = List(nondamagedCard))
          gs.friendlyPlayer = friendly
          gs.enemyPlayer = enemy
          val voter = new Voter("A Twitch User").voteEntry(CardPlayWithEnemyTarget(1, 1))
          voter.actionVoteList shouldBe List(CardPlayWithEnemyTarget(1, 1))
          voter.invalidVoteList shouldBe Nil
        }

        "invalid" in {
          val gs = new GameState()
          val friendly = new Player(1, hand = List(card), board = List(nondamagedCard))
          val enemy = new Player(2, board = List(damagedCard))
          gs.friendlyPlayer = friendly
          gs.enemyPlayer = enemy
          val voter = new Voter("A Twitch User").voteEntry(CardPlayWithFriendlyTarget(1, 1))
          voter.actionVoteList shouldBe Nil
          voter.invalidVoteList shouldBe List(CardPlayWithFriendlyTarget(1, 1))
        }
      }

      "REQ_WEAPON_EQUIPPED - Blade Flurry" - {

        val card = new Card("Blade Flurry", 1, 1, Constants.INT_UNINIT, 1, "CS2_233")

        "valid" in {
          val gs = new GameState()
          val friendly = new Player(1, hand = List(card), isWeaponEquipped = true)
          gs.friendlyPlayer = friendly
          val voter = new Voter("A Twitch User").voteEntry(CardPlay(1))
          voter.actionVoteList shouldBe List(CardPlay(1))
          voter.invalidVoteList shouldBe Nil
        }

        "invalid" in {
          val gs = new GameState()
          val friendly = new Player(1, hand = List(card))
          gs.friendlyPlayer = friendly
          val voter = new Voter("A Twitch User").voteEntry(CardPlay(1))
          voter.actionVoteList shouldBe Nil
          voter.invalidVoteList shouldBe List(CardPlay(1))
        }
      }

      "REQ_TARGET_FOR_COMBO - SI:7 Agent" - {

        val card = new Card("SI:7 Agent", 1, 1, Constants.INT_UNINIT, 1, "EX1_134")
        val wispCard = new Card("Wisp", 11, Constants.INT_UNINIT, 1, 1, "CS2_231")

        "valid" in {
          val gs = new GameState()
          val friendly = new Player(1, hand = List(card), board = List(wispCard), isComboActive = true)
          gs.friendlyPlayer = friendly
          val voter = new Voter("A Twitch User").voteEntry(CardPlayWithFriendlyTarget(1, 1))
          voter.actionVoteList shouldBe List(CardPlayWithFriendlyTarget(1, 1))
          voter.invalidVoteList shouldBe Nil
        }

        "invalid" in {
          val gs = new GameState()
          val friendly = new Player(1, hand = List(card), board = List(wispCard))
          gs.friendlyPlayer = friendly
          val voter = new Voter("A Twitch User").voteEntry(CardPlayWithFriendlyTarget(1, 1))
          voter.actionVoteList shouldBe Nil
          voter.invalidVoteList shouldBe List(CardPlayWithFriendlyTarget(1, 1))
        }
      }


      "REQ_NONSELF_TARGET - Cruel Taskmaster" - {

        val card = new Card("Cruel Taskmaster", 1, 1, Constants.INT_UNINIT, 1, "EX1_603")
        val wispCard = new Card("Wisp", 11, Constants.INT_UNINIT, 1, 1, "CS2_231")

        "valid" in {
          val gs = new GameState()
          val friendly = new Player(1, hand = List(card), board = List(wispCard))
          gs.friendlyPlayer = friendly
          val voter = new Voter("A Twitch User").voteEntry(CardPlayWithFriendlyTarget(1, 1))
          voter.actionVoteList shouldBe List(CardPlayWithFriendlyTarget(1, 1))
          voter.invalidVoteList shouldBe Nil
        }

        "invalid" in {
          val gs = new GameState()
          val friendly = new Player(1, hand = List(card), board = List(wispCard))
          gs.friendlyPlayer = friendly
          val voter = new Voter("A Twitch User").voteEntry(CardPlayWithFriendlyTarget(1, 0))
          voter.actionVoteList shouldBe Nil
          voter.invalidVoteList shouldBe List(CardPlayWithFriendlyTarget(1, 0))
        }
      }

      "REQ_MUST_TARGET_TAUNTER - The Black Knight" - {

        val card = new Card("The Black Knight", 1, 1, Constants.INT_UNINIT, 1, "EX1_002")
        val tauntCard = new Card("Lil' Exorcist", 31, Constants.INT_UNINIT, 1, 2, "GVG_097")
        val wispCard = new Card("Wisp", 31, Constants.INT_UNINIT, 1, 2, "CS2_231")

        "valid" in {
          val gs = new GameState()
          val friendly = new Player(1, hand = List(card))
          val enemy = new Player(2, board = List(tauntCard))
          gs.friendlyPlayer = friendly
          gs.enemyPlayer = enemy
          val voter = new Voter("A Twitch User").voteEntry(CardPlayWithEnemyTarget(1, 1))
          voter.actionVoteList shouldBe List(CardPlayWithEnemyTarget(1, 1))
          voter.invalidVoteList shouldBe Nil
        }

        "invalid" in {
          val gs = new GameState()
          val friendly = new Player(1, hand = List(card))
          val enemy = new Player(2, board = List(wispCard))
          gs.friendlyPlayer = friendly
          gs.enemyPlayer = enemy
          val voter = new Voter("A Twitch User").voteEntry(CardPlayWithEnemyTarget(1, 1))
          voter.actionVoteList shouldBe Nil
          voter.invalidVoteList shouldBe List(CardPlayWithEnemyTarget(1, 1))
        }
      }

      "REQ_TARGET_WITH_DEATHRATTLE - Unearthed Raptor" - {

        val card = new Card("Unearthed Raptor", 1, 1, Constants.INT_UNINIT, 1, "LOE_019")
        val deathrattleCard = new Card("Dreedstead", 11, Constants.INT_UNINIT, 1, 1, "AT_019")
        val wispCard = new Card("Wisp", 11, Constants.INT_UNINIT, 1, 1, "CS2_231")

        "valid" in {
          val gs = new GameState()
          val friendly = new Player(1, hand = List(card), board = List(deathrattleCard))
          gs.friendlyPlayer = friendly
          val voter = new Voter("A Twitch User").voteEntry(CardPlayWithFriendlyTarget(1, 1))
          voter.actionVoteList shouldBe List(CardPlayWithFriendlyTarget(1, 1))
          voter.invalidVoteList shouldBe Nil
        }

        "invalid" in {
          val gs = new GameState()
          val friendly = new Player(1, hand = List(card), board = List(wispCard))
          gs.friendlyPlayer = friendly
          val voter = new Voter("A Twitch User").voteEntry(CardPlayWithFriendlyTarget(1, 1))
          voter.actionVoteList shouldBe Nil
          voter.invalidVoteList shouldBe List(CardPlayWithFriendlyTarget(1, 1))
        }
      }


      "REQ_ENEMY_TARGET - Aldor Peacekeeper" - {

        val card = new Card("Aldor Peacekeeper", 1, 1, Constants.INT_UNINIT, 1, "EX1_382")
        val friendlyWispCard = new Card("Wisp", 11, Constants.INT_UNINIT, 1, 1, "CS2_231")
        val enemyWispCard = new Card("Wisp", 31, Constants.INT_UNINIT, 1, 2, "CS2_231")

        "valid" in {
          val gs = new GameState()
          val friendly = new Player(1, hand = List(card))
          val enemy = new Player(2, board = List(enemyWispCard))
          gs.friendlyPlayer = friendly
          gs.enemyPlayer = enemy
          val voter = new Voter("A Twitch User").voteEntry(CardPlayWithEnemyTarget(1, 1))
          voter.actionVoteList shouldBe List(CardPlayWithEnemyTarget(1, 1))
          voter.invalidVoteList shouldBe Nil
        }

        "invalid" in {
          val gs = new GameState()
          val friendly = new Player(1, hand = List(card), board = List(friendlyWispCard))
          gs.friendlyPlayer = friendly
          val voter = new Voter("A Twitch User").voteEntry(CardPlayWithFriendlyTarget(1, 1))
          voter.actionVoteList shouldBe Nil
          voter.invalidVoteList shouldBe List(CardPlayWithFriendlyTarget(1, 1))
        }
      }

      "REQ_HERO_TARGET - Alexstrasza" - {

        val card = new Card("Alexstrasza", 1, 1, Constants.INT_UNINIT, 1, "EX1_561")
        val wispCard = new Card("Wisp", 31, Constants.INT_UNINIT, 1, 2, "CS2_231")

        "valid" in {
          val gs = new GameState()
          val friendly = new Player(1, hand = List(card))
          gs.friendlyPlayer = friendly
          val voter = new Voter("A Twitch User").voteEntry(CardPlayWithEnemyTarget(1, 0))
          voter.actionVoteList shouldBe List(CardPlayWithEnemyTarget(1, 0))
          voter.invalidVoteList shouldBe Nil
        }

        "invalid" in {
          val gs = new GameState()
          val friendly = new Player(1, hand = List(card))
          val enemy = new Player(2, board = List(wispCard))
          gs.friendlyPlayer = friendly
          gs.enemyPlayer = enemy
          val voter = new Voter("A Twitch User").voteEntry(CardPlayWithEnemyTarget(1, 1))
          voter.actionVoteList shouldBe Nil
          voter.invalidVoteList shouldBe List(CardPlayWithEnemyTarget(1, 1))
        }
      }


      "REQ_TARGET_IF_AVAILABLE_AND_MINIMUM_FRIENDLY_MINIONS - Gormok the Impaler" - {


        val card = new Card("Gormok the Impaler", 1, 1, Constants.INT_UNINIT, 1, "AT_122")
        val friendlyCard1 = new Card("Wisp", 11, Constants.INT_UNINIT, 1, 1, "CS2_231")
        val friendlyCard2 = new Card("Wisp", 12, Constants.INT_UNINIT, 2, 1, "CS2_231")
        val friendlyCard3 = new Card("Wisp", 13, Constants.INT_UNINIT, 3, 1, "CS2_231")
        val friendlyCard4 = new Card("Wisp", 14, Constants.INT_UNINIT, 4, 1, "CS2_231")

        "valid" in {
          val gs = new GameState()
          val friendly = new Player(1, hand = List(card), board = List(friendlyCard1, friendlyCard2, friendlyCard3, friendlyCard4))
          gs.friendlyPlayer = friendly
          val voter = new Voter("A Twitch User").voteEntry(CardPlayWithFriendlyTarget(1, 1))
          voter.actionVoteList shouldBe List(CardPlayWithFriendlyTarget(1, 1))
          voter.invalidVoteList shouldBe Nil
        }

        "invalid" in {
          val gs = new GameState()
          val friendly = new Player(1, hand = List(card), board = List(friendlyCard1))
          gs.friendlyPlayer = friendly
          val voter = new Voter("A Twitch User").voteEntry(CardPlayWithFriendlyTarget(1, 1))
          voter.actionVoteList shouldBe Nil
          voter.invalidVoteList shouldBe List(CardPlayWithFriendlyTarget(1, 1))
        }
      }

      "REQ_NUM_MINION_SLOTS - Animal Companion" - {

        val card = new Card("Animal Companion", 1, 1, Constants.INT_UNINIT, 1, "NEW1_031")
        val friendlyCard1 = new Card("Wisp", 11, Constants.INT_UNINIT, 1, 1, "CS2_231")
        val friendlyCard2 = new Card("Wisp", 12, Constants.INT_UNINIT, 2, 1, "CS2_231")
        val friendlyCard3 = new Card("Wisp", 13, Constants.INT_UNINIT, 3, 1, "CS2_231")
        val friendlyCard4 = new Card("Wisp", 14, Constants.INT_UNINIT, 4, 1, "CS2_231")
        val friendlyCard5 = new Card("Wisp", 15, Constants.INT_UNINIT, 5, 1, "CS2_231")
        val friendlyCard6 = new Card("Wisp", 16, Constants.INT_UNINIT, 6, 1, "CS2_231")
        val friendlyCard7 = new Card("Wisp", 17, Constants.INT_UNINIT, 7, 1, "CS2_231")

        "valid" in {
          val gs = new GameState()
          val friendly = new Player(1, hand = List(card))
          gs.friendlyPlayer = friendly
          val voter = new Voter("A Twitch User").voteEntry(CardPlay(1))
          voter.actionVoteList shouldBe List(CardPlay(1))
          voter.invalidVoteList shouldBe Nil
        }

        "invalid" in {
          val gs = new GameState()
          val friendly = new Player(1, hand = List(card), board = List(friendlyCard1, friendlyCard2, friendlyCard3, friendlyCard4, friendlyCard5, friendlyCard6, friendlyCard7))
          gs.friendlyPlayer = friendly
          val voter = new Voter("A Twitch User").voteEntry(CardPlay(1))
          voter.actionVoteList shouldBe Nil
          voter.invalidVoteList shouldBe List(CardPlay(1))
        }
      }

      "REQ_ENTIRE_ENTOURAGE_NOT_IN_PLAY - Totemic Call" - {

        val hp = new Card("Totemic Call", 0, 0, Constants.INT_UNINIT, 1, "CS2_049")
        val friendlyCard1 = new Card("Searing Totem", 11, Constants.INT_UNINIT, 1, 1, "CS2_050")
        val friendlyCard2 = new Card("Stoneclaw Totem", 12, Constants.INT_UNINIT, 2, 1, "CS2_051")
        val friendlyCard3 = new Card("Wrath of Air Totem", 13, Constants.INT_UNINIT, 3, 1, "CS2_052")
        val friendlyCard4 = new Card("Healing Totem", 14, Constants.INT_UNINIT, 4, 1, "NEW1_009")

        "valid" in {
          val gs = new GameState()
          val friendly = new Player(1, heroPower = Some(hp))
          gs.friendlyPlayer = friendly
          val voter = new Voter("A Twitch User").voteEntry(HeroPower())
          voter.actionVoteList shouldBe List(HeroPower())
          voter.invalidVoteList shouldBe Nil
        }

        "invalid" in {
          val gs = new GameState()
          val friendly = new Player(1, heroPower = Some(hp), board = List(friendlyCard1, friendlyCard2, friendlyCard3, friendlyCard4))
          gs.friendlyPlayer = friendly
          val voter = new Voter("A Twitch User").voteEntry(HeroPower())
          voter.actionVoteList shouldBe Nil
          voter.invalidVoteList shouldBe List(HeroPower())
        }
      }

      "REQ_MINIMUM_ENEMY_MINIONS - Forked Lightning" - {

        val card = new Card("Forked Lightning", 1, 1, Constants.INT_UNINIT, 1, "EX1_251")
        val card2 = new Card("Wisp", 31, Constants.INT_UNINIT, 1, 2, "CS2_231")
        val card3 = new Card("Wisp", 32, Constants.INT_UNINIT, 2, 2, "CS2_231")

        "valid" in {
          val gs = new GameState()
          val friendly = new Player(1, hand = List(card))
          val enemy = new Player(2, board = List(card2, card3))
          gs.friendlyPlayer = friendly
          gs.enemyPlayer = enemy
          val voter = new Voter("A Twitch User").voteEntry(CardPlay(1))
          voter.actionVoteList shouldBe List(CardPlay(1))
          voter.invalidVoteList shouldBe Nil
        }

        "invalid" in {
          val gs = new GameState()
          val friendly = new Player(1, hand = List(card))
          val enemy = new Player(2, board = List(card2))
          gs.friendlyPlayer = friendly
          gs.enemyPlayer = enemy
          val voter = new Voter("A Twitch User").voteEntry(CardPlay(1))
          voter.actionVoteList shouldBe Nil
          voter.invalidVoteList shouldBe List(CardPlay(1))
        }
      }

      "REQ_TARGET_WITH_RACE - Houndmaster" - {

        val card = new Card("Houndmaster", 1, 1, Constants.INT_UNINIT, 1, "DS1_070")
        val beastCard = new Card("Tundra Rhino", 11, Constants.INT_UNINIT, 1, 1, "DS1_178")
        val nonBeastCard = new Card("Wisp", 12, Constants.INT_UNINIT, 2, 1, "CS2_231")

        "valid" in {
          val gs = new GameState()
          val friendly = new Player(1, hand = List(card), board = List(beastCard))
          gs.friendlyPlayer = friendly
          val voter = new Voter("A Twitch User").voteEntry(CardPlayWithFriendlyTarget(1, 1))
          voter.actionVoteList shouldBe List(CardPlayWithFriendlyTarget(1, 1))
          voter.invalidVoteList shouldBe Nil
        }

        "invalid" in {
          val gs = new GameState()
          val friendly = new Player(1, hand = List(card), board = List(nonBeastCard))
          gs.friendlyPlayer = friendly
          val voter = new Voter("A Twitch User").voteEntry(CardPlayWithFriendlyTarget(1, 1))
          voter.actionVoteList shouldBe Nil
          voter.invalidVoteList shouldBe List(CardPlayWithFriendlyTarget(1, 1))
        }
      }


      "REQ_TARGET_MAX_ATTACK - Shadow Word: Pain" - {

        val card = new Card("Shadow Word: Pain", 1, 1, Constants.INT_UNINIT, 1, "CS2_234")
        val wispCard = new Card("Wisp", 11, Constants.INT_UNINIT, 1, 1, "CS2_231")
        val psgCard = new Card("Piloted Sky Golem", 12, Constants.INT_UNINIT, 2, 1, "GVG_105")

        "valid" in {
          val gs = new GameState()
          val friendly = new Player(1, hand = List(card), board = List(wispCard, psgCard))
          gs.friendlyPlayer = friendly
          val voter = new Voter("A Twitch User").voteEntry(CardPlayWithFriendlyTarget(1, 1))
          voter.actionVoteList shouldBe List(CardPlayWithFriendlyTarget(1, 1))
          voter.invalidVoteList shouldBe Nil
        }

        "invalid" in {
          val gs = new GameState()
          val friendly = new Player(1, hand = List(card), board = List(wispCard, psgCard))
          gs.friendlyPlayer = friendly
          val voter = new Voter("A Twitch User").voteEntry(CardPlayWithFriendlyTarget(1, 2))
          voter.actionVoteList shouldBe Nil
          voter.invalidVoteList shouldBe List(CardPlayWithFriendlyTarget(1, 2))
        }
      }


      "REQ_TARGET_IF_AVAILABLE - Lance Carrier" - {
        val card = new Card("Lance Carrier", 1, 1, Constants.INT_UNINIT, 1, "AT_084")
        val friendlyWispCard = new Card("Wisp", 11, Constants.INT_UNINIT, 1, 1, "CS2_231")

        "valid" in {
          val gs = new GameState()
          val friendly = new Player(1, hand = List(card), board = List(friendlyWispCard))
          gs.friendlyPlayer = friendly
          val voter = new Voter("A Twitch User").voteEntry(CardPlayWithFriendlyTarget(1, 1))
          voter.actionVoteList shouldBe List(CardPlayWithFriendlyTarget(1, 1))
          voter.invalidVoteList shouldBe Nil
        }

        "invalid" in {
          val gs = new GameState()
          val friendly = new Player(1, hand = List(card))
          gs.friendlyPlayer = friendly
          val voter = new Voter("A Twitch User").voteEntry(CardPlayWithFriendlyTarget(1, 1))
          voter.actionVoteList shouldBe Nil
          voter.invalidVoteList shouldBe List(CardPlayWithFriendlyTarget(1, 1))
        }
      }


      "REQ_FRIENDLY_TARGET - Lance Carrier" - {
        val card = new Card("Lance Carrier", 1, 1, Constants.INT_UNINIT, 1, "AT_084")
        val friendlyWispCard = new Card("Wisp", 11, Constants.INT_UNINIT, 1, 1, "CS2_231")
        val enemyWispCard = new Card("Wisp", 31, Constants.INT_UNINIT, 1, 2, "CS2_231")

        "valid" in {
          val gs = new GameState()
          val friendly = new Player(1, hand = List(card), board = List(friendlyWispCard))
          gs.friendlyPlayer = friendly
          val voter = new Voter("A Twitch User").voteEntry(CardPlayWithFriendlyTarget(1, 1))
          voter.actionVoteList shouldBe List(CardPlayWithFriendlyTarget(1, 1))
          voter.invalidVoteList shouldBe Nil
        }

        "invalid" in {
          val gs = new GameState()
          val friendly = new Player(1, hand = List(card))
          val enemy = new Player(2, board = List(enemyWispCard))
          gs.friendlyPlayer = friendly
          val voter = new Voter("A Twitch User").voteEntry(CardPlayWithEnemyTarget(1, 1))
          voter.actionVoteList shouldBe Nil
          voter.invalidVoteList shouldBe List(CardPlayWithEnemyTarget(1, 1))
        }
      }


      "REQ_TARGET_TO_PLAY - Lesser Heal" - {
        val hp = new Card("Lesser Heal", 0, 0, Constants.INT_UNINIT, 1, "CS1h_001")
        val friendlyWispCard = new Card("Wisp", 11, Constants.INT_UNINIT, 1, 1, "CS2_231")

        "valid" in {
          val gs = new GameState()
          val friendly = new Player(1, heroPower = Some(hp), board = List(friendlyWispCard))
          gs.friendlyPlayer = friendly
          val voter = new Voter("A Twitch User").voteEntry(HeroPowerWithFriendlyTarget(1))
          voter.actionVoteList shouldBe List(HeroPowerWithFriendlyTarget(1))
          voter.invalidVoteList shouldBe Nil
        }

        "invalid" in {
          val gs = new GameState()
          val friendly = new Player(1, heroPower = Some(hp))
          gs.friendlyPlayer = friendly
          val voter = new Voter("A Twitch User").voteEntry(HeroPower())
          voter.actionVoteList shouldBe Nil
          voter.invalidVoteList shouldBe List(HeroPower())
        }
      }


      "REQ_MINION_TARGET - Lance Carrier" - {
        val card = new Card("Lance Carrier", 1, 1, Constants.INT_UNINIT, 1, "AT_084")
        val friendlyWispCard = new Card("Wisp", 11, Constants.INT_UNINIT, 1, 1, "CS2_231")

        "valid" in {
          val gs = new GameState()
          val friendly = new Player(1, hand = List(card), board = List(friendlyWispCard))
          gs.friendlyPlayer = friendly
          val voter = new Voter("A Twitch User").voteEntry(CardPlayWithFriendlyTarget(1, 1))
          voter.actionVoteList shouldBe List(CardPlayWithFriendlyTarget(1, 1))
          voter.invalidVoteList shouldBe Nil
        }

        "invalid" in {
          val gs = new GameState()
          val friendly = new Player(1, hand = List(card))
          gs.friendlyPlayer = friendly
          val voter = new Voter("A Twitch User").voteEntry(CardPlayWithFriendlyTarget(1, 0))
          voter.actionVoteList shouldBe Nil
          voter.invalidVoteList shouldBe List(CardPlayWithFriendlyTarget(1, 0))
        }
      }

      "REQ_MINIMUM_TOTAL_MINIONS - Brawl" - {
        val card = new Card("Brawl", 1, 1, Constants.INT_UNINIT, 1, "EX1_407")
        val friendlyWispCard = new Card("Wisp", 11, Constants.INT_UNINIT, 1, 1, "CS2_231")

        "valid" in {
          val gs = new GameState()
          val friendly = new Player(1, hand = List(card), board = List(friendlyWispCard))
          gs.friendlyPlayer = friendly
          val voter = new Voter("A Twitch User").voteEntry(CardPlay(1))
          voter.actionVoteList shouldBe List(CardPlay(1))
          voter.invalidVoteList shouldBe Nil
        }

        "invalid" in {
          val gs = new GameState()
          val friendly = new Player(1)
          gs.friendlyPlayer = friendly
          val voter = new Voter("A Twitch User").voteEntry(CardPlay(1))
          voter.actionVoteList shouldBe Nil
          voter.invalidVoteList shouldBe List(CardPlay(1))
        }
      }
    }



    "to be able to record vote accuracy, so that I can identify trolls" in {
      //For unit tests:
      //Be able to divide by 0
      //Do not record accuracy if both vote lists are 0
      val gs = new GameState()
      val vm = new VoteManager(gs)
      val trollVoteList = List(
        CardPlayWithFriendlyTarget(1,1),
        CardPlayWithFriendlyTarget(1,2),
        CardPlayWithFriendlyTarget(1,3),
        CardPlayWithFriendlyTarget(1,4))

      val validVoteList = List(
        CardPlay(1))
      val troll = new Voter("Troll", validVoteList, trollVoteList)
      vm.voterMap = Map[String, Voter]("Troll" -> troll)
      vm.recordAccuracy
      vm.voterMap("Troll").voteAccuracy shouldBe List(.25)
    }


    "to be able to eliminate voting power for voters that has been declared a troll" in {
      val gs = new GameState()
      val vm = new VoteManager(gs)
      val mockVoteAccuracy = List(.3,.3,.3,.3,.3,.3,.3,.3,.3,.3,.3,.3,.3,.3,.3,.3,.3,.3,.3,.3)
      val validVoteList = List(CardPlay(1))
      val troll = new Voter("Troll", validVoteList, voteAccuracy = mockVoteAccuracy)
      vm.voterMap = Map[String, Voter]("Troll" -> troll)
      vm.voterMap("Troll").getTotalVoteValues shouldBe Map[Vote, Double]()
    }

    "to be able to influence vote power based on previous voter accuracy, so that I can reward thoughtful players and punish trolls" in {
      val gs = new GameState()
      val vm = new VoteManager(gs)
      val mockVoteAccuracy = List(.8,.8,.8,.8,.8,.8,.8,.8,.8,.8,.8,.8,.8,.8,.8,.8,.8,.8,.8,.8)
      val validVoteList = List(CardPlay(1))
      val thoughtfulVoter = new Voter("Twitch User", validVoteList, voteAccuracy = mockVoteAccuracy)
      vm.voterMap = Map[String,Voter]("Twitch User" -> thoughtfulVoter)
      vm.voterMap("Twitch User").accuracyVoteValues shouldBe Map(CardPlay(1) -> .8)
    }



    //todo As the client, I want to be able to influence the vote results, so that I can implement AI logic that detects combos and patterns.

    /*
    BRAINSTORM:
    Define:
    pattern type - A pattern type is non specific to the actual card being played, but rather the type of card.
                  - For example: A heal enabled card that has a damaged card target will be pattern type "heal"
                  - Another example: A card that is played directly after another card will be pattern type "order"
    pattern - Specific to the cards played and targeted. The AI will detect specific patterns of certain pattern types
    pattern list - After the AI detects specific patterns from the massive total vote list, a list of patterns will be saved with a pattern factor.
    pattern factor - The strength of influence that each pattern will contribute to individual voter votes that meet the given pattern.
                    -For example: A pattern of CardPlay(1) -> CardPlay(2) with pattern factor .2 will increase the vote value of any CardPlay(1) vote that is followed by CardPlay(2)
                    -Pattern factor is determined by pattern type and popularity of pattern.

    AI says:
    -I've noticed a pattern of card 4 being played directly after card 2.
    -I've also noticed a pattern of a heal enabled card targeting a damaged minion




     */









  }
}


