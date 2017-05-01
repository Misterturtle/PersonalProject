package GameState

import FileReaders.CardInfo
import com.typesafe.scalalogging.LazyLogging
import tph.{Card, Constants}

/**
  * Created by Harambe on 2/20/2017.
  */
case class Player(playerNumber: Int = Constants.INT_UNINIT, hand: List[Card] = Nil, board: List[Card] = Nil, hero:Option[Card] = None, heroPower:Option[Card] = None, secretsInPlay:Int = 0, isWeaponEquipped:Boolean = false, isComboActive:Boolean = false, manaAvailable: Int = Constants.INT_UNINIT, elementalPlayedThisTurn:Boolean = false, elementalPlayedLastTurn:Boolean = false) extends LazyLogging {

  def addCard(card: Card, isHand: Boolean): Player = {
    if (isHand && board.contains(card))
      println("Safety check to make sure the added card is not in both the hand and board")


    if (isHand) {

      val shiftedHand = hand.foldLeft(hand) {
        (changingHand, oldCard) =>
          val equalTo = oldCard.handPosition == card.handPosition
          val greaterThan = oldCard.handPosition > card.handPosition

          if(equalTo || greaterThan)
            (changingHand diff List(oldCard)) ::: List(oldCard.copy(handPosition = oldCard.handPosition +1))
          else
          changingHand
      }

      this.copy(hand = shiftedHand ::: List(card))
    }
    else
    {
      val shiftedBoard = board.foldLeft(board) {
        (changingBoard, oldCard) =>
          val equalTo = oldCard.boardPosition == card.boardPosition
          val greaterThan = oldCard.boardPosition > card.boardPosition

          if(equalTo || greaterThan)
            (changingBoard diff List(oldCard)) ::: List(oldCard.copy(boardPosition = oldCard.boardPosition +1))
          else
            changingBoard


      }
      this.copy(board = shiftedBoard ::: List(card))
    }
  }


  def removeCard(card: Card): Player = {

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

          if(equalTo)
            changingHand diff List(oldCard)
          else if (greaterThan)
            (changingHand diff List(oldCard)) ::: List(oldCard.copy(handPosition = oldCard.handPosition-1))
          else
          changingHand
      }
        this.copy(hand = shiftedHand)
      }
    else
      {
        val shiftedBoard = board.foldLeft(board) {
          (changingBoard, oldCard) =>
            val equalTo = oldCard == card
            val greaterThan = oldCard.boardPosition > card.boardPosition

            if(equalTo)
              changingBoard diff List(oldCard)
            else if (greaterThan)
              (changingBoard diff List(oldCard)) ::: List(oldCard.copy(boardPosition = oldCard.boardPosition-1))
            else
            changingBoard

        }
        this.copy(board = shiftedBoard)
      }
  }

  def addCardToNextHandPosition(name:String, id:Int, cardID:String, cardInfo:CardInfo): Player ={
    val newHand = hand ::: List(new Card(name, id, getNextHandPosition(), Constants.INT_UNINIT, playerNumber, cardID, cardInfo = cardInfo))
    this.copy(hand = newHand)}

  def getNextHandPosition(): Int ={
    for(a<-1 to 20){
      if(hand.exists(x => x.handPosition == a)){
      }
      else{
        return a
      }
    }
    Constants.INT_UNINIT
  }

}
