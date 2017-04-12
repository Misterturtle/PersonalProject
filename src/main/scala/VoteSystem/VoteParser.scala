package VoteSystem

import com.typesafe.scalalogging.LazyLogging
import tph.Constants.ActionVotes._
import tph.Constants.EmojiVotes._
import tph.Constants.Vote

/**
  * Created by Harambe on 2/22/2017.
  */
class VoteParser extends LazyLogging {
  //Emote Type
  val GREETINGS = "greetings"
  val THANKS = "thanks"
  val WELL_PLAYED = "wellplayed"
  val WOW = "wow"
  val OOPS = "oops"
  val THREATEN = "threaten"

  //In Game Always Type
  val BIND = "bind"
  val FUTURE = "future"

  def createVote(sender: String, command: String): Vote = {

    val trimmedCommand = command.replaceAll("\\s+","")

     trimmedCommand match {

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


      //------------Action Votes----------//
      case _ =>
        createActionVote(sender, trimmedCommand)

    }
  }

  def createActionVote(sender: String, command: String): ActionVote = {

    val CARD_PLAY_COMMAND = """(\?*)c(\d+)""".r
    val CARD_PLAY_WITH_POSITION = """(\?*)c(\d+)>>(\?*)f(\d+)""".r
    val CARD_PLAY_WITH_FRIENDLY_TARGET_WITH_POSITION = """(\?*)c(\d+)>>(\?*)f(\d+)>(\?*)f(\d+)""".r
    val REVERSE_CARD_PLAY_WITH_FRIENDLY_TARGET_WITH_POSITION = """(\?*)c(\d+)>(\?*)f(\d+)>>(\?*)f(\d+)""".r
    val CARD_PLAY_WITH_ENEMY_TARGET_WITH_POSITION = """(\?*)c(\d+)>>(\?*)f(\d+)>(\?*)e(\d+)""".r
    val REVERSE_CARD_PLAY_WITH_ENEMY_TARGET_WITH_POSITION = """(\?*)c(\d+)>(\?*)e(\d+)>>(\?*)f(\d+)""".r
    val CARD_PLAY_WITH_FRIENDLY_TARGET = """(\?*)c(\d+)>(\?*)f(\d+)""".r
    val CARD_PLAY_WITH_ENEMY_TARGET = """(\?*)c(\d+)>(\?*)e(\d+)""".r
    val NORMAL_ATTACK_COMMAND = """(\?*)f(\d+)>(\?*)e(\d+)""".r
    val HERO_POWER = """hp""".r
    val HERO_POWER_WITH_FRIENDLY_TARGET = """hp>(\?*)f(\d+)""".r
    val HERO_POWER_WITH_ENEMY_TARGET = """hp>(\?*)e(\d+)""".r
    val DISCOVER = """discover(\d)""".r
    val END_TURN = """endturn""".r
    val END_TURN_SHORTHAND = """et""".r
    val REMOVE_VOTE = """remove(.+)""".r
    val HURRY = "hurry"



    command match {
      case CARD_PLAY_COMMAND(futureTrue, card) =>
        if(futureTrue.nonEmpty)
          FutureCardPlay(card.toInt)
        else
          CardPlay(card.toInt)

      case CARD_PLAY_WITH_POSITION(futureCard, card, futurePosition, position) =>
        if(futureCard.nonEmpty || futurePosition.nonEmpty)
          FutureCardPlayWithPosition(card.toInt, position.toInt, futureCard.nonEmpty, futurePosition.nonEmpty)
        else
          CardPlayWithPosition(card.toInt, position.toInt)

      case CARD_PLAY_WITH_FRIENDLY_TARGET_WITH_POSITION(futureCard, card, futurePosition, position, futureTarget, friendlyTarget) =>
        if(futureCard.nonEmpty || futurePosition.nonEmpty || futureTarget.nonEmpty)
          FutureCardPlayWithFriendlyTargetWithPosition(card.toInt, friendlyTarget.toInt, position.toInt, futureCard.nonEmpty, futureTarget.nonEmpty, futurePosition.nonEmpty)
        else
          CardPlayWithFriendlyTargetWithPosition(card.toInt, friendlyTarget.toInt, position.toInt)

      case REVERSE_CARD_PLAY_WITH_FRIENDLY_TARGET_WITH_POSITION(futureCard, card, futureTarget, friendlyTarget, futurePosition, position) =>
        if(futureCard.nonEmpty || futurePosition.nonEmpty || futureTarget.nonEmpty)
          FutureCardPlayWithFriendlyTargetWithPosition(card.toInt, friendlyTarget.toInt, position.toInt, futureCard.nonEmpty, futureTarget.nonEmpty, futurePosition.nonEmpty)
        else
          CardPlayWithFriendlyTargetWithPosition(card.toInt, friendlyTarget.toInt, position.toInt)

      case CARD_PLAY_WITH_ENEMY_TARGET_WITH_POSITION(futureCard, card, futurePosition, position, futureTarget, enemyTarget) =>
        if(futureCard.nonEmpty || futurePosition.nonEmpty || futureTarget.nonEmpty)
          FutureCardPlayWithEnemyTargetWithPosition(card.toInt, enemyTarget.toInt, position.toInt, futureCard.nonEmpty, futureTarget.nonEmpty, futurePosition.nonEmpty)
        else
          CardPlayWithEnemyTargetWithPosition(card.toInt, enemyTarget.toInt, position.toInt)

      case REVERSE_CARD_PLAY_WITH_ENEMY_TARGET_WITH_POSITION(futureCard, card, futureTarget, enemyTarget, futurePosition, position) =>
        if(futureCard.nonEmpty || futurePosition.nonEmpty || futureTarget.nonEmpty)
          FutureCardPlayWithEnemyTargetWithPosition(card.toInt, enemyTarget.toInt, position.toInt, futureCard.nonEmpty, futureTarget.nonEmpty, futurePosition.nonEmpty)
        else
          CardPlayWithEnemyTargetWithPosition(card.toInt, enemyTarget.toInt, position.toInt)

      case CARD_PLAY_WITH_FRIENDLY_TARGET(futureCard, card, futureTarget, friendlyTarget) =>
        if(futureCard.nonEmpty || futureTarget.nonEmpty)
          FutureCardPlayWithFriendlyTarget(card.toInt, friendlyTarget.toInt, futureCard.nonEmpty, futureTarget.nonEmpty)
        else
          CardPlayWithFriendlyTarget(card.toInt, friendlyTarget.toInt)

      case CARD_PLAY_WITH_ENEMY_TARGET(futureCard, card, futureTarget, enemyTarget) =>
        if(futureCard.nonEmpty || futureTarget.nonEmpty)
          FutureCardPlayWithEnemyTarget(card.toInt, enemyTarget.toInt, futureCard.nonEmpty, futureTarget.nonEmpty)
        else
          CardPlayWithEnemyTarget(card.toInt, enemyTarget.toInt)

      case NORMAL_ATTACK_COMMAND(futureFriendlyTarget, friendlyTarget, futureEnemyTarget, enemyTarget) =>
        if(futureEnemyTarget.nonEmpty || futureFriendlyTarget.nonEmpty)
          FutureNormalAttack(friendlyTarget.toInt, enemyTarget.toInt, futureFriendlyTarget.nonEmpty, futureEnemyTarget.nonEmpty)
        else
          NormalAttack(friendlyTarget.toInt, enemyTarget.toInt)

      case HERO_POWER() =>
        new HeroPower()

      case HERO_POWER_WITH_FRIENDLY_TARGET(futureTarget, friendlyTarget) =>
        if(futureTarget.nonEmpty)
          FutureHeroPowerWithFriendlyTarget(friendlyTarget.toInt)
        else
          HeroPowerWithFriendlyTarget(friendlyTarget.toInt)

      case HERO_POWER_WITH_ENEMY_TARGET(futureTarget, enemyTarget) =>
        if(futureTarget.nonEmpty)
          FutureHeroPowerWithEnemyTarget(enemyTarget.toInt)
        else
          HeroPowerWithEnemyTarget(enemyTarget.toInt)

      case DISCOVER(card) =>
        new Discover(card.toInt)

      case END_TURN() =>
        new EndTurn()

      case END_TURN_SHORTHAND() =>
        new EndTurn()

      case HURRY =>
        Hurry()

      case "last" =>
        RemoveLastVote()

      case "all" =>
        RemoveAllVotes()

      case REMOVE_VOTE(vote) =>
        val voteToRemove = createActionVote(sender, vote)
        if (voteToRemove != ActionUninit())
          RemoveVote(voteToRemove)
        else
          ActionUninit()

      case _ =>
        new ActionUninit()
    }
  }


  def parseMulligan(sender: String, cards: String): ActionVote = {
    val ONE = """(\d)""".r
    val TWO = """(\d),(\d)""".r
    val THREE = """(\d),(\d),(\d)""".r
    val FOUR = """(\d),(\d),(\d),(\d)""".r

    var firstCard = false
    var secondCard = false
    var thirdCard = false
    var fourthCard = false

    def chooseMulligan(number: String): Unit = {
      number match {
        case "1" => firstCard = true
        case "2" => secondCard = true
        case "3" => thirdCard = true
        case "4" => fourthCard = true
      }
    }


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
