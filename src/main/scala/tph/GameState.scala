package tph

import com.typesafe.scalalogging.LazyLogging

/**
  * Created by Harambe on 2/20/2017.
  */
case class GameState(firstPlayer: Player = new Player(1, 0), secondPlayer: Player = new Player(2, 0)) extends LazyLogging {

  val friendlyPlayer = firstPlayer
  val enemyPlayer = secondPlayer

    def GetCardByID(cardID: Int): HSCard = {

      (friendlyPlayer.hand ::: friendlyPlayer.board ::: enemyPlayer.hand ::: enemyPlayer.board).find(_.id == cardID) match {
        case Some(card) => card
        case None => NoCards()
      }
    }

  def SetPlayerNumbers(friendlyPlayerNumber:Int): GameState = {
    val enemyPlayerNumber =
      friendlyPlayerNumber match {
        case 1 => 2
        case 2 => 1
        case _ => Constants.INT_UNINIT
      }

    new GameState(new Player(friendlyPlayerNumber, friendlyPlayer.weaponValue,  hand = friendlyPlayer.hand, board = friendlyPlayer.board), new Player(enemyPlayerNumber, enemyPlayer.weaponValue, hand = enemyPlayer.hand, board = enemyPlayer.board))
  }
}
