package FileReaders

import java.io._

import com.typesafe.config.ConfigFactory
import HSAction._
import tph.{Constants, GameState, Player}

/**
  * Created by Harambe on 2/15/2017.
  */
class LogParser() {

    val config = ConfigFactory.load()
    val defaultFileNameString = config.getString("tph.writerFiles.actionLog")
    val defaultLog = new File(defaultFileNameString)

    def identifyHSAction(actionString: String): HSAction = {
      import Constants.LogFileReaderStrings.HSActionStrings._

      actionString match {

        //////////////////////////////////////////////////Friendly Events//////////////////////////////////////////////////////
        case FRIENDLY_CARD_DRAWN(name, id, cardID, player, position) =>
          new CardDrawn(name, id.toInt, cardID, position.toInt, player.toInt)

        case FRIENDLY_CARD_CREATED(name, id, cardID, player, position) =>
          new CardDrawn(name, id.toInt, cardID, position.toInt, player.toInt)

        case FRIENDLY_MINION_CONTROLLED(name, id, position) =>
          new FriendlyMinionControlled(name, id.toInt, position.toInt)

        case FRIENDLY_MULLIGAN_REDRAW(name, id, position, player) if position.toInt != 0 =>
          new MulliganRedraw(name, id.toInt, position.toInt, player.toInt)

        case FRIENDLY_CARD_RETURN(name, id, player) =>
          new FriendlyCardReturn(name, id.toInt, player.toInt)



        //////////////////////////////////////////////////Enemy Events//////////////////////////////////////////////////////
        case ENEMY_CARD_DRAWN(id, player, position) =>
          new CardDrawn(Constants.STRING_UNINIT,id.toInt, Constants.STRING_UNINIT, position.toInt, player.toInt)

        case ENEMY_MINION_CONTROLLED(name, id, position) =>
          new EnemyMinionControlled(name, id.toInt, position.toInt)

        case ENEMY_MULLIGAN_REDRAW(id, player) =>
          new EnemyMulliganRedraw(id.toInt, player.toInt)

        case ENEMY_CARD_RETURN(name, id, cardID, player) =>
          new EnemyCardReturn(name, id.toInt, cardID, player.toInt)

        case ENEMY_CARD_CREATED(id, player, position) =>
          new CardDrawn(Constants.STRING_UNINIT, id.toInt, Constants.STRING_UNINIT, position.toInt, player.toInt)

        //////////////////////////////////////////////////Neutral Events//////////////////////////////////////////////////////

        case FACE_ATTACK_VALUE(player, value) =>
          new ChangeFaceAttackValue(player.toInt, value.toInt)

        case DECK_TO_BOARD(name, id, cardID, player) =>
          new DeckToBoard(name, id.toInt, cardID, player.toInt)

        case RETURNED_CARD_PLAYED(name, id, position, cardID, player) if position.toInt != 0 =>
          new CardPlayed(name, id.toInt, position.toInt, cardID, player.toInt)

        case SECRET_PLAYED(id, player) =>
          new SecretPlayed(id.toInt, player.toInt)

        case CARD_PLAYED(name, id, position, cardID, player) =>
          new CardPlayed(name, id.toInt, position.toInt, cardID, player.toInt)

        case CARD_DEATH(name, id, player) =>
          new CardDeath(name, id.toInt, player.toInt)

        case MINION_SUMMONED(name, id, position, cardID, player) if position.toInt != 0 =>
          new MinionSummoned(name, id.toInt, position.toInt, cardID, player.toInt)

        case TRANSFORM(name, id, position, cardID, newID) if position.toInt != 0 =>
          new Transform(name, id.toInt, position.toInt, cardID, newID.toInt)

        case BOARD_SETASIDE_REMOVAL(name, id, player) =>
          new CardDeath(Constants.STRING_UNINIT, id.toInt, player.toInt)

        case HAND_SETASIDE_REMOVAL(id, player) =>
          new CardDeath(Constants.STRING_UNINIT, id.toInt, player.toInt)

        case DEFINE_PLAYERS(friendlyPlayerNumber) =>
          new DefinePlayers(friendlyPlayerNumber.toInt)

        case MODIFIED_HERO_FACE_VALUE(player, value) =>
          new ChangeFaceAttackValue(player.toInt, value.toInt)

        case GAME_OVER =>
          new GameOver()

        case _ =>
          new HSActionUninit()
      }
    }

    def constructGameState(file: File = defaultLog): GameState = {
      val br = new BufferedReader(new FileReader(file))
      val streams = Stream.continually(br.readLine()).takeWhile(_ != null)
      val gameState:GameState = streams.foldLeft(new GameState()) { (r, c) =>
          val hsAction = identifyHSAction(c)
        if(hsAction != HSActionUninit())
        hsAction.ExecuteAction(r)
        else
          r
      }
      gameState
    }


    def getPlayerNumbers(file:File = defaultLog): (Int, Int) = {
      val DEFINE_PLAYERS = Constants.LogFileReaderStrings.HSActionStrings.DEFINE_PLAYERS
      val reader = new BufferedReader(new FileReader(file))
      val streams = Stream.continually(reader.readLine()).takeWhile(_ != null)
      streams.foreach {
        case DEFINE_PLAYERS(friendlyPlayer) =>
          friendlyPlayer.toInt match {
            case 1 =>
              return (1, 2)
            case 2 =>
              return (2, 1)
          }
        case _ =>
      }
    println("Couldn't find DEFINE_PLAYERS regex string")

    (Constants.INT_UNINIT, Constants.INT_UNINIT)
    }
}
