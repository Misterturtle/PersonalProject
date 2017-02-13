package tph

import com.typesafe.scalalogging.LazyLogging
import tph.Constants.ActionVoteCodes.ActionVoteCode

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  * Created by Harambe on 10/31/2016.
  */
class LockedVoteList extends VoteList with LazyLogging {

  var active = false
  val blocks = new ListBuffer[Block]
  var activeBlock = new Block(new ActionVote("bindVoteList", Constants.ActionVoteCodes.ActionUninit()))

  override def AddVote(vote: ActionVote): Unit = {

    var voteAdded = false

    if (blocks.isEmpty) {
      CreateBlock()
    }
    if (activeBlock.voteList(0).actionVoteCode == Constants.ActionVoteCodes.ActionUninit() && !voteAdded) {
      blocks.last.voteList(0) = vote
      voteAdded = true
      activeBlock = blocks.last
      activeBlock.nextVote = vote
    }

    val duplicateVote = blocks.find(_.voteList.head.actionVoteCode == vote.actionVoteCode)
    if (duplicateVote == None && !voteAdded) {
      activeBlock.voteList.append(vote)
      voteAdded = true
    }
  }

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
            vote.UpdateVoteCode()
        }
    }
  }

  def Activate(): Unit = {
    active = true
  }

  def Deactivate(): Unit = {
    active = false
    activeBlock.active = false
  }

  def RemovePreviousDecision(previousVote: ActionVote): Unit = {

    blocks foreach {
      case block =>


        //For each block votelist
        block.voteList foreach {
          case vote =>
            //If any vote == the previous vote
            if (vote.actionVoteCode == previousVote.actionVoteCode) {
              val index = block.voteList.indexWhere(_.actionVoteCode == vote.actionVoteCode)
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
                  if (block.voteList(index) == block.voteList.last && block.active) {
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
            if (vote == block.nextVote) {
              if (!tallyMap.isDefinedAt(vote.actionVoteCode))
                tallyMap(vote.actionVoteCode) = 0


              tallyMap(vote.actionVoteCode) += ircLogic.ACTIVE_FUTURE_VOTE_VALUE
            }

            else {
              if (block.active) {
                if (!tallyMap.isDefinedAt(vote.actionVoteCode))
                  tallyMap(vote.actionVoteCode) = 0

                tallyMap(vote.actionVoteCode) += ircLogic.NONACTIVE_FUTURE_VOTE_VALUE
              }
            }
        }
    }
    return tallyMap
  }

  def CreateBlock(): Unit = {
    val block = new Block(new ActionVote("lockedVoteList", Constants.ActionVoteCodes.ActionUninit()))
    blocks.append(block)
    activeBlock.active = false
    activeBlock = blocks.last
  }
}
