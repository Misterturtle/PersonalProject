package tph

import tph.Card
import tph.GameState
import tph.HSAction.HSAction
import tph.LogFileAction.LogFileAction
import tph.Player




  object HSAction {

    trait HSAction extends LogFileAction {
      def ExecuteAction(gameState: GameState): GameState
    }

    case class KnownCardDrawn(name: String, id: Int, position: Int, player: Int) extends HSAction {
      def ExecuteAction(gameState: GameState): GameState = {

        if (player == gameState.friendlyPlayer.playerNumber) {
          val changedFriendlyPlayer: Player = gameState.friendlyPlayer.AddCard(new Card(name, id, position, Constants.INT_UNINIT, player), true)
          new GameState(changedFriendlyPlayer, gameState.enemyPlayer)
        }
        else {
          val changedEnemyPlayer: Player = gameState.enemyPlayer.AddCard(new Card(name, id, position, Constants.INT_UNINIT, player), true)
          new GameState(gameState.friendlyPlayer, changedEnemyPlayer)
        }
      }
    }

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

      //Enemy Events
      case class EnemyCardDrawn(id: Int, position: Int, player: Int) extends HSAction {
        override def ExecuteAction(gameState: GameState): GameState = {
          val newEnemyPlayer = gameState.enemyPlayer.AddCard(new Card(Constants.STRING_UNINIT, id, position, Constants.INT_UNINIT, player), true)
          new GameState(gameState.friendlyPlayer, newEnemyPlayer)
        }
      }

      case class EnemyMinionControlled(name: String, id: Int, zonePos: Int) extends HSAction {
        override def ExecuteAction(gameState: GameState): GameState = {
          val cardBeingControlled = gameState.GetCardByID(id)
          val convertedCard = new Card(name, id, Constants.INT_UNINIT, gameState.friendlyPlayer.board.size +1, 1)
          val friendlyPlayer = gameState.friendlyPlayer.AddCard(convertedCard, false)

          val enemyPlayer = gameState.enemyPlayer.RemoveCard(cardBeingControlled)
          new GameState(friendlyPlayer, enemyPlayer)
        }
      }


      //Neutral Events

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

      case class ChangeFaceAttackValue(player: Int, value: Int) extends HSAction{
        override  def ExecuteAction(gameState: GameState): GameState = {
          if(player == gameState.friendlyPlayer.playerNumber){
            val newFriendlyPlayer = new Player(player, gameState.friendlyPlayer.hand, gameState.friendlyPlayer.board, value)
            new GameState(newFriendlyPlayer, gameState.enemyPlayer)
          }
          else{
            val newEnemyPlayer = new Player(player, gameState.enemyPlayer.hand, gameState.enemyPlayer.board, value)
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

    //  // I don't think this will ever be used
    //  // case class OldZoneChange(id: Int, zone: String, player: Int, dstZone: Int) extends HSAction
    //
    //  //I dont think this will ever be used
    //  //case class ZoneChange(id: Int, player: Int, zone: String, dstZone: Int) extends HSAction
    //
    //
      case class Sap(name: String, id: Int, player: Int) extends HSAction {
        override def ExecuteAction(gameState: GameState): GameState = {
          if (player == gameState.friendlyPlayer.playerNumber) {
            val cardBeingSapped = gameState.GetCardByID(id)
            val convertedCard = new Card(name, id, gameState.friendlyPlayer.hand.size +1, Constants.INT_UNINIT, 1)

            val playerWithNewHand = gameState.friendlyPlayer.RemoveCard(cardBeingSapped)
            val newPlayer = playerWithNewHand.AddCard(convertedCard, true)
            new GameState(newPlayer, gameState.enemyPlayer)
          }
          else {
            val cardBeingSapped = gameState.GetCardByID(id)
            val convertedCard = new Card(name, id, gameState.enemyPlayer.hand.size +1, Constants.INT_UNINIT, 2)

            val playerWithNewHand = gameState.enemyPlayer.RemoveCard(cardBeingSapped)
            val newPlayer = playerWithNewHand.AddCard(convertedCard, true)
            new GameState(gameState.friendlyPlayer, newPlayer)
          }
        }
      }


    case class CardPlayed(name: String, id: Int, dstPos: Int, player: Int) extends HSAction {
      override def ExecuteAction(gameState: GameState): GameState = {
        if (player == gameState.friendlyPlayer.playerNumber) {
          val cardBeingPlayed = gameState.GetCardByID(id)
          val convertedCard = new Card(name, id, Constants.INT_UNINIT, dstPos, player)

          val playerWithNewHand = gameState.friendlyPlayer.RemoveCard(cardBeingPlayed)
          val newPlayer = playerWithNewHand.AddCard(convertedCard, false)
          new GameState(newPlayer, gameState.enemyPlayer)
        }

        else {
          val cardBeingPlayed = gameState.GetCardByID(id)
          val convertedCard = new Card(name, id, Constants.INT_UNINIT, dstPos, player)

          val playerWithNewHand = gameState.enemyPlayer.RemoveCard(cardBeingPlayed)
          val newPlayer = playerWithNewHand.AddCard(convertedCard, false)
          new GameState(gameState.friendlyPlayer, newPlayer)
        }
      }
    }


    //// I don't think I should ever be using these for a HSAction. Add/Remove card should handle position shifts.
    ////  case class HAND_POSITION_CHANGE(id:Int, pos:Int, player:Int, dstPos:Int) extends HSAction
    ////
    ////  case class BOARD_POSITION_CHANGE(id:Int, player:Int, dstPos:Int) extends HSAction
    //

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

    //case class Transform(oldId: Int, newId: Int) extends HSAction {
    //  override def ExecuteAction(gameState: GameState): GameState = {
    //    val card = gameState.GetCardByID(oldId)
    //
    //
    //  }
    //}
    //
    //case class Hex(name: String, id: Int, player: Int, zonePos: Int) extends HSAction
    }


