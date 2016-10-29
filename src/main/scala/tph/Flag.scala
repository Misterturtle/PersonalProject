package tph

/**
  * Created by Harambe on 10/29/2016.
  */


class Flag(name: String) {

  var callsActive = 0
  var booleanInit = false
  var booleanFlagStatus = false



  def GetName():String = {
    return name
  }

  def GetStatusAsBoolean(): Boolean = {

    if(booleanInit)
    return booleanFlagStatus
    else{
      return false
    }
  }




}
