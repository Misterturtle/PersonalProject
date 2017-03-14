package VoteSystemTests

import VoteSystem.VoteParser
import org.scalatest.{FlatSpec, Matchers}
import tph.Constants.ActionVotes._
import tph.Constants.EmojiVotes._

/**
  * Created by Harambe on 3/10/2017.
  */
class VoteParserTests extends FlatSpec with Matchers {

  val vp = new VoteParser()
  val sender = "VoteParserTest"


  //--------------Action Vote Commands----------------//

  "A VoteParser" should "create a play card vote" in {

    val command = " c1 "
    vp.createVote(sender, command) shouldBe new CardPlay(card = 1)
  }

  it should "create a card play with position vote" in{
    val command = " c1 >  > f2 "
    vp.createVote(sender, command) shouldBe new CardPlayWithPosition(1, 2)
  }

  it should "create a card play with position and friendly target vote" in {
    val command = " c1 > > f2 > f3"
    val command2 = " c1 > f3 >> f2"

    vp.createVote(sender, command) shouldBe new CardPlayWithFriendlyTargetWithPosition(1, 3, 2)
    vp.createVote(sender, command2) shouldBe new CardPlayWithFriendlyTargetWithPosition(1, 3, 2)
  }

  it should "create a card play with position and enemy target vote" in {
    val command = " c1 > > f2 > e3"
    val command2 = " c1 > e3 >> f2"

    vp.createVote(sender, command) shouldBe new CardPlayWithEnemyTargetWithPosition(1, 3, 2)
    vp.createVote(sender, command2) shouldBe new CardPlayWithEnemyTargetWithPosition(1, 3, 2)
  }

  it should "create a card play with friendly target vote" in {
    val command = " c1 > f2 "
    vp.createVote(sender, command) shouldBe new CardPlayWithFriendlyTarget(1, 2)
  }

  it should "create a card play with enemy target vote" in {
    val command = " c1 > e2 "
    vp.createVote(sender, command) shouldBe new CardPlayWithEnemyTarget(1, 2)
  }

  it should "create a normal attack vote" in {
    val command = " f1 > e2 "
    vp.createVote(sender, command) shouldBe new NormalAttack(1, 2)
  }

  it should "create a hero power vote" in {
    val command = " hp "
    vp.createVote(sender, command) shouldBe new HeroPower()
  }

  it should "create a hero power with friendly target vote" in {
    val command = " hp > f1 "
    vp.createVote(sender,command) shouldBe new HeroPowerWithFriendlyTarget(1)
  }

  it should "create a hero power with enemy target vote" in {
    val command = " hp > e1 "
    vp.createVote(sender,command) shouldBe new HeroPowerWithEnemyTarget(1)
  }

  it should "create a discover vote" in {
    val command = " discover1 "
    vp.createVote(sender, command) shouldBe new Discover(1)
  }

  it should "create a mulligan vote" in {
    val command = " mulligan 123 "
    vp.createVote(sender, command) shouldBe new MulliganVote(true, true, true, false)
  }

  it should "create a mulligan vote in any order" in {
    val command = " mulligan 3 14 "
    vp.createVote(sender, command) shouldBe new MulliganVote(true, false, true, true)
  }




  //-------------Emoji Vote Commands------------//

  it should "create a greetings vote" in {
    val command = "greetings"
    vp.createVote(sender, command) shouldBe new Greetings()
  }

  it should "create a well played vote" in {
    val command = "well played"
    vp.createVote(sender, command) shouldBe new WellPlayed()
  }

  it should "create an oops vote" in {
    val command = "oops"
    vp.createVote(sender, command) shouldBe new Oops()
  }

  it should "create a thanks vote" in {
    val command = "thanks"
    vp.createVote(sender, command) shouldBe new Thanks()
  }

  it should "create a wow vote" in {
    val command = "wow"
    vp.createVote(sender, command) shouldBe new Wow()
  }

  it should "create a threaten vote" in {
    val command = "threaten"
    vp.createVote(sender, command) shouldBe new Threaten()
  }
}
