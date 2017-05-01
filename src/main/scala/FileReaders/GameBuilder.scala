package FileReaders

import java.io.{FileReader, BufferedReader, File}

import GameState._
import com.typesafe.config.ConfigFactory

/**
  * Created by Harambe on 4/26/2017.
  */



class GameBuilder(gs: GameState, log:File = new File(ConfigFactory.load().getString("tph.readerFiles.outputLog"))) {

  val reader = new BufferedReader(new FileReader(log))

  val NEW_GAME = "[Power] GameState.DebugPrintPower() - CREATE_GAME"
  val GAME_ENTITY = """\[Power\] GameState.DebugPrintPower\(\) -     GameEntity EntityID=(\d+)""".r
  val PLAYER_ENTITY = """\[Power\] GameState.DebugPrintPower\(\) -     Player EntityID=(\d+) PlayerID=(\d+) GameAccountId=\[hi=(\d+) lo=(\d+)\]""".r
  val TAG = """\[Power\] GameState.DebugPrintPower\(\) -         tag=(.+) value=(.+)""".r

  var currentGame:Game = CreateNewGame()
  var currentEntity: Entity = currentGame.gameEntity




  def update(): Unit = {
    currentEntity.updateGame(currentGame)
    gs.currentGame = currentGame
  }

  def read(): Unit ={

    while(reader.ready()){
      val line = reader.readLine()
      line match{
        case NEW_GAME =>
          currentGame = CreateNewGame()

        case GAME_ENTITY(entityID) =>
          SetCurrentEntity(GameEntity())
          SetCurrentEntityID(entityID.toInt)

        case TAG(tag, value) =>
          AddTagToCurrentEntity(TagChange(tag,value))
      }
    }


  }



  private def CreateNewGame(): Game = Game(GameEntity(), Deck(), PlayerEntity(), PlayerEntity())

  private def AddTagToCurrentEntity(tag:TagChange): Unit = currentEntity = currentEntity.addTag(tag)

  private def SetCurrentEntityID(newID:Int): Unit = currentEntity = currentEntity.copyEntity(id = Some(newID))

  private def SetCurrentEntity(entity:Entity):Unit = currentEntity = entity








}
