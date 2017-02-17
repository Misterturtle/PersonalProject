package tph.Immutable

import tph.Immutable.HSAction.HSAction

/**
  * Created by Harambe on 2/15/2017.
  */
object GameManipulator {

  def AddCard(card: Card, cardAddress: CardAddress, gameState: GameState): GameState = {

    cardAddress.playerNumber match {
      case gameState.player1.playerNumber =>
        if (cardAddress.isHand) {
          new GameState(gameState.player1.AddCardToList(card, true, gameState.player1.hand), gameState.player2)
        }
        else
          new GameState(gameState.player1.AddCardToList(card, false, gameState.player1.board), gameState.player2)

      case gameState.player2.playerNumber =>
        if (cardAddress.isHand)
          new GameState(gameState.player1, gameState.player2.AddCardToList(card, true, gameState.player2.hand))
        else
          new GameState(gameState.player1, gameState.player2.AddCardToList(card, false, gameState.player2.board))
    }
  }


  def MoveCard(card: Card, newAddress: CardAddress, gameState: GameState): GameState = {

    RemoveCardByID(card.id)
    AddCard(card, newAddress, gameState)
  }


  def RemoveCard(card: Card, gameState: GameState): GameState = {
    gameState.


  }
}


class scala() {}