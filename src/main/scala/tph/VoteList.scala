package tph

import scala.collection.mutable.ListBuffer
import scala.collection.mutable

class VoteList {

  val voteLog = new ListBuffer[Vote]
  val voteList = scala.collection.mutable.Map[(_), Vote]()

  def AddVoteList(v1: VoteList): Unit ={

    v1.voteList foreach {
      case (voteCode, vote) =>
      AddVote(vote)

    }
  }

  def CombineTallyMaps(tm1: mutable.Map[(_), Int], tm2: mutable.Map[(_), Int]): mutable.Map[(_), Int] =
  {
    val tm3 = scala.collection.mutable.Map[(_), Int]()

    tm1 foreach {

      case (vote, value) =>
        if(!tm3.contains(vote)){
          tm3(vote) = value
        }
        else{
          tm3(vote) += value
        }
    }

    tm2 foreach {

      case (vote, value) =>
        if(!tm3.contains(vote)){
          tm3(vote) = value
        }
        else{
          tm3(vote) += value
        }
    }

    return tm3
  }

  def AddVote(vote:Vote):Unit = {

      voteList(vote.GetVoteCode()) = vote

  }


}
