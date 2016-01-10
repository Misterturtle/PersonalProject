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

  DisplayStatus()


  def receive = {
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
      him.hand(position - 1) = drawnCard

    case BoardChangeEvent(name: String, id: Int, zonePos: Int, player: Int, dstPos: Int) if player == 1 =>
      if (dstPos == 0) {
        me.board -= me.board(zonePos)
      }
      else {
        if (me.board.length < dstPos) {
          var a = 0
          for (a <- me.board.length to dstPos) {
            me.board += new Card()
          }
        }

        val changedCard: Card = Card()
        changedCard.name = name
        changedCard.id = id
        me.board(dstPos) = changedCard
      }


    case BoardChangeEvent(name: String, id: Int, zonePos: Int, player: Int, dstPos: Int) if player == 2 =>
      if (dstPos == 0) {
        him.board -= him.board(zonePos)
      }
      else {
        if (him.board.length < dstPos) {
          var a = 0
          for (a <- him.board.length to dstPos) {
            him.board += new Card()
          }
        }
        val changedCard: Card = Card()
        changedCard.name = name
        changedCard.id = id
        him.board(dstPos) = changedCard
      }


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

    while (true) {
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
        log.info("Friendly board position " + b + ": " + me.board(b - 1))
      }

      log.info("It is turn: " + turn)
      Thread.sleep(5000)
    }
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

}


