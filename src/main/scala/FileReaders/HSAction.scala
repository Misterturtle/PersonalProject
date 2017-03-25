package FileReaders

import net.liftweb.json.JsonAST.JObject
import tph.{Card, Constants, GameState, Player}


object HSAction {

  trait HSAction {
    def ExecuteAction(gameState: GameState): GameState
  }

  //////////////////////////////////////////////////Friendly Events//////////////////////////////////////////////////////

  //  case class FriendlyCardDrawn(name:String, id:Int, position:Int, player:Int) extends HSAction {
  //    override def ExecuteAction(gameState: GameState): GameState = {
  //      if(player == gameState.friendlyPlayer.playerNumber) {
  //        val existingCard = gameState.friendlyPlayer.hand.find(_.id == id)
  //        existingCard match {
  //          case Some(card) =>
  //            gameState
  //          case None =>
  //            val newFriendlyPlayer = gameState.friendlyPlayer.AddCardToNextHandPosition(name, id)
  //            new GameState(newFriendlyPlayer, gameState.enemyPlayer)
  //        }
  //      }
  //      else
  //        gameState
  //      }
  //  }

  case class FriendlyMinionControlled(name: String, id: Int, position: Int) extends HSAction {
    //Friendly minion gets removed from board and added to other board
    def ExecuteAction(gameState: GameState): GameState = {
      if (gameState.getCardByID(id).isDefined) {
        val cardBeingControlled = gameState.getCardByID(id).get
        gameState.friendlyPlayer = gameState.friendlyPlayer.RemoveCard(cardBeingControlled)

        val convertedCard = cardBeingControlled.copy(boardPosition = gameState.enemyPlayer.board.size + 1, player = gameState.enemyPlayer.playerNumber)
        gameState.enemyPlayer = gameState.enemyPlayer.AddCard(convertedCard, false)
        gameState
      }
      else
        gameState
    }
  }

  case class FriendlyCardReturn(name: String, id: Int, player: Int) extends HSAction {
    override def ExecuteAction(gameState: GameState): GameState = {
      if (gameState.getCardByID(id).isDefined) {
        val boardCard = gameState.getCardByID(id).get
        val part1Friendly = gameState.friendlyPlayer.RemoveCard(boardCard)
        gameState.friendlyPlayer = part1Friendly.AddCardToNextHandPosition(name, id, boardCard.cardID, boardCard.cardInfo)
        gameState
      }
      else
        gameState
    }
  }

  case class MulliganRedraw(name: String, id: Int, position: Int, playerNumber: Int) extends HSAction {
    override def ExecuteAction(gameState: GameState): GameState = {
      if (gameState.getCardByID(id).isDefined) {
        val card = gameState.getCardByID(id).get
        //Remove card without adjusting hand position.
        //New mulligan redraw will fill empty hand positions.
        gameState.friendlyPlayer = gameState.friendlyPlayer.copy(hand = gameState.friendlyPlayer.hand diff List(card))
        gameState
      }
      else
        gameState
    }
  }


  //////////////////////////////////////////////////Enemy Events//////////////////////////////////////////////////////


  //  case class EnemyCardDrawn(id:Int, position:Int, player:Int ) extends HSAction {
  //    override def ExecuteAction(gameState: GameState): GameState = {
  //      val newEnemyPlayer = gameState.enemyPlayer.AddCardToNextHandPosition(Constants.STRING_UNINIT, id)
  //      new GameState(gameState.friendlyPlayer, newEnemyPlayer)
  //    }
  //  }

  case class EnemyCardReturn(name: String, id: Int, cardID: String, player: Int) extends HSAction {
    override def ExecuteAction(gameState: GameState): GameState = {
      if (gameState.getCardByID(id).isDefined) {
        val card = gameState.getCardByID(id).get
        val part1Enemy = gameState.enemyPlayer.RemoveCard(card)
        gameState.enemyPlayer = part1Enemy.AddCardToNextHandPosition(name, id, cardID, card.cardInfo)
        gameState
      }
      else gameState
    }
  }

  case class EnemyMinionControlled(name: String, id: Int, zonePos: Int) extends HSAction {
    override def ExecuteAction(gameState: GameState): GameState = {
      if (gameState.getCardByID(id).isDefined) {
        val cardBeingControlled = gameState.getCardByID(id).get
        val convertedCard = cardBeingControlled.copy(boardPosition = gameState.friendlyPlayer.board.size + 1, player = gameState.friendlyPlayer.playerNumber)
        gameState.friendlyPlayer = gameState.friendlyPlayer.AddCard(convertedCard, false)

        gameState.enemyPlayer = gameState.enemyPlayer.RemoveCard(cardBeingControlled)
        gameState
      }
      else
        gameState
    }
  }

  case class EnemyMulliganRedraw(id: Int, playerNumber: Int) extends HSAction {
    override def ExecuteAction(gameState: GameState): GameState = {
      if (gameState.getCardByID(id).isDefined) {
        //Weird logic due to the ordering of hearthstones log output
        //A new card is drawn before a mulligan discard is detected.
        val replacementCard = gameState.enemyPlayer.hand.last
        val discardCard = gameState.getCardByID(id).get
        val removeMulliganPlayer = gameState.enemyPlayer.RemoveCard(discardCard)
        //Due to the log output order, the new card is drawn before the old card is removed.
        //So we have to remove the last card and re-add it AFTER we removed the old mulligan card
        gameState.enemyPlayer = removeMulliganPlayer.RemoveCard(removeMulliganPlayer.hand.last).AddCard(replacementCard.copy(handPosition = discardCard.handPosition, boardPosition = Constants.INT_UNINIT, player = playerNumber), true)
        gameState
      }
      else
        gameState
    }
  }


  //////////////////////////////////////////////////Neutral Events//////////////////////////////////////////////////////


  case class CardDrawn(name: String, id: Int, cardID: String, position: Int, player: Int) extends HSAction {
    def ExecuteAction(gameState: GameState): GameState = {
      if (player == gameState.friendlyPlayer.playerNumber) {
        val cardInfo = gameState.getCardInfo(cardID)
        val existingCard = gameState.friendlyPlayer.hand.find(_.id == id)
        existingCard match {
          case Some(card) =>
            gameState
          case None =>
            gameState.friendlyPlayer = gameState.friendlyPlayer.AddCardToNextHandPosition(name, id, cardID, cardInfo)
            gameState
        }
      }
      else {
        val existingCard = gameState.enemyPlayer.hand.find(_.id == id)
        existingCard match {
          case Some(card) =>
            gameState
          case None =>
            gameState.enemyPlayer = gameState.enemyPlayer.AddCardToNextHandPosition(name, id, cardID, new CardInfo(Some(Constants.STRING_UNINIT), Some(Constants.STRING_UNINIT), Some(Constants.INT_UNINIT), Some(Nil), Some(Constants.INT_UNINIT), Some(Constants.STRING_UNINIT), Some(new JObject(Nil))))
            gameState
        }
      }
    }
  }

  case class CardDeath(name: String, id: Int, player: Int) extends HSAction {
    def ExecuteAction(gameState: GameState): GameState = {
      if (gameState.getCardByID(id).isDefined) {
        if (player == gameState.friendlyPlayer.playerNumber) {
          gameState.friendlyPlayer = gameState.friendlyPlayer.RemoveCard(gameState.getCardByID(id).get)
          gameState
        }
        else {
          gameState.enemyPlayer = gameState.enemyPlayer.RemoveCard(gameState.getCardByID(id).get)
          gameState
        }
      }
      else
        gameState
    }
  }

  case class DeckToBoard(name: String, id: Int, cardID: String, player: Int) extends HSAction {
    def ExecuteAction(gameState: GameState): GameState = {
      val cardInfo = gameState.getCardInfo(cardID)
      if (player == gameState.friendlyPlayer.playerNumber) {
        gameState.friendlyPlayer = gameState.friendlyPlayer.AddCard(new Card(name, id, Constants.INT_UNINIT, gameState.friendlyPlayer.board.size + 1, player, cardID, cardInfo = cardInfo), false)
        gameState
      }
      else {
        gameState.enemyPlayer = gameState.enemyPlayer.AddCard(new Card(name, id, Constants.INT_UNINIT, gameState.enemyPlayer.board.size + 1, player, cardID, cardInfo = cardInfo), false)
        gameState
      }
    }
  }

  case class WeaponPlayed(id: Int, player: Int) extends HSAction {
    override def ExecuteAction(gameState: GameState): GameState = {
      if (gameState.getCardByID(id).isDefined) {
        if (player == gameState.friendlyPlayer.playerNumber) {
          gameState.friendlyPlayer = gameState.friendlyPlayer.RemoveCard(gameState.getCardByID(id).get)
          gameState
        }
        else {
          gameState.enemyPlayer = gameState.enemyPlayer.RemoveCard(gameState.getCardByID(id).get)
          gameState
        }
      }
      else
        gameState
    }
  }

  case class ChangeFaceAttackValue(player: Int, value: Int) extends HSAction {
    override def ExecuteAction(gameState: GameState): GameState = {
      if (player == gameState.friendlyPlayer.playerNumber) {
        gameState.friendlyPlayer = new Player(player, value, gameState.friendlyPlayer.hand, gameState.friendlyPlayer.board)
        gameState
      }
      else {
        gameState.enemyPlayer = new Player(player, value, gameState.enemyPlayer.hand, gameState.enemyPlayer.board)
        gameState
      }
    }
  }

  case class SecretPlayed(id: Int, player: Int) extends HSAction {
    override def ExecuteAction(gameState: GameState): GameState = {
      if (gameState.getCardByID(id).isDefined) {
        if (player == gameState.friendlyPlayer.playerNumber) {
          gameState.friendlyPlayer = gameState.friendlyPlayer.RemoveCard(gameState.getCardByID(id).get)
          gameState
        }
        else {
          gameState.enemyPlayer = gameState.enemyPlayer.RemoveCard(gameState.getCardByID(id).get)
          gameState
        }
      }
      else
        gameState
    }
  }

  case class CardPlayed(name: String, id: Int, dstPos: Int, cardID: String, player: Int) extends HSAction {
    override def ExecuteAction(gameState: GameState): GameState = {
      if (gameState.getCardByID(id).isDefined) {
        val cardInfo = gameState.getCardInfo(cardID)
        if (player == gameState.friendlyPlayer.playerNumber) {
          val cardBeingPlayed = gameState.getCardByID(id).get
          val convertedCard = new Card(name, id, Constants.INT_UNINIT, dstPos, player, cardID, cardInfo = cardInfo)

          gameState.friendlyPlayer = gameState.friendlyPlayer.RemoveCard(cardBeingPlayed)
          if (dstPos == 0)
            gameState
          else {
            gameState.friendlyPlayer = gameState.friendlyPlayer.AddCard(convertedCard, false)
            gameState
          }
        }


        else {
          val cardBeingPlayed = gameState.getCardByID(id).get
          val convertedCard = new Card(name, id, Constants.INT_UNINIT, dstPos, player, cardID, cardInfo = cardInfo)

          gameState.enemyPlayer = gameState.enemyPlayer.RemoveCard(cardBeingPlayed)
          if (dstPos == 0)
            gameState
          else {
            gameState.enemyPlayer = gameState.enemyPlayer.AddCard(convertedCard, false)
            gameState
          }
        }
      }
      else
        gameState
    }
  }

  case class MinionSummoned(name: String, id: Int, position: Int, cardID: String, player: Int) extends HSAction {
    override def ExecuteAction(gameState: GameState): GameState = {
      val cardInfo = gameState.getCardInfo(cardID)
      if (player == gameState.friendlyPlayer.playerNumber) {
        gameState.friendlyPlayer = gameState.friendlyPlayer.AddCard(new Card(name, id, Constants.INT_UNINIT, position, player, cardID, cardInfo = cardInfo), false)
        gameState
      }
      else {
        gameState.enemyPlayer = gameState.enemyPlayer.AddCard(new Card(name, id, Constants.INT_UNINIT, position, player, cardID, cardInfo = cardInfo), false)
        gameState
      }
    }
  }

  case class Transform(name: String, oldId: Int, position: Int, cardID: String, newId: Int) extends HSAction {
    override def ExecuteAction(gameState: GameState): GameState = {
      if (gameState.getCardByID(oldId).isDefined) {
        val card = gameState.getCardByID(oldId).get
        val cardInfo = gameState.getCardInfo(cardID)

        if (card.player == gameState.friendlyPlayer.playerNumber) {
          if (card.handPosition != Constants.INT_UNINIT) {
            val newCard = card.copy(name = name, id = newId, cardID = cardID, cardInfo = cardInfo)
            val removedCardFriendlyPlayer = gameState.friendlyPlayer.RemoveCard(card)
            gameState.friendlyPlayer = removedCardFriendlyPlayer.AddCard(newCard, true)
            gameState
          }
          else {
            val newCard = card.copy(name = name, id = newId, cardID = cardID, cardInfo = cardInfo)
            val removedCardFriendlyPlayer = gameState.friendlyPlayer.RemoveCard(card)
            gameState.friendlyPlayer = removedCardFriendlyPlayer.AddCard(newCard, false)
            gameState
          }
        }
        else {
          if (card.handPosition != Constants.INT_UNINIT) {
            val newCard = card.copy(name = name, id = newId, cardID = cardID, cardInfo = cardInfo)
            val removedCardEnemyPlayer = gameState.enemyPlayer.RemoveCard(card)
            gameState.enemyPlayer = removedCardEnemyPlayer.AddCard(newCard, true)
            gameState
          }
          else {
            val newCard = card.copy(name = name, id = newId, cardID = cardID, cardInfo = cardInfo)
            val removedCardEnemyPlayer = gameState.enemyPlayer.RemoveCard(card)
            gameState.enemyPlayer = removedCardEnemyPlayer.AddCard(newCard, false)
            gameState
          }
        }
      }
      else
        gameState
    }
  }


  case class DefinePlayers(friendlyPlayerNumber: Int) extends HSAction {
    override def ExecuteAction(gameState: GameState): GameState = {
      gameState.setPlayerNumbers(friendlyPlayerNumber)
      gameState
    }
  }

  case class GameOver() extends HSAction {
    override def ExecuteAction(gameState: GameState): GameState = {
      gameState.gameOver()
      gameState

    }
  }


  case class HSActionUninit() extends HSAction {
    override def ExecuteAction(gameState: GameState): GameState = {
      gameState
    }
  }


}


