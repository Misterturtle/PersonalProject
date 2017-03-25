package VoteSystem

import tph.Constants
import tph.Constants.ActionVotes.ActionUninit
import tph.Constants.EmojiVotes.EmojiUninit

/**
  * Created by Harambe on 3/10/2017.
  */
case class Voter(name: String, actionVoteList: List[ActionVote] = Nil, invalidVoteList: List[Vote] = Nil, voteAccuracy: List[Double] = Nil, emojiVote:EmojiVote = EmojiUninit()) {

  def personalVotePower = 1.00

  def isTroll: Boolean = {
    if(voteAccuracy.size < 10)
      false
    else
    (voteAccuracy.sum / voteAccuracy.size) < Constants.InfluenceFactors.trollFactor}

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

  def voteEntry(vote: Vote): Voter =

  vote match{
    case actionVote:ActionVote =>
      this.copy(actionVoteList = actionVoteList :+ actionVote)

    case emojiVote:EmojiVote =>
      this.copy(emojiVote = emojiVote)

    case menuVote:MenuVote =>
      ???
  }


  def updateVotes(updateMap: Map[Int, Int]): Unit = {}

  def baseVoteValues: Map[Vote, Double] = actionVoteList.foldLeft(Map[Vote,Double]()){case (r,c) =>
  if(!r.isDefinedAt(c))
    r + (c -> 1.00)
      else
    r + (c -> (r(c) + 1.00))
  }


  def getTotalVoteValues:Map[Vote, Double] = {
    if(isTroll)
      Map[Vote,Double]()
    else {
      //Add any vote filter here after defining
      accuracyVoteValues
    }
  }
}
