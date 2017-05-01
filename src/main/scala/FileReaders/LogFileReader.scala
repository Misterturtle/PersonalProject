package FileReaders

import java.io._
import java.util.concurrent.{ScheduledThreadPoolExecutor, TimeUnit}

import GameState.GameState
import com.typesafe.config.ConfigFactory
import FileReaders.HSAction._

import scala.collection.mutable.ListBuffer

/**
  * Created by Harambe on 2/22/2017.
  */
class LogFileReader(lp: LogParser, gs: GameState, file:File = new File(ConfigFactory.load().getString("tph.readerFiles.outputLog"))) {

  val config = ConfigFactory.load()
  val reader = new BufferedReader(new FileReader(file))

  val gameStateActions = ListBuffer[GameStateAction]()
  val ircActions = ListBuffer[IRCAction]()
  val optionDumpList = ListBuffer[PowerOption]()

  var lastTimeActive = System.currentTimeMillis()

  def init() = {
    while (reader.ready()) {
      reader.readLine()
    }
  }

  def read(): Unit = {
    while (reader.ready()) {
      lastTimeActive = System.currentTimeMillis()
      val line = reader.readLine()
      val hsAction = lp.identifyHSAction(line)
      sort(hsAction)
    }
  }

  private def sort(hsAction: HSAction): Unit ={
    hsAction match {
      case action: GameStateAction =>
        gameStateActions.append(action)

      case action: IRCAction =>
        ircActions.append(action)

      case _ =>
    }
  }


  def updateGameState(): Unit ={
    gameStateActions.foreach(
      x => x.updateGameState(gs)
    )
  }
}

