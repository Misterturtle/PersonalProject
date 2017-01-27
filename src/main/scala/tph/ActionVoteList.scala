package tph

import tph.Constants.ActionVoteCodes.ActionVoteCode

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  * Created by Harambe on 10/31/2016.
  */
class ActionVoteList extends VoteList {

  val normalVoteList = new NormalVoteList()
  val bindVoteList = new BindVoteList()
  val lockedVoteList = new LockedVoteList()
  val tallyMap = scala.collection.mutable.Map[String, Vote]()

  var bindActive = false
  var lockedActive = false


  override def AddVote(vote: ActionVote): Unit = {

    var voteAdded = false
    vote.Init()


    if (!bindActive && !lockedActive && !voteAdded) {
      vote.voteCode match {
        case Constants.ActionVoteCodes.Bind() =>
          bindActive = true
          voteAdded = true

        case Constants.ActionVoteCodes.Future() =>
          lockedActive = true
          voteAdded = true

        case _ =>
          normalVoteList.AddVote(vote)
          voteAdded = true
      }
    }

    if (bindActive && !voteAdded) {
      vote.voteCode match {
        case Constants.ActionVoteCodes.Bind() =>
          bindActive = true
          voteAdded = true

        case Constants.ActionVoteCodes.Future() =>
          lockedActive = true
          voteAdded = true

        case _ =>
          bindActive = false
          voteAdded = true
          bindVoteList.AddVote(vote)
      }
    }
    if (lockedActive && !voteAdded) {

      vote.voteCode match {
        case Constants.ActionVoteCodes.Bind() =>
          bindActive = true
          voteAdded = true

        case Constants.ActionVoteCodes.Future() =>
          lockedActive = true
          voteAdded = true

        case _ =>
          lockedActive = false
          voteAdded = true
          lockedVoteList.AddVote(vote)
      }


    }
  }

  def RemovePreviousDecision(vote: ActionVote): Unit = {

    normalVoteList.RemovePreviousDecision(vote)
    bindVoteList.RemovePreviousDecision(vote)
    lockedVoteList.RemovePreviousDecision(vote)

  }

  override def Reset(): Unit = {

    normalVoteList.Reset()
    bindVoteList.Reset()
    lockedVoteList.Reset()
  }

  def GetNumberOfTurns(): Int = {

    var numberOfTurns = 0

    normalVoteList.voteList foreach {

      case vote =>
        vote.voteCode match {

          case x: ActionVoteCode =>
            numberOfTurns += 1
        }
    }

    bindVoteList.voteList foreach {

      case vote =>
        vote.voteCode match {

          case x: ActionVoteCode =>
            numberOfTurns += 1
        }
    }

    lockedVoteList.voteList foreach {

      case vote =>
        vote.voteCode match {

          case x: ActionVoteCode =>
            numberOfTurns += 1
        }
    }
    numberOfTurns
  }


  def AdjustVotes(previousGameStatus: FrozenGameStatus, currentGameStatus: FrozenGameStatus): Unit = {


    //Extract Frozen Players from frozen game status
    val oldPlayers = previousGameStatus.GetFrozenPlayers()
    val newPlayers = currentGameStatus.GetFrozenPlayers()

    //Create Change Map from old status to new status
    //Based on ID's, hand/board positions are mapped to new values
    val myHandChangeMap = CreateChangeMap(oldPlayers(0), newPlayers(0), true)
    val myBoardChangeMap = CreateChangeMap(oldPlayers(0), newPlayers(0), false)
    val hisBoardChangeMap = CreateChangeMap(oldPlayers(1), newPlayers(1), false)
    val hisHandChangeMap = CreateChangeMap(oldPlayers(1), newPlayers(1), true)


    //Each votelist should adjust votes differently, but all use same ChangeMap
    normalVoteList.AdjustVotes(myHandChangeMap, myBoardChangeMap, hisHandChangeMap, hisBoardChangeMap)
    bindVoteList.AdjustVotes(myHandChangeMap, myBoardChangeMap, hisHandChangeMap, hisBoardChangeMap)
    lockedVoteList.AdjustVotes(myHandChangeMap, myBoardChangeMap, hisHandChangeMap, hisBoardChangeMap)



    // Methods for AdjustVotes for clean code

    def CreateChangeMap(oldPlayer: FrozenPlayer, newPlayer: FrozenPlayer, hand: Boolean): mutable.Map[Int, Int] = {
      val changedLocationMap = mutable.Map[Int, Int]()

      if (hand) {

        for (a <- 0 until oldPlayer.hand.size) {
          val id = oldPlayer.hand(a).id
          val oldHandPosition = HandPositionOfID(oldPlayer, id)
          val newHandPosition = HandPositionOfID(newPlayer, id)

          if (oldHandPosition != newHandPosition) {
            changedLocationMap(oldHandPosition) = newHandPosition
          }

        }
        return changedLocationMap
      }


      else {

        for (a <- 0 until oldPlayer.board.size) {
          val id = oldPlayer.board(a).id
          val oldBoardPosition = BoardPositionOfID(oldPlayer, id)
          val newBoardPosition = BoardPositionOfID(newPlayer, id)

          if (oldBoardPosition != newBoardPosition) {
            changedLocationMap(oldBoardPosition) = newBoardPosition
          }
        }
        return changedLocationMap
      }
    }

    def BoardPositionOfID(player: FrozenPlayer, id: Int): Int = {

      val index = player.board.indexWhere(_.id == id)
      if (index >= 0) {
        val position = player.board(index).boardPosition
        position
      }

      else Constants.UNINIT
    }

    def HandPositionOfID(player: FrozenPlayer, id: Int): Int = {

      val index = player.hand.indexWhere(_.id == id)
      if (index >= 0) {
        val position = player.board(index).boardPosition
        position
      }
      else Constants.UNINIT
    }
  }


  def TallyVotes(): scala.collection.mutable.Map[ActionVoteCode, Int] = {

    val tallyMap = mutable.Map[ActionVoteCode, Int]()
    val normalMap = normalVoteList.TallyVotes()
    val bindMap = bindVoteList.TallyVotes()
    val lockedMap = lockedVoteList.TallyVotes()

    normalMap foreach {
      case (vote, value) =>

        if (tallyMap.isDefinedAt(vote)) {
          tallyMap(vote) += value

        }
        else tallyMap(vote) = value
    }

    bindMap foreach {
      case (vote, value) =>

        if (tallyMap.isDefinedAt(vote)) {
          tallyMap(vote) += value
        }
        else tallyMap(vote) = value
    }


    lockedMap foreach {
      case (vote, value) =>

        if (tallyMap.isDefinedAt(vote)) {
          tallyMap(vote) += value
        }
        else tallyMap(vote) = value
    }

    return tallyMap


  }



}
