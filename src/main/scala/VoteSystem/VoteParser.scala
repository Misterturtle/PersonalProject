package VoteSystem

import com.typesafe.scalalogging.LazyLogging
import tph.Constants.MenuVotes._
import tph.Constants.ActionVotes._
import tph.Constants.EmojiVotes._
import tph.Constants.MiscVotes._
import tph.{Constants, IRCBot}

/**
  * Created by Harambe on 2/22/2017.
  */
class VoteParser extends LazyLogging {

  import IRCBot._

  def ParseVote(stringVote: String, sender: String): Vote = {

    stringVote.toLowerCase match {

      case PLAY =>
        new Play(sender)
      case SHOP =>
        new Shop(sender)
      case OPEN_PACKS =>
        new OpenPacks(sender)
      case COLLECTION =>
        new Collection(sender)
      case QUEST_LOG =>
        new QuestLog(sender)
      case CASUAL =>
        new Casual(sender)
      case RANKED =>
        new Ranked(sender)
      case BACK =>
        new Back(sender)
      case DECK(deckNumber) =>
        new Deck(sender, deckNumber.toInt)
      case FIRST_PAGE =>
        new FirstPage(sender)
      case SECOND_PAGE =>
        new SecondPage(sender)
      case QUEST(number) =>
        new Quest(sender, number.toInt)
      case GREETINGS =>
        new Greetings(sender)
      case THANKS =>
        new Thanks(sender)
      case WELL_PLAYED =>
        new WellPlayed(sender)
      case WOW =>
        new Wow(sender)
      case OOPS =>
        new Oops(sender)
      case THREATEN =>
        new Threaten(sender)
      case END_TURN =>
        new EndTurn(sender)
      case BIND =>
        new Bind(sender)
      case FUTURE =>
        new Future(sender)

      case MULLIGAN(stringVote: String) =>
        ParseMulligan(sender, stringVote)

      case _ =>
        new ActionUninit(sender)


    }
  }

//  def CreateActionVote(sender: String, stringVote:String): ActionVote ={
//    //Add Cast command
//    //!f1 att e2 then play
//    val friendlyCardRegex = """c(\d)""".r
//    val friendlyBoardRegex = """f(\d)""".r
//    val enemyRegex = """.*e(\d).*""".r
//    val attackRegex = """.*att.*""".r
//    val castRegex = """.*cast(.*)""".r
//
//
//  }


  def ParseMulligan(sender: String, stringVote: String): MulliganVote = {
    val ONE = """.*(\d).*""".r
    val TWO = """.*(\d).*,.*(\d).*""".r
    val THREE = """.*(\d).*,.*(\d).*,.*(\d).*""".r
    val FOUR = """.*(\d).*,.*(\d).*,.*(\d).*,.*(\d).*""".r

    stringVote match {
      case FOUR(first, second, third, fourth) =>
        var firstCard = false
        var secondCard = false
        var thirdCard = false
        var fourthCard = false


        first match {

          case "1" =>
            firstCard = true
          case "2" =>
            secondCard = true
          case "3" =>
            thirdCard = true
          case "4" =>
            fourthCard = true
        }

        second match {

          case "1" =>
            firstCard = true
          case "2" =>
            secondCard = true
          case "3" =>
            thirdCard = true
          case "4" =>
            fourthCard = true
        }

        third match {

          case "1" =>
            firstCard = true
          case "2" =>
            secondCard = true
          case "3" =>
            thirdCard = true
          case "4" =>
            fourthCard = true
        }

        fourth match {

          case "1" =>
            firstCard = true
          case "2" =>
            secondCard = true
          case "3" =>
            thirdCard = true
          case "4" =>
            fourthCard = true
        }

        new MulliganVote(sender, firstCard, secondCard, thirdCard, fourthCard)


      case THREE(first, second, third) =>
        var firstCard = false
        var secondCard = false
        var thirdCard = false
        var fourthCard = false


        first match {

          case "1" =>
            firstCard = true
          case "2" =>
            secondCard = true
          case "3" =>
            thirdCard = true
          case "4" =>
            fourthCard = true
        }

        second match {

          case "1" =>
            firstCard = true
          case "2" =>
            secondCard = true
          case "3" =>
            thirdCard = true
          case "4" =>
            fourthCard = true
        }

        third match {

          case "1" =>
            firstCard = true
          case "2" =>
            secondCard = true
          case "3" =>
            thirdCard = true
          case "4" =>
            fourthCard = true
        }

        new MulliganVote(sender, firstCard, secondCard, thirdCard, fourthCard)


      case TWO(first, second) =>
        var firstCard = false
        var secondCard = false
        var thirdCard = false
        var fourthCard = false


        first match {

          case "1" =>
            firstCard = true
          case "2" =>
            secondCard = true
          case "3" =>
            thirdCard = true
          case "4" =>
            fourthCard = true
        }

        second match {

          case "1" =>
            firstCard = true
          case "2" =>
            secondCard = true
          case "3" =>
            thirdCard = true
          case "4" =>
            fourthCard = true
        }

        new MulliganVote(sender, firstCard, secondCard, thirdCard, fourthCard)

      case ONE(first) =>
        var firstCard = false
        var secondCard = false
        var thirdCard = false
        var fourthCard = false


        first match {

          case "1" =>
            firstCard = true
          case "2" =>
            secondCard = true
          case "3" =>
            thirdCard = true
          case "4" =>
            fourthCard = true
        }


        new MulliganVote(sender, firstCard, secondCard, thirdCard, fourthCard)

      case _ =>
        logger.debug("Unexpected mulligan string " + stringVote)
        logger.debug("Creating empty vote to match return type")

        new MulliganVote(sender, false, false, false, false)
    }
  }
}
