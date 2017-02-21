package UnitTests

import jdk.nashorn.internal.ir.annotations.Ignore
import org.scalatest.{Matchers, FlatSpec}
import tph.HSAction.{KnownCardDrawn}
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




  /*

  GameState(Player(1,
  List(Card(Friendly Minion 6,6,6,-5,1)),List(Card(Friendly Board 1,11,-5,1,1), Card(Friendly Board 2,12,-5,2,1), Card(Friendly Board 3,13,-5,3,1), Card(Friendly Board 4,14,-5,4,1))),Player(2,List(Card(Enemy Tester,20,3,-5,2)),List(Card(Enemy Board 1,31,-5,1,2), Card(Enemy Board 2,32,-5,2,2), Card(Enemy Board 3,33,-5,3,2), Card(Enemy Board 4,34,-5,4,2)))) did not equal GameState(Player(1,List(Card(Friendly Hand 1,1,1,-5,1), Card(Friendly Hand 2,2,2,-5,1), Card(Friendly Hand 3,3,3,-5,1), Card(Friendly Hand 4,4,4,-5,1), Card(Friendly Hand 5,5,5,-5,1), Card(Friendly Hand 6,6,6,-5,1), Card(Friendly Hand 7,7,7,-5,1)),List()),Player(2,List(Card(Enemy Hand 1,21,1,-5,2), Card(Enemy Hand 2,22,2,-5,2), Card(Enemy Hand 3,23,3,-5,2), Card(Enemy Hand 4,24,4,-5,2), Card(Enemy Hand 5,25,5,-5,2), Card(Enemy Hand 6,26,6,-5,2), Card(Enemy Hand 7,27,7,-5,2)),List()))




   */
















//  "HSAction CardDeath" should "ExecuteAction" in {
//    val friendlyHSAction = new CardDeath("Friendly Hand 2", 2, 1)
//    val enemyHSAction = new CardDeath("Enemy Hand 2", 22, 2)
//
//    val newFriendlyPlayer = new Player(1, List(
//      new Card("Friendly Hand 1", 1, 1, Constants.INT_UNINIT, 1),
//      new Card("Friendly Hand 3", 3, 2, Constants.INT_UNINIT, 1),
//      new Card("Friendly Hand 4", 4, 3, Constants.INT_UNINIT, 1),
//      new Card("Friendly Hand 5", 5, 4, Constants.INT_UNINIT, 1),
//      new Card("Friendly Hand 6", 6, 5, Constants.INT_UNINIT, 1)))
//
//    val newEnemyPlayer = new Player(2,List(
//      new Card("Enemy Board 1", 31, Constants.INT_UNINIT, 1, 2),
//      new Card("Enemy Board 3", 33, Constants.INT_UNINIT, 2, 2),
//      new Card("Enemy Board 4", 34, Constants.INT_UNINIT, 3, 2)))
//
//    val newGameState = new GameState(newFriendlyPlayer, newEnemyPlayer)
//
//    enemyHSAction.ExecuteAction(friendlyHSAction.ExecuteAction(defaultGameState)) shouldEqual newGameState
//
//  }


}
