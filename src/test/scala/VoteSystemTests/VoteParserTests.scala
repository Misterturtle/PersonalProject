package VoteSystemTests

import VoteSystem.VoteParser
import org.scalatest.{FreeSpec, FlatSpec, Matchers}
import tph.Constants.ActionVotes._
import tph.Constants.EmojiVotes._

/**
  * Created by Harambe on 3/10/2017.
  */
class VoteParserTests extends FreeSpec with Matchers {

  val vp = new VoteParser()
  val sender = "VoteParserTest"


  //--------------Action Vote Commands----------------//

  "The Vote Parser should be able to" - {

    "create a play card vote" - {

      "normal" in {
        val command = " c1 "
        vp.createVote(sender, command) shouldBe CardPlay(1)
      }

      "future" in {
        val command = " ?c1 "
        vp.createVote(sender, command) shouldBe FutureCardPlay(1)
      }
    }

      "create a card play with position vote" - {

        "normal" in {
          val command = " c1 >  > f2 "
          vp.createVote(sender, command) shouldBe new CardPlayWithPosition(1, 2)
        }
        "future" in {
          val command = " ?c1 >  > ?f2 "
          vp.createVote(sender, command) shouldBe FutureCardPlayWithPosition(1, 2, true, true)
        }
      }

      "create a card play with position and friendly target vote" - {

        "normal" in {
          val command = " c1 > > f2 > f3"
          val command2 = " c1 > f3 >> f2"

          vp.createVote(sender, command) shouldBe new CardPlayWithFriendlyTargetWithPosition(1, 3, 2)
          vp.createVote(sender, command2) shouldBe new CardPlayWithFriendlyTargetWithPosition(1, 3, 2)
        }

        "future" in {
          val command = " ?c1 > > ?f2 > ?f3"
          val command2 = " c1 > ?f3 >> ?f2"

          vp.createVote(sender, command) shouldBe FutureCardPlayWithFriendlyTargetWithPosition(1, 3, 2, true, true, true)
          vp.createVote(sender, command2) shouldBe FutureCardPlayWithFriendlyTargetWithPosition(1, 3, 2, false, true, true)
        }
      }



      "create a card play with position and enemy target vote" - {

        "normal" in {
          val command = " c1 > > f2 > e3"
          val command2 = " c1 > e3 >> f2"

          vp.createVote(sender, command) shouldBe new CardPlayWithEnemyTargetWithPosition(1, 3, 2)
          vp.createVote(sender, command2) shouldBe new CardPlayWithEnemyTargetWithPosition(1, 3, 2)
        }

        "future" in {
          val command = " ?c1 > > f2 > ?e3"
          val command2 = " c1 > ?e3 >> ?f2"

          vp.createVote(sender, command) shouldBe FutureCardPlayWithEnemyTargetWithPosition(1, 3, 2, true, true, false)
          vp.createVote(sender, command2) shouldBe FutureCardPlayWithEnemyTargetWithPosition(1, 3, 2, false, true, true)
        }
      }

      "create a card play with friendly target vote" - {

        "normal" in {
          val command = " c1 > f2 "
          vp.createVote(sender, command) shouldBe CardPlayWithFriendlyTarget(1, 2)
        }

        "future" in {
          val command = " c1 > ?f2 "
          vp.createVote(sender, command) shouldBe FutureCardPlayWithFriendlyTarget(1, 2, false, true)
        }
      }

      "create a card play with enemy target vote" - {

      "normal" in {
        val command = " c1 > e2 "
        vp.createVote(sender, command) shouldBe CardPlayWithEnemyTarget(1, 2)
      }
      "future" in {
        val command = " c1 > ?e2 "
        vp.createVote(sender, command) shouldBe FutureCardPlayWithEnemyTarget(1, 2, false, true)
      }
    }

     "create a normal attack vote" - {

       "normal" in {
         val command = " f1 > e2 "
         vp.createVote(sender, command) shouldBe NormalAttack(1, 2)
       }

       "future" in {
         val command = " ?f1 > ?e2 "
         vp.createVote(sender, command) shouldBe FutureNormalAttack(1, 2, true, true)
       }
     }

     "create a hero power vote" in {
      val command = " hp "
      vp.createVote(sender, command) shouldBe new HeroPower()
    }

     "create a hero power with friendly target vote" - {

       "normal" in {
         val command = " hp > f1 "
         vp.createVote(sender, command) shouldBe new HeroPowerWithFriendlyTarget(1)
       }

       "future" in {
         val command = " hp > ?f1 "
         vp.createVote(sender, command) shouldBe FutureHeroPowerWithFriendlyTarget(1)
       }
     }

    "create a hero power with enemy target vote" - {

      "normal" in {
        val command = " hp > e1 "
        vp.createVote(sender, command) shouldBe new HeroPowerWithEnemyTarget(1)
      }

      "future" in {
        val command = " hp > ?e1 "
        vp.createVote(sender, command) shouldBe FutureHeroPowerWithEnemyTarget(1)
      }
    }

     "create a discover vote" in {
      val command = " discover1 "
      vp.createVote(sender, command) shouldBe new Discover(1)
    }

     "create a mulligan vote" in {
      val command = " 1,2,3 "
      vp.parseMulligan(sender, command) shouldBe new MulliganVote(true, true, true, false)
    }

     "create a mulligan vote in any order" in {
      val command = "3, 1,4 "
      vp.parseMulligan(sender, command) shouldBe new MulliganVote(true, false, true, true)
    }

     "create an end turn vote" - {

       "when spelled out" in {

         val command = " endturn"
         vp.createVote(sender, command) shouldBe new EndTurn()
       }

       "when using shorthand" in {
         val command = " et "
         vp.createVote(sender,command) shouldBe new EndTurn()
       }
    }





    //-------------Emoji Vote Commands------------//

     "create a greetings vote" in {
      val command = "greetings"
      vp.createVote(sender, command) shouldBe new Greetings()
    }

     "create a well played vote" in {
      val command = "well played"
      vp.createVote(sender, command) shouldBe new WellPlayed()
    }

     "create an oops vote" in {
      val command = "oops"
      vp.createVote(sender, command) shouldBe new Oops()
    }

     "create a thanks vote" in {
      val command = "thanks"
      vp.createVote(sender, command) shouldBe new Thanks()
    }

     "create a wow vote" in {
      val command = "wow"
      vp.createVote(sender, command) shouldBe new Wow()
    }

     "create a threaten vote" in {
      val command = "threaten"
      vp.createVote(sender, command) shouldBe new Threaten()
    }
  }
}