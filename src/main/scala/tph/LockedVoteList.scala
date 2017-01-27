package tph

import com.typesafe.scalalogging.LazyLogging
import tph.Constants.ActionVoteCodes.ActionVoteCode

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  * Created by Harambe on 10/31/2016.
  */
class LockedVoteList extends VoteList with LazyLogging {

  val blocks = new ListBuffer[Block]
  val futureVoteWorth = 20
  override val voteList = Constants.UNINIT

  def AdjustVotes(myHandChangeMap: mutable.Map[Int, Int], myBoardChangeMap: mutable.Map[Int, Int], hisHandChangeMap: mutable.Map[Int, Int], hisBoardChangeMap: mutable.Map[Int, Int]): Unit = {

    //Has to adjust votes for every card added/remove, every minion added/removed


    blocks foreach {
      case block =>

        block.voteList foreach {

          case vote =>
            //If the block is active (meaning the first vote has been executed), do no adjust.
            if (!block.active) {

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
    }
  }

  def RemovePreviousDecision(previousVote: ActionVote): Unit = {

    blocks foreach {
      case block =>


        //For each block votelist
        block.voteList foreach {
          case vote =>
            //If any vote == the previous vote
            if (vote.voteCode == previousVote.actionVoteCode) {
              val index = block.voteList.indexWhere(_.voteCode == vote.actionVoteCode)
              //Safety
              if (index == -1) {
                logger.debug("Something is wrong in RemovePreviousDecision in BindVoteList")
              }
              //If there is a vote that equals the previous vote
              else {
                //If it is the nextVote in the block
                if (vote == block.nextVote) {
                  //Activate block
                  block.active = true
                  //If the vote(also the previous vote) is last in the block
                  if (block.voteList(index) == block.voteList.last) {
                    //Remove the block
                    val blockIndex = blocks.indexWhere(_ == block)
                    blocks.remove(blockIndex)
                  }
                  else
                  //If not, just change the nextVote
                    block.nextVote = block.voteList(index + 1)
                  //And remove the previous vote
                  block.voteList.remove(index)

                }
              }
            }
        }
    }
  }


  override def RemoveVote(vote: ActionVote): Unit = {

    blocks foreach {
      case block =>

        if (block.nextVote == vote) {
          val index = blocks.indexWhere(_ == block)
          RemoveBlock(index)
        }
        if (block.ContainsVote(vote) && block.nextVote != vote) {
          block.RemoveVotes(vote)
        }
    }
  }


  def RemoveBlock(blockIndex: Int): Unit = {
    blocks.remove(blockIndex)
  }


  def TallyVotes(): scala.collection.mutable.Map[ActionVoteCode, Int] = {
    val tallyMap = scala.collection.mutable.Map[ActionVoteCode, Int]()


    blocks foreach {

      case block =>


        block.voteList foreach {
          case (vote: ActionVote) =>
            if (vote == block.nextVote)
              tallyMap(vote.actionVoteCode) += ircLogic.ACTIVE_FUTURE_VOTE_VALUE

            else {
              if (block.active) {
                tallyMap(vote.actionVoteCode) += ircLogic.NORMAL_FUTURE_VOTE_VALUE
              }
            }
        }
    }
    return tallyMap
  }

  def CreateBlock(firstVote: ActionVote): Unit = {
    val block = new Block(firstVote)
    blocks.append(block)
  }
}
