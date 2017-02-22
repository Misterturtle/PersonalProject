package tph

import com.typesafe.scalalogging.LazyLogging

/**
  * Created by Harambe on 2/20/2017.
  */
case class GameState(firstPlayer: Player = new Player(1, List[HSCard](), List[HSCard]()), secondPlayer: Player = new Player(2, List[HSCard](), List[HSCard]())) extends LazyLogging {

  val friendlyPlayer = firstPlayer
  val enemyPlayer = secondPlayer

  //  def GetFriendlyHand(): List[Card] = {
  //    new LogParser().ConstructFriendlyHand()
  //  }
  //
  //  def GetEnemyHand(): List[Card] = {
  //    new LogParser().ParseEnemyHand()
  //  }
  //
  //  def GetFriendlyBoard(): List[Card] = {
  //    new LogParser().ParseFriendlyBoard
  //  }
  //
  //  def GetEnemyBoard(): List[Card] = {
  //    new LogParser().ParseEnemyBoard()
  //  }

  //  def GetAllCards(): List[Card] = {
  //    GetFriendlyHand ++ GetEnemyHand ++ GetFriendlyBoard ++ GetEnemyBoard
  //  }

    def GetCardByID(cardID: Int): HSCard = {

      (friendlyPlayer.hand ::: friendlyPlayer.board ::: enemyPlayer.hand ::: enemyPlayer.board).find(_.id == cardID) match {
        case Some(card) => card
        case None =>
          logger.debug("Not able to find card with id " + cardID)
          NoCards()
      }
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
  //
  //
  //    def FindIndex(card: Card): Int = {
  //      val index = FindList(card)._2.indexWhere(_.id == card.id)
  //      if (index != -1)
  //        return index
  //      else
  //        return Constants.INT_UNINIT
  //    }
  //  }
}
