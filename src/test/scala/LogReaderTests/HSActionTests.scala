package LogReaderTests

import FileReaders.HSAction
import FileReaders.HSAction._
import GameState.{Player, GameState}
import Logic.IRCState
import com.typesafe.config.ConfigFactory
import org.scalatest.{FlatSpec, Matchers}
import tph._


/**
  * Created by Harambe on 2/20/2017.
  */

class HSActionTests extends FlatSpec with Matchers {


  import Constants.TestConstants._
///////////////////////////////////////////Update GameState Actions/////////////////////////////////////////
///////////////////////////////////////////Friendly Actions/////////////////////////////////////////////////
  "HSAction FriendlyMinionControlled" should "Update GameState" in {

    val gs = defaultGameState
    val origMinion = createFriendlyBoardCard(2)
    val controlledMinion = createFriendlyBoardCard(2).copy(boardPosition = 5, player = 2)

    FriendlyMinionControlled(origMinion.name, origMinion.id, origMinion.boardPosition).updateGameState(gs)

    gs.friendlyPlayer.board.contains(origMinion) shouldBe false
    gs.enemyPlayer.board.contains(controlledMinion) shouldBe true
  }


  "HSAction FriendlyCardReturn" should "Update GameState" in {
    val gs = defaultGameState
    val returnedCard = createFriendlyBoardCard(2)

    FriendlyCardReturn(returnedCard.name, returnedCard.id, returnedCard.player).updateGameState(gs)

    gs.friendlyPlayer.board.contains(returnedCard) shouldBe false
  }

  "HSAction FriendlyMulliganRedraw" should "Update GameState" in {
    val gs = defaultGameState
    val discardedMulliganCard = createFriendlyHandCard(2)

    FriendlyMulliganRedraw(discardedMulliganCard.name, discardedMulliganCard.id, discardedMulliganCard.handPosition, gs.friendlyPlayer.playerNumber).updateGameState(gs)

    gs.friendlyPlayer.hand shouldBe (defaultGameState.friendlyPlayer.hand diff List(discardedMulliganCard))
  }

  //////////////////////////////////////////////////Enemy Events//////////////////////////////////////////////////////


  "HSAction EnemyCardReturn" should "Update GameState" in {
    val gs = defaultGameState
    val returnedCard = createEnemyBoardCard(2)
    val expectedHandPos = 7

    EnemyCardReturn(returnedCard.name, returnedCard.id, returnedCard.cardID, returnedCard.player).updateGameState(gs)

    gs.enemyPlayer.board.contains(returnedCard) shouldBe false
    gs.enemyPlayer.hand.last shouldBe returnedCard.copy(boardPosition = Constants.INT_UNINIT, handPosition = expectedHandPos)
  }


  "HSAction EnemyMinionControlled" should "Update GameState" in{
    val gs = defaultGameState
    val origMinion = createEnemyBoardCard(2)
    val controlledMinion = createEnemyBoardCard(2).copy(boardPosition = 5, player = 1)

    EnemyMinionControlled(origMinion.name, origMinion.id, origMinion.boardPosition).updateGameState(gs)

    gs.enemyPlayer.board.contains(origMinion) shouldBe false
    gs.friendlyPlayer.board.contains(controlledMinion) shouldBe true
  }

  "HSAction EnemyMulliganRedraw" should "Update GameState" in{
    val gs = defaultGameState
    val discardedMulliganCard = createEnemyHandCard(3)
    val replacementCard = createEnemyHandCard(7)
    gs.enemyPlayer = gs.enemyPlayer.copy(hand = gs.enemyPlayer.hand :+ replacementCard)

    EnemyMulliganRedraw(discardedMulliganCard.id, discardedMulliganCard.player).updateGameState(gs)

    gs.enemyPlayer.hand.last.id shouldBe replacementCard.id
    gs.enemyPlayer.hand.last.boardPosition shouldBe discardedMulliganCard.boardPosition
  }


  //////////////////////////////////////////////////Neutral Events//////////////////////////////////////////////////////


  "HSAction CardDrawn" should "ExectueAction" in{

    val gs = defaultGameState
    val friendlyCard = createFriendlyHandCard(7)
    val enemyCard = createEnemyHandCard(7)

    CardDrawn(friendlyCard.name, friendlyCard.id, friendlyCard.cardID, friendlyCard.handPosition, friendlyCard.player).updateGameState(gs)
    CardDrawn(enemyCard.name, enemyCard.id, enemyCard.cardID, enemyCard.handPosition, enemyCard.player).updateGameState(gs)

    gs.friendlyPlayer.hand.contains(friendlyCard) shouldBe true
    gs.enemyPlayer.hand.contains(enemyCard) shouldBe true
  }


  "HSAction CardDeath" should "Update GameState" in {
    val gs = defaultGameState
    val friendlyHandCard = createFriendlyHandCard(4)
    val friendlyBoardMinion = createEnemyBoardCard(2)
    val enemyHandCard = createEnemyHandCard(4)
    val enemyBoardMinion = createEnemyBoardCard(2)

    CardDeath(friendlyHandCard.name, friendlyHandCard.id, friendlyHandCard.player).updateGameState(gs)
    CardDeath(friendlyBoardMinion.name, friendlyBoardMinion.id, friendlyBoardMinion.player).updateGameState(gs)
    CardDeath(enemyHandCard.name, enemyHandCard.id, enemyHandCard.player).updateGameState(gs)
    CardDeath(enemyBoardMinion.name, enemyBoardMinion.id, enemyBoardMinion.player).updateGameState(gs)

    gs.friendlyPlayer.hand.contains(friendlyHandCard) shouldBe false
    gs.friendlyPlayer.board.contains(friendlyBoardMinion) shouldBe false
    gs.enemyPlayer.hand.contains(enemyHandCard) shouldBe false
    gs.enemyPlayer.board.contains(enemyBoardMinion) shouldBe false
  }


  "HSAction NewHero" should "Update GameState" in {
    val gs = new GameState()
    val validHeroCardID = "TU4a_006"
    val newFriendlyHero = Card("Friendly Hero", 100, Constants.INT_UNINIT, 0, 1, validHeroCardID, cardInfo = gs.getCardInfo(validHeroCardID))
    val newEnemyHero = Card("Enemy Hero", 200, Constants.INT_UNINIT, 0, 2, validHeroCardID, cardInfo = gs.getCardInfo(validHeroCardID))

    NewHero("Friendly Hero", newFriendlyHero.id, newFriendlyHero.cardID, newFriendlyHero.player, "FRIENDLY").updateGameState(gs)
    NewHero("Enemy Hero", newEnemyHero.id, newEnemyHero.cardID, newEnemyHero.player, "OPPOSING").updateGameState(gs)
    
    gs.friendlyPlayer.hero shouldBe Some(newFriendlyHero)
    gs.enemyPlayer.hero shouldBe Some(newEnemyHero)
    gs.friendlyPlayer.playerNumber shouldBe 1
    gs.enemyPlayer.playerNumber shouldBe 2
  }
  
  
  
  "HSAction NewHeroPower" should "Update GameState" in {
    val gs = defaultGameState
    val validHeroPowerCardID = "CS2_102_H1"
    val newFriendlyHeroPower = Card("Friendly Hero Power", 101, Constants.INT_UNINIT,Constants.INT_UNINIT, gs.friendlyPlayer.playerNumber, validHeroPowerCardID, cardInfo = gs.getCardInfo(validHeroPowerCardID))
    val newEnemyHeroPower = Card("Enemy Hero Power", 201, Constants.INT_UNINIT, Constants.INT_UNINIT, gs.enemyPlayer.playerNumber, validHeroPowerCardID, cardInfo = gs.getCardInfo(validHeroPowerCardID))

    NewHeroPower(newFriendlyHeroPower.id, newFriendlyHeroPower.cardID, newFriendlyHeroPower.player).updateGameState(gs)
    NewHeroPower(newEnemyHeroPower.id, newEnemyHeroPower.cardID, newEnemyHeroPower.player).updateGameState(gs)

    gs.friendlyPlayer.heroPower shouldBe Some(newFriendlyHeroPower)
    gs.enemyPlayer.heroPower shouldBe Some(newEnemyHeroPower)
  }
  
  
  "HSAction ReplaceHero" should "Update GameState" in {
    val gs = defaultGameState
    val validHeroCardID = "TU4a_006"
    val newFriendlyHero = Card("Friendly Hero", 100, Constants.INT_UNINIT, 0, gs.friendlyPlayer.playerNumber, validHeroCardID, cardInfo = gs.getCardInfo(validHeroCardID))
    val newEnemyHero = Card("Enemy Hero", 200, Constants.INT_UNINIT, 0, gs.enemyPlayer.playerNumber, validHeroCardID, cardInfo = gs.getCardInfo(validHeroCardID))
    val oldHeroID = Constants.INT_UNINIT
    
    ReplaceHero(newFriendlyHero.id, newFriendlyHero.cardID, newFriendlyHero.player, oldHeroID).updateGameState(gs)
    ReplaceHero(newEnemyHero.id, newEnemyHero.cardID, newEnemyHero.player, oldHeroID).updateGameState(gs)
    
    gs.friendlyPlayer.hero shouldBe Some(newFriendlyHero)
    gs.enemyPlayer.hero shouldBe Some(newEnemyHero)
  }
  
  
  
  
  "HSAction DeckToBoard" should "Update GameState" in {
    val gs = defaultGameState
    val newFriendlyMinion = createFriendlyBoardCard(gs.friendlyPlayer.board.size+1)
    val newEnemyMinion = createEnemyBoardCard(gs.enemyPlayer.board.size+1)
    
    DeckToBoard(newFriendlyMinion.name, newFriendlyMinion.id, newFriendlyMinion.cardID, newFriendlyMinion.player).updateGameState(gs)
    DeckToBoard(newEnemyMinion.name, newEnemyMinion.id, newEnemyMinion.cardID, newEnemyMinion.player).updateGameState(gs)
    
    gs.friendlyPlayer.board.last shouldBe newFriendlyMinion
    gs.enemyPlayer.board.last shouldBe newEnemyMinion
  }



  "HSAction WeaponChanged" should "Update GameState" in{
    val gs = defaultGameState

    WeaponChange(3, 1, true).updateGameState(gs)
    WeaponChange(23, 2, true).updateGameState(gs)

    gs.friendlyPlayer.isWeaponEquipped shouldBe true
    gs.enemyPlayer.isWeaponEquipped shouldBe true
  }
  
  
  "HSAction ComboActive" should "Update GameState" in {
    val gs = defaultGameState
    
    ComboActive(gs.accountName, true).updateGameState(gs)
    ComboActive("SomeOtherAccount", true).updateGameState(gs)
    
    gs.friendlyPlayer.isComboActive shouldBe true
    gs.enemyPlayer.isComboActive shouldBe true
  }


  "HSAction ChangeFaceAttackValue" should "Update GameState" in{
    val gs = defaultGameState
    val friendlyHero = Card("FriendlyHero", 100, Constants.INT_UNINIT, 0, gs.friendlyPlayer.playerNumber, "Some")
    val enemyHero = Card("EnemyHero", 200, Constants.INT_UNINIT, 0, gs.enemyPlayer.playerNumber, "Some")
    gs.friendlyPlayer = gs.friendlyPlayer.copy(hero = Some(friendlyHero))
    gs.enemyPlayer = gs.enemyPlayer.copy(hero = Some(enemyHero))
    val friendlyAttValue = 5
    val enemyAttValue = 6

    ChangeAttackValue(gs.friendlyPlayer.playerNumber, friendlyAttValue, friendlyHero.id, friendlyHero.boardPosition).updateGameState(gs)
    ChangeAttackValue(gs.enemyPlayer.playerNumber, enemyAttValue, enemyHero.id, enemyHero.boardPosition).updateGameState(gs)

    gs.friendlyPlayer.hero.get.attack shouldBe Some(5)
    gs.enemyPlayer.hero.get.attack shouldBe Some(6)
  }


  "HSAction CardPlayed" should "Update GameState" in{
    val gs = defaultGameState
    val friendlyCard = createFriendlyHandCard(1)
    val enemyCard = createEnemyHandCard(1)
    val validCardID = "UNG_208"
    val playedFriendlyCard = friendlyCard.copy(handPosition = Constants.INT_UNINIT, boardPosition = gs.friendlyPlayer.board.size +1)
    val playedEnemyCard = enemyCard.copy(name = "Played Minion", handPosition = Constants.INT_UNINIT, boardPosition = gs.enemyPlayer.board.size +1, cardID = validCardID, cardInfo = gs.getCardInfo(validCardID))
    
    CardPlayed(friendlyCard.name, friendlyCard.id, gs.friendlyPlayer.board.size +1, friendlyCard.cardID, friendlyCard.player).updateGameState(gs)
    CardPlayed("Played Minion", enemyCard.id, gs.enemyPlayer.board.size +1, validCardID, enemyCard.player).updateGameState(gs)

    gs.friendlyPlayer.hand.contains(friendlyCard) shouldBe false
    gs.enemyPlayer.hand.contains(enemyCard) shouldBe false
    gs.friendlyPlayer.board.contains(playedFriendlyCard) shouldBe true
    gs.enemyPlayer.board.contains(playedEnemyCard) shouldBe true
  }


  "HSAction CardPlayed Elemental" should "Update GameState" in{
    val gs = new GameState()
    val elementalCardID = "UNG_208"
    val friendlyCard = createFriendlyHandCard(1).copy(cardID = elementalCardID, cardInfo = gs.getCardInfo(elementalCardID))
    val enemyCard = createEnemyHandCard(1).copy(cardID = elementalCardID, cardInfo = gs.getCardInfo(elementalCardID))
    gs.friendlyPlayer = Player(1, List(friendlyCard))
    gs.enemyPlayer = Player(2, List(enemyCard))

    CardPlayed(friendlyCard.name, friendlyCard.id, gs.friendlyPlayer.board.size +1, friendlyCard.cardID, friendlyCard.player).updateGameState(gs)
    CardPlayed(enemyCard.name, enemyCard.id, gs.enemyPlayer.board.size +1, enemyCard.cardID, enemyCard.player).updateGameState(gs)

    gs.friendlyPlayer.elementalPlayedThisTurn shouldBe true
    gs.enemyPlayer.elementalPlayedThisTurn shouldBe true
  }


  "HSAction SecretPlayed" should "Update GameState" in {
    val gs = defaultGameState
    val friendlySecret = createFriendlyHandCard(5)
    val enemySecret = createEnemyHandCard(5)
    val enemySecret2 = createEnemyHandCard(6)

    SecretPlayed(friendlySecret.id, friendlySecret.player).updateGameState(gs)
    SecretPlayed(enemySecret.id, enemySecret.player).updateGameState(gs)
    SecretPlayed(enemySecret2.id, enemySecret2.player).updateGameState(gs)

    gs.friendlyPlayer.hand.contains(friendlySecret) shouldBe false
    gs.enemyPlayer.hand.contains(enemySecret) shouldBe false
    gs.enemyPlayer.hand.contains(enemySecret2) shouldBe false
    gs.friendlyPlayer.secretsInPlay shouldBe 1
    gs.enemyPlayer.secretsInPlay shouldBe 2
  }


  "HSAction SecretDestroyed" should "Update GameState" in {
    val gs = defaultGameState
    gs.friendlyPlayer = gs.friendlyPlayer.copy(secretsInPlay = 2)
    gs.enemyPlayer = gs.enemyPlayer.copy(secretsInPlay = 3)

    SecretDestroyed(1).updateGameState(gs)
    SecretDestroyed(2).updateGameState(gs)

    gs.friendlyPlayer.secretsInPlay shouldBe 1
    gs.enemyPlayer.secretsInPlay shouldBe 2
  }


  "HSAction MinionSummoned" should "Update GameState" in {
    val gs = defaultGameState
    val friendlyMinion = createFriendlyBoardCard(5)
    val enemyMinion = createEnemyBoardCard(5)
    
    MinionSummoned(friendlyMinion.name, friendlyMinion.id, friendlyMinion.boardPosition, friendlyMinion.cardID, friendlyMinion.player).updateGameState(gs)
    MinionSummoned(enemyMinion.name, enemyMinion.id, enemyMinion.boardPosition, enemyMinion.cardID, enemyMinion.player).updateGameState(gs)

    gs.friendlyPlayer.board.contains(friendlyMinion) shouldBe true
    gs.enemyPlayer.board.contains(enemyMinion) shouldBe true
  }

  "HSAction Transform" should "Update GameState" in{
    val gs = defaultGameState
    val newFriendlyHandCard = createFriendlyHandCard(7).copy(name = "Transformed Card", handPosition = 2)
    val newFriendlyBoardMinion = createFriendlyBoardCard(5).copy(name = "Transformed Minion", boardPosition = 2)
    val newEnemyHandCard = createEnemyHandCard(7).copy(name = "Transformed Card", handPosition = 1)
    val newEnemyBoardMinion = createEnemyBoardCard(5).copy(name = "Transformed Minion", boardPosition = 3)
    val oldFriendlyHandCard = createFriendlyHandCard(2)
    val oldFriendlyBoardMinion = createFriendlyBoardCard(2)
    val oldEnemyHandCard = createEnemyHandCard(1)
    val oldEnemyBoardMinion = createEnemyBoardCard(3)
    

    Transform(newFriendlyHandCard.name, oldFriendlyHandCard.id, newFriendlyHandCard.handPosition, newFriendlyHandCard.cardID,newFriendlyHandCard.id).updateGameState(gs)
    Transform(newFriendlyBoardMinion.name, oldFriendlyBoardMinion.id, newFriendlyBoardMinion.boardPosition, newFriendlyBoardMinion.cardID,newFriendlyBoardMinion.id).updateGameState(gs)
    Transform(newEnemyHandCard.name, oldEnemyHandCard.id, newEnemyHandCard.handPosition, newEnemyHandCard.cardID,newEnemyHandCard.id).updateGameState(gs)
    Transform(newEnemyBoardMinion.name, oldEnemyBoardMinion.id, newEnemyBoardMinion.boardPosition, newEnemyBoardMinion.cardID,newEnemyBoardMinion.id).updateGameState(gs)

    gs.friendlyPlayer.hand.contains(oldFriendlyHandCard) shouldBe false
    gs.friendlyPlayer.hand.contains(newFriendlyHandCard) shouldBe true
    gs.friendlyPlayer.board.contains(oldFriendlyBoardMinion) shouldBe false
    gs.friendlyPlayer.board.contains(newFriendlyBoardMinion) shouldBe true
    gs.enemyPlayer.hand.contains(oldEnemyHandCard) shouldBe false
    gs.enemyPlayer.hand.contains(newEnemyHandCard) shouldBe true
    gs.enemyPlayer.board.contains(oldEnemyBoardMinion) shouldBe false
    gs.enemyPlayer.board.contains(newEnemyBoardMinion) shouldBe true
  }
  
  
  "HSAction Frozen" should "Update GameState" in {
    val gs = defaultGameState
    val friendlyMinion = createFriendlyBoardCard(2)
    val friendlyHero = createFriendlyBoardCard(0)
    val enemyMinion = createEnemyBoardCard(2)
    val enemyHero = createEnemyBoardCard(0)
    gs.friendlyPlayer = gs.friendlyPlayer.copy(hero = Some(friendlyHero))
    gs.enemyPlayer = gs.enemyPlayer.copy(hero = Some(enemyHero))
    
    Frozen(friendlyMinion.id, friendlyMinion.player, 1).updateGameState(gs)
    Frozen(friendlyHero.id, friendlyHero.player, 1).updateGameState(gs)
    Frozen(enemyMinion.id, enemyMinion.player, 1).updateGameState(gs)
    Frozen(enemyHero.id, enemyHero.player, 1).updateGameState(gs)

    gs.friendlyPlayer.board.head.isFrozen shouldBe true
    gs.friendlyPlayer.hero.get.isFrozen shouldBe true
    gs.enemyPlayer.board.head.isFrozen shouldBe true
    gs.enemyPlayer.hero.get.isFrozen shouldBe true
  }


  "HSAction MinionDamaged" should "Update GameState" in {
    val gs = defaultGameState
    val friendlyMinion = createFriendlyBoardCard(2)
    val friendlyHero = createFriendlyBoardCard(0)
    val enemyMinion = createEnemyBoardCard(2)
    val enemyHero = createEnemyBoardCard(0)
    gs.friendlyPlayer = gs.friendlyPlayer.copy(hero = Some(friendlyHero))
    gs.enemyPlayer = gs.enemyPlayer.copy(hero = Some(enemyHero))

    MinionDamaged(friendlyMinion.id, friendlyMinion.player, 1).updateGameState(gs)
    MinionDamaged(friendlyHero.id, friendlyHero.player, 1).updateGameState(gs)
    MinionDamaged(enemyMinion.id, enemyMinion.player, 1).updateGameState(gs)
    MinionDamaged(enemyHero.id, enemyHero.player, 1).updateGameState(gs)

    gs.friendlyPlayer.board.head.isDamaged shouldBe true
    gs.friendlyPlayer.hero.get.isDamaged shouldBe true
    gs.enemyPlayer.board.head.isDamaged shouldBe true
    gs.enemyPlayer.hero.get.isDamaged shouldBe true
  }

  "HSAction MinionStealthed" should "Update GameState" in {
    val gs = defaultGameState
    val friendlyMinion = createFriendlyBoardCard(2)
    val enemyMinion = createEnemyBoardCard(2)

    MinionStealthed(friendlyMinion.id, friendlyMinion.player, true).updateGameState(gs)
    MinionStealthed(enemyMinion.id, enemyMinion.player, true).updateGameState(gs)

    gs.friendlyPlayer.board.head.isStealthed shouldBe true
    gs.enemyPlayer.board.head.isStealthed shouldBe true
  }


  "HSAction TauntChange" should "Update GameState" in {
    val gs = defaultGameState
    val friendlyMinion = createFriendlyBoardCard(2)
    val enemyMinion = createEnemyBoardCard(2)

    TauntChange(friendlyMinion.id, friendlyMinion.player, true).updateGameState(gs)
    TauntChange(enemyMinion.id, enemyMinion.player, true).updateGameState(gs)

    gs.friendlyPlayer.board.head.isTaunt shouldBe Some(true)
    gs.enemyPlayer.board.head.isTaunt shouldBe Some(true)
  }


  "HSAction DeathrattleChange" should "Update GameState" in {
    val gs = defaultGameState
    val friendlyMinion = createFriendlyBoardCard(2)
    val enemyMinion = createEnemyBoardCard(2)

    DeathrattleChange(friendlyMinion.id, friendlyMinion.player, true).updateGameState(gs)
    DeathrattleChange(enemyMinion.id, enemyMinion.player, true).updateGameState(gs)

    gs.friendlyPlayer.board.head.isDeathrattle shouldBe Some(true)
    gs.enemyPlayer.board.head.isDeathrattle shouldBe Some(true)
  }

  "HSAction TurnEnd" should "Update GameState" in {
    val gs = new GameState()
    gs.friendlyPlayer = Player(isComboActive = true, elementalPlayedThisTurn = true)
    gs.enemyPlayer = Player(isComboActive = true, elementalPlayedThisTurn = true)

    val config = ConfigFactory.load()
    val accountName = config.getString("tph.hearthstone.accountName")

    TurnEnd(accountName).updateGameState(gs)
    TurnEnd("SomeOtherName").updateGameState(gs)

    gs.friendlyPlayer.isComboActive shouldBe false
    gs.friendlyPlayer.elementalPlayedThisTurn shouldBe false
    gs.friendlyPlayer.elementalPlayedLastTurn shouldBe true
    gs.enemyPlayer.isComboActive shouldBe false
    gs.enemyPlayer.elementalPlayedThisTurn shouldBe false
    gs.enemyPlayer.elementalPlayedLastTurn shouldBe true
  }


  "HSAction GameOver" should "Update GameState" in {
    var gameOverCalled = false
    val gs = new GameState{
      override def gameOver(): Unit ={
        gameOverCalled = true}
    }

    GameOver().updateGameState(gs)

    gameOverCalled shouldBe true
  }




  ///////////////////////////////////////////// UpdateIRC Actions //////////////////////////////////////////


  "HSAction MulliganStart" should "Update IRCState" in {
    var mulliganStarted = false
    val ircState = new IRCState{
      override def startMulligan(): Unit ={
        mulliganStarted = true
      }
    }


    MulliganStart().updateIRC(ircState)

    mulliganStarted shouldBe true
  }



  "HSAction MulliganOption" should "Update IRCState" in {
    val ircState = new IRCState

    MulliganOption().updateIRC(ircState)
    MulliganOption().updateIRC(ircState)

    ircState.mulliganOptions shouldBe 2
  }



  "HSAction DiscoverStart" should "Update IRCState" in {
    var discoverStarted = false
    val ircState = new IRCState{
      override def startDiscover(): Unit ={
        discoverStarted = true}
    }


    DiscoverStart().updateIRC(ircState)

    discoverStarted shouldBe true
  }



  "HSAction GameOver" should "Update IRCState" in {
    var gameOverCalled = false
    val ircState = new IRCState{
      override def gameOver(): Unit ={
        gameOverCalled = true}
    }


    GameOver().updateIRC(ircState)

    gameOverCalled shouldBe true
  }


  "HSAction TurnStart" should "Update IRCState" in {
    var myTurnStarted = false
    val config = ConfigFactory.load()
    val accountName = config.getString("tph.hearthstone.accountName")
    val ircState = new IRCState{
      override def startMyTurn(): Unit ={
        myTurnStarted = true}
    }


    TurnStart(accountName).updateIRC(ircState)
    TurnStart("SomeOtherName").updateIRC(ircState)

    myTurnStarted shouldBe true
  }


  "HSAction TurnEnd" should "Update IRCState" in {
    var myTurnEnded = false
    val config = ConfigFactory.load()
    val accountName = config.getString("tph.hearthstone.accountName")
    val ircState = new IRCState{
      override def endMyTurn(): Unit ={
        myTurnEnded = true}
    }


    TurnEnd(accountName).updateIRC(ircState)
    TurnEnd("SomeOtherName").updateIRC(ircState)

    myTurnEnded shouldBe true
  }




}
