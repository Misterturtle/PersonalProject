package LogReaderTests

import java.io._

import FileReaders.HSAction.{IRCAction, GameStateAction, CardDrawn, HSAction}
import FileReaders.{HSDataBase, LogFileReader, LogParser}
import Logic.IRCState
import VoteSystem.{VoteValidator, VoteAI, VoteState, VoteManager}
import com.typesafe.config.ConfigFactory
import org.scalatest.{FreeSpec, FlatSpec, Matchers}
import tph._

import scala.collection.mutable.ListBuffer

/**
  * Created by Harambe on 2/22/2017.
  */
class LogFileReaderTests extends FreeSpec with Matchers {

  val config = ConfigFactory.load()
  val defaultFile = new File(config.getString("tph.readerFiles.outputLog"))

  "When a LogFileReader initializes, the logfile should not be ready unless new lines come in" in {
    val writer = new BufferedWriter(new PrintWriter(defaultFile))
    val gs = new GameState()
    val lp = new LogParser(gs)
    val lfr = new LogFileReader(lp, gs)
    writer. write("Line 1\n")
    writer.write("Line 2\n")
    writer.write("Line 3\n")
    writer.write("Line 4\n")
    writer.flush()
    writer.close()


    lfr.init()


    lfr.reader.ready() shouldBe false
  }



  "When a LogFileReader reads, it should sort and store actions based on type" in {
    var gameStateUpdated = false
    var ircStateUpdated = false
    val writer = new BufferedWriter(new PrintWriter(defaultFile))
    val gs = new GameState()
    gs.friendlyPlayer = Player(1)
    gs.enemyPlayer = Player(2)
    case class MockGameStateAction() extends GameStateAction{
      override def updateGameState(gameState: GameState): Unit = {
        gameStateUpdated = true
      }
    }
    case class MockIRCAction() extends IRCAction{
      override def updateIRC(ircState: IRCState): Unit = {
        ircStateUpdated = true
      }
    }
    val lp = new LogParser(gs){
      override def identifyHSAction(actionString: String): HSAction = {
        actionString match{
          case "GameStateAction" =>
            MockGameStateAction()

          case "IRCAction" =>
            MockIRCAction()
        }
      }
    }
    val lfr = new LogFileReader(lp, gs, defaultFile)
    writer.write("GameStateAction\n")
    writer.write("IRCAction")
    writer.flush()
    writer.close()


    lfr.read()
    lfr.updateGameState()


    lfr.gameStateActions.head shouldBe MockGameStateAction()
    lfr.ircActions.head shouldBe MockIRCAction()
  }


  "LogFileReader should be able to update the GameState" in {
    val executedActions = ListBuffer[HSAction]()
    val gs = new GameState()
    val lp = new LogParser(gs)
    val lfr = new LogFileReader(lp, gs)
    case class MockGameStateAction()extends GameStateAction{
      override def updateGameState(gameState: GameState): Unit = executedActions.append(this)
    }
    lfr.gameStateActions.appendAll(List(MockGameStateAction(),MockGameStateAction(),MockGameStateAction()))


    lfr.updateGameState()


    executedActions shouldBe ListBuffer[HSAction](MockGameStateAction(),MockGameStateAction(),MockGameStateAction())
  }








}
