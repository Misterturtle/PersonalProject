package tph.Immutable

import scala.util.matching.Regex

/**
  * Created by Harambe on 2/14/2017.
  */
object HSAction {


  trait HSAction {


  }


  case class CardDrawn(name: String, id: Int, position: Int, player: Int) extends HSAction

  case class CardDeath(name: String, id: Int, player: Int) extends HSAction

  case class KnownCardDrawn(name: String, id: Int, position: Int, player: Int) extends HSAction

  case class FriendlyMinionControlled(name: String, id, zonePos) extends HSAction

  //Enemy Events
  case class EnemyCardDrawn(id, position, player) extends HSAction

  case class ENEMY_MINION_CONTROLLED(name, id, zonePos) extends HSAction


  //Neutral Events

  case class DISCOVER_OPTION(option) extends HSAction

  case class FACE_ATTACK_VALUE(player, value) extends HSAction

  case class WEAPON(id, player) extends HSAction

  case class SECRET_PLAYED(id, player) extends HSAction

  case class OLD_ZONE_CHANGE(id, zone, player, dstZone) extends HSAction

  case class ZONE_CHANGE(id, player, zone, dstZone) extends HSAction


  case class KNOWN_CARD_DRAWN(name, id, position, player) extends HSAction

  case class SAP(name, id, player) extends HSAction

  case class DEFINE_PLAYERS(friendlyPlayerID) extends HSAction

  case class CARD_PLAYED(name, id, dstPos, player) extends HSAction


  case class HAND_POSITION_CHANGE(id, pos, player, dstPos) extends HSAction

  case class BOARD_POSITION_CHANGE(id, player, dstPos) extends HSAction

  case class CARD_DEATH(name, id, player) extends HSAction

  case class MINION_SUMMONED(name, id, zonePos, player) extends HSAction

  case class TRANSFORM(oldId, newId) extends HSAction

  case class HEX(name, id, player, zonePos) extends HSAction


  case class HSActionError() extends HSAction


}
