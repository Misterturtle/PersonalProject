package LogReaderTests

import java.io._

import FileReaders.{LogFileReader, HSAction, LogParser}
import Logic.IRCState
import VoteSystem.{VoteManager, VoteAI, VoteState}
import org.scalatest.{FlatSpec, Matchers}
import tph.{Hearthstone, GameState, Card, Constants}

/**
  * Created by Harambe on 2/21/2017.
  */
class LogParserTests extends FlatSpec with Matchers {

  val mockActionFile = new File(getClass.getResource("/mockActionLog.txt").getPath)

  "A Log Parser" should "Get Player Numbers" in {

    val gs = new GameState()
    val lp = new LogParser(gs)
    val actualPlayerNumbers = lp.getPlayerNumbers(mockActionFile)
    val expectedPlayerNumbers = (1,2)
    actualPlayerNumbers shouldEqual expectedPlayerNumbers
  }

  it should "construct a GameState" in {

    val gs = new GameState()
    val lp = new LogParser(gs)
    val logFileReader = new LogFileReader(lp, gs)
    logFileReader.parseFile(mockActionFile)
    

    val actualFriendlyHand = gs.friendlyPlayer.hand
    val expectedFriendlyHand = Constants.TestConstants.defaultGameState.friendlyPlayer.hand

    val actualFriendlyBoard = gs.friendlyPlayer.board
    val expectedFriendlyBoard = Constants.TestConstants.defaultGameState.friendlyPlayer.board

    val actualEnemyHand = gs.enemyPlayer.hand
    val expectedEnemyHand = List(
      new Card(Constants.STRING_UNINIT, 21, 1, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT),
      new Card(Constants.STRING_UNINIT, 22, 2, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT),
      new Card(Constants.STRING_UNINIT, 23, 3, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT),
      new Card(Constants.STRING_UNINIT, 24, 4, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT),
      new Card(Constants.STRING_UNINIT, 25, 5, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT),
      new Card(Constants.STRING_UNINIT, 26, 6, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT))

    val actualEnemyBoard = gs.enemyPlayer.board
    val expectedEnemyBoard = Constants.TestConstants.defaultGameState.enemyPlayer.board

    actualFriendlyHand shouldEqual expectedFriendlyHand
    actualFriendlyBoard shouldEqual expectedFriendlyBoard
    actualEnemyHand shouldEqual expectedEnemyHand
    actualEnemyBoard shouldEqual expectedEnemyBoard
  }








}
