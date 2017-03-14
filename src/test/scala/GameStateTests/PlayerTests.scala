package GameStateTests

import org.scalatest.{FlatSpec, Matchers}
import tph._

/**
  * Created by Harambe on 2/20/2017.
  */
class PlayerTests extends FlatSpec with Matchers {


  import Constants.TestConstants._


  "A Player" should "Add card" in {
    val newFriendlyHandCard = new Card("Friendly Hand 7", 7, 3, tph.Constants.INT_UNINIT, 1)
    val expectedFriendlyHand = List(
      new Card("Friendly Hand 1", 1, 1, Constants.INT_UNINIT, 1),
      new Card("Friendly Hand 2", 2, 2, Constants.INT_UNINIT, 1),
      new Card("Friendly Hand 3", 3, 4, Constants.INT_UNINIT, 1),
      new Card("Friendly Hand 4", 4, 5, Constants.INT_UNINIT, 1),
      new Card("Friendly Hand 5", 5, 6, Constants.INT_UNINIT, 1),
      new Card("Friendly Hand 6", 6, 7, Constants.INT_UNINIT, 1),
      new Card("Friendly Hand 7", 7, 3, Constants.INT_UNINIT, 1))
    val actualFriendlyHand = defaultGameState.friendlyPlayer.AddCard(newFriendlyHandCard, true).hand

    val newFriendlyBoardCard = new Card("Friendly Board 5", 15, Constants.INT_UNINIT, 2, 1)
    val expectedFriendlyBoard = List(
      new Card("Friendly Board 1", 11, Constants.INT_UNINIT, 1, 1),
      new Card("Friendly Board 2", 12, Constants.INT_UNINIT, 3, 1),
      new Card("Friendly Board 3", 13, Constants.INT_UNINIT, 4, 1),
      new Card("Friendly Board 4", 14, Constants.INT_UNINIT, 5, 1),
      new Card("Friendly Board 5", 15, Constants.INT_UNINIT, 2, 1))
    val actualFriendlyBoard = defaultGameState.friendlyPlayer.AddCard(newFriendlyBoardCard, false).board

    val newEnemyHandCard = new Card("Enemy Hand 7", 27, 3, Constants.INT_UNINIT, 2)
    val expectedEnemyHandCard = List(
      new Card("Enemy Hand 1", 21, 1, Constants.INT_UNINIT, 2),
      new Card("Enemy Hand 2", 22, 2, Constants.INT_UNINIT, 2),
      new Card("Enemy Hand 3", 23, 4, Constants.INT_UNINIT, 2),
      new Card("Enemy Hand 4", 24, 5, Constants.INT_UNINIT, 2),
      new Card("Enemy Hand 5", 25, 6, Constants.INT_UNINIT, 2),
      new Card("Enemy Hand 6", 26, 7, Constants.INT_UNINIT, 2),
      new Card("Enemy Hand 7", 27, 3, Constants.INT_UNINIT, 2))
    val actualEnemyHand = defaultGameState.enemyPlayer.AddCard(newEnemyHandCard, true).hand

    val newEnemyBoardCard = new Card("Enemy Board 5", 35, Constants.INT_UNINIT, 2, 2)
    val expectedEnemyBoard = List(
      new Card("Enemy Board 1", 31, Constants.INT_UNINIT, 1, 2),
      new Card("Enemy Board 2", 32, Constants.INT_UNINIT, 3, 2),
      new Card("Enemy Board 3", 33, Constants.INT_UNINIT, 4, 2),
      new Card("Enemy Board 4", 34, Constants.INT_UNINIT, 5, 2),
      new Card("Enemy Board 5", 35, Constants.INT_UNINIT, 2, 2))
    val actualEnemyBoard = defaultGameState.enemyPlayer.AddCard(newEnemyBoardCard, false).board

    actualFriendlyHand shouldEqual expectedFriendlyHand
    actualFriendlyBoard shouldEqual expectedFriendlyBoard
    actualEnemyHand shouldEqual expectedEnemyHandCard
    actualEnemyBoard shouldEqual expectedEnemyBoard
  }

  it should "Remove card" in {


    val newFriendlyHandCard = new Card("Friendly Hand 3", 3, 3, tph.Constants.INT_UNINIT, 1)
    val expectedFriendlyHand = List(
      new Card("Friendly Hand 1", 1, 1, Constants.INT_UNINIT, 1),
      new Card("Friendly Hand 2", 2, 2, Constants.INT_UNINIT, 1),
      new Card("Friendly Hand 4", 4, 3, Constants.INT_UNINIT, 1),
      new Card("Friendly Hand 5", 5, 4, Constants.INT_UNINIT, 1),
      new Card("Friendly Hand 6", 6, 5, Constants.INT_UNINIT, 1))
    val actualFriendlyHand = defaultGameState.friendlyPlayer.RemoveCard(newFriendlyHandCard).hand

    val newFriendlyBoardCard = new Card("Friendly Board 2", 12, Constants.INT_UNINIT, 2, 1)
    val expectedFriendlyBoard = List(
      new Card("Friendly Board 1", 11, Constants.INT_UNINIT, 1, 1),
      new Card("Friendly Board 3", 13, Constants.INT_UNINIT, 2, 1),
      new Card("Friendly Board 4", 14, Constants.INT_UNINIT, 3, 1))
    val actualFriendlyBoard = defaultGameState.friendlyPlayer.RemoveCard(newFriendlyBoardCard).board

    val newEnemyHandCard = new Card("Enemy Hand 3", 23, 3, Constants.INT_UNINIT, 2)
    val expectedEnemyHandCard = List(
      new Card("Enemy Hand 1", 21, 1, Constants.INT_UNINIT, 2),
      new Card("Enemy Hand 2", 22, 2, Constants.INT_UNINIT, 2),
      new Card("Enemy Hand 4", 24, 3, Constants.INT_UNINIT, 2),
      new Card("Enemy Hand 5", 25, 4, Constants.INT_UNINIT, 2),
      new Card("Enemy Hand 6", 26, 5, Constants.INT_UNINIT, 2))
    val actualEnemyHand = defaultGameState.enemyPlayer.RemoveCard(newEnemyHandCard).hand

    val newEnemyBoardCard = new Card("Enemy Board 2", 32, Constants.INT_UNINIT, 2, 2)
    val expectedEnemyBoard = List(
      new Card("Enemy Board 1", 31, Constants.INT_UNINIT, 1, 2),
      new Card("Enemy Board 3", 33, Constants.INT_UNINIT, 2, 2),
      new Card("Enemy Board 4", 34, Constants.INT_UNINIT, 3, 2))
    val actualEnemyBoard = defaultGameState.enemyPlayer.RemoveCard(newEnemyBoardCard).board

    actualFriendlyHand shouldEqual expectedFriendlyHand
    actualFriendlyBoard shouldEqual expectedFriendlyBoard
    actualEnemyHand shouldEqual expectedEnemyHandCard
    actualEnemyBoard shouldEqual expectedEnemyBoard

  }

  it should "add card to next hand position" in {


    val startingFriendlyHand = List(
      new Card("Friendly Card 1", 1, 1, Constants.INT_UNINIT, 1),
      new Card("Friendly Card 2", 2, 2, Constants.INT_UNINIT, 1),
      new Card("Friendly Card 3", 3, 3, Constants.INT_UNINIT, 1),
      new Card("Friendly Card 6", 6, 6, Constants.INT_UNINIT, 1),
      new Card("Friendly Card 7", 7, 7, Constants.INT_UNINIT, 1))


    val startingEnemyHand = List(
      new Card("Enemy Card 1", 21, 1, Constants.INT_UNINIT, 2),
      new Card("Enemy Card 2", 22, 2, Constants.INT_UNINIT, 2),
      new Card("Enemy Card 3", 23, 3, Constants.INT_UNINIT, 2),
      new Card("Enemy Card 6", 26, 6, Constants.INT_UNINIT, 2),
      new Card("Enemy Card 7", 27, 7, Constants.INT_UNINIT, 2))


    val gameState = new GameState(new Player(1, 0, hand = startingFriendlyHand, board = List[HSCard]()), new Player(2, 0, hand = startingEnemyHand, board = List[HSCard]()))


    val part1FriendlyPlayer = gameState.friendlyPlayer.AddCardToNextHandPosition("Friendly Card 4", 4)
    val newFriendlyPlayer = part1FriendlyPlayer.AddCardToNextHandPosition("Friendly Card 5", 5)


    val part1EnemyPlayer = gameState.enemyPlayer.AddCardToNextHandPosition("Enemy Card 4", 24)
    val newEnemyPlayer = part1EnemyPlayer.AddCardToNextHandPosition("Enemy Card 5", 25)

    val actualFriendlyHand = newFriendlyPlayer.hand
    val expectedFriendlyHand = List(
      new Card("Friendly Card 1", 1, 1, Constants.INT_UNINIT, 1),
      new Card("Friendly Card 2", 2, 2, Constants.INT_UNINIT, 1),
      new Card("Friendly Card 3", 3, 3, Constants.INT_UNINIT, 1),
      new Card("Friendly Card 6", 6, 6, Constants.INT_UNINIT, 1),
      new Card("Friendly Card 7", 7, 7, Constants.INT_UNINIT, 1),
      new Card("Friendly Card 4", 4, 4, Constants.INT_UNINIT, 1),
      new Card("Friendly Card 5", 5, 5, Constants.INT_UNINIT, 1))

    val actualEnemyHand = newEnemyPlayer.hand
    val expectedEnemyHand = List(
      new Card("Enemy Card 1", 21, 1, Constants.INT_UNINIT, 2),
      new Card("Enemy Card 2", 22, 2, Constants.INT_UNINIT, 2),
      new Card("Enemy Card 3", 23, 3, Constants.INT_UNINIT, 2),
      new Card("Enemy Card 6", 26, 6, Constants.INT_UNINIT, 2),
      new Card("Enemy Card 7", 27, 7, Constants.INT_UNINIT, 2),
      new Card("Enemy Card 4", 24, 4, Constants.INT_UNINIT, 2),
      new Card("Enemy Card 5", 25, 5, Constants.INT_UNINIT, 2))


    actualFriendlyHand shouldEqual expectedFriendlyHand
    actualEnemyHand shouldEqual expectedEnemyHand
  }
}