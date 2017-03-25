package VoteSystem

import tph.Constants.ActionVotes.ActionUninit
import tph.GameState


/**
  * Created by Harambe on 2/23/2017.
  */
class VoteManager(gameState:GameState)  {

  var voterMap = Map[String, Voter]()
  var voterHistory = List[Map[String,Voter]]()


  def baseVoteValues:Map[Vote, Double] = {
    var counterMap = Map[Vote, Double]()
    voterMap foreach {
      case (name, voter)=>
        counterMap = counterMap ++ voter.baseVoteValues.map{ case (k,v) => k -> (v + counterMap.getOrElse(k,0.00))}
    }
    counterMap
  }

  def averageVotersFromHistory:Int = voterHistory.foldLeft(0){case (r,c) => r + c.size}/voterHistory.size

  def recordAccuracy:Unit = voterMap = voterMap.foldLeft(Map[String,Voter]()){case (r,c) => r + (c._1 -> c._2.recordAccuracy)}

  def influenceVoteValues(condition: Vote => Boolean, effect: Double => Double): Map[Vote,Double] = {
    baseVoteValues.map{ case (vote, value) => if(condition(vote)) (vote, effect(value)) else (vote, value)}
  }


  def voteEntry(voterName:String, vote:Vote): Unit = {
    if(vote != ActionUninit()) {
      if (!voterMap.isDefinedAt(voterName)) {
        voterMap = voterMap ++ Map[String, Voter](voterName -> new Voter(voterName, Nil))
      }
      voterMap = voterMap - voterName + (voterName -> voterMap(voterName).voteEntry(vote))
    }
  }

  def updateVotes(updateMap: Map[Int,Int]):Unit = {
      voterMap foreach {
        case (name, voter) =>
          voter.updateVotes(updateMap)
      }
  }
}
