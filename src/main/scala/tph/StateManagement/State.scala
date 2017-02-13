package tph.StateManagement

import com.typesafe.scalalogging.LazyLogging

/**
  * Created by Harambe on 1/22/2017.
  */
trait State extends LazyLogging {

  var active = false

  def signature: String

  def Activate()
}
