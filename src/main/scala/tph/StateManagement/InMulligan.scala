package tph.StateManagement

import com.typesafe.scalalogging.LazyLogging
import tph.Constants._
import tph.{TheBrain, Vote, VoteManager}

/**
  * Created by Harambe on 1/24/2017.
  */
class InMulligan(theBrain: TheBrain) extends State with LazyLogging {

  var mulliganOptions = 0
  val signature = StateSignatures.inMulliganSignature


  def VoteEntry(vote: Vote, voteManager: VoteManager): Unit = {

    vote.voteCode match {

      case ActionVoteCodes.MulliganVote(first, second, third, fourth) =>
        if (mulliganOptions == 3)
          voteManager.MulliganVoteEntry(first, second, third, false)
        if (mulliganOptions == 4)
          voteManager.MulliganVoteEntry(first, second, third, fourth)
        if (mulliganOptions != 3 && mulliganOptions != 4)
          logger.debug("Invalid mulligan vote entry. Mulligan options: " + mulliganOptions)

      case _ =>


    }
  }

  def Activate(): Unit = {
    if (theBrain.logFileReader.readerReady) {
      logger.debug("Activating In Mulligan Status")

      theBrain.ircLogic.StartMulligan()
    }
  }


}
