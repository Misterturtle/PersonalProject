package tph.Immutable

import tph.Constants
import tph.Immutable.HSAction.HSAction
import tph.Immutable.LogFileAction.LogFileAction

import scala.util.matching.Regex

/**
  * Created by Harambe on 2/14/2017.
  */

object LogFileAction {

  trait LogFileAction {


  }

  case class DISCOVER_OPTION(option: Int) extends LogFileAction

  case class FACE_ATTACK_VALUE(player: Int, value: Int) extends LogFileAction

  case class DEFINE_PLAYERS(friendlyPlayerID: Int) extends LogFileAction

}

object HSAction {

  trait HSAction extends LogFileAction {
    def ExecuteAction(gameState: GameState): GameState
  }

  case class KnownCardDrawn(name: String, id: Int, position: Int, player: Int) extends HSAction {
    def ExecuteAction(gameState: GameState): GameState = {
      if (player == gameState.friendlyPlayer.playerNumber) {
        val playerNumber = gameState.friendlyPlayer.playerNumber
        val friendlyBoard = gameState.friendlyPlayer.board
        val changedFriendlyPlayer: Player = gameState.friendlyPlayer.AddCard(new Card(name, id, position, Constants.INT_UNINIT, player), true)
        new GameState(changedFriendlyPlayer, gameState.enemyPlayer)
      }
      else {
        val playerNumber = gameState.enemyPlayer.playerNumber
        val enemyBoard = gameState.enemyPlayer.board
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
      val friendlyPlayer = gameState.friendlyPlayer.RemoveCard(gameState.GetCardByID(id))
      val enemyPlayer = gameState.enemyPlayer.AddCard(gameState.GetCardByID(id), false)
      new GameState(friendlyPlayer, enemyPlayer)
    }
  }

  //Enemy Events
  case class EnemyCardDrawn(id: Int, position: Int, player: Int) extends HSAction {
    override def ExecuteAction(gameState: GameState): GameState = {
      val newEnemyPlayer = gameState.enemyPlayer.AddCard(new Card(Constants.STRING_UNINIT, id, position, Constants.INT_UNINIT, player))
      new GameState(gameState.friendlyPlayer, newEnemyPlayer)
    }
  }

  case class EnemyMinionControlled(name: String, id: Int, zonePos: Int) extends HSAction {
    override def ExecuteAction(gameState: GameState): GameState = {
      val enemyPlayer = gameState.enemyPlayer.RemoveCard(gameState.GetCardByID(id))
      val friendlyPlayer = gameState.friendlyPlayer.AddCard(gameState.GetCardByID(id), false)
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

  // I don't think this will ever be used
  // case class OldZoneChange(id: Int, zone: String, player: Int, dstZone: Int) extends HSAction

  //I dont think this will ever be used
  //case class ZoneChange(id: Int, player: Int, zone: String, dstZone: Int) extends HSAction


  case class Sap(name: String, id: Int, player: Int) extends HSAction {
    override def ExecuteAction(gameState: GameState): GameState = {
      if (player == gameState.friendlyPlayer.playerNumber) {
        val playerWithNewHand = gameState.friendlyPlayer.RemoveCard(gameState.GetCardByID(id))
        val newPlayer = playerWithNewHand.AddCard(gameState.GetCardByID(id), true)
        new GameState(newPlayer, gameState.enemyPlayer)
      }
      else {
        val playerWithNewHand = gameState.enemyPlayer.RemoveCard(gameState.GetCardByID(id))
        val newPlayer = playerWithNewHand.AddCard(gameState.GetCardByID(id), true)
        new GameState(gameState.friendlyPlayer, newPlayer)
      }
    }
  }
}

case class CardPlayed(name: String, id: Int, dstPos: Int, player: Int) extends HSAction {
  override def ExecuteAction(gameState: GameState): GameState = {
    if (player == gameState.friendlyPlayer.playerNumber) {
      val playerWithNewHand = gameState.friendlyPlayer.RemoveCard(gameState.GetCardByID(id))
      val newPlayer = playerWithNewHand.AddCard(gameState.GetCardByID(id), false)
      new GameState(newPlayer, gameState.enemyPlayer)
    }
    else {
      val playerWithNewHand = gameState.enemyPlayer.RemoveCard(gameState.GetCardByID(id))
      val newPlayer = playerWithNewHand.AddCard(gameState.GetCardByID(id), false)
      new GameState(gameState.friendlyPlayer, newPlayer)
    }
}


// I don't think I should ever be using these for a HSAction. Add/Remove card should handle position shifts.
//  case class HAND_POSITION_CHANGE(id:Int, pos:Int, player:Int, dstPos:Int) extends HSAction
//
//  case class BOARD_POSITION_CHANGE(id:Int, player:Int, dstPos:Int) extends HSAction

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


  }
}

case class Hex(name: String, id: Int, player: Int, zonePos: Int) extends HSAction


case class HSActionError() extends HSAction


}