package VoteSystemTests

import VoteSystem._
import org.scalatest.{FlatSpec, Matchers}
import tph.Constants.ActionVotes.{NormalAttack, CardPlay, ActionUninit}
import tph.Constants.EmojiVotes.EmojiUninit
import tph.GameState

/**
  * Created by Harambe on 2/23/2017.
  */


/*
it should be able to report a decision based on voter's vote values
 */





class VoteManagerTests extends FlatSpec with Matchers {

  val sender = "VoteManagerTests"


  "A Vote Manager" should "tell a voter to store a vote" in {
    val vm = new VoteManager(new GameState())
    val vote = new CardPlay(1)
    var voteEntered = false
    val mockVoter = new Voter(sender) {
      override def voteEntry(vote: Vote): Voter = {
        voteEntered = true
        new Voter("")
      }
    }
    vm.voterMap = Map[String, Voter](sender -> mockVoter)
    vm.voteEntry(sender, vote)
    voteEntered shouldBe true
  }

  it should "add a voter to the voter list when entering a vote from an unknown voter" in {
    val vote = new CardPlay(1)
    val vm = new VoteManager(new GameState())
    vm.voteEntry(sender, vote)
    vm.voterMap.isDefinedAt(sender) shouldBe true
  }

  it should "be able to tell voters to adjust votes with any given GameState updateMap" in {
    val vm = new VoteManager(new GameState())
    var votesUpdated = false
    val mockVoter = new Voter(sender) {override def updateVotes(updateMap: Map[Int, Int]): Unit ={
      votesUpdated = true
    }}
    vm.voterMap = Map[String, Voter](sender -> mockVoter)
    val updateMap = Map[Int,Int](1 ->2, 3->4)
    vm.updateVotes(updateMap)
  }

  it should "collect base vote values from voter" in {
    val vm = new VoteManager(new GameState())
    vm.voterMap = Map(sender -> new Voter(sender){
      override def baseVoteValues():Map[Vote, Double] = Map(new CardPlay(1) -> 10)
    })
    vm.baseVoteValues shouldBe Map(new CardPlay(1) -> 10)
  }

  it should "collect base vote values from multiple voters" in {
    val vm = new VoteManager(new GameState())
    vm.voterMap = Map[String, Voter](
      sender -> new Voter(sender){override def baseVoteValues():Map[Vote, Double] = {Map(new CardPlay(1) -> 10)}},
      "tester2" -> new Voter("tester2"){override def baseVoteValues():Map[Vote, Double] = {Map(new NormalAttack(1,2) -> 10)}}
    )
    vm.baseVoteValues shouldBe Map(new CardPlay(1) -> 10, new NormalAttack(1,2) -> 10)
  }

  it should "collect and combine vote values from multiple voters" in {
    val vm = new VoteManager(new GameState())
    vm.voterMap = Map[String, Voter](
      sender -> new Voter(sender,Nil){override def baseVoteValues():Map[Vote, Double] = {Map(new CardPlay(1) -> 10)}},
      "tester2" -> new Voter("tester2"){override def baseVoteValues():Map[Vote, Double] = {Map(new NormalAttack(1,2) -> 10)}},
      "tester3" -> new Voter("tester3"){override def baseVoteValues():Map[Vote, Double] = {Map(new NormalAttack(1,2) -> 10)}}
    )
    vm.baseVoteValues shouldBe Map(new CardPlay(1) -> 10, new NormalAttack(1,2) -> 20)
  }

  it should "collect and modify vote values with a condition function and effect function" in {
    val vm = new VoteManager(new GameState())
    vm.voterMap = Map[String, Voter](
      sender -> new Voter(sender){override def baseVoteValues():Map[Vote, Double] = {Map(new CardPlay(1) -> 10)}})
    def influenceCondition(v:Vote): Boolean = {v == CardPlay(1)}
    def influenceEffect(initValue:Double): Double = {initValue + 1.00}
    vm.influenceVoteValues(influenceCondition _, influenceEffect _) shouldBe Map(new CardPlay(1) -> 11)
    }
}
