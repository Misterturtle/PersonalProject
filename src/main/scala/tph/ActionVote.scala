package tph

import com.typesafe.scalalogging.LazyLogging
import tph.Constants.ActionVoteCodes.{ActionUninit, ActionVoteCode}

/**
  * Created by Harambe on 10/30/2016.
  */
class ActionVote(sender: String, voteCode: Constants.ActionVoteCodes.ActionVoteCode) extends Vote(sender: String, voteCode: Constants.ActionVoteCodes.ActionVoteCode) with LazyLogging {


  var card = Constants.UNINIT
  var spot = Constants.UNINIT
  var friendlyTarget = Constants.UNINIT
  var enemyTarget = Constants.UNINIT
  var firstCommand = ""
  var secondCommand = ""
  var thirdCommand = ""
  var fullCommand = ""

  var actionVoteCode: ActionVoteCode = voteCode


  def Init(): Unit = {

    actionVoteCode match {

      case Constants.ActionVoteCodes.CardPlay(cardPlayed) =>
        card = cardPlayed

      case Constants.ActionVoteCodes.CardPlayWithEnemyBoardTarget(card1, target1) =>
        card = card1
        enemyTarget = target1

      case Constants.ActionVoteCodes.CardPlayWithFriendlyOption(card1, boardTarget1) =>
        card = card1
        friendlyTarget = boardTarget1

      case Constants.ActionVoteCodes.CardPlayWithFriendlyFaceOption(card1) =>
        card = card1

      case Constants.ActionVoteCodes.CardPlayWithEnemyOption(card1, boardTarget1) =>
        card = card1
        enemyTarget = boardTarget1

      case Constants.ActionVoteCodes.CardPlayWithEnemyFaceOption(card1) =>
        card = card1

      //Battlecry Option with Position Type
      case Constants.ActionVoteCodes.CardPlayWithFriendlyOptionWithPosition(card1, target1, position1) =>
        card = card1
        friendlyTarget = target1
        spot = position1

      case Constants.ActionVoteCodes.CardPlayWithFriendlyFaceOptionWithPosition(card1, position1) =>
        card = card1
        spot = position1


      case Constants.ActionVoteCodes.CardPlayWithEnemyOptionWithPosition(card1, target1, position1) =>
        card = card1
        enemyTarget = target1
        spot = position1


      case Constants.ActionVoteCodes.CardPlayWithEnemyFaceOptionWithPosition(card1, position1) =>
        card = card1
        spot = position1




      //Normal Turn Play Type
      case Constants.ActionVoteCodes.CardPlay(card1) =>
        card = card1

      case Constants.ActionVoteCodes.CardPlayWithPosition(card1, position1) =>
        card = card1
        spot = position1

      case Constants.ActionVoteCodes.CardPlayWithFriendlyBoardTarget(card1, target1) =>
        card = card1
        friendlyTarget = target1

      case Constants.ActionVoteCodes.CardPlayWithEnemyBoardTarget(card1, target1) =>
        card = card1
        enemyTarget = target1

      case Constants.ActionVoteCodes.CardPlayWithFriendlyFaceTarget(card1) =>
        card = card1

      case Constants.ActionVoteCodes.CardPlayWithEnemyFaceTarget(card1) =>
        card = card1

      case Constants.ActionVoteCodes.HeroPower() =>


      case Constants.ActionVoteCodes.HeroPowerWithFriendlyTarget(target1) =>
        friendlyTarget = target1

      case Constants.ActionVoteCodes.HeroPowerWithEnemyTarget(target1) =>
        enemyTarget = target1

      case Constants.ActionVoteCodes.HeroPowerWithFriendlyFace() =>


      case Constants.ActionVoteCodes.HeroPowerWithEnemyFace() =>


      //Attack Type
      case Constants.ActionVoteCodes.NormalAttack(friendlyPosition1, enemyPosition1) =>
        friendlyTarget = friendlyPosition1
        enemyTarget = enemyPosition1

      case Constants.ActionVoteCodes.FaceAttack(position1) =>
        spot = position1

      case Constants.ActionVoteCodes.NormalAttackToFace(position1) =>
        spot = position1

      case Constants.ActionVoteCodes.FaceAttackToFace() =>

      case _ =>
        logger.debug("Unknown Action votecode: " + voteCode)

    }
  }

  def UpdateVoteCode(): Unit = {

    actionVoteCode match {

      case Constants.ActionVoteCodes.CardPlay(cardPlayed) =>
        actionVoteCode = Constants.ActionVoteCodes.CardPlay(card)

      case Constants.ActionVoteCodes.CardPlayWithEnemyBoardTarget(card1, target1) =>
        actionVoteCode = Constants.ActionVoteCodes.CardPlayWithEnemyBoardTarget(card, target1)

      case Constants.ActionVoteCodes.CardPlayWithFriendlyOption(card1, boardTarget1) =>
        actionVoteCode = Constants.ActionVoteCodes.CardPlayWithFriendlyOption(card, friendlyTarget)


      case Constants.ActionVoteCodes.CardPlayWithFriendlyFaceOption(card1) =>
        actionVoteCode = Constants.ActionVoteCodes.CardPlayWithFriendlyFaceOption(card)

      case Constants.ActionVoteCodes.CardPlayWithEnemyOption(card1, boardTarget1) =>
        actionVoteCode = Constants.ActionVoteCodes.CardPlayWithEnemyOption(card, enemyTarget)

      case Constants.ActionVoteCodes.CardPlayWithEnemyFaceOption(card1) =>
        actionVoteCode = Constants.ActionVoteCodes.CardPlayWithEnemyFaceOption(card)

      //Battlecry Option with Position Type
      case Constants.ActionVoteCodes.CardPlayWithFriendlyOptionWithPosition(card1, target1, position1) =>
        actionVoteCode = Constants.ActionVoteCodes.CardPlayWithFriendlyOptionWithPosition(card, friendlyTarget, spot)

      case Constants.ActionVoteCodes.CardPlayWithFriendlyFaceOptionWithPosition(card1, position1) =>
        actionVoteCode = Constants.ActionVoteCodes.CardPlayWithFriendlyFaceOptionWithPosition(card, spot)


      case Constants.ActionVoteCodes.CardPlayWithEnemyOptionWithPosition(card1, target1, position1) =>
        actionVoteCode = Constants.ActionVoteCodes.CardPlayWithEnemyOptionWithPosition(card, enemyTarget, spot)


      case Constants.ActionVoteCodes.CardPlayWithEnemyFaceOptionWithPosition(card1, position1) =>
        actionVoteCode = Constants.ActionVoteCodes.CardPlayWithEnemyFaceOptionWithPosition(card, spot)




      //Normal Turn Play Type
      case Constants.ActionVoteCodes.CardPlay(card1) =>
        actionVoteCode = Constants.ActionVoteCodes.CardPlay(card)

      case Constants.ActionVoteCodes.CardPlayWithPosition(card1, position1) =>
        actionVoteCode = Constants.ActionVoteCodes.CardPlayWithPosition(card, spot)

      case Constants.ActionVoteCodes.CardPlayWithFriendlyBoardTarget(card1, target1) =>
        actionVoteCode = Constants.ActionVoteCodes.CardPlayWithFriendlyBoardTarget(card, friendlyTarget)

      case Constants.ActionVoteCodes.CardPlayWithEnemyBoardTarget(card1, target1) =>
        actionVoteCode = Constants.ActionVoteCodes.CardPlayWithEnemyBoardTarget(card, enemyTarget)

      case Constants.ActionVoteCodes.CardPlayWithFriendlyFaceTarget(card1) =>
        actionVoteCode = Constants.ActionVoteCodes.CardPlayWithFriendlyFaceTarget(card)

      case Constants.ActionVoteCodes.CardPlayWithEnemyFaceTarget(card1) =>
        actionVoteCode = Constants.ActionVoteCodes.CardPlayWithEnemyFaceTarget(card)

      case Constants.ActionVoteCodes.HeroPower() =>


      case Constants.ActionVoteCodes.HeroPowerWithFriendlyTarget(target1) =>
        actionVoteCode = Constants.ActionVoteCodes.HeroPowerWithFriendlyTarget(friendlyTarget)

      case Constants.ActionVoteCodes.HeroPowerWithEnemyTarget(target1) =>
        actionVoteCode = Constants.ActionVoteCodes.HeroPowerWithEnemyTarget(enemyTarget)

      case Constants.ActionVoteCodes.HeroPowerWithFriendlyFace() =>


      case Constants.ActionVoteCodes.HeroPowerWithEnemyFace() =>


      //Attack Type
      case Constants.ActionVoteCodes.NormalAttack(friendlyPosition1, enemyPosition1) =>
        actionVoteCode = Constants.ActionVoteCodes.NormalAttack(friendlyTarget, enemyTarget)

      case Constants.ActionVoteCodes.FaceAttack(position1) =>
        actionVoteCode = Constants.ActionVoteCodes.FaceAttack(spot)

      case Constants.ActionVoteCodes.NormalAttackToFace(position1) =>
        actionVoteCode = Constants.ActionVoteCodes.NormalAttackToFace(spot)

      case Constants.ActionVoteCodes.FaceAttackToFace() =>

      case _ =>
        logger.debug("Unknown Action votecode: " + voteCode)


    }
  }

}
