package tph

import scala.collection.mutable.ListBuffer

class VoteList {

  val voteList = new ListBuffer[Vote]


  def AddVote(vote:Vote):Unit = {
    voteList.append(vote)
  }

  def GetMostRecentVote() : Vote = {
  return voteList.last
  }



}
