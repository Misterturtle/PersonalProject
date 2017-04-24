package VoteSystemTests

import Logic.IRCState
import VoteSystem._
import org.scalatest.{FreeSpec, FlatSpec, Matchers}
import tph.Constants.ActionVotes._
import tph.Constants.EmojiVotes.EmojiUninit
import tph.Constants.Vote
import tph._

import scala.collection.mutable.ListBuffer


class VoteManagerTests extends FreeSpec with Matchers {

  val sender = "VoteManagerTests"


  "When VoteManager receives a vote, it should" - {
    "pass the vote to the appropriate voter" in {
      val gs = new GameState()
      val vs = new VoteState()
      val ai = new VoteAI(vs, gs)
      val ircState = new IRCState()
      val validator = new VoteValidator(gs)
      val vm = new VoteManager(gs, vs, ai, ircState, validator)
      val vote = new CardPlay(1)
      var voteEntered = false
      val mockVoter = new Voter(sender) {
        override def actionVoteEntry(vote: ActionVote, valid: Boolean = true): Voter = {
          voteEntered = true
          Voter("")
        }
      }
      vs.voterMap = Map[String, Voter](sender -> mockVoter)
      vm.voteEntry(sender, vote)
      voteEntered shouldBe true
    }

    "add a voter to the voter list when entering a vote from an unknown voter" in {
      val vote = new CardPlay(1)
      val gs = new GameState()
      val vs = new VoteState()
      val ai = new VoteAI(vs, gs)
      val ircState = new IRCState()
      val validator = new VoteValidator(gs)
      val vm = new VoteManager(gs, vs, ai, ircState, validator)
      vm.voteEntry(sender, vote)
      vs.voterMap.isDefinedAt(sender) shouldBe true
    }
  }


  "When RemoveVote method is called, VoteManager should tell the voter to remove the vote" in {
    val gs = new GameState()
    val vs = new VoteState()
    vs.voterMap = Map[String, Voter]("A Twitch User" -> Voter("A Twitch User", List(CardPlay(1))))
    val ai = new VoteAI(vs, gs)
    val ircState = new IRCState()
    val validator = new VoteValidator(gs)
    val vm = new VoteManager(gs, vs, ai, ircState, validator)


    vm.removeVote("A Twitch User", CardPlay(1))
    vs.voterMap("A Twitch User").actionVoteList.isEmpty shouldBe true
  }


  "When the makeDecision method is called, VoteManager should have the VoteAI build a decision" in {
    var decisionMade = false
    val gs = new GameState()
    val vs = new VoteState()
    val ai = new VoteAI(vs, gs) {
      override def makeDecision(): List[(ActionVote, (HSCard, HSCard))] = {
        decisionMade = true
        List((ActionUninit(), (NoCard(), NoCard())))
      }
    }
    val ircState = new IRCState()
    val validator = new VoteValidator(gs)
    val vm = new VoteManager(gs, vs, ai, ircState, validator)
    vm.makeDecision()
    decisionMade shouldBe true
  }

  "When the makeDiscoverDecision method is called, VoteManager shouldTally votes and return the max vote" in {
    val gs = new GameState()
    val vs = new VoteState()
    val ai = new VoteAI(vs, gs)


      val voter1 = Voter("A Twitch User", List(CardPlay(1), Discover(1)))
      val voter2 = Voter("Another Twitch User", List(CardPlay(1), Discover(1)))
      val voter3 = Voter("A Third Twitch User", List(CardPlay(1), Discover(2)))
    val ircState = new IRCState()
    val validator = new VoteValidator(gs)
    val vm = new VoteManager(gs, vs, ai, ircState, validator)

      vs.voterMap = Map[String, Voter]("A Twitch User" -> voter1, "Another Twitch User" -> voter2, "A Third Twitch User" -> voter3)
      vm.makeDiscoverDecision() shouldBe Discover(1)
    }


  "When the makeChooseOneDecision method is called, VoteManager shouldTally votes and return the max vote" in {
    val gs = new GameState()
    val vs = new VoteState()
    val ai = new VoteAI(vs, gs)


    val voter1 = Voter("A Twitch User", List(CardPlay(1), ChooseOne(1)))
    val voter2 = Voter("Another Twitch User", List(CardPlay(1), ChooseOne(1)))
    val voter3 = Voter("A Third Twitch User", List(CardPlay(1), ChooseOne(2)))
    val ircState = new IRCState()
    val validator = new VoteValidator(gs)
    val vm = new VoteManager(gs, vs, ai, ircState, validator)

    vs.voterMap = Map[String, Voter]("A Twitch User" -> voter1, "Another Twitch User" -> voter2, "A Third Twitch User" -> voter3)
    vm.makeChooseOneDecision() shouldBe (ChooseOne(1), (NoCard(), NoCard()))
  }


  "When the makeMulliganDecision method is called, VoteManager shouldTally votes and return the max vote" in {
    val gs = new GameState()
    val vs = new VoteState()
    val ai = new VoteAI(vs, gs)


    val voter1 = Voter("A Twitch User", List(MulliganVote(true,false,true,false)))
    val voter2 = Voter("Another Twitch User", List(MulliganVote(true,false,true,false)))
    val voter3 = Voter("A Third Twitch User", List(MulliganVote(true,false,false,false)))
    val ircState = new IRCState()
    val validator = new VoteValidator(gs)
    val vm = new VoteManager(gs, vs, ai, ircState, validator)

    vs.voterMap = Map[String, Voter]("A Twitch User" -> voter1, "Another Twitch User" -> voter2, "A Third Twitch User" -> voter3)
    vm.makeMulliganDecision() shouldBe MulliganVote(true,false,true,false)
  }



  "When clearActionVotes method is ran, VoteManager should tell all voters to clear their actionVote lists" in {

    val votersCleared = ListBuffer[String]()
    val gs = new GameState()
    val vs = new VoteState()
    val ai = new VoteAI(vs,gs)
    val ircState = new IRCState()
    val validator = new VoteValidator(gs)
    val vm = new VoteManager(gs, vs, ai, ircState, validator)
    val voter1 = new Voter("A Twitch User"){
      override def clearActionVotes():Voter = {
        votersCleared.append(name)
        copy()
      }
    }
    val voter2 = new Voter("Another Twitch User"){
      override def clearActionVotes():Voter = {
        votersCleared.append(name)
        copy()
      }
    }
    val voter3 = new Voter("A Third Twitch User"){
      override def clearActionVotes():Voter = {
        votersCleared.append(name)
        copy()
      }
    }
    vs.voterMap = Map("A Twitch User" -> voter1, "Another Twitch User" -> voter2, "A Third Twitch User" -> voter3)
    vm.clearActionVotes()
    votersCleared shouldBe ListBuffer[String]("A Twitch User", "Another Twitch User", "A Third Twitch User")
  }

  "When clearAllVotes method is ran, VoteManager should tell all voters to clear all their votes" in {

    val votersCleared = ListBuffer[String]()
    val gs = new GameState()
    val vs = new VoteState()
    val ai = new VoteAI(vs,gs)
    val ircState = new IRCState()
    val validator = new VoteValidator(gs)
    val vm = new VoteManager(gs, vs, ai, ircState, validator)
    val voter1 = new Voter("A Twitch User"){
      override def clearAllVotes():Voter = {
        votersCleared.append(name)
        copy()
      }
    }
    val voter2 = new Voter("Another Twitch User"){
      override def clearAllVotes():Voter = {
        votersCleared.append(name)
        copy()
      }
    }
    val voter3 = new Voter("A Third Twitch User"){
      override def clearAllVotes():Voter = {
        votersCleared.append(name)
        copy()
      }
    }
    vs.voterMap = Map("A Twitch User" -> voter1, "Another Twitch User" -> voter2, "A Third Twitch User" -> voter3)
    vm.clearAllVotes()
    votersCleared shouldBe ListBuffer[String]("A Twitch User", "Another Twitch User", "A Third Twitch User")
  }


    "Vote Manager should detect invalid votes" - {

      "REQ_TARGET_MIN_ATTACK - Shadow Word: Death" - {

        "valid" in {
          val gs = new GameState()
          val vs = new VoteState()
          val ai = new VoteAI(vs, gs)
          val card = new Card("Shadow Word: Death", 1, 1, Constants.INT_UNINIT, 1, "EX1_622", cardInfo = gs.getCardInfo("EX1_622"))
          val wispCard = new Card("Wisp", 11, Constants.INT_UNINIT, 1, 1, "CS2_231", cardInfo = gs.getCardInfo("CS2_231"))
          val psgCard = new Card("Piloted Sky Golem", 12, Constants.INT_UNINIT, 2, 1, "GVG_105", cardInfo = gs.getCardInfo("GVG_105"))
          val ircState = new IRCState()
          val validator = new VoteValidator(gs)
          val vm = new VoteManager(gs, vs, ai, ircState, validator)
          val ircBot = new IRCBot(vm)
          val friendly = new Player(1, hand = List(card), board = List(wispCard, psgCard))
          gs.friendlyPlayer = friendly
          ircBot.onMessage("None", "A Twitch User", "None", "None", "!c1>f2")
          vs.voterMap("A Twitch User").actionVoteList shouldBe List(CardPlayWithFriendlyTarget(1, 2))
          vs.voterMap("A Twitch User").invalidVoteList shouldBe Nil
        }

        "invalid" in {
          val gs = new GameState()
          val vs = new VoteState()
          val ai = new VoteAI(vs, gs)
          val card = new Card("Shadow Word: Death", 1, 1, Constants.INT_UNINIT, 1, "EX1_622", cardInfo = gs.getCardInfo("EX1_622"))
          val wispCard = new Card("Wisp", 11, Constants.INT_UNINIT, 1, 1, "CS2_231", cardInfo = gs.getCardInfo("CS2_231"))
          val psgCard = new Card("Piloted Sky Golem", 12, Constants.INT_UNINIT, 2, 1, "GVG_105", cardInfo = gs.getCardInfo("GVG_105"))
          val ircState = new IRCState()
          val validator = new VoteValidator(gs)
          val vm = new VoteManager(gs, vs, ai, ircState, validator)
          val ircBot = new IRCBot(vm)
          val friendly = new Player(1, hand = List(card), board = List(wispCard, psgCard))
          gs.friendlyPlayer = friendly
          val vote = CardPlayWithFriendlyTarget(1,1)
          validator.isValidVote(vote, false, true) shouldBe false
        }
      }

      "REQ_LEGENDARY_TARGET - Rend Blackhand" - {


        "valid" in {
          val gs = new GameState()
          val vs = new VoteState()
          val ai = new VoteAI(vs, gs)
          val card = new Card("Rend Blackhand", 1, 1, Constants.INT_UNINIT, 1, "BRM_029", cardInfo = gs.getCardInfo("BRM_029"))
          val wispCard = new Card("Wisp", 11, Constants.INT_UNINIT, 1, 1, "CS2_231", cardInfo = gs.getCardInfo("CS2_231"))
          val chillCard = new Card("Chillmaw", 12, Constants.INT_UNINIT, 2, 1, "AT_123", cardInfo = gs.getCardInfo("AT_123"))
          val dragonCard = new Card("Hungry Dragon", 2, 2, Constants.INT_UNINIT, 1, "BRM_026", cardInfo = gs.getCardInfo("BRM_026"))
          val ircState = new IRCState()
          val validator = new VoteValidator(gs)
          val vm = new VoteManager(gs, vs, ai, ircState, validator)
          val ircBot = new IRCBot(vm)
          val friendly = new Player(1, hand = List(card, dragonCard), board = List(wispCard, chillCard))
          gs.friendlyPlayer = friendly
          ircBot.onMessage("None", "A Twitch User", "None", "None", "!c1>f2")
          vs.voterMap("A Twitch User").actionVoteList shouldBe List(CardPlayWithFriendlyTarget(1, 2))
          vs.voterMap("A Twitch User").invalidVoteList shouldBe Nil
        }

        "invalid" in {
          val gs = new GameState()
          val vs = new VoteState()
          val ai = new VoteAI(vs, gs)
          val card = new Card("Rend Blackhand", 1, 1, Constants.INT_UNINIT, 1, "BRM_029", cardInfo = gs.getCardInfo("BRM_029"))
          val wispCard = new Card("Wisp", 11, Constants.INT_UNINIT, 1, 1, "CS2_231", cardInfo = gs.getCardInfo("CS2_231"))
          val chillCard = new Card("Chillmaw", 12, Constants.INT_UNINIT, 2, 1, "AT_123", cardInfo = gs.getCardInfo("AT_123"))
          val dragonCard = new Card("Hungry Dragon", 2, 2, Constants.INT_UNINIT, 1, "BRM_026", cardInfo = gs.getCardInfo("BRM_026"))
          val ircState = new IRCState()
          val validator = new VoteValidator(gs)
          val vm = new VoteManager(gs, vs, ai, ircState, validator)
          val friendly = new Player(1, hand = List(card, dragonCard), board = List(wispCard, chillCard))
          gs.friendlyPlayer = friendly
          val vote = CardPlayWithFriendlyTarget(1,1)
          validator.isValidVote(vote, false, true) shouldBe false
        }
      }

      "REQ_TARGET_IF_AVAILABLE_AND_MINIMUM_FRIENDLY_SECRETS - Medivh's Valet" - {

        "valid" in {

          val gs = new GameState()
          val vs = new VoteState()
          val ai = new VoteAI(vs, gs)
          val card = new Card("Medivh's Valet", 1, 1, Constants.INT_UNINIT, 1, "KAR_092", cardInfo = gs.getCardInfo("KAR_092"))
          val wispCard = new Card("Wisp", 11, Constants.INT_UNINIT, 1, 1, "CS2_231", cardInfo = gs.getCardInfo("CS2_231"))
          val ircState = new IRCState()
          val validator = new VoteValidator(gs)
          val vm = new VoteManager(gs, vs, ai, ircState, validator)
          val ircBot = new IRCBot(vm)
          val friendly = new Player(1, hand = List(card), board = List(wispCard), secretsInPlay = 1)
          gs.friendlyPlayer = friendly
          ircBot.onMessage("None", "A Twitch User", "None", "None", "!c1>f1")
          vs.voterMap("A Twitch User").actionVoteList shouldBe List(CardPlayWithFriendlyTarget(1, 1))
          vs.voterMap("A Twitch User").invalidVoteList shouldBe Nil
        }

        "invalid" in {
          val gs = new GameState()
          val vs = new VoteState()
          val ai = new VoteAI(vs, gs)
          val card = new Card("Medivh's Valet", 1, 1, Constants.INT_UNINIT, 1, "KAR_092", cardInfo = gs.getCardInfo("KAR_092"))
          val wispCard = new Card("Wisp", 11, Constants.INT_UNINIT, 1, 1, "CS2_231", cardInfo = gs.getCardInfo("CS2_231"))
          val ircState = new IRCState()
          val validator = new VoteValidator(gs)
          val vm = new VoteManager(gs, vs, ai, ircState, validator)
          val ircBot = new IRCBot(vm)
          val friendly = new Player(1, hand = List(card), board = List(wispCard), secretsInPlay = 0)
          gs.friendlyPlayer = friendly
          val vote = CardPlayWithFriendlyTarget(1,1)
          validator.isValidVote(vote, false, true) shouldBe false
        }
      }


      "REQ_TARGET_IF_AVAILABLE_AND_DRAGON_IN_HAND - Blackwing Corruptor" - {

        "valid" in {
          val gs = new GameState()
          val vs = new VoteState()
          val ai = new VoteAI(vs, gs)
          val card = new Card("Blackwing Corruptor", 1, 1, Constants.INT_UNINIT, 1, "BRM_034", cardInfo = gs.getCardInfo("BRM_034"))
          val wispCard = new Card("Wisp", 11, Constants.INT_UNINIT, 1, 1, "CS2_231", cardInfo = gs.getCardInfo("CS2_231"))
          val dragonCard = new Card("Hungry Dragon", 2, 2, Constants.INT_UNINIT, 1, "BRM_026", cardInfo = gs.getCardInfo("BRM_026"))
          val ircState = new IRCState()
          val validator = new VoteValidator(gs)
          val vm = new VoteManager(gs, vs, ai, ircState, validator)
          val ircBot = new IRCBot(vm)
          val friendly = new Player(1, hand = List(card, dragonCard), board = List(wispCard))
          gs.friendlyPlayer = friendly
          ircBot.onMessage("None", "A Twitch User", "None", "None", "!c1>f1")
          vs.voterMap("A Twitch User").actionVoteList shouldBe List(CardPlayWithFriendlyTarget(1, 1))
          vs.voterMap("A Twitch User").invalidVoteList shouldBe Nil
        }

        "invalid" in {
          val gs = new GameState()
          val vs = new VoteState()
          val ai = new VoteAI(vs, gs)
          val card = new Card("Blackwing Corruptor", 1, 1, Constants.INT_UNINIT, 1, "BRM_034", cardInfo = gs.getCardInfo("BRM_034"))
          val wispCard = new Card("Wisp", 11, Constants.INT_UNINIT, 1, 1, "CS2_231", cardInfo = gs.getCardInfo("CS2_231"))
          val ircState = new IRCState()
          val validator = new VoteValidator(gs)
          val vm = new VoteManager(gs, vs, ai, ircState, validator)
          val ircBot = new IRCBot(vm)
          val friendly = new Player(1, hand = List(card), board = List(wispCard))
          gs.friendlyPlayer = friendly
          val vote = CardPlayWithFriendlyTarget(1,1)
          validator.isValidVote(vote, false, true) shouldBe false
        }
      }


      "REQ_STEALTHED_TARGET - Shadow Sensei" - {

        "valid" in {
          val gs = new GameState()
          val vs = new VoteState()
          val ai = new VoteAI(vs, gs)
          val card = new Card("Shadow Sensei", 1, 1, Constants.INT_UNINIT, 1, "CFM_694", cardInfo = gs.getCardInfo("CFM_694"))
          val wispCard = new Card("Wisp", 11, Constants.INT_UNINIT, 1, 1, "CS2_231", cardInfo = gs.getCardInfo("CS2_231"))
          val stealthedCard = new Card("Mini-Mage", 12, Constants.INT_UNINIT, 2, 1, "GVG_109", cardInfo = gs.getCardInfo("GVG_109"), isStealthed = true)
          val ircState = new IRCState()
          val validator = new VoteValidator(gs)
          val vm = new VoteManager(gs, vs, ai, ircState, validator)
          val ircBot = new IRCBot(vm)
          val friendly = new Player(1, hand = List(card), board = List(wispCard, stealthedCard))
          gs.friendlyPlayer = friendly
          ircBot.onMessage("None", "A Twitch User", "None", "None", "!c1>f2")
          vs.voterMap("A Twitch User").actionVoteList shouldBe List(CardPlayWithFriendlyTarget(1, 2))
          vs.voterMap("A Twitch User").invalidVoteList shouldBe Nil
        }

        "invalid" in {
          val gs = new GameState()
          val vs = new VoteState()
          val ai = new VoteAI(vs, gs)
          val card = new Card("Shadow Sensei", 1, 1, Constants.INT_UNINIT, 1, "CFM_694", cardInfo = gs.getCardInfo("CFM_694"))
          val wispCard = new Card("Wisp", 11, Constants.INT_UNINIT, 1, 1, "CS2_231", cardInfo = gs.getCardInfo("CS2_231"))
          val stealthedCard = new Card("Mini-Mage", 12, Constants.INT_UNINIT, 2, 1, "GVG_109", cardInfo = gs.getCardInfo("GVG_109"))
          val ircState = new IRCState()
          val validator = new VoteValidator(gs)
          val vm = new VoteManager(gs, vs, ai, ircState, validator)
          val ircBot = new IRCBot(vm)
          val friendly = new Player(1, hand = List(card), board = List(wispCard))
          gs.friendlyPlayer = friendly
          val vote = CardPlayWithFriendlyTarget(1,1)
          validator.isValidVote(vote, false, true) shouldBe false
        }
      }

      "REQ_DAMAGED_TARGET - Execute" - {

        "valid" in {
          val gs = new GameState()
          val vs = new VoteState()
          val ai = new VoteAI(vs, gs)
          val card = new Card("Execute", 1, 1, Constants.INT_UNINIT, 1, "CS2_108", cardInfo = gs.getCardInfo("CS2_108"))
          val damagedCard = new Card("Hungry Dragon", 32, Constants.INT_UNINIT, 1, 2, "BRM_026", isDamaged = true, cardInfo = gs.getCardInfo("BRM_026"))
          val friendly = new Player(1, hand = List(card))
          val enemy = new Player(2, board = List(damagedCard))
          val ircState = new IRCState()
          val validator = new VoteValidator(gs)
          val vm = new VoteManager(gs, vs, ai, ircState, validator)
          val ircBot = new IRCBot(vm)
          gs.friendlyPlayer = friendly
          gs.enemyPlayer = enemy
          ircBot.onMessage("None", "A Twitch User", "None", "None", "!c1>e1")
          vs.voterMap("A Twitch User").actionVoteList shouldBe List(CardPlayWithEnemyTarget(1, 1))
          vs.voterMap("A Twitch User").invalidVoteList shouldBe Nil
        }

        "invalid" in {
          val gs = new GameState()
          val vs = new VoteState()
          val ai = new VoteAI(vs, gs)
          val card = new Card("Execute", 1, 1, Constants.INT_UNINIT, 1, "CS2_108", cardInfo = gs.getCardInfo("CS2_108"))
          val nondamagedCard = new Card("Hungry Dragon", 32, Constants.INT_UNINIT, 1, 2, "BRM_026", cardInfo = gs.getCardInfo("BRM_026"))
          val friendly = new Player(1, hand = List(card), board = List(nondamagedCard))
          val enemy = new Player(2, board = List(nondamagedCard))
          val ircState = new IRCState()
          val validator = new VoteValidator(gs)
          val vm = new VoteManager(gs, vs, ai, ircState, validator)
          val ircBot = new IRCBot(vm)
          gs.friendlyPlayer = friendly
          gs.enemyPlayer = enemy
          val vote = CardPlayWithEnemyTarget(1,1)
          validator.isValidVote(vote, false, true) shouldBe false
        }
      }


      "REQ_FROZEN_TARGET - Shatter" - {

        "valid" in {
          val gs = new GameState()
          val vs = new VoteState()
          val ai = new VoteAI(vs, gs)
          val card = new Card("Shatter", 1, 1, Constants.INT_UNINIT, 1, "OG_081", cardInfo = gs.getCardInfo("OG_081"))
          val frozenCard = new Card("Wisp", 11, Constants.INT_UNINIT, 1, 1, "CS2_231", isFrozen = true, cardInfo = gs.getCardInfo("CS2_231"))
          val friendly = new Player(1, hand = List(card), board = List(frozenCard))
          val ircState = new IRCState()
          val validator = new VoteValidator(gs)
          val vm = new VoteManager(gs, vs, ai, ircState, validator)
          val ircBot = new IRCBot(vm)
          gs.friendlyPlayer = friendly
          ircBot.onMessage("None", "A Twitch User", "None", "None", "!c1>f1")
          vs.voterMap("A Twitch User").actionVoteList shouldBe List(CardPlayWithFriendlyTarget(1, 1))
          vs.voterMap("A Twitch User").invalidVoteList shouldBe Nil
        }

        "invalid" in {
          val gs = new GameState()
          val vs = new VoteState()
          val ai = new VoteAI(vs, gs)
          val card = new Card("Shatter", 1, 1, Constants.INT_UNINIT, 1, "OG_081", cardInfo = gs.getCardInfo("OG_081"))
          val nonfrozenCard = new Card("Wisp", 11, Constants.INT_UNINIT, 1, 1, "CS2_231", cardInfo = gs.getCardInfo("CS2_231"))
          val friendly = new Player(1, hand = List(card), board = List(nonfrozenCard))
          val ircState = new IRCState()
          val validator = new VoteValidator(gs)
          val vm = new VoteManager(gs, vs, ai, ircState, validator)
          val ircBot = new IRCBot(vm)
          gs.friendlyPlayer = friendly
          val vote = CardPlayWithFriendlyTarget(1,1)
          validator.isValidVote(vote, false, true) shouldBe false
        }
      }

      "REQ_UNDAMAGED_TARGET - Shadow Strike" - {

        "valid" in {
          val gs = new GameState()
          val vs = new VoteState()
          val ai = new VoteAI(vs, gs)
          val card = new Card("Shadow Strike", 1, 1, Constants.INT_UNINIT, 1, "OG_176", cardInfo = gs.getCardInfo("OG_176"))
          val nondamagedCard = new Card("Hungry Dragon", 32, Constants.INT_UNINIT, 1, 2, "BRM_026", cardInfo = gs.getCardInfo("BRM_026"))
          val friendly = new Player(1, hand = List(card))
          val enemy = new Player(2, board = List(nondamagedCard))
          gs.friendlyPlayer = friendly
          gs.enemyPlayer = enemy
          val ircState = new IRCState()
          val validator = new VoteValidator(gs)
          val vm = new VoteManager(gs, vs, ai, ircState, validator)
          val ircBot = new IRCBot(vm)
          ircBot.onMessage("None", "A Twitch User", "None", "None", "!c1>e1")
          vs.voterMap("A Twitch User").actionVoteList shouldBe List(CardPlayWithEnemyTarget(1, 1))
          vs.voterMap("A Twitch User").invalidVoteList shouldBe Nil
        }

        "invalid" in {
          val gs = new GameState()
          val vs = new VoteState()
          val ai = new VoteAI(vs, gs)
          val card = new Card("Shadow Strike", 1, 1, Constants.INT_UNINIT, 1, "OG_176", cardInfo = gs.getCardInfo("OG_176"))
          val damagedCard = new Card("Hungry Dragon", 32, Constants.INT_UNINIT, 1, 2, "BRM_026", isDamaged = true, cardInfo = gs.getCardInfo("BRM_026"))
          val friendly = new Player(1, hand = List(card))
          val enemy = new Player(2, board = List(damagedCard))
          gs.friendlyPlayer = friendly
          gs.enemyPlayer = enemy
          val ircState = new IRCState()
          val validator = new VoteValidator(gs)
          val vm = new VoteManager(gs, vs, ai, ircState, validator)
          val ircBot = new IRCBot(vm)
          val vote = CardPlayWithEnemyTarget(1,1)
          validator.isValidVote(vote, false, true) shouldBe false
        }
      }

      "REQ_WEAPON_EQUIPPED - Blade Flurry" - {

        "valid" in {
          val gs = new GameState()
          val vs = new VoteState()
          val ai = new VoteAI(vs, gs)
          val card = new Card("Blade Flurry", 1, 1, Constants.INT_UNINIT, 1, "CS2_233", cardInfo = gs.getCardInfo("CS2_233"))
          val friendly = new Player(1, hand = List(card), isWeaponEquipped = true)
          gs.friendlyPlayer = friendly
          val ircState = new IRCState()
          val validator = new VoteValidator(gs)
          val vm = new VoteManager(gs, vs, ai, ircState, validator)
          val ircBot = new IRCBot(vm)
          ircBot.onMessage("None", "A Twitch User", "None", "None", "!c1")
          vs.voterMap("A Twitch User").actionVoteList shouldBe List(CardPlay(1))
          vs.voterMap("A Twitch User").invalidVoteList shouldBe Nil
        }

        "invalid" in {
          val gs = new GameState()
          val vs = new VoteState()
          val ai = new VoteAI(vs, gs)
          val card = new Card("Blade Flurry", 1, 1, Constants.INT_UNINIT, 1, "CS2_233", cardInfo = gs.getCardInfo("CS2_233"))
          val friendly = new Player(1, hand = List(card))
          gs.friendlyPlayer = friendly
          val ircState = new IRCState()
          val validator = new VoteValidator(gs)
          val vm = new VoteManager(gs, vs, ai, ircState, validator)
          val ircBot = new IRCBot(vm)
          val vote = CardPlay(1)
          validator.isValidVote(vote, false, true) shouldBe false
        }
      }

      "REQ_TARGET_FOR_COMBO - SI:7 Agent" - {

        "valid" in {
          val gs = new GameState()
          val vs = new VoteState()
          val ai = new VoteAI(vs, gs)
          val card = new Card("SI:7 Agent", 1, 1, Constants.INT_UNINIT, 1, "EX1_134", cardInfo = gs.getCardInfo("EX1_134"))
          val wispCard = new Card("Wisp", 11, Constants.INT_UNINIT, 1, 1, "CS2_231", cardInfo = gs.getCardInfo("CS2_231"))
          val friendly = new Player(1, hand = List(card), board = List(wispCard), isComboActive = true)
          gs.friendlyPlayer = friendly
          val ircState = new IRCState()
          val validator = new VoteValidator(gs)
          val vm = new VoteManager(gs, vs, ai, ircState, validator)
          val ircBot = new IRCBot(vm)
          ircBot.onMessage("None", "A Twitch User", "None", "None", "!c1>f1")
          vs.voterMap("A Twitch User").actionVoteList shouldBe List(CardPlayWithFriendlyTarget(1, 1))
          vs.voterMap("A Twitch User").invalidVoteList shouldBe Nil
        }

        "invalid" in {
          val gs = new GameState()
          val vs = new VoteState()
          val ai = new VoteAI(vs, gs)
          val card = new Card("SI:7 Agent", 1, 1, Constants.INT_UNINIT, 1, "EX1_134", cardInfo = gs.getCardInfo("EX1_134"))
          val wispCard = new Card("Wisp", 11, Constants.INT_UNINIT, 1, 1, "CS2_231", cardInfo = gs.getCardInfo("CS2_231"))
          val friendly = new Player(1, hand = List(card), board = List(wispCard))
          gs.friendlyPlayer = friendly
          val ircState = new IRCState()
          val validator = new VoteValidator(gs)
          val vm = new VoteManager(gs, vs, ai, ircState, validator)
          val ircBot = new IRCBot(vm)
          val vote = CardPlayWithFriendlyTarget(1,1)
          validator.isValidVote(vote, false, true) shouldBe false
        }
      }



      "REQ_MUST_TARGET_TAUNTER - The Black Knight" - {

        "valid" in {
          val gs = new GameState()
          val vs = new VoteState()
          val ai = new VoteAI(vs, gs)
          val card = new Card("The Black Knight", 1, 1, Constants.INT_UNINIT, 1, "EX1_002", cardInfo = gs.getCardInfo("EX1_002"))
          val tauntCard = new Card("Lil' Exorcist", 31, Constants.INT_UNINIT, 1, 2, "GVG_097", cardInfo = gs.getCardInfo("GVG_097"), isTaunt = Some(true))
          val friendly = new Player(1, hand = List(card))
          val enemy = new Player(2, board = List(tauntCard))
          val ircState = new IRCState()
          val validator = new VoteValidator(gs)
          val vm = new VoteManager(gs, vs, ai, ircState, validator)
          val ircBot = new IRCBot(vm)
          gs.friendlyPlayer = friendly
          gs.enemyPlayer = enemy
          ircBot.onMessage("None", "A Twitch User", "None", "None", "!c1>e1")
          vs.voterMap("A Twitch User").actionVoteList shouldBe List(CardPlayWithEnemyTarget(1, 1))
          vs.voterMap("A Twitch User").invalidVoteList shouldBe Nil
        }

        "invalid" in {
          val gs = new GameState()
          val vs = new VoteState()
          val ai = new VoteAI(vs, gs)
          val card = new Card("The Black Knight", 1, 1, Constants.INT_UNINIT, 1, "EX1_002", cardInfo = gs.getCardInfo("EX1_002"))
          val wispCard = new Card("Wisp", 31, Constants.INT_UNINIT, 1, 2, "CS2_231", cardInfo = gs.getCardInfo("CS2_231"))
          val friendly = new Player(1, hand = List(card))
          val enemy = new Player(2, board = List(wispCard))
          val ircState = new IRCState()
          val validator = new VoteValidator(gs)
          val vm = new VoteManager(gs, vs, ai, ircState, validator)
          val ircBot = new IRCBot(vm)
          gs.friendlyPlayer = friendly
          gs.enemyPlayer = enemy
          val vote = CardPlayWithEnemyTarget(1,1)
          validator.isValidVote(vote, false, true) shouldBe false
        }
      }

      "REQ_TARGET_WITH_DEATHRATTLE - Unearthed Raptor" - {

        "valid" in {
          val gs = new GameState()
          val vs = new VoteState()
          val ai = new VoteAI(vs, gs)
          val card = new Card("Unearthed Raptor", 1, 1, Constants.INT_UNINIT, 1, "LOE_019", cardInfo = gs.getCardInfo("LOE_019"))
          val deathrattleCard = new Card("Dreedstead", 11, Constants.INT_UNINIT, 1, 1, "AT_019", cardInfo = gs.getCardInfo("AT_019"), isDeathrattle = Some(true))
          val friendly = new Player(1, hand = List(card), board = List(deathrattleCard))
          gs.friendlyPlayer = friendly
          val ircState = new IRCState()
          val validator = new VoteValidator(gs)
          val vm = new VoteManager(gs, vs, ai, ircState, validator)
          val ircBot = new IRCBot(vm)
          ircBot.onMessage("None", "A Twitch User", "None", "None", "!c1>f1")
          vs.voterMap("A Twitch User").actionVoteList shouldBe List(CardPlayWithFriendlyTarget(1, 1))
          vs.voterMap("A Twitch User").invalidVoteList shouldBe Nil
        }

        "invalid" in {
          val gs = new GameState()
          val vs = new VoteState()
          val ai = new VoteAI(vs, gs)
          val card = new Card("Unearthed Raptor", 1, 1, Constants.INT_UNINIT, 1, "LOE_019", cardInfo = gs.getCardInfo("LOE_019"))
          val wispCard = new Card("Wisp", 11, Constants.INT_UNINIT, 1, 1, "CS2_231", cardInfo = gs.getCardInfo("CS2_231"))
          val friendly = new Player(1, hand = List(card), board = List(wispCard))
          gs.friendlyPlayer = friendly
          val ircState = new IRCState()
          val validator = new VoteValidator(gs)
          val vm = new VoteManager(gs, vs, ai, ircState, validator)
          val ircBot = new IRCBot(vm)
          val vote = CardPlayWithFriendlyTarget(1,1)
          validator.isValidVote(vote, false, true) shouldBe false
        }
      }


      "REQ_ENEMY_TARGET - Aldor Peacekeeper" - {

        "valid" in {
          val gs = new GameState()
          val vs = new VoteState()
          val ai = new VoteAI(vs, gs)
          val card = new Card("Aldor Peacekeeper", 1, 1, Constants.INT_UNINIT, 1, "EX1_382", cardInfo = gs.getCardInfo("EX1_382"))
          val enemyWispCard = new Card("Wisp", 31, Constants.INT_UNINIT, 1, 2, "CS2_231", cardInfo = gs.getCardInfo("CS2_231"))
          val friendly = new Player(1, hand = List(card))
          val enemy = new Player(2, board = List(enemyWispCard))
          val ircState = new IRCState()
          val validator = new VoteValidator(gs)
          val vm = new VoteManager(gs, vs, ai, ircState, validator)
          val ircBot = new IRCBot(vm)
          gs.friendlyPlayer = friendly
          gs.enemyPlayer = enemy
          ircBot.onMessage("None", "A Twitch User", "None", "None", "!c1>e1")
          vs.voterMap("A Twitch User").actionVoteList shouldBe List(CardPlayWithEnemyTarget(1, 1))
          vs.voterMap("A Twitch User").invalidVoteList shouldBe Nil
        }

        "invalid" in {
          val gs = new GameState()
          val vs = new VoteState()
          val ai = new VoteAI(vs, gs)
          val card = new Card("Aldor Peacekeeper", 1, 1, Constants.INT_UNINIT, 1, "EX1_382", cardInfo = gs.getCardInfo("EX1_382"))
          val friendlyWispCard = new Card("Wisp", 11, Constants.INT_UNINIT, 1, 1, "CS2_231", cardInfo = gs.getCardInfo("CS2_231"))
          val friendly = new Player(1, hand = List(card), board = List(friendlyWispCard))
          val ircState = new IRCState()
          val validator = new VoteValidator(gs)
          val vm = new VoteManager(gs, vs, ai, ircState, validator)
          val ircBot = new IRCBot(vm)
          gs.friendlyPlayer = friendly
          val vote = CardPlayWithFriendlyTarget(1,1)
          validator.isValidVote(vote, false, true) shouldBe false
        }
      }

      "REQ_HERO_TARGET - Alexstrasza" - {

        "valid" in {
          val gs = new GameState()
          val vs = new VoteState()
          val ai = new VoteAI(vs, gs)
          val card = new Card("Alexstrasza", 1, 1, Constants.INT_UNINIT, 1, "EX1_561", cardInfo = gs.getCardInfo("EX1_561"))
          val heroCard = Card("Rexxar", 0, Constants.INT_UNINIT, 0, 1, "HERO_05", cardInfo = gs.getCardInfo("HERO_05"))
          val friendly = new Player(1, hand = List(card), hero = Some(heroCard))
          gs.friendlyPlayer = friendly
          val ircState = new IRCState()
          val validator = new VoteValidator(gs)
          val vm = new VoteManager(gs, vs, ai, ircState, validator)
          val ircBot = new IRCBot(vm)
          ircBot.onMessage("None", "A Twitch User", "None", "None", "!c1>f0")
          vs.voterMap("A Twitch User").actionVoteList shouldBe List(CardPlayWithFriendlyTarget(1, 0))
          vs.voterMap("A Twitch User").invalidVoteList shouldBe Nil
        }

        "invalid" in {
          val gs = new GameState()
          val vs = new VoteState()
          val ai = new VoteAI(vs, gs)
          val card = new Card("Alexstrasza", 1, 1, Constants.INT_UNINIT, 1, "EX1_561", cardInfo = gs.getCardInfo("EX1_561"))
          val wispCard = new Card("Wisp", 31, Constants.INT_UNINIT, 1, 2, "CS2_231", cardInfo = gs.getCardInfo("CS2_231"))
          val friendly = new Player(1, hand = List(card))
          val enemy = new Player(2, board = List(wispCard))
          gs.friendlyPlayer = friendly
          gs.enemyPlayer = enemy
          val ircState = new IRCState()
          val validator = new VoteValidator(gs)
          val vm = new VoteManager(gs, vs, ai, ircState, validator)
          val ircBot = new IRCBot(vm)
          val vote = CardPlayWithEnemyTarget(1,1)
          validator.isValidVote(vote, false, true) shouldBe false
        }
      }


      "REQ_TARGET_IF_AVAILABLE_AND_MINIMUM_FRIENDLY_MINIONS - Gormok the Impaler" - {

        "valid" in {

          val gs = new GameState()
          val vs = new VoteState()
          val ai = new VoteAI(vs, gs)
          val card = new Card("Gormok the Impaler", 1, 1, Constants.INT_UNINIT, 1, "AT_122", cardInfo = gs.getCardInfo("AT_122"))
          val friendlyCard1 = new Card("Wisp", 11, Constants.INT_UNINIT, 1, 1, "CS2_231", cardInfo = gs.getCardInfo("CS2_231"))
          val friendlyCard2 = new Card("Wisp", 12, Constants.INT_UNINIT, 2, 1, "CS2_231", cardInfo = gs.getCardInfo("CS2_231"))
          val friendlyCard3 = new Card("Wisp", 13, Constants.INT_UNINIT, 3, 1, "CS2_231", cardInfo = gs.getCardInfo("CS2_231"))
          val friendlyCard4 = new Card("Wisp", 14, Constants.INT_UNINIT, 4, 1, "CS2_231", cardInfo = gs.getCardInfo("CS2_231"))
          val friendly = new Player(1, hand = List(card), board = List(friendlyCard1, friendlyCard2, friendlyCard3, friendlyCard4))
          val ircState = new IRCState()
          val validator = new VoteValidator(gs)
          val vm = new VoteManager(gs, vs, ai, ircState, validator)
          val ircBot = new IRCBot(vm)
          gs.friendlyPlayer = friendly
          ircBot.onMessage("None", "A Twitch User", "None", "None", "!c1>f1")
          vs.voterMap("A Twitch User").actionVoteList shouldBe List(CardPlayWithFriendlyTarget(1, 1))
          vs.voterMap("A Twitch User").invalidVoteList shouldBe Nil
        }

        "invalid" in {
          val gs = new GameState()
          val vs = new VoteState()
          val ai = new VoteAI(vs, gs)
          val card = new Card("Gormok the Impaler", 1, 1, Constants.INT_UNINIT, 1, "AT_122", cardInfo = gs.getCardInfo("AT_122"))
          val friendlyCard1 = new Card("Wisp", 11, Constants.INT_UNINIT, 1, 1, "CS2_231", cardInfo = gs.getCardInfo("CS2_231"))
          val friendly = new Player(1, hand = List(card), board = List(friendlyCard1))
          val ircState = new IRCState()
          val validator = new VoteValidator(gs)
          val vm = new VoteManager(gs, vs, ai, ircState, validator)
          val ircBot = new IRCBot(vm)
          gs.friendlyPlayer = friendly
          val vote = CardPlayWithFriendlyTarget(1,1)
          validator.isValidVote(vote, false, true) shouldBe false
        }
      }


      "REQ_NUM_MINION_SLOTS - Animal Companion" - {


        "valid" in {
          val gs = new GameState()
          val vs = new VoteState()
          val ai = new VoteAI(vs, gs)
          val card = new Card("Animal Companion", 1, 1, Constants.INT_UNINIT, 1, "NEW1_031", cardInfo = gs.getCardInfo("NEW1_031"))
          val friendly = new Player(1, hand = List(card))
          val ircState = new IRCState()
          val validator = new VoteValidator(gs)
          val vm = new VoteManager(gs, vs, ai, ircState, validator)
          val ircBot = new IRCBot(vm)
          gs.friendlyPlayer = friendly
          ircBot.onMessage("None", "A Twitch User", "None", "None", "!c1")
          vs.voterMap("A Twitch User").actionVoteList shouldBe List(CardPlay(1))
          vs.voterMap("A Twitch User").invalidVoteList shouldBe Nil
        }

        "invalid" in {
          val gs = new GameState()
          val vs = new VoteState()
          val ai = new VoteAI(vs, gs)
          val card = new Card("Animal Companion", 1, 1, Constants.INT_UNINIT, 1, "NEW1_031", cardInfo = gs.getCardInfo("NEW1_031"))
          val friendlyCard1 = new Card("Wisp", 11, Constants.INT_UNINIT, 1, 1, "CS2_231", cardInfo = gs.getCardInfo("CS2_231"))
          val friendlyCard2 = new Card("Wisp", 12, Constants.INT_UNINIT, 2, 1, "CS2_231", cardInfo = gs.getCardInfo("CS2_231"))
          val friendlyCard3 = new Card("Wisp", 13, Constants.INT_UNINIT, 3, 1, "CS2_231", cardInfo = gs.getCardInfo("CS2_231"))
          val friendlyCard4 = new Card("Wisp", 14, Constants.INT_UNINIT, 4, 1, "CS2_231", cardInfo = gs.getCardInfo("CS2_231"))
          val friendlyCard5 = new Card("Wisp", 15, Constants.INT_UNINIT, 5, 1, "CS2_231", cardInfo = gs.getCardInfo("CS2_231"))
          val friendlyCard6 = new Card("Wisp", 16, Constants.INT_UNINIT, 6, 1, "CS2_231", cardInfo = gs.getCardInfo("CS2_231"))
          val friendlyCard7 = new Card("Wisp", 17, Constants.INT_UNINIT, 7, 1, "CS2_231", cardInfo = gs.getCardInfo("CS2_231"))
          val friendly = new Player(1, hand = List(card), board = List(friendlyCard1, friendlyCard2, friendlyCard3, friendlyCard4, friendlyCard5, friendlyCard6, friendlyCard7))
          val ircState = new IRCState()
          val validator = new VoteValidator(gs)
          val vm = new VoteManager(gs, vs, ai, ircState, validator)
          val ircBot = new IRCBot(vm)
          gs.friendlyPlayer = friendly
          val vote = CardPlay(1)
          validator.isValidVote(vote, false, true) shouldBe false
        }
      }

      "REQ_ENTIRE_ENTOURAGE_NOT_IN_PLAY - Totemic Call" - {

        "valid" in {
          val gs = new GameState()
          val vs = new VoteState()
          val ai = new VoteAI(vs, gs)
          val hp = new Card("Totemic Call", 0, 0, Constants.INT_UNINIT, 1, "CS2_049", cardInfo = gs.getCardInfo("CS2_049"))
          val friendlyCard1 = new Card("Searing Totem", 11, Constants.INT_UNINIT, 1, 1, "CS2_050", cardInfo = gs.getCardInfo("CS2_050"))
          val friendly = new Player(1, heroPower = Some(hp), board = List(friendlyCard1))
          val ircState = new IRCState()
          val validator = new VoteValidator(gs)
          val vm = new VoteManager(gs, vs, ai, ircState, validator)
          val ircBot = new IRCBot(vm)
          gs.friendlyPlayer = friendly
          ircBot.onMessage("None", "A Twitch User", "None", "None", "!hp")
          vs.voterMap("A Twitch User").actionVoteList shouldBe List(HeroPower())
          vs.voterMap("A Twitch User").invalidVoteList shouldBe Nil
        }

        "invalid" in {
          val gs = new GameState()
          val vs = new VoteState()
          val ai = new VoteAI(vs, gs)
          val hp = new Card("Totemic Call", 0, 0, Constants.INT_UNINIT, 1, "CS2_049", cardInfo = gs.getCardInfo("CS2_049"))
          val friendlyCard1 = new Card("Searing Totem", 11, Constants.INT_UNINIT, 1, 1, "CS2_050", cardInfo = gs.getCardInfo("CS2_050"))
          val friendlyCard2 = new Card("Stoneclaw Totem", 12, Constants.INT_UNINIT, 2, 1, "CS2_051", cardInfo = gs.getCardInfo("CS2_051"))
          val friendlyCard3 = new Card("Wrath of Air Totem", 13, Constants.INT_UNINIT, 3, 1, "CS2_052", cardInfo = gs.getCardInfo("CS2_052"))
          val friendlyCard4 = new Card("Healing Totem", 14, Constants.INT_UNINIT, 4, 1, "NEW1_009", cardInfo = gs.getCardInfo("NEW1_009"))
          val friendly = new Player(1, heroPower = Some(hp), board = List(friendlyCard1, friendlyCard2, friendlyCard3, friendlyCard4))
          val ircState = new IRCState()
          val validator = new VoteValidator(gs)
          val vm = new VoteManager(gs, vs, ai, ircState, validator)
          val ircBot = new IRCBot(vm)
          gs.friendlyPlayer = friendly
          val vote = HeroPower()
          validator.isValidVote(vote, false, true) shouldBe false
        }
      }

      "REQ_MINIMUM_ENEMY_MINIONS - Forked Lightning" - {

        "valid" in {
          val gs = new GameState()
          val vs = new VoteState()
          val ai = new VoteAI(vs, gs)
          val card = new Card("Forked Lightning", 1, 1, Constants.INT_UNINIT, 1, "EX1_251", cardInfo = gs.getCardInfo("EX1_251"))
          val card2 = new Card("Wisp", 31, Constants.INT_UNINIT, 1, 2, "CS2_231", cardInfo = gs.getCardInfo("CS2_231"))
          val card3 = new Card("Wisp", 32, Constants.INT_UNINIT, 2, 2, "CS2_231", cardInfo = gs.getCardInfo("CS2_231"))
          val friendly = new Player(1, hand = List(card))
          val enemy = new Player(2, board = List(card2, card3))
          val ircState = new IRCState()
          val validator = new VoteValidator(gs)
          val vm = new VoteManager(gs, vs, ai, ircState, validator)
          val ircBot = new IRCBot(vm)
          gs.friendlyPlayer = friendly
          gs.enemyPlayer = enemy
          ircBot.onMessage("None", "A Twitch User", "None", "None", "!c1")
          vs.voterMap("A Twitch User").actionVoteList shouldBe List(CardPlay(1))
          vs.voterMap("A Twitch User").invalidVoteList shouldBe Nil
        }

        "invalid" in {
          val gs = new GameState()
          val vs = new VoteState()
          val ai = new VoteAI(vs, gs)
          val card = new Card("Forked Lightning", 1, 1, Constants.INT_UNINIT, 1, "EX1_251", cardInfo = gs.getCardInfo("EX1_251"))
          val card2 = new Card("Wisp", 31, Constants.INT_UNINIT, 1, 2, "CS2_231", cardInfo = gs.getCardInfo("CS2_231"))
          val card3 = new Card("Wisp", 32, Constants.INT_UNINIT, 2, 2, "CS2_231", cardInfo = gs.getCardInfo("CS2_231"))
          val friendly = new Player(1, hand = List(card))
          val enemy = new Player(2, board = List(card2))
          val ircState = new IRCState()
          val validator = new VoteValidator(gs)
          val vm = new VoteManager(gs, vs, ai, ircState, validator)
          val ircBot = new IRCBot(vm)
          gs.friendlyPlayer = friendly
          gs.enemyPlayer = enemy
          val vote = CardPlay(1)
          validator.isValidVote(vote, false, true) shouldBe false
        }
      }

      "REQ_TARGET_WITH_RACE - Houndmaster" - {

        "valid" in {
          val gs = new GameState()
          val vs = new VoteState()
          val ai = new VoteAI(vs, gs)
          val card = new Card("Houndmaster", 1, 1, Constants.INT_UNINIT, 1, "DS1_070", cardInfo = gs.getCardInfo("DS1_070"))
          val beastCard = new Card("Tundra Rhino", 11, Constants.INT_UNINIT, 1, 1, "DS1_178", cardInfo = gs.getCardInfo("DS1_178"))
          val nonBeastCard = new Card("Wisp", 12, Constants.INT_UNINIT, 2, 1, "CS2_231", cardInfo = gs.getCardInfo("CS2_231"))
          val friendly = new Player(1, hand = List(card), board = List(beastCard))
          val ircState = new IRCState()
          val validator = new VoteValidator(gs)
          val vm = new VoteManager(gs, vs, ai, ircState, validator)
          val ircBot = new IRCBot(vm)
          gs.friendlyPlayer = friendly
          ircBot.onMessage("None", "A Twitch User", "None", "None", "!c1>f1")
          vs.voterMap("A Twitch User").actionVoteList shouldBe List(CardPlayWithFriendlyTarget(1, 1))
          vs.voterMap("A Twitch User").invalidVoteList shouldBe Nil
        }

        "invalid" in {
          val gs = new GameState()
          val vs = new VoteState()
          val ai = new VoteAI(vs, gs)
          val card = new Card("Houndmaster", 1, 1, Constants.INT_UNINIT, 1, "DS1_070", cardInfo = gs.getCardInfo("DS1_070"))
          val beastCard = new Card("Tundra Rhino", 11, Constants.INT_UNINIT, 1, 1, "DS1_178", cardInfo = gs.getCardInfo("DS1_178"))
          val nonBeastCard = new Card("Wisp", 12, Constants.INT_UNINIT, 1, 1, "CS2_231", cardInfo = gs.getCardInfo("CS2_231"))
          val friendly = new Player(1, hand = List(card), board = List(nonBeastCard))
          val ircState = new IRCState()
          val validator = new VoteValidator(gs)
          val vm = new VoteManager(gs, vs, ai, ircState, validator)
          val ircBot = new IRCBot(vm)
          gs.friendlyPlayer = friendly
          val vote = CardPlayWithFriendlyTarget(1,1)
          validator.isValidVote(vote, false, true) shouldBe false
        }
      }


      "REQ_TARGET_MAX_ATTACK - Shadow Word: Pain" - {


        "valid" in {
          val gs = new GameState()
          val vs = new VoteState()
          val ai = new VoteAI(vs, gs)
          val card = new Card("Shadow Word: Pain", 1, 1, Constants.INT_UNINIT, 1, "CS2_234", cardInfo = gs.getCardInfo("CS2_234"))
          val wispCard = new Card("Wisp", 11, Constants.INT_UNINIT, 1, 1, "CS2_231", cardInfo = gs.getCardInfo("CS2_231"), attack = Some(3))
          val psgCard = new Card("Piloted Sky Golem", 12, Constants.INT_UNINIT, 2, 1, "GVG_105", cardInfo = gs.getCardInfo("GVG_105"))
          val friendly = new Player(1, hand = List(card), board = List(wispCard, psgCard))
          val ircState = new IRCState()
          val validator = new VoteValidator(gs)
          val vm = new VoteManager(gs, vs, ai, ircState, validator)
          val ircBot = new IRCBot(vm)
          gs.friendlyPlayer = friendly
          ircBot.onMessage("None", "A Twitch User", "None", "None", "!c1>f1")
          vs.voterMap("A Twitch User").actionVoteList shouldBe List(CardPlayWithFriendlyTarget(1, 1))
          vs.voterMap("A Twitch User").invalidVoteList shouldBe Nil
        }

        "invalid" in {
          val gs = new GameState()
          val vs = new VoteState()
          val ai = new VoteAI(vs, gs)
          val card = new Card("Shadow Word: Pain", 1, 1, Constants.INT_UNINIT, 1, "CS2_234", cardInfo = gs.getCardInfo("CS2_234"))
          val wispCard = new Card("Wisp", 11, Constants.INT_UNINIT, 1, 1, "CS2_231", cardInfo = gs.getCardInfo("CS2_231"))
          val psgCard = new Card("Piloted Sky Golem", 12, Constants.INT_UNINIT, 2, 1, "GVG_105", cardInfo = gs.getCardInfo("GVG_105"))
          val friendly = new Player(1, hand = List(card), board = List(wispCard, psgCard))
          val ircState = new IRCState()
          val validator = new VoteValidator(gs)
          val vm = new VoteManager(gs, vs, ai, ircState, validator)
          val ircBot = new IRCBot(vm)
          gs.friendlyPlayer = friendly
          val vote = CardPlayWithFriendlyTarget(1,2)
          validator.isValidVote(vote, false, true) shouldBe false
        }
      }


      "REQ_FRIENDLY_TARGET - Lance Carrier" - {


        "valid" in {
          val gs = new GameState()
          val vs = new VoteState()
          val ai = new VoteAI(vs, gs)
          val card = new Card("Lance Carrier", 1, 1, Constants.INT_UNINIT, 1, "AT_084", cardInfo = gs.getCardInfo("AT_084"))
          val friendlyWispCard = new Card("Wisp", 11, Constants.INT_UNINIT, 1, 1, "CS2_231", cardInfo = gs.getCardInfo("CS2_231"))
          val enemyWispCard = new Card("Wisp", 31, Constants.INT_UNINIT, 1, 2, "CS2_231", cardInfo = gs.getCardInfo("CS2_231"))
          val friendly = new Player(1, hand = List(card), board = List(friendlyWispCard))
          gs.friendlyPlayer = friendly
          val ircState = new IRCState()
          val validator = new VoteValidator(gs)
          val vm = new VoteManager(gs, vs, ai, ircState, validator)
          val ircBot = new IRCBot(vm)
          ircBot.onMessage("None", "A Twitch User", "None", "None", "!c1>f1")
          vs.voterMap("A Twitch User").actionVoteList shouldBe List(CardPlayWithFriendlyTarget(1, 1))
          vs.voterMap("A Twitch User").invalidVoteList shouldBe Nil
        }

        "invalid" in {
          val gs = new GameState()
          val vs = new VoteState()
          val ai = new VoteAI(vs, gs)
          val card = new Card("Lance Carrier", 1, 1, Constants.INT_UNINIT, 1, "AT_084", cardInfo = gs.getCardInfo("AT_084"))
          val friendlyWispCard = new Card("Wisp", 11, Constants.INT_UNINIT, 1, 1, "CS2_231", cardInfo = gs.getCardInfo("CS2_231"))
          val enemyWispCard = new Card("Wisp", 31, Constants.INT_UNINIT, 1, 2, "CS2_231", cardInfo = gs.getCardInfo("CS2_231"))
          val friendly = new Player(1, hand = List(card))
          val enemy = new Player(2, board = List(enemyWispCard))
          gs.friendlyPlayer = friendly
          gs.enemyPlayer = enemy
          val ircState = new IRCState()
          val validator = new VoteValidator(gs)
          val vm = new VoteManager(gs, vs, ai, ircState, validator)
          val ircBot = new IRCBot(vm)
          val vote = CardPlayWithEnemyTarget(1,1)
          validator.isValidVote(vote, false, true) shouldBe false
        }
      }


      "REQ_TARGET_TO_PLAY - Lesser Heal" - {

        "valid" in {
          val gs = new GameState()
          val vs = new VoteState()
          val ai = new VoteAI(vs, gs)
          val hp = new Card("Lesser Heal", 0, 0, Constants.INT_UNINIT, 1, "CS1h_001", cardInfo = gs.getCardInfo("CS1h_001"))
          val friendlyWispCard = new Card("Wisp", 11, Constants.INT_UNINIT, 1, 1, "CS2_231", cardInfo = gs.getCardInfo("CS2_231"))
          val friendly = new Player(1, heroPower = Some(hp), board = List(friendlyWispCard))
          gs.friendlyPlayer = friendly
          val ircState = new IRCState()
          val validator = new VoteValidator(gs)
          val vm = new VoteManager(gs, vs, ai, ircState, validator)
          val ircBot = new IRCBot(vm)
          ircBot.onMessage("None", "A Twitch User", "None", "None", "!hp>f1")
          vs.voterMap("A Twitch User").actionVoteList shouldBe List(HeroPowerWithFriendlyTarget(1))
          vs.voterMap("A Twitch User").invalidVoteList shouldBe Nil
        }

        "invalid" in {
          val gs = new GameState()
          val vs = new VoteState()
          val ai = new VoteAI(vs, gs)
          val hp = new Card("Lesser Heal", 0, 0, Constants.INT_UNINIT, 1, "CS1h_001", cardInfo = gs.getCardInfo("CS1h_001"))
          val friendlyWispCard = new Card("Wisp", 11, Constants.INT_UNINIT, 1, 1, "CS2_231", cardInfo = gs.getCardInfo("CS2_231"))
          val friendly = new Player(1, heroPower = Some(hp))
          gs.friendlyPlayer = friendly
          val ircState = new IRCState()
          val validator = new VoteValidator(gs)
          val vm = new VoteManager(gs, vs, ai, ircState, validator)
          val ircBot = new IRCBot(vm)
          val vote = HeroPower()
          validator.isValidVote(vote, false, true) shouldBe false
        }
      }


      "REQ_MINION_TARGET - Lance Carrier" - {

        "valid" in {
          val gs = new GameState()
          val vs = new VoteState()
          val ai = new VoteAI(vs, gs)
          val card = new Card("Lance Carrier", 1, 1, Constants.INT_UNINIT, 1, "AT_084", cardInfo = gs.getCardInfo("AT_084"))
          val friendlyWispCard = new Card("Wisp", 11, Constants.INT_UNINIT, 1, 1, "CS2_231", cardInfo = gs.getCardInfo("CS2_231"))
          val friendly = new Player(1, hand = List(card), board = List(friendlyWispCard))
          gs.friendlyPlayer = friendly
          val ircState = new IRCState()
          val validator = new VoteValidator(gs)
          val vm = new VoteManager(gs, vs, ai, ircState, validator)
          val ircBot = new IRCBot(vm)
          ircBot.onMessage("None", "A Twitch User", "None", "None", "!c1>f1")
          vs.voterMap("A Twitch User").actionVoteList shouldBe List(CardPlayWithFriendlyTarget(1, 1))
          vs.voterMap("A Twitch User").invalidVoteList shouldBe Nil
        }

        "invalid" in {
          val gs = new GameState()
          val vs = new VoteState()
          val ai = new VoteAI(vs, gs)
          val card = new Card("Lance Carrier", 1, 1, Constants.INT_UNINIT, 1, "AT_084", cardInfo = gs.getCardInfo("AT_084"))
          val friendlyWispCard = new Card("Wisp", 11, Constants.INT_UNINIT, 1, 1, "CS2_231", cardInfo = gs.getCardInfo("CS2_231"))
          val heroCard = Card("Friendly Hero", 100, Constants.INT_UNINIT, 0, 1, "HERO_05a")
          val friendly = new Player(1, hand = List(card), hero = Some(heroCard))
          gs.friendlyPlayer = friendly
          val ircState = new IRCState()
          val validator = new VoteValidator(gs)
          val vm = new VoteManager(gs, vs, ai, ircState, validator)
          val ircBot = new IRCBot(vm)
          val vote = CardPlayWithFriendlyTarget(1,0)
          validator.isValidVote(vote, false, true) shouldBe false
        }
      }

      "REQ_MINIMUM_TOTAL_MINIONS - Brawl" - {

        "valid" in {
          val gs = new GameState()
          val vs = new VoteState()
          val ai = new VoteAI(vs, gs)
          val card = new Card("Brawl", 1, 1, Constants.INT_UNINIT, 1, "EX1_407", cardInfo = gs.getCardInfo("EX1_407"))
          val friendlyWispCard = new Card("Wisp", 11, Constants.INT_UNINIT, 1, 1, "CS2_231", cardInfo = gs.getCardInfo("CS2_231"))
          val friendlyWispCard2 = new Card("Wisp", 12, Constants.INT_UNINIT, 2, 1, "CS2_231", cardInfo = gs.getCardInfo("CS2_231"))
          val friendly = new Player(1, hand = List(card), board = List(friendlyWispCard, friendlyWispCard2))
          gs.friendlyPlayer = friendly
          val ircState = new IRCState()
          val validator = new VoteValidator(gs)
          val vm = new VoteManager(gs, vs, ai, ircState, validator)
          val ircBot = new IRCBot(vm)
          ircBot.onMessage("None", "A Twitch User", "None", "None", "!c1")
          vs.voterMap("A Twitch User").actionVoteList shouldBe List(CardPlay(1))
          vs.voterMap("A Twitch User").invalidVoteList shouldBe Nil
        }

        "invalid" in {
          val gs = new GameState()
          val vs = new VoteState()
          val ai = new VoteAI(vs, gs)
          val card = new Card("Brawl", 1, 1, Constants.INT_UNINIT, 1, "EX1_407", cardInfo = gs.getCardInfo("EX1_407"))
          val friendly = new Player(1, hand = List(card))
          gs.friendlyPlayer = friendly
          val ircState = new IRCState()
          val validator = new VoteValidator(gs)
          val vm = new VoteManager(gs, vs, ai, ircState, validator)
          val ircBot = new IRCBot(vm)
          val vote = CardPlay(1)
          validator.isValidVote(vote, false, true) shouldBe false
        }
      }
    }



  "Vote Manager should update "


  }
