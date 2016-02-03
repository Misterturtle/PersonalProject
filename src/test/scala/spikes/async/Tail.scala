package spikes.async

import java.io.{File, FileReader, BufferedReader}

import akka.actor.Actor.Receive
import akka.actor.{Props, Actor, ActorSystem}
import org.apache.commons.io.input.{Tailer, TailerListenerAdapter}

import scala.concurrent.Future

/**
  * Created by rconaway on 2/1/16.
  */

object Tail extends App {
  new Tail()
}

class Tail {

  val file=new File("""C:\\Program Files (x86)\\Hearthstone\\Hearthstone_Data\\output_log.txt""")

  val system = ActorSystem("Tail")
  implicit val ec = system.dispatcher

  val actorRef = system.actorOf(Props[A], "actor")

  val listener = new TailerListenerAdapter {

    override def handle(line:String):Unit = {
     actorRef ! line
    }

  }


  val tailer = Tailer.create(file, listener, 100)
  val future = Future {tailer.run()}

  Thread.sleep(10000)
  tailer.stop()

  system.terminate()

}

class A extends Actor {
  def receive: Receive = {
    case x => println(x)
  }
}

