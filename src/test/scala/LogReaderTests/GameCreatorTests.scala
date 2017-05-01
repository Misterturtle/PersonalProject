package LogReaderTests

import java.io.File

import FileReaders.GameBuilder
import GameState.{TagChange, Game, GameEntity, GameState}
import org.scalatest.{Matchers, FreeSpec}

/**
  * Created by Harambe on 4/26/2017.
  */
class GameCreatorTests extends FreeSpec with Matchers {


  "A Game should be created with" - {

    "A GameEntity" ignore {

      val gs = new GameState()
      val log = new File(getClass.getResource("/CreateGame.txt").getPath)
      val gc = new GameBuilder(gs, log)

      gc.read()
      gc.update()

      val expectedTagList = List(TagChange("ZONE", "PLAY"), TagChange("ENTITY_ID", 1), TagChange("CARDTYPE", "GAME"))
      gs.currentGame.gameEntity shouldBe GameEntity(Some(1), expectedTagList)
    }




  }





}

