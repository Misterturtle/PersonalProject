package FileReaders

import Logic.IRCState
import net.liftweb.json.JsonAST.JObject
import tph._


object HSAction {

  trait HSAction {
    def updateGameState(gameState: GameState): Unit
    def updateIRC(ircState:IRCState): Unit
  }
  
  trait GameStateAction extends HSAction{
    def updateGameState(gameState: GameState): Unit
    def updateIRC(ircState: IRCState): Unit = {}
  }

  trait IRCAction extends HSAction{
    def updateIRC(ircState: IRCState): Unit
    def updateGameState(gameState:GameState): Unit = {}
  }

  case class Entity(rawString:String){
    val parseUnknownRegex = """\[name=UNKNOWN ENTITY \[cardType=INVALID\] id=(\d+) zone=(.+) zonePos=(\d+) cardID= player=(\d+)\]""".r
    val parseKnownRegex = """\[name=(.+) id=(.+) zone=(.+) zonePos=(\d+) cardID=(.+) player=(\d+)\]""".r
    val controllerRegex = """(.+)""".r


    val name:String = rawString match{
      case parseUnknownRegex(id,zone,zonePos,player)=>


    }


    rawString match{




    }


  }

  //////////////////////////////////////////////////Friendly Events//////////////////////////////////////////////////////

  case class FriendlyMinionControlled(name: String, id: Int, position: Int) extends GameStateAction {
    //Friendly minion gets removed from board and added to other board
    def updateGameState(gameState: GameState): Unit = {
      if (gameState.getCardByID(id).isDefined) {
        val cardBeingControlled = gameState.getCardByID(id).get
        gameState.friendlyPlayer = gameState.friendlyPlayer.removeCard(cardBeingControlled)

        val convertedCard = cardBeingControlled.copy(boardPosition = gameState.enemyPlayer.board.size + 1, player = gameState.enemyPlayer.playerNumber)
        gameState.enemyPlayer = gameState.enemyPlayer.addCard(convertedCard, false)
      }
    }
  }

  case class FriendlyCardReturn(name: String, id: Int, player: Int) extends GameStateAction {
    override def updateGameState(gameState: GameState): Unit = {
      if (gameState.getCardByID(id).isDefined) {
        val boardCard = gameState.getCardByID(id).get
        val part1Friendly = gameState.friendlyPlayer.removeCard(boardCard)
        gameState.friendlyPlayer = part1Friendly.addCardToNextHandPosition(name, id, boardCard.cardID, boardCard.cardInfo)
      }
    }
  }

  case class MulliganRedraw(name: String, id: Int, position: Int, playerNumber: Int) extends GameStateAction {
    override def updateGameState(gameState: GameState): Unit = {
      if (gameState.getCardByID(id).isDefined) {
        val card = gameState.getCardByID(id).get
        //Remove card without adjusting hand position.
        //New mulligan redraw will fill empty hand positions.
        gameState.friendlyPlayer = gameState.friendlyPlayer.copy(hand = gameState.friendlyPlayer.hand diff List(card))
      }
    }
  }


  //////////////////////////////////////////////////Enemy Events//////////////////////////////////////////////////////


  case class EnemyCardReturn(name: String, id: Int, cardID: String, player: Int) extends GameStateAction {
    override def updateGameState(gameState: GameState): Unit = {
      if (gameState.getCardByID(id).isDefined) {
        val card = gameState.getCardByID(id).get
        val part1Enemy = gameState.enemyPlayer.removeCard(card)
        gameState.enemyPlayer = part1Enemy.addCardToNextHandPosition(name, id, cardID, card.cardInfo)
      }
    }
  }

  case class EnemyMinionControlled(name: String, id: Int, zonePos: Int) extends GameStateAction {
    override def updateGameState(gameState: GameState): Unit = {
      if (gameState.getCardByID(id).isDefined) {
        val cardBeingControlled = gameState.getCardByID(id).get
        val convertedCard = cardBeingControlled.copy(boardPosition = gameState.friendlyPlayer.board.size + 1, player = gameState.friendlyPlayer.playerNumber)
        gameState.friendlyPlayer = gameState.friendlyPlayer.addCard(convertedCard, false)
        gameState.enemyPlayer = gameState.enemyPlayer.removeCard(cardBeingControlled)
      }
    }
  }

  case class EnemyMulliganRedraw(id: Int, playerNumber: Int) extends GameStateAction {
    override def updateGameState(gameState: GameState): Unit = {
      if (gameState.getCardByID(id).isDefined) {
        //Weird logic due to the ordering of hearthstones log output
        //A new card is drawn before a mulligan discard is detected.
        val replacementCard = gameState.enemyPlayer.hand.last
        val discardCard = gameState.getCardByID(id).get
        val removeMulliganPlayer = gameState.enemyPlayer.removeCard(discardCard)
        //Due to the log output order, the new card is drawn before the old card is removed.
        //So we have to remove the last card and re-add it AFTER we removed the old mulligan card
        gameState.enemyPlayer = removeMulliganPlayer.removeCard(removeMulliganPlayer.hand.last).addCard(replacementCard.copy(handPosition = discardCard.handPosition, boardPosition = Constants.INT_UNINIT, player = playerNumber), true)
      }
    }
  }


  //////////////////////////////////////////////////Neutral Events//////////////////////////////////////////////////////


  case class CardDrawn(name: String, id: Int, cardID: String, position: Int, player: Int) extends GameStateAction {
    def updateGameState(gameState: GameState): Unit = {
      if (player == gameState.friendlyPlayer.playerNumber) {
        val cardInfo = gameState.getCardInfo(cardID)
        val existingCard = gameState.friendlyPlayer.hand.find(_.id == id)
        existingCard match {
          case Some(card) =>
          case None =>
            gameState.friendlyPlayer = gameState.friendlyPlayer.addCardToNextHandPosition(name, id, cardID, cardInfo)
        }
      }
      else {
        val existingCard = gameState.enemyPlayer.hand.find(_.id == id)
        existingCard match {
          case Some(card) =>
          case None =>
            gameState.enemyPlayer = gameState.enemyPlayer.addCardToNextHandPosition(name, id, cardID, Constants.emptyCardInfo)
        }
      }
    }
  }

  case class CardDeath(name: String, id: Int, player: Int) extends GameStateAction {
    def updateGameState(gameState: GameState): Unit = {
      if (gameState.getCardByID(id).isDefined) {
        if (player == gameState.friendlyPlayer.playerNumber) {
          gameState.friendlyPlayer = gameState.friendlyPlayer.removeCard(gameState.getCardByID(id).get)
        }
        else {
          gameState.enemyPlayer = gameState.enemyPlayer.removeCard(gameState.getCardByID(id).get)
        }
      }
    }
  }

  case class NewHeroPower(id: Int, cardID: String, player: Int) extends GameStateAction {
    def updateGameState(gameState: GameState): Unit = {
      val cardInfo = gameState.getCardInfo(cardID)
        if (player == gameState.friendlyPlayer.playerNumber) {
          val newHeroPower = Card("Friendly Hero Power", id, Constants.INT_UNINIT, Constants.INT_UNINIT, player, cardID, cardInfo = cardInfo)
          gameState.friendlyPlayer = gameState.friendlyPlayer.copy(heroPower = Some(newHeroPower))
        }
        else {
          val newHeroPower = Card("Enemy Hero Power", id, Constants.INT_UNINIT, Constants.INT_UNINIT, player, cardID, cardInfo = cardInfo)
          gameState.enemyPlayer = gameState.enemyPlayer.copy(heroPower = Some(newHeroPower))
        }
    }
  }


  case class DeckToBoard(name: String, id: Int, cardID: String, player: Int) extends GameStateAction {
    def updateGameState(gameState: GameState): Unit = {
      val cardInfo = gameState.getCardInfo(cardID)
      if (player == gameState.friendlyPlayer.playerNumber) {
        gameState.friendlyPlayer = gameState.friendlyPlayer.addCard(new Card(name, id, Constants.INT_UNINIT, gameState.friendlyPlayer.board.size + 1, player, cardID, cardInfo = cardInfo), false)
      }
      else {
        gameState.enemyPlayer = gameState.enemyPlayer.addCard(new Card(name, id, Constants.INT_UNINIT, gameState.enemyPlayer.board.size + 1, player, cardID, cardInfo = cardInfo), false)
      }
    }
  }

  case class WeaponChange(id: Int, player: Int, isEquipped:Boolean) extends GameStateAction {
    override def updateGameState(gameState: GameState): Unit = {
        if (player == gameState.friendlyPlayer.playerNumber) {
          gameState.friendlyPlayer = gameState.friendlyPlayer.copy(isWeaponEquipped = isEquipped)
        }
        else {
          gameState.enemyPlayer = gameState.enemyPlayer.copy(isWeaponEquipped = isEquipped)
        }
      }
  }


  case class ComboActive(playerName: String, isCombo:Boolean) extends GameStateAction {
    override def updateGameState(gameState: GameState): Unit = {
      if (playerName == gameState.accountName) {
        gameState.friendlyPlayer = gameState.friendlyPlayer.copy(isComboActive = isCombo)
      }
      else {
        gameState.enemyPlayer = gameState.enemyPlayer.copy(isComboActive = isCombo)
      }
    }
  }

  case class NewHero(id:Int, cardID:String, player:Int) extends GameStateAction {
    override def updateGameState(gameState: GameState): Unit = {
      val cardInfo = gameState.getCardInfo(cardID)
      if (player == gameState.friendlyPlayer.playerNumber) {
        val newHero = Card("Friendly Hero", id, Constants.INT_UNINIT, 0, player, cardID, cardInfo = cardInfo)
        gameState.friendlyPlayer = gameState.friendlyPlayer.copy(hero = Some(newHero))
      }
      else {
        val newHero = Card("Enemy Hero", id, Constants.INT_UNINIT, 0, player, cardID, cardInfo = cardInfo)
        gameState.enemyPlayer = gameState.enemyPlayer.copy(hero = Some(newHero))
      }
    }
  }

  case class ReplaceHero(id:Int, cardID:String, player:Int, oldHeroID:Int) extends GameStateAction {
    override def updateGameState(gameState: GameState): Unit = {
      if (player == gameState.friendlyPlayer.playerNumber) {
        if(oldHeroID == gameState.friendlyPlayer.hero.getOrElse(NoCard()).id) {
          val newHeroCard = Card("Friendly Hero", id, Constants.INT_UNINIT, 0, player, cardID)
          gameState.friendlyPlayer = gameState.friendlyPlayer.copy(hero = Some(newHeroCard))
        }
      }
      else {
        if(oldHeroID == gameState.enemyPlayer.hero.getOrElse(NoCard()).id) {
          val newHero = Card("Enemy Hero", id, Constants.INT_UNINIT, 0, player, cardID)
          gameState.enemyPlayer = gameState.enemyPlayer.copy(hero = Some(newHero))
        }
      }
    }
  }


  case class ChangeAttackValue(player: Int, value: Int, id: Int, position:Int) extends GameStateAction {
    override def updateGameState(gameState: GameState): Unit = {
      if (player == gameState.friendlyPlayer.playerNumber) {
        if(position == 0 && gameState.friendlyPlayer.hero.nonEmpty){
          val currentHero = gameState.friendlyPlayer.hero.get
          val modifiedHero = currentHero.copy(attack = Some(value))
          gameState.friendlyPlayer = gameState.friendlyPlayer.copy(hero = Some(modifiedHero))
        }
        else{
          val card = gameState.friendlyPlayer.board.find(_.id == id)
          if(card.nonEmpty){
            val modCard = card.get.copy(attack = Some(value))
            val newBoard = modCard :: (gameState.friendlyPlayer.board diff List(card.get))
            gameState.friendlyPlayer = gameState.friendlyPlayer.copy(board = newBoard)
          }
        }
      }
      else {
          if(position == 0 && gameState.enemyPlayer.hero.nonEmpty){
            val currentHero = gameState.enemyPlayer.hero.get
            val modifiedHero = currentHero.copy(attack = Some(value))
            gameState.enemyPlayer = gameState.enemyPlayer.copy(hero = Some(modifiedHero))
          }
          else{
            val card = gameState.enemyPlayer.board.find(_.id == id)
            if(card.nonEmpty){
              val modCard = card.get.copy(attack = Some(value))
              val newBoard = modCard :: (gameState.enemyPlayer.board diff List(card.get))
              gameState.enemyPlayer = gameState.enemyPlayer.copy(board = newBoard)
            }
          }
      }
    }
  }


  case class CardPlayed(name: String, id: Int, dstPos: Int, cardID: String, player: Int) extends GameStateAction {
    override def updateGameState(gameState: GameState): Unit = {
      if (gameState.getCardByID(id).isDefined) {
        val cardInfo = gameState.getCardInfo(cardID)
        if (player == gameState.friendlyPlayer.playerNumber) {
          val cardBeingPlayed = gameState.getCardByID(id).get
          val convertedCard = new Card(name, id, Constants.INT_UNINIT, dstPos, player, cardID, cardInfo = cardInfo)

          gameState.friendlyPlayer = gameState.friendlyPlayer.removeCard(cardBeingPlayed)
          if (dstPos != 0)
            gameState.friendlyPlayer = gameState.friendlyPlayer.addCard(convertedCard, false)

          if(convertedCard.cardInfo.race.getOrElse("None") == "ELEMENTAL")
            gameState.friendlyPlayer = gameState.friendlyPlayer.copy(elementalPlayedThisTurn = true)
        }
        else {
          val cardBeingPlayed = gameState.getCardByID(id).get
          val convertedCard = new Card(name, id, Constants.INT_UNINIT, dstPos, player, cardID, cardInfo = cardInfo)

          gameState.enemyPlayer = gameState.enemyPlayer.removeCard(cardBeingPlayed)
          if (dstPos != 0)
            gameState.enemyPlayer = gameState.enemyPlayer.addCard(convertedCard, false)

          if(convertedCard.cardInfo.race.getOrElse("None") == "ELEMENTAL")
            gameState.enemyPlayer = gameState.enemyPlayer.copy(elementalPlayedThisTurn = true)
        }
      }
    }
  }

  case class MinionSummoned(name: String, id: Int, position: Int, cardID: String, player: Int) extends GameStateAction {
    override def updateGameState(gameState: GameState): Unit = {
      val cardInfo = gameState.getCardInfo(cardID)
      if (player == gameState.friendlyPlayer.playerNumber) {
        gameState.friendlyPlayer = gameState.friendlyPlayer.addCard(new Card(name, id, Constants.INT_UNINIT, position, player, cardID, cardInfo = cardInfo), false)
      }
      else {
        gameState.enemyPlayer = gameState.enemyPlayer.addCard(new Card(name, id, Constants.INT_UNINIT, position, player, cardID, cardInfo = cardInfo), false)
      }
    }
  }

  case class Transform(name: String, oldId: Int, position: Int, cardID: String, newId: Int) extends GameStateAction {
    override def updateGameState(gameState: GameState): Unit = {
      if (gameState.getCardByID(oldId).isDefined) {
        val card = gameState.getCardByID(oldId).get
        val cardInfo = gameState.getCardInfo(cardID)

        if (card.player == gameState.friendlyPlayer.playerNumber) {
          if (card.handPosition != Constants.INT_UNINIT) {
            val newCard = card.copy(name = name, id = newId, cardID = cardID, cardInfo = cardInfo)
            val removedCardFriendlyPlayer = gameState.friendlyPlayer.removeCard(card)
            gameState.friendlyPlayer = removedCardFriendlyPlayer.addCard(newCard, true)
          }
          else {
            val newCard = card.copy(name = name, id = newId, cardID = cardID, cardInfo = cardInfo)
            val removedCardFriendlyPlayer = gameState.friendlyPlayer.removeCard(card)
            gameState.friendlyPlayer = removedCardFriendlyPlayer.addCard(newCard, false)
          }
        }
        else {
          if (card.handPosition != Constants.INT_UNINIT) {
            val newCard = card.copy(name = name, id = newId, cardID = cardID, cardInfo = cardInfo)
            val removedCardEnemyPlayer = gameState.enemyPlayer.removeCard(card)
            gameState.enemyPlayer = removedCardEnemyPlayer.addCard(newCard, true)
          }
          else {
            val newCard = card.copy(name = name, id = newId, cardID = cardID, cardInfo = cardInfo)
            val removedCardEnemyPlayer = gameState.enemyPlayer.removeCard(card)
            gameState.enemyPlayer = removedCardEnemyPlayer.addCard(newCard, false)
          }
        }
      }
    }
  }


  case class Frozen(id: Int, player: Int, frozenValue: Int) extends GameStateAction {
    override def updateGameState(gameState: GameState): Unit = {
      val card = gameState.getCardByID(id)
      val boolMap = Map[Int, Boolean](1 -> true, 0 -> false)
      if (card.nonEmpty) {
        val newCard = card.get.copy(isFrozen = boolMap(frozenValue))
        if (player == gameState.friendlyPlayer.playerNumber) {
          if (card.get.boardPosition == 0) {
            gameState.friendlyPlayer = gameState.friendlyPlayer.copy(hero = Some(newCard))
          }
          else {
            val newBoard = newCard :: (gameState.friendlyPlayer.board diff List(card.get))
            gameState.friendlyPlayer = gameState.friendlyPlayer.copy(board = newBoard)
          }
        }
        else {
          if (card.get.boardPosition == 0) {
            gameState.enemyPlayer = gameState.enemyPlayer.copy(hero = Some(newCard))
          }
          else {
            val newBoard = newCard :: (gameState.enemyPlayer.board diff List(card.get))
            gameState.enemyPlayer = gameState.enemyPlayer.copy(board = newBoard)
          }
        }
      }
    }
  }
  case class SecretPlayed(id: Int, player: Int) extends GameStateAction {
    override def updateGameState(gameState: GameState): Unit = {
      if (gameState.getCardByID(id).isDefined) {
        if (player == gameState.friendlyPlayer.playerNumber) {
          val currentSecrets = gameState.friendlyPlayer.secretsInPlay
          gameState.friendlyPlayer = gameState.friendlyPlayer.removeCard(gameState.getCardByID(id).get).copy(secretsInPlay = currentSecrets +1)
        }
        else {
          val currentSecrets = gameState.enemyPlayer.secretsInPlay
          gameState.enemyPlayer = gameState.enemyPlayer.removeCard(gameState.getCardByID(id).get).copy(secretsInPlay = currentSecrets +1)
        }
      }
    }
  }

  case class MinionDamaged(id:Int, player:Int, damage:Int) extends GameStateAction {
    override def updateGameState(gameState: GameState): Unit = {
      val isDamaged = {
        if (damage == 0) false else true
      }
      val card = gameState.getCardByID(id)
      if (card.nonEmpty) {
        if (player == gameState.friendlyPlayer.playerNumber) {
          val newCard = card.get.copy(isDamaged = isDamaged)
          if (card.get.boardPosition == 0) {
            gameState.friendlyPlayer = gameState.friendlyPlayer.copy(hero = Some(newCard))
          }
          else {

            val newBoard = newCard :: (gameState.friendlyPlayer.board diff List(card.get))
            gameState.friendlyPlayer = gameState.friendlyPlayer.copy(board = newBoard)
          }
        }
        else {
          val newCard = card.get.copy(isDamaged = isDamaged)
          if (card.get.boardPosition == 0) {
            gameState.enemyPlayer = gameState.enemyPlayer.copy(hero = Some(newCard))
          }
          else {
            val newBoard = newCard :: (gameState.enemyPlayer.board diff List(card.get))
            gameState.enemyPlayer = gameState.enemyPlayer.copy(board = newBoard)
          }
        }
      }
    }
  }
  case class MinionStealthed(isStealthed:Boolean, id:Int, player:Int) extends GameStateAction {
    override def updateGameState(gameState: GameState): Unit = {
      val card = gameState.getCardByID(id)
      if (card.nonEmpty) {
        if (player == gameState.friendlyPlayer.playerNumber) {
          val newCard = card.get.copy(isStealthed = isStealthed)
          val newBoard = newCard :: (gameState.friendlyPlayer.board diff List(card.get))
          gameState.friendlyPlayer = gameState.friendlyPlayer.copy(board = newBoard)
        }
        else {
          val newCard = card.get.copy(isStealthed = isStealthed)
          val newBoard = newCard :: (gameState.enemyPlayer.board diff List(card.get))
          gameState.enemyPlayer = gameState.enemyPlayer.copy(board = newBoard)
        }
      }
    }
  }

  case class TauntChange(id:Int, player:Int, isTaunt:Boolean) extends GameStateAction {
    override def updateGameState(gameState: GameState): Unit = {
      val card = gameState.getCardByID(id)
      if (card.nonEmpty) {
        if (player == gameState.friendlyPlayer.playerNumber) {
          val newCard = card.get.copy(isTaunt = Some(isTaunt))
          val newBoard = newCard :: (gameState.friendlyPlayer.board diff List(card.get))
          gameState.friendlyPlayer = gameState.friendlyPlayer.copy(board = newBoard)
        }
        else {
          val newCard = card.get.copy(isTaunt = Some(isTaunt))
          val newBoard = newCard :: (gameState.enemyPlayer.board diff List(card.get))
          gameState.enemyPlayer = gameState.enemyPlayer.copy(board = newBoard)
        }
      }
    }
  }


  case class DeathrattleChange(id:Int, player:Int, isDeathrattle:Boolean) extends GameStateAction {
    override def updateGameState(gameState: GameState): Unit = {
      val card = gameState.getCardByID(id)
      if (card.nonEmpty) {
        if (player == gameState.friendlyPlayer.playerNumber) {
          val newCard = card.get.copy(isDeathrattle = Some(isDeathrattle))
          val newBoard = newCard :: (gameState.friendlyPlayer.board diff List(card.get))
          gameState.friendlyPlayer = gameState.friendlyPlayer.copy(board = newBoard)
        }
        else {
          val newCard = card.get.copy(isDeathrattle = Some(isDeathrattle))
          val newBoard = newCard :: (gameState.enemyPlayer.board diff List(card.get))
          gameState.enemyPlayer = gameState.enemyPlayer.copy(board = newBoard)
        }
      }
    }
  }


  case class SecretDestroyed(id: Int, player: Int) extends GameStateAction {
    override def updateGameState(gameState: GameState): Unit = {
      if (player == gameState.friendlyPlayer.playerNumber) {
        val currentSecrets = gameState.friendlyPlayer.secretsInPlay
        gameState.friendlyPlayer = gameState.friendlyPlayer.copy(secretsInPlay = currentSecrets -1)
      }
      else {
        val currentSecrets = gameState.enemyPlayer.secretsInPlay
        gameState.enemyPlayer = gameState.enemyPlayer.copy(secretsInPlay = currentSecrets -1)
      }
    }
  }


  case class MulliganStart() extends IRCAction {
    override def updateIRC(ircState:IRCState): Unit = {
        ircState.startMulligan()
    }
  }


  case class MulliganOption() extends IRCAction {
    override def updateIRC(ircState:IRCState): Unit = {
      ircState.mulliganOptions += 1
    }
  }

  case class DiscoverStart() extends IRCAction {
    override def updateIRC(ircState:IRCState): Unit = {
        ircState.startDiscover()
    }
  }


  case class DefinePlayers(id:Int, cardID:String, friendlyPlayerNumber: Int) extends GameStateAction {
    override def updateGameState(gameState: GameState): Unit = {
      gameState.setPlayerNumbers(friendlyPlayerNumber)
      val newHero = Card("Friendly Hero", id, Constants.INT_UNINIT, 0, friendlyPlayerNumber, cardID)
      gameState.friendlyPlayer = gameState.friendlyPlayer.copy(hero = Some(newHero))
    }
  }

  case class GameOver() extends GameStateAction with IRCAction {
    override def updateGameState(gameState: GameState): Unit = {
      gameState.gameOver()
    }

    override def updateIRC(ircState:IRCState):Unit = {
      ircState.gameOver()
    }
  }

  case class TurnStart(playerName:String) extends IRCAction{
    override def updateIRC(ircState:IRCState): Unit = {
          ircState.startTurn()
    }
  }

  case class TurnEnd(playerName:String) extends GameStateAction with IRCAction{
    override def updateGameState(gameState: GameState): Unit = {
      if(playerName == gameState.accountName) {
        gameState.friendlyPlayer = gameState.friendlyPlayer.copy(isComboActive = false)
        if(gameState.friendlyPlayer.elementalPlayedThisTurn)
          gameState.friendlyPlayer = gameState.friendlyPlayer.copy(elementalPlayedLastTurn = true, elementalPlayedThisTurn = false)
      }
      else {
          gameState.enemyPlayer = gameState.enemyPlayer.copy(isComboActive = false)
        if(gameState.enemyPlayer.elementalPlayedThisTurn)
          gameState.enemyPlayer = gameState.enemyPlayer.copy(elementalPlayedLastTurn = true, elementalPlayedThisTurn = false)
      }
    }

    override def updateIRC(ircState:IRCState): Unit = {
        ircState.endTurn()
    }
  }



  case class OptionChoice(choiceNum:Int, choiceType:String, mainEntity:Entity, error, errorParam)





  case class HSActionUninit() extends HSAction {
    override def updateGameState(gameState: GameState): Unit = {}
    override def updateIRC(ircState:IRCState):Unit = {}
  }
}


