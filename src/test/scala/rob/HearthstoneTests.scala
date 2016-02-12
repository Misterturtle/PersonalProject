package rob

import java.io.File

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import org.scalatest.{FreeSpec, Matchers}

/*
  I've given up on meaningful tests for now.  Too hard to verify.
  These are here to let me manually exercise specific parts of Hearthstone, verifying manually.
 */
class HearthstoneTests extends FreeSpec with Matchers {

  val hearthstone = new Hearthstone
  val config = ConfigFactory.load()
  val system = ActorSystem("HearthstoneTests")
  val controller = system.actorOf(Props(new Controller(hearthstone)), "Controller")
  controller ! Controller.Start

  val gameLogFile = new File(config.getString("tph.game-log.file"))
  val gameLogReader = new GameLogReader(gameLogFile, controller, system.dispatcher)
  gameLogReader.start()

  "Initialize should fail if Hearthstone isn't started.  It should reposition and resize the Hearthstone window" in {
    hearthstone.initialize()
  }

  """
    |IsPlaying should be false if not on game board.
    |IsPlaying should be true if on game board.
  """.stripMargin in {

    var last = false
    while (true) {
      if (hearthstone.isPlaying != last) {
        last = hearthstone.isPlaying
        println(if (last) "Playing" else "Not Playing")
        System.out.flush()
      }
      Thread.sleep(100)
    }

  }

  "Click End Turn" in {
    hearthstone.clickEndTurn()
  }
}
