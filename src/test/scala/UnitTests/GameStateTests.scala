package UnitTests

import java.io.{FileReader, Reader, BufferedReader, File}

import org.scalatest.{Matchers, FlatSpec}
import tph.HSAction.CardDeath
import tph._

/**
  * Created by Harambe on 2/21/2017.
  */
class GameStateTests extends FlatSpec with Matchers {


  import Constants.TestConstants._


    "GameState" should "get card by ID" in {

      val expectedEnemyBoardCard = new Card("Enemy Board 3", 33, Constants.INT_UNINIT, 3,2)
      val actualEnemyBoardCard = defaultGameState.GetCardByID(33)

      val expectedEnemyHandCard = new Card("Enemy Hand 4", 24, 4, Constants.INT_UNINIT,2)
      val actualEnemyHandCard = defaultGameState.GetCardByID(24)

      val expectedFriendlyHandCard = new Card("Friendly Hand 4", 4, 4, Constants.INT_UNINIT,1)
      val actualFriendlyHandCard = defaultGameState.GetCardByID(4)

      val expectedFriendlyBoardCard = new Card("Friendly Board 2", 12, Constants.INT_UNINIT, 2,1)
      val actualFriendlyBoardCard = defaultGameState.GetCardByID(12)

      actualFriendlyHandCard shouldEqual expectedFriendlyHandCard
      actualFriendlyBoardCard shouldEqual expectedFriendlyBoardCard
      actualEnemyHandCard shouldEqual expectedEnemyHandCard
      actualEnemyBoardCard shouldEqual expectedEnemyBoardCard
    }



}
