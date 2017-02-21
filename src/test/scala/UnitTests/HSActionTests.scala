package UnitTests

import jdk.nashorn.internal.ir.annotations.Ignore
import org.scalatest.{Matchers, FlatSpec}
import tph.HSAction.{CardDeath, KnownCardDrawn}
import tph._


/**
  * Created by Harambe on 2/20/2017.
  */

class HSActionTests extends FlatSpec with Matchers {


  val defaultGameState = new GameState(new Player(1,
    List(
      new Card("Friendly Hand 1", 1, 1, Constants.INT_UNINIT, 1),
      new Card("Friendly Hand 2", 2, 2, Constants.INT_UNINIT, 1),
      new Card("Friendly Hand 3", 3, 3, Constants.INT_UNINIT, 1),
      new Card("Friendly Hand 4", 4, 4, Constants.INT_UNINIT, 1),
      new Card("Friendly Hand 5", 5, 5, Constants.INT_UNINIT, 1),
      new Card("Friendly Hand 6", 6, 6, Constants.INT_UNINIT, 1)),
    List(
      new Card("Friendly Board 1", 11, Constants.INT_UNINIT, 1, 1),
      new Card("Friendly Board 2", 12, Constants.INT_UNINIT, 2, 1),
      new Card("Friendly Board 3", 13, Constants.INT_UNINIT, 3, 1),
      new Card("Friendly Board 4", 14, Constants.INT_UNINIT, 4, 1))),
    new Player(2,
      List(
        new Card("Enemy Hand 1", 21, 1, Constants.INT_UNINIT, 2),
        new Card("Enemy Hand 2", 22, 2, Constants.INT_UNINIT, 2),
        new Card("Enemy Hand 3", 23, 3, Constants.INT_UNINIT, 2),
        new Card("Enemy Hand 4", 24, 4, Constants.INT_UNINIT, 2),
        new Card("Enemy Hand 5", 25, 5, Constants.INT_UNINIT, 2),
        new Card("Enemy Hand 6", 26, 6, Constants.INT_UNINIT, 2)),
      List(
        new Card("Enemy Board 1", 31, Constants.INT_UNINIT, 1, 2),
        new Card("Enemy Board 2", 32, Constants.INT_UNINIT, 2, 2),
        new Card("Enemy Board 3", 33, Constants.INT_UNINIT, 3, 2),
        new Card("Enemy Board 4", 34, Constants.INT_UNINIT, 4, 2))))






  "HSAction KnownCardDrawn" should "ExecuteAction" in {


    val friendlyHSAction = new KnownCardDrawn("Friendly Hand 7", 7, 7, 1)
    val enemyHSAction = new KnownCardDrawn("Enemy Hand 7", 27, 7, 2)

    val newFriendlyHand = defaultGameState.friendlyPlayer.hand ::: List(new Card("Friendly Hand 7", 7, 7, tph.Constants.INT_UNINIT, 1))
    val newEnemyHand = defaultGameState.enemyPlayer.hand ::: List(new Card("Enemy Hand 7", 27, 7, tph.Constants.INT_UNINIT, 2))

    val testFriendlyHand = friendlyHSAction.ExecuteAction(defaultGameState).friendlyPlayer.hand
    val testEnemyHand = enemyHSAction.ExecuteAction(defaultGameState).enemyPlayer.hand

     newFriendlyHand shouldEqual testFriendlyHand

    newEnemyHand shouldEqual testEnemyHand

  }

  "HSAction CardDeath" should "ExecuteAction" in {
    val friendlyHandCardDeath = new CardDeath("Friendly Hand 4", 4, 1)
    val friendlyBoardCardDeath = new CardDeath("Friendly Board 2", 12, 1)
    val enemyHandCardDeath = new CardDeath("Enemy Hand 4", 24, 2)
    val enemyBoardCardDeath = new CardDeath("Enemy Board 2", 32, 2)


    val expectedFriendlyHand = List(
      new Card("Friendly Hand 1", 1, 1, Constants.INT_UNINIT, 1),
      new Card("Friendly Hand 2", 2, 2, Constants.INT_UNINIT, 1),
      new Card("Friendly Hand 3", 3, 3, Constants.INT_UNINIT, 1),
      new Card("Friendly Hand 5", 5, 4, Constants.INT_UNINIT, 1),
      new Card("Friendly Hand 6", 6, 5, Constants.INT_UNINIT, 1))

    val expectedFriendlyBoard = List(
      new Card("Friendly Board 1", 11, Constants.INT_UNINIT, 1, 1),
      new Card("Friendly Board 3", 13, Constants.INT_UNINIT, 2, 1),
      new Card("Friendly Board 4", 14, Constants.INT_UNINIT, 3, 1))

    val expectedEnemyHand =
      List(
        new Card("Enemy Hand 1", 21, 1, Constants.INT_UNINIT, 2),
        new Card("Enemy Hand 2", 22, 2, Constants.INT_UNINIT, 2),
        new Card("Enemy Hand 3", 23, 3, Constants.INT_UNINIT, 2),
        new Card("Enemy Hand 5", 25, 4, Constants.INT_UNINIT, 2),
        new Card("Enemy Hand 6", 26, 5, Constants.INT_UNINIT, 2))

    val expectedEnemyBoard =
      List(
        new Card("Enemy Board 1", 31, Constants.INT_UNINIT, 1, 2),
        new Card("Enemy Board 3", 33, Constants.INT_UNINIT, 2, 2),
        new Card("Enemy Board 4", 34, Constants.INT_UNINIT, 3, 2))


    val actualFriendlyHand = friendlyHandCardDeath.ExecuteAction(defaultGameState).friendlyPlayer.hand
    val actualFriendlyBoard = friendlyBoardCardDeath.ExecuteAction(defaultGameState).friendlyPlayer.board
    val actualEnemyHand = enemyHandCardDeath.ExecuteAction(defaultGameState).enemyPlayer.hand
    val actualEnemyBoard = enemyBoardCardDeath.ExecuteAction(defaultGameState).enemyPlayer.board

    actualFriendlyHand shouldEqual expectedFriendlyHand
    actualFriendlyBoard shouldEqual expectedFriendlyBoard
    actualEnemyHand shouldEqual expectedEnemyHand
    actualEnemyBoard shouldEqual expectedEnemyBoard

  }


}