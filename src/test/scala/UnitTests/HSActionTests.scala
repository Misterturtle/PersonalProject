package UnitTests

import jdk.nashorn.internal.ir.annotations.Ignore
import org.scalatest.{Matchers, FlatSpec}
import tph.HSAction.KnownCardDrawn
import tph._


/**
  * Created by Harambe on 2/20/2017.
  */

@Ignore
class HSActionTests extends FlatSpec with Matchers {

  "HSAction KnownCardDrawn" should "ExecuteAction" in {
    val friendlyHSAction = new KnownCardDrawn("Tester",10, 5, 1)
    val enemyHSAction = new KnownCardDrawn("EnemyTester", 20, 3, 2)

    val gameState = new GameState(new Player(1, List[HSCard](), List[HSCard]()), new Player(2, List[HSCard](), List[HSCard]()))

    val newFriendlyHand = List(new Card("Tester", 10, 5, tph.Constants.INT_UNINIT, 1))
    val newEnemyHand = List(new Card("EnemyTester", 20, 3, tph.Constants.INT_UNINIT, 2))
    val newGameState = new GameState(new Player(2, newFriendlyHand, List[HSCard]()), new Player(2, newEnemyHand, List[HSCard]()))

    val testValue = enemyHSAction.ExecuteAction(friendlyHSAction.ExecuteAction(gameState))

    testValue shouldEqual newGameState

  }





}
