package tph.StateManagement

import tph.Constants._
import tph._

/**
  * Created by Harambe on 1/22/2017.
  */
class InMenu(theBrain: TheBrain, ircLogic: ircLogic) extends State {

  val signature = StateSignatures.inMenuSignature

  def VoteEntry(vote: Vote, voteManager: VoteManager): Unit = {

    vote match {

      case x: MenuVote =>

        voteManager.VoteEntry(x)


    }
  }


  def Activate(): Unit = {
    if (theBrain.logFileReader.readerReady) {
      logger.debug("Activating In Menu Status")
      ircLogic.ResetMenuVotes()

      ircLogic.StartMenuDecide()

      if (theBrain.currentMenu == "Uninit") {
        theBrain.ChangeMenu(Constants.MenuNames.MAIN_MENU)
      }
      else
        theBrain.ChangeMenu(theBrain.previousMenu)
    }
  }
}
