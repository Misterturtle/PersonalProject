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
    val gs = defaultGameState
    val newFriendlyHand = defaultGameState.friendlyPlayer.hand ::: List(new Card("Friendly Card 7", 7, 7, tph.Constants.INT_UNINIT, 1, Constants.STRING_UNINIT))
    CardDrawn("Friendly Card 7", 7, Constants.STRING_UNINIT, 7, 1).updateGameState(gs)
    val testFriendlyHand = gs.friendlyPlayer.hand
    newFriendlyHand shouldEqual testFriendlyHand
  }

  "HSAction CardDeath" should "ExecuteAction" in {
    val gs = defaultGameState
    CardDeath("Friendly Card 4", 4, 1).updateGameState(gs)
    CardDeath("Friendly Minion 2", 12, 1).updateGameState(gs)
    CardDeath("Enemy Card 4", 24, 2).updateGameState(gs)
    CardDeath("Enemy Minion 2", 32, 2).updateGameState(gs)


    val expectedFriendlyHand = List(
      Constants.TestConstants.createFriendlyHandCard(1),
      Constants.TestConstants.createFriendlyHandCard(2),
      Constants.TestConstants.createFriendlyHandCard(3),
      Card("Friendly Card 5", 5, 4, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT),
      Card("Friendly Card 6", 6, 5, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT))

    val expectedFriendlyBoard = List(
      Constants.TestConstants.createFriendlyBoardCard(1),
      Card("Friendly Minion 3", 13, Constants.INT_UNINIT, 2, 1, Constants.STRING_UNINIT),
      Card("Friendly Minion 4", 14, Constants.INT_UNINIT, 3, 1, Constants.STRING_UNINIT))

    val expectedEnemyHand =
      List(
        Constants.TestConstants.createEnemyHandCard(1),
        Constants.TestConstants.createEnemyHandCard(2),
        Constants.TestConstants.createEnemyHandCard(3),
        Card("Enemy Card 5", 25, 4, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT),
        Card("Enemy Card 6", 26, 5, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT))

    val expectedEnemyBoard =
      List(
        Constants.TestConstants.createEnemyBoardCard(1),
        Card("Enemy Minion 3", 33, Constants.INT_UNINIT, 2, 2, Constants.STRING_UNINIT),
        Card("Enemy Minion 4", 34, Constants.INT_UNINIT, 3, 2, Constants.STRING_UNINIT))


    val actualFriendlyHand = gs.friendlyPlayer.hand
    val actualFriendlyBoard = gs.friendlyPlayer.board
    val actualEnemyHand = gs.enemyPlayer.hand
    val actualEnemyBoard = gs.enemyPlayer.board

    actualFriendlyHand shouldEqual expectedFriendlyHand
    actualFriendlyBoard shouldEqual expectedFriendlyBoard
    actualEnemyHand shouldEqual expectedEnemyHand
    actualEnemyBoard shouldEqual expectedEnemyBoard
  }


  "HSAction FriendlyMinionControlled" should "ExecuteAction" in {

    val gs = defaultGameState
    FriendlyMinionControlled("Friendly Minion 2", 12, 2).updateGameState(gs)
    

    val actualFriendlyBoard = gs.friendlyPlayer.board
    val expectedFriendlyBoard = List(
      new Card("Friendly Minion 1", 11, Constants.INT_UNINIT, 1, 1, Constants.STRING_UNINIT),
      new Card("Friendly Minion 3", 13, Constants.INT_UNINIT, 2, 1, Constants.STRING_UNINIT),
      new Card("Friendly Minion 4", 14, Constants.INT_UNINIT, 3, 1, Constants.STRING_UNINIT))


    val actualEnemyBoard = gs.enemyPlayer.board
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

    val gs = defaultGameState
    CardDrawn(Constants.STRING_UNINIT,27, Constants.STRING_UNINIT, 2, 7).updateGameState(gs)

    val actualEnemyHand = gs.enemyPlayer.hand
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

    val gs = defaultGameState
    EnemyMinionControlled("Enemy Minion 2", 32, 2).updateGameState(gs)

    val actualFriendlyBoard = gs.friendlyPlayer.board
    val expectedFriendlyBoard = List(
      new Card("Friendly Minion 1", 11, Constants.INT_UNINIT, 1, 1, Constants.STRING_UNINIT),
      new Card("Friendly Minion 2", 12, Constants.INT_UNINIT, 2, 1, Constants.STRING_UNINIT),
      new Card("Friendly Minion 3", 13, Constants.INT_UNINIT, 3, 1, Constants.STRING_UNINIT),
      new Card("Friendly Minion 4", 14, Constants.INT_UNINIT, 4, 1, Constants.STRING_UNINIT),
      new Card("Enemy Minion 2", 32, Constants.INT_UNINIT, 5, 1, Constants.STRING_UNINIT))


    val actualEnemyBoard = gs.enemyPlayer.board
    val expectedEnemyBoard = List(
      new Card("Enemy Minion 1", 31, Constants.INT_UNINIT, 1, 2, Constants.STRING_UNINIT),
      new Card("Enemy Minion 3", 33, Constants.INT_UNINIT, 2, 2, Constants.STRING_UNINIT),
      new Card("Enemy Minion 4", 34, Constants.INT_UNINIT, 3, 2, Constants.STRING_UNINIT))


    actualFriendlyBoard shouldEqual expectedFriendlyBoard
    actualEnemyBoard shouldEqual expectedEnemyBoard
  }





  "HSAction WeaponPlayed" should "ExecuteAction" in{

    val gs = defaultGameState
    WeaponChange(3, 1, true).updateGameState(gs)
    WeaponChange(23, 2, false).updateGameState(gs)

    val actualFriendly = gs.friendlyPlayer

    val actualEnemy = gs.enemyPlayer

    actualFriendly.isWeaponEquipped shouldEqual true
    actualEnemy.isWeaponEquipped shouldEqual false
  }


  "HSAction ChangeFaceAttackValue" should "ExecuteAction" in{
    val gs = new GameState()
    val friendlyHero = Card("FriendlyHero", 100, Constants.INT_UNINIT, 0, 1, "Some")
    val enemyHero = Card("EnemyHero", 200, Constants.INT_UNINIT, 0, 2, "Some")
    gs.friendlyPlayer = gs.friendlyPlayer.copy(hero = Some(friendlyHero))
    gs.enemyPlayer = gs.enemyPlayer.copy(hero = Some(enemyHero))
    gs.friendlyPlayer = gs.friendlyPlayer.copy(playerNumber = 1)
    gs.enemyPlayer = gs.enemyPlayer.copy(playerNumber = 2)
    ChangeAttackValue(1, 5, 100, 0).updateGameState(gs)
    ChangeAttackValue(2, 6, 200, 0).updateGameState(gs)

    val actualFriendlyFaceValue = gs.friendlyPlayer.hero.get.attack
    val expectedFriendlyFaceValue = Some(5)

    val actualyEnemyFaceValue = gs.enemyPlayer.hero.get.attack
    val expectedEnemyFaceValue = Some(6)

    actualFriendlyFaceValue shouldEqual expectedFriendlyFaceValue
    actualyEnemyFaceValue shouldEqual expectedEnemyFaceValue
  }


  "HSAction SecretPlayed" should "ExecuteAction" in {
    val gs = defaultGameState
    SecretPlayed(5, 1).updateGameState(gs)
    SecretPlayed(25, 2).updateGameState(gs)

    val actualFriendlyHand = gs.friendlyPlayer.hand
    val expectedFriendlyHand = List(
      new Card("Friendly Card 1", 1, 1, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT),
      new Card("Friendly Card 2", 2, 2, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT),
      new Card("Friendly Card 3", 3, 3, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT),
      new Card("Friendly Card 4", 4, 4, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT),
      new Card("Friendly Card 6", 6, 5, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT))

    val actualEnemyHand = gs.enemyPlayer.hand
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
      val gs = defaultGameState
      CardPlayed("Enemy Card 1", 21,5,Constants.STRING_UNINIT, 2).updateGameState(gs)
      CardPlayed("Friendly Card 1", 1,  5,Constants.STRING_UNINIT, 1).updateGameState(gs)

      val actualFriendlyHand = gs.friendlyPlayer.hand
      val expectedFriendlyHand = List(
        new Card("Friendly Card 2", 2, 1, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT),
        new Card("Friendly Card 3", 3, 2, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT),
        new Card("Friendly Card 4", 4, 3, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT),
        new Card("Friendly Card 5", 5, 4, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT),
        new Card("Friendly Card 6", 6, 5, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT))

      val actualFriendlyBoard = gs.friendlyPlayer.board
      val expectedFriendlyBoard = List(
        new Card("Friendly Minion 1", 11, Constants.INT_UNINIT, 1, 1, Constants.STRING_UNINIT),
        new Card("Friendly Minion 2", 12, Constants.INT_UNINIT, 2, 1, Constants.STRING_UNINIT),
        new Card("Friendly Minion 3", 13, Constants.INT_UNINIT, 3, 1, Constants.STRING_UNINIT),
        new Card("Friendly Minion 4", 14, Constants.INT_UNINIT, 4, 1, Constants.STRING_UNINIT),
        new Card("Friendly Card 1", 1, Constants.INT_UNINIT, 5, 1, Constants.STRING_UNINIT))

      val actualEnemyHand = gs.enemyPlayer.hand
      val expectedEnemyHand = List(
        new Card("Enemy Card 2", 22, 1, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT),
        new Card("Enemy Card 3", 23, 2, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT),
        new Card("Enemy Card 4", 24, 3, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT),
        new Card("Enemy Card 5", 25, 4, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT),
        new Card("Enemy Card 6", 26, 5, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT))

      val actualEnemyBoard = gs.enemyPlayer.board
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
    val gs = defaultGameState
    MinionSummoned("Summoned Friendly Minion", 15, 5,Constants.STRING_UNINIT, 1).updateGameState(gs)
    MinionSummoned("Summoned Enemy Minion", 35, 5,Constants.STRING_UNINIT, 2).updateGameState(gs)

    val actualFriendlyBoard = gs.friendlyPlayer.board
    val expectedFriendlyBoard = List(
      new Card("Friendly Minion 1", 11, Constants.INT_UNINIT, 1, 1, Constants.STRING_UNINIT),
      new Card("Friendly Minion 2", 12, Constants.INT_UNINIT, 2, 1, Constants.STRING_UNINIT),
      new Card("Friendly Minion 3", 13, Constants.INT_UNINIT, 3, 1, Constants.STRING_UNINIT),
      new Card("Friendly Minion 4", 14, Constants.INT_UNINIT, 4, 1, Constants.STRING_UNINIT),
      new Card("Summoned Friendly Minion", 15, Constants.INT_UNINIT, 5, 1, Constants.STRING_UNINIT))

    val actualEnemyBoard = gs.enemyPlayer.board
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
    val gs = defaultGameState
    Transform("Transformed Friendly Card",3, 3, Constants.STRING_UNINIT, 100).updateGameState(gs)
    Transform("Transformed Friendly Minion",12, 2, Constants.STRING_UNINIT, 101).updateGameState(gs)
    Transform("Transformed Enemy Card",21, 1, Constants.STRING_UNINIT, 102).updateGameState(gs)
    Transform("Transformed Enemy Minion",33, 3, Constants.STRING_UNINIT, 103).updateGameState(gs)

    val actualFriendlyHand = gs.friendlyPlayer.hand
    val expectedFriendHand = List(
      new Card("Friendly Card 1", 1, 1, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT),
      new Card("Friendly Card 2", 2, 2, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT),
      new Card("Friendly Card 4", 4, 4, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT),
      new Card("Friendly Card 5", 5, 5, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT),
      new Card("Friendly Card 6", 6, 6, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT),
      new Card("Transformed Friendly Card", 100, 3, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT))

    val actualFriendlyBoard = gs.friendlyPlayer.board
    val expectedFriendlyBoard = List(
      new Card("Friendly Minion 1", 11, Constants.INT_UNINIT, 1, 1, Constants.STRING_UNINIT),
      new Card("Friendly Minion 3", 13, Constants.INT_UNINIT, 3, 1, Constants.STRING_UNINIT),
      new Card("Friendly Minion 4", 14, Constants.INT_UNINIT, 4, 1, Constants.STRING_UNINIT),
      new Card("Transformed Friendly Minion", 101, Constants.INT_UNINIT, 2, 1, Constants.STRING_UNINIT))

    val actualEnemyHand = gs.enemyPlayer.hand
    val expectedEnemyHand = List(
      new Card("Enemy Card 2", 22, 2, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT),
      new Card("Enemy Card 3", 23, 3, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT),
      new Card("Enemy Card 4", 24, 4, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT),
      new Card("Enemy Card 5", 25, 5, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT),
      new Card("Enemy Card 6", 26, 6, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT),
      new Card("Transformed Enemy Card", 102, 1, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT))

    val actualEnemyBoard = gs.enemyPlayer.board
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
    val hsAction = new DefinePlayers(Constants.INT_UNINIT, "some", 2)
    val actualGameState = new GameState
    hsAction.updateGameState(actualGameState)
    val expectedGameState = new GameState()
    expectedGameState.setPlayerNumbers(2)
    expectedGameState.friendlyPlayer = expectedGameState.friendlyPlayer.copy(hero = Some(Card("Friendly Hero", 500, 500, 0, 2, "some")))

    actualGameState.friendlyPlayer shouldBe expectedGameState.friendlyPlayer
    actualGameState.enemyPlayer shouldBe expectedGameState.enemyPlayer
  }

  "Problem situation with MinionDamaged after Weapon equipped" should "work" in{
    val gs = new GameState()
    val friendlyHero = Card("Alleria Windrunner", 100, Constants.INT_UNINIT,0,1,"HERO_05a")

    gs.friendlyPlayer = gs.friendlyPlayer.copy(playerNumber = 1, hero = Some(friendlyHero))

    WeaponChange(100,1,true).updateGameState(gs)
    ChangeAttackValue(1,4,100,0).updateGameState(gs)
    MinionDamaged(100,1,2).updateGameState(gs)

    gs.friendlyPlayer.hero.get shouldBe Card("Alleria Windrunner", 100, Constants.INT_UNINIT,0,1,"HERO_05a", attack = Some(4), isDamaged = true)
    gs.friendlyPlayer.isWeaponEquipped shouldBe true




  }


}
