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

  object Votes{

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

  object EmoteVote {


  }

}
