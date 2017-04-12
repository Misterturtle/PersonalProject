package VoteSystem

import FileReaders.HSAction.{Entity, OptionChoice, OptionTarget, VoteAction}
import tph.Constants

import scala.collection.mutable.ListBuffer

/**
  * Created by Harambe on 3/30/2017.
  */

class VoteState {

  var voterMap = Map[String, Voter]()
  var voterHistory = List[Map[String,Voter]]()
  val optionDumpList = ListBuffer[VoteAction]()
  val possibleOptions = ListBuffer[CompleteOption]()


  def averageVotersFromHistory:Int = voterHistory.foldLeft(0){case (r,c) => r + c.size}/voterHistory.size



  def createPossibleOptions(): Unit ={

    var lastChoice: Option[OptionChoice] = None
    val possibleTargets: ListBuffer[OptionTarget] = ()
    val completeOptions: ListBuffer[CompleteOption] = ()

    optionDumpList.foreach{
      case choice:OptionChoice =>
        if(lastChoice.nonEmpty) {
          CompleteOption(lastChoice.get, possibleTargets.toList)
          possibleTargets.clear()
        }
        lastChoice = Some(choice)

      case target:OptionTarget =>
        possibleTargets.append(target)
    }


    completeOptions.foreach{
      case x =>
        if(x.optionChoice.error)



    }


  }



}

case class CompleteOption(optionChoice: OptionChoice, possibleTargets: List[OptionTarget])