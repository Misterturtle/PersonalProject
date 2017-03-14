package Logic

import VoteSystem.ActionVote
import tph.Constants
import tph.Constants.ActionVotes._

/**
  * Created by Harambe on 2/23/2017.
  */

/*
Try to keep this clean.......
Possible Variables:
-Previous Decision (pd)
                    (If pd was Card Played:)
-All votes with playing that card are erased. (Adjust Votes)
-Battlecrys and targets votes on card are worth more.         }   (****)
-Normal Attacks with card as friendlyTarget are worth more.   }  (***)
-




 */




class IRCLogic {



  def GetBaseResults(listOfVotes: List[ActionVote]): Map[ActionVote, Double] ={

    val results = listOfVotes.foldLeft(Map[ActionVote, Double](new CardPlay(0) -> 0)){
      (resultsMap: Map[ActionVote, Double], currentVote:ActionVote) =>

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
          resultsMap ++ Map[ActionVote, Double](currentVote -> 1)
    }
    results
  }

 // def InfluenceWithPreviousDecision(results: Map[ActionVote, Double], previousDecision: ActionVote, influenceFactor: Double = Constants.InfluenceFactors.previousDecisionBonus): Map[ActionVote, Int] ={

    //Go through results map and find the previous decision
    //Add value to the next vote
    //Return a map with only the added values, so Map[ActionVote, Int](voteAfterPrevious, valueToAdd)

//    results.foldLeft(Map[ActionVote, Double](), false) { (voteMapAndActive, currentMapElement) =>
//      if(voteMapAndActive._2)
//
//
//      if (currentMapElement._1 == previousDecision)
//        voteMap ++ Map[ActionVote, Double](currentMapElement._1, influenceFactor)
//      else
//        voteMap
//
    //}













}
