package VoteSystem

import javax.swing.ActionMap

import Logic.IRCState
import com.typesafe.scalalogging.LazyLogging
import tph.Constants.ActionVotes._
import tph.Constants.EmojiVotes.EmojiVote
import tph.Constants.Vote
import tph._

import scala.collection.mutable.ListBuffer

class VoteManager(gs: GameState, vs: VoteState, ai: VoteAI, ircState: IRCState, voteValidator: VoteValidator) extends LazyLogging {
  def makeDiscoverDecision(): ActionVote = {
    val discoverTally = vs.voterMap.foldLeft(Map[Discover, Double]()) { case (accTally, voterMapElement) =>

      val discoverVote = voterMapElement._2.actionVoteList.find { case x => x match {
        case discoverVote: Discover =>
          true
        case _ =>
          false
      }
      }.asInstanceOf[Option[Discover]]

      if (discoverVote.nonEmpty) {
        if (accTally.isDefinedAt(discoverVote.get))
          accTally + (discoverVote.get -> (accTally(discoverVote.get) + voterMapElement._2.personalVotePower))
        else
          accTally + (discoverVote.get -> voterMapElement._2.personalVotePower)
      }
      else {
        accTally
      }
    }
    if (discoverTally.nonEmpty)
      discoverTally.toList.sortBy(_._2).reverse.head._1
    else
      ActionUninit()
  }

  def makeChooseOneDecision(): (ActionVote, (HSCard, HSCard)) = {
    val chooseOneTally = vs.voterMap.foldLeft(Map[ChooseOne, Double]()) { case (accTally, voterMapElement) =>

      val chooseOneVote = voterMapElement._2.actionVoteList.find { case x => x match {
        case chooseOneVote: ChooseOne =>
          true
        case _ =>
          false
      }
      }.asInstanceOf[Option[ChooseOne]]

      if (chooseOneVote.nonEmpty) {
        if (accTally.isDefinedAt(chooseOneVote.get))
          accTally + (chooseOneVote.get -> (accTally(chooseOneVote.get) + voterMapElement._2.personalVotePower))
        else
          accTally + (chooseOneVote.get -> voterMapElement._2.personalVotePower)
      }
      else {
        accTally
      }
    }
    if (chooseOneTally.nonEmpty) {
      val vote = chooseOneTally.toList.sortBy(_._2).reverse.head._1
      (vote, gs.getSourceAndTarget(vote))
    }
    else
      (ActionUninit(), (NoCard(), NoCard()))
  }


  def makeMulliganDecision(): ActionVote = {
    val mulliganTally = vs.voterMap.foldLeft(Map[MulliganVote, Double]()) { case (accTally, voterMapElement) =>

      val mulliganVote = voterMapElement._2.actionVoteList.find { case x => x match {
        case mulliganVote: MulliganVote =>
          true
        case _ =>
          false
      }
      }.asInstanceOf[Option[MulliganVote]]

      if (mulliganVote.nonEmpty) {
        if (accTally.isDefinedAt(mulliganVote.get))
          accTally + (mulliganVote.get -> (accTally(mulliganVote.get) + voterMapElement._2.personalVotePower))
        else
          accTally + (mulliganVote.get -> voterMapElement._2.personalVotePower)
      }
      else {
        accTally
      }
    }
    if (mulliganTally.nonEmpty)
      mulliganTally.toList.sortBy(_._2).reverse.head._1
    else
      ActionUninit()
  }


  def clearActionVotes(): Unit = {
    logger.debug("Clearing Action Votes")
    vs.voterMap = vs.voterMap.foldLeft(Map[String, Voter]()) { case (r, c) =>

      c match {
        case (name, voter) =>
          r + (name -> voter.clearActionVotes())
      }
    }
  }

  def clearAllVotes(): Unit = {
    logger.debug("Clearing All Votes")
    vs.voterMap = vs.voterMap.foldLeft(Map[String, Voter]()) { case (r, c) =>

      c match {
        case (name, voter) =>
          r + (name -> voter.clearAllVotes())
      }
    }
  }


  def makeDecision(): List[(ActionVote, (HSCard, HSCard))] = {
    ai.makeDecision()
  }

  def updateDecision(updateVotes: UpdateVotes): Unit = {

    val friendlyHandChangeMap: Map[Int, Int] = updateVotes.friendlyHandChangeMap
    val friendlyBoardChangeMap: Map[Int, Int] = updateVotes.friendlyBoardChangeMap
    val enemyBoardChangeMap: Map[Int, Int] = updateVotes.enemyBoardChangeMap

    val friendlyHandKnownFutures: List[Int] = updateVotes.friendlyHandKnownFutures
    val friendlyBoardKnownFutures: List[Int] = updateVotes.friendlyBoardKnownFutures
    val enemyBoardKnownFutures: List[Int] = updateVotes.enemyBoardKnownFutures

    def convertFutures(vote: ActionVote): ActionVote = {

      vote match {
        case future: FutureVote =>
          val convertCard = future.isFutureCard && friendlyHandKnownFutures.contains(future.card)
          val convertFriendlyTarget = future.isFutureFriendlyTarget && friendlyBoardKnownFutures.contains(future.friendlyTarget)
          val convertEnemyTarget = future.isFutureEnemyTarget && enemyBoardKnownFutures.contains(future.enemyTarget)

          val stillFutureCard = future.isFutureCard && !friendlyHandKnownFutures.contains(future.card)
          val stillFutureFriendlyTarget = future.isFutureFriendlyTarget && !friendlyBoardKnownFutures.contains(future.friendlyTarget)
          val stillFutureEnemyTarget = future.isFutureEnemyTarget && !enemyBoardKnownFutures.contains(future.enemyTarget)

          if (!future.isFutureCard && !future.isFutureEnemyTarget && !future.isFutureFriendlyTarget && !future.isFuturePosition) {
            future.convertToNonFuture()
          }
          else {
            if (convertCard || convertFriendlyTarget || convertEnemyTarget) {
              convertFutures(future.copyFutureVote(isFutureCard = stillFutureCard, isFutureEnemyTarget = stillFutureEnemyTarget, isFutureFriendlyTarget = stillFutureFriendlyTarget))
            }
            else {
              future
            }
          }


        case action: ActionVote =>
          vote


      }
    }


    def findNewVote(vote: ActionVote): ActionVote = {

      vote match {

        case future: FutureVote =>

          val newCard = {
            if (friendlyHandChangeMap.isDefinedAt(vote.card) && !friendlyHandKnownFutures.contains(vote.card)) {
              friendlyHandChangeMap(vote.card)
            }
            else {
              future.card
            }
          }


          val newFriendlyTarget = {

            if (friendlyBoardChangeMap.isDefinedAt(vote.friendlyTarget) && !friendlyBoardKnownFutures.contains(vote.friendlyTarget)) {
              friendlyBoardChangeMap(vote.friendlyTarget)
            }
            else {
              future.friendlyTarget
            }
          }

          val newEnemyTarget = {
            if (enemyBoardChangeMap.isDefinedAt(vote.enemyTarget) && !enemyBoardKnownFutures.contains(vote.enemyTarget)) {
              enemyBoardChangeMap(vote.enemyTarget)
            }
            else {
              future.enemyTarget
            }
          }

          val newPosition = {
            if (friendlyBoardChangeMap.isDefinedAt(vote.position)) {
              friendlyBoardChangeMap(vote.position)
            }
            else {
              vote.position
            }
          }


          future.copyFutureVote(card = newCard, friendlyTarget = newFriendlyTarget, enemyTarget = newEnemyTarget, position = newPosition)


        case action: ActionVote =>

          val DELETE_VOTE = -999

          val newCard = {
            if (vote.card == Constants.INT_UNINIT) {
              Constants.INT_UNINIT
            }
            else {
              if (friendlyHandChangeMap.isDefinedAt(vote.card)) {
                friendlyHandChangeMap(vote.card)
              }
              else {
                DELETE_VOTE
              }
            }
          }

          val newFriendlyTarget = {
            if (vote.friendlyTarget == 0) {
              0
            }
            else {
              if (vote.friendlyTarget == Constants.INT_UNINIT) {
                Constants.INT_UNINIT
              }
              else {
                if (friendlyBoardChangeMap.isDefinedAt(vote.friendlyTarget)) {
                  friendlyBoardChangeMap(vote.friendlyTarget)
                }
                else {
                  DELETE_VOTE
                }
              }
            }
          }

          val newEnemyTarget = {
            if (vote.enemyTarget == 0) {
              0
            }
            else {
              if (vote.enemyTarget == Constants.INT_UNINIT) {
                Constants.INT_UNINIT
              }
              else {
                if (enemyBoardChangeMap.isDefinedAt(vote.enemyTarget)) {
                  enemyBoardChangeMap(vote.enemyTarget)
                }
                else {
                  DELETE_VOTE
                }
              }
            }
          }

          val newPosition = {
            if (vote.position == Constants.INT_UNINIT) {
              Constants.INT_UNINIT
            }
            else {
              if (friendlyBoardChangeMap.isDefinedAt(vote.position)) {
                friendlyBoardChangeMap(vote.position)
              }
              else {
                vote.position
              }
            }
          }

          val newVote = vote.copyVote(card = newCard, friendlyTarget = newFriendlyTarget, enemyTarget = newEnemyTarget, position = newPosition)

          if (newVote.card != DELETE_VOTE && newVote.friendlyTarget != DELETE_VOTE && newVote.enemyTarget != DELETE_VOTE)
            newVote
          else
            ActionUninit()
      }
    }

    val newVoteExecutionList = ListBuffer[(ActionVote, (HSCard, HSCard))]()

    ircState.voteExecutionList.foreach { case (vote, st) =>

      vote match {
        case discover: Discover =>
          newVoteExecutionList.append((vote, st))

        case chooseOne: ChooseOne =>
          newVoteExecutionList.append((vote, st))

        case uninit: ActionUninit =>
          logger.error("Action Uninit found in vote execution list when trying to update votes. I don't believe this should ever happen. Removing the ActionUninit and continuing.")

        case hurry: Hurry =>
          newVoteExecutionList.append((vote, st))

        case endturn: EndTurn =>
          newVoteExecutionList.append((vote, st))

        case future: FutureVote =>
          val newVote = findNewVote(future)
          val convertIfNeeded = convertFutures(newVote)

          val finalVote = (convertIfNeeded, gs.getSourceAndTarget(convertIfNeeded))


          if (finalVote._1 == ActionUninit()) {
            logger.debug(s"Found ActionUninit when adjusting votes. Removing vote: $vote")
          }
          else {
            if (finalVote !=(vote, st)) {
              logger.debug(s"Found vote that needs adjusted. Old Vote: $vote, New Vote: $convertIfNeeded")
              newVoteExecutionList.append(finalVote)
            }
            else {
              newVoteExecutionList.append((vote, st))
            }
          }


        case action: ActionVote =>

          val newVote = findNewVote(action)
          val finalVote = (newVote, gs.getSourceAndTarget(newVote))

          if (newVote == ActionUninit()) {
            logger.debug(s"Found ActionUninit when adjusting votes. Removing vote: $vote")
          }
          else {
            if (finalVote !=(vote, st)) {
              logger.debug(s"Found vote that need adjusted. Old Vote: $vote, New Vote: $newVote")
              newVoteExecutionList.append(finalVote)
            }
            else {
              newVoteExecutionList.append((vote, st))
            }
          }
      }
    }

    ircState.voteExecutionList.clear()
    ircState.voteExecutionList.appendAll(newVoteExecutionList)

  }



  def getVoteListAsString(voterName: String): String = {
    vs.voterMap(voterName).actionVoteList.foldLeft("VoteList:") { (r, c) =>
      r + s" ${convertVoteToString(c)},"
    }
  }

  def convertVoteToString(vote: ActionVote): String = {
    //todo: Add future votes
    vote match {
      case Discover(card) =>
        s"discover $card"
      case CardPlayWithFriendlyTargetWithPosition(card, friendlyTarget, position) =>
        s"c$card>>f$position>f$friendlyTarget"
      case CardPlayWithEnemyTargetWithPosition(card, enemyTarget, position) =>
        s"c$card>>f$position>e$enemyTarget"
      case CardPlayWithPosition(card, position) =>
        s"c$card>>f$position"
      case CardPlay(card) =>
        s"c$card"
      case CardPlayWithFriendlyTarget(card, friendlyTarget) =>
        s"c$card>f$friendlyTarget"
      case CardPlayWithEnemyTarget(card, enemyTarget) =>
        s"c$card>e$enemyTarget"
      case HeroPower() =>
        s"hp"
      case HeroPowerWithFriendlyTarget(friendlyTarget) =>
        s"hp>f$friendlyTarget"
      case HeroPowerWithEnemyTarget(enemyTarget) =>
        s"hp>e$enemyTarget"
      case NormalAttack(friendlyTarget, enemyTarget) =>
        s"f$friendlyTarget>e$enemyTarget"
      case MulliganVote(first: Boolean, second: Boolean, third: Boolean, fourth: Boolean) =>
        var mulliganString = "mulligan"
        if (first)
          mulliganString += " 1,"
        if (second)
          mulliganString += " 2,"
        if (third)
          mulliganString += " 3,"
        if (fourth)
          mulliganString += " 4,"

        mulliganString
      case EndTurn() =>
        "end turn"
    }
  }


  def voteEntry(voterName: String, vote: Vote): Unit = {
    if (vote != ActionUninit()) {
      logger.debug("Vote detected. Determining if it should be entered")
      if (!vs.voterMap.isDefinedAt(voterName))
        vs.voterMap += (voterName -> Voter(voterName))
      vote match {

        case discoverVote: Discover =>
          if (ircState.isDiscover) {
            logger.debug("Discover vote detected while discover is active. Entering vote.")
            vs.voterMap += (voterName -> vs.voterMap(voterName).actionVoteEntry(discoverVote))
          }

        case chooseOneVote: ChooseOne =>
          if (ircState.isChooseOne) {
            logger.debug("ChooseOne vote detected while chooseOne is active. Entering vote.")
            vs.voterMap += (voterName -> vs.voterMap(voterName).actionVoteEntry(chooseOneVote))
          }

        case mulliganVote: MulliganVote =>
          if (ircState.isMulligan) {
            logger.debug("Mulligan vote detected while mulligan is active. Entering vote.")
            vs.voterMap += (voterName -> vs.voterMap(voterName).actionVoteEntry(mulliganVote))
          }

        case actionVote: ActionVote =>
          if (!ircState.isChooseOne && !ircState.isDiscover && !ircState.isMulligan) {
            if (voteValidator.isValidVote(actionVote, voteEntry = true, voteExecution = false)) {
              logger.debug("Valid Action vote detected while no special states are active. Entering vote.")
              vs.voterMap += (voterName -> vs.voterMap(voterName).actionVoteEntry(actionVote))
            }
            else {
              logger.debug("Invalid Action vote detected while no special states are active. Entering vote to invalidActionlist.")
              vs.voterMap += (voterName -> vs.voterMap(voterName).actionVoteEntry(actionVote, false))
            }
          }


        case emojiVote: EmojiVote =>
          if (!ircState.isChooseOne && !ircState.isDiscover && !ircState.isMulligan) {
            logger.debug("Emoji vote detected while no special states are active. Entering vote.")
            vs.voterMap += (voterName -> vs.voterMap(voterName).emojiVoteEntry(emojiVote))
          }

        case _ =>

      }
    }
  }

  def removeVote(voterName: String, vote: ActionVote): Unit = {
    if (!vs.voterMap.isDefinedAt(voterName))
      vs.voterMap += (voterName -> Voter(voterName, Nil))
    else
      vs.voterMap += (voterName -> vs.voterMap(voterName).removeVote(vote))
  }

}
