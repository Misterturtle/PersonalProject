package VoteSystem

import tph.Constants
import tph.Constants.ActionVotes.ActionUninit

/**
  * Created by Harambe on 3/10/2017.
  */
 case class Voter(name: String, actionVoteList:List[Vote] = Nil) {


    def voteEntry(vote:Vote):Voter = this.copy(actionVoteList = actionVoteList :+ vote)

    def updateVotes(updateMap: Map[Int,Int]):Unit = {}

    def baseVoteValues():Map[Vote, Double] = Map(new ActionUninit() -> Constants.INT_UNINIT.toDouble)



}
