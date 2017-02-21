package tph


import scala.collection.mutable.ListBuffer

/**
  * Created by Harambe on 10/29/2016.
  */
object Constants {

  val INT_UNINIT = -5
  val STRING_UNINIT = "Constant Uninitialized"
  val booleanToIntMap = Map[Boolean, Int](true -> 1, false -> 0)

  object StateSignatures {

    val inGameSignature = "In Game"
    val inMenuSignature = "In Menu"
    val initSignature = "Initializing"
    val hisTurnSignature = "His Turn"
    val myTurnSignature = "My Turn"
    val inMulliganSignature = "In Mulligan"
  }

  object TestConstants{
    val defaultGameState = new GameState(new Player(1,
      List(
        new Card("Friendly Hand 1", 1, 1, Constants.INT_UNINIT, 1),
        new Card("Friendly Hand 2", 2, 2, Constants.INT_UNINIT, 1),
        new Card("Friendly Hand 3", 3, 3, Constants.INT_UNINIT, 1),
        new Card("Friendly Hand 4", 4, 4, Constants.INT_UNINIT, 1),
        new Card("Friendly Hand 5", 5, 5, Constants.INT_UNINIT, 1),
        new Card("Friendly Hand 6", 6, 6, Constants.INT_UNINIT, 1)),
      List(
        new Card("Friendly Board 1", 11, Constants.INT_UNINIT, 1, 1),
        new Card("Friendly Board 2", 12, Constants.INT_UNINIT, 2, 1),
        new Card("Friendly Board 3", 13, Constants.INT_UNINIT, 3, 1),
        new Card("Friendly Board 4", 14, Constants.INT_UNINIT, 4, 1))),
      new Player(2,
        List(
          new Card("Enemy Hand 1", 21, 1, Constants.INT_UNINIT, 2),
          new Card("Enemy Hand 2", 22, 2, Constants.INT_UNINIT, 2),
          new Card("Enemy Hand 3", 23, 3, Constants.INT_UNINIT, 2),
          new Card("Enemy Hand 4", 24, 4, Constants.INT_UNINIT, 2),
          new Card("Enemy Hand 5", 25, 5, Constants.INT_UNINIT, 2),
          new Card("Enemy Hand 6", 26, 6, Constants.INT_UNINIT, 2)),
        List(
          new Card("Enemy Board 1", 31, Constants.INT_UNINIT, 1, 2),
          new Card("Enemy Board 2", 32, Constants.INT_UNINIT, 2, 2),
          new Card("Enemy Board 3", 33, Constants.INT_UNINIT, 3, 2),
          new Card("Enemy Board 4", 34, Constants.INT_UNINIT, 4, 2))))




  }


  object FunctionalConstants {

    def RepeatFunction[A](f: A => A, startingParameter: A, numberOfRepeats: Int, currentIteration:Int = 0): A = {
      if (currentIteration < numberOfRepeats) {
        RepeatFunction(f, f(startingParameter), numberOfRepeats, currentIteration +1)
      }
      else {
        println("End function result is: " + f(startingParameter))
        f(startingParameter)
      }
    }

    def UnwrapMultiSome(option: Option[_]): Option[_] = {
      option match {
        case Some(x) =>
          x match {
            case Some(someOption: Option[_]) =>
              println("Unwrapping once")
              UnwrapMultiSome(someOption)

            case trueValue =>
              println("Returning Some(trueValue)" + trueValue)
              Some(trueValue)

            case None =>
              println("This should never happen. Returning None value for UnwrapMultiSome")
              None
          }
        case None => println("This should never happen. Returning None value for UnwrapMultiSome")
          None
      }
    }
  }


  object MenuNames {

    val MAIN_MENU = "Main Menu"
    val QUEST_MENU = "Quest Menu"
    val IN_GAME = "In Game"
    val COLLECTION_MENU = "Collection Menu"
    val PLAY_MENU = "Play Menu"
    val ARENA_MENU = "Arena Menu"
    val DECK_CREATION = "Deck Creation"
    val SHOP_MENU = "Shop Menu"
    val OPEN_PACKS_MENU = "Open Packs Menu"
  }

  case class UninitVoteCode() extends VoteCode


  sealed trait VoteCode {}

  object CommandMessages {


    case class ChangeReaderFile(fileName: String)

    //case class CommandVote(builtCommand: Vote)

    case class ChangeMenu(previousMenu: String, changeToMenu: String)

    case class GetGameStatus()

  }


  object ActionVoteCodes {

    sealed trait ActionVoteCode extends VoteCode {

      var positionVote = Constants.INT_UNINIT
      var cardVote = Constants.INT_UNINIT
      var friendlyTargetVote = Constants.INT_UNINIT
      var enemyTargetVote = Constants.INT_UNINIT
    }


    //Discover Type
    case class Discover(card: Int) extends ActionVoteCode {
      cardVote = card
    }

    // Battlecry Option Type
    case class CardPlayWithFriendlyOption(card: Int, friendlyTarget: Int) extends ActionVoteCode {
      cardVote = card
      friendlyTargetVote = friendlyTarget
    }

    case class CardPlayWithFriendlyFaceOption(card: Int) extends ActionVoteCode {
      cardVote = card
    }

    case class CardPlayWithEnemyOption(card: Int, enemyTarget: Int) extends ActionVoteCode {
      cardVote = card
      enemyTargetVote = enemyTarget
    }

    case class CardPlayWithEnemyFaceOption(card: Int) extends ActionVoteCode {
      cardVote = card
    }

    //Battlecry Option with Position Type
    case class CardPlayWithFriendlyOptionWithPosition(card: Int, friendlyTarget: Int, position: Int) extends ActionVoteCode {
      cardVote = card
      friendlyTargetVote = friendlyTarget
      positionVote = position
    }

    case class CardPlayWithFriendlyFaceOptionWithPosition(card: Int, position: Int) extends ActionVoteCode {
      cardVote = card
      positionVote = position
    }

    case class CardPlayWithEnemyOptionWithPosition(card: Int, enemyTarget: Int, position: Int) extends ActionVoteCode {
      cardVote = card
      enemyTargetVote = enemyTarget
      positionVote = position
    }

    case class CardPlayWithEnemyFaceOptionWithPosition(card: Int, position: Int) extends ActionVoteCode {
      cardVote = card
      positionVote = position
    }


    //Normal Turn Play Type
    case class CardPlay(cardFoo: Int) extends ActionVoteCode {
      cardVote = cardFoo
    }

    case class CardPlayWithPosition(card: Int, position: Int) extends ActionVoteCode {
      cardVote = card
      positionVote = position
    }

    case class CardPlayWithFriendlyBoardTarget(card: Int, friendlyTarget: Int) extends ActionVoteCode {
      cardVote = card
      friendlyTargetVote = friendlyTarget
    }

    case class CardPlayWithEnemyBoardTarget(card: Int, enemyTarget: Int) extends ActionVoteCode {
      cardVote = card
      enemyTargetVote = enemyTarget
    }

    case class CardPlayWithFriendlyFaceTarget(card: Int) extends ActionVoteCode {
      cardVote = card
    }

    case class CardPlayWithEnemyFaceTarget(card: Int) extends ActionVoteCode {
      cardVote = card
    }

    case class HeroPower() extends ActionVoteCode

    case class HeroPowerWithFriendlyTarget(friendlyTarget: Int) extends ActionVoteCode {
      friendlyTargetVote = friendlyTarget
    }

    case class HeroPowerWithEnemyTarget(enemyTarget: Int) extends ActionVoteCode {
      enemyTargetVote = enemyTarget
    }

    case class HeroPowerWithFriendlyFace() extends ActionVoteCode

    case class HeroPowerWithEnemyFace() extends ActionVoteCode


    //Attack Type
    case class NormalAttack(friendlyTarget: Int, enemyTarget: Int) extends ActionVoteCode {
      positionVote = friendlyTarget
      enemyTargetVote = enemyTarget
    }

    case class FaceAttack(position: Int) extends ActionVoteCode {
      positionVote = position
    }

    case class NormalAttackToFace(position: Int) extends ActionVoteCode {
      positionVote = position
    }

    case class FaceAttackToFace() extends ActionVoteCode

    case class ActionUninit() extends ActionVoteCode

    case class Bind() extends ActionVoteCode

    case class Future() extends ActionVoteCode

    case class MulliganVote(first: Boolean, second: Boolean, third: Boolean, fourth: Boolean) extends VoteCode


  }

  object MiscVoteCodes {


    case class Hurry() extends VoteCode

    // Probably removing Concede
    // case class Concede(decision: Boolean) extends VoteCode

    case class EndTurn() extends VoteCode

    case class Uninit() extends VoteCode

  }

  object EmojiVoteCodes {

    //Emote Type

    sealed trait EmojiVoteCode extends VoteCode {

    }

    case class Greetings() extends EmojiVoteCode

    case class Thanks() extends EmojiVoteCode

    case class WellPlayed() extends EmojiVoteCode

    case class Wow() extends EmojiVoteCode

    case class Oops() extends EmojiVoteCode

    case class Threaten() extends EmojiVoteCode

    case class EmojiUninit() extends EmojiVoteCode

  }


  object MenuVoteCodes {


    sealed trait MenuVoteCode extends VoteCode {
      def menu: String

      var numberOption = Constants.INT_UNINIT
    }


    //MultiMenu

    case class MenuUninit() extends MenuVoteCode {
      val menu = "Uninit"
    }


    case class Back(currentMenu: String) extends MenuVoteCode {
      val menu = currentMenu
    }


    case class Play(currentMenu: String) extends MenuVoteCode {
      val menu = currentMenu
    }

    case class Collection(currentMenu: String) extends MenuVoteCode {
      val menu = currentMenu
    }


    //Main Menu

    case class Shop(currentMenu: String) extends MenuVoteCode {
      val menu = currentMenu
    }

    case class OpenPacks(currentMenu: String) extends MenuVoteCode {
      val menu = currentMenu
    }

    case class QuestLog(currentMenu: String) extends MenuVoteCode {
      val menu = currentMenu
    }

    //Play Menu

    case class Casual(currentMenu: String) extends MenuVoteCode {
      val menu = currentMenu
    }

    case class Ranked(currentMenu: String) extends MenuVoteCode {
      val menu = currentMenu
    }

    case class Deck(deckNumber: Int, currentMenu: String) extends MenuVoteCode {
      val menu = currentMenu
      numberOption = deckNumber

    }

    case class FirstPage(currentMenu: String) extends MenuVoteCode {
      val menu = currentMenu
    }

    case class SecondPage(currentMenu: String) extends MenuVoteCode {
      val menu = currentMenu
    }

    //Quest Menu

    case class Quest(number: Int, currentMenu: String) extends MenuVoteCode {
      numberOption = number
      val menu = currentMenu
    }

  }

  object CommandVotes {

    val DISCOVER = "Discover"

    val CARD_PLAY_WITH_FRIENDLY_OPTION = "CardPlayWithFriendlyOption"
    val CARD_PLAY_WITH_FRIENDLY_FACE_OPTION = "CardPlayWithFriendlyFaceOption"
    val CARD_PLAY_WITH_ENEMY_OPTION = "CardPlayWithEnemyOption"
    val CARD_PLAY_WITH_ENEMY_FACE_OPTION = "CardPlayWithEnemyFaceOption"


    //Battlecry and Position Type
    val CARD_PLAY_WITH_FRIENDLY_OPTION_WITH_POSITION = "CardPlayWithFriendlyOptionWithPosition"
    val CARD_PLAY_WITH_FRIENDLY_FACE_OPTION_WITH_POSITION = "CardPlayWithFriendlyFaceOptionWithPosition"
    val CARD_PLAY_WITH_ENEMY_OPTION_WITH_POSITION = "CardPlayWithEnemyOptionWithPosition"
    val CARD_PLAY_WITH_ENEMY_FACE_OPTION_WITH_POSITION = "CardPlayWithEnemyFaceOptionWithPosition"


    //Normal Turn Play Type
    val CARD_PLAY = "CardPlay"
    val CARD_PLAY_WITH_POSITION = "CardPlayWithPosition"
    val CARD_PLAY_WITH_FRIENDLY_TARGET = "CardPlayWithFriendlyTarget"
    val CARD_PLAY_WITH_ENEMY_TARGET = "CardPlayWithEnemyTarget"
    val CARD_PLAY_WITH_FRIENDLY_FACE_TARGET = "CardPlayWithFriendlyFaceTarget"
    val CARD_PLAY_WITH_ENEMY_FACE_TARGET = "CardPlayWithEnemyFaceTarget"


    //Hero Power Type
    val HERO_POWER = "HeroPower"
    val HERO_POWER_WITH_ENEMY_FACE = "HeroPowerWithEnemyFace"
    val HERO_POWER_WITH_ENEMY_TARGET = "HeroPowerWithEnemyTarget"
    val HERO_POWER_WITH_FRIENDLY_FACE = "HeroPowerWithFriendlyFace"
    val HERO_POWER_WITH_FRIENDLY_TARGET = "HeroPowerWithFriendlyTarget"


    //Attack Type
    val NORMAL_ATTACK_WITH_ENEMY_TARGET = "NormalAttackWithEnemyTarget"
    val NORMAL_ATTACK_WITH_ENEMY_FACE_TARGET = "NormalAttackWithEnemyFaceTarget"
    val FACE_ATTACK_WITH_ENEMY_TARGET = "FaceAttackWithEnemyTarget"
    val FACE_ATTACK_WITH_ENEMY_FACE_TARGET = "FaceAttackWithEnemyFaceTarget"

  }


}
