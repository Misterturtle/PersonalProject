import FileReaders.HSAction.{TurnStart, IRCAction}
import Logic.IRCState

import scala.collection.mutable.ListBuffer

val al = ListBuffer[IRCAction](TurnStart("Wizard"), TurnStart("Wizard"),TurnStart("SomeOtherAcc"))

val mulliganComplete = true
def update(actionList: ListBuffer[IRCAction]): Unit = {
  actionList.foreach {
    case action: TurnStart =>
      if (action.playerName == "Wizard") {
        if (mulliganComplete) {
          println("Starting my turn. Clearing all votes")
          val turnStarts = actionList.filter {
            case vote: TurnStart => true
            case _ => false
          }
          println(s"Removing ${turnStarts.size} startTurns from ircAction list")
          actionList --= turnStarts
        }
      }
      else {
        val i = actionList.indexOf(action)
        actionList.remove(i)
      }
  }
}

update(al)