package tph

import com.typesafe.scalalogging.LazyLogging

/**
  * Created by Harambe on 2/20/2017.
  */
case class GameState(firstPlayer: Player = new Player(1, List[HSCard](), List[HSCard]()), secondPlayer: Player = new Player(2, List[HSCard](), List[HSCard]())) extends LazyLogging {

  val friendlyPlayer = firstPlayer
  val enemyPlayer = secondPlayer

    def GetCardByID(cardID: Int): HSCard = {

      (friendlyPlayer.hand ::: friendlyPlayer.board ::: enemyPlayer.hand ::: enemyPlayer.board).find(_.id == cardID) match {
        case Some(card) => card
        case None =>
          logger.debug("Not able to find card with id " + cardID)
          NoCards()
      }
    }

  def SetPlayerNumbers(friendlyPlayerNumber:Int): GameState = {
    val enemyPlayerNumber =
      friendlyPlayerNumber match {
        case 1 => 2
        case 2 => 1
        case _ => -5
      }

    new GameState(new Player(friendlyPlayerNumber, friendlyPlayer.hand, friendlyPlayer.board), new Player(enemyPlayerNumber, enemyPlayer.hand,enemyPlayer.board))
  }


  //  def GetCardAddress(card: Card): CardAddress = {
  //
  //    CardAddress(card.player, FindList(card)._1, FindList(card)._2, FindIndex(card))
  //
  //    def FindList(card: Card): (Boolean, List[Card]) = {
  //      if (GetFriendlyHand().contains(card))
  //        return (true, GetFriendlyHand())
  //
  //      if (GetFriendlyBoard().contains(card))
  //        return (false, GetFriendlyBoard())
  //
  //      if (GetEnemyBoard().contains(card))
  //        return (true, GetEnemyHand())
  //
  //      if (GetEnemyHand().contains(card))
  //        return (false, GetEnemyBoard())
  //
  //      return (false, List(NoCards()))
  //    }
  //  }
}
