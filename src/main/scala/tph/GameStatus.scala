package tph

import java.io.{FileReader, BufferedReader, File}

import scala.concurrent.duration._
import akka.actor.{ActorRef, Props, ActorSystem, Actor}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.matching.Regex
import tph.LogFileEvents._
import scala.collection.mutable._


class GameStatus(system: ActorSystem) extends Actor with akka.actor.ActorLogging {


  var turn: Int = 0
  val me: Player = Player("UNKNOWN")
  val him: Player = Player("UNKNOWN")


  def receive = {

    case "poll" =>
      DisplayStatus()
      system.scheduler.scheduleOnce(10000.millis, this.self, "poll")


    //Friendly Cases
    case FriendlyDefinedEvent(name: String) => {
      //"""^\s*TAG_CHANGE Entity=(.+) tag=(.+) value=(.+)$""".r
      me.name = name
    }
    case FriendlyCardDrawnEvent(name: String, id: Int, position: Int) => {
      //"""^.+id=\d+ local=False \[name=(.+) id=(\d+) zone=HAND zonePos=(\d+) cardId=\S+ player=1\] pos from 0 -> \d+""".r
      var drawnCard: Card = Card()
      drawnCard.name = name
      drawnCard.id = id
      drawnCard.zone = "HAND"
      drawnCard.handPosition = position
      me.hand.append(drawnCard)
    }
    case FriendlyHandChangeEvent(name, id, zonePos, player, dstPos) if player == 1 => {
      //"""^\[Power\] PowerTaskList.+TAG_CHANGE Entity=\[name=(.+) id=(\d+) zone=HAND zonePos=(\d+) cardId=.+ player=(\d+)\] tag=ZONE_POSITION value=(\d+)$""".r
      var index = -2

      me.hand.indexWhere(_.id == id) match {
        case x => index = x
      }

      if (index >= 0) {
        me.hand(index).handPosition = dstPos
      }
    }
    case FriendlyPlaysCardEvent(name, id, srcZone, srcPos, dstZone, dstPos) if dstZone == "PLAY" => {
      //"""^.+triggerEntity=\[name=(.+) id=(\d+) zone=.+ srcZone=(.+) srcPos=(\d+) dstZone=(.+) dstPos=(\d+)""".r
      var index = -2

      index = me.hand.indexWhere(_.id == id)
      if (index >= 0) {
        me.hand(index).zone = "PLAY"
        me.hand(index).handPosition = 0
        me.hand(index).boardPosition = dstPos
        me.board.append(me.hand(index))
        me.hand.remove(index)
      }
    }
    case FriendlyCardReturnEvent(name, id, zonePos, dstPos) if me.board.indexWhere(_.id == id) >= 0 => {
      //"""processing index=\d+ change=powerTask=\[power=\[type=TAG_CHANGE entity=\[id=\d+.+ entity=\[name=(.+) id=(\d+) zone=HAND zonePos=(\d+).+ player=1.+ dstPos=(\d+)""".r
      var index = -2

      index = me.board.indexWhere(_.id == id)
      if (index >= 0) {
        me.board(index).zone = "HAND"
        me.board(index).boardPosition = 0
        me.board(index).handPosition = dstPos
        me.hand.append(me.board(index))
        me.board.remove(index)
      }
    }
    case BoardChangeEvent(name: String, id: Int, zonePos: Int, player: Int, dstPos: Int) if player == 1 && dstPos != 0 => {
      // """^\[Power\] PowerTaskList.+TAG_CHANGE Entity=\[name=(.+) id=(\d+) zone=PLAY zonePos=(\d+) cardId=.+ player=(\d)\] tag=ZONE_POSITION value=(\d+)""".r
      var index = -2

      index = me.board.indexWhere(_.id == id)

      if (index >= 0) {
        me.board(index).boardPosition = dstPos
      }
    }
    case CardDeath(name:String, id:Int, zone:String, zonePos:Int, player:Int) if player == 1 && zone == "PLAY" => {
      //"""^\[Power\] PowerTaskList.+TAG_CHANGE Entity=\[name=(.+) id=(\d+) zone=(.+) zonePos=(\d+).+ player=(\d+).+ tag=ZONE value=GRAVEYARD""".r
      var index = -2
      index = me.board.indexWhere(_.id == id)
      if (index >= 0){
        me.board.remove(index)
      }
    }



    //Enemy Events
    case EnemyDefinedEvent(name: String) => {
      //"""^\s*TAG_CHANGE Entity=(.+) tag=(.+) value=(.+)$""".r
      him.name = name
    }
    case EnemyCardDrawnEvent(id: Int, position: Int) => {
      //"""^.+id=\d+ local=.+ \[id=(\d+) cardId=.+type=.+zone=HAND zonePos=(\d+) player=2\] pos from 0 -> \d+""".r
      var drawnCard: Card = Card()
      drawnCard.id = id
      drawnCard.zone = "HAND"
      drawnCard.handPosition = position
      him.hand.append(drawnCard)
    }
    case EnemyPlaysCardEvent(name, id, zonePos, dstPos) => {
      //"""^.+processing index=\d+ change=powerTask=\[power=\[type=TAG_CHANGE entity=\[id=\d+ cardId=.+
      // entity=\[name=(.+) id=(\d+) zone=PLAY zonePos=(\d+) cardId=.+ player=2\] .+ dstPos=(\d+)""".r
      var index = -2

      index = him.hand.indexWhere(_.id == id)
      if (index >= 0) {
        him.hand(index).zone = "PLAY"
        him.hand(index).name = name
        him.hand(index).boardPosition = dstPos
        him.hand(index).handPosition = 0
        him.board.append(him.hand(index))
        him.hand.remove(index)
      }
    }
    case EnemyHandChangeEvent(id, zonePos, player, dstPos) if player == 2 => {
      //"""^\[Power\] PowerTaskList.+TAG_CHANGE Entity=\[id=(\d+).+ zone=HAND zonePos=(\d+) player=(\d+)\] tag=ZONE_POSITION value=(\d+)""".r
      var index = -2

      index = him.hand.indexWhere(_.id == id)

      if (index >= 0) {
        him.hand(index).handPosition = dstPos
      }
    }
    //Needs tested
    case EnemyCardReturnEvent(name, id, zone, zonePos) => {
      //"""^\[Power\] PowerTaskList.+TAG_CHANGE Entity=\[name=.+ id=(\d+) zone=(.+) zonePos=(\d+).+ player=2\] tag=ZONE value=HAND""".r
      var index = -2

      index = him.board.indexWhere(_.id == id)
      if (index >= 0) {
        him.board(index).zone = "HAND"
        him.board(index).boardPosition = 0
        him.hand.append(him.board(index))
        him.board.remove(index)
      }
    }
    case BoardChangeEvent(name: String, id: Int, zonePos: Int, player: Int, dstPos: Int) if player == 2 && dstPos != 0 => {
      // """^\[Power\] PowerTaskList.+TAG_CHANGE Entity=\[name=(.+) id=(\d+) zone=PLAY zonePos=(\d+) cardId=.+ player=(\d)\] tag=ZONE_POSITION value=(\d+)""".r
      var index = -2

      him.board.indexWhere(_.id == id) match {
        case x => index = x
      }

      if (index >= 0) {
        him.board(index).boardPosition = dstPos
      }
    }
    case CardDeath(name:String, id:Int, zone:String, zonePos:Int, player:Int) if player == 2 && zone == "PLAY" => {
      //"""^\[Power\] PowerTaskList.+TAG_CHANGE Entity=\[name=(.+) id=(\d+) zone=(.+) zonePos=(\d+).+ player=(\d+).+ tag=ZONE value=GRAVEYARD""".r
      var index = -2
      index = him.board.indexWhere(_.id == id)
      if (index >= 0){
        him.board.remove(index)
      }
    }



    //Neutral Events
    case TurnStartEvent(value: Int) => {
      turn = value
    }
    case GameOver() => {
      Reset()
    }
    case x: String => {
      log.debug("GameStatus: " + x)
    }
    case _ =>{
      log.debug("GameStatus: DEFAULT case")
  }
}

  def Reset(): Unit = {
    me.hand.clear()
    me.board.clear()
    him.hand.clear()
    him.board.clear()
    turn = 0
    me.name = "UNKNOWN"
    him.name = "UNKNOWN"

  }

  def DisplayStatus(): Unit = {

    var a: Int = 0
    for (a <- 0 until me.hand.length) {
      log.info("My hand, card " + a + ": " + me.hand(a).name)
    }

    var b: Int = 0
    for (b <- 0 until me.board.length) {
      log.info("Friendly board position " + b + ": " + me.board(b).name)
    }

    log.info("\n\nHis hand has " + him.hand.length + " cards.\n")


    var c:Int = 0
    for (c <- 0 until him.board.length){
      log.info("Enemy board position " + b + ": " + him.board(c).name)
    }

    log.info("It is turn: " + turn)
  }
}

object Player {
  def apply(name: String) = new Player(name, new ListBuffer[Card], new ListBuffer[Card])
}


class Player(var name: String, handList: ListBuffer[Card], boardList: ListBuffer[Card]) {

  var hand: ListBuffer[Card] = handList
  var board: ListBuffer[Card] = boardList


}

case class Card() {
  var name: String = "UNKNOWN"
  var id: Int = 0
  var zone: String = "UNKNOWN"
  var handPosition = -1
  var boardPosition = -1


}


