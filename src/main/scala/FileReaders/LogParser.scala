package FileReaders

import java.io._
import java.util.concurrent.{Executors, TimeUnit}


import com.typesafe.config.ConfigFactory
import HSAction._
import tph.{Constants, GameState, Player}

/**
  * Created by Harambe on 2/15/2017.
  */
class LogParser(gs: GameState) {

  val config = ConfigFactory.load()
  val defaultLog = new File(config.getString("tph.writerFiles.actionLog"))
  val scheduler = Executors.newScheduledThreadPool(1)
  val br = new BufferedReader(new FileReader(defaultLog))

  //Possible way to fix bugs where gamestate isn't accurate from a constant stream read (The poll method)


  def identifyHSAction(actionString: String): HSAction = {
    import Constants.LogFileReaderStrings.HSActionStrings._
    import Constants.LogFileReaderStrings.GameStateStrings._

    actionString match {

      //////////////////////////////////////////////////Friendly Events//////////////////////////////////////////////////////
      case DEFINE_PLAYERS(id, cardID, friendlyPlayerNumber) if gs.friendlyPlayer.playerNumber == Constants.INT_UNINIT | gs.enemyPlayer.playerNumber == Constants.INT_UNINIT =>
        new DefinePlayers(id.toInt, cardID, friendlyPlayerNumber.toInt)

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
        new CardDrawn(Constants.STRING_UNINIT, id.toInt, Constants.STRING_UNINIT, position.toInt, player.toInt)

      case ENEMY_MINION_CONTROLLED(name, id, position) =>
        new EnemyMinionControlled(name, id.toInt, position.toInt)

      case ENEMY_MULLIGAN_REDRAW(id, player) =>
        new EnemyMulliganRedraw(id.toInt, player.toInt)

      case ENEMY_CARD_RETURN(name, id, cardID, player) =>
        new EnemyCardReturn(name, id.toInt, cardID, player.toInt)

      case ENEMY_CARD_CREATED(id, player, position) =>
        new CardDrawn(Constants.STRING_UNINIT, id.toInt, Constants.STRING_UNINIT, position.toInt, player.toInt)

      //////////////////////////////////////////////////Neutral Events//////////////////////////////////////////////////////

      case FROZEN(id, player, value) =>
        new Frozen(id.toInt, player.toInt, value.toInt)

      case SECRET_DESTROYED(id, player) =>
        new SecretDestroyed(id.toInt, player.toInt)

      case MINION_DAMAGED(id, player, dmg) =>
        new MinionDamaged(id.toInt, player.toInt, dmg.toInt)

        //\[Zone\] ZoneChangeList.ProcessChanges\(\) - processing index=\d+ change=powerTask=\[power=\[type=TAG_CHANGE entity=\[id=\d+ cardId=.+ name=.+\] tag=CANT_BE_ATTACKED value=(\d+)\] complete=False\] entity=\[name=.+ id=(\d+) zone=PLAY zonePos=\d+ cardId=.+ player=(\d+)\] srcZoneTag=INVALID srcPos= dstZoneTag=INVALID dstPos=
      case MINION_STEALTHED(stealthValue, id, player)=>
        if(stealthValue.toInt == 0)
          new MinionStealthed(false, id.toInt, player.toInt)
        else
          new MinionStealthed(true,id.toInt,player.toInt)

      case TAUNT_CHANGE(id, player, tauntValue)=>
        if(tauntValue.toInt == 0)
          new TauntChange(id.toInt, player.toInt, false)
        else
          new TauntChange(id.toInt, player.toInt, true)

      case DEATHRATTLE_CHANGE(id, player, deathrattleValue) =>
        if(deathrattleValue.toInt == 0)
          new DeathrattleChange(id.toInt, player.toInt, false)
        else
          new DeathrattleChange(id.toInt, player.toInt, true)

      case WEAPON_EQUIPPED(id, player)=>
        new WeaponChange(id.toInt, player.toInt, true)

      case WEAPON_DESTROYED(id, player)=>
        new WeaponChange(id.toInt, player.toInt, false)

      case COMBO_ACTIVE(playerName, value) =>
        if(value.toInt == 0)
          new ComboActive(playerName, false)
        else
          new ComboActive(playerName, true)


      case NEW_HERO(id, cardID, player) =>
        new NewHero(id.toInt, cardID, player.toInt)

      case REPLACE_HERO(id, cardID, player, oldHeroID)=>
        new ReplaceHero(id.toInt, cardID, player.toInt, oldHeroID.toInt)

      case CHANGE_ATTACK_VALUE(id, position, player, value) =>
        new ChangeAttackValue(player.toInt, value.toInt, id.toInt, position.toInt)

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

      case NEW_HERO_POWER(id, cardID, player) =>
        new NewHeroPower(id.toInt, cardID, player.toInt)

      case MINION_SUMMONED(name, id, position, cardID, player) if position.toInt != 0 =>
        new MinionSummoned(name, id.toInt, position.toInt, cardID, player.toInt)

      case TRANSFORM(name, id, position, cardID, newID) if position.toInt != 0 =>
        new Transform(name, id.toInt, position.toInt, cardID, newID.toInt)

      case BOARD_SETASIDE_REMOVAL(name, id, player) =>
        new CardDeath(Constants.STRING_UNINIT, id.toInt, player.toInt)

      case HAND_SETASIDE_REMOVAL(id, player) =>
        new CardDeath(Constants.STRING_UNINIT, id.toInt, player.toInt)



        //Option Creations

      case OPTION_CHOICE(choiceNum, choiceType, mainEntity, error, errorParam) =>
        OptionChoice(choiceNum.toInt, choiceType, Entity(mainEntity), error, errorParam)


      case OPTION_TARGET(targetNum, mainEntity, error, errorParam)=>
        OptionTarget(targetNum.toInt, Entity(mainEntity), error, errorParam)



      case MULLIGAN_START =>
        MulliganStart()

      case MULLIGAN_OPTION() =>
        MulliganOption()

      case DISCOVER_OPTION()=>
        DiscoverStart()

      case TURN_START(playerName)=>
        TurnStart(playerName)

      case TURN_END(playerName)=>
        TurnEnd(playerName)

      case GAME_OVER =>
        new GameOver()

      case _ =>
        new HSActionUninit()
    }
  }

  def getPlayerNumbers(file: File = defaultLog): (Int, Int) = {
    val DEFINE_PLAYERS = Constants.LogFileReaderStrings.HSActionStrings.DEFINE_PLAYERS
    val reader = new BufferedReader(new FileReader(file))
    val streams = Stream.continually(reader.readLine()).takeWhile(_ != null)
    streams.foreach {
      case DEFINE_PLAYERS(id, cardID, friendlyPlayer) =>
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
