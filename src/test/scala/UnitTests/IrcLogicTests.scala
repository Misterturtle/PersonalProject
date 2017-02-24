package UnitTests


import Logic.IRCLogic
import VoteSystem.{VoteManager, ActionVote}
import org.scalatest.{FlatSpec, Matchers}
import tph.Constants.ActionVotes
import tph.Constants.ActionVotes._

import scala.util.Random
import scala.util.Random

/**
  * Created by Harambe on 2/23/2017.
  */
class IrcLogicTests extends FlatSpec with Matchers {

  val sender = "IRCLogicTests"

  "IRCLogic" should "make a decision with no extra factors" in {

    val ircLogic = new IRCLogic()
    val listOfVotes: List[ActionVote] =
      //30 CardPlay() summed with
      MockVote(30, new CardPlay(sender, 4)) :::
      MockVote(40, new CardPlay(sender, 5)) :::
      MockVote(20, new NormalAttack(sender, 2, 3))

    val results: Map[ActionVote, Int] = ircLogic.GetBaseResults(listOfVotes)
    val highestResult = results.values.max
    val actualDecision: ActionVote = results.find(_._2 == highestResult).getOrElse((new ActionUninit(sender), tph.Constants.INT_UNINIT))._1
    val expectedDecision = new CardPlay(sender, 5)

    actualDecision shouldEqual expectedDecision
  }

  def MockVote(amount:Int, vote:ActionVote): List[ActionVote] ={

    new Range(0, amount, 1).foldLeft(List[ActionVote]()){
      (listOfVotes, iteration) =>
        listOfVotes ::: List(vote)
    }
  }

  def CreateRandomVoteList(amount: Int): List[ActionVote] = {
    new Range(0, amount, 1).foldLeft(List[ActionVote]()) { (list, iteration) =>
      list ::: List(RandomVote())
    }
  }

  def RandomVote(voteMapKey: Int = new Random().nextInt(20)): ActionVote = {
    import tph.Constants.VoteStringNames._
    val voteMap: Map[Int, String] = listOfVoteStringNames.foldLeft((Map[Int, String](), 0)) {
      (mapAndAmount: (Map[Int, String], Int), string) =>
        (mapAndAmount._1 ++ Map(mapAndAmount._2 -> string), mapAndAmount._2 + 1)
    }._1

    voteMap(voteMapKey) match {

      case CARD_PLAY =>
        new CardPlay(sender, new Random().nextInt(10) + 1)
      case CARD_PLAY_WITH_ENEMY_FACE_TARGET =>
        new CardPlayWithEnemyFaceTarget(sender, new Random().nextInt(10) + 1)
      case CARD_PLAY_WITH_ENEMY_FACE_TARGET_WITH_POSITION =>
        new CardPlayWithEnemyFaceTargetWithPosition(sender, new Random().nextInt(10) + 1, new Random().nextInt(7) + 1)
      case CARD_PLAY_WITH_ENEMY_FACE_TARGET =>
        new CardPlayWithEnemyFaceTarget(sender, new Random().nextInt(10))
      case CARD_PLAY_WITH_ENEMY_TARGET =>
        new CardPlayWithEnemyTarget(sender, new Random().nextInt(10) + 1, new Random().nextInt(7) + 1)
      case DISCOVER =>
        new Discover(sender, new Random().nextInt(3) + 1)
      case CARD_PLAY_WITH_FRIENDLY_TARGET_WITH_POSITION =>
        new CardPlayWithFriendlyTargetWithPosition(sender, new Random().nextInt(10) + 1, new Random().nextInt(7) + 1, new Random().nextInt(7) + 1)
      case CARD_PLAY_WITH_FRIENDLY_FACE_TARGET_WITH_POSITION =>
        new CardPlayWithFriendlyFaceTargetWithPosition(sender, new Random().nextInt(10) + 1, new Random().nextInt(7) + 1)
      case CARD_PLAY_WITH_ENEMY_TARGET_WITH_POSITION =>
        new CardPlayWithEnemyTargetWithPosition(sender, new Random().nextInt(10) + 1, new Random().nextInt(7) + 1, new Random().nextInt(7) + 1)
      case CARD_PLAY_WITH_ENEMY_FACE_TARGET_WITH_POSITION =>
        new CardPlayWithEnemyFaceTargetWithPosition(sender, new Random().nextInt(10) + 1, new Random().nextInt(7) + 1)
      case CARD_PLAY =>
        new CardPlay(sender, new Random().nextInt(10) + 1)
      case CARD_PLAY_WITH_POSITION =>
        new CardPlayWithPosition(sender, new Random().nextInt(10) + 1, new Random().nextInt(7) + 1)
      case CARD_PLAY_WITH_FRIENDLY_TARGET =>
        new CardPlayWithFriendlyTarget(sender, new Random().nextInt(10) + 1, new Random().nextInt(7) + 1)
      case CARD_PLAY_WITH_ENEMY_TARGET =>
        new CardPlayWithEnemyTarget(sender, new Random().nextInt(10) + 1, new Random().nextInt(7) + 1)
      case CARD_PLAY_WITH_FRIENDLY_FACE_TARGET =>
        new CardPlayWithFriendlyFaceTarget(sender, new Random().nextInt(7) + 1)
      case CARD_PLAY_WITH_ENEMY_FACE_TARGET =>
        new CardPlayWithEnemyFaceTarget(sender, new Random().nextInt(7) + 1)
      case HERO_POWER =>
        new HeroPower(sender)
      case HERO_POWER_WITH_ENEMY_FACE =>
        new HeroPowerWithEnemyFace(sender)
      case HERO_POWER_WITH_ENEMY_TARGET =>
        new HeroPowerWithEnemyTarget(sender, new Random().nextInt(7) + 1)
      case HERO_POWER_WITH_FRIENDLY_FACE =>
        new HeroPowerWithFriendlyFace(sender)
      case HERO_POWER_WITH_FRIENDLY_TARGET =>
        new HeroPowerWithFriendlyTarget(sender, new Random().nextInt(7) + 1)
      case NORMAL_ATTACK_WITH_ENEMY_TARGET =>
        new NormalAttack(sender, new Random().nextInt(7) + 1, new Random().nextInt(7) + 1)
      case NORMAL_ATTACK_WITH_ENEMY_FACE_TARGET =>
        new NormalAttackToFace(sender, new Random().nextInt(7) + 1)
      case FACE_ATTACK_WITH_ENEMY_TARGET =>
        new FaceAttack(sender, new Random().nextInt(7) + 1)
      case FACE_ATTACK_WITH_ENEMY_FACE_TARGET =>
        new FaceAttackToFace(sender)
    }
  }


}
