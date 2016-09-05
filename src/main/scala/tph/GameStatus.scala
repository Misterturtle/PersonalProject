package tph

import java.io._

import akka.actor.{Actor, ActorSystem}
import tph.LogFileEvents._

import scala.collection.mutable._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._


class GameStatus(system: ActorSystem) extends Actor with akka.actor.ActorLogging {



  var turn: Int = 0
  val me: Player = Player("UNKNOWN")
  val him: Player = Player("UNKNOWN")
  val USER_NAME: String = "Wizard"

  def receive = {

    //Friendly Cases

        case OldZoneChangeEvent(id, zone, player, dstZone) =>

          if(player == me.player) {
            if (dstZone == "HAND") {
              var index = me.board.indexWhere(_.id == id)
              if (index >= 0 && me.board(index).zone == "PLAY")
                me.board(index).zone = "HAND"
            }

            if (dstZone == "DECK" && zone == "HAND") {
              var index = me.hand.indexWhere(_.id == id)
              if (index >= 0)
                me.hand.remove(index)
            }

            if (dstZone == "DECK" && zone == "PLAY") {
              var index = me.board.indexWhere(_.id == id)
              if (index >= 0)
                me.board.remove(index)
            }
          }

          if(player == him.player) {
            if (dstZone == "HAND" && zone == "PLAY") {
              var index = him.board.indexWhere(_.id == id)
              if (index >= 0)
                him.board.remove(index)
            }

            if (dstZone == "DECK" && zone == "HAND") {
              var index = him.hand.indexWhere(_.id == id)
              if (index >= 0)
                him.hand.remove(index)
            }

            if (dstZone == "DECK" && zone == "PLAY") {
              var index = him.board.indexWhere(_.id == id)
              if (index >= 0)
                him.board.remove(index)
            }
          }

// Forseen problem: No dstPos passed in. Relying on Position Change methods to fix. Position change methods must come after ZoneChangeEvent
    case ZoneChangeEvent(id, player, zone, dstZone) => {

      var index = -2

      if (zone == "OPPOSING PLAY") {
        index = him.board.indexWhere(_.id == id)
        if (index >= 0) {
          if (dstZone == "FRIENDLY PLAY") {

            val name = him.board(index).name
            him.board.remove(index)
            val changedCard = new Card
            changedCard.name = name
            changedCard.id = id
            changedCard.handPosition = -1
            changedCard.boardPosition = -5
            me.board.append(changedCard)
          }

          if (dstZone == "OPPOSING HAND") {
            val name = him.board(index).name
            him.board.remove(index)
            if (him.hand.indexWhere(_.id == id) == -1) {
              // If his hand doesn't already contain the card
              val changedCard = new Card
              changedCard.name = name
              changedCard.id = id
              changedCard.handPosition = -5
              changedCard.boardPosition = -1
              him.hand.append(changedCard)
            }
          }

          if (dstZone == "OPPOSING DECK") {
            him.board.remove(index)
          }
        }
      }

      if (zone == "OPPOSING HAND") {
        index = him.hand.indexWhere(_.id == id)
        if (index >= 0) {
          if (dstZone == "OPPOSING GRAVEYARD") {
            him.hand.remove(index)
          }
        }
      }



      if (zone == "FRIENDLY PLAY") {
        index = me.board.indexWhere(_.id == id)
        if (index >= 0) {
          if (dstZone == "OPPOSING PLAY") {

            val name = me.board(index).name
            me.board.remove(index)
            val changedCard = new Card
            changedCard.name = name
            changedCard.id = id
            changedCard.handPosition = -1
            changedCard.boardPosition = -5
            him.board.append(changedCard)
          }

          if (dstZone == "FRIENDLY HAND") {
            val name = me.board(index).name
            me.board.remove(index)
            if (me.hand.indexWhere(_.id == id) == -1) {
              // If my hand doesn't already contain the card
              val changedCard = new Card
              changedCard.name = name
              changedCard.id = id
              changedCard.handPosition = -5
              changedCard.boardPosition = -1
              me.hand.append(changedCard)
            }
          }
          if (dstZone == "FRIENDLY DECK") {
            me.hand.remove(index)
          }
        }


      }



      if (zone == "FRIENDLY HAND") {
        index = me.hand.indexWhere(_.id == id)
        if (index >= 0) {
          if (dstZone == "FRIENDLY GRAVEYARD") {
            me.hand.remove(index)
          }
        }
        }
    }


        case FaceAttackValueEvent(player: Int, value: Int) => {
          if (player == me.player) {
            me.faceValue = value
          }
          if (player == him.player) {
            him.faceValue = value
          }
        }

        case WeaponPlayedEvent(id: Int, player: Int) =>
          if (player == me.player) {
            val index = me.hand.indexWhere(_.id == id)
            if (index >= 0)
              me.hand.remove(index)
          }

          if (player == him.player) {
            val index = him.hand.indexWhere(_.id == id)
            if (index >= 0)
              him.hand.remove(index)
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
    case CardDeath(name: String, id: Int, zonePos: Int, player: Int) => {
      //"""^\[Power\] PowerTaskList.+TAG_CHANGE Entity=\[name=(.+) id=(\d+) zone=PLAY zonePos=(\d+).+ player=(\d+).+ tag=ZONE value=GRAVEYARD""".r
      var index = -2
      if (player == me.player) {
        index = me.board.indexWhere(_.id == id)
        if (index >= 0) {
          me.board.remove(index)
        }
        else {
          index = me.hand.indexWhere(_.id == id)
          if (index != -1) {
            me.hand.remove(index)
          }
        }
      }
      if (player == him.player)
        {
          index = him.board.indexWhere(_.id == id)
          if (index >= 0) {
            him.board.remove(index)
          }
          else {
            index = him.hand.indexWhere(_.id == id)
            if (index != -1) {
              him.hand.remove(index)
            }
          }
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
    case EnemyCardDrawnEvent(id: Int, position: Int, player: Int) if player == him.player => {
      //"""^.+id=\d+ local=.+ \[id=(\d+) cardId=.+type=.+zone=HAND zonePos=(\d+) player=2\] pos from \d+ -> \d+""".r

      if(him.hand.indexWhere(_.id == id) == -1) {
        var drawnCard: Card = Card()
        drawnCard.id = id
        drawnCard.zone = "HAND"
        drawnCard.handPosition = position
        him.hand.append(drawnCard)
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



    //Neutral Events

    case KnownCardDrawn(name: String, id: Int, position: Int, player: Int) =>

      if (player == me.player &&
        me.hand.indexWhere(_.id == id) == -1) {
        // Prevents bug of drawing a returned card
        var drawnCard: Card = Card()
        drawnCard.name = name
        drawnCard.id = id
        drawnCard.zone = "HAND"
        drawnCard.handPosition = position
        me.hand.append(drawnCard)
      }

      if (player == him.player
      && him.hand.indexWhere(_.id == id) == -1 // Prevents bug where a returned enemy card is "drawn" again
      ) {
        var drawnCard: Card = Card()
        drawnCard.id = id
        drawnCard.zone = "HAND"
        drawnCard.handPosition = position
        him.hand.append(drawnCard)
      }

    case Sap(name: String, id: Int, player: Int) =>
      if (player == me.player) {
        var index = -2
        index = me.board.indexWhere(_.id == id)
        me.board.remove(index)
      }

      if (player == him.player) {
        var index = -2
        index = him.board.indexWhere(_.id == id)
        him.board.remove(index)
      }


    case CardPlayed(name: String, id: Int, dstPos: Int, player: Int) =>
      if (player == me.player) {
        var index = -2

        index = me.hand.indexWhere(_.id == id)
        if (index >= 0) {
          me.hand(index).zone = "PLAY"
          me.hand(index).name = name
          me.hand(index).boardPosition = dstPos
          me.hand(index).handPosition = 0
          if (dstPos > 0) {
            me.board.append(me.hand(index))
          }
          me.hand.remove(index)
        }
      }

      if (player == him.player) {
        var index = -2

        index = him.hand.indexWhere(_.id == id)
        if (index >= 0) {
          him.hand(index).zone = "PLAY"
          him.hand(index).name = name
          him.hand(index).boardPosition = dstPos
          him.hand(index).handPosition = 0
          if (dstPos > 0) {
            him.board.append(him.hand(index))
          }
          him.hand.remove(index)
        }
      }

    case DefinePlayers(friendlyPlayerID: Int) =>
      if (friendlyPlayerID == 1) {
        me.player = 1
        him.player = 2
      }
      if (friendlyPlayerID == 2) {
        me.player = 2
        him.player = 1
      }


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


    case BoardPositionChange(id: Int, player: Int, dstPos: Int) => {
      //\[Zone\] ZoneChangeList.ProcessChanges\(\) - processing index=\d+ change=powerTask=\[power=\[type=TAG_CHANGE entity=\[id=\d+ cardId=.+ name=.+\] tag=ZONE_POSITION value=\d+\] complete=False\] entity=\[name=.+ id=(\d+) zone=PLAY zonePos=(\d+) cardId=.+ player=(\d+)] srcZoneTag=INVALID srcPos= dstZoneTag=INVALID dstPos=(\d+)
      var index = -2

      if (me.board.indexWhere(_.id == id) >= 0) {
        index = me.board.indexWhere(_.id == id)
        me.board(index).boardPosition = dstPos
      }
      if (him.board.indexWhere(_.id == id) >= 0) {
        index = him.board.indexWhere(_.id == id)
        him.board(index).boardPosition = dstPos
      }
    }

        case HandPositionChange(id, pos, player, dstPos)
          if pos != 0 =>
      var index = -2
      if (player == me.player)
        {
          index = me.hand.indexWhere(_.id == id)
          if(index >= 0)
          me.hand(index).handPosition = dstPos
        }
      if (player == him.player)
        {
          index = him.hand.indexWhere(_.id == id)
          if (index >= 0)
          him.hand(index).handPosition = dstPos
        }


        case Transform(oldId, newId) if (newId != 0) =>
          //\[Power\] .+.DebugPrintPower\(\) -     TAG_CHANGE Entity=.+id=(\d+) zone=PLAY.+tag=LINKED_ENTITY value=(\d+)
          var index = -2
          //These conditions are needed due to some transform cards creating an instance on the board and some not.
          if (me.hand.indexWhere(_.id == oldId) >= 0) //Old card in my hand
          {
            index = me.hand.indexWhere(_.id == oldId)
            me.hand.remove(index)
            var changedCard = new Card
            changedCard.name = "Transformed Minion"
            changedCard.id = newId
            changedCard.boardPosition = -5
            changedCard.handPosition = -1
            me.board.append(changedCard)
          }

          if (me.board.indexWhere(_.id == oldId) >= 0) //Old card on him board
          {
            index = me.board.indexWhere(_.id == oldId)
            me.board(index).id = newId
            me.board(index).name = "Transformed Minion"
          }

          if (him.hand.indexWhere(_.id == oldId) >= 0) // Old card in his hand
          {
            index = him.hand.indexWhere(_.id == oldId)
            him.hand.remove(index)
            var changedCard = new Card
            changedCard.name = "Transformed Minion"
            changedCard.id = newId
            changedCard.boardPosition = -5
            changedCard.handPosition = -1
            him.board.append(changedCard)
          }

          if (him.board.indexWhere(_.id == oldId) >= 0) //Old card on his board
          {
            index = him.board.indexWhere(_.id == oldId)
            him.board(index).id = newId
            him.board(index).name = "Transformed Minion"
          }

    case Hex(name, id,player, zonePos) => {
      var oldIndex = -2
      var changedCard = new Card()
      if (player == me.player) {
        oldIndex = me.board.indexWhere(_.boardPosition == zonePos)
        me.board.remove(oldIndex)
        changedCard.name = name
        changedCard.id = id
        changedCard.boardPosition = zonePos
        me.board.append(changedCard)
      }
      if (player == him.player) {
        oldIndex = him.board.indexWhere(_.boardPosition == zonePos)
        him.board.remove(oldIndex)
        changedCard.name = name
        changedCard.id = id
        changedCard.boardPosition = zonePos
        him.board.append(changedCard)
      }
    }


    case SecretPlayedEvent(id,player) =>
      var index = -2
      if (player == me.player)
        {
          index = me.hand.indexWhere(_.id == id)
          if (index != -1)
          me.hand.remove(index)
        }
      if (player == him.player)
      {
        index = him.hand.indexWhere(_.id == id)
        if (index != -1)
          him.hand.remove(index)
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

      log.info("\n\nMy face value is " + me.faceValue)
      log.info("\n\nHis hand has " + him.hand.length + " cards.\n")
      log.info("\nHis face value is " + him.faceValue)



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

      var d: Int = 0
      for (d <- 0 until him.hand.length) {
        var index = him.hand.indexWhere(_.handPosition == d + 1)
        if (index >= 0) {
          log.info("His hand, card " + (d + 1) + ": " + him.hand(index).id)
        }
        else
          log.info("Something is wrong with his handPosition in index " + index)
      }

      log.info("It is turn: " + turn)
      system.scheduler.scheduleOnce(10000.milli, this.self, "Display Status")

    }

        case "GetGameStatus" =>
          val array: Array[Player] = new Array[Player](2)
          array(0) = me
          array(1) = him
          sender ! array


        case x => {
    }

    case PrintState(fileName) =>
      {
        PrintState(fileName)
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
    me.faceValue = 0
    him.faceValue = 0
  }

  def PrintState(fileName: String): Unit = {
    val writer = new PrintWriter(new FileWriter("C:\\Users\\RC\\Documents\\GitHubRepository\\TwitchPlaysHearthstone\\debugsituations\\Results\\" + fileName ))

    for (a <- 0 until me.hand.length) {
      writer.println("Friendly Hand Index "+ (a+1) +": HandPosition: " + me.hand(a).handPosition + " BoardPosition: " + me.hand(a).boardPosition + " Id: "+ me.hand(a).id+"\n" )
      writer.flush()
    }

    for (b <- 0 until me.board.length) {
      writer.println("Friendly Board Index "+ (b+1) +": HandPosition: " + me.board(b).handPosition + " BoardPosition: " + me.board(b).boardPosition + " Id: "+ me.board(b).id+"\n" )
      writer.flush()
    }

    for (c <- 0 until him.hand.length) {
      writer.println("Enemy Hand Index "+ (c+1) +": HandPosition: " + him.hand(c).handPosition + " BoardPosition: " + him.hand(c).boardPosition + " Id: "+ him.hand(c).id+"\n" )
      writer.flush()
    }

    for (d <- 0 until him.board.length) {
      writer.println("Enemy Board Index "+ (d+1) +": HandPosition: " + him.board(d).handPosition + " BoardPosition: " + him.board(d).boardPosition + " Id: "+ him.board(d).id+"\n" )
      writer.flush()
    }
  }

}


object Player {
  def apply(name: String) = new Player(name, new ListBuffer[Card], new ListBuffer[Card])
}


class Player(var name: String, handList: ListBuffer[Card], boardList: ListBuffer[Card]) {

  var hand: ListBuffer[Card] = handList
  var board: ListBuffer[Card] = boardList
  var player:Int = -1
  var faceValue = 0
}

case class Card() {
  var name: String = "UNKNOWN"
  var id: Int = 0
  var zone: String = "UNKNOWN"
  var handPosition = -1
  var boardPosition = -1
}


