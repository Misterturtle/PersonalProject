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

  def identifyHSAction(actionString: String): HSAction = {
    import Constants.LogFileReaderStrings.HSActionStrings._
    import Constants.LogFileReaderStrings.GameStateStrings._

    actionString match {

      //////////////////////////////////////////////////Friendly Events//////////////////////////////////////////////////////

      case FRIENDLY_CARD_DRAWN(name, id, cardID, player, position) =>
        CardDrawn(name, id.toInt, cardID, position.toInt, player.toInt)

      case FRIENDLY_CARD_CREATED(name, id, cardID, player, position) =>
        CardDrawn(name, id.toInt, cardID, position.toInt, player.toInt)

      case FRIENDLY_MINION_CONTROLLED(name, id, position) =>
        FriendlyMinionControlled(name, id.toInt, position.toInt)

      case FRIENDLY_MULLIGAN_REDRAW(name, id, position, player) if position.toInt != 0 =>
        FriendlyMulliganRedraw(name, id.toInt, position.toInt, player.toInt)

      case FRIENDLY_CARD_RETURN(name, id, player) =>
        FriendlyCardReturn(name, id.toInt, player.toInt)



      //////////////////////////////////////////////////Enemy Events//////////////////////////////////////////////////////
      case ENEMY_CARD_DRAWN(id, player, position) =>
        CardDrawn(Constants.STRING_UNINIT, id.toInt, Constants.STRING_UNINIT, position.toInt, player.toInt)

      case ENEMY_MINION_CONTROLLED(name, id, position) =>
        EnemyMinionControlled(name, id.toInt, position.toInt)

      case ENEMY_MULLIGAN_REDRAW(id, player) =>
        EnemyMulliganRedraw(id.toInt, player.toInt)

      case ENEMY_CARD_RETURN(name, id, cardID, player) =>
        EnemyCardReturn(name, id.toInt, cardID, player.toInt)

      case ENEMY_CARD_CREATED(id, player, position) =>
        CardDrawn(Constants.STRING_UNINIT, id.toInt, Constants.STRING_UNINIT, position.toInt, player.toInt)

      //////////////////////////////////////////////////Neutral Events//////////////////////////////////////////////////////

      case FROZEN(id, player, value) =>
        Frozen(id.toInt, player.toInt, value.toInt)

      case SECRET_DESTROYED(player) =>
        SecretDestroyed(player.toInt)

      case MINION_DAMAGED(id, player, dmg) =>
        MinionDamaged(id.toInt, player.toInt, dmg.toInt)

        //\[Zone\] ZoneChangeList.ProcessChanges\(\) - processing index=\d+ change=powerTask=\[power=\[type=TAG_CHANGE entity=\[id=\d+ cardId=.+ name=.+\] tag=CANT_BE_ATTACKED value=(\d+)\] complete=False\] entity=\[name=.+ id=(\d+) zone=PLAY zonePos=\d+ cardId=.+ player=(\d+)\] srcZoneTag=INVALID srcPos= dstZoneTag=INVALID dstPos=
      case MINION_STEALTHED(stealthValue, id, player)=>
        if(stealthValue.toInt == 0)
          MinionStealthed(id.toInt, player.toInt, false)
        else
          MinionStealthed(id.toInt, player.toInt, true)

      case TAUNT_CHANGE(id, player, tauntValue)=>
        if(tauntValue.toInt == 0)
          TauntChange(id.toInt, player.toInt, false)
        else
          TauntChange(id.toInt, player.toInt, true)

      case DEATHRATTLE_CHANGE(id, player, deathrattleValue) =>
        if(deathrattleValue.toInt == 0)
          DeathrattleChange(id.toInt, player.toInt, false)
        else
          DeathrattleChange(id.toInt, player.toInt, true)

      case WEAPON_EQUIPPED(id, player)=>
        WeaponChange(id.toInt, player.toInt, true)

      case WEAPON_DESTROYED(id, player)=>
        WeaponChange(id.toInt, player.toInt, false)

      case COMBO_ACTIVE(playerName, value) =>
        if(value.toInt == 0)
          ComboActive(playerName, false)
        else
          ComboActive(playerName, true)


      case NEW_HERO(name, id, cardID, player, friendOrFoe) =>
        NewHero(name, id.toInt, cardID, player.toInt, friendOrFoe)

      case REPLACE_HERO(id, cardID, player, oldHeroID)=>
        ReplaceHero(id.toInt, cardID, player.toInt, oldHeroID.toInt)

      case CHANGE_ATTACK_VALUE(id, position, player, value) =>
        ChangeAttackValue(player.toInt, value.toInt, id.toInt, position.toInt)

      case DECK_TO_BOARD(name, id, cardID, player) =>
        DeckToBoard(name, id.toInt, cardID, player.toInt)

      case RETURNED_CARD_PLAYED(name, id, position, cardID, player) if position.toInt != 0 =>
        CardPlayed(name, id.toInt, position.toInt, cardID, player.toInt)

      case SECRET_PLAYED(id, player) =>
        SecretPlayed(id.toInt, player.toInt)

      case CARD_PLAYED(name, id, position, cardID, player) =>
        CardPlayed(name, id.toInt, position.toInt, cardID, player.toInt)

      case CARD_DEATH(name, id, player) =>
        CardDeath(name, id.toInt, player.toInt)

      case NEW_HERO_POWER(id, cardID, player) =>
        NewHeroPower(id.toInt, cardID, player.toInt)

      case MINION_SUMMONED(name, id, position, cardID, player) if position.toInt != 0 =>
        MinionSummoned(name, id.toInt, position.toInt, cardID, player.toInt)

      case TRANSFORM(name, id, position, cardID, newID) if position.toInt != 0 =>
        Transform(name, id.toInt, position.toInt, cardID, newID.toInt)

      case BOARD_SETASIDE_REMOVAL(name, id, player) =>
        CardDeath(Constants.STRING_UNINIT, id.toInt, player.toInt)

      case HAND_SETASIDE_REMOVAL(id, player) =>
        CardDeath(Constants.STRING_UNINIT, id.toInt, player.toInt)

        //Option Creations

      case OPTION_CHOICE(choiceNum, choiceType, mainEntity, error, errorParam) =>
        PowerOptionChoice(choiceNum.toInt, choiceType, Entity(mainEntity), error, errorParam)


      case OPTION_TARGET(targetNum, mainEntity, error, errorParam)=>
        PowerOptionTarget(targetNum.toInt, Entity(mainEntity), error, errorParam)

      case SUBOPTION(subOptionNum, entity, error, errorParam)=>
        PowerSubOption(subOptionNum.toInt, Entity(entity), error, errorParam)


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
        GameOver()

      case _ =>
        HSActionUninit()
    }
  }
}
