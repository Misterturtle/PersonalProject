package tph


import scala.collection.mutable.ListBuffer

/**
  * Created by Harambe on 10/29/2016.
  */
object Constants {


  object StateSignatures {

    val inGameSignature = "In Game"
    val inMenuSignature = "In Menu"
    val initSignature = "Initializing"
    val hisTurnSignature = "His Turn"
    val myTurnSignature = "My Turn"
    val inMulliganSignature = "In Mulligan"
  }


  object MenuNames {

    val MAIN_MENU = "Main Menu"
    val QUEST_MENU = "Quest Menu"
    val IN_GAME = "In Game"
    val COLLECTION_MENU = "Collection Menu"
    val PLAY_MENU = "Play Menu"
    val ARENA_MENU = "Arena Menu"
    val DECK_CREATION = "Deck Creation"

  }



  val UNINIT = -5

  case class UninitVoteCode() extends VoteCode


  sealed trait VoteCode {}

  object CommandMessages {


    case class ChangeReaderFile(fileName: String)

    case class CommandVote(builtCommand: Vote)

    case class ChangeMenu(previousMenu: String, changeToMenu: String)

    case class GetGameStatus()

  }


  object ActionVoteCodes {

    sealed trait ActionVoteCode extends VoteCode {

      var positionVote = Constants.UNINIT
      var cardVote = Constants.UNINIT
      var battleCryVote = Constants.UNINIT
      var targetVote = Constants.UNINIT
    }




    //Discover Type
    case class Discover(card: Int) extends ActionVoteCode {
      cardVote = card
    }

    // Battlecry Option Type
    case class CardPlayWithFriendlyOption(card: Int, boardTarget: Int) extends ActionVoteCode {
      cardVote = card
      targetVote = boardTarget
    }

    case class CardPlayWithFriendlyFaceOption(card: Int) extends ActionVoteCode {
      cardVote = card
    }

    case class CardPlayWithEnemyOption(card: Int, boardTarget: Int) extends ActionVoteCode {
      cardVote = card
      targetVote = boardTarget
    }

    case class CardPlayWithEnemyFaceOption(card: Int) extends ActionVoteCode {
      cardVote = card
    }

    //Battlecry Option with Position Type
    case class CardPlayWithFriendlyOptionWithPosition(card: Int, target: Int, position: Int) extends ActionVoteCode {
      cardVote = card
      targetVote = target
      positionVote = position
    }

    case class CardPlayWithFriendlyFaceOptionWithPosition(card: Int, position: Int) extends ActionVoteCode {
      cardVote = card
      positionVote = position
    }

    case class CardPlayWithEnemyOptionWithPosition(card: Int, target: Int, position: Int) extends ActionVoteCode {
      cardVote = card
      targetVote = target
      positionVote = position
    }

    case class CardPlayWithEnemyFaceOptionWithPosition(card: Int, position: Int) extends ActionVoteCode {
      cardVote = card
      positionVote = position
    }


    //Normal Turn Play Type
    case class CardPlay(card: Int) extends ActionVoteCode {
      cardVote = card
    }

    case class CardPlayWithPosition(card: Int, position: Int) extends ActionVoteCode {
      cardVote = card
      positionVote = position
    }

    case class CardPlayWithFriendlyBoardTarget(card: Int, target: Int) extends ActionVoteCode {
      cardVote = card
      targetVote = target
    }

    case class CardPlayWithEnemyBoardTarget(card: Int, target: Int) extends ActionVoteCode {
      cardVote = card
      targetVote = target
    }

    case class CardPlayWithFriendlyFaceTarget(card: Int) extends ActionVoteCode {
      cardVote = card
    }

    case class CardPlayWithEnemyFaceTarget(card: Int) extends ActionVoteCode {
      cardVote = card
    }

    case class HeroPower() extends ActionVoteCode

    case class HeroPowerWithFriendlyTarget(target: Int) extends ActionVoteCode {
      targetVote = target
    }

    case class HeroPowerWithEnemyTarget(target: Int) extends ActionVoteCode {
      targetVote = target
    }

    case class HeroPowerWithFriendlyFace() extends ActionVoteCode

    case class HeroPowerWithEnemyFace() extends ActionVoteCode


    //Attack Type
    case class NormalAttack(friendlyPosition: Int, enemyPosition: Int) extends ActionVoteCode {
      positionVote = friendlyPosition
      targetVote = enemyPosition
    }

    case class FaceAttack(position: Int) extends ActionVoteCode {
      positionVote = position
    }

    case class NormalAttackToFace(position: Int) extends ActionVoteCode {
      positionVote = position
    }

    case class FaceAttackToFace() extends ActionVoteCode

    case class ActionUninit() extends ActionVoteCode


    //Misc Type
    case class Wait() extends VoteCode

    case class Hurry() extends VoteCode

    case class Concede() extends VoteCode

    case class EndTurn() extends VoteCode

    case class Bind() extends VoteCode

    case class Future() extends VoteCode

    case class MulliganVote(first: Boolean, second: Boolean, third: Boolean, fourth: Boolean) extends VoteCode

    case class PreviousDecision() extends VoteCode

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

      var numberOption = Constants.UNINIT
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


  object CommandVotes{

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
