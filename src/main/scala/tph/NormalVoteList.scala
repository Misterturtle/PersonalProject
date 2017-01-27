package tph

import tph.Constants.ActionVoteCodes.ActionVoteCode

import scala.collection.mutable

/**
  * Created by Harambe on 10/31/2016.
  */
class NormalVoteList extends VoteList {

  val normalVoteValue = 10


  def AdjustVotes(myHandChangeMap: mutable.Map[Int, Int], myBoardChangeMap: mutable.Map[Int, Int], hisHandChangeMap: mutable.Map[Int, Int], hisBoardChangeMap: mutable.Map[Int, Int]): Unit = {

    //Has to adjust votes for every card added/remove, every minion added/removed


    voteList foreach {
      case vote =>

        //-------------------
        //My Hand Adjustments
        //-------------------

        //If the card vote is in the ChangeMap
        if (myHandChangeMap.contains(vote.card)) {
          //If the new mapped value is UNINIT(Meaning the card is no longer in the hand)
          if (myHandChangeMap(vote.card) == Constants.UNINIT)
            this.RemoveVote(vote)
          else {
            //If there is a new mapped value, change the vote card value
            vote.card = myHandChangeMap(vote.card)
          }
        }


        //--------------------
        //My Board Adjustments
        //--------------------

        //Friendly spot(position) adjustment
        if (myBoardChangeMap.contains(vote.spot)) {
          //If
          if (myBoardChangeMap(vote.spot) != Constants.UNINIT) {
            //Remember that "spot" is in between two minions; to the left of whatever spot value is.
            //*** If the minion associated with vote.spot dies, the spot stays the same(since the minion was technically to the right)
            //*** If the minion associated with vote.spot changes, the spot adjusts to the same value.

            vote.spot = myBoardChangeMap(vote.spot)
          }
        }



        //Friendly target adjustment
        if (myBoardChangeMap.contains(vote.friendlyTarget)) {
          //If the friendly minion has died
          if (myBoardChangeMap(vote.friendlyTarget) == Constants.UNINIT) {
            //Remove the vote
            this.RemoveVote(vote)
          }
          //If the friendly minion has moved
          else {
            vote.friendlyTarget = myBoardChangeMap(vote.friendlyTarget)
          }
        }


        //---------------------
        //His Board Adjustments
        //---------------------
        //Enemy target adjustment
        if (hisBoardChangeMap.contains(vote.enemyTarget)) {
          //If the enemy minion has died
          if (hisBoardChangeMap(vote.enemyTarget) == Constants.UNINIT) {
            //Remove the vote
            this.RemoveVote(vote)
          }
          //If the enemy minion has moved
          else {
            vote.enemyTarget = hisBoardChangeMap(vote.enemyTarget)
          }
        }
    }
  }


  def RemovePreviousDecision(previousVote: ActionVote): Unit = {

    voteList foreach {
      case vote =>
        if (vote.voteCode == previousVote.voteCode) {
          val index = voteList.indexWhere(_.voteCode == vote.voteCode)
          voteList.remove(index)
        }
    }
  }

  def TallyVotes(): scala.collection.mutable.Map[ActionVoteCode, Int] = {
    val tallyMap = scala.collection.mutable.Map[ActionVoteCode, Int]()


    voteList foreach {
      case (vote: ActionVote) =>

        val thisVoteCode = vote.actionVoteCode

        if (tallyMap.isDefinedAt(thisVoteCode)) {
          tallyMap(thisVoteCode) += ircLogic.NORMAL_VOTE_VALUE
        }
        else tallyMap(thisVoteCode) = ircLogic.NORMAL_VOTE_VALUE
    }

    return tallyMap

  }

}
