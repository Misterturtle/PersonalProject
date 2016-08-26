package tph

object LogFileEvents {

  case class TurnStartEvent(turn: Int)

  //Friendly Events
  //case class FriendlyCardReturnEvent(name: String, id: Int, player:Int)
  case class FriendlyMinionControlled(name:String, id:Int)


  //Enemy Events
  case class EnemyCardDrawnEvent(id: Int, position: Int,player:Int)
  //case class EnemyCardReturnEvent(name:String, id: Int,player:Int)



  //Neutral Events
  case class DiscoverOption(option: Int)

  case class FaceAttackValueEvent(player: Int, value: Int)

  case class WeaponPlayedEvent(id: Int, player: Int)
  case class SecretPlayedEvent(id:Int, player:Int)
  case class OldZoneChangeEvent(id:Int, zone:String, player:Int, dstZone:String)
  case class ZoneChangeEvent(id:Int, player:Int, zone:String, dstZone:String)
  case class KnownCardDrawn(name: String, id:Int, position:Int, player:Int)
  case class Sap(name: String, id:Int, player:Int)
  case class CardPlayed(name: String, id:Int, dstPos:Int, player:Int)
  case class DefinePlayers(friendlyPlayerID: Int)
  case class CardDeath(name:String, id:Int, zonePos:Int, player:Int)
  case class PlaysFirstEvent(firstPlayerName: String)
  case class BoardChangeEvent(name: String, id: Int, zonePos: Int, player: Int, dstPos: Int)
  case class GameOver()
  case class MinionSummoned(name:String, id:Int, zonePos:Int, player: Int)
  case class BoardPositionChange(id:Int, player:Int, dstPos:Int)
  case class HandPositionChange(id:Int, pos:Int, player:Int, dstPos:Int)

  //case class Polymorph(newId:Int,oldId:Int ,player:Int)
  case class Transform(oldId: Int, newId: Int)
  case class Hex(name:String, id:Int, player:Int, zonePos:Int)



  //Logging Events
  case class TagChange(entity: String, tag: String, value: String)
  case class NumOptions(source: String, entity: String, value: String)
  case class DebugPrintPower(source:String, pad:String, text:String)
  case class PrintState(fileName:String)
}
