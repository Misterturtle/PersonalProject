package tph.StateManagement

/**
  * Created by Harambe on 1/22/2017.
  */
trait State {

  var active = false

  def signature: String

  def Activate()
}
