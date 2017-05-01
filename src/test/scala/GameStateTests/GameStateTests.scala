package GameStateTests

import FileReaders.HSDataBase
import GameState.{Player, GameState}
import com.typesafe.config.ConfigFactory
import org.scalatest.{FreeSpec, FlatSpec, Matchers}
import tph.Constants.ActionVotes.CardPlay
import tph._

/**
  * Created by Harambe on 2/21/2017.
  */
class GameStateTests extends FreeSpec with Matchers {


  import Constants.TestConstants._


  "GameState should" - {

    "get card by ID" in {

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

    "When isChooseOne is called" - {
      val database = new HSDataBase
      val chooseOneCardID = "AT_042"
      val chooseOneCard = Card("Druid of the Saber", 1, 1, Constants.INT_UNINIT, 1, chooseOneCardID, cardInfo = database.cardIDMap(chooseOneCardID))

      "Detect if a card is a choose one" in {
        val notAChooseOneCard = Card("Wisp", 2, 2, Constants.INT_UNINIT, 1, "CS2_231", cardInfo = database.cardIDMap("CS2_231"))
        val gs = new GameState()
        gs.friendlyPlayer = Player(1, hand = List(chooseOneCard, notAChooseOneCard))

        gs.isChooseOne(CardPlay(1), (chooseOneCard, NoCard())) shouldBe true
        gs.isChooseOne(CardPlay(2), (notAChooseOneCard, NoCard())) shouldBe false
      }

      "Always return false is the chooseOne Legendary card is on the board" in {
        val legendaryCardID = "OG_044"
        val chooseOneLegendary = Card("Fandral Staghelm", 11, Constants.INT_UNINIT, 1, 1, legendaryCardID, cardInfo = database.cardIDMap(legendaryCardID))
        val gs = new GameState()
        gs.friendlyPlayer = Player(1, hand = List(chooseOneCard), board = List(chooseOneLegendary))

        gs.isChooseOne(CardPlay(1), (chooseOneCard, NoCard())) shouldBe false
      }

      "Be able to return true when the chooseOne Legendary card is in the hand, not on the board" in {
        val legendaryCardID = "OG_044"
        val chooseOneLegendary = Card("Fandral Staghelm", 11, Constants.INT_UNINIT, 1, 1, legendaryCardID, cardInfo = database.cardIDMap(legendaryCardID))
        val gs = new GameState()
        gs.friendlyPlayer = Player(1, hand = List(chooseOneCard, chooseOneLegendary))


        gs.isChooseOne(CardPlay(1), (chooseOneCard, NoCard())) shouldBe true
      }
    }



    "Reset the state when gameOver is called" in {
      val gs = new GameState
      gs.friendlyPlayer = Player(1, hand = List(Constants.emptyCard), board = List(Constants.emptyCard))
      gs.enemyPlayer = Player(2, hand = List(Constants.emptyCard), board = List(Constants.emptyCard))


      gs.gameOver()


      gs.friendlyPlayer shouldBe Player()
      gs.enemyPlayer shouldBe Player()
    }


  }
}