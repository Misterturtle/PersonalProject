package tph.StateManagement

import com.typesafe.scalalogging.LazyLogging
import tph.Constants.EmojiVoteCodes.EmojiVoteCode
import tph.Constants._
import tph._

/**
  * Created by Harambe on 1/24/2017.
  */
class HisTurn(ircLogic: ircLogic, theBrain: TheBrain) extends State with LazyLogging {

  val signature = StateSignatures.hisTurnSignature

  def VoteEntry(vote: Vote, voteManager: VoteManager): Unit = {

    vote.voteCode match {

      case x: EmojiVoteCode =>

        voteManager.VoteEntry(vote)
      case _ =>


    }
  }


  def Activate(): Unit = {
    if (theBrain.logFileReader.readerReady) {
      if (!theBrain.testMode) {
        logger.debug("Activating His Turn Status")
        ircLogic.ResetEmojiVotes()
      }
    }
  }
}
