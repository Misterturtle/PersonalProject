package GameStateTests

import org.scalatest.{FlatSpec, Matchers}
import tph._

/**
  * Created by Harambe on 2/21/2017.
  */
class GameStateTests extends FlatSpec with Matchers {


  import Constants.TestConstants._


    "GameState" should "get card by ID" in {

      val expectedEnemyBoardCard = new Card("Enemy Minion 3", 33, Constants.INT_UNINIT, 3, 2, Constants.STRING_UNINIT)
      val actualEnemyBoardCard = defaultGameState.getCardByID(33).get

      val expectedEnemyHandCard = new Card("Enemy Card 4", 24, 4, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT)
      val actualEnemyHandCard = defaultGameState.getCardByID(24).get

      val expectedFriendlyHandCard = new Card("Friendly Card 4", 4, 4, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT)
      val actualFriendlyHandCard = defaultGameState.getCardByID(4).get

      val expectedFriendlyBoardCard = new Card("Friendly Minion 2", 12, Constants.INT_UNINIT, 2, 1, Constants.STRING_UNINIT)
      val actualFriendlyBoardCard = defaultGameState.getCardByID(12).get

      actualFriendlyHandCard shouldEqual expectedFriendlyHandCard
      actualFriendlyBoardCard shouldEqual expectedFriendlyBoardCard
      actualEnemyHandCard shouldEqual expectedEnemyHandCard
      actualEnemyBoardCard shouldEqual expectedEnemyBoardCard
    }

  it should "set player numbers" in{
    val gs = new GameState()
    gs.setPlayerNumbers(2)
    gs.friendlyPlayer.playerNumber shouldBe 2
    gs.enemyPlayer.playerNumber shouldBe 1}

  it should "create a changeMap" ignore {
    val friendly = new Player(1,hand = List(
      new Card("Hand1", 1, 1, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT),
      new Card("Hand2", 2, 2, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT)
    ), board = List(
      new Card("Board1", 11, Constants.INT_UNINIT, 1, 1, Constants.STRING_UNINIT),
      new Card("Board2", 12, Constants.INT_UNINIT, 2, 1, Constants.STRING_UNINIT)
    ))

    val enemy = new Player(1,hand = List(
      new Card("Hand1", 1, 1, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT),
      new Card("Hand2", 2, 2, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT)
    ), board = List(
      new Card("Board1", 11, Constants.INT_UNINIT, 1, 1, Constants.STRING_UNINIT),
      new Card("Board2", 12, Constants.INT_UNINIT, 2, 1, Constants.STRING_UNINIT)
    ))
  }



}
