package tph


import VoteSystem.{ActionVote, EmojiVote, Vote, MenuVote}
import tph.Constants.VoteStringNames._

import scala.collection.mutable.ListBuffer

/**
  * Created by Harambe on 10/29/2016.
  */
object Constants {

  val INT_UNINIT = 500
  val STRING_UNINIT = "Constant Uninitialized"
  val booleanToIntMap = Map[Boolean, Int](true -> 1, false -> 0)


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
    case class Discover(sender:String, card: Int) extends ActionVote(sender)
    //Battlecry Option with Position Type
    case class CardPlayWithFriendlyTargetWithPosition(sender:String, card: Int, friendlyTarget: Int, position: Int) extends ActionVote(sender)
    case class CardPlayWithFriendlyFaceTargetWithPosition(sender:String, card: Int, position: Int) extends ActionVote(sender)
    case class CardPlayWithEnemyTargetWithPosition(sender:String, card: Int, enemyTarget: Int, position: Int) extends ActionVote(sender)
    case class CardPlayWithEnemyFaceTargetWithPosition(sender:String, card: Int, position: Int) extends ActionVote(sender)
    //Normal Turn Play Type
    case class CardPlayWithPosition(sender:String, card: Int, position: Int) extends ActionVote(sender)
    case class CardPlay(sender:String, card: Int) extends ActionVote(sender)
    case class CardPlayWithFriendlyTarget(sender:String, card: Int, friendlyTarget: Int) extends ActionVote(sender)
    case class CardPlayWithEnemyTarget(sender:String, card: Int, enemyTarget: Int) extends ActionVote(sender)
    case class CardPlayWithFriendlyFaceTarget(sender:String, card: Int) extends ActionVote(sender)
    case class CardPlayWithEnemyFaceTarget(sender:String, card: Int) extends ActionVote(sender)
    case class HeroPower(sender:String) extends ActionVote(sender)
    case class HeroPowerWithFriendlyTarget(sender:String, friendlyTarget: Int) extends ActionVote(sender)
    case class HeroPowerWithEnemyTarget(sender:String, enemyTarget: Int) extends ActionVote(sender)
    case class HeroPowerWithFriendlyFace(sender:String) extends ActionVote(sender)
    case class HeroPowerWithEnemyFace(sender:String) extends ActionVote(sender)
    //3 part commands

    //Attack Type
    case class NormalAttack(sender:String, friendlyTarget: Int, enemyTarget: Int) extends ActionVote(sender)
    case class FaceAttack(sender:String, position: Int) extends ActionVote(sender)
    case class NormalAttackToFace(sender:String, position: Int) extends ActionVote (sender)
    case class FaceAttackToFace(sender:String) extends ActionVote(sender)
    case class ActionUninit(sender:String) extends ActionVote(sender)
    case class Bind(sender:String) extends ActionVote(sender)
    case class Future(sender:String) extends ActionVote(sender)
    case class MulliganVote(sender:String, first: Boolean, second: Boolean, third: Boolean, fourth: Boolean) extends Vote(sender)
  }

  object MiscVotes {

    case class UninitVote(sender:String) extends Vote(sender)
    case class Hurry(sender:String) extends Vote(sender)
    case class EndTurn(sender:String) extends Vote(sender)
    case class Uninit(sender:String) extends Vote(sender)
  }

  object EmojiVotes {

    case class Greetings(sender:String) extends EmojiVote(sender)
    case class Thanks(sender:String) extends EmojiVote(sender)
    case class WellPlayed(sender:String) extends EmojiVote(sender)
    case class Wow(sender:String) extends EmojiVote(sender)
    case class Oops(sender:String) extends EmojiVote(sender)
    case class Threaten(sender:String) extends EmojiVote(sender)
    case class EmojiUninit(sender:String) extends EmojiVote(sender)
  }


  object MenuVotes {

    case class MenuUninit(sender:String) extends MenuVote(sender)
    case class Back(sender:String) extends MenuVote(sender)
    case class Play(sender:String) extends MenuVote(sender)
    case class Collection(sender:String) extends MenuVote(sender)
    case class Shop(sender:String) extends MenuVote(sender)
    case class OpenPacks(sender:String) extends MenuVote(sender)
    case class QuestLog(sender:String) extends MenuVote(sender)
    case class Casual(sender:String) extends MenuVote(sender)
    case class Ranked(sender:String) extends MenuVote(sender)
    case class Deck(sender:String, deckNumber: Int) extends MenuVote(sender)
    case class FirstPage(sender:String) extends MenuVote(sender)
    case class SecondPage(sender:String) extends MenuVote(sender)
    case class Quest( sender:String, number: Int) extends MenuVote(sender)
  }

  object VoteStringNames {
    val totalVoteStringNames = 20

    val DISCOVER = "Discover"

    //Battlecry and Position Type
    val CARD_PLAY_WITH_FRIENDLY_TARGET_WITH_POSITION = "CardPlayWithFriendlyTargetWithPosition"
    val CARD_PLAY_WITH_FRIENDLY_FACE_TARGET_WITH_POSITION = "CardPlayWithFriendlyFaceTargetWithPosition"
    val CARD_PLAY_WITH_ENEMY_TARGET_WITH_POSITION = "CardPlayWithEnemyTargetWithPosition"
    val CARD_PLAY_WITH_ENEMY_FACE_TARGET_WITH_POSITION = "CardPlayWithEnemyFaceTargetWithPosition"


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


    val listOfVoteStringNames = List[String](
      DISCOVER,
      CARD_PLAY_WITH_FRIENDLY_TARGET_WITH_POSITION,
      CARD_PLAY_WITH_FRIENDLY_FACE_TARGET_WITH_POSITION,
      CARD_PLAY_WITH_ENEMY_TARGET_WITH_POSITION ,
      CARD_PLAY_WITH_ENEMY_FACE_TARGET_WITH_POSITION,
      CARD_PLAY ,
      CARD_PLAY_WITH_POSITION  ,
      CARD_PLAY_WITH_FRIENDLY_TARGET  ,
      CARD_PLAY_WITH_ENEMY_TARGET ,
      CARD_PLAY_WITH_FRIENDLY_FACE_TARGET,
      CARD_PLAY_WITH_ENEMY_FACE_TARGET  ,
      HERO_POWER ,
      HERO_POWER_WITH_ENEMY_FACE,
      HERO_POWER_WITH_ENEMY_TARGET,
      HERO_POWER_WITH_FRIENDLY_FACE ,
      HERO_POWER_WITH_FRIENDLY_TARGET ,
      NORMAL_ATTACK_WITH_ENEMY_TARGET,
      NORMAL_ATTACK_WITH_ENEMY_FACE_TARGET,
      FACE_ATTACK_WITH_ENEMY_TARGET ,
      FACE_ATTACK_WITH_ENEMY_FACE_TARGET)
  }



  object LogFileReaderStrings {

    object HSActionStrings{
      val FRIENDLY_MINION_CONTROLLED = """^.+\[name=(.+) id=(\d+) zone=PLAY zonePos=(\d+) .+ zone from FRIENDLY PLAY -> OPPOSING PLAY""".r
      val ENEMY_MINION_CONTROLLED = """^.+\[name=(.+) id=(\d+) zone=PLAY zonePos=(\d+) .+ zone from OPPOSING PLAY -> FRIENDLY PLAY""".r
      val ENEMY_CARD_DRAWN = """^.+id=\d+ local=.+ \[id=(\d+) cardId=.+type=.+zone=HAND zonePos=(\d+) player=(\d+)\] pos from .* -> .*""".r
      val FACE_ATTACK_VALUE = """\[Power\] PowerTaskList.DebugPrintPower\(\) -     TAG_CHANGE Entity=\[name=.+ id=\d+ zone=PLAY zonePos=0 cardId=HERO.+ player=(\d+)] tag=ATK value=(\d+)""".r
      val SECRET_PLAYED = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - TRANSITIONING card .+id=(\d+).+zone=SECRET zonePos=\d+.+player=(\d+)\] to .+ SECRET""".r
      val KNOWN_CARD_DRAWN = """^.+id=\d+ local=False \[name=(.+) id=(\d+) zone=HAND zonePos=(\d+) cardId=.+ player=(\d+)\] pos from .* -> .*""".r
      val CARD_PLAYED = """\[Power\] PowerProcessor.DoTaskListForCard\(\) - unhandled BlockType PLAY for sourceEntity \[name=(.+) id=(\d+) zone=PLAY zonePos=(\d+) cardId=.+ player=(\d+)\]""".r
      val CARD_DEATH = """^\[Power\] PowerTaskList.+TAG_CHANGE Entity=\[name=(.+) id=(\d+) zone=.+ zonePos=.+ player=(\d+).+ tag=ZONE value=GRAVEYARD""".r
      val MINION_SUMMONED = """^.+FULL_ENTITY - Updating \[name=(.+) id=(\d+) zone=PLAY zonePos=(\d+).+player=(\d+).+""".r
      val TRANSFORM ="""\[Power\] .+.DebugPrintPower\(\) -     TAG_CHANGE Entity=.+id=(\d+) zone=PLAY.+tag=LINKED_ENTITY value=(\d+)""".r
      val SAP = """\[Power\] PowerTaskList.DebugPrintPower\(\) - BLOCK_START BlockType=POWER Entity=\[name=Sap id=.+ zone=PLAY zonePos=.+ cardId=.+ player=.+\] EffectCardId= EffectIndex=.+ Target=\[name=(.+) id=(\d+) zone=PLAY zonePos=.+ cardId=.+ player=(\d+)\]""".r
      val WEAPON = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - TRANSITIONING card \[name=.+ id=(\d+) zone=PLAY zonePos=0 cardId=.+ player=(\d+)] to .+ PLAY \(Weapon\)""".r
      val DEFINE_PLAYERS = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - TRANSITIONING card \[name=.+ id=.+ zone=PLAY zonePos=0 cardId=.+ player=(\d+)\] to FRIENDLY PLAY \(Hero\)""".r
    }

    object GameStateStrings{

      val MULLIGAN_OPTION = """\[Power\] PowerTaskList.DebugPrintPower\(\) -     SHOW_ENTITY - Updating Entity=\[name=UNKNOWN ENTITY \[cardType=INVALID\] id=\d+ zone=DECK zonePos=0 cardId= player=1\] CardID=.+""".r
      val MULLIGAN_START = "[Power] PowerTaskList.DebugPrintPower() -     TAG_CHANGE Entity=GameEntity tag=STEP value=BEGIN_MULLIGAN"
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
