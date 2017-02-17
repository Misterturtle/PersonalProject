package tph.StateManagement

import com.typesafe.scalalogging.LazyLogging
import tph.Constants.ActionVoteCodes.ActionVoteCode
import tph.Constants.EmojiVoteCodes.EmojiVoteCode
import tph.Constants.MiscVoteCodes._
import tph.Constants._
import tph._

/**
  * Created by Harambe on 1/24/2017.
  */
class MyTurn(ircLogic: ircLogic, theBrain: TheBrain) extends State with LazyLogging {

  val signature = StateSignatures.myTurnSignature


  def VoteEntry(vote: Vote, voteManager: VoteManager): Unit = {

    vote.voteCode match {

      case x: ActionVoteCode =>
        voteManager.VoteEntry(vote)

      case x: EmojiVoteCode =>
        voteManager.VoteEntry(vote)

      case Hurry() =>
        voteManager.VoteEntry(vote)


      case EndTurn() =>
        voteManager.VoteEntry(vote)

      //Probably removing Concede
      //      case Concede(decision) =>
      //        voteManager.VoteEntry(vote)


      case _ =>
        logger.debug("Unknown Vote Entry in Class MyTurn. Unknown vote: " + vote + " Votecode: " + vote.voteCode)


    }
  }


  def Activate(): Unit = {
    if (theBrain.logFileReader.readerReady) {
      logger.debug("Activating My Turn Status")
      //TestBrain calls Decide() directly.
      theBrain.voteManager.Reset()
      if (!theBrain.testMode)
        ircLogic.TurnStart()
      ircLogic.Check()
    }
  }
}
