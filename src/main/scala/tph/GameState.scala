package tph

import FileReaders.{LogParser, CardInfo, HSDataBase}
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import net.liftweb.json.JsonAST.JObject
import tph.Constants.ActionVotes._



class GameState() extends LazyLogging {
  val config = ConfigFactory.load()
  val accountName = config.getString("tph.hearthstone.accountName")


  ///////////////////-----------------------------State Dependent Variables Below Here-----------------------///////////////////////////////
  var friendlyPlayer = new Player()
  var enemyPlayer = new Player()

  ///////////////////-----------------------------State Dependent Variables Above Here-----------------------///////////////////////////////

  val dataBase = new HSDataBase()

  def getCardByID(cardID: Int): Option[Card] = {

    (friendlyPlayer.hand ::: friendlyPlayer.board ::: List(friendlyPlayer.hero.getOrElse(Constants.emptyCard), friendlyPlayer.heroPower.getOrElse(Constants.emptyCard)) ::: enemyPlayer.hand ::: enemyPlayer.board ::: List(enemyPlayer.hero.getOrElse(Constants.emptyCard), enemyPlayer.heroPower.getOrElse(Constants.emptyCard))).find(_.id == cardID)
  }

  def isChooseOne(vote: ActionVote, st: (HSCard, HSCard)) : Boolean = {
    //cardID "OG_044" is the legendary that plays both chooseone effects. Therefore we act as if it is not a chooseone
    if (friendlyPlayer.board.exists(_.cardID == "OG_044")) {
      false
    }
    else {
      vote match {
        case playVote: CardPlayType =>
          if (st._1.cardInfo.mechanics.getOrElse(Nil).contains("CHOOSE_ONE")) {
            true
          }
          else
            false

        case _ =>
          false
      }
    }
  }



  def getCard(friendly: Boolean, hand: Boolean, number: Int): HSCard = {
    if (friendly) {
      if (hand) {
        friendlyPlayer.hand.find(_.handPosition == number).getOrElse(NoCard())
      }
      else {
        if(number == 0)
          friendlyPlayer.hero.getOrElse(NoCard())
        else
          friendlyPlayer.board.find(_.boardPosition == number).getOrElse(NoCard())
      }
    }
    else {
      if (hand) {
        enemyPlayer.hand.find(_.handPosition == number).getOrElse(NoCard())
      }
      else {
        if(number == 0)
          enemyPlayer.hero.getOrElse(NoCard())
        else
          enemyPlayer.board.find(_.boardPosition == number).getOrElse(NoCard())
      }
    }
  }

  def getSourceAndTarget(vote: ActionVote): (HSCard, HSCard) = {

    vote match {
      case CardPlayWithFriendlyTargetWithPosition(cardVote, friendlyTargetVote, positionVote) =>
        return (getCard(true, true, cardVote), getCard(true, false, friendlyTargetVote))

      case CardPlayWithEnemyTargetWithPosition(cardVote, enemyTargetVote, positionVote) =>
        return (getCard(true, true, cardVote), getCard(false, false, enemyTargetVote))

      case CardPlayWithPosition(cardVote, positionVote) =>
        return (getCard(true, true, cardVote), NoCard())

      case CardPlay(cardVote) =>
        return (getCard(true, true, cardVote), NoCard())

      case CardPlayWithFriendlyTarget(cardVote, friendlyTargetVote) =>
        return (getCard(true, true, cardVote), getCard(true, false, friendlyTargetVote))

      case CardPlayWithEnemyTarget(cardVote, enemyTargetVote) =>
        return (getCard(true, true, cardVote), getCard(false, false, enemyTargetVote))

      case HeroPower() =>
        val heroPowerCard = friendlyPlayer.heroPower.getOrElse(NoCard())
        return (heroPowerCard, NoCard())

      case HeroPowerWithFriendlyTarget(friendlyTargetVote) =>
        val heroPowerCard = friendlyPlayer.heroPower.getOrElse(NoCard())
        return (heroPowerCard, getCard(true, false, friendlyTargetVote))

      case HeroPowerWithEnemyTarget(enemyTargetVote) =>
        val heroPowerCard = friendlyPlayer.heroPower.getOrElse(NoCard())
        return (heroPowerCard, getCard(false, false, enemyTargetVote))

      case NormalAttack(friendlyTargetVote, enemyTargetVote) =>
        return (getCard(true, false, friendlyTargetVote), getCard(false, false, enemyTargetVote))

      case FutureCardPlayWithFriendlyTargetWithPosition(cardVote, friendlyTargetVote, positionVote, isFutureCard, isFutureFriendlyTarget, isFuturePosition) =>

        if (isFutureCard && isFutureFriendlyTarget)
          return (FutureCard(), FutureCard())

        if (isFutureCard && !isFutureFriendlyTarget)
          return (FutureCard(), getCard(true, false, friendlyTargetVote))

        if (!isFutureCard && isFutureFriendlyTarget)
          return (getCard(true, true, cardVote), FutureCard())

        if (!isFutureCard && !isFutureFriendlyTarget)
          return (getCard(true, true, cardVote), getCard(true, false, friendlyTargetVote))

        return (NoCard(), NoCard())


      case FutureCardPlayWithEnemyTargetWithPosition(cardVote, enemyTargetVote, positionVote, isFutureCard, isFutureEnemyTarget, isFuturePosition) =>
        if (isFutureCard && isFutureEnemyTarget)
          return (FutureCard(), FutureCard())

        if (isFutureCard && !isFutureEnemyTarget)
          return (FutureCard(), getCard(false, false, enemyTargetVote))

        if (!isFutureCard && isFutureEnemyTarget)
          return (getCard(true, true, cardVote), FutureCard())

        if (!isFutureCard && !isFutureEnemyTarget)
          return (getCard(true, true, cardVote), getCard(false, false, enemyTargetVote))

        return (NoCard(), NoCard())


      case FutureCardPlayWithPosition(cardVote, positionVote, isFutureCard, isFuturePosition) =>

        if (!isFutureCard)
          return (getCard(true, true, cardVote), NoCard())

        return (FutureCard(), NoCard())

      case FutureCardPlay(card, isFutureCard) =>
        return (FutureCard(), NoCard())


      case FutureCardPlayWithFriendlyTarget(cardVote, friendlyTargetVote, isFutureCard, isFutureFriendlyTarget) =>
        if (isFutureCard && isFutureFriendlyTarget)
          return (FutureCard(), FutureCard())

        if (isFutureCard && !isFutureFriendlyTarget)
          return (FutureCard(), getCard(true, false, friendlyTargetVote))

        if (!isFutureCard && isFutureFriendlyTarget)
          return (getCard(true, true, cardVote), FutureCard())

        if (!isFutureCard && !isFutureFriendlyTarget)
          return (getCard(true, true, cardVote), getCard(true, false, friendlyTargetVote))

        return (NoCard(), NoCard())


      case FutureCardPlayWithEnemyTarget(cardVote, enemyTargetVote, isFutureCard, isFutureEnemyTarget) =>
        if (isFutureCard && isFutureEnemyTarget)
          return (FutureCard(), FutureCard())

        if (isFutureCard && !isFutureEnemyTarget)
          return (FutureCard(), getCard(false, false, enemyTargetVote))

        if (!isFutureCard && isFutureEnemyTarget)
          return (getCard(true, true, cardVote), FutureCard())

        if (!isFutureCard && !isFutureEnemyTarget)
          return (getCard(true, true, cardVote), getCard(false, false, enemyTargetVote))

        return (NoCard(), NoCard())

      case FutureHeroPower() =>
        val heroPowerCard = friendlyPlayer.heroPower.getOrElse(NoCard())
        return (heroPowerCard, NoCard())

      case FutureHeroPowerWithEnemyTarget(enemyTarget, isFutureEnemytarget) =>
        val heroPowerCard = friendlyPlayer.heroPower.getOrElse(NoCard())
        return (heroPowerCard, FutureCard())

      case FutureHeroPowerWithFriendlyTarget(friendlyTarget, isFutureFriendlyTarget) =>
        val heroPowerCard = friendlyPlayer.heroPower.getOrElse(NoCard())
        return (heroPowerCard, FutureCard())


      case FutureNormalAttack(friendlyTargetVote, enemyTargetVote, isFutureFriendlyTarget, isFutureEnemyTarget) =>
        if (isFutureFriendlyTarget && isFutureEnemyTarget)
          return (FutureCard(), FutureCard())

        if (isFutureFriendlyTarget && !isFutureEnemyTarget)
          return (FutureCard(), getCard(false, false, enemyTargetVote))

        if (!isFutureFriendlyTarget && isFutureEnemyTarget)
          return (getCard(true, false, friendlyTargetVote), FutureCard())

        if (!isFutureFriendlyTarget && !isFutureEnemyTarget)
          return (getCard(true, false, friendlyTargetVote), getCard(false, false, enemyTargetVote))

        return (NoCard(), NoCard())


      case _ =>
        return (NoCard(), NoCard())
    }
    (NoCard(), NoCard())
  }

  def setPlayerNumbers(friendlyPlayerNumber: Int): Unit = {
    val enemyPlayerNumber =
      friendlyPlayerNumber match {
        case 1 => 2
        case 2 => 1
        case _ => Constants.INT_UNINIT
      }
    friendlyPlayer = friendlyPlayer.copy(playerNumber = friendlyPlayerNumber)
    enemyPlayer = enemyPlayer.copy(playerNumber = enemyPlayerNumber)
  }

  def gameOver(): Unit = {
    logger.debug("Gameover method ran in GameState")

    friendlyPlayer = new Player()
    enemyPlayer = new Player()
  }

  def getCardInfo(cardID: String): CardInfo = {
    if (cardID != Constants.STRING_UNINIT)
      dataBase.cardIDMap(cardID)
    else
      Constants.emptyCardInfo
  }

  def update(newGS:GameState) = {
    friendlyPlayer = newGS.friendlyPlayer
    enemyPlayer = newGS.enemyPlayer
  }


}
