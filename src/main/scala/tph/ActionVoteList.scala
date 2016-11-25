package tph

/**
  * Created by Harambe on 10/31/2016.
  */
class ActionVoteList extends VoteList {

  val normalVoteList = new NormalVoteList()
  val bindVoteList = new BindVoteList()
  val futureVoteList = new FutureVoteList()
  val tallyMap = scala.collection.mutable.Map[String, Vote]()



  def AdjustVotes(previousDecision: Vote): Unit ={

    normalVoteList.AdjustVotes(previousDecision)
    bindVoteList.AdjustVotes(previousDecision)
    futureVoteList.AdjustVotes(previousDecision)
  }


  def TallyVotes(): scala.collection.mutable.Map[(_), Int] ={

    normalVoteList.TallyVotes()
    bindVoteList.TallyVotes()
    futureVoteList.TallyVotes()


  }


  def GetTallyMap(): Unit ={


  }
}
