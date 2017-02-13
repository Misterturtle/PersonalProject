package tph.StateManagement

import tph.Constants._
import tph.TheBrain

/**
  * Created by Harambe on 1/22/2017.
  */
class InitState(theBrain: TheBrain) extends State {
  var signature = StateSignatures.initSignature


  val startingState: State = theBrain.inMenu


  def Activate(): Unit = {
    logger.debug("Activating InitState Status")
    theBrain.logFileReader.Init()
    theBrain.hearthstone.Start()
    theBrain.gameStatus.Reset()
    theBrain.ChangeState(startingState)

  }
}
