package tph

object LogFileEvents {

  case class TurnStartEvent(turn: Int)

  //Friendly Events
  case class FriendlyCardDrawnEvent(name: String, id: Int, position: Int, player:Int)
  case class FriendlyPlaysCardEvent(name:String, id:Int, srcZone:String, srcPos:Int, dstZone:String, dstPos:Int)
  case class FriendlyHandChangeEvent(name: String, id: Int, zonePos: Int, player: Int, dstPos: Int)
  case class FriendlyZoneChangeEvent(name:String, id:Int, zone:String, player:Int, dstZone:String)
  case class FriendlyCardReturnEvent(name: String, id: Int,zonePos: Int, player:Int, dstPos: Int)
  case class FriendlyMinionControlled(name:String, id:Int)


  //Enemy Events
  case class EnemyCardDrawnEvent(id: Int, position: Int,player:Int)
  case class EnemyPlaysCardEvent(name: String, id: Int, dstPos: Int,player:Int)
  case class EnemyHandChangeEvent(id: Int, zonePos: Int, player: Int, dstPos: Int)
  case class EnemyCardReturnEvent(name:String, id: Int, zone: String, zonePos: Int,player:Int)



  //Neutral Events
  case class PlayerDefinedEvent(name:String, player:Int)
  case class CardDeath(name:String, id:Int, zone:String, zonePos:Int, player:Int)
  case class PlaysFirstEvent(firstPlayerName: String)
  case class BoardChangeEvent(name: String, id: Int, zonePos: Int, player: Int, dstPos: Int)
  case class GameOver()
  case class MinionSummoned(name:String, id:Int, zonePos:Int, player: Int)
  case class PositionChange(id:Int, player:Int, dstPos:Int)
  case class Polymorph(newId:Int,name:String,id:Int ,zonePos:Int ,player:Int)


  //Logging Events
  case class TagChange(entity: String, tag: String, value: String)
  case class NumOptions(source: String, entity: String, value: String)
  case class DebugPrintPower(source:String, pad:String, text:String)

}
