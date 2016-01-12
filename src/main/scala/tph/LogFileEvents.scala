package tph

object LogFileEvents {

  case class TurnStartEvent(turn: Int)

  case class FriendlyCardDrawnEvent(name: String, id: Int, position: Int)

  case class EnemyCardDrawnEvent(id: Int, position: Int)

  case class FriendlyHandChangeEvent(name:String, id:Int, zonePos:Int, player:Int, dstPos:Int)

  case class EnemyHandChangeEvent(id:Int, zonePos:Int, player:Int, dstPos:Int)

  case class EnemyPlaysCardEvent(id:Int, zonePos:Int, player:Int)

  case class EnemyCardReturnEvent(id:Int, zone:String, zonePos:Int, dstZone:String)

  case class FriendlyZoneChangeEvent(name:String, id:Int, zone:String, zonePos:Int, dstZone:String)

  case class BoardChangeEvent(name: String, id:Int, zonePos:Int, player: Int, dstPos: Int)

  case class FriendlyDefinedEvent(name:String)

  case class EnemyDefinedEvent(name:String)

  case class PlaysFirstEvent(firstPlayerName: String)

  case class GameOver()

}
