package UnitTests

import org.scalatest.{Matchers, FlatSpec}
import tph.HSAction.CardDeath
import tph.{Constants, Card, Player, GameState}

/**
  * Created by Harambe on 2/21/2017.
  */
class GameStateTests extends FlatSpec with Matchers {


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


    "GetCardByID" should "find and return a card" in {

      val expectedEnemyBoardCard = new Card("Enemy Board 3", 33, Constants.INT_UNINIT, 3,2)
      val actualEnemyBoardCard = defaultGameState.GetCardByID(33)

      val expectedEnemyHandCard = new Card("Enemy Hand 4", 24, 4, Constants.INT_UNINIT,2)
      val actualEnemyHandCard = defaultGameState.GetCardByID(24)

      val expectedFriendlyHandCard = new Card("Friendly Hand 4", 4, 4, Constants.INT_UNINIT,1)
      val actualFriendlyHandCard = defaultGameState.GetCardByID(4)

      val expectedFriendlyBoardCard = new Card("Friendly Board 2", 12, Constants.INT_UNINIT, 2,1)
      val actualFriendlyBoardCard = defaultGameState.GetCardByID(12)

      expectedFriendlyHandCard shouldEqual actualFriendlyHandCard
      expectedFriendlyBoardCard shouldEqual actualFriendlyBoardCard
      expectedEnemyHandCard shouldEqual actualEnemyHandCard
      expectedEnemyBoardCard shouldEqual actualEnemyBoardCard
    }

}
