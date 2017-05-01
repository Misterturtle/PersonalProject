package VoteSystem

import GameState.GameState
import com.typesafe.scalalogging.LazyLogging
import tph.Constants.ActionVotes._
import tph._

//todo: Friendly card plays with friendly battlecry targets will move when the friendly card is played. However, gamestate won't adjust because the card isnt truly played yet.


/*

//I need to know which cards require a target, so that my program wont get stuck

//I would like to know which cards don't require a target, so I can filter invalid votes.



!c2



 */

case class TargetRequirement(friendly:Boolean = false, enemy:Boolean = false, minion:Boolean = false, hero:Boolean = false, minAttack:Option[Int] = None,
                             maxAttack:Option[Int] = None, legendary:Boolean = false, stealthed:Boolean = false, damaged:Boolean = false, undamaged:Boolean = false,
                             frozen:Boolean = false, taunt:Boolean = false, deathrattle:Boolean = false, race:Option[Int] = None, noReq:Boolean = true)

class VoteValidator(gs:GameState) extends LazyLogging {


  def targetMeetsRequirements(card: Card, targetReq: TargetRequirement): Boolean = {

    val raceMap = Map[String, Int](
      "DEMON" -> 15,
      "BEAST" -> 20,
      "MECH" -> 17,
      "MURLOC" -> 14)

    val isDeathRattle = {
      if (card.isDeathrattle.nonEmpty)
        card.isDeathrattle.get
      else
        card.cardInfo.mechanics.getOrElse(Nil).contains("DEATHRATTLE")
    }

    val isTaunt = {
      if (card.isTaunt.nonEmpty)
        card.isTaunt.get
      else
        card.cardInfo.mechanics.getOrElse(Nil).contains("TAUNT")
    }



    if (targetReq.damaged && !card.isDamaged)
      return false

    if (targetReq.deathrattle && !isDeathRattle)
      return false

    if (targetReq.enemy && card.player != gs.enemyPlayer.playerNumber)
      return false

    if (targetReq.friendly && card.player != gs.friendlyPlayer.playerNumber)
      return false

    if (targetReq.frozen && !card.isFrozen)
      return false

    if (targetReq.hero && card.cardInfo.cardType.getOrElse("NONE") != "HERO")
      return false

    if (targetReq.legendary && card.cardInfo.rarity.getOrElse("NONE") != "LEGENDARY")
      return false

    if (targetReq.maxAttack.nonEmpty) {
      val attack = {
        if (card.attack.nonEmpty)
          card.attack.get
        else
          card.cardInfo.attack.getOrElse(500)
      }
      if (attack > targetReq.maxAttack.get) {
        return false
      }
    }



    if (targetReq.minAttack.nonEmpty) {
      val attack = {
        if (card.attack.nonEmpty)
          card.attack.get
        else
          card.cardInfo.attack.getOrElse(-500)
      }
      if (attack < targetReq.minAttack.get) {
        return false
      }
    }


    if (targetReq.minion && card.cardInfo.cardType.getOrElse("NONE") != "MINION")
      return false

    if (targetReq.race.nonEmpty) {
      if (raceMap.isDefinedAt(card.cardInfo.race.getOrElse("NONE"))) {
        if (raceMap(card.cardInfo.race.getOrElse("NONE")) != targetReq.race.get)
          return false
      }
      else
        return false
    }


    if (targetReq.stealthed && !card.isStealthed)
      return false


    if (targetReq.taunt && !isTaunt)
      return false


    if (targetReq.undamaged && card.isDamaged)
      return false


    //If it makes it all the way here, return true
    true
  }

  def buildTargetReq(source: HSCard, target: HSCard): TargetRequirement = {

    var reqCounter = 0

    var friendly: Boolean = false
    var enemy: Boolean = false
    var minion: Boolean = false
    var hero: Boolean = false
    var minAttack: Option[Int] = None
    var maxAttack: Option[Int] = None
    var legendary: Boolean = false
    var stealthed: Boolean = false
    var damaged: Boolean = false
    var undamaged: Boolean = false
    var frozen: Boolean = false
    var taunt: Boolean = false
    var deathrattle: Boolean = false
    var race: Option[Int] = None

    source.cardInfo.playReqMap.keys.foreach {

      case "REQ_TARGET_MIN_ATTACK" =>
        minAttack = Some(source.cardInfo.playReqMap("REQ_TARGET_MIN_ATTACK"))
        reqCounter += 1

      case "REQ_LEGENDARY_TARGET" =>
        legendary = true
        reqCounter += 1

      case "REQ_STEALTHED_TARGET" =>
        stealthed = true
        reqCounter += 1

      case "REQ_DAMAGED_TARGET" =>
        damaged = true
        reqCounter += 1

      case "REQ_FROZEN_TARGET" =>
        frozen = true
        reqCounter += 1

      case "REQ_UNDAMAGED_TARGET" =>
        undamaged = true
        reqCounter += 1

      case "REQ_MUST_TARGET_TAUNTER" =>
        taunt = true
        reqCounter += 1

      case "REQ_TARGET_WITH_DEATHRATTLE" =>
        deathrattle = true
        reqCounter += 1

      case "REQ_ENEMY_TARGET" =>
        enemy = true
        reqCounter += 1

      case "REQ_HERO_TARGET" =>
        hero = true
        reqCounter += 1

      case "REQ_TARGET_WITH_RACE" =>
        race = Some(source.cardInfo.playReqMap("REQ_TARGET_WITH_RACE"))
        reqCounter += 1

      case "REQ_TARGET_MAX_ATTACK" =>
        maxAttack = Some(source.cardInfo.playReqMap("REQ_TARGET_MAX_ATTACK"))
        reqCounter += 1

      case "REQ_FRIENDLY_TARGET" =>
        friendly = true
        reqCounter += 1

      case "REQ_MINION_TARGET" =>
        minion = true
        reqCounter += 1

      case "REQ_NONSELF_TARGET" =>
        reqCounter += 1

      case _ =>
    }

    if (reqCounter > 0)
      TargetRequirement(friendly = friendly, enemy = enemy, minion = minion, hero = hero, minAttack = minAttack, maxAttack = maxAttack, legendary = legendary, stealthed = stealthed, damaged = damaged, undamaged = undamaged, frozen = frozen, taunt = taunt, deathrattle = deathrattle, race = race, noReq = false)
    else
      TargetRequirement(noReq = true)
  }


  def voteValidation(vote: ActionVote, voteEntry: Boolean, voteExecution: Boolean): Boolean = {

    val ignoreExceptions = List(
      "UNG_946",
      "AT_115",
      "EX1_154",
      "UNG_208",
      "NEW1_008",
      "KARA_00_10",
      "GVG_041",
      "NEW1_007",
      "GVG_041b",
      "GVG_047",
      "CFM_696",
      "LOE_079",
      "UNG_035",
      "GVG_022",
      "UNG_816",
      "UNG_854",
      "TB_KaraPortal_001",
      "LOE_008",
      "OG_073",
      "EX1_392",
      "UNG_060",
      "EX1_155",
      "EX1_166")




    val st = gs.getSourceAndTarget(vote)
    val source = {
      vote match {
        case future: FutureVote =>
          if (voteExecution)
            gs.getSourceAndTarget(future.copyFutureVote(isFutureCard = false, isFutureEnemyTarget = false, isFutureFriendlyTarget = false, isFuturePosition = false))._1
          else
            st._1

        case actionVote: ActionVote =>
          st._1
      }
    }

    if (ignoreExceptions.contains(source.cardID))
      return true


    val target = st._2
    val targetReq = buildTargetReq(source, target)
    var ifAvailable = false
    var targetNeeded = false
    var weaponNeeded = false
    var enemyWeaponNeeded = false
    var minionSlots = 0
    //Be very careful about one requirement invalidating a vote when it shouldnt
    var invalid = false
    var targetPossible = false

    source.cardInfo.playReqMap.keys.foreach {

      case "REQ_MINIMUM_TOTAL_MINIONS" =>
        if (voteExecution && source.cardInfo.playReqMap("REQ_MINIMUM_TOTAL_MINIONS") > (gs.friendlyPlayer.board.size + gs.enemyPlayer.board.size))
          invalid = true

      case "REQ_TARGET_IF_AVAILABLE_AND_MINIMUM_FRIENDLY_SECRETS" =>
        if (voteExecution) {
          val minimumSecrets = gs.friendlyPlayer.secretsInPlay >= source.cardInfo.playReqMap("REQ_TARGET_IF_AVAILABLE_AND_MINIMUM_FRIENDLY_SECRETS")
          if (minimumSecrets)
            ifAvailable = true
        }
        if (voteEntry)
          targetPossible = true

      case "REQ_TARGET_IF_AVAILABLE_AND_DRAGON_IN_HAND" =>
        if (voteExecution) {
          val dragonInHand = gs.friendlyPlayer.hand.exists(_.cardInfo.race.getOrElse("None") == "DRAGON")
          ifAvailable = dragonInHand
        }
        if (voteEntry)
          targetPossible = true

      case "REQ_WEAPON_EQUIPPED" =>
        if (voteExecution)
          weaponNeeded = true

      case "REQ_ENEMY_WEAPON_EQUIPPED" =>
        if (voteExecution)
          enemyWeaponNeeded = true

      case "REQ_TARGET_FOR_COMBO" =>
        if (gs.friendlyPlayer.isComboActive)
          targetNeeded = true
        if (voteEntry)
          targetPossible = true


      case "REQ_TARGET_IF_AVAILABLE_AND_MINIMUM_FRIENDLY_MINIONS" =>
        if (voteExecution) {
          if (gs.friendlyPlayer.board.size >= source.cardInfo.playReqMap("REQ_TARGET_IF_AVAILABLE_AND_MINIMUM_FRIENDLY_MINIONS"))
            ifAvailable = true
        }
        if (voteEntry)
          targetPossible = true


      case "REQ_NUM_MINION_SLOTS" =>
        if (voteExecution) {
          minionSlots = source.cardInfo.playReqMap("REQ_NUM_MINION_SLOTS")
        }


      case "REQ_ENTIRE_ENTOURAGE_NOT_IN_PLAY" =>
        if (voteExecution) {
          var entourageCounter = 0
          source.cardInfo.entourage.getOrElse(Nil).foreach { case entourageCardID =>
            if (gs.friendlyPlayer.board.exists(_.cardID == entourageCardID))
              entourageCounter += 1
          }
          if (source.cardInfo.entourage.getOrElse(Nil).size == entourageCounter)
            invalid = true
        }


      case "REQ_MINIMUM_ENEMY_MINIONS" =>
        if (voteExecution && source.cardInfo.playReqMap("REQ_MINIMUM_ENEMY_MINIONS") > gs.enemyPlayer.board.size)
          invalid = true


      case "REQ_TARGET_TO_PLAY" =>
        targetNeeded = true
        targetPossible = true

      case "REQ_TARGET_IF_AVAILABE_AND_ELEMENTAL_PLAYED_LAST_TURN" =>
        if (voteExecution && gs.friendlyPlayer.elementalPlayedLastTurn)
          targetNeeded = true
        if (voteEntry)
          targetPossible = true

      case "REQ_TARGET_IF_AVAILABLE" =>
        if (voteExecution)
          ifAvailable = true

        if (voteEntry)
          targetPossible = true


      case _ =>
    }



    if (voteEntry) {
      target match {
        case noCard: NoCard =>
          if (targetNeeded)
            return false

        case future: FutureCard =>
          if (!targetPossible)
            return false

        case card: Card =>
          if (!targetPossible)
            return false
      }
    }



    if (voteExecution) {
      val finalTarget = {
        vote match {
          case future: FutureVote =>
            gs.getSourceAndTarget(future.copyFutureVote(isFutureCard = false, isFutureEnemyTarget = false, isFutureFriendlyTarget = false, isFuturePosition = false))._2

          case actionVote: ActionVote =>
            target
        }
      }


      if (invalid)
        return false
      if (weaponNeeded && !gs.friendlyPlayer.isWeaponEquipped)
        return false
      if (enemyWeaponNeeded && !gs.enemyPlayer.isWeaponEquipped)
        return false

      if (ifAvailable) {
        val allCardsInGame = gs.friendlyPlayer.board ::: gs.enemyPlayer.board ::: List(gs.friendlyPlayer.hero.getOrElse(Constants.emptyCard), gs.friendlyPlayer.heroPower.getOrElse(Constants.emptyCard)) ::: List(gs.enemyPlayer.hero.getOrElse(Constants.emptyCard), gs.enemyPlayer.heroPower.getOrElse(Constants.emptyCard))
        var targetAvailable = false
        for (a <- allCardsInGame.indices) {
          if (!targetAvailable && targetMeetsRequirements(allCardsInGame(a), targetReq))
            targetAvailable = true
        }

        if (targetAvailable)
          targetNeeded = true
        else
          targetNeeded = false
      }


      if (targetNeeded) {
        if (finalTarget == NoCard())
          return false

        if (!targetMeetsRequirements(finalTarget.asInstanceOf[Card], targetReq))
          return false
      }
      else {
        if (finalTarget != NoCard())
          return false
      }



      if ((gs.friendlyPlayer.board.size + minionSlots) > 7)
        return false
    }


    //If it makes it all the way to here, the vote is valid
    true

  }



  def isValidVote(vote: ActionVote, voteEntry: Boolean, voteExecution: Boolean): Boolean = {

    vote match {

      case attack: AttackType =>
        true

      case et:EndTurn =>
        true

      case play: CardPlayType =>
        voteValidation(vote, voteEntry, voteExecution)

      case hp: HeroPowerType =>
        voteValidation(vote, voteEntry, voteExecution)



      case _ =>
        logger.debug(s"Trying to validate unknown type of vote. Vote: $vote. Returning invalid.")
        false
    }


  }
}