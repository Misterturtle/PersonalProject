package LogReaderTests

import FileReaders.HSAction
import FileReaders.HSAction._
import org.scalatest.{FlatSpec, Matchers}
import tph._


/**
  * Created by Harambe on 2/20/2017.
  */

class HSActionTests extends FlatSpec with Matchers {


  import Constants.TestConstants._


  "HSAction FriendlyCardDrawn" should "ExecuteAction" in {


    val friendlyHSAction = new CardDrawn("Friendly Card 7", 7, Constants.STRING_UNINIT, 7, 1)

    val newFriendlyHand = defaultGameState.friendlyPlayer.hand ::: List(new Card("Friendly Card 7", 7, 7, tph.Constants.INT_UNINIT, 1, Constants.STRING_UNINIT))

    val testFriendlyHand = friendlyHSAction.ExecuteAction(defaultGameState).friendlyPlayer.hand

    newFriendlyHand shouldEqual testFriendlyHand
  }

  "HSAction CardDeath" should "ExecuteAction" in {
    val friendlyHandCardDeath = new CardDeath("Friendly Card 4", 4, 1)
    val friendlyBoardCardDeath = new CardDeath("Friendly Minion 2", 12, 1)
    val enemyHandCardDeath = new CardDeath("Enemy Card 4", 24, 2)
    val enemyBoardCardDeath = new CardDeath("Enemy Minion 2", 32, 2)


    val expectedFriendlyHand = List(
      Constants.TestConstants.createFriendlyHandCard(1),
      Constants.TestConstants.createFriendlyHandCard(2),
      Constants.TestConstants.createFriendlyHandCard(3),
      new Card("Friendly Card 5", 5, 4, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT),
      new Card("Friendly Card 6", 6, 5, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT))

    val expectedFriendlyBoard = List(
      Constants.TestConstants.createFriendlyBoardCard(1),
      new Card("Friendly Minion 3", 13, Constants.INT_UNINIT, 2, 1, Constants.STRING_UNINIT),
      new Card("Friendly Minion 4", 14, Constants.INT_UNINIT, 3, 1, Constants.STRING_UNINIT))

    val expectedEnemyHand =
      List(
        Constants.TestConstants.createEnemyHandCard(1),
        Constants.TestConstants.createEnemyHandCard(2),
        Constants.TestConstants.createEnemyHandCard(3),
        new Card("Enemy Card 5", 25, 4, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT),
        new Card("Enemy Card 6", 26, 5, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT))

    val expectedEnemyBoard =
      List(
        Constants.TestConstants.createEnemyBoardCard(1),
        new Card("Enemy Minion 3", 33, Constants.INT_UNINIT, 2, 2, Constants.STRING_UNINIT),
        new Card("Enemy Minion 4", 34, Constants.INT_UNINIT, 3, 2, Constants.STRING_UNINIT))


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

    val hsAction = new FriendlyMinionControlled("Friendly Minion 2", 12, 2)
    

    val actualFriendlyBoard = hsAction.ExecuteAction(defaultGameState).friendlyPlayer.board
    val expectedFriendlyBoard = List(
      new Card("Friendly Minion 1", 11, Constants.INT_UNINIT, 1, 1, Constants.STRING_UNINIT),
      new Card("Friendly Minion 3", 13, Constants.INT_UNINIT, 2, 1, Constants.STRING_UNINIT),
      new Card("Friendly Minion 4", 14, Constants.INT_UNINIT, 3, 1, Constants.STRING_UNINIT))


    val actualEnemyBoard = hsAction.ExecuteAction(defaultGameState).enemyPlayer.board
    val expectedEnemyBoard = List(
      new Card("Enemy Minion 1", 31, Constants.INT_UNINIT, 1, 2, Constants.STRING_UNINIT),
      new Card("Enemy Minion 2", 32, Constants.INT_UNINIT, 2, 2, Constants.STRING_UNINIT),
      new Card("Enemy Minion 3", 33, Constants.INT_UNINIT, 3, 2, Constants.STRING_UNINIT),
      new Card("Enemy Minion 4", 34, Constants.INT_UNINIT, 4, 2, Constants.STRING_UNINIT),
      new Card("Friendly Minion 2", 12, Constants.INT_UNINIT, 5, 2, Constants.STRING_UNINIT))


    actualFriendlyBoard shouldEqual expectedFriendlyBoard
    actualEnemyBoard shouldEqual expectedEnemyBoard
  }



  "HSAction EnemyCardDrawn" should "ExectueAction" in{

    val hsAction = new CardDrawn(Constants.STRING_UNINIT,27, Constants.STRING_UNINIT, 2, 7)

    val actualEnemyHand = hsAction.ExecuteAction(defaultGameState).enemyPlayer.hand
    val expectedEnemyHand =  List(
      new Card("Enemy Card 1", 21, 1, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT),
      new Card("Enemy Card 2", 22, 2, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT),
      new Card("Enemy Card 3", 23, 3, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT),
      new Card("Enemy Card 4", 24, 4, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT),
      new Card("Enemy Card 5", 25, 5, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT),
      new Card("Enemy Card 6", 26, 6, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT),
      new Card(Constants.STRING_UNINIT, 27, 7, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT))

    actualEnemyHand shouldEqual expectedEnemyHand
  }

  "HSAction EnemyMinionControlled" should "ExecuteAction" in{

    val hsAction = new EnemyMinionControlled("Enemy Minion 2", 32, 2)

    val actualFriendlyBoard = hsAction.ExecuteAction(defaultGameState).friendlyPlayer.board
    val expectedFriendlyBoard = List(
      new Card("Friendly Minion 1", 11, Constants.INT_UNINIT, 1, 1, Constants.STRING_UNINIT),
      new Card("Friendly Minion 2", 12, Constants.INT_UNINIT, 2, 1, Constants.STRING_UNINIT),
      new Card("Friendly Minion 3", 13, Constants.INT_UNINIT, 3, 1, Constants.STRING_UNINIT),
      new Card("Friendly Minion 4", 14, Constants.INT_UNINIT, 4, 1, Constants.STRING_UNINIT),
      new Card("Enemy Minion 2", 32, Constants.INT_UNINIT, 5, 1, Constants.STRING_UNINIT))


    val actualEnemyBoard = hsAction.ExecuteAction(defaultGameState).enemyPlayer.board
    val expectedEnemyBoard = List(
      new Card("Enemy Minion 1", 31, Constants.INT_UNINIT, 1, 2, Constants.STRING_UNINIT),
      new Card("Enemy Minion 3", 33, Constants.INT_UNINIT, 2, 2, Constants.STRING_UNINIT),
      new Card("Enemy Minion 4", 34, Constants.INT_UNINIT, 3, 2, Constants.STRING_UNINIT))


    actualFriendlyBoard shouldEqual expectedFriendlyBoard
    actualEnemyBoard shouldEqual expectedEnemyBoard
  }





  "HSAction WeaponPlayed" should "ExecuteAction" in{

    val friendlyHSAction = new WeaponPlayed(3, 1)
    val enemyHSAction = new WeaponPlayed(23, 2)

    val actualFriendlyHand = friendlyHSAction.ExecuteAction(defaultGameState).friendlyPlayer.hand
    val expectedFriendlyHand = List(
      new Card("Friendly Card 1", 1, 1, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT),
      new Card("Friendly Card 2", 2, 2, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT),
      new Card("Friendly Card 4", 4, 3, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT),
      new Card("Friendly Card 5", 5, 4, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT),
      new Card("Friendly Card 6", 6, 5, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT))

    val actualEnemyHand = enemyHSAction.ExecuteAction(defaultGameState).enemyPlayer.hand
    val expectedEnemyHand =  List(
      new Card("Enemy Card 1", 21, 1, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT),
      new Card("Enemy Card 2", 22, 2, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT),
      new Card("Enemy Card 4", 24, 3, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT),
      new Card("Enemy Card 5", 25, 4, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT),
      new Card("Enemy Card 6", 26, 5, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT))

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
      new Card("Friendly Card 1", 1, 1, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT),
      new Card("Friendly Card 2", 2, 2, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT),
      new Card("Friendly Card 3", 3, 3, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT),
      new Card("Friendly Card 4", 4, 4, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT),
      new Card("Friendly Card 6", 6, 5, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT))

    val actualEnemyHand = enemyHSAction.ExecuteAction(defaultGameState).enemyPlayer.hand
    val expectedEnemyHand = List(
      new Card("Enemy Card 1", 21, 1, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT),
      new Card("Enemy Card 2", 22, 2, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT),
      new Card("Enemy Card 3", 23, 3, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT),
      new Card("Enemy Card 4", 24, 4, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT),
      new Card("Enemy Card 6", 26, 5, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT))

    actualFriendlyHand shouldEqual expectedFriendlyHand
    actualEnemyHand shouldEqual expectedEnemyHand
  }


    "HSAction CardPlayed" should "ExecuteAction" in{
      val friendlyHSAction = new CardPlayed("Friendly Card 1", 1,  5,Constants.STRING_UNINIT, 1)
      val enemyHSAction = new CardPlayed("Enemy Card 1", 21,5,Constants.STRING_UNINIT, 2)

      val actualFriendlyHand = friendlyHSAction.ExecuteAction(defaultGameState).friendlyPlayer.hand
      val expectedFriendlyHand = List(
        new Card("Friendly Card 2", 2, 1, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT),
        new Card("Friendly Card 3", 3, 2, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT),
        new Card("Friendly Card 4", 4, 3, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT),
        new Card("Friendly Card 5", 5, 4, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT),
        new Card("Friendly Card 6", 6, 5, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT))

      val actualFriendlyBoard = friendlyHSAction.ExecuteAction(defaultGameState).friendlyPlayer.board
      val expectedFriendlyBoard = List(
        new Card("Friendly Minion 1", 11, Constants.INT_UNINIT, 1, 1, Constants.STRING_UNINIT),
        new Card("Friendly Minion 2", 12, Constants.INT_UNINIT, 2, 1, Constants.STRING_UNINIT),
        new Card("Friendly Minion 3", 13, Constants.INT_UNINIT, 3, 1, Constants.STRING_UNINIT),
        new Card("Friendly Minion 4", 14, Constants.INT_UNINIT, 4, 1, Constants.STRING_UNINIT),
        new Card("Friendly Card 1", 1, Constants.INT_UNINIT, 5, 1, Constants.STRING_UNINIT))

      val actualEnemyHand = enemyHSAction.ExecuteAction(defaultGameState).enemyPlayer.hand
      val expectedEnemyHand = List(
        new Card("Enemy Card 2", 22, 1, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT),
        new Card("Enemy Card 3", 23, 2, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT),
        new Card("Enemy Card 4", 24, 3, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT),
        new Card("Enemy Card 5", 25, 4, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT),
        new Card("Enemy Card 6", 26, 5, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT))

      val actualEnemyBoard = enemyHSAction.ExecuteAction(defaultGameState).enemyPlayer.board
      val expectedEnemyBoard = List(
        new Card("Enemy Minion 1", 31, Constants.INT_UNINIT, 1, 2, Constants.STRING_UNINIT),
        new Card("Enemy Minion 2", 32, Constants.INT_UNINIT, 2, 2, Constants.STRING_UNINIT),
        new Card("Enemy Minion 3", 33, Constants.INT_UNINIT, 3, 2, Constants.STRING_UNINIT),
        new Card("Enemy Minion 4", 34, Constants.INT_UNINIT, 4, 2, Constants.STRING_UNINIT),
        new Card("Enemy Card 1", 21, Constants.INT_UNINIT, 5, 2, Constants.STRING_UNINIT))


      actualFriendlyHand shouldEqual expectedFriendlyHand
      actualFriendlyBoard shouldEqual expectedFriendlyBoard
      actualEnemyHand shouldEqual expectedEnemyHand
      actualEnemyBoard shouldEqual expectedEnemyBoard
    }



  "HSAction MinionSummoned" should "Execute Action" in {
    val friendlyHSAction = new MinionSummoned("Summoned Friendly Minion", 15, 5,Constants.STRING_UNINIT, 1)
    val enemyHSAction = new MinionSummoned("Summoned Enemy Minion", 35, 5,Constants.STRING_UNINIT, 2)

    val actualFriendlyBoard = friendlyHSAction.ExecuteAction(defaultGameState).friendlyPlayer.board
    val expectedFriendlyBoard = List(
      new Card("Friendly Minion 1", 11, Constants.INT_UNINIT, 1, 1, Constants.STRING_UNINIT),
      new Card("Friendly Minion 2", 12, Constants.INT_UNINIT, 2, 1, Constants.STRING_UNINIT),
      new Card("Friendly Minion 3", 13, Constants.INT_UNINIT, 3, 1, Constants.STRING_UNINIT),
      new Card("Friendly Minion 4", 14, Constants.INT_UNINIT, 4, 1, Constants.STRING_UNINIT),
      new Card("Summoned Friendly Minion", 15, Constants.INT_UNINIT, 5, 1, Constants.STRING_UNINIT))

    val actualEnemyBoard = enemyHSAction.ExecuteAction(defaultGameState).enemyPlayer.board
    val expectedEnemyBoard = List(
      new Card("Enemy Minion 1", 31, Constants.INT_UNINIT, 1, 2, Constants.STRING_UNINIT),
      new Card("Enemy Minion 2", 32, Constants.INT_UNINIT, 2, 2, Constants.STRING_UNINIT),
      new Card("Enemy Minion 3", 33, Constants.INT_UNINIT, 3, 2, Constants.STRING_UNINIT),
      new Card("Enemy Minion 4", 34, Constants.INT_UNINIT, 4, 2, Constants.STRING_UNINIT),
      new Card("Summoned Enemy Minion", 35, Constants.INT_UNINIT, 5, 2, Constants.STRING_UNINIT))

    actualFriendlyBoard shouldEqual expectedFriendlyBoard
    actualEnemyBoard shouldEqual expectedEnemyBoard
  }

  "HSAction Transform" should "ExecuteAction" in{
    val friendlyHandHSAction = new Transform("Transformed Friendly Card",3, 3, Constants.STRING_UNINIT, 100)
    val friendlyBoardHSAction = new Transform("Transformed Friendly Minion",12, 2, Constants.STRING_UNINIT, 101)
    val enemyHandHSAction = new Transform("Transformed Enemy Card",21, 1, Constants.STRING_UNINIT, 102)
    val enemyBoardHSAction = new Transform("Transformed Enemy Minion",33, 3, Constants.STRING_UNINIT, 103)

    val actualFriendlyHand = friendlyHandHSAction.ExecuteAction(defaultGameState).friendlyPlayer.hand
    val expectedFriendHand = List(
      new Card("Friendly Card 1", 1, 1, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT),
      new Card("Friendly Card 2", 2, 2, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT),
      new Card("Friendly Card 4", 4, 4, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT),
      new Card("Friendly Card 5", 5, 5, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT),
      new Card("Friendly Card 6", 6, 6, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT),
      new Card("Transformed Friendly Card", 100, 3, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT))

    val actualFriendlyBoard = friendlyBoardHSAction.ExecuteAction(defaultGameState).friendlyPlayer.board
    val expectedFriendlyBoard = List(
      new Card("Friendly Minion 1", 11, Constants.INT_UNINIT, 1, 1, Constants.STRING_UNINIT),
      new Card("Friendly Minion 3", 13, Constants.INT_UNINIT, 3, 1, Constants.STRING_UNINIT),
      new Card("Friendly Minion 4", 14, Constants.INT_UNINIT, 4, 1, Constants.STRING_UNINIT),
      new Card("Transformed Friendly Minion", 101, Constants.INT_UNINIT, 2, 1, Constants.STRING_UNINIT))

    val actualEnemyHand = enemyHandHSAction.ExecuteAction(defaultGameState).enemyPlayer.hand
    val expectedEnemyHand = List(
      new Card("Enemy Card 2", 22, 2, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT),
      new Card("Enemy Card 3", 23, 3, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT),
      new Card("Enemy Card 4", 24, 4, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT),
      new Card("Enemy Card 5", 25, 5, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT),
      new Card("Enemy Card 6", 26, 6, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT),
      new Card("Transformed Enemy Card", 102, 1, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT))

    val actualEnemyBoard = enemyBoardHSAction.ExecuteAction(defaultGameState).enemyPlayer.board
    val expectedEnemyBoard = List(
      new Card("Enemy Minion 1", 31, Constants.INT_UNINIT, 1, 2, Constants.STRING_UNINIT),
      new Card("Enemy Minion 2", 32, Constants.INT_UNINIT, 2, 2, Constants.STRING_UNINIT),
      new Card("Enemy Minion 4", 34, Constants.INT_UNINIT, 4, 2, Constants.STRING_UNINIT),
      new Card("Transformed Enemy Minion", 103, Constants.INT_UNINIT, 3, 2, Constants.STRING_UNINIT))


    actualFriendlyHand shouldEqual expectedFriendHand
    actualFriendlyBoard shouldEqual expectedFriendlyBoard
    actualEnemyHand shouldEqual expectedEnemyHand
    actualEnemyBoard shouldEqual expectedEnemyBoard
  }


  "HSAction Define Players" should "Execute Action" in{
    val hsAction = new DefinePlayers(2)
    val actualGameState = new GameState
    hsAction.ExecuteAction(actualGameState)
    val expectedGameState = new GameState()
    expectedGameState.setPlayerNumbers(2)

    actualGameState.friendlyPlayer shouldBe expectedGameState.friendlyPlayer
    actualGameState.enemyPlayer shouldBe expectedGameState.enemyPlayer
  }






}
