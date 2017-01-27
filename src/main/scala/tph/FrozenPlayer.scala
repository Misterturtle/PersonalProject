package tph

/**
  * Created by Harambe on 1/24/2017.
  */
class FrozenPlayer(player: Player) {


  val hand = new Array[Card](player.hand.size)
  val board = new Array[Card](player.board.size)

  for (a <- 0 until player.hand.size) {
    val newCard = new Card()
    newCard.boardPosition = player.hand(a).boardPosition
    newCard.handPosition = player.hand(a).handPosition
    newCard.id = player.hand(a).id
    newCard.name = player.hand(a).name
    newCard.zone = player.hand(a).zone

    hand(a) = newCard
  }

  for (a <- 0 until player.board.size) {
    val newCard = new Card()
    newCard.boardPosition = player.board(a).boardPosition
    newCard.handPosition = player.board(a).handPosition
    newCard.id = player.board(a).id
    newCard.name = player.board(a).name
    newCard.zone = player.board(a).zone

    board(a) = newCard
  }


}
