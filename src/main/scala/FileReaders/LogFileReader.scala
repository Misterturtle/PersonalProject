package FileReaders

import java.io._
import java.util.concurrent.{ScheduledThreadPoolExecutor, TimeUnit}

import com.typesafe.config.ConfigFactory
import FileReaders.HSAction._
import tph.GameState

import scala.collection.mutable.ListBuffer

/**
  * Created by Harambe on 2/22/2017.
  */
class LogFileReader(lp: LogParser, gs: GameState) {

  val config = ConfigFactory.load()
  val defaultFile = new File(config.getString("tph.readerFiles.outputLog"))
  val reader = new BufferedReader(new FileReader(defaultFile))

  val actionLogFile = new File(config.getString("tph.writerFiles.actionLog"))
  val writer = new PrintWriter(new FileWriter(actionLogFile))

  val gameStateActions = ListBuffer[GameStateAction]()
  val ircActions = ListBuffer[IRCAction]()

  var lastTimeActive = System.currentTimeMillis()

  def init() = {
    while (reader.ready()) {
      reader.readLine()
    }
  }


  def update(): Unit = {
    while (reader.ready()) {
      lastTimeActive = System.currentTimeMillis()
      val line = reader.readLine()
      val hsAction = lp.identifyHSAction(line)
      hsAction match {
        case action: GameStateAction =>
          gameStateActions.append(action)

        case action: IRCAction =>
          ircActions.append(action)

        case _ =>
      }
    }
  }


  def parseFile(file: File): Unit = {
    val fileReader = new BufferedReader(new FileReader(file))

    while (fileReader.ready()) {
      lastTimeActive = System.currentTimeMillis()
      val line = fileReader.readLine()
      val hsAction = lp.identifyHSAction(line)
      hsAction match {
        case action: GameStateAction =>
          action.updateGameState(gs)

        case action: IRCAction =>
          ircActions.append(action)

        case _ =>
      }
    }
  }

}

