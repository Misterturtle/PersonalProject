//package LogReaderTests
//
//import org.scalatest.{FreeSpec, Matchers}
//import tph.{Card,  Player, GameState}
//
///**
//  * Created by Harambe on 3/18/2017.
//  */
//class HSDataBaseTests extends FreeSpec with Matchers {
//
//  val hsDataBase = new HSDataBaseTests()
//  val defaultGameState = new GameState()
//
//  "Given a source cardID and target cardID, the HSDataBase should" - {
//
//    "report REQ_TARGET_MIN_ATTACK" - {
//
//
//      val friendly = new Player(board = List[Card](new Card("")))
//      val gs = new GameState(friendly, enemy)
//
//      "valid" in {
//        //Source:
//        val sourceName= "Shadow Word: Death"
//        val sourceID= "EX1_622"
//
//        //Target:
//        val targetName = "Piloted Sky Golem"
//        val targetID = "GVG_105"
//        hsDataBase.isPlayValid(sourceID, targetID) shouldBe true
//      }
//
//      "invalid" in {
//        //Source:
//        val sourceName= "Shadow Word: Death"
//        val sourceID= "EX1_622"
//        //Target:
//        val targetName = "Knife Juggler"
//        val targetID = "NEW1_019"
//        hsDataBase.isPlayValid(sourceID, targetID) shouldBe false
//      }
//    }
//
//    "report REQ_LEGENDARY_TARGET" - {
//      "valid" in {
//        //Source:
//        val sourceName= "Rend Blackhand"
//        val sourceID= "BRM_029"
//
//        //Target:
//        val targetName = "Emperor Thaurissan"
//        val targetID = "BRMA03_1H"
//        hsDataBase.isPlayValid(sourceID, targetID) shouldBe true
//      }
//
//      "invalid" in {
//        //Source:
//        val sourceName= "Rend Blackhand"
//        val sourceID= "BRM_029"
//        //Target:
//        val targetName = "Knife Juggler"
//        val targetID = "NEW1_019"
//        hsDataBase.isPlayValid(sourceID, targetID) shouldBe false
//    }
//  }
//
//    "report REQ_TARGET_IF_AVAILABLE_AND_MINIMUM_FRIENDLY_SECRETS" - {
//      "valid" in {
//        //Source:
//        val sourceName= "Rend Blackhand"
//        val sourceID= "BRM_029"
//
//        //Target:
//        val targetName = "Emperor Thaurissan"
//        val targetID = "BRMA03_1H"
//        hsDataBase.isPlayValid(sourceID, targetID) shouldBe true
//      }
//
//      "invalid" in {
//        //Source:
//        val sourceName= "Rend Blackhand"
//        val sourceID= "BRM_029"
//        //Target:
//        val targetName = "Knife Juggler"
//        val targetID = "NEW1_019"
//        hsDataBase.isPlayValid(sourceID, targetID) shouldBe false
//      }
//    }
//}
