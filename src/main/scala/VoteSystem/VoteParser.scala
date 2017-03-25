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
  //Emote Type
  val GREETINGS = "greetings"
  val THANKS = "thanks"
  val WELL_PLAYED = "well played"
  val WOW = "wow"
  val OOPS = "oops"
  val THREATEN = "threaten"

  //In Game Always Type
  val END_TURN = "end turn"
  val BIND = "bind"
  val FUTURE = "future"

  def createVote(sender:String, command:String): Vote = {

    command.toLowerCase match {

        //----------Emoji Votes-------//
      case GREETINGS =>
        new Greetings()
      case THANKS =>
        new Thanks()
      case WELL_PLAYED =>
        new WellPlayed()
      case WOW =>
        new Wow()
      case OOPS =>
        new Oops()
      case THREATEN =>
        new Threaten()


        //-----------Misc Votes----------//
      case END_TURN =>
        new EndTurn()


        //------------Action Votes----------//
      case _ =>
        createActionVote(sender, command)

    }
  }

  def createActionVote(sender:String, command:String): ActionVote ={
    val CARD_PLAY_COMMAND = """\s*c(\d+)\s*""".r
    val CARD_PLAY_WITH_POSITION = """\s*c(\d+)\s*>\s*>\s*f(\d+)\s*""".r
    val CARD_PLAY_WITH_FRIENDLY_TARGET_WITH_POSITION = """\s*c(\d+)\s*>\s*>\s*f(\d+)\s*>\s*f(\d+)\s*""".r
    val REVERSE_CARD_PLAY_WITH_FRIENDLY_TARGET_WITH_POSITION = """\s*c(\d+)\s*>\s*f(\d+)\s*>\s*>\s*f(\d+)\s*""".r
    val CARD_PLAY_WITH_ENEMY_TARGET_WITH_POSITION = """\s*c(\d+)\s*>\s*>\s*f(\d+)\s*>\s*e(\d+)\s*""".r
    val REVERSE_CARD_PLAY_WITH_ENEMY_TARGET_WITH_POSITION = """\s*c(\d+)\s*>\s*e(\d+)\s*>\s*>\s*f(\d+)\s*""".r
    val CARD_PLAY_WITH_FRIENDLY_TARGET = """\s*c(\d+)\s*>\s*f(\d+)\s*""".r
    val CARD_PLAY_WITH_ENEMY_TARGET = """\s*c(\d+)\s*>\s*e(\d+)\s*""".r
    val NORMAL_ATTACK_COMMAND = """\s*f(\d+)\s*>\s*e(\d+)\s*""".r
    val HERO_POWER = """\s*hp\s*""".r
    val HERO_POWER_WITH_FRIENDLY_TARGET = """\s*hp\s*>\s*f(\d+)\s*""".r
    val HERO_POWER_WITH_ENEMY_TARGET = """\s*hp\s*>\s*e(\d+)\s*""".r
    val DISCOVER = """\s*discover\s*(\d)\s*""".r
    val MULLIGAN_VOTE = """\s*mulligan\s*(.+)\s*""".r

    command match{
      case CARD_PLAY_COMMAND(card) =>
        new CardPlay(card.toInt)

      case CARD_PLAY_WITH_POSITION(card, position)=>
        new CardPlayWithPosition(card.toInt, position.toInt)

      case CARD_PLAY_WITH_FRIENDLY_TARGET_WITH_POSITION(card, position, friendlyTarget) =>
        new CardPlayWithFriendlyTargetWithPosition(card.toInt, friendlyTarget.toInt, position.toInt)

      case REVERSE_CARD_PLAY_WITH_FRIENDLY_TARGET_WITH_POSITION(card, friendlyTarget, position) =>
        new CardPlayWithFriendlyTargetWithPosition(card.toInt, friendlyTarget.toInt, position.toInt)

      case CARD_PLAY_WITH_ENEMY_TARGET_WITH_POSITION(card, position, enemyTarget) =>
        new CardPlayWithEnemyTargetWithPosition(card.toInt, enemyTarget.toInt, position.toInt)

      case REVERSE_CARD_PLAY_WITH_ENEMY_TARGET_WITH_POSITION(card, enemyTarget, position) =>
        new CardPlayWithEnemyTargetWithPosition(card.toInt, enemyTarget.toInt, position.toInt)

      case CARD_PLAY_WITH_FRIENDLY_TARGET(card, friendlyTarget)=>
        new CardPlayWithFriendlyTarget(card.toInt, friendlyTarget.toInt)

      case CARD_PLAY_WITH_ENEMY_TARGET(card, enemyTarget) =>
        new CardPlayWithEnemyTarget(card.toInt, enemyTarget.toInt)

      case NORMAL_ATTACK_COMMAND(friendlyTarget, enemyTarget) =>
        new NormalAttack(friendlyTarget.toInt, enemyTarget.toInt)

      case HERO_POWER()=>
        new HeroPower()

      case HERO_POWER_WITH_FRIENDLY_TARGET(friendlyTarget)=>
        new HeroPowerWithFriendlyTarget(friendlyTarget.toInt)

      case HERO_POWER_WITH_ENEMY_TARGET(enemyTarget)=>
        new HeroPowerWithEnemyTarget(enemyTarget.toInt)

      case DISCOVER(card)=>
        new Discover(card.toInt)

      case MULLIGAN_VOTE(cards)=>
        parseMulligan(sender, cards)

      case _ =>
        new ActionUninit()
    }
  }


  def parseMulligan(sender: String, cards: String): ActionVote = {
    val ONE = """\s*(\d)\s*""".r
    val TWO = """\s*(\d)\s*(\d)\s*""".r
    val THREE = """\s*(\d)\s*(\d)\s*(\d)\s*""".r
    val FOUR = """\s*(\d)\s*(\d)\s*(\d)\s*(\d)\s*""".r

    var firstCard = false
    var secondCard = false
    var thirdCard = false
    var fourthCard = false

    def chooseMulligan(number:String):Unit = {
      number match {
        case "1" => firstCard = true
        case "2" => secondCard = true
        case "3" => thirdCard = true
        case "4" => fourthCard = true}}
    

    cards match {
      case FOUR(a, b, c, d) =>
        chooseMulligan(a)
        chooseMulligan(b)
        chooseMulligan(c)
        chooseMulligan(d)
        new MulliganVote(firstCard, secondCard, thirdCard, fourthCard)

      case THREE(a, b, c) =>
        chooseMulligan(a)
        chooseMulligan(b)
        chooseMulligan(c)

        new MulliganVote(firstCard, secondCard, thirdCard, fourthCard)


      case TWO(a, b) =>
        chooseMulligan(a)
        chooseMulligan(b)
        new MulliganVote(firstCard, secondCard, thirdCard, fourthCard)

      case ONE(a) =>
        chooseMulligan(a)

        new MulliganVote(firstCard, secondCard, thirdCard, fourthCard)

      case _ =>
        logger.debug("Unexpected mulligan string " + cards)
        logger.debug("Creating empty vote to match return type")
        new ActionUninit()
    }
  }
}
