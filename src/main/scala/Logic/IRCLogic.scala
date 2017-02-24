package Logic

import VoteSystem.ActionVote
import tph.Constants.ActionVotes._

/**
  * Created by Harambe on 2/23/2017.
  */
class IRCLogic {



  def GetBaseResults(listOfVotes: List[ActionVote]): Map[ActionVote, Int] ={

    val results = listOfVotes.foldLeft(Map[ActionVote, Int](new CardPlay("ircLogic", 0) -> 0)){
      (resultsMap: Map[ActionVote, Int], currentVote:ActionVote) =>

        if(resultsMap.contains(currentVote)) {
          resultsMap.map {
            case (vote, tally) =>
              if (vote == currentVote)
                (vote, tally + 1)
              else
                (vote, tally)
          }
        }
        else
          resultsMap ++ Map[ActionVote, Int](currentVote -> 1)
    }
    results
  }
}
