package tph


import VoteSystem.{ActionVote, EmojiVote, Vote, MenuVote}

import scala.collection.mutable.ListBuffer

/**
  * Created by Harambe on 10/29/2016.
  */
object Constants {

  val INT_UNINIT = 500
  val STRING_UNINIT = "Constant Uninitialized"
  val booleanToIntMap = Map[Boolean, Int](true -> 1, false -> 0)


  object TestConstants{
    val defaultGameState = new GameState(new Player(1, 0, hand = List(
            new Card("Friendly Hand 1", 1, 1, Constants.INT_UNINIT, 1),
            new Card("Friendly Hand 2", 2, 2, Constants.INT_UNINIT, 1),
            new Card("Friendly Hand 3", 3, 3, Constants.INT_UNINIT, 1),
            new Card("Friendly Hand 4", 4, 4, Constants.INT_UNINIT, 1),
            new Card("Friendly Hand 5", 5, 5, Constants.INT_UNINIT, 1),
            new Card("Friendly Hand 6", 6, 6, Constants.INT_UNINIT, 1)), board = List(
            new Card("Friendly Board 1", 11, Constants.INT_UNINIT, 1, 1),
            new Card("Friendly Board 2", 12, Constants.INT_UNINIT, 2, 1),
            new Card("Friendly Board 3", 13, Constants.INT_UNINIT, 3, 1),
            new Card("Friendly Board 4", 14, Constants.INT_UNINIT, 4, 1))),
      new Player(2, 0, hand = List(
                new Card("Enemy Hand 1", 21, 1, Constants.INT_UNINIT, 2),
                new Card("Enemy Hand 2", 22, 2, Constants.INT_UNINIT, 2),
                new Card("Enemy Hand 3", 23, 3, Constants.INT_UNINIT, 2),
                new Card("Enemy Hand 4", 24, 4, Constants.INT_UNINIT, 2),
                new Card("Enemy Hand 5", 25, 5, Constants.INT_UNINIT, 2),
                new Card("Enemy Hand 6", 26, 6, Constants.INT_UNINIT, 2)), board = List(
                new Card("Enemy Board 1", 31, Constants.INT_UNINIT, 1, 2),
                new Card("Enemy Board 2", 32, Constants.INT_UNINIT, 2, 2),
                new Card("Enemy Board 3", 33, Constants.INT_UNINIT, 3, 2),
                new Card("Enemy Board 4", 34, Constants.INT_UNINIT, 4, 2))))
  }

  object InfluenceFactors{
    val previousDecisionBonus = .5



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




  object ActionVotes {

    //Discover Type
    case class Discover(card: Int) extends ActionVote()
    //Battlecry Option with Position Type
    case class CardPlayWithFriendlyTargetWithPosition(card: Int, friendlyTarget: Int, position: Int) extends ActionVote()
    case class CardPlayWithEnemyTargetWithPosition(card: Int, enemyTarget: Int, position: Int) extends ActionVote()
    //Normal Turn Play Type
    case class CardPlayWithPosition(card: Int, position: Int) extends ActionVote()
    case class CardPlay(card: Int) extends ActionVote()
    case class CardPlayWithFriendlyTarget(card: Int, friendlyTarget: Int) extends ActionVote()
    case class CardPlayWithEnemyTarget(card: Int, enemyTarget: Int) extends ActionVote()
    case class HeroPower() extends ActionVote()
    case class HeroPowerWithFriendlyTarget(friendlyTarget: Int) extends ActionVote()
    case class HeroPowerWithEnemyTarget(enemyTarget: Int) extends ActionVote()
    //3 part commands

    //Attack Type
    case class NormalAttack(friendlyTarget: Int, enemyTarget: Int) extends ActionVote()
    case class ActionUninit() extends ActionVote()
    case class Bind() extends ActionVote()
    case class Future() extends ActionVote()
    case class MulliganVote(first: Boolean, second: Boolean, third: Boolean, fourth: Boolean) extends ActionVote()
  }

  object MiscVotes {

    case class UninitVote() extends Vote()
    case class Hurry() extends Vote()
    case class EndTurn() extends Vote()
    case class Uninit() extends Vote()
  }

  object EmojiVotes {

    case class Greetings() extends EmojiVote()
    case class Thanks() extends EmojiVote()
    case class WellPlayed() extends EmojiVote()
    case class Wow() extends EmojiVote()
    case class Oops() extends EmojiVote()
    case class Threaten() extends EmojiVote()
    case class EmojiUninit() extends EmojiVote()
  }


  object MenuVotes {

    case class MenuUninit() extends MenuVote()
    case class Back() extends MenuVote()
    case class Play() extends MenuVote()
    case class Collection() extends MenuVote()
    case class Shop() extends MenuVote()
    case class OpenPacks() extends MenuVote()
    case class QuestLog() extends MenuVote()
    case class Casual() extends MenuVote()
    case class Ranked() extends MenuVote()
    case class Deck(deckNumber: Int) extends MenuVote()
    case class FirstPage() extends MenuVote()
    case class SecondPage() extends MenuVote()
    case class Quest(number: Int) extends MenuVote()
  }




  object LogFileReaderStrings {

    object HSActionStrings{

      //Friendly HSActions
      val FRIENDLY_MINION_CONTROLLED = """^.+\[name=(.+) id=(\d+) zone=PLAY zonePos=(\d+) .+ zone from FRIENDLY PLAY -> OPPOSING PLAY""".r
      val FRIENDLY_CARD_DRAWN = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - processing index=\d+ change=powerTask=\[power=\[type=TAG_CHANGE entity=\[id=\d+ cardId=.+ name=.+\] tag=ZONE_POSITION value=\d+\] complete=False\] entity=\[name=(.+) id=(\d+) zone=HAND zonePos=\d+ cardId=.+ player=(\d+)\] srcZoneTag=INVALID srcPos= dstZoneTag=INVALID dstPos=(\d+)""".r
      val FRIENDLY_CARD_RETURN = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - id=.+ local=.+ \[name=(.+) id=(\d+) zone=HAND zonePos=.+ cardId=.+ player=(\d+)\] zone from FRIENDLY PLAY -> FRIENDLY HAND""".r
      val FRIENDLY_MULLIGAN_REDRAW = """\[Power\] PowerTaskList.DebugPrintPower\(\) -     HIDE_ENTITY - Entity=\[name=(.+) id=(\d+) zone=HAND zonePos=(\d+) cardId=.+ player=(\d+)] tag=ZONE value=DECK""".r
      val FRIENDLY_CARD_CREATED = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - processing index=\d+ change=powerTask=\[power=\[type=FULL_ENTITY entity=\[id=\d+ cardId=.+ name=.+\] tags=System.Collections.Generic.List\`1\[Network\+Entity\+Tag\]\] complete=False\] entity=\[name=(.+) id=(\d+) zone=HAND zonePos=\d+ cardId=.+ player=(\d+)\] srcZoneTag=INVALID srcPos= dstZoneTag=HAND dstPos=(\d+)""".r

      //Enemy HSActions
      val ENEMY_MINION_CONTROLLED = """^.+\[name=(.+) id=(\d+) zone=PLAY zonePos=(\d+) .+ zone from OPPOSING PLAY -> FRIENDLY PLAY""".r
      val ENEMY_CARD_DRAWN = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - processing index=\d+ change=powerTask=\[power=\[type=TAG_CHANGE entity=\[id=\d+ cardId= name=UNKNOWN ENTITY \[cardType=INVALID\]\] tag=ZONE_POSITION value=\d+\] complete=False\] entity=\[name=UNKNOWN ENTITY \[cardType=INVALID\] id=(\d+) zone=HAND zonePos=\d+ cardId= player=(\d+)\] srcZoneTag=INVALID srcPos= dstZoneTag=INVALID dstPos=(\d+)""".r
      val ENEMY_MULLIGAN_REDRAW = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - id=\d+ local=False \[name=UNKNOWN ENTITY \[cardType=INVALID\] id=(\d+) zone=DECK zonePos=\d+ cardId= player=(\d+)\] zone from OPPOSING HAND -> OPPOSING DECK""".r
      val ENEMY_CARD_RETURN = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - id=.+ local=.+ \[name=(.+) id=(\d+) zone=HAND zonePos=.+ cardId=.+ player=(\d+)\] zone from OPPOSING PLAY -> OPPOSING HAND""".r
      val KNOWN_ENEMY_CARD_PLAYED = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - processing index=\d+ change=powerTask=\[power=\[type=TAG_CHANGE entity=\[id=\d+ cardId=.+ name=.+\] tag=JUST_PLAYED value=1\] complete=False\] entity=\[name=(.+) id=(\d+) zone=PLAY zonePos=(\d+) cardId=.+ player=(\d+)\] srcZoneTag=INVALID srcPos= dstZoneTag=INVALID dstPos=""".r
      val ENEMY_CARD_CREATED = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - processing index=\d+ change=powerTask=\[power=\[type=.+ entity=\[id=\d+ cardId= name=UNKNOWN ENTITY \[cardType=INVALID\]\] tag.+ complete=False\] entity=\[name=UNKNOWN ENTITY \[cardType=INVALID\] id=(\d+) zone=HAND zonePos=\d+ cardId= player=(\d+)\] srcZoneTag=INVALID srcPos= dstZoneTag=HAND dstPos=(\d+)""".r


      //Neutral HSActions
      val CARD_DRAWN = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - processing index=\d+ change=powerTask=\[power=\[type=TAG_CHANGE entity=\[id=\d+ cardId=.+ name=.+\] tag=ZONE_POSITION value=\d+\] complete=False\] entity=\[name=(.+) id=(\d+) zone=HAND zonePos=\d+ cardId=.+ player=(\d+)\] srcZoneTag=INVALID srcPos= dstZoneTag=INVALID dstPos=(\d+)""".r
      val FACE_ATTACK_VALUE = """\[Power\] PowerTaskList.DebugPrintPower\(\) -     TAG_CHANGE Entity=\[name=.+ id=\d+ zone=PLAY zonePos=0 cardId=HERO.+ player=(\d+)] tag=ATK value=(\d+)""".r
      val SECRET_PLAYED = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - TRANSITIONING card .+id=(\d+).+zone=SECRET zonePos=\d+.+player=(\d+)\] to .+ SECRET""".r
      val RETURNED_CARD_PLAYED = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - processing index=\d+ change=powerTask=\[power=\[type=TAG_CHANGE entity=\[id=\d+ cardId=.+ name=.+\] tag=COST value=\d+\] complete=False\] entity=\[name=(.+) id=(\d+) zone=PLAY zonePos=(\d+) cardId=.+ player=(\d+)\] srcZoneTag=INVALID srcPos= dstZoneTag=INVALID dstPos=""".r
      val CARD_PLAYED = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - processing index=\d+ change=powerTask=\[power=\[type=TAG_CHANGE entity=\[id=\d+ cardId=.+ name=.+\] tag=JUST_PLAYED value=1\] complete=False\] entity=\[name=(.+) id=(\d+) zone=PLAY zonePos=(\d+) cardId=.+ player=(\d+)\] srcZoneTag=INVALID srcPos= dstZoneTag=INVALID dstPos=""".r
      val CARD_DEATH = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - TRANSITIONING card \[name=(.+) id=(\d+) zone=GRAVEYARD zonePos=\d+ cardId=.+ player=(\d+)] to .+ GRAVEYARD""".r
      val MINION_SUMMONED = """^.+FULL_ENTITY - Updating \[name=(.+) id=(\d+) zone=PLAY zonePos=(\d+).+player=(\d+).+""".r
      val TRANSFORM ="""\[Power\] PowerTaskList.DebugPrintPower\(\) -     TAG_CHANGE Entity=\[name=.+ id=(\d+) zone=PLAY zonePos=(\d+) cardId=.+ player=\d+] tag=LINKED_ENTITY value=(\d+)""".r
      val WEAPON = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - TRANSITIONING card \[name=.+ id=(\d+) zone=PLAY zonePos=0 cardId=.+ player=(\d+)] to .+ PLAY \(Weapon\)""".r
      val DEFINE_PLAYERS = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - TRANSITIONING card \[name=.+ id=.+ zone=PLAY zonePos=0 cardId=.+ player=(\d+)\] to FRIENDLY PLAY \(Hero\)""".r
      val GAME_OVER = "[Power] GameState.DebugPrintPower() -     TAG_CHANGE Entity=GameEntity tag=NEXT_STEP value=FINAL_GAMEOVER"
      val DECK_TO_BOARD = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - id=\d+ local=False \[name=(.+) id=(\d+) zone=PLAY zonePos=0 cardId=.+ player=(\d+)\] zone from .+ DECK -> .+ PLAY""".r
      val BOARD_SETASIDE_REMOVAL = """\[Power\] PowerProcessor.DoTaskListForCard\(\) - unhandled BlockType PLAY for sourceEntity \[name=(.+) id=(\d+) zone=SETASIDE zonePos=0 cardId=.+ player=(\d+)\]""".r
      val MODIFIED_HERO_FACE_VALUE = """\[Power\] PowerTaskList.DebugPrintPower\(\) -     TAG_CHANGE Entity=\[name=.+ id=\d+ zone=PLAY zonePos=0 cardId=.+ player=(\d+)\] tag=ATK value=(\d+)""".r
      val HAND_SETASIDE_REMOVAL = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - id=\d+ local=False \[name=.+ id=(\d+) zone=SETASIDE zonePos=\d+ cardId= player=(\d+)] zone from .+ HAND -> """.r
    }

    object GameStateStrings{


      val MULLIGAN_START = "[Power] PowerTaskList.DebugPrintPower() -     TAG_CHANGE Entity=GameEntity tag=STEP value=BEGIN_MULLIGAN"
      val MULLIGAN_END = """\[Power\] PowerTaskList.DebugPrintPower\(\) -     TAG_CHANGE Entity=.+ tag=MULLIGAN_STATE value=DONE""".r
      val MULLIGAN_OPTION = """\[Power\] PowerTaskList.DebugPrintPower\(\) -     SHOW_ENTITY - Updating Entity=\[name=UNKNOWN ENTITY \[cardType=INVALID\] id=\d+ zone=DECK zonePos=0 cardId= player=\d+\] CardID=.+""".r
      val DISCOVER_OPTION = """\[Power\] GameState.DebugPrintEntityChoices\(\) -   Entities\[(\d+)\]=\[name=.+ id=\d+ zone=SETASIDE zonePos=0 cardId=.+ player=\d+\]""".r
      val TURN_START = """\[Power\] PowerTaskList.DebugPrintPower\(\) -     TAG_CHANGE Entity=(.+) tag=CURRENT_PLAYER value=1""".r
      val TURN_END = """\[Power\] PowerTaskList.DebugPrintPower\(\) -     TAG_CHANGE Entity=(.+) tag=CURRENT_PLAYER value=0""".r
    }

    object MiscStrings {
      val ENEMY_CARD_RETURN = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - id=.+ local=.+ \[name=(.+) id=(\d+) zone=HAND zonePos=.+ cardId=.+ player=(\d+)\] zone from OPPOSING PLAY -> OPPOSING HAND""".r
      val OLD_ZONE_CHANGE = """^\[Power\] PowerTaskList.+TAG_CHANGE Entity=.+id=(\d+).+zone=(.+) zonePos=.+ player=(\d+)\] tag=ZONE value=(.+)""".r
      val ZONE_CHANGE = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - id=\d+ local=False .+id=(\d+) zone=.+ zonePos=\d+ cardId=.+ player=(\d+)\] zone from (.+) -> (.+)$""".r
      val BOARD_POSITION_CHANGE = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - processing index=\d+ change=powerTask=\[power=\[type=TAG_CHANGE entity=\[id=\d+ cardId=.+ name=.+\] tag=ZONE_POSITION value=\d+\] complete=False\] entity=\[name=.+ id=(\d+) zone=PLAY zonePos=\d+ cardId=.+ player=(\d+)] srcZoneTag=INVALID srcPos= dstZoneTag=INVALID dstPos=(\d+)""".r
      val HAND_POSITION_CHANGE = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - processing index=\d+ change=powerTask=\[power=\[type=TAG_CHANGE entity=\[id=\d+ cardId=.+name=.+tag=ZONE_POSITION value=\d+\] complete=False\] entity=.+id=(\d+).+zone=HAND zonePos=(\d+).+player=(\d+).+dstPos=(\d+)""".r
    }
  }
}
