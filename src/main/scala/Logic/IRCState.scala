package Logic

import FileReaders.HSAction.HSAction
import GUI.Overlay
import VoteSystem.{VoteManager, Vote, ActionVote}
import tph.{HearthStone, GameState, Constants}
import tph.Constants.ActionVotes._

/**
  * Created by Harambe on 2/23/2017.
  */

class IRCState(ircAI:IRCAI, hearthstone: HearthStone, overlay: Overlay) {
  def startTurn(): Unit = ???

  def isValidDecision(gs: GameState, decision: Vote): Option[Vote] = ???


  def getBaseResults(listOfVotes: List[ActionVote]): Map[ActionVote, Double] ={

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
