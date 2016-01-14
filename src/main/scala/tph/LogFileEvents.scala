package tph

object LogFileEvents {

  case class TurnStartEvent(turn: Int)

  //Friendly Events
  case class FriendlyDefinedEvent(name: String)
  case class FriendlyCardDrawnEvent(name: String, id: Int, position: Int)
  case class FriendlyPlaysCardEvent(name:String, id:Int, srcZone:String, srcPos:Int, dstZone:String, dstPos:Int)
  case class FriendlyHandChangeEvent(name: String, id: Int, zonePos: Int, player: Int, dstPos: Int)
  case class FriendlyCardReturnEvent(name: String, id: Int,zonePos: Int, dstPos: Int)


  //Enemy Events
  case class EnemyDefinedEvent(name: String)
  case class EnemyCardDrawnEvent(id: Int, position: Int)
  case class EnemyPlaysCardEvent(name: String, id: Int, zonePos: Int, dstPos:Int)
  case class EnemyHandChangeEvent(id: Int, zonePos: Int, player: Int, dstPos: Int)
  case class EnemyCardReturnEvent(name:String, id: Int, zone: String, zonePos: Int)



  //Neutral Events
  case class CardDeath(name:String, id:Int, zone:String, zonePos:Int, player:Int)
  case class PlaysFirstEvent(firstPlayerName: String)
  case class BoardChangeEvent(name: String, id: Int, zonePos: Int, player: Int, dstPos: Int)
  case class GameOver()


  //Logging Events
  case class TagChange(entity: String, tag: String, value: String)
  case class NumOptions(source: String, entity: String, value: String)
  case class DebugPrintPower(source:String, pad:String, text:String)

}
