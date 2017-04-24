package VoteSystem

import FileReaders.HSAction._
import tph.Constants

import scala.collection.mutable.ListBuffer

/**
  * Created by Harambe on 3/30/2017.
  */

class VoteState {

  var voterMap = Map[String, Voter]()
  var voterHistory = List[Map[String, Voter]]()
  val optionDumpList = ListBuffer[PowerOption]()
  val possibleOptions = ListBuffer[PowerOptionChoice]()


  def averageVotersFromHistory: Int = voterHistory.foldLeft(0) { case (r, c) => r + c.size } / voterHistory.size

  def updatePowerOptions(): Unit = {
    optionDumpList.dropWhile{
      case x:PowerOptionChoice =>
        false
      case _ =>
        true
    }


    var currentPowerOption:Option[PowerOptionChoice] = None
    var currentSubOption:Option[PowerSubOption] = None

    optionDumpList.foreach{
      case x:PowerOptionChoice=>
        if(currentPowerOption.nonEmpty){
          if(currentSubOption.nonEmpty){
            currentPowerOption = Some(currentPowerOption.get.copy(listOfSubOptions = currentPowerOption.get.listOfSubOptions :+ currentSubOption.get))
            currentSubOption = None
          }
          possibleOptions.append(currentPowerOption.get)
        }

        currentPowerOption = Some(x)


      case x:PowerOptionTarget=>
        if(currentSubOption.nonEmpty) {
          currentSubOption = Some(currentSubOption.get.copy(listOfTargets = currentSubOption.get.listOfTargets :+ x))
        }
        else{
          if(currentPowerOption.nonEmpty){
            currentPowerOption = Some(currentPowerOption.get.copy(listOfTargets = currentPowerOption.get.listOfTargets :+ x))
          }
        }

      case x:PowerSubOption=>
        if(currentSubOption.nonEmpty && currentPowerOption.nonEmpty)
          currentPowerOption = Some(currentPowerOption.get.copy(listOfSubOptions = currentPowerOption.get.listOfSubOptions :+ currentSubOption.get))

        currentSubOption = Some(x)
    }





    if(currentSubOption.nonEmpty) {
      currentPowerOption = Some(currentPowerOption.get.copy(listOfSubOptions = currentPowerOption.get.listOfSubOptions :+ currentSubOption.get))
      currentSubOption = None
    }
    if(currentPowerOption.nonEmpty) {
      possibleOptions.append(currentPowerOption.get)
      currentPowerOption = None
    }





  }
}