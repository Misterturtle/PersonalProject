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

//    def RepeatFunction[A](f: A => A, startingParameter: A, numberOfRepeats: Int, currentIteration:Int = 0): A = {
//      if (currentIteration < numberOfRepeats) {
//        RepeatFunction(f, f(startingParameter), numberOfRepeats, currentIteration +1)
//      }
//      else {
//        println("End function result is: " + f(startingParameter))
//        f(startingParameter)
//      }
//    }
//
//    def UnwrapMultiSome(option: Option[_]): Option[_] = {
//      option match {
//        case Some(x) =>
//          x match {
//            case Some(someOption: Option[_]) =>
//              println("Unwrapping once")
//              UnwrapMultiSome(someOption)
//
//            case trueValue =>
//              println("Returning Some(trueValue)" + trueValue)
//              Some(trueValue)
//
//            case None =>
//              println("This should never happen. Returning None value for UnwrapMultiSome")
//              None
//          }
//        case None => println("This should never happen. Returning None value for UnwrapMultiSome")
//          None
//      }
//    }
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

  object LogFileReaderStrings {

    val FRIENDLY_MINION_CONTROLLED =
      """^.+\[name=(.+) id=(\d+) zone=PLAY zonePos=(\d+) .+ zone from FRIENDLY PLAY -> OPPOSING PLAY""".r

    //Enemy Strings
    val ENEMY_MINION_CONTROLLED =
      """^.+\[name=(.+) id=(\d+) zone=PLAY zonePos=(\d+) .+ zone from OPPOSING PLAY -> FRIENDLY PLAY""".r
    val ENEMY_CARD_DRAWN = """^.+id=\d+ local=.+ \[id=(\d+) cardId=.+type=.+zone=HAND zonePos=(\d+) player=(\d+)\] pos from \d+ -> \d+""".r
    val ENEMY_CARD_RETURN = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - id=.+ local=.+ \[name=(.+) id=(\d+) zone=HAND zonePos=.+ cardId=.+ player=(\d+)\] zone from OPPOSING PLAY -> OPPOSING HAND""".r

    //Neutral Strings
    val FACE_ATTACK_VALUE =
      """\[Power\] PowerTaskList.DebugPrintPower\(\) -     TAG_CHANGE Entity=\[name=.+ id=\d+ zone=PLAY zonePos=0 cardId=HERO.+ player=(\d+)] tag=ATK value=(\d+)""".r
    val SECRET_PLAYED = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - TRANSITIONING card .+id=(\d+).+zone=SECRET zonePos=\d+.+player=(\d+)\] to .+ SECRET""".r
    val OLD_ZONE_CHANGE = """^\[Power\] PowerTaskList.+TAG_CHANGE Entity=.+id=(\d+).+zone=(.+) zonePos=.+ player=(\d+)\] tag=ZONE value=(.+)""".r
    val ZONE_CHANGE = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - id=\d+ local=False .+id=(\d+) zone=.+ zonePos=\d+ cardId=.+ player=(\d+)\] zone from (.+) -> (.+)$""".r
    val KNOWN_CARD_DRAWN = """^.+id=\d+ local=False \[name=(.+) id=(\d+) zone=HAND zonePos=(\d+) cardId=.+ player=(\d+)\] pos from \d+ -> \d+""".r
    val CARD_PLAYED = """\[Power\] PowerProcessor.DoTaskListForCard\(\) - unhandled BlockType PLAY for sourceEntity \[name=(.+) id=(\d+) zone=PLAY zonePos=(\d+) cardId=.+ player=(\d+)\]""".r
    val BOARD_POSITION_CHANGE = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - processing index=\d+ change=powerTask=\[power=\[type=TAG_CHANGE entity=\[id=\d+ cardId=.+ name=.+\] tag=ZONE_POSITION value=\d+\] complete=False\] entity=\[name=.+ id=(\d+) zone=PLAY zonePos=\d+ cardId=.+ player=(\d+)] srcZoneTag=INVALID srcPos= dstZoneTag=INVALID dstPos=(\d+)""".r
    val HAND_POSITION_CHANGE = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - processing index=\d+ change=powerTask=\[power=\[type=TAG_CHANGE entity=\[id=\d+ cardId=.+name=.+tag=ZONE_POSITION value=\d+\] complete=False\] entity=.+id=(\d+).+zone=HAND zonePos=(\d+).+player=(\d+).+dstPos=(\d+)""".r
    val CARD_DEATH = """^\[Power\] PowerTaskList.+TAG_CHANGE Entity=\[name=(.+) id=(\d+) zone=.+ zonePos=.+ player=(\d+).+ tag=ZONE value=GRAVEYARD""".r
    val MINION_SUMMONED = """^.+FULL_ENTITY - Updating \[name=(.+) id=(\d+) zone=PLAY zonePos=(\d+).+player=(\d+).+""".r
    val TRANSFORM ="""\[Power\] .+.DebugPrintPower\(\) -     TAG_CHANGE Entity=.+id=(\d+) zone=PLAY.+tag=LINKED_ENTITY value=(\d+)""".r
    val HEX = """\[Zone\] ZoneChangeList.+ \[name=(.+) id=(\d+) zone=PLAY zonePos=.+ cardId=hexfrog player=(\d+)\] pos from 0 -> (\d)""".r
    val DEFINE_PLAYERS = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - TRANSITIONING card \[name=.+ id=.+ zone=PLAY zonePos=0 cardId=.+ player=(\d+)\] to FRIENDLY PLAY \(Hero\)""".r
    val SAP = """\[Power\] PowerTaskList.DebugPrintPower\(\) - BLOCK_START BlockType=POWER Entity=\[name=Sap id=.+ zone=PLAY zonePos=.+ cardId=.+ player=.+\] EffectCardId= EffectIndex=.+ Target=\[name=(.+) id=(\d+) zone=PLAY zonePos=.+ cardId=.+ player=(\d+)\]""".r
    val WEAPON = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - TRANSITIONING card \[name=.+ id=(\d+) zone=PLAY zonePos=0 cardId=.+ player=(\d+)] to .+ PLAY \(Weapon\)""".r

    //IrcLogic Strings
    val MULLIGAN_OPTION =
      """\[Power\] PowerTaskList.DebugPrintPower\(\) -     SHOW_ENTITY - Updating Entity=\[name=UNKNOWN ENTITY \[cardType=INVALID\] id=\d+ zone=DECK zonePos=0 cardId= player=1\] CardID=.+""".r


    //"""\[Zone\] ZoneChangeList.ProcessChanges\(\) - processing index=\d+ change=powerTask=\[power=\[type=SHOW_ENTITY entity=\[id=\d+ cardId=.+ name=\[id=\d+ cardId= type=INVALID zone=DECK zonePos=0 player=\d+\]\] tags=System.Collections.Generic.List`1\[Network\+Entity\+Tag\]\] complete=False\] entity=\[id=\d+ cardId= type=INVALID zone=DECK zonePos=0 player=\d+\] srcZoneTag=INVALID srcPos= dstZoneTag=HAND dstPos=""".r
    val MULLIGAN_START = "[Power] PowerTaskList.DebugPrintPower() -     TAG_CHANGE Entity=GameEntity tag=STEP value=BEGIN_MULLIGAN"
    val DISCOVER_OPTION = """\[Power\] GameState.DebugPrintEntityChoices\(\) -   Entities\[(\d+)\]=\[name=.+ id=\d+ zone=SETASIDE zonePos=0 cardId=.+ player=\d+\]""".r
    val TURN_START = """\[Power\] PowerTaskList.DebugPrintPower\(\) -     TAG_CHANGE Entity=(.+) tag=CURRENT_PLAYER value=1""".r
    val TURN_END = """\[Power\] PowerTaskList.DebugPrintPower\(\) -     TAG_CHANGE Entity=(.+) tag=CURRENT_PLAYER value=0""".r


    //Controller Strings
    val PLAY_MENU = "FSM not Preprocessed: Hero_Armor(Clone) : FSM"
    val COLLECTION_MENU = "FSM not Preprocessed: DeckGlowAll : FSM"
    val START_UP = "FSM not Preprocessed: Startup_Hub : FSM"




  }


}
