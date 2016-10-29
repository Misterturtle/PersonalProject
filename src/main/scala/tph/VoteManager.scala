package tph

import scala.collection.mutable.ListBuffer

class VoteManager {

  val voteLog = new VoteList
  val normalList = new VoteList
  val bindList = new VoteList
  val futureList = new VoteList

  val flagSystem = new FlagSystem
  flagSystem.AddFlag(new Flag("bindActive"))
  flagSystem.AddFlag(new Flag("futureActive"))





  def AddVote(vote:Vote): Unit ={
    voteLog.AddVote(vote:Vote)

    vote.GetVoteType() match {
      case Constants.UNINIT =>
            println ("Vote " +vote + " has an uninitilized vote type. Will add vote to normalList as default")
            normalList.AddVote(vote)

      case Constants.NORMAL_VOTE_TYPE =>
        normalList.AddVote(vote)

      case Constants.BIND_VOTE_TYPE =>
        bindList.AddVote(vote)

      case Constants.FUTURE_VOTE_TYPE =>
        futureList.AddVote(vote)
    }
  }
}
