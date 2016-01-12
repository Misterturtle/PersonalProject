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
  var enemyZoneChange: Card = Card()
  var friendlyZoneChange: Card = Card()


  def receive = {

    case "poll" =>
      DisplayStatus()
      system.scheduler.scheduleOnce(10000.millis, this.self, "poll")

    case TurnStartEvent(value: Int) =>
      turn = value

    case FriendlyCardDrawnEvent(name: String, id: Int, position: Int) =>


      if (me.hand.length < position) {
        var a = 0
        for (a <- me.hand.length to position - 1) {
          me.hand += new Card()
        }
      }
      val drawnCard: Card = Card()
      drawnCard.name = name
      drawnCard.id = id
      drawnCard.zone = "HAND"

      me.hand(position - 1) = drawnCard


    case EnemyCardDrawnEvent(id: Int, position: Int) =>


      if (him.hand.length < position) {
        var a = 0
        for (a <- him.hand.length to position - 1) {
          him.hand += new Card()
        }
      }
      val drawnCard: Card = Card()
      drawnCard.id = id
      drawnCard.zone = "HAND"
      him.hand(position - 1) = drawnCard

    //Player 1
    case BoardChangeEvent(name: String, id: Int, zonePos: Int, player: Int, dstPos: Int) if player == 1 =>
      if (dstPos == 0 && me.board(zonePos-1).id == id) {
        me.board.remove(zonePos - 1)
      }
      else {
        if (me.board.length < dstPos) {
          var a = 0
          for (a <- me.board.length to dstPos-1) {
            me.board += new Card()
          }
        }

        val changedCard: Card = Card()
        changedCard.zone = "PLAY"
        changedCard.name = name
        changedCard.id = id
        me.board(dstPos - 1) = changedCard
      }

    //Player 2
    case BoardChangeEvent(name: String, id: Int, zonePos: Int, player: Int, dstPos: Int) if player == 2  =>
      if (dstPos == 0 && him.board(zonePos-1).id == id) {
        him.board.remove(zonePos - 1)
      }
      else {
        if (him.board.length < dstPos) {
          var a = 0
          for (a <- him.board.length to dstPos-1) {
            him.board += new Card()
          }
        }
        val changedCard: Card = Card()
        changedCard.zone = "PLAY"
        changedCard.name = name
        changedCard.id = id
        him.board(dstPos - 1) = changedCard
      }


    case FriendlyHandChangeEvent(name, id, zonePos, player, dstPos) if dstPos != 0 && player == 1 && friendlyZoneChange.name == "UNKNOWN" =>
      //Only Friendly Hand Position Reassignments
      if (me.hand.length < dstPos) {
        var a = 0
        for (a <- me.hand.length to dstPos-1) {
          me.hand += new Card()
        }
      }
      val changedCard: Card = Card()
      changedCard.zone = "HAND"
      changedCard.name = name
      changedCard.id = id
      me.hand(dstPos - 1) = changedCard
      if(me.hand(zonePos-1).id == me.hand(dstPos-1).id)
        me.hand.remove(zonePos-1)


    case EnemyHandChangeEvent(id, zonePos, player, dstPos) if dstPos != 0 && player == 2 && enemyZoneChange.name == "UNKNOWN" =>
      // Only Enemy Hand Position Reassignments
      if (him.hand.length < dstPos) {
        var a = 0
        for (a <- him.hand.length to dstPos-1) {
          him.hand += new Card()
        }
      }
      val changedCard: Card = Card()
      changedCard.id = id
      changedCard.zone = "HAND"
      him.hand(dstPos - 1) = changedCard







    //Enemy Zone Changing
    //
    //
    //
    case EnemyPlaysCardEvent(id, zonePos, player) if player == 2 && him.hand(zonePos - 1).id == id =>
      //Enemy plays card from hand to board
      him.hand.remove(zonePos - 1)
      enemyZoneChange.id = id
      enemyZoneChange.zone = "PLAY"


    case EnemyHandChangeEvent(id, zonePos, player, dstPos) if player == 2 && enemyZoneChange.zone == "PLAY" && enemyZoneChange.id == id =>
      // Creates card for board
      // Zone must already be set as "PLAY"
      // Sets board position for recently played cards
      // Disposes of card if position = 0

        enemyZoneChange.zone = "UNKNOWN"
        enemyZoneChange.id = 0

      if(dstPos > 0 ) {
        if (him.board.length < dstPos) {
          var a = 0
          for (a <- him.board.length to dstPos-1) {
            him.board += new Card()
          }
        }

        val changedCard: Card = Card()
        changedCard.id = id
        him.board(dstPos - 1) = changedCard
      }

    case EnemyCardReturnEvent(id, zone, zonePos, dstZone) if dstZone == "HAND" && him.board(zonePos - 1).id == id =>
      //When a card is returned from board to hand
      //Does not create hand Card() yet
      enemyZoneChange.id = id
      enemyZoneChange.zone = "HAND"
      him.board.remove(zonePos - 1)


    case BoardChangeEvent(name, id, zonePos, player, dstPos) if enemyZoneChange.id == id && enemyZoneChange.zone == "HAND" =>
      //When a card is returned from board to hand
      //Creates hand Card()
      if (him.hand.length < dstPos) {
        var a = 0
        for (a <- him.hand.length to dstPos-1) {
          him.hand += new Card()
        }
      }

      val changedCard: Card = Card()
      changedCard.id = id
      him.hand(dstPos - 1) = changedCard
    //
    //
    //
    //End of Enemy Zone Change


    //Friendly Zone Change
    //
    //
    //



    case FriendlyZoneChangeEvent(name, id, zone, zonePos, dstZone) if dstZone == "PLAY"  =>
      friendlyZoneChange.name = name
      friendlyZoneChange.id = id
      friendlyZoneChange.zone = "PLAY"
      if(me.hand(zonePos-1).id == id)
      me.hand.remove(zonePos-1)

    case FriendlyHandChangeEvent(name, id, zonePos, player, dstPos) if player == 1 && friendlyZoneChange.id == id && friendlyZoneChange.zone == "PLAY" =>

        friendlyZoneChange.zone = "UNKNOWN"
        friendlyZoneChange.id = 0
        friendlyZoneChange.name = "UNKNOWN"

      if (dstPos > 0) {
        if (me.board.length < dstPos) {
          var a = 0
          for (a <- me.board.length to dstPos-1) {
            me.board += new Card()
          }
        }

        val changedCard: Card = Card()
        changedCard.name = name
        changedCard.id = id
        changedCard.zone = "PLAY"
        me.board(dstPos - 1) = changedCard
      }

    case FriendlyZoneChangeEvent(name, id, zone, zonePos, dstZone) if dstZone == "HAND" && me.board(zonePos-1).id == id =>
      friendlyZoneChange.name = name
      friendlyZoneChange.id = id
      friendlyZoneChange.zone = "HAND"
      me.board.remove(zonePos-1)

    case FriendlyHandChangeEvent(name, id, zonePos, player, dstPos) if player == 1 && friendlyZoneChange.id == id && friendlyZoneChange.zone == "HAND" =>

        friendlyZoneChange.zone = "UNKNOWN"
        friendlyZoneChange.id = 0
        friendlyZoneChange.name = "UNKNOWN"

      if (dstPos > 0) {
        if (me.hand.length < dstPos) {
          var a = 0
          for (a <- me.hand.length to dstPos-1) {
            me.hand += new Card()
          }
        }

        val changedCard: Card = Card()
        changedCard.id = id
        changedCard.name = name
        changedCard.zone = "HAND"
        me.hand(dstPos - 1) = changedCard
      }


      //
      //
      //
      //End Friendly Zone Change




    case FriendlyDefinedEvent(name: String) =>
      me.name = name

    case EnemyDefinedEvent(name: String) =>
      him.name = name


    case PlaysFirstEvent(firstPlayerName: String) if firstPlayerName == me.name =>
      me.playsFirst = true


    case PlaysFirstEvent(firstPlayerName: String) if firstPlayerName == him.name =>
      him.playsFirst = true

    case GameOver() =>

      Reset()

    case x: String =>
      log.debug("GameStatus: " + x)

    case _ =>
      log.debug("GameStatus: DEFAULT case")
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

    if (me.playsFirst == true)
      log.info(me.name + " plays first")
    if (him.playsFirst == false)
      log.info(him.name + " plays first")

    var a: Int = 0
    for (a <- 1 to me.hand.length) {
      log.info("My hand, card " + a + ": " + me.hand(a - 1).name)
    }

    log.info("\n\nHis hand has " + him.hand.length + " cards.\n")

    var b: Int = 0
    for (b <- 1 to me.board.length) {
      log.info("Friendly board position " + b + ": " + me.board(b - 1).name)
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
  var playsFirst: Boolean = false


}

case class Card() {
  var name: String = "UNKNOWN"
  var id: Int = 0
  var zone: String = "UNKNOWN"

}


