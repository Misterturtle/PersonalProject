package tph

import com.typesafe.scalalogging.LazyLogging

import scala.Option

/**
  * Created by Harambe on 2/20/2017.
  */
case class Player(playerNumber: Int = Constants.INT_UNINIT, weaponValue: Int = 0, hand: List[Card] = List[Card](), board: List[Card] = List[Card]()) extends LazyLogging {

  def AddCard(card: Card, isHand: Boolean): Player = {
    if (isHand && board.contains(card))
      println("Safety check to make sure the added card is not in both the hand and board")


    if (isHand) {

      val shiftedHand = hand.foldLeft(hand) {
        (changingHand, oldCard) =>
          val equalTo = oldCard.handPosition == card.handPosition
          val greaterThan = oldCard.handPosition > card.handPosition
          val lessThan = oldCard.handPosition < card.handPosition

          true match {
            case `equalTo` | `greaterThan` =>
              (changingHand diff List(oldCard)) ::: List(oldCard.copy(handPosition = oldCard.handPosition +1))
            case `lessThan` =>
              changingHand
          }
      }

      this.copy(hand = shiftedHand ::: List(card))
    }
    else
    {
      val shiftedBoard = board.foldLeft(board) {
        (changingBoard, oldCard) =>
          val equalTo = oldCard.boardPosition == card.boardPosition
          val greaterThan = oldCard.boardPosition > card.boardPosition
          val lessThan = oldCard.boardPosition < card.boardPosition

          true match {
            case `equalTo` | `greaterThan` =>
              (changingBoard diff List(oldCard)) ::: List(oldCard.copy(boardPosition = oldCard.boardPosition +1))

            case `lessThan` =>
              changingBoard
          }
      }
      this.copy(board = shiftedBoard ::: List(card))
    }
  }


  def RemoveCard(card: Card): Player = {

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
              (changingHand diff List(oldCard)) ::: List(oldCard.copy(handPosition = oldCard.handPosition-1))

            case `lessThan` =>
              changingHand
          }
      }
        this.copy(hand = shiftedHand)
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
                (changingBoard diff List(oldCard)) ::: List(oldCard.copy(boardPosition = oldCard.boardPosition-1))

              case `lessThan` =>
                changingBoard
            }
        }
        this.copy(board = shiftedBoard)
      }
  }

  def AddCardToNextHandPosition(name:String, id:Int, cardID:String): Player ={
    val newHand = hand ::: List(new Card(name, id, GetNextHandPosition(), Constants.INT_UNINIT, playerNumber, cardID))
    this.copy(hand = newHand)}

  def GetNextHandPosition(): Int ={
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
