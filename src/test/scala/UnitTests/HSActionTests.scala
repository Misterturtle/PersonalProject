package UnitTests

import jdk.internal.org.objectweb.asm.tree.MethodInsnNode
import jdk.nashorn.internal.ir.annotations.Ignore
import org.scalatest.{Matchers, FlatSpec}
import tph.HSAction._
import tph._


/**
  * Created by Harambe on 2/20/2017.
  */

class HSActionTests extends FlatSpec with Matchers {


  import Constants.TestConstants._


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


  "HSAction FriendlyMinionControlled" should "ExecuteAction" in {

    val hsAction = new FriendlyMinionControlled("Friendly Board 2", 12, 2)

    val actualFriendlyBoard = hsAction.ExecuteAction(defaultGameState).friendlyPlayer.board
    val expectedFriendlyBoard = List(
      new Card("Friendly Board 1", 11, Constants.INT_UNINIT, 1, 1),
      new Card("Friendly Board 3", 13, Constants.INT_UNINIT, 2, 1),
      new Card("Friendly Board 4", 14, Constants.INT_UNINIT, 3, 1))


    val actualEnemyBoard = hsAction.ExecuteAction(defaultGameState).enemyPlayer.board
    val expectedEnemyBoard = List(
      new Card("Enemy Board 1", 31, Constants.INT_UNINIT, 1, 2),
      new Card("Enemy Board 2", 32, Constants.INT_UNINIT, 2, 2),
      new Card("Enemy Board 3", 33, Constants.INT_UNINIT, 3, 2),
      new Card("Enemy Board 4", 34, Constants.INT_UNINIT, 4, 2),
      new Card("Friendly Board 2", 12, Constants.INT_UNINIT, 5, 2))


    actualFriendlyBoard shouldEqual expectedFriendlyBoard
    actualEnemyBoard shouldEqual expectedEnemyBoard
  }



  "HSAction EnemyCardDrawn" should "ExectueAction" in{

    val hsAction = new EnemyCardDrawn(27, 2, 7)

    val actualEnemyHand = hsAction.ExecuteAction(defaultGameState).enemyPlayer.hand
    val expectedEnemyHand =  List(
      new Card("Enemy Hand 1", 21, 1, Constants.INT_UNINIT, 2),
      new Card("Enemy Hand 2", 22, 2, Constants.INT_UNINIT, 2),
      new Card("Enemy Hand 3", 23, 3, Constants.INT_UNINIT, 2),
      new Card("Enemy Hand 4", 24, 4, Constants.INT_UNINIT, 2),
      new Card("Enemy Hand 5", 25, 5, Constants.INT_UNINIT, 2),
      new Card("Enemy Hand 6", 26, 6, Constants.INT_UNINIT, 2),
      new Card(Constants.STRING_UNINIT, 27, 7, Constants.INT_UNINIT, 2))

    actualEnemyHand shouldEqual expectedEnemyHand
  }

  "HSAction EnemyMinionControlled" should "ExecuteAction" in{

    val hsAction = new EnemyMinionControlled("Enemy Board 2", 32, 2)

    val actualFriendlyBoard = hsAction.ExecuteAction(defaultGameState).friendlyPlayer.board
    val expectedFriendlyBoard = List(
      new Card("Friendly Board 1", 11, Constants.INT_UNINIT, 1, 1),
      new Card("Friendly Board 2", 12, Constants.INT_UNINIT, 2, 1),
      new Card("Friendly Board 3", 13, Constants.INT_UNINIT, 3, 1),
      new Card("Friendly Board 4", 14, Constants.INT_UNINIT, 4, 1),
      new Card("Enemy Board 2", 32, Constants.INT_UNINIT, 5, 1))


    val actualEnemyBoard = hsAction.ExecuteAction(defaultGameState).enemyPlayer.board
    val expectedEnemyBoard = List(
      new Card("Enemy Board 1", 31, Constants.INT_UNINIT, 1, 2),
      new Card("Enemy Board 3", 33, Constants.INT_UNINIT, 2, 2),
      new Card("Enemy Board 4", 34, Constants.INT_UNINIT, 3, 2))


    actualFriendlyBoard shouldEqual expectedFriendlyBoard
    actualEnemyBoard shouldEqual expectedEnemyBoard
  }





  "HSAction WeaponPlayed" should "ExecuteAction" in{

    val friendlyHSAction = new WeaponPlayed(3, 1)
    val enemyHSAction = new WeaponPlayed(23, 2)

    val actualFriendlyHand = friendlyHSAction.ExecuteAction(defaultGameState).friendlyPlayer.hand
    val expectedFriendlyHand = List(
      new Card("Friendly Hand 1", 1, 1, Constants.INT_UNINIT, 1),
      new Card("Friendly Hand 2", 2, 2, Constants.INT_UNINIT, 1),
      new Card("Friendly Hand 4", 4, 3, Constants.INT_UNINIT, 1),
      new Card("Friendly Hand 5", 5, 4, Constants.INT_UNINIT, 1),
      new Card("Friendly Hand 6", 6, 5, Constants.INT_UNINIT, 1))

    val actualEnemyHand = enemyHSAction.ExecuteAction(defaultGameState).enemyPlayer.hand
    val expectedEnemyHand =  List(
      new Card("Enemy Hand 1", 21, 1, Constants.INT_UNINIT, 2),
      new Card("Enemy Hand 2", 22, 2, Constants.INT_UNINIT, 2),
      new Card("Enemy Hand 4", 24, 3, Constants.INT_UNINIT, 2),
      new Card("Enemy Hand 5", 25, 4, Constants.INT_UNINIT, 2),
      new Card("Enemy Hand 6", 26, 5, Constants.INT_UNINIT, 2))

    actualFriendlyHand shouldEqual expectedFriendlyHand
    actualEnemyHand shouldEqual expectedEnemyHand
  }


  "HSAction ChangeFaceAttackValue" should "ExecuteAction" in{
    val friendlyHSAction = new ChangeFaceAttackValue(1, 5)
    val enemyHSAction = new ChangeFaceAttackValue(2, 5)

    val actualFriendlyFaceValue = friendlyHSAction.ExecuteAction(defaultGameState).friendlyPlayer.weaponValue
    val expectedFriendlyFaceValue = 5

    val actualyEnemyFaceValue = enemyHSAction.ExecuteAction(defaultGameState).enemyPlayer.weaponValue
    val expectedEnemyFaceValue = 5

    actualFriendlyFaceValue shouldEqual expectedFriendlyFaceValue
    actualyEnemyFaceValue shouldEqual expectedEnemyFaceValue
  }


  "HSAction SecretPlayed" should "ExecuteAction" in {
    val friendlyHSAction = new SecretPlayed(5, 1)
    val enemyHSAction = new SecretPlayed(25, 2)

    val actualFriendlyHand = friendlyHSAction.ExecuteAction(defaultGameState).friendlyPlayer.hand
    val expectedFriendlyHand = List(
      new Card("Friendly Hand 1", 1, 1, Constants.INT_UNINIT, 1),
      new Card("Friendly Hand 2", 2, 2, Constants.INT_UNINIT, 1),
      new Card("Friendly Hand 3", 3, 3, Constants.INT_UNINIT, 1),
      new Card("Friendly Hand 4", 4, 4, Constants.INT_UNINIT, 1),
      new Card("Friendly Hand 6", 6, 5, Constants.INT_UNINIT, 1))

    val actualEnemyHand = enemyHSAction.ExecuteAction(defaultGameState).enemyPlayer.hand
    val expectedEnemyHand = List(
      new Card("Enemy Hand 1", 21, 1, Constants.INT_UNINIT, 2),
      new Card("Enemy Hand 2", 22, 2, Constants.INT_UNINIT, 2),
      new Card("Enemy Hand 3", 23, 3, Constants.INT_UNINIT, 2),
      new Card("Enemy Hand 4", 24, 4, Constants.INT_UNINIT, 2),
      new Card("Enemy Hand 6", 26, 5, Constants.INT_UNINIT, 2))

    actualFriendlyHand shouldEqual expectedFriendlyHand
    actualEnemyHand shouldEqual expectedEnemyHand
  }


    "HSAction CardPlayed" should "ExecuteAction" in{
      val friendlyHSAction = new CardPlayed("Friendly Hand 1", 1, 5, 1)
      val enemyHSAction = new CardPlayed("Enemy Hand 1", 21, 5, 2)

      val actualFriendlyHand = friendlyHSAction.ExecuteAction(defaultGameState).friendlyPlayer.hand
      val expectedFriendlyHand = List(
        new Card("Friendly Hand 2", 2, 1, Constants.INT_UNINIT, 1),
        new Card("Friendly Hand 3", 3, 2, Constants.INT_UNINIT, 1),
        new Card("Friendly Hand 4", 4, 3, Constants.INT_UNINIT, 1),
        new Card("Friendly Hand 5", 5, 4, Constants.INT_UNINIT, 1),
        new Card("Friendly Hand 6", 6, 5, Constants.INT_UNINIT, 1))

      val actualFriendlyBoard = friendlyHSAction.ExecuteAction(defaultGameState).friendlyPlayer.board
      val expectedFriendlyBoard = List(
        new Card("Friendly Board 1", 11, Constants.INT_UNINIT, 1, 1),
        new Card("Friendly Board 2", 12, Constants.INT_UNINIT, 2, 1),
        new Card("Friendly Board 3", 13, Constants.INT_UNINIT, 3, 1),
        new Card("Friendly Board 4", 14, Constants.INT_UNINIT, 4, 1),
        new Card("Friendly Hand 1", 1, Constants.INT_UNINIT, 5, 1))

      val actualEnemyHand = enemyHSAction.ExecuteAction(defaultGameState).enemyPlayer.hand
      val expectedEnemyHand = List(
        new Card("Enemy Hand 2", 22, 1, Constants.INT_UNINIT, 2),
        new Card("Enemy Hand 3", 23, 2, Constants.INT_UNINIT, 2),
        new Card("Enemy Hand 4", 24, 3, Constants.INT_UNINIT, 2),
        new Card("Enemy Hand 5", 25, 4, Constants.INT_UNINIT, 2),
        new Card("Enemy Hand 6", 26, 5, Constants.INT_UNINIT, 2))

      val actualEnemyBoard = enemyHSAction.ExecuteAction(defaultGameState).enemyPlayer.board
      val expectedEnemyBoard = List(
        new Card("Enemy Board 1", 31, Constants.INT_UNINIT, 1, 2),
        new Card("Enemy Board 2", 32, Constants.INT_UNINIT, 2, 2),
        new Card("Enemy Board 3", 33, Constants.INT_UNINIT, 3, 2),
        new Card("Enemy Board 4", 34, Constants.INT_UNINIT, 4, 2),
        new Card("Enemy Hand 1", 21, Constants.INT_UNINIT, 5, 2))


      actualFriendlyHand shouldEqual expectedFriendlyHand
      actualFriendlyBoard shouldEqual expectedFriendlyBoard
      actualEnemyHand shouldEqual expectedEnemyHand
      actualEnemyBoard shouldEqual expectedEnemyBoard
    }



  "HSAction MinionSummoned" should "Execute Action" in {
    val friendlyHSAction = new MinionSummoned("Summoned Friendly Minion", 15, 5, 1)
    val enemyHSAction = new MinionSummoned("Summoned Enemy Minion", 35, 5, 2)

    val actualFriendlyBoard = friendlyHSAction.ExecuteAction(defaultGameState).friendlyPlayer.board
    val expectedFriendlyBoard = List(
      new Card("Friendly Board 1", 11, Constants.INT_UNINIT, 1, 1),
      new Card("Friendly Board 2", 12, Constants.INT_UNINIT, 2, 1),
      new Card("Friendly Board 3", 13, Constants.INT_UNINIT, 3, 1),
      new Card("Friendly Board 4", 14, Constants.INT_UNINIT, 4, 1),
      new Card("Summoned Friendly Minion", 15, Constants.INT_UNINIT, 5, 1))

    val actualEnemyBoard = enemyHSAction.ExecuteAction(defaultGameState).enemyPlayer.board
    val expectedEnemyBoard = List(
      new Card("Enemy Board 1", 31, Constants.INT_UNINIT, 1, 2),
      new Card("Enemy Board 2", 32, Constants.INT_UNINIT, 2, 2),
      new Card("Enemy Board 3", 33, Constants.INT_UNINIT, 3, 2),
      new Card("Enemy Board 4", 34, Constants.INT_UNINIT, 4, 2),
      new Card("Summoned Enemy Minion", 35, Constants.INT_UNINIT, 5, 2))

    actualFriendlyBoard shouldEqual expectedFriendlyBoard
    actualEnemyBoard shouldEqual expectedEnemyBoard
  }

  "HSAction Transform" should "ExecuteAction" in{
    val friendlyHandHSAction = new Transform(3, 100)
    val friendlyBoardHSAction = new Transform(12, 101)
    val enemyHandHSAction = new Transform(21, 102)
    val enemyBoardHSAction = new Transform(33, 103)

    val actualFriendlyHand = friendlyHandHSAction.ExecuteAction(defaultGameState).friendlyPlayer.hand
    val expectedFriendHand = List(
      new Card("Friendly Hand 1", 1, 1, Constants.INT_UNINIT, 1),
      new Card("Friendly Hand 2", 2, 2, Constants.INT_UNINIT, 1),
      new Card("Friendly Hand 4", 4, 4, Constants.INT_UNINIT, 1),
      new Card("Friendly Hand 5", 5, 5, Constants.INT_UNINIT, 1),
      new Card("Friendly Hand 6", 6, 6, Constants.INT_UNINIT, 1),
      new Card("Transformed Friendly Card", 100, 3, Constants.INT_UNINIT, 1))

    val actualFriendlyBoard = friendlyBoardHSAction.ExecuteAction(defaultGameState).friendlyPlayer.board
    val expectedFriendlyBoard = List(
      new Card("Friendly Board 1", 11, Constants.INT_UNINIT, 1, 1),
      new Card("Friendly Board 3", 13, Constants.INT_UNINIT, 3, 1),
      new Card("Friendly Board 4", 14, Constants.INT_UNINIT, 4, 1),
      new Card("Transformed Friendly Minion", 101, Constants.INT_UNINIT, 2, 1))

    val actualEnemyHand = enemyHandHSAction.ExecuteAction(defaultGameState).enemyPlayer.hand
    val expectedEnemyHand = List(
      new Card("Enemy Hand 2", 22, 2, Constants.INT_UNINIT, 2),
      new Card("Enemy Hand 3", 23, 3, Constants.INT_UNINIT, 2),
      new Card("Enemy Hand 4", 24, 4, Constants.INT_UNINIT, 2),
      new Card("Enemy Hand 5", 25, 5, Constants.INT_UNINIT, 2),
      new Card("Enemy Hand 6", 26, 6, Constants.INT_UNINIT, 2),
      new Card("Transformed Enemy Card", 102, 1, Constants.INT_UNINIT, 2))

    val actualEnemyBoard = enemyBoardHSAction.ExecuteAction(defaultGameState).enemyPlayer.board
    val expectedEnemyBoard = List(
      new Card("Enemy Board 1", 31, Constants.INT_UNINIT, 1, 2),
      new Card("Enemy Board 2", 32, Constants.INT_UNINIT, 2, 2),
      new Card("Enemy Board 4", 34, Constants.INT_UNINIT, 4, 2),
      new Card("Transformed Enemy Minion", 103, Constants.INT_UNINIT, 3, 2))


    actualFriendlyHand shouldEqual expectedFriendHand
    actualFriendlyBoard shouldEqual expectedFriendlyBoard
    actualEnemyHand shouldEqual expectedEnemyHand
    actualEnemyBoard shouldEqual expectedEnemyBoard
  }


  "HSAction Define Players" should "Execute Action" in{
    val actualGameState = new GameState().SetPlayerNumbers(2)
    val expectedGameState = new GameState(new Player(2), new Player(1))
  }






}
