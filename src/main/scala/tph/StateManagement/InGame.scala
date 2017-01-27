package tph.StateManagement

import tph.Constants._
import tph.TheBrain

/**
  * Created by Harambe on 1/22/2017.
  */
class InGame(theBrain: TheBrain) extends State {

  val signature = StateSignatures.inGameSignature
  var discoverOptions = 0

  def Activate(): Unit = {
    theBrain.StartGame()
  }
}
