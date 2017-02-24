package UnitTests

import org.scalatest.{FlatSpec, Matchers}
import tph.Constants
import Constants.VoteStringNames._

/**
  * Created by Harambe on 2/23/2017.
  */
class ConstantsTests extends FlatSpec with Matchers {

  "Constants" should "contain a list of VoteStringNames" in {

    val actualList = Constants.VoteStringNames.listOfVoteStringNames
    val expectedList = List[String](
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

    actualList shouldEqual expectedList
    Constants.VoteStringNames.totalVoteStringNames shouldEqual expectedList.size
  }
}
