package VoteSystem

import tph.Constants
import tph.Constants.ActionVotes.ActionUninit

/**
  * Created by Harambe on 3/10/2017.
  */
class Voter(val name: String) {

    def voteEntry(vote:Vote):Unit = {}

    def updateVotes(updateMap: Map[Int,Int]):Unit = {}

    def baseVoteValues():Map[Vote, Double] = Map(new ActionUninit() -> Constants.INT_UNINIT.toDouble)



}
