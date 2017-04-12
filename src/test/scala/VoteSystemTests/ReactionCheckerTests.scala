//package VoteSystemTests
//
//import Logic.{Reaction, ReactionChecker}
//import org.scalatest.{FreeSpec, Matchers}
//import tph.Constants.ActionVotes.{NormalAttack, CardPlay, ActionVote}
//import tph._
//
//
////todo: Try to address the problem of only being able to look ahead 1 vote for reactions. Ex: A minion dies from attacking them a spell damaging it. Reaction checker won't detect that.
//
//
//
//class ReactionCheckerTests extends FreeSpec with Matchers {
//
//
//  "While card with reactive property is alive" - {
//
//    "When any friendly minion dies" - {
//
//      "From an attack vote" in {
//        val theBrain = new TheBrain
//        val rc = new ReactionChecker()
//        val friendlyMinion = Card("Wisp", 12, Constants.INT_UNINIT, 2, 1, "CS2_231", cardInfo = theBrain.gs.getCardInfo("CS2_231"), health = Some(2))
//        val conditionCard = Card("Cult Master", 11, Constants.INT_UNINIT, 1, 1, "EX1_595", cardInfo = theBrain.gs.getCardInfo("EX1_595"))
//        val highAttEnemyMinion = Card("Wisp", 31, Constants.INT_UNINIT, 1, 2, "CS2_231", cardInfo = theBrain.gs.getCardInfo("CS2_231"), attack = Some(3))
//        val lowAttEnemyMinion = Card("Wisp", 32, Constants.INT_UNINIT, 2, 2, "CS2_231", cardInfo = theBrain.gs.getCardInfo("CS2_231"), attack = Some(1))
//        theBrain.gs.friendlyPlayer = Player(1, board = List(conditionCard, friendlyMinion))
//        theBrain.gs.enemyPlayer = Player(2, board = List(highAttEnemyMinion, lowAttEnemyMinion))
//
//        val isReactive = rc.isVoteReactive(NormalAttack(2, 1), theBrain.gs)
//        val isNotReactive = rc.isVoteReactive(NormalAttack(2, 2), theBrain.gs)
//
//        isReactive shouldBe true
//        isNotReactive shouldBe false
//      }
//
//      "From a damaging spell or battlecry" in {
//        val theBrain = new TheBrain
//        val rc = new ReactionChecker()
//        val friendlyMinion = Card("Wisp", 12, Constants.INT_UNINIT, 2, 1, "CS2_231", cardInfo = theBrain.gs.getCardInfo("CS2_231"), health = Some(2))
//        val conditionCard = Card("Cult Master", 11, Constants.INT_UNINIT, 1, 1, "EX1_595", cardInfo = theBrain.gs.getCardInfo("EX1_595"))
//        val highAttEnemyMinion = Card("Wisp", 31, Constants.INT_UNINIT, 1, 2, "CS2_231", cardInfo = theBrain.gs.getCardInfo("CS2_231"), attack = Some(3))
//        val lowAttEnemyMinion = Card("Wisp", 32, Constants.INT_UNINIT, 2, 2, "CS2_231", cardInfo = theBrain.gs.getCardInfo("CS2_231"), attack = Some(1))
//        theBrain.gs.friendlyPlayer = Player(1, board = List(conditionCard, friendlyMinion))
//        theBrain.gs.enemyPlayer = Player(2, board = List(highAttEnemyMinion, lowAttEnemyMinion))
//
//        val isReactive = rc.isVoteReactive(NormalAttack(2, 1), theBrain.gs)
//        val isNotReactive = rc.isVoteReactive(NormalAttack(2, 2), theBrain.gs)
//
//        isReactive shouldBe true
//        isNotReactive shouldBe false
//
//
//      }
//    }
//
//
//    "When a spell is played(Gadgetzan Auctioneer)" - {
//
//      "While active (Gadgetzan Auctioneer)" in {
//
//        val theBrain = new TheBrain
//        val rc = new ReactionChecker()
//        val conditionCard = Card("Gadgetzan Auctioneer", 11, Constants.INT_UNINIT, 1, 1, "EX1_095", cardInfo = theBrain.gs.getCardInfo("EX1_095"))
//        val spell = Card("Flamestrike", 1, 1, Constants.INT_UNINIT, 1, "CS2_032", cardInfo = theBrain.gs.getCardInfo("CS2_032"))
//        val minion = Card("Wisp", 2, 2, Constants.INT_UNINIT, 1, "CS2_231", cardInfo = theBrain.gs.getCardInfo("CS2_231"))
//        theBrain.gs.friendlyPlayer = Player(1, hand = List(spell, minion), board = List(conditionCard))
//
//        val isReactive = rc.isVoteReactive(CardPlay(1), theBrain.gs)
//        val isNotReactive = rc.isVoteReactive(CardPlay(2), theBrain.gs)
//
//        isReactive shouldBe true
//        isNotReactive shouldBe false
//      }
//
//      "Does not detect reactive vote when Gadgetzan Auctioneer is in hand" in {
//
//        val theBrain = new TheBrain
//        val rc = new ReactionChecker()
//        val conditionCard = Card("Gadgetzan Auctioneer", 2, Constants.INT_UNINIT, 1, 1, "EX1_095", cardInfo = theBrain.gs.getCardInfo("EX1_095"))
//        val spell = Card("Flamestrike", 1, 1, Constants.INT_UNINIT, 1, "CS2_032", cardInfo = theBrain.gs.getCardInfo("CS2_032"))
//        val minion = Card("Wisp", 2, 2, Constants.INT_UNINIT, 1, "CS2_231", cardInfo = theBrain.gs.getCardInfo("CS2_231"))
//        theBrain.gs.friendlyPlayer = Player(1, hand = List(spell, minion, conditionCard))
//
//        val isNotReactive = rc.isVoteReactive(CardPlay(1), theBrain.gs)
//
//        isNotReactive shouldBe false
//      }
//    }
//  }
//
//
//
//  "A Reaction Checker should detect reactive votes" - {
//
//
//
//
//
//
//
//
//
//
//    "Reactive attack votes" - {
//
//      "Source Attacks and survives (Wind-Up BurgleBot)" in {
//        val theBrain = new TheBrain
//        val rc = new ReactionChecker()
//        val conditionCard = Card("Wind-Up BurgleBot", 11, Constants.INT_UNINIT, 1, 1, "CFM_025", cardInfo = theBrain.gs.getCardInfo("CFM_025"), health = Some(2))
//        val lowAttMinion = Card("Wisp", 31, Constants.INT_UNINIT, 1, 2, "CS2_231", cardInfo = theBrain.gs.getCardInfo("CS2_231"), attack = Some(1))
//        val highAttMinion = Card("Wisp", 32, Constants.INT_UNINIT, 2, 2, "CS2_231", cardInfo = theBrain.gs.getCardInfo("CS2_231"), attack = Some(3))
//        theBrain.gs.friendlyPlayer = Player(1, board = List(conditionCard))
//        theBrain.gs.enemyPlayer = Player(2, board = List(lowAttMinion, highAttMinion))
//
//        val isReactive = rc.isVoteReactive(NormalAttack(1,1), theBrain.gs)
//        val isNotReactive = rc.isVoteReactive(NormalAttack(1,2), theBrain.gs)
//
//        isReactive shouldBe true
//        isNotReactive shouldBe false
//      }
//
//      "Source Attacks" in {
//        val theBrain = new TheBrain
//        val rc = new ReactionChecker()
//        val fillerFriendlyMinion = Card("Wisp", 1, 1, Constants.INT_UNINIT, 1, "CS2_231", cardInfo = theBrain.gs.getCardInfo("CS2_231"))
//        val conditionCard = Card("Genzo, the Shark", 11, Constants.INT_UNINIT, 1, 1, "CFM_808", cardInfo = theBrain.gs.getCardInfo("CFM_808"))
//        val enemyMinion = Card("Wisp", 31, Constants.INT_UNINIT, 1, 2, "CS2_231", cardInfo = theBrain.gs.getCardInfo("CS2_231"))
//        theBrain.gs.friendlyPlayer = Player(1, board = List(conditionCard))
//        theBrain.gs.enemyPlayer = Player(2, board = List(enemyMinion))
//
//        val isReactive = rc.isVoteReactive(NormalAttack(1,1), theBrain.gs)
//        val isNotReactive = rc.isVoteReactive(CardPlay(1), theBrain.gs)
//
//        isReactive shouldBe true
//        isNotReactive shouldBe false
//      }
//    }
//  }
//}