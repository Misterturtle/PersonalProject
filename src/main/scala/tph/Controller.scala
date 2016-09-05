package tph

//

import akka.actor._
import akka.event.LoggingReceive
import akka.pattern.ask
import akka.util.Timeout
//import tph.Controller.ChangeMenu
import tph.IrcMessages.ChangeMenu
import tph.LogFileEvents._

import scala.concurrent.Await
import scala.concurrent.duration._


object Controller {

  trait Message
  object Start extends Message
}

class Controller(system: ActorSystem, hearthstone: ActorRef, logFileReader: ActorRef, ircLogic: ActorRef) extends Actor with ActorLogging {

  var currentMenu = "mainMenu"
  var previousMenu = ""

  override def receive: Receive = LoggingReceive({

    case "init" => // Entry point to start everything
      hearthstone ! "Start"
      logFileReader ! "LogFileReader.start"
      ircLogic ! "Start"

    case ChangeMenu(pastMenu, changeToMenu) =>
      if (pastMenu == currentMenu) {
        currentMenu = changeToMenu
        previousMenu = pastMenu
      }

      //Test Purposes
        if(previousMenu == "")
          {
            currentMenu = changeToMenu
            previousMenu = pastMenu
          }

      else throw new IllegalArgumentException

      ircLogic ! ChangeMenu(pastMenu, changeToMenu)
      IrcBot.previousMenu = pastMenu
      IrcBot.currentMenu = changeToMenu

    case "CurrentMenu" =>
      sender ! currentMenu

    case "Start Game" =>
      ircLogic ! "Start Game"
    case "Game Over" =>
      ircLogic ! "Game Over"
    case "Turn Start" =>
      ircLogic ! "Turn Start"
    case "Turn End"=>
      ircLogic ! "Turn End"

    // Messages from LogFileReader
    case DiscoverOption(option) =>
      ircLogic ! DiscoverOption(option)

    case "Mulligan" =>
      ircLogic ! "Mulligan"

    case "NewMulliganOption" =>
      ircLogic ! "NewMulliganOption"


    case "GetGameStatus" =>
      implicit val timeout = Timeout(30 seconds)
      val future = logFileReader ? "GetGameStatus"
      val result = Await.result(future, timeout.duration)
      sender ! result

  })
}


