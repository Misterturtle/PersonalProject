package tph.StateManagement

import tph.Constants._
import tph._

/**
  * Created by Harambe on 1/22/2017.
  */
class InMenu(ircLogic: ircLogic) extends State {

  val signature = StateSignatures.inMenuSignature

  def VoteEntry(vote: Vote, voteManager: VoteManager): Unit = {

    vote match {

      case x: MenuVote =>

        voteManager.VoteEntry(x)


    }
  }


  def Activate(): Unit = {
    ircLogic.ResetMenuVotes()
    ircLogic.StartMenuDecide()
  }


}
