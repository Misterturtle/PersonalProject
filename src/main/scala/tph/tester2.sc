import FileReaders.HSAction.{TurnStart, IRCAction}
import Logic.IRCState

import scala.collection.mutable.ListBuffer

val intList = ListBuffer[Int](1,2,3,2,4,2,5,6)
val mulliganComplete = true
def update(actionList: ListBuffer[Int]): Unit = {
  actionList.foreach {
    case action: Int =>
      if (mulliganComplete) {
        println(s"Int is $action")
        val turnStarts = actionList.filter {
          case action: Int =>
            if(action == 2) true
            else
              false
        }
        println(s"Removing ${turnStarts.size} startTurns from ircAction list")
        actionList --= turnStarts
      }
      else {
        val i = actionList.indexOf(action)
        actionList.remove(i)
      }

    case _ =>
      println("Hit the other case")
  }
}

update(intList)