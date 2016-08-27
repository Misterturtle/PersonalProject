package tph

//

import akka.actor._
import akka.event.LoggingReceive
import akka.pattern.ask
import akka.util.Timeout
import tph.Controller.ChangeMenu
import tph.LogFileEvents._

import scala.concurrent.Await
import scala.concurrent.duration._


object Controller {

  trait Message
  object Start extends Message

  case class ChangeMenu(previousMenu: String, changeToMenu: String)




}

class Controller(system: ActorSystem, hearthstone: ActorRef, logFileReader: ActorRef, ircLogic: ActorRef) extends Actor with ActorLogging {

  var currentMenu = ""
  var previousMenu = ""

  override def receive: Receive = LoggingReceive({

    case "init" => // Entry point to start everything
      hearthstone ! "Start"
      logFileReader ! "LogFileReader.start"
      ircLogic ! "Activate"

    case ChangeMenu(pastMenu, changeToMenu) =>
      if (pastMenu == previousMenu)
        currentMenu = changeToMenu
      if (pastMenu != previousMenu)
        throw new IllegalArgumentException

    case "CurrentMenu" =>
      sender ! currentMenu

    case "Start Game" =>
      ircLogic ! "Start Game"
    case "Game Over" =>
      ircLogic ! "Game Over"

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


