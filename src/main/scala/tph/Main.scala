package tph

import java.io.File

import akka.actor.{ActorRef, Props, ActorSystem}
import com.typesafe.config.ConfigFactory

object Main extends App {

  val config = ConfigFactory.load()
  val screens =
  startHearthstoneIfNecessary()
//  startChatWatcher()
//  startScreenWatcher()
//  startLogWatcher()
//  startGameWatcher()
//
//  navigateToPlayingField()


  def startHearthstoneIfNecessary(): Unit = {

  }
}



object Hearthstone {

  def startIfNecessary() = ???

}


object ChatWatcher {
  // watch IRC
  // store messages in queue

  def nextMessage(): Option[ChatMessage] = {
    // return next message from Queue
    return ???
  }
}

object ScreenWatcher {
  // watch screen by polling
  // when transition,
  //   send transition message to GameWatcher
}

object LogWatcher {
  // watch Log with blocking I/O
  // when relevant message,
  //    send message to GameWatcher
}


object TransitionWatcher {

}

object Vote {

  // start collecting votes for desired target
  //   reset current votes
  //   wait 10 seconds
  //   send vote to CommandExecutor

}

object CommandExecutor {
  def execute(command:Command): Unit = {
    // execute the command

  }
}

class Command

class ChatMessage