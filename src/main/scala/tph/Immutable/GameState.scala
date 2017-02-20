package tph.Immutable

import java.io.BufferedReader
import java.util

import com.typesafe.scalalogging.LazyLogging
import tph.Constants
import tph.Immutable.HSAction.{CardDrawn, HSAction}

import scala.util.matching.Regex
import Constants.FunctionalConstants._


/**
  * Created by Harambe on 2/14/2017.
  */


class GameState(firstPlayer: Player, secondPlayer: Player) extends LazyLogging {

  val friendlyPlayer = firstPlayer
  val enemyPlayer = secondPlayer

  def GetFriendlyHand(): List[Card] = {
    new LogParser().ConstructFriendlyHand()
  }

  def GetEnemyHand(): List[Card] = {
    new LogParser().ParseEnemyHand()
  }

  def GetFriendlyBoard(): List[Card] = {
    new LogParser().ParseFriendlyBoard
  }

  def GetEnemyBoard(): List[Card] = {
    new LogParser().ParseEnemyBoard()
  }


  def GetPlayerNumbers(): (Int, Int) = {
    new LogParser().GetPlayerNumb `` ers()
  }

  def GetAllCards(): List[Card] = {
    GetFriendlyHand ++ GetEnemyHand ++ GetFriendlyBoard ++ GetEnemyBoard
  }

  def GetCardByID(cardID: Int): Card = {

    (player1.hand ++ player1.board ++ player2.hand ++ player2.board).find(_ == cardID) match {
      case Some(card) => card
      case None =>
        logger.debug("Not able to find card with id " + cardID)
        NoCards()
    }
  }

  def GetCardAddress(card: Card): CardAddress = {

    CardAddress(card.player, FindList(card)._1, FindList(card)._2, FindIndex(card))

    def FindList(card: Card): (Boolean, List[Card]) = {
      if (GetFriendlyHand().contains(card))
        return (true, GetFriendlyHand())

      if (GetFriendlyBoard().contains(card))
        return (false, GetFriendlyBoard())

      if (GetEnemyBoard().contains(card))
        return (true, GetEnemyHand())

      if (GetEnemyHand().contains(card))
        return (false, GetEnemyBoard())

      return (false, List(NoCards()))
    }


    def FindIndex(card: Card): Int = {
      val index = FindList(card)._2.indexWhere(_.id == card.id)
      if (index != -1)
        return index
      else
        return Constants.INT_UNINIT
    }
  }
}


case class Player(playerNumber: Int, hand: List[Card], board: List[Card]) extends LazyLogging {

  def AddCard(card: Card, isHand: Boolean): Player = {
    if (isHand && board.contains(card))
      println("Safety check to make sure the added card is not in both the hand and board")


    def ShiftCardRight(modifiedList: List[Card]): List[Card] = {
      val tempCard = GetNextCard()
      modifiedList ::: List(new Card(tempCard.name, tempCard.id, tempCard.handPosition + Constants.booleanToIntMap(isHand), tempCard.boardPosition + Constants.booleanToIntMap(!isHand), tempCard.player))


      def GetNextCard(): Card = {
        if (isHand) {
          modifiedList foreach { f =>
            if (hand.contains(f) && f.handPosition >= card.handPosition) {
              return f
            }
          }
          NoCards()
        }
        else {
          modifiedList foreach { f =>
            if (board.contains(f) && f.boardPosition >= card.boardPosition) {
              return f
            }
          }
          NoCards()
        }
      }
    }

    if (isHand) {
      val newHand = RepeatFunction(ShiftCardRight, hand, (hand.size - card.handPosition) + 1)
      new Player(playerNumber, newHand, board)
    }
    else {
      val newBoard = RepeatFunction(ShiftCardRight, board, (board.size - card.boardPosition) + 1)
      new Player(playerNumber, hand, newBoard)
    }
  }


  def RemoveCard(card: Card): Player = {

    val isHand = hand.contains(card)

    //Just a safety check for a scenario that should never happen
    if (isHand && board.contains(card))
      logger.debug("BUG: Card: " + card + " is found in both the hand and board of Player:" + this)


    def ShiftCardLeft(modifiedList: List[Card]): List[Card] = {
      val tempCard = GetNextCard()
      modifiedList diff List(tempCard) ::: List(new Card(tempCard.name, tempCard.id, tempCard.handPosition - Constants.booleanToIntMap(isHand), tempCard.boardPosition - Constants.booleanToIntMap(!isHand), tempCard.player))


      def GetNextCard(): Card = {
        if (isHand) {
          modifiedList foreach { f =>
            if (hand.contains(f) && f.handPosition > card.handPosition) {
              return f
            }
          }
          NoCards()
        }
        else {
          modifiedList foreach { f =>
            if (board.contains(f) && f.boardPosition > card.boardPosition) {
              return f
            }
          }
          NoCards()
        }
      }
    }

    if (isHand) {
      //1,2,(3),4,5
      val removedCardHand = hand diff List(card)
      val newHand = RepeatFunction(ShiftCardLeft, removedCardHand, hand.size - card.handPosition)
      new Player(playerNumber, newHand, board)
    }
    else {
      val removedCardBoard = board diff List(card)
      val newBoard = RepeatFunction(ShiftCardLeft, removedCardBoard, board.size - card.boardPosition)
      new Player(playerNumber, hand, newBoard)
    }
  }
}


case class Card(name: String, id: Int, handPosition: Int, boardPosition: Int, player: Int)

case class NoCards() extends Card("", -5, -5, -5, -5) {
  println("NoCards case class ran.")
}

case class CardAddress(playerNumber: Int, isHand: Boolean, list: List[Card], index: Int)