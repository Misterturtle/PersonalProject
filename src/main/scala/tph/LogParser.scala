package tph.Immutable

import java.io._

import tph.Constants
import tph.HSAction._

/**
  * Created by Harambe on 2/15/2017.
  */
class LogParser() {

  //  val defaultLog = new File("/actionLog.txt")
  //
  //
  //  def CreateActionList(file: File = defaultLog, gameState: GameState): List[HSAction] ={
  //
  //    import LogFileReader._
  //    val reader = new BufferedReader(new FileReader(file))
  //    val streams = Stream.continually(reader.readLine()).takeWhile(_ != null)
  //    val friendlyPlayerNumber = GetPlayerNumbers()._1
  //
  //    streams.map(line => line match{
  //      case LogFileReader.CARD_DEATH(name, id, player) => new HSAction.CardDeath(name, id.toInt, player.toInt)
  //      case LogFileReader.CARD_PLAYED(name, id, position, player) => new HSAction.CardPlayed()
  //
  //
  //
  //    })
  //
  //
  //
  //    streams.foreach { f: String =>
  //      f match {
  //        case CARD_DEATH(name, id, player) =>
  //
  //          streams.map
  //
  //        case CARD_PLAYED(name, id, position, player) =>
  //          val card = new Card(name, id.toInt, Constants.INT_UNINIT, position.toInt, player.toInt)
  //          val cardAddress = CardAddress(player.toInt, gameState.)
  //          gameManipulator.AddCard(card,)
  //
  //
  //        case HAND_POSITION_CHANGE =>
  //        case KNOWN_CARD_DRAWN =>
  //        case SECRET_PLAYED =>
  //
  //      }
  //    }
  //
  //
  //  }
  //
  //  def ConstructFriendlyHand(file: File = defaultLog): List[Card] = {
  //    import LogFileReader._
  //    val reader = new BufferedReader(new FileReader(file))
  //    val streams = Stream.continually(reader.readLine()).takeWhile(_ != null)
  //    val friendlyPlayerNumber = GetPlayerNumbers()._1
  //
  //
  //
  //    streams.foreach { f: String =>
  //      f match {
  //        case CARD_DEATH(name, id, player) =>
  //          if (player.toInt == friendlyPlayerNumber) {
  //            GameManipulator.RemoveCard()
  //          }
  //          GameManipulator.RemoveCard()
  //
  //        case CARD_PLAYED(name, id, position, player) =>
  //          val card = new Card(name, id.toInt, Constants.INT_UNINIT, position.toInt, player.toInt)
  //          val cardAddress = CardAddress(player.toInt, gameState.)
  //          gameManipulator.AddCard(card,)
  //
  //
  //        case HAND_POSITION_CHANGE =>
  //        case KNOWN_CARD_DRAWN =>
  //        case SECRET_PLAYED =>
  //
  //      }
  //    }
  //  }
  //
  //
  //  def GetPlayerNumbers(): (Int, Int) = {
  //    val DEFINE_PLAYERS = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - TRANSITIONING card \[name=.+ id=.+ zone=PLAY zonePos=0 cardId=.+ player=(\d+)\] to FRIENDLY PLAY \(Hero\)""".r
  //    val streams = Stream.continually(new LogFileReader().reader.readLine()).takeWhile(_ != null)
  //    streams.foreach {
  //      case DEFINE_PLAYERS(friendlyPlayer) =>
  //        friendlyPlayer.toInt match {
  //          case 1 =>
  //            (1, 2)
  //          case 2 =>
  //            (2, 1)
  //          case _ =>
  //            logger.debug("Unexpected player value of :" + friendlyPlayer)
  //        }
  //      case _ =>
  //    }
  //
  //
  //  }


}
