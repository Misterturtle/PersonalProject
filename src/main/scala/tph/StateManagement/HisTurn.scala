package tph.StateManagement

import tph.Constants._
import tph._

/**
  * Created by Harambe on 1/24/2017.
  */
class HisTurn(ircLogic: ircLogic) extends State {

  val signature = StateSignatures.hisTurnSignature

  def VoteEntry(vote: Vote, voteManager: VoteManager): Unit = {

    vote.voteCode match {

      case x: EmojiVote =>

        voteManager.VoteEntry(vote)
    }
  }


  def Activate(): Unit = {
    ircLogic.ResetEmojiVotes()
  }
}
