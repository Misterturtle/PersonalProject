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

  val player1 = firstPlayer
  val player2 = secondPlayer

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

  def AddCard(card: Card, isHand: Boolean): List[Card] = {
    if (isHand && board.contains(card))
      println("Safety check to make sure the added card is not in both the hand and board")

    if (isHand) {
      UnwrapMultiSome(RepeatFunction(ShiftCardRight, hand, card.handPosition, hand.size)) match {
        case Some(newHand: List[Card]) =>
          newHand
        case None =>
          println("Possible bug in AddCard() in Player. Returning empty hand list")
          List(NoCards())
      }
    }
    else
      UnwrapMultiSome(RepeatFunction(ShiftCardRight, board, card.boardPosition, board.size)) match {
        case Some(newBoard: List[Card]) =>
          newBoard
        case None =>
          println("Possible bug in AddCard() in Player. Returning empty hand list")
          List(NoCards())
      }


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
  }


  def RemoveCard(card: Card): List[Card] = {

    val isHand = hand.contains(card)

    //Just a safety check for a scenario that should never happen
    if (isHand && board.contains(card))
      logger.debug("BUG: Card: " + card + " is found in both the hand and board of Player:" + this)

    if (isHand) {
      UnwrapMultiSome(RepeatFunction(ShiftCardLeft, hand, card.handPosition, hand.size)) match {
        case Some(newHand: List[Card]) =>
          newHand
        case None =>
          println("Possible bug in RemoveCard() in Player. Returning empty hand list")
          List(NoCards())
      }
    }
    else {
      UnwrapMultiSome(RepeatFunction(ShiftCardLeft, board diff List(card), card.boardPosition, board.size)) match {
        case Some(newHand: List[Card]) =>
          newHand
        case None =>
      }
    }

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
  }
}

case class Card(name: String, id: Int, handPosition: Int, boardPosition: Int, player: Int)

case class NoCards() extends Card("", -5, -5, -5, -5) {
  println("NoCards case class ran.")

}

val card1 = new Card ("Card1", 1, 1, - 5, 1)
val card2 = new Card ("Card2", 2, 2, - 5, 1)
val card3 = new Card ("Card3", 3, 3, - 5, 1)
val card4 = new Card ("Card4", 4, 4, - 5, 1)
val card5 = new Card ("Card5", 5, 5, - 5, 1)
val card6 = new Card ("Card6", 6, 6, - 5, 1)
val card7 = new Card ("Card7", 7, 7, - 5, 1)

val testPlayer = new Player ()


case class CardAddress(playerNumber: Int, isHand: Boolean, list: List[Card], index: Int)