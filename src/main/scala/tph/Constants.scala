package tph


import scala.collection.mutable.ListBuffer

/**
  * Created by Harambe on 10/29/2016.
  */
object Constants {

  val NORMAL_VOTE_TYPE = 0
  val BIND_VOTE_TYPE = 1
  val FUTURE_VOTE_TYPE = 2
  val UNINIT = -5


  object voteCodes {


    case class ChangeReaderFile(fileName: String)

    case class CommandVote(builtCommand: Vote)

    case class ChangeMenu(previousMenu: String, changeToMenu: String)


    //Discover Type
    case class Discover(option: Int)

    // Battlecry Option Type
    case class CardPlayWithFriendlyOption(card: Int, boardTarget: Int)

    case class CardPlayWithFriendlyFaceOption(card: Int)

    case class CardPlayWithEnemyOption(card: Int, boardTarget: Int)

    case class CardPlayWithEnemyFaceOption(card: Int)

    //Battlecry Option with Position Type
    case class CardPlayWithFriendlyOptionWithPosition(card: Int, target: Int, position: Int)

    case class CardPlayWithFriendlyFaceOptionWithPosition(card: Int, position: Int)

    case class CardPlayWithEnemyOptionWithPosition(card: Int, target: Int, position: Int)

    case class CardPlayWithEnemyFaceOptionWithPosition(card: Int, position: Int)

    //Normal Turn Play Type
    case class CardPlay(card: Int)

    case class CardPlayWithPosition(card: Int, position: Int)

    case class CardPlayWithFriendlyBoardTarget(card: Int, target: Int)

    case class CardPlayWithEnemyBoardTarget(card: Int, target: Int)

    case class CardPlayWithFriendlyFaceTarget(card: Int)

    case class CardPlayWithEnemyFaceTarget(card: Int)

    case class HeroPower()

    case class HeroPowerWithFriendlyTarget(target: Int)

    case class HeroPowerWithEnemyTarget(target: Int)

    case class HeroPowerWithFriendlyFace()

    case class HeroPowerWithEnemyFace()


    //Attack Type
    case class NormalAttack(friendlyPosition: Int, enemyPosition: Int)

    case class FaceAttack(position: Int)

    case class NormalAttackToFace(position: Int)

    case class FaceAttackToFace()

    //Emote Type
    case class Greetings()

    case class Thanks()

    case class WellPlayed()

    case class Wow()

    case class Oops()

    case class Threaten()

    //Misc Type
    case class Wait()

    case class Hurry()

    case class Concede()

    case class EndTurn()

    case class Bind()

    case class Future()

    case class Bound()

    case class FutureBound()

    case class Break()

    case class MulliganVote(vote: Array[Int])

    //MultiMenu
    case class Back(menu: String)

    case class Play(menu: String)

    case class Collection(menu: String)

    //Main Menu

    case class Shop()

    case class OpenPacks()

    case class QuestLog()

    //Play Menu

    case class Casual()

    case class Ranked()

    case class Deck(deckNumber: Int)

    case class FirstPage()

    case class SecondPage()

    //Quest Menu

    case class Quest(number: Int)

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




    //!att f1 e3, !p c1 s1

    //"BN

  }

  object EmoteVotes {
    val GREETINGS = "Greetings"
    val THANKS = "Thanks"
    val WELL_PLAYED = "Well Played"
    val WOW = "Wow"
    val OOPS = "Oops"
    val THREATEN = "Threaten"
  }

  object MiscVotes {



  }

}
