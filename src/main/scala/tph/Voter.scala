package tph

/**
  * Created by Harambe on 10/29/2016.
  */


object Voter{
  val flagSystem = new FlagSystem
  val bindFlag = new Flag()
  val futureFlag = new Flag()
  flagSystem.AddFlag(bindFlag)
  flagSystem.AddFlag(futureFlag)


  val normalVoteList = new VoteList()
  val bindVoteList = new VoteList()
  val futureVoteList = new VoteList()
  val emojiVoteList = new VoteList()
  val menuVoteList = new VoteList()


}

class Voter(sender: String) {






      def VoteEntry(vote:Vote): Unit = {



      }

}
