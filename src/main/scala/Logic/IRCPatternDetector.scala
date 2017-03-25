package Logic

import FileReaders.HSDataBase
import VoteSystem.{Vote, Voter, VoteManager}
import tph.Constants.ActionVotes._
import tph.GameState

/**
  * Created by Harambe on 3/21/2017.
  */
class IRCPatternDetector(gameState:GameState, voteManager: VoteManager, hSDataBase: HSDataBase) {


//  def detectWindFuryPatterns(voterMap:Map[String, Voter]): List[Pattern] = {
//    var patternList:List[Pattern] = Nil
//
//
//
//
//    voterMap foreach { case (name, voter) =>
//      var validSources: Map[Int, Vote] = Map()
//        voter.actionVoteList foreach {
//          case vote:NormalAttack =>
//            validSources = validSources + (voter.actionVoteList.indexWhere(_ == vote) -> vote)
//          case _ =>
//        }
//
//
//
//
////
////
////    }
//
//
//
//
//  }





}


case class Pattern(pattern:() => Boolean, patternFactor:Double)