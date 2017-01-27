package tph.StateManagement

import tph.Constants._
import tph._

/**
  * Created by Harambe on 1/24/2017.
  */
class MyTurn(ircLogic: ircLogic) extends State {

  val signature = StateSignatures.myTurnSignature


  def VoteEntry(vote: Vote, voteManager: VoteManager): Unit = {

    vote.voteCode match {

      case x: ActionVote =>
        voteManager.VoteEntry(vote)

      case x: EmojiVote =>
        voteManager.VoteEntry(vote)


    }
  }


  def Activate(): Unit = {
    ircLogic.TurnStart()

  }
}
