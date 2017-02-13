package tph

import scala.collection.mutable.ListBuffer
import scala.collection.mutable

class VoteList {

  val voteLog = new ListBuffer[Vote]
  val voteList = new ListBuffer[ActionVote]


  def Reset(): Unit = {
    voteList.clear()
  }


  def AddVote(vote: ActionVote): Unit = {

    voteList.append(vote)
  }

  def RemoveVote(vote: ActionVote): Unit = {

    if (voteList.contains(vote)) {
      val index = voteList.indexWhere(_ == vote)
      voteList.remove(index)
    }
  }




}
