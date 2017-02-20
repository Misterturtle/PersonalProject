package UnitTests

import org.scalatest.{Matchers, FlatSpec}
import tph.HSAction.KnownCardDrawn
import tph._


/**
  * Created by Harambe on 2/20/2017.
  */
class HSActionTests extends FlatSpec with Matchers {

  "HSAction KnownCardDrawn" should "ExecuteAction" in {
    val friendlyHSAction = new KnownCardDrawn("Tester",10, 5, 1)
    val enemyHSAction = new KnownCardDrawn("EnemyTester", 20, 3, 2)

    val gameState = new GameState(new Player(1, List(NoCards()), List(NoCards())), new Player(2, List(NoCards()), List(NoCards())))

    val newFriendlyHand = List(new Card("Tester", 10, 5, tph.Constants.INT_UNINIT, 1))
    val newEnemyHand = List(new Card("EnemyTester", 20, 3, tph.Constants.INT_UNINIT, 2))
    val newGameState = new GameState(new Player(2, newFriendlyHand, List(NoCards())), new Player(2, newEnemyHand, List(NoCards())))

    val testValue = enemyHSAction.ExecuteAction(friendlyHSAction.ExecuteAction(gameState))

    testValue shouldEqual newGameState

  }





}
