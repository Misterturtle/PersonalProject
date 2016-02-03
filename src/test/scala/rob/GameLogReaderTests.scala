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
class GameLogReaderTests extends org.scalatest.path.FreeSpec with Matchers with Eventually  {
  implicit override val patienceConfig = PatienceConfig(timeout = 5 seconds, interval = 100 millis)

  val file = "/tmp/GameLogReaderTests.txt"
  val system = ActorSystem("GameLogReaderTests")
  implicit val ec = system.dispatcher
  val buffer = ListBuffer[String]()
  val targetRef = system.actorOf(Props(new Target(buffer)), "Target")
  val writer = new PrintWriter(new FileWriter(file))

  "Reads a file" in {
    Files.write(Paths.get(file), "a line\nanother line\n".getBytes(StandardCharsets.UTF_8))
    val reader = new GameLogReader(new File(file), targetRef, system.dispatcher)
    eventually { buffer shouldBe ListBuffer("a line", "another line") }
    reader.stop()
  }

  "Reads a file as it is written" in {
    writer.println("a line")
    writer.flush()

    val reader = new GameLogReader(new File(file), targetRef, system.dispatcher)
    eventually { buffer shouldBe ListBuffer("a line") }

    writer.println("another line")
    writer.flush()
    eventually { buffer shouldBe ListBuffer("a line", "another line") }

    reader.stop()
  }

  system.terminate()

  class Target(buffer:ListBuffer[String]) extends Actor {
    override def receive = {
      case Message(x) => buffer.append(x)
      case m => fail(s"Unexpected message: $m")
    }
  }

}
