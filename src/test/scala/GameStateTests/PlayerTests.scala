package GameStateTests

import FileReaders.CardInfo
import net.liftweb.json.JsonAST.JObject
import org.scalatest.{FlatSpec, Matchers}
import tph._

/**
  * Created by Harambe on 2/20/2017.
  */
class PlayerTests extends FlatSpec with Matchers {

  import Constants.TestConstants._

  "A Player" should "Add card" in {
    val newFriendlyHandCard = new Card("Friendly Card 7", 7, 3, tph.Constants.INT_UNINIT, 1, Constants.STRING_UNINIT)
    val expectedFriendlyHand = List(
      new Card("Friendly Card 1", 1, 1, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT),
      new Card("Friendly Card 2", 2, 2, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT),
      new Card("Friendly Card 3", 3, 4, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT),
      new Card("Friendly Card 4", 4, 5, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT),
      new Card("Friendly Card 5", 5, 6, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT),
      new Card("Friendly Card 6", 6, 7, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT),
      new Card("Friendly Card 7", 7, 3, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT))
    val actualFriendlyHand = defaultGameState.friendlyPlayer.AddCard(newFriendlyHandCard, true).hand

    val newFriendlyBoardCard = new Card("Friendly Minion 5", 15, Constants.INT_UNINIT, 2, 1, Constants.STRING_UNINIT)
    val expectedFriendlyBoard = List(
      new Card("Friendly Minion 1", 11, Constants.INT_UNINIT, 1, 1, Constants.STRING_UNINIT),
      new Card("Friendly Minion 2", 12, Constants.INT_UNINIT, 3, 1, Constants.STRING_UNINIT),
      new Card("Friendly Minion 3", 13, Constants.INT_UNINIT, 4, 1, Constants.STRING_UNINIT),
      new Card("Friendly Minion 4", 14, Constants.INT_UNINIT, 5, 1, Constants.STRING_UNINIT),
      new Card("Friendly Minion 5", 15, Constants.INT_UNINIT, 2, 1, Constants.STRING_UNINIT))
    val actualFriendlyBoard = defaultGameState.friendlyPlayer.AddCard(newFriendlyBoardCard, false).board

    val newEnemyHandCard = new Card("Enemy Card 7", 27, 3, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT)
    val expectedEnemyHandCard = List(
      new Card("Enemy Card 1", 21, 1, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT),
      new Card("Enemy Card 2", 22, 2, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT),
      new Card("Enemy Card 3", 23, 4, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT),
      new Card("Enemy Card 4", 24, 5, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT),
      new Card("Enemy Card 5", 25, 6, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT),
      new Card("Enemy Card 6", 26, 7, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT),
      new Card("Enemy Card 7", 27, 3, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT))
    val actualEnemyHand = defaultGameState.enemyPlayer.AddCard(newEnemyHandCard, true).hand

    val newEnemyBoardCard = new Card("Enemy Minion 5", 35, Constants.INT_UNINIT, 2, 2, Constants.STRING_UNINIT)
    val expectedEnemyBoard = List(
      new Card("Enemy Minion 1", 31, Constants.INT_UNINIT, 1, 2, Constants.STRING_UNINIT),
      new Card("Enemy Minion 2", 32, Constants.INT_UNINIT, 3, 2, Constants.STRING_UNINIT),
      new Card("Enemy Minion 3", 33, Constants.INT_UNINIT, 4, 2, Constants.STRING_UNINIT),
      new Card("Enemy Minion 4", 34, Constants.INT_UNINIT, 5, 2, Constants.STRING_UNINIT),
      new Card("Enemy Minion 5", 35, Constants.INT_UNINIT, 2, 2, Constants.STRING_UNINIT))
    val actualEnemyBoard = defaultGameState.enemyPlayer.AddCard(newEnemyBoardCard, false).board

    actualFriendlyHand shouldEqual expectedFriendlyHand
    actualFriendlyBoard shouldEqual expectedFriendlyBoard
    actualEnemyHand shouldEqual expectedEnemyHandCard
    actualEnemyBoard shouldEqual expectedEnemyBoard
  }

  it should "Remove card" in {


    val newFriendlyHandCard = new Card("Friendly Card 3", 3, 3, tph.Constants.INT_UNINIT, 1, Constants.STRING_UNINIT)
    val expectedFriendlyHand = List(
      new Card("Friendly Card 1", 1, 1, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT),
      new Card("Friendly Card 2", 2, 2, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT),
      new Card("Friendly Card 4", 4, 3, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT),
      new Card("Friendly Card 5", 5, 4, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT),
      new Card("Friendly Card 6", 6, 5, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT))
    val actualFriendlyHand = defaultGameState.friendlyPlayer.RemoveCard(newFriendlyHandCard).hand

    val newFriendlyBoardCard = new Card("Friendly Minion 2", 12, Constants.INT_UNINIT, 2, 1, Constants.STRING_UNINIT)
    val expectedFriendlyBoard = List(
      new Card("Friendly Minion 1", 11, Constants.INT_UNINIT, 1, 1, Constants.STRING_UNINIT),
      new Card("Friendly Minion 3", 13, Constants.INT_UNINIT, 2, 1, Constants.STRING_UNINIT),
      new Card("Friendly Minion 4", 14, Constants.INT_UNINIT, 3, 1, Constants.STRING_UNINIT))
    val actualFriendlyBoard = defaultGameState.friendlyPlayer.RemoveCard(newFriendlyBoardCard).board

    val newEnemyHandCard = new Card("Enemy Card 3", 23, 3, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT)
    val expectedEnemyHandCard = List(
      new Card("Enemy Card 1", 21, 1, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT),
      new Card("Enemy Card 2", 22, 2, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT),
      new Card("Enemy Card 4", 24, 3, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT),
      new Card("Enemy Card 5", 25, 4, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT),
      new Card("Enemy Card 6", 26, 5, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT))
    val actualEnemyHand = defaultGameState.enemyPlayer.RemoveCard(newEnemyHandCard).hand

    val newEnemyBoardCard = new Card("Enemy Minion 2", 32, Constants.INT_UNINIT, 2, 2, Constants.STRING_UNINIT)
    val expectedEnemyBoard = List(
      new Card("Enemy Minion 1", 31, Constants.INT_UNINIT, 1, 2, Constants.STRING_UNINIT),
      new Card("Enemy Minion 3", 33, Constants.INT_UNINIT, 2, 2, Constants.STRING_UNINIT),
      new Card("Enemy Minion 4", 34, Constants.INT_UNINIT, 3, 2, Constants.STRING_UNINIT))
    val actualEnemyBoard = defaultGameState.enemyPlayer.RemoveCard(newEnemyBoardCard).board

    actualFriendlyHand shouldEqual expectedFriendlyHand
    actualFriendlyBoard shouldEqual expectedFriendlyBoard
    actualEnemyHand shouldEqual expectedEnemyHandCard
    actualEnemyBoard shouldEqual expectedEnemyBoard

  }

  it should "add card to next hand position" in {


    val startingFriendlyHand = List(
      new Card("Friendly Card 1", 1, 1, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT),
      new Card("Friendly Card 2", 2, 2, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT),
      new Card("Friendly Card 3", 3, 3, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT),
      new Card("Friendly Card 6", 6, 6, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT),
      new Card("Friendly Card 7", 7, 7, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT))


    val startingEnemyHand = List(
      new Card("Enemy Card 1", 21, 1, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT),
      new Card("Enemy Card 2", 22, 2, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT),
      new Card("Enemy Card 3", 23, 3, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT),
      new Card("Enemy Card 6", 26, 6, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT),
      new Card("Enemy Card 7", 27, 7, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT))


    val gameState = new GameState()
    gameState.friendlyPlayer = new Player(1,hand = startingFriendlyHand)
    gameState.enemyPlayer = new Player(2,hand = startingEnemyHand)


    val part1FriendlyPlayer = gameState.friendlyPlayer.AddCardToNextHandPosition("Friendly Card 4", 4, Constants.STRING_UNINIT, new CardInfo(Some(Constants.STRING_UNINIT), Some(Constants.STRING_UNINIT), Some(Constants.INT_UNINIT), Some(Nil), Some(Constants.INT_UNINIT), Some(Constants.STRING_UNINIT), Some(new JObject(Nil))))
    val newFriendlyPlayer = part1FriendlyPlayer.AddCardToNextHandPosition("Friendly Card 5", 5, Constants.STRING_UNINIT, new CardInfo(Some(Constants.STRING_UNINIT), Some(Constants.STRING_UNINIT), Some(Constants.INT_UNINIT), Some(Nil), Some(Constants.INT_UNINIT), Some(Constants.STRING_UNINIT), Some(new JObject(Nil))))


    val part1EnemyPlayer = gameState.enemyPlayer.AddCardToNextHandPosition("Enemy Card 4", 24, Constants.STRING_UNINIT, new CardInfo(Some(Constants.STRING_UNINIT), Some(Constants.STRING_UNINIT), Some(Constants.INT_UNINIT), Some(Nil), Some(Constants.INT_UNINIT), Some(Constants.STRING_UNINIT), Some(new JObject(Nil))))
    val newEnemyPlayer = part1EnemyPlayer.AddCardToNextHandPosition("Enemy Card 5", 25, Constants.STRING_UNINIT, new CardInfo(Some(Constants.STRING_UNINIT), Some(Constants.STRING_UNINIT), Some(Constants.INT_UNINIT), Some(Nil), Some(Constants.INT_UNINIT), Some(Constants.STRING_UNINIT), Some(new JObject(Nil))))

    val actualFriendlyHand = newFriendlyPlayer.hand
    val expectedFriendlyHand = List(
      new Card("Friendly Card 1", 1, 1, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT),
      new Card("Friendly Card 2", 2, 2, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT),
      new Card("Friendly Card 3", 3, 3, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT),
      new Card("Friendly Card 6", 6, 6, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT),
      new Card("Friendly Card 7", 7, 7, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT),
      new Card("Friendly Card 4", 4, 4, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT),
      new Card("Friendly Card 5", 5, 5, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT))

    val actualEnemyHand = newEnemyPlayer.hand
    val expectedEnemyHand = List(
      new Card("Enemy Card 1", 21, 1, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT),
      new Card("Enemy Card 2", 22, 2, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT),
      new Card("Enemy Card 3", 23, 3, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT),
      new Card("Enemy Card 6", 26, 6, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT),
      new Card("Enemy Card 7", 27, 7, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT),
      new Card("Enemy Card 4", 24, 4, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT),
      new Card("Enemy Card 5", 25, 5, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT))


    actualFriendlyHand shouldEqual expectedFriendlyHand
    actualEnemyHand shouldEqual expectedEnemyHand
  }
}