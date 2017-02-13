package tph

import tph.Constants.ActionVoteCodes
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

  var activeVoter = false


  override def AddVote(vote: ActionVote): Unit = {

    var voteAdded = false
    vote.Init()


    if (!bindVoteList.active && !lockedVoteList.active && !voteAdded) {
      vote.voteCode match {
        case Constants.ActionVoteCodes.Bind() =>
          bindVoteList.Activate()
          lockedVoteList.Deactivate()
          voteAdded = true

        case Constants.ActionVoteCodes.Future() =>
          lockedVoteList.Activate()
          bindVoteList.Deactivate()
          voteAdded = true

        case _ =>
          val normDupVote = normalVoteList.voteList.find(_.voteCode == vote.voteCode)
          var bindDupVote = false

          bindVoteList.blocks foreach {
            case block =>
              block.voteList foreach {
                case vote2 =>
                  if (vote2.actionVoteCode == vote.actionVoteCode)
                    bindDupVote = true
              }
          }


          if (normDupVote == None && !bindDupVote) {
            normalVoteList.AddVote(vote)
            voteAdded = true
            activeVoter = true
            bindVoteList.Deactivate()
            lockedVoteList.Deactivate()

          }
      }
    }

    if (bindVoteList.active && !voteAdded) {
      vote.voteCode match {
        case Constants.ActionVoteCodes.Bind() =>
          voteAdded = true
          lockedVoteList.Deactivate()

        case Constants.ActionVoteCodes.Future() =>
          bindVoteList.Deactivate()
          lockedVoteList.Activate()
          voteAdded = true

        case _ =>


          voteAdded = true
          bindVoteList.AddVote(vote)
          activeVoter = true
          bindVoteList.Deactivate()
          lockedVoteList.Deactivate()
      }
    }

    if (lockedVoteList.active && !voteAdded) {

      vote.voteCode match {
        case Constants.ActionVoteCodes.Bind() =>
          bindVoteList.Activate()
          lockedVoteList.Deactivate()
          voteAdded = true

        case Constants.ActionVoteCodes.Future() =>
          lockedVoteList.Activate()
          bindVoteList.Deactivate()
          voteAdded = true

        case _ =>


          voteAdded = true
          lockedVoteList.AddVote(vote)
          activeVoter = true
          bindVoteList.Deactivate()
          lockedVoteList.Deactivate()

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

    activeVoter = false
  }

  def GetNumberOfTurns(): Int = {

    var numberOfTurns = 0

    normalVoteList.voteList foreach {

      case vote =>
        vote.actionVoteCode match {

          case x: ActionVoteCode =>
            if (vote.actionVoteCode != Constants.ActionVoteCodes.ActionUninit())
            numberOfTurns += 1
          case _ =>
        }
    }

    bindVoteList.blocks foreach {
      case block =>

        block.voteList foreach {
          case vote =>

            vote.actionVoteCode match {

              case x: ActionVoteCode =>

                if (vote.actionVoteCode != Constants.ActionVoteCodes.ActionUninit())
                  numberOfTurns += 1
              case _ =>
            }
        }
    }

    lockedVoteList.blocks foreach {
      case block =>

        block.voteList foreach {
          case vote =>

            vote.voteCode match {

              case x: ActionVoteCode =>
                if (vote.actionVoteCode != Constants.ActionVoteCodes.ActionUninit())
                  numberOfTurns += 1
              case _ =>
            }
        }
    }
    numberOfTurns
  }


  def AdjustVotes(previousGameStatus: FrozenGameStatus, currentGameStatus: FrozenGameStatus): Unit = {


    //Extract Frozen Players from frozen game status
    val oldPlayers = previousGameStatus.frozenPlayers
    val newPlayers = currentGameStatus.frozenPlayers





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
        val position = player.hand(index).handPosition
        position
      }
      else Constants.UNINIT
    }



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


    if (!tallyMap.isEmpty) {
      val default = (ActionVoteCodes.ActionUninit(), Constants.UNINIT)
      val highestValue = tallyMap.values.max
      val chosenVoteCode = tallyMap.find(_._2 == highestValue).getOrElse(default)._1

      if (chosenVoteCode == Constants.ActionVoteCodes.ActionUninit()) {
        tallyMap.remove(chosenVoteCode)
        return tallyMap
      }
    }
    return tallyMap


  }



}
