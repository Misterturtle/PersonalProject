package tph

import com.typesafe.scalalogging.LazyLogging

/**
  * Created by Harambe on 2/20/2017.
  */
class GameState() extends LazyLogging {

  var friendlyPlayer = new Player()
  var enemyPlayer = new Player()

  def getCardByID(cardID: Int): Option[Card] = {

    (friendlyPlayer.hand ::: friendlyPlayer.board ::: enemyPlayer.hand ::: enemyPlayer.board).find(_.id == cardID)
  }

  def setPlayerNumbers(friendlyPlayerNumber: Int): Unit = {
    val enemyPlayerNumber =
      friendlyPlayerNumber match {
        case 1 => 2
        case 2 => 1
        case _ => Constants.INT_UNINIT
      }
    friendlyPlayer = friendlyPlayer.copy(playerNumber = friendlyPlayerNumber)
    enemyPlayer = enemyPlayer.copy(playerNumber = enemyPlayerNumber)
  }

  def gameOver(): Unit = {
    friendlyPlayer = new Player()
    enemyPlayer = new Player()
  }


}
