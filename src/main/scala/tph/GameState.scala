package tph

import FileReaders.{CardInfo, HSDataBase}
import com.typesafe.scalalogging.LazyLogging
import net.liftweb.json.JsonAST.JObject

/**
  * Created by Harambe on 2/20/2017.
  */
class GameState() extends LazyLogging {

  var friendlyPlayer = new Player()
  var enemyPlayer = new Player()
  val dataBase = new HSDataBase()

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

  def getCardInfo(cardID:String): CardInfo = {
    if(cardID != Constants.STRING_UNINIT)
    dataBase.cardIDMap(cardID)
    else
      new CardInfo(Some(Constants.STRING_UNINIT), Some(Constants.STRING_UNINIT), Some(Constants.INT_UNINIT), Some(Nil), Some(Constants.INT_UNINIT), Some(Constants.STRING_UNINIT), Some(new JObject(Nil)))
    }


}
