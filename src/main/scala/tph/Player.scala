package tph

import com.typesafe.scalalogging.LazyLogging
import tph.Constants.FunctionalConstants._

/**
  * Created by Harambe on 2/20/2017.
  */
case class Player(playerNumber: Int, hand: List[HSCard] = List[HSCard](), board: List[HSCard] = List[HSCard]()) extends LazyLogging {

  def AddCard(card: HSCard, isHand: Boolean): Player = {
    if (isHand && board.contains(card))
      println("Safety check to make sure the added card is not in both the hand and board")


    if (isHand) {
      val shiftedHand = hand.foldLeft(List[HSCard]()) { (newHand, oldCard) =>
          if(oldCard.handPosition >= card.handPosition){
            val removedCardList = newHand diff List(oldCard)
            removedCardList ::: List(new Card(oldCard.name, oldCard.id, oldCard.handPosition +1, oldCard.boardPosition, playerNumber))
          }
        newHand
      }
        val newHand = shiftedHand ::: List(new Card(card.name, card.id, card.handPosition, card.boardPosition,card.player))
        new Player(playerNumber, newHand, board)
    }
    else {
        val shiftedBoard = board.foldLeft(List[HSCard]()) { (newBoard, oldCard) =>

          if(oldCard.boardPosition >= card.handPosition){
            val removedCardList = newBoard diff List(oldCard)
            removedCardList ::: List(new Card(oldCard.name, oldCard.id, oldCard.handPosition, oldCard.boardPosition +1, playerNumber))
          }
          newBoard
        }
        val newBoard = shiftedBoard ::: List(new Card(card.name, card.id, card.handPosition, card.boardPosition,card.player))

      new Player(playerNumber, hand, newBoard)
    }
  }


  def RemoveCard(card: HSCard): Player = {

    val isHand = hand.contains(card)

    //Just a safety check for a scenario that should never happen
    if (isHand && board.contains(card))
      logger.debug("BUG: Card: " + card + " is found in both the hand and board of Player:" + this)


    if (isHand) {

      val shiftedHand = hand.foldLeft(hand) {
        (changingHand, oldCard) =>
          val equalTo = oldCard == card
          val greaterThan = oldCard.handPosition > card.handPosition
          val lessThan = oldCard.handPosition < card.handPosition

          true match {
            case `equalTo` =>
              changingHand diff List(oldCard)

            case `greaterThan` =>
              (changingHand diff List(oldCard)) ::: List(new Card(oldCard.name, oldCard.id, oldCard.handPosition -1, oldCard.boardPosition, oldCard.player))

            case `lessThan` =>
              changingHand
          }
      }

        new Player(playerNumber, shiftedHand, board)
      }
    else
      {
        val shiftedBoard = board.foldLeft(board) {
          (changingBoard, oldCard) =>
            val equalTo = oldCard == card
            val greaterThan = oldCard.boardPosition > card.boardPosition
            val lessThan = oldCard.boardPosition < card.boardPosition

            true match {
              case `equalTo` =>
                changingBoard diff List(oldCard)

              case `greaterThan` =>
                (changingBoard diff List(oldCard)) ::: List(new Card(oldCard.name, oldCard.id, oldCard.handPosition, oldCard.boardPosition -1, oldCard.player))

              case `lessThan` =>
                changingBoard
            }
        }

        new Player(playerNumber, hand, shiftedBoard)
      }
  }
}
