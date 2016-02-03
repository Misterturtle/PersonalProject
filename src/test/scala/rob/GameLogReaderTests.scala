package rob

import java.io.{PrintWriter, BufferedWriter, FileWriter, File}
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

import akka.actor.{Props, Actor, ActorSystem}
import org.scalatest.concurrent.Eventually
import org.scalatest.{BeforeAndAfterEach, Matchers, FreeSpec}

import scala.collection.mutable.ListBuffer
import scala.concurrent.{Future, Await}
import scala.concurrent.duration._

import GameLogReader._

/**
  * Created by rconaway on 2/1/16.
  */
class GameLogReaderTests extends org.scalatest.path.FreeSpec with Matchers with Eventually {

  import GameLogReader._

  "the reader reads and parses" - {
    class Target(buffer: ListBuffer[Message]) extends Actor {
      override def receive = {
        case x:Message => buffer.append(x)
      }
    }

    implicit val patienceConfig = PatienceConfig(timeout = 1 seconds, interval = 100 millis)

    val file = "/tmp/GameLogReaderTests.txt"
    val system = ActorSystem("GameLogReaderTests")
    implicit val ec = system.dispatcher
    val buffer = ListBuffer[Message]()
    val targetRef = system.actorOf(Props(new Target(buffer)), "Target")
    val writer = new PrintWriter(new FileWriter(file))
    val line1 = "[Power] GameState.DebugPrintPower() - TAG_CHANGE Entity=The Innkeeper tag=PLAYSTATE value=PLAYING"
    val expected1 = PlayState("The Innkeeper", "PLAYING")
    val line2 = "[Power] GameState.DebugPrintPower() - TAG_CHANGE Entity=The Innkeeper tag=PLAYSTATE value=DEAD"
    val expected2 = PlayState("The Innkeeper", "DEAD")

    "an existing file" in {
      writer.println(line1)
      writer.println(line2)
      writer.flush()
      val reader = new GameLogReader(new File(file), targetRef, system.dispatcher)
      eventually {
        buffer shouldBe ListBuffer(expected1, expected2)
      }
      reader.stop()
    }

    "a file as it is written" in {
      writer.println(line1)
      writer.flush()

      val reader = new GameLogReader(new File(file), targetRef, system.dispatcher)
      eventually {
        buffer shouldBe ListBuffer(expected1)
      }

      writer.println(line2)
      writer.flush()
      eventually {
        buffer shouldBe ListBuffer(expected1, expected2)
      }

      reader.stop()
    }

    system.terminate()
  }

  "parsing of log lines" - {
    "parses a TAG_CHANGE for PLAYSTATE" in {
      val line = "[Power] GameState.DebugPrintPower() - TAG_CHANGE Entity=The Innkeeper tag=PLAYSTATE value=PLAYING"
      parse(line) shouldBe Some(PlayState("The Innkeeper", "PLAYING"))
    }
  }

}
