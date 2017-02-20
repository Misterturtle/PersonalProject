package tph

import com.typesafe.scalalogging.LazyLogging
import tph.Constants.FunctionalConstants._
import tph.NoCards

/**
  * Created by Harambe on 2/20/2017.
  */
case class Player(playerNumber: Int, hand: List[HSCard] = List(NoCards()), board: List[HSCard] = List(NoCards())) extends LazyLogging {

  def AddCard(card: HSCard, isHand: Boolean): Player = {
    if (isHand && board.contains(card))
      println("Safety check to make sure the added card is not in both the hand and board")


    def ShiftCardRight(modifiedList: List[HSCard]): List[HSCard] = {

      def GetNextCard(): HSCard = {
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

      val tempCard = GetNextCard()
      modifiedList ::: List(new Card(tempCard.name, tempCard.id, tempCard.handPosition + Constants.booleanToIntMap(isHand), tempCard.boardPosition + Constants.booleanToIntMap(!isHand), tempCard.player))
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


  def RemoveCard(card: HSCard): Player = {

    val isHand = hand.contains(card)

    //Just a safety check for a scenario that should never happen
    if (isHand && board.contains(card))
      logger.debug("BUG: Card: " + card + " is found in both the hand and board of Player:" + this)


    def ShiftCardLeft(modifiedList: List[HSCard]): List[HSCard] = {

      def GetNextCard(): HSCard = {
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

      val tempCard = GetNextCard()
      modifiedList diff List(tempCard) ::: List(new Card(tempCard.name, tempCard.id, tempCard.handPosition - Constants.booleanToIntMap(isHand), tempCard.boardPosition - Constants.booleanToIntMap(!isHand), tempCard.player))
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
