//package Logic
//
//import tph.Constants.ActionVotes.{AttackType, CardPlayType, ActionVote}
//import tph.{Card, GameState, HSCard}
//
//import scala.collection.mutable.ListBuffer
//
///**
//  * Created by Harambe on 3/31/2017.
//  */
//case class Reaction(isReactionTriggered: (ActionVote, HSCard, HSCard) => Boolean)
//
//class ReactionChecker {
//
//
//  val reactionBoardMap = Map[String, Reaction](
//    "EX1_095" -> Reaction(ifSpellPlayed _),
//    "CFM_025" -> Reaction(ifSourceAttacksAndSurvives _),
//    "CFM_808" -> Reaction(ifSourceAttacks _),
//    "EX1_595" -> Reaction(ifFriendlyMinionDies _)
//  )
//
//  val reactionHandMap = Map[String, Reaction](
//  )
//
//
//
//  def ifFriendlyMinionDies(decision: ActionVote, source:HSCard, target:HSCard):Boolean = {
//    decision match{
//      case attackVote:AttackType =>
//        source.health.getOrElse(-1) < target.attack.getOrElse(0)
//
//      case _ => false
//        }
//    }
//
//
//  def ifSpellPlayed(decision: ActionVote, source:HSCard, target:HSCard):Boolean = {
//    decision match {
//      case vote:CardPlayType =>
//        source.cardInfo.cardType.getOrElse("None") == "SPELL"
//      case _ =>
//        false
//    }
//  }
//
//
//  def ifSourceAttacksAndSurvives(decision:ActionVote, source:HSCard, target:HSCard):Boolean = {
//    decision match {
//      case vote:AttackType =>
//        source.health.getOrElse(-1) > target.attack.getOrElse(0)
//      case _ =>
//        false
//    }
//  }
//
//
//  def ifSourceAttacks(decision:ActionVote, source:HSCard, target:HSCard):Boolean = {
//    decision match {
//      case vote:AttackType =>
//        source match {
//          case validSource:Card =>
//            true
//          case _ =>
//            false
//        }
//      case _ =>
//        false
//    }
//  }
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
//
//
//
//
//  def isVoteReactive(vote:ActionVote, gs:GameState):Boolean = {
//    var hasTriggered = false
//
//    val activeReactions = findActiveReactions(gs)
//    activeReactions.foreach{ case reaction =>
//      if(!hasTriggered) {
//        val st = gs.getSourceAndTarget(vote)
//        hasTriggered = reaction.isReactionTriggered(vote, st._1, st._2)
//      }
//    }
//    hasTriggered
//  }
//
//
//
//  def findActiveReactions(gs:GameState): List[Reaction] = {
//
//    val possibleReactions = ListBuffer[Reaction]()
//
//    gs.friendlyPlayer.board.foreach{case card =>
//        if(reactionBoardMap.isDefinedAt(card.cardID)){
//          possibleReactions.append(reactionBoardMap(card.cardID))
//        }
//
//        if(reactionBoardMap.isDefinedAt(card.enhancementID.getOrElse("None"))){
//          possibleReactions.append(reactionBoardMap(card.enhancementID.get))
//        }
//    }
//
//    gs.friendlyPlayer.hand.foreach{case card =>
//      if(reactionHandMap.isDefinedAt(card.cardID)){
//        possibleReactions.append(reactionHandMap(card.cardID))
//      }
//
//      if(reactionHandMap.isDefinedAt(card.enhancementID.getOrElse("None"))){
//        possibleReactions.append(reactionHandMap(card.enhancementID.get))
//      }
//    }
//
//
//
//    possibleReactions.toList
//  }
//
//
//
//}
