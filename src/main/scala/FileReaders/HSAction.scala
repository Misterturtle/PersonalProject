package FileReaders

import tph.{Card, Constants, GameState, Player}


object HSAction {

  trait HSAction{
    def ExecuteAction(gameState: GameState): GameState
  }

  //////////////////////////////////////////////////Friendly Events//////////////////////////////////////////////////////

  case class FriendlyCardDrawn(name:String, id:Int, position:Int, player:Int) extends HSAction {
    override def ExecuteAction(gameState: GameState): GameState = {
      val newFriendlyPlayer = gameState.friendlyPlayer.AddCardToNextHandPosition(name, id)
      new GameState(newFriendlyPlayer, gameState.enemyPlayer)}
  }

  case class FriendlyMinionControlled(name: String, id: Int, position: Int) extends HSAction {
    //Friendly minion gets removed from board and added to other board
    def ExecuteAction(gameState: GameState): GameState = {
      val cardBeingControlled = gameState.GetCardByID(id)
      val friendlyPlayer = gameState.friendlyPlayer.RemoveCard(cardBeingControlled)

      val convertedCard = new Card(cardBeingControlled.name, cardBeingControlled.id, Constants.INT_UNINIT, gameState.enemyPlayer.board.size + 1, 2)
      val enemyPlayer = gameState.enemyPlayer.AddCard(convertedCard, false)
      new GameState(friendlyPlayer, enemyPlayer)
    }
  }

  case class FriendlyCardReturn(name:String, id:Int, player:Int) extends HSAction{
    override def ExecuteAction(gameState: GameState): GameState ={
      val part1Friendly = gameState.friendlyPlayer.RemoveCard(gameState.GetCardByID(id))
      val newFriendly = part1Friendly.AddCardToNextHandPosition(name, id)
      new GameState(newFriendly, gameState.enemyPlayer)
    }
  }

  case class MulliganRedraw(name:String, id:Int, position:Int, playerNumber:Int) extends HSAction{
    override def ExecuteAction(gameState: GameState):GameState ={
      val card = gameState.GetCardByID(id)
      //Remove card without adjusting hand position.
      //New mulligan redraw will fill empty hand positions.
      val friendlyPlayer = new Player(playerNumber, gameState.friendlyPlayer.weaponValue, gameState.friendlyPlayer.hand diff List(card), gameState.friendlyPlayer.board)
      new GameState(friendlyPlayer, gameState.enemyPlayer)
    }
  }




  //////////////////////////////////////////////////Enemy Events//////////////////////////////////////////////////////


  case class EnemyCardDrawn(id:Int, position:Int, player:Int ) extends HSAction {
    override def ExecuteAction(gameState: GameState): GameState = {
      val newEnemyPlayer = gameState.enemyPlayer.AddCardToNextHandPosition(Constants.STRING_UNINIT, id)
      new GameState(gameState.friendlyPlayer, newEnemyPlayer)
    }
  }

  case class EnemyCardReturn(name:String, id:Int, player:Int) extends HSAction{
    override def ExecuteAction(gameState: GameState): GameState ={
      val card = gameState.GetCardByID(id)
      val part1Enemy = gameState.enemyPlayer.RemoveCard(card)
      val newEnemy = part1Enemy.AddCardToNextHandPosition(name, id)
      new GameState(gameState.friendlyPlayer, newEnemy)
    }
  }

  case class EnemyMinionControlled(name: String, id: Int, zonePos: Int) extends HSAction {
    override def ExecuteAction(gameState: GameState): GameState = {
      val cardBeingControlled = gameState.GetCardByID(id)
      val convertedCard = new Card(name, id, Constants.INT_UNINIT, gameState.friendlyPlayer.board.size + 1, 1)
      val friendlyPlayer = gameState.friendlyPlayer.AddCard(convertedCard, false)

      val enemyPlayer = gameState.enemyPlayer.RemoveCard(cardBeingControlled)
      new GameState(friendlyPlayer, enemyPlayer)
    }
  }

  case class EnemyMulliganRedraw(id:Int, playerNumber:Int) extends HSAction{
    override def ExecuteAction(gameState: GameState):GameState = {
      //Weird logic due to the ordering of hearthstones log output
      //A new card is drawn before a mulligan discard is detected.
      val replacementCard = gameState.enemyPlayer.hand.last
      val discardCard = gameState.GetCardByID(id)
      val removeMulliganPlayer = gameState.enemyPlayer.RemoveCard(discardCard)
      val newEnemyPlayer = removeMulliganPlayer.RemoveCard(removeMulliganPlayer.hand.last).AddCard(new Card(replacementCard.name, replacementCard.id, discardCard.handPosition, Constants.INT_UNINIT, playerNumber), true)
      new GameState(gameState.friendlyPlayer, newEnemyPlayer)
    }
  }





  //////////////////////////////////////////////////Neutral Events//////////////////////////////////////////////////////


  case class CardDeath(name: String, id: Int, player: Int) extends HSAction {
    def ExecuteAction(gameState: GameState): GameState = {
      if (player == gameState.friendlyPlayer.playerNumber) {
        val newFriendlyPlayer = gameState.friendlyPlayer.RemoveCard(gameState.GetCardByID(id))
        new GameState(newFriendlyPlayer, gameState.enemyPlayer)
      }
      else {
        val newEnemyPlayer = gameState.enemyPlayer.RemoveCard(gameState.GetCardByID(id))
        new GameState(gameState.friendlyPlayer, newEnemyPlayer)
      }
    }
  }

  case class WeaponPlayed(id: Int, player: Int) extends HSAction {
    override def ExecuteAction(gameState: GameState): GameState = {
      if (player == gameState.friendlyPlayer.playerNumber) {
        val newFriendlyPlayer = gameState.friendlyPlayer.RemoveCard(gameState.GetCardByID(id))
        new GameState(newFriendlyPlayer, gameState.enemyPlayer)
      }
      else {
        val newEnemyPlayer = gameState.enemyPlayer.RemoveCard(gameState.GetCardByID(id))
        new GameState(gameState.friendlyPlayer, newEnemyPlayer)
      }
    }
  }

  case class ChangeFaceAttackValue(player: Int, value: Int) extends HSAction {
    override def ExecuteAction(gameState: GameState): GameState = {
      if (player == gameState.friendlyPlayer.playerNumber) {
        val newFriendlyPlayer = new Player(player, value, gameState.friendlyPlayer.hand, gameState.friendlyPlayer.board)
        new GameState(newFriendlyPlayer, gameState.enemyPlayer)
      }
      else {
        val newEnemyPlayer = new Player(player, value, gameState.enemyPlayer.hand, gameState.enemyPlayer.board)
        new GameState(gameState.friendlyPlayer, newEnemyPlayer)
      }
    }
  }

  case class SecretPlayed(id: Int, player: Int) extends HSAction {
    override def ExecuteAction(gameState: GameState): GameState = {
      if (player == gameState.friendlyPlayer.playerNumber) {
        val newFriendlyPlayer = gameState.friendlyPlayer.RemoveCard(gameState.GetCardByID(id))
        new GameState(newFriendlyPlayer, gameState.enemyPlayer)
      }
      else {
        val newEnemyPlayer = gameState.enemyPlayer.RemoveCard(gameState.GetCardByID(id))
        new GameState(gameState.friendlyPlayer, newEnemyPlayer)
      }
    }
  }

  case class CardPlayed(name: String, id: Int, dstPos: Int, player: Int) extends HSAction {
    override def ExecuteAction(gameState: GameState): GameState = {
        if (player == gameState.friendlyPlayer.playerNumber) {
          val cardBeingPlayed = gameState.GetCardByID(id)
          val convertedCard = new Card(name, id, Constants.INT_UNINIT, dstPos, player)

          val playerWithNewHand = gameState.friendlyPlayer.RemoveCard(cardBeingPlayed)
          if(dstPos == 0)
            new GameState(playerWithNewHand, gameState.enemyPlayer)
          else {
            val newPlayer = playerWithNewHand.AddCard(convertedCard, false)
            new GameState(newPlayer, gameState.enemyPlayer)
          }
        }

        else {
          val cardBeingPlayed = gameState.GetCardByID(id)
          val convertedCard = new Card(name, id, Constants.INT_UNINIT, dstPos, player)

          val playerWithNewHand = gameState.enemyPlayer.RemoveCard(cardBeingPlayed)
          if(dstPos == 0)
            new GameState(gameState.friendlyPlayer, playerWithNewHand)
          else {
            val newPlayer = playerWithNewHand.AddCard(convertedCard, false)
            new GameState(gameState.friendlyPlayer, newPlayer)
          }
        }
      }
    }

  case class MinionSummoned(name: String, id: Int, position: Int, player: Int) extends HSAction {
    override def ExecuteAction(gameState: GameState): GameState = {
      if (player == gameState.friendlyPlayer.playerNumber) {
        val newPlayer = gameState.friendlyPlayer.AddCard(new Card(name, id, Constants.INT_UNINIT, position, player), false)
        new GameState(newPlayer, gameState.enemyPlayer)
      }
      else {
        val newPlayer = gameState.enemyPlayer.AddCard(new Card(name, id, Constants.INT_UNINIT, position, player), false)
        new GameState(gameState.friendlyPlayer, newPlayer)
      }
    }
  }

  case class Transform(oldId: Int, newId: Int) extends HSAction {
    override def ExecuteAction(gameState: GameState): GameState = {
      val card = gameState.GetCardByID(oldId)


      if (card.player == gameState.friendlyPlayer.playerNumber) {
        if (card.handPosition != Constants.INT_UNINIT) {
          val newCard = new Card("Transformed Friendly Card", newId, card.handPosition, card.boardPosition, card.player)
          val removedCardFriendlyPlayer = gameState.friendlyPlayer.RemoveCard(card)
          val newFriendlyPlayer = removedCardFriendlyPlayer.AddCard(newCard, true)
          new GameState(newFriendlyPlayer, gameState.enemyPlayer)
        }
        else {
          val newCard = new Card("Transformed Friendly Minion", newId, card.handPosition, card.boardPosition, card.player)
          val removedCardFriendlyPlayer = gameState.friendlyPlayer.RemoveCard(card)
          val newFriendlyPlayer = removedCardFriendlyPlayer.AddCard(newCard, false)
          new GameState(newFriendlyPlayer, gameState.enemyPlayer)
        }
      }
      else {
        if (card.handPosition != Constants.INT_UNINIT) {
          val newCard = new Card("Transformed Enemy Card", newId, card.handPosition, card.boardPosition, card.player)
          val removedCardEnemyPlayer = gameState.enemyPlayer.RemoveCard(card)
          val newEnemyPlayer = removedCardEnemyPlayer.AddCard(newCard, true)
          new GameState(gameState.friendlyPlayer, newEnemyPlayer)
        }
        else {
          val newCard = new Card("Transformed Enemy Minion", newId, card.handPosition, card.boardPosition, card.player)
          val removedCardEnemyPlayer = gameState.enemyPlayer.RemoveCard(card)
          val newEnemyPlayer = removedCardEnemyPlayer.AddCard(newCard, false)
          new GameState(gameState.friendlyPlayer, newEnemyPlayer)
        }
      }
    }
  }

  case class DefinePlayers(friendlyPlayerNumber: Int) extends HSAction {
    override def ExecuteAction(gameState: GameState):GameState = {
      gameState.SetPlayerNumbers(friendlyPlayerNumber)
    }
  }

  case class GameOver() extends HSAction {
    override def ExecuteAction(gameState: GameState):GameState = {
      new GameState(new Player(Constants.INT_UNINIT, 0), new Player(Constants.INT_UNINIT, 0))

    }

  }

  case class HSActionUninit() extends HSAction {
    override def ExecuteAction(gameState: GameState): GameState = {
      gameState
    }
  }








}


