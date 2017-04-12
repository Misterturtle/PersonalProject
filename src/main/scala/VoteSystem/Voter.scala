package VoteSystem

import tph.Constants
import tph.Constants.ActionVotes.{ActionVote, RemoveAllVotes, RemoveLastVote, ActionUninit}
import tph.Constants.EmojiVotes.{EmojiVote, EmojiUninit}
import tph.Constants.MenuVotes.MenuVote
import tph.Constants.Vote

/**
  * Created by Harambe on 3/10/2017.
  */
case class Voter(name: String, actionVoteList: List[ActionVote] = Nil, invalidVoteList: List[Vote] = Nil, voteAccuracy: List[Double] = Nil, emojiVote: EmojiVote = EmojiUninit()) {
  def clearActionVotes(): Voter = {
    copy(actionVoteList = Nil)
  }
  def clearAllVotes(): Voter = {
    copy(actionVoteList = Nil, emojiVote = EmojiUninit())
  }


  def personalVotePower = 1.00

  def isTroll: Boolean = {
    if (voteAccuracy.size < 10)
      false
    else
      (voteAccuracy.sum / voteAccuracy.size) < Constants.InfluenceFactors.trollFactor
  }

  def recordAccuracy: Voter = {
    var newVoteAccuracy = voteAccuracy
    for (a <- 0 until voteAccuracy.size - 19)
      newVoteAccuracy = newVoteAccuracy.dropRight(1)
    newVoteAccuracy = currentTurnAccuracyFactor :: newVoteAccuracy
    this.copy(voteAccuracy = newVoteAccuracy)
  }

  def accuracyVoteValues: Map[Vote, Double] = baseVoteValues.map(x => x._1 -> x._2 * averageAccuracyFactor)

  val averageAccuracyFactor: Double = voteAccuracy.sum / voteAccuracy.size

  def currentTurnAccuracyFactor: Double = 1 - (invalidVoteList.size / actionVoteList.size)

  def actionVoteEntry(vote: ActionVote, valid: Boolean = true): Voter = {
    if (valid) {
      this.copy(actionVoteList = actionVoteList :+ vote)
    }
    else {
      this.copy(invalidVoteList = invalidVoteList :+ vote)
    }
  }

  def emojiVoteEntry(vote: EmojiVote): Voter = {
    this.copy(emojiVote = vote)
  }

  def removeVote(vote: ActionVote): Voter = {
    vote match {
      case RemoveLastVote() =>
        this.copy(actionVoteList = (actionVoteList.reverse diff List(actionVoteList.last)).reverse)
      case RemoveAllVotes() =>
        this.copy(actionVoteList = Nil)
      case _ =>
        this.copy(actionVoteList = (actionVoteList.reverse diff List(vote)).reverse)
    }
  }


  def updateVotes(updateMap: Map[Int, Int]): Unit = {}

  def baseVoteValues: Map[Vote, Double] = actionVoteList.foldLeft(Map[Vote, Double]()) { case (r, c) =>
    if (!r.isDefinedAt(c))
      r + (c -> 1.00)
    else
      r + (c -> (r(c) + 1.00))
  }


  def getTotalVoteValues: Map[Vote, Double] = {
    if (isTroll)
      Map[Vote, Double]()
    else {
      //Add any vote filter here after defining
      accuracyVoteValues
    }
  }
}
