package tph

import scala.collection.mutable

/**
  * Created by Harambe on 10/29/2016.
  */
class FlagSystem {

  val flags = mutable.Map[String, Flag]()

  def AddFlag(flag:Flag): Unit ={
  flags(flag.name) == flag
}

  def GetFlagStatus(flagName:String): Boolean ={
    return flags(flagName).GetStatus()
  }



}
