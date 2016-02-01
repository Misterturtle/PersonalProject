package tph

import java.io.{FileReader, BufferedReader, File}

import scala.concurrent.duration._
import akka.actor.{ActorRef, Props, ActorSystem, Actor}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.matching.Regex
import tph.LogFileEvents._
import scala.collection.mutable._


class GameStatus(system: ActorSystem) extends Actor with akka.actor.ActorLogging {

//His hand gets confused sometimes.


  var turn: Int = 0
  val me: Player = Player("UNKNOWN")
  val him: Player = Player("UNKNOWN")
  val USER_NAME: String = "Wizard"

  def receive = {


    //Friendly Cases

    case FriendlyCardDrawnEvent(name: String, id: Int, position: Int, player:Int) if player == me.player => {
      //"""^.+id=\d+ local=False \[name=(.+) id=(\d+) zone=HAND zonePos=(\d+) cardId=\S+ player=1\] pos from 0 -> \d+""".r
      var drawnCard: Card = Card()
      drawnCard.name = name
      drawnCard.id = id
      drawnCard.zone = "HAND"
      drawnCard.handPosition = position
      me.hand.append(drawnCard)
    }
    case FriendlyHandChangeEvent(name, id, zonePos, player, dstPos) if player == me.player => {
      //"""^\[Power\] PowerTaskList.+TAG_CHANGE Entity=\[name=(.+) id=(\d+) zone=HAND zonePos=(\d+) cardId=.+ player=(\d+)\] tag=ZONE_POSITION value=(\d+)$""".r
      var index = -2

      me.hand.indexWhere(_.id == id) match {
        case x => index = x
      }

      if (index >= 0) {
        me.hand(index).handPosition = dstPos
        CleanHand(index, me)
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

    case FriendlyZoneChangeEvent(name, id, zone, player, dstZone) if player == me.player =>

      if(dstZone == "HAND"){
        var index = me.board.indexWhere(_.id == id)
        if (index >= 0 && me.board(index).zone == "PLAY")
          me.board(index).zone = "HAND"}

      if(dstZone == "DECK" && zone == "HAND"){
        var index = me.hand.indexWhere(_.id == id)
        if(index >= 0)
          me.hand.remove(index)
      }

      if(dstZone == "DECK" && zone == "PLAY"){
        var index = me.board.indexWhere(_.id == id)
        if(index>=0)
          me.board.remove(index)
      }


    case FriendlyCardReturnEvent(name, id, zonePos,player, dstPos) if (me.board.indexWhere(_.id == id) >= 0) &&  player == me.player => {
      //"""processing index=\d+ change=powerTask=\[power=\[type=TAG_CHANGE entity=\[id=\d+.+ entity=\[name=(.+) id=(\d+) zone=HAND zonePos=(\d+).+ player=1.+ dstPos=(\d+)""".r
      var index = -2

      index = me.board.indexWhere(_.id == id)

      if (index >= 0 && me.board(index).zone == "HAND") {
        me.board(index).zone = "HAND"
        me.board(index).boardPosition = 0
        me.board(index).handPosition = dstPos
        me.hand.append(me.board(index))
        me.board.remove(index)
      }
    }
    case BoardChangeEvent(name: String, id: Int, zonePos: Int, player: Int, dstPos: Int) if player == me.player && dstPos != 0 => {
      // """^\[Power\] PowerTaskList.+TAG_CHANGE Entity=\[name=(.+) id=(\d+) zone=PLAY zonePos=(\d+) cardId=.+ player=(\d)\] tag=ZONE_POSITION value=(\d+)""".r
      var index = -2

      index = me.board.indexWhere(_.id == id)

      if (index >= 0) {
        me.board(index).boardPosition = dstPos
        CleanBoard(index, me)
      }

    }
    case CardDeath(name: String, id: Int, zone: String, zonePos: Int, player: Int) if player == me.player && zone == "PLAY" => {
      //"""^\[Power\] PowerTaskList.+TAG_CHANGE Entity=\[name=(.+) id=(\d+) zone=(.+) zonePos=(\d+).+ player=(\d+).+ tag=ZONE value=GRAVEYARD""".r
      var index = -2
      index = me.board.indexWhere(_.id == id)
      if (index >= 0) {
        me.board.remove(index)
      }
    }

    case FriendlyMinionControlled(name: String, id: Int) =>
      var index = -2
      index = him.board.indexWhere(_.id == id)
      if (index >= 0) {
        me.board.append(him.board(index))
        him.board.remove(index)
      }



    //Enemy Events
    case EnemyCardDrawnEvent(id: Int, position: Int,player:Int) if player == him.player => {
      //"""^.+id=\d+ local=.+ \[id=(\d+) cardId=.+type=.+zone=HAND zonePos=(\d+) player=2\] pos from 0 -> \d+""".r
      var drawnCard: Card = Card()
      drawnCard.id = id
      drawnCard.zone = "HAND"
      drawnCard.handPosition = position
      him.hand.append(drawnCard)
    }
    case EnemyPlaysCardEvent(name, id, dstPos,player)if player == him.player => {
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
    case EnemyHandChangeEvent(id, zonePos, player, dstPos) if player == him.player => {
      //"""^\[Power\] PowerTaskList.+TAG_CHANGE Entity=\[id=(\d+).+ zone=HAND zonePos=(\d+) player=(\d+)\] tag=ZONE_POSITION value=(\d+)""".r
      var index = -2

      index = him.hand.indexWhere(_.id == id)

      if (index >= 0) {
        him.hand(index).handPosition = dstPos
        CleanHand(index, him)
      }

    }
    //Needs tested
    case EnemyCardReturnEvent(name, id, zone, zonePos,player) if player == him.player => {
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
    case BoardChangeEvent(name: String, id: Int, zonePos: Int, player: Int, dstPos: Int) if player == him.player && dstPos != 0 => {
      // """^\[Power\] PowerTaskList.+TAG_CHANGE Entity=\[name=(.+) id=(\d+) zone=PLAY zonePos=(\d+) cardId=.+ player=(\d)\] tag=ZONE_POSITION value=(\d+)""".r
      var index = -2

      him.board.indexWhere(_.id == id) match {
        case x => index = x
      }

      if (index >= 0) {
        him.board(index).boardPosition = dstPos
        CleanBoard(index, him)
      }

    }
    case CardDeath(name: String, id: Int, zone: String, zonePos: Int, player: Int) if player == him.player && zone == "PLAY" => {
      //"""^\[Power\] PowerTaskList.+TAG_CHANGE Entity=\[name=(.+) id=(\d+) zone=(.+) zonePos=(\d+).+ player=(\d+).+ tag=ZONE value=GRAVEYARD""".r
      var index = -2
      index = him.board.indexWhere(_.id == id)
      if (index >= 0) {
        him.board.remove(index)
      }
    }



    //Neutral Events

    case PlayerDefinedEvent(name:String, player: Int) =>
      if (name == USER_NAME){
      me.player = player}
    if (name != USER_NAME){
      him.player = player}

    case MinionSummoned(name: String, id: Int, zonePos: Int, player: Int) if zonePos != 0 => {
      //"""^.+FULL_ENTITY - Updating \[name=(.+) id=(\d+) zone=PLAY zonePos=(\d+).+player=(\d+).+"""
      if (player == me.player) {
        var playedCard = new Card()
        playedCard.name = name
        playedCard.id = id
        playedCard.zone = "PLAY"
        playedCard.boardPosition = zonePos
        me.board.append(playedCard)
      }

      if (player == him.player) {
        var playedCard = new Card()
        playedCard.name = name
        playedCard.id = id
        playedCard.zone = "PLAY"
        playedCard.boardPosition = zonePos
        him.board.append(playedCard)
      }
    }


    case PositionChange(id: Int, player: Int, dstPos: Int) if dstPos != 0 =>  {
      //\[Power\] GameState.DebugPrintPower.+TAG_CHANGE Entity=\[name=.+ id=(\d+).+player=(\d+)\] tag=ZONE_POSITION value=(\d+)
      var index = -2
      var zone = "UNKNOWN"

      if (me.hand.indexWhere(_.id == id) >= 0) {
        index = me.hand.indexWhere(_.id == id)
        me.hand(index).handPosition = dstPos
      }
      if (me.board.indexWhere(_.id == id) >= 0) {
        index = me.board.indexWhere(_.id == id)
        me.board(index).boardPosition = dstPos
      }
      if (him.hand.indexWhere(_.id == id) >= 0) {
        index = him.hand.indexWhere(_.id == id)
        him.hand(index).handPosition = dstPos
      }
      if (him.board.indexWhere(_.id == id) >= 0) {
        index = him.board.indexWhere(_.id == id)
        him.board(index).boardPosition = dstPos
      }
    }

    case Polymorph(newID, name, id, zonePos, player) =>
      //"""\[Zone\] ZoneChangeList.ProcessChanges.+power=\[type=TAG_CHANGE entity=\[id=\d+.+name=.+\] tag=LINKEDCARD value=(\d+)\] complete=false\] entity=\[name=(.+) id=(\d+) zone=PLAY zonePos=(\d+).+ player=(\d+)\].+""".r
      var oldIndex = -2
      if (player == me.player)
        {
          oldIndex = me.board.indexWhere(_.id == id)
          me.board.remove(oldIndex)
          val changedCard = new Card()
          changedCard.id = newID
          changedCard.name = "Sheep"
          changedCard.boardPosition = zonePos
          changedCard.zone = "PLAY"
          me.board.append(changedCard)
        }
      if (player == him.player)
        {
          oldIndex = him.board.indexWhere(_.id == id)
          him.board.remove(oldIndex)
          val changedCard = new Card()
          changedCard.id = newID
          changedCard.name = "Sheep"
          changedCard.boardPosition= zonePos
          changedCard.zone = "PLAY"
          him.board.append(changedCard)
        }






    case TurnStartEvent(value: Int) => {
      turn = value
    }
    case GameOver() => {
      Reset()
    }

    case "Display Status" => {


      var a: Int = 0
      for (a <- 0 until me.hand.length) {
        var index = me.hand.indexWhere(_.handPosition == a + 1)
        if (index >= 0) {
          log.info("My hand, card " + (a + 1) + ": " + me.hand(index).name)
        }
        else
          log.info("Something is wrong with my handPosition in index " + index)
      }




      var b: Int = 0
      for (b <- 0 until me.board.length) {
        var index = me.board.indexWhere(_.boardPosition == b + 1)
        if (index >= 0) {
          log.info("My board position " + (b + 1) + ": " + me.board(index).name)
        }
        else {
          log.info("Something is wrong with my boardPosition in index " + index)
        }
      }

      log.info("\n\nHis hand has " + him.hand.length + " cards.\n")


      var c: Int = 0
      for (c <- 0 until him.board.length) {
        var index = him.board.indexWhere(_.boardPosition == c + 1)
        if (index >= 0) {
          log.info("His board position " + (c + 1) + ": " + him.board(index).name)
        }
        else {
          log.info("Something is wrong with his boardPosition in index " + index)
        }
      }

      log.info("It is turn: " + turn)
      system.scheduler.scheduleOnce(10000.milli, this.self, "Display Status")
    }


    case x: String => {
      log.debug("GameStatus: " + x)
    }


    case _ => {
      log.debug("GameStatus: DEFAULT case")
    }


  }


  def CleanHand(index: Int, player: Player): Unit = {
    if (player.hand(index).handPosition == 0)
      player.hand.remove(index)
  }

  def CleanBoard(index: Int, player: Player): Unit = {
    if (player.board(index).boardPosition == 0)
      player.board.remove(index)
  }

  def Reset(): Unit = {
    me.hand.clear()
    me.board.clear()
    him.hand.clear()
    him.board.clear()
    turn = 0
    me.name = "UNKNOWN"
    him.name = "UNKNOWN"
    me.player = -1
    him.player = -1

  }


}


object Player {
  def apply(name: String) = new Player(name, new ListBuffer[Card], new ListBuffer[Card])
}


class Player(var name: String, handList: ListBuffer[Card], boardList: ListBuffer[Card]) {

  var hand: ListBuffer[Card] = handList
  var board: ListBuffer[Card] = boardList
  var player:Int = -1


}

case class Card() {
  var name: String = "UNKNOWN"
  var id: Int = 0
  var zone: String = "UNKNOWN"
  var handPosition = -1
  var boardPosition = -1


}


