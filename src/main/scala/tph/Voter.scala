package tph

/**
  * Created by Harambe on 10/29/2016.
  */
class Voter {

  var name = ""
  val voteManager = new VoteManager



      def AddVote(vote:Vote) {
        voteManager.AddVote(vote)
      }

}
