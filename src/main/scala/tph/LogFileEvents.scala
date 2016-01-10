package tph

/**
 * Created by RC on 1/7/2016.
 */
object LogFileEvents {

  case class TurnStartEvent(turn: Int)

  case class FriendlyCardDrawnEvent(name: String, id: Int, position: Int)

  case class EnemyCardDrawnEvent(id: Int, position: Int)

  case class BoardChangeEvent(name: String, id:Int, zonePos:Int, player: Int, dstPos: Int)

  case class FriendlyDefinedEvent(name:String)

  case class EnemyDefinedEvent(name:String)

  case class PlaysFirstEvent(firstPlayerName: String)

  case class GameOver()

}
