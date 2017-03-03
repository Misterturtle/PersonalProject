package tph

import com.typesafe.scalalogging.LazyLogging

import scala.Option

/**
  * Created by Harambe on 2/20/2017.
  */
case class Player(playerNumber: Int, weaponValue: Int, hand: List[HSCard] = List[HSCard](), board: List[HSCard] = List[HSCard]()) extends LazyLogging {

  def AddCard(card: HSCard, isHand: Boolean): Player = {
    if (isHand && board.contains(card))
      println("Safety check to make sure the added card is not in both the hand and board")


    if (isHand) {

      val shiftedHand = hand.foldLeft(hand) {
        (changingHand, oldCard) =>
          val equalTo = oldCard.handPosition == card.handPosition
          val greaterThan = oldCard.handPosition > card.handPosition
          val lessThan = oldCard.handPosition < card.handPosition

          true match {
            case `equalTo` =>
              (changingHand diff List(oldCard)) ::: List(new Card(oldCard.name, oldCard.id, oldCard.handPosition +1, oldCard.boardPosition, oldCard.player))
            case `greaterThan` =>
              (changingHand diff List(oldCard)) ::: List(new Card(oldCard.name, oldCard.id, oldCard.handPosition +1, oldCard.boardPosition, oldCard.player))
            case `lessThan` =>
              changingHand
          }
      }

      new Player(playerNumber, weaponValue, shiftedHand ::: List(card), board)
    }
    else
    {
      val shiftedBoard = board.foldLeft(board) {
        (changingBoard, oldCard) =>
          val equalTo = oldCard.boardPosition == card.boardPosition
          val greaterThan = oldCard.boardPosition > card.boardPosition
          val lessThan = oldCard.boardPosition < card.boardPosition

          true match {
            case `equalTo` =>
              (changingBoard diff List(oldCard)) ::: List(new Card(oldCard.name, oldCard.id, oldCard.handPosition, oldCard.boardPosition +1, oldCard.player))

            case `greaterThan` =>
              (changingBoard diff List(oldCard)) ::: List(new Card(oldCard.name, oldCard.id, oldCard.handPosition, oldCard.boardPosition +1, oldCard.player))

            case `lessThan` =>
              changingBoard
          }
      }
      new Player(playerNumber, weaponValue, hand, shiftedBoard ::: List(card))
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

        new Player(playerNumber, weaponValue, shiftedHand, board)
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

        new Player(playerNumber, weaponValue, hand, shiftedBoard)
      }
  }

  def AddCardToNextHandPosition(name:String, id:Int): Player ={
    val newHand = hand ::: List(new Card(name, id, GetNextHandPosition(), Constants.INT_UNINIT, playerNumber))
    new Player(playerNumber, weaponValue, newHand, board)}

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
