package tph.StateManagement

import tph.Constants._
import tph.TheBrain

/**
  * Created by Harambe on 1/22/2017.
  */
class InitState(theBrain: TheBrain) extends State {
  var signature = StateSignatures.initSignature

  def Activate(): Unit = {
    theBrain.ChangeMenu(MenuNames.MAIN_MENU)
    theBrain.ChangeState(theBrain.inMenu)
  }
}
