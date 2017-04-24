package tph


import FileReaders.CardInfo

import scala.collection.mutable.ListBuffer

/*
Characteristic of games
Richard garfield
 */





object Constants {

  val INT_UNINIT = 500
  val STRING_UNINIT = "Constant Uninitialized"

  def emptyCardInfo = CardInfo(None, None, None, None, None, None, None, None, None, None)
  def emptyCard = Card(Constants.STRING_UNINIT, Constants.INT_UNINIT, Constants.INT_UNINIT, Constants.INT_UNINIT, Constants.INT_UNINIT, Constants.STRING_UNINIT)

  val booleanToIntMap = Map[Boolean, Int](true -> 1, false -> 0)

  trait Vote


  object TestConstants {

    def createFriendlyHandCard(position: Int): Card = new Card(s"Friendly Card $position", position, position, Constants.INT_UNINIT, 1, Constants.STRING_UNINIT)

    def createFriendlyBoardCard(position: Int): Card = new Card(s"Friendly Minion $position", 10 + position, Constants.INT_UNINIT, position, 1, Constants.STRING_UNINIT)

    def createEnemyHandCard(position: Int): Card = new Card(s"Enemy Card $position", 20 + position, position, Constants.INT_UNINIT, 2, Constants.STRING_UNINIT)

    def createEnemyBoardCard(position: Int): Card = new Card(s"Enemy Minion $position", 30 + position, Constants.INT_UNINIT, position, 2, Constants.STRING_UNINIT)



    def defaultGameState: GameState = {
      val gs = new GameState()
      gs.friendlyPlayer = new Player(1, hand = List(
        createFriendlyHandCard(1),
        createFriendlyHandCard(2),
        createFriendlyHandCard(3),
        createFriendlyHandCard(4),
        createFriendlyHandCard(5),
        createFriendlyHandCard(6)), board = List(
        createFriendlyBoardCard(1),
        createFriendlyBoardCard(2),
        createFriendlyBoardCard(3),
        createFriendlyBoardCard(4)))

      gs.enemyPlayer = new Player(2, hand = List(
        createEnemyHandCard(1),
        createEnemyHandCard(2),
        createEnemyHandCard(3),
        createEnemyHandCard(4),
        createEnemyHandCard(5),
        createEnemyHandCard(6)), board = List(
        createEnemyBoardCard(1),
        createEnemyBoardCard(2),
        createEnemyBoardCard(3),
        createEnemyBoardCard(4)))

      gs
    }
  }

  object InfluenceFactors {
    val previousDecisionBonus = .5
    val maximumVoterHistory = 20
    val substantialVoterFactor = .3
    val trollFactor = .4
  }

  object IRCState {
    val minimumDelayBeforeExecution = 5
  }

  object MenuNames {

    val MAIN_MENU = "Main Menu"
    val QUEST_MENU = "Quest Menu"
    val IN_GAME = "In Game"
    val COLLECTION_MENU = "Collection Menu"
    val PLAY_MENU = "Play Menu"
    val ARENA_MENU = "Arena Menu"
    val DECK_CREATION = "Deck Creation"
    val SHOP_MENU = "Shop Menu"
    val OPEN_PACKS_MENU = "Open Packs Menu"
  }


  object ActionVotes {

    trait CardPlayType

    trait HeroPowerType

    trait AttackType

    trait FutureVote extends ActionVote{
      def copyFutureVote(card:Int = card, friendlyTarget:Int = friendlyTarget, enemyTarget:Int = enemyTarget, position:Int = position, isFutureCard:Boolean = isFutureCard, isFutureFriendlyTarget:Boolean = isFutureFriendlyTarget, isFutureEnemyTarget:Boolean = isFutureEnemyTarget, isFuturePosition:Boolean = isFuturePosition): FutureVote
      def convertToNonFuture(): ActionVote
      val isFutureCard = false
      val isFutureFriendlyTarget = false
      val isFutureEnemyTarget = false
      val isFuturePosition = false

    }

    trait ActionVote extends Vote {

      def copyVote(card:Int = card, friendlyTarget:Int = friendlyTarget, enemyTarget:Int = enemyTarget, position:Int = position): ActionVote


      val card = INT_UNINIT
      val friendlyTarget = INT_UNINIT
      val enemyTarget = INT_UNINIT
      val position = INT_UNINIT
    }

    case class UpdateVotes(oldFriendlyPlayer: Player, oldEnemyPlayer: Player, currentGS: GameState) extends ActionVote {
      def friendlyHandChangeMap = oldFriendlyPlayer.hand.foldLeft(Map[Int, Int]()) { case (r, c) =>
        c match {
          case oldCard =>
            val newCard = currentGS.getCardByID(oldCard.id)
            if (newCard.nonEmpty && newCard.get.handPosition != Constants.INT_UNINIT) {
              r + (oldCard.handPosition -> newCard.get.handPosition)
            }
            else {
              r
            }
        }
      }


      def friendlyHandKnownFutures = currentGS.friendlyPlayer.hand.foldLeft(List[Int]()) { case (r, c) =>
        c match {
          case newCard =>
            if (oldFriendlyPlayer.hand.exists(_.id == newCard.id)) {
              r
            }
            else {
              newCard.handPosition :: r
            }
        }
      }

      def friendlyBoardChangeMap = oldFriendlyPlayer.board.foldLeft(Map[Int, Int]()) { case (r, c) =>
        c match {
          case oldMinion =>
            val newMinion = currentGS.getCardByID(oldMinion.id)
            if (newMinion.nonEmpty && newMinion.get.boardPosition != Constants.INT_UNINIT) {
              r + (oldMinion.boardPosition -> newMinion.get.boardPosition)
            }
            else {
              r
            }
        }
      }


      def friendlyBoardKnownFutures = currentGS.friendlyPlayer.board.foldLeft(List[Int]()) { case (r, c) =>
        c match {
          case newMinion =>
            if (oldFriendlyPlayer.board.exists(_.id == newMinion.id)) {
              r
            }
            else {
              newMinion.boardPosition :: r
            }
        }
      }



      def enemyBoardChangeMap = oldEnemyPlayer.board.foldLeft(Map[Int, Int]()) { case (r, c) =>
        c match {
          case oldCard =>
            val newCard = currentGS.getCardByID(oldCard.id)
            if (newCard.nonEmpty && newCard.get.boardPosition != Constants.INT_UNINIT) {
              r + (oldCard.boardPosition -> newCard.get.boardPosition)
            }
            else {
              r
            }
        }
      }


      def enemyBoardKnownFutures = currentGS.enemyPlayer.board.foldLeft(List[Int]()) { case (r, c) =>
        c match {
          case newMinion =>
            if (oldEnemyPlayer.board.exists(_.id == newMinion.id)) {
              r
            }
            else {
              newMinion.boardPosition :: r
            }
        }
      }


      override def copyVote(card:Int = card, friendlyTarget:Int = friendlyTarget, enemyTarget:Int = enemyTarget, position:Int = position): ActionVote = this.copy()}


    case class Hurry() extends ActionVote{
      override def copyVote(card:Int = card, friendlyTarget:Int = friendlyTarget, enemyTarget:Int = enemyTarget, position:Int = position): ActionVote = this}

    case class Discover(override val card: Int) extends ActionVote{
      override def copyVote(card:Int = card, friendlyTarget:Int = friendlyTarget, enemyTarget:Int = enemyTarget, position:Int = position): ActionVote = this.copy()}

    case class ChooseOne(override val card: Int) extends ActionVote{
      override def copyVote(card:Int = card, friendlyTarget:Int = friendlyTarget, enemyTarget:Int = enemyTarget, position:Int = position): ActionVote = this.copy(card = card)}

    case class CardPlayWithFriendlyTargetWithPosition(override val card: Int, override val friendlyTarget: Int, override val position: Int) extends ActionVote with CardPlayType{
      override def copyVote(card:Int = card, friendlyTarget:Int = friendlyTarget, enemyTarget:Int = enemyTarget, position:Int = position): ActionVote = this.copy(card = card, friendlyTarget = friendlyTarget, position = position)}

    case class CardPlayWithEnemyTargetWithPosition(override val card: Int, override val enemyTarget: Int, override val position: Int) extends ActionVote with CardPlayType{
      override def copyVote(card:Int = card, friendlyTarget:Int = friendlyTarget, enemyTarget:Int = enemyTarget, position:Int = position): ActionVote = this.copy(card = card, enemyTarget = enemyTarget, position = position)}

    case class CardPlayWithPosition(override val card: Int, override val position: Int) extends ActionVote with CardPlayType{
      override def copyVote(card:Int = card, friendlyTarget:Int = friendlyTarget, enemyTarget:Int = enemyTarget, position:Int = position): ActionVote = this.copy(card = card, position = position)}

    case class CardPlay(override val card: Int) extends ActionVote with CardPlayType{
      override def copyVote(card:Int = card, friendlyTarget:Int = friendlyTarget, enemyTarget:Int = enemyTarget, position:Int = position): ActionVote = this.copy(card = card)}

    case class CardPlayWithEnemyTarget(override val card: Int, override val enemyTarget: Int) extends ActionVote with CardPlayType{
      override def copyVote(card:Int = card, friendlyTarget:Int = friendlyTarget, enemyTarget:Int = enemyTarget, position:Int = position): ActionVote = this.copy(card =card, enemyTarget = enemyTarget)}

    case class CardPlayWithFriendlyTarget(override val card: Int, override val friendlyTarget: Int) extends ActionVote with CardPlayType{
      override def copyVote(card:Int = card, friendlyTarget:Int = friendlyTarget, enemyTarget:Int = enemyTarget, position:Int = position): ActionVote = this.copy(card = card, friendlyTarget = friendlyTarget)}

    case class HeroPower() extends ActionVote with HeroPowerType{
      override def copyVote(card:Int = card, friendlyTarget:Int = friendlyTarget, enemyTarget:Int = enemyTarget, position:Int = position): ActionVote = this}


    case class HeroPowerWithFriendlyTarget(override val friendlyTarget: Int) extends ActionVote with HeroPowerType{
      override def copyVote(card:Int = card, friendlyTarget:Int = friendlyTarget, enemyTarget:Int = enemyTarget, position:Int = position): ActionVote = this.copy(friendlyTarget = friendlyTarget)}

    case class HeroPowerWithEnemyTarget(override val enemyTarget: Int) extends ActionVote with HeroPowerType{
      override def copyVote(card:Int = card, friendlyTarget:Int = friendlyTarget, enemyTarget:Int = enemyTarget, position:Int = position): ActionVote = this.copy(enemyTarget = enemyTarget)}

    case class NormalAttack(override val friendlyTarget: Int, override val enemyTarget: Int) extends ActionVote with AttackType{
      override def copyVote(card:Int = card, friendlyTarget:Int = friendlyTarget, enemyTarget:Int = enemyTarget, position:Int = position): ActionVote = this.copy(friendlyTarget = friendlyTarget, enemyTarget = enemyTarget)}

    case class FutureCardPlayWithFriendlyTargetWithPosition(override val card: Int, override val friendlyTarget: Int, override val position: Int, override val isFutureCard: Boolean, override val isFutureFriendlyTarget: Boolean, override val isFuturePosition: Boolean) extends FutureVote with CardPlayType{
      override def copyFutureVote(card: Int, friendlyTarget: Int, enemyTarget: Int, position: Int, isFutureCard: Boolean, isFutureFriendlyTarget: Boolean, isFutureEnemyTarget: Boolean, isFuturePosition: Boolean): FutureVote = this.copy(card = card, friendlyTarget = friendlyTarget, position = position, isFutureCard = isFutureCard, isFutureFriendlyTarget = isFutureFriendlyTarget, isFuturePosition = isFuturePosition)
      override def copyVote(card: Int, friendlyTarget: Int, enemyTarget: Int, position: Int): ActionVote = this.copy(card = card, friendlyTarget = friendlyTarget, position = position)
      override def convertToNonFuture(): ActionVote = CardPlayWithFriendlyTargetWithPosition(card, friendlyTarget, position)
    }

    case class FutureCardPlayWithEnemyTargetWithPosition(override val card: Int, override val enemyTarget: Int, override val position: Int, override val isFutureCard: Boolean, override val isFutureEnemyTarget: Boolean, override val isFuturePosition: Boolean) extends FutureVote with CardPlayType {
      override def copyFutureVote(card: Int, friendlyTarget: Int, enemyTarget: Int, position: Int, isFutureCard: Boolean, isFutureFriendlyTarget: Boolean, isFutureEnemyTarget: Boolean, isFuturePosition: Boolean): FutureVote = this.copy(card = card, enemyTarget = enemyTarget, position = position, isFutureCard = isFutureCard, isFutureEnemyTarget = isFutureEnemyTarget, isFuturePosition = isFuturePosition)
      override def copyVote(card: Int, friendlyTarget: Int, enemyTarget: Int, position: Int): ActionVote = this.copy(card = card, enemyTarget = enemyTarget, position = position)
      override def convertToNonFuture(): ActionVote = CardPlayWithEnemyTargetWithPosition(card,enemyTarget,position)
    }

    case class FutureCardPlayWithPosition(override val card: Int, override val position: Int, override val isFutureCard: Boolean, override val isFuturePosition: Boolean) extends FutureVote with CardPlayType {
      override def copyFutureVote(card: Int, friendlyTarget: Int, enemyTarget: Int, position: Int, isFutureCard: Boolean, isFutureFriendlyTarget: Boolean, isFutureEnemyTarget: Boolean, isFuturePosition: Boolean): FutureVote = this.copy(card = card, position = position, isFutureCard = isFutureCard, isFuturePosition = isFuturePosition)
      override def copyVote(card: Int, friendlyTarget: Int, enemyTarget: Int, position: Int): ActionVote = this.copy(card = card, position = position, isFutureCard = isFutureCard, isFuturePosition = isFuturePosition)
      override def convertToNonFuture(): ActionVote = CardPlayWithPosition(card,position)
    }

    case class FutureCardPlay(override val card: Int, override val isFutureCard:Boolean = true) extends FutureVote with CardPlayType {
      override def copyFutureVote(card: Int, friendlyTarget: Int, enemyTarget: Int, position: Int, isFutureCard: Boolean, isFutureFriendlyTarget: Boolean, isFutureEnemyTarget: Boolean, isFuturePosition: Boolean): FutureVote = this.copy(card = card, isFutureCard = isFutureCard)
      override def copyVote(card: Int, friendlyTarget: Int, enemyTarget: Int, position: Int): ActionVote = this.copy(card = card)
      override def convertToNonFuture(): ActionVote = CardPlay(card)
    }

    case class FutureCardPlayWithFriendlyTarget(override val card: Int, override val friendlyTarget: Int, override val isFutureCard: Boolean, override val isFutureFriendlyTarget: Boolean) extends FutureVote with CardPlayType {
      override def copyFutureVote(card: Int, friendlyTarget: Int, enemyTarget: Int, position: Int, isFutureCard: Boolean, isFutureFriendlyTarget: Boolean, isFutureEnemyTarget: Boolean, isFuturePosition: Boolean): FutureVote = this.copy(card = card, friendlyTarget = friendlyTarget, isFutureCard = isFutureCard, isFutureFriendlyTarget = isFutureFriendlyTarget)
      override def copyVote(card: Int, friendlyTarget: Int, enemyTarget: Int, position: Int): ActionVote = this.copy(card = card, friendlyTarget = friendlyTarget)
      override def convertToNonFuture(): ActionVote = CardPlayWithFriendlyTarget(card,friendlyTarget)
    }

    case class FutureCardPlayWithEnemyTarget(override val card: Int, override val enemyTarget: Int, override val isFutureCard: Boolean, override val isFutureEnemyTarget: Boolean) extends FutureVote with CardPlayType {
      override def copyFutureVote(card: Int, friendlyTarget: Int, enemyTarget: Int, position: Int, isFutureCard: Boolean, isFutureFriendlyTarget: Boolean, isFutureEnemyTarget: Boolean, isFuturePosition: Boolean): FutureVote = this.copy(card = card, enemyTarget = enemyTarget, isFutureCard = isFutureCard, isFutureEnemyTarget = isFutureEnemyTarget)
      override def copyVote(card: Int, friendlyTarget: Int, enemyTarget: Int, position: Int): ActionVote = this.copy(card = card, enemyTarget = enemyTarget)
      override def convertToNonFuture(): ActionVote = CardPlayWithEnemyTarget(card,enemyTarget)
    }

    case class FutureHeroPower() extends FutureVote with HeroPowerType {
      override def copyFutureVote(card: Int, friendlyTarget: Int, enemyTarget: Int, position: Int, isFutureCard: Boolean, isFutureFriendlyTarget: Boolean, isFutureEnemyTarget: Boolean, isFuturePosition: Boolean): FutureVote = this
      override def copyVote(card: Int, friendlyTarget: Int, enemyTarget: Int, position: Int): ActionVote = this
      override def convertToNonFuture(): ActionVote = HeroPower()
    }

    case class FutureHeroPowerWithFriendlyTarget(override val friendlyTarget: Int, override val isFutureFriendlyTarget:Boolean = true) extends FutureVote with HeroPowerType {
      override def copyFutureVote(card: Int, friendlyTarget: Int, enemyTarget: Int, position: Int, isFutureCard: Boolean, isFutureFriendlyTarget: Boolean, isFutureEnemyTarget: Boolean, isFuturePosition: Boolean): FutureVote = this.copy(friendlyTarget = friendlyTarget, isFutureFriendlyTarget = isFutureFriendlyTarget)
      override def copyVote(card: Int, friendlyTarget: Int, enemyTarget: Int, position: Int): ActionVote = this.copy(friendlyTarget = friendlyTarget)
      override def convertToNonFuture(): ActionVote = HeroPowerWithFriendlyTarget(friendlyTarget)
    }

    case class FutureHeroPowerWithEnemyTarget(override val enemyTarget: Int, override val isFutureEnemyTarget:Boolean = true) extends FutureVote with HeroPowerType {
      override def copyFutureVote(card: Int, friendlyTarget: Int, enemyTarget: Int, position: Int, isFutureCard: Boolean, isFutureFriendlyTarget: Boolean, isFutureEnemyTarget: Boolean, isFuturePosition: Boolean): FutureVote = this.copy(enemyTarget = enemyTarget, isFutureEnemyTarget = isFutureEnemyTarget)
      override def copyVote(card: Int, friendlyTarget: Int, enemyTarget: Int, position: Int): ActionVote = this.copy(enemyTarget = enemyTarget)
      override def convertToNonFuture(): ActionVote = HeroPowerWithEnemyTarget(enemyTarget)
    }

    case class FutureNormalAttack(override val friendlyTarget: Int, override val enemyTarget: Int, override val isFutureFriendlyTarget: Boolean, override val isFutureEnemyTarget: Boolean) extends FutureVote with AttackType {
      override def copyFutureVote(card: Int, friendlyTarget: Int, enemyTarget: Int, position: Int, isFutureCard: Boolean, isFutureFriendlyTarget: Boolean, isFutureEnemyTarget: Boolean, isFuturePosition: Boolean): FutureVote = this.copy(friendlyTarget = friendlyTarget, enemyTarget= enemyTarget, isFutureFriendlyTarget = isFutureFriendlyTarget, isFutureEnemyTarget = isFutureEnemyTarget)
      override def copyVote(card: Int, friendlyTarget: Int, enemyTarget: Int, position: Int): ActionVote = this.copy(friendlyTarget = friendlyTarget, enemyTarget = enemyTarget)
      override def convertToNonFuture(): ActionVote = NormalAttack(friendlyTarget, enemyTarget)
    }

    case class ActionUninit() extends ActionVote() {
      override def copyVote(card: Int, friendlyTarget: Int, enemyTarget: Int, position: Int): ActionVote = this
    }

    case class MulliganVote(first: Boolean, second: Boolean, third: Boolean, fourth: Boolean) extends ActionVote() {
      override def copyVote(card: Int, friendlyTarget: Int, enemyTarget: Int, position: Int): ActionVote = this
    }

    case class EndTurn() extends ActionVote() {
      override def copyVote(card: Int, friendlyTarget: Int, enemyTarget: Int, position: Int): ActionVote = this
    }

    case class RemoveVote(vote: ActionVote) extends ActionVote() {
      override def copyVote(card: Int, friendlyTarget: Int, enemyTarget: Int, position: Int): ActionVote = this
    }

    case class RemoveLastVote() extends ActionVote() {
      override def copyVote(card: Int, friendlyTarget: Int, enemyTarget: Int, position: Int): ActionVote = this
    }

    case class RemoveAllVotes() extends ActionVote() {
      override def copyVote(card: Int, friendlyTarget: Int, enemyTarget: Int, position: Int): ActionVote = this
    }

    case class Pattern(indexList: List[(ActionVote, Int)]) {
      def combine(otherPattern: Pattern, offset: Int): Pattern = {
        if (offset > 0) {
          val arr = new Array[(ActionVote, Int)](otherPattern.indexList.last._2 + offset + 1)
          indexList.foreach { case element =>
            arr(element._2) = (element._1, element._2)
          }

          otherPattern.indexList.foreach { case element =>
            arr(element._2 + offset) = (element._1, element._2 + offset)
          }

          val newPatternIndexList = arr.foldLeft(List[(ActionVote, Int)]()) { (r, c) =>

            if (c != null)
              r :+ c
            else
              r
          }

          Pattern(newPatternIndexList)
        }
        else {
          val arr = new Array[(ActionVote, Int)](indexList.last._2 - offset)
          indexList.foreach { case element =>
            //Minus offset because it is negative, so we are really adding the offset
            arr(element._2 - offset) = (element._1, element._2 - offset)
          }

          otherPattern.indexList.foreach { case element =>
            arr(element._2) = (element._1, element._2)
          }

          val newPatternIndexList = arr.foldLeft(List[(ActionVote, Int)]()) { (r, c) =>

            if (c != null)
              r :+ c
            else
              r
          }

          Pattern(newPatternIndexList)
        }
      }
    }

    case class PatternVote() extends ActionVote() {
      override def copyVote(card: Int, friendlyTarget: Int, enemyTarget: Int, position: Int): ActionVote = this
    }

  }

  object MiscVotes {

    case class UninitVote() extends Vote()

  }

  object EmojiVotes {

    trait EmojiVote extends Vote

    case class Greetings() extends EmojiVote()

    case class Thanks() extends EmojiVote()

    case class WellPlayed() extends EmojiVote()

    case class Wow() extends EmojiVote()

    case class Oops() extends EmojiVote()

    case class Threaten() extends EmojiVote()

    case class EmojiUninit() extends EmojiVote()

  }


  object MenuVotes {

    trait MenuVote extends Vote

    case class MenuUninit() extends MenuVote()

    case class Back() extends MenuVote()

    case class Play() extends MenuVote()

    case class Collection() extends MenuVote()

    case class Shop() extends MenuVote()

    case class OpenPacks() extends MenuVote()

    case class QuestLog() extends MenuVote()

    case class Casual() extends MenuVote()

    case class Ranked() extends MenuVote()

    case class Deck(deckNumber: Int) extends MenuVote()

    case class FirstPage() extends MenuVote()

    case class SecondPage() extends MenuVote()

    case class Quest(number: Int) extends MenuVote()

  }


  object LogFileReaderStrings {

    object HSActionStrings {

      //Friendly HSActions
      val FRIENDLY_MINION_CONTROLLED ="""^.+\[name=(.+) id=(\d+) zone=PLAY zonePos=(\d+) .+ zone from FRIENDLY PLAY -> OPPOSING PLAY""".r
      val FRIENDLY_CARD_DRAWN = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - processing index=\d+ change=powerTask=\[power=\[type=TAG_CHANGE entity=\[id=\d+ cardId=.+ name=.+\] tag=ZONE_POSITION value=\d+\] complete=False\] entity=\[name=(.+) id=(\d+) zone=HAND zonePos=\d+ cardId=(.+) player=(\d+)\] srcZoneTag=INVALID srcPos= dstZoneTag=INVALID dstPos=(\d+)""".r
      val FRIENDLY_CARD_RETURN = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - id=.+ local=.+ \[name=(.+) id=(\d+) zone=HAND zonePos=.+ cardId=.+ player=(\d+)\] zone from FRIENDLY PLAY -> FRIENDLY HAND""".r
      val FRIENDLY_MULLIGAN_REDRAW = """\[Power\] PowerTaskList.DebugPrintPower\(\) -     HIDE_ENTITY - Entity=\[name=(.+) id=(\d+) zone=HAND zonePos=(\d+) cardId=.+ player=(\d+)] tag=ZONE value=DECK""".r
      val FRIENDLY_CARD_CREATED = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - processing index=\d+ change=powerTask=\[power=\[type=FULL_ENTITY entity=\[id=\d+ cardId=.+ name=.+\] tags=System.Collections.Generic.List\`1\[Network\+Entity\+Tag\]\] complete=False\] entity=\[name=(.+) id=(\d+) zone=HAND zonePos=\d+ cardId=(.+) player=(\d+)\] srcZoneTag=INVALID srcPos= dstZoneTag=HAND dstPos=(\d+)""".r

      //Enemy HSActions
      val ENEMY_MINION_CONTROLLED ="""^.+\[name=(.+) id=(\d+) zone=PLAY zonePos=(\d+) .+ zone from OPPOSING PLAY -> FRIENDLY PLAY""".r
      val ENEMY_CARD_DRAWN = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - processing index=\d+ change=powerTask=\[power=\[type=TAG_CHANGE entity=\[id=\d+ cardId= name=UNKNOWN ENTITY \[cardType=INVALID\]\] tag=ZONE_POSITION value=\d+\] complete=False\] entity=\[name=UNKNOWN ENTITY \[cardType=INVALID\] id=(\d+) zone=HAND zonePos=\d+ cardId= player=(\d+)\] srcZoneTag=INVALID srcPos= dstZoneTag=INVALID dstPos=(\d+)""".r
      val ENEMY_MULLIGAN_REDRAW = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - id=\d+ local=False \[name=UNKNOWN ENTITY \[cardType=INVALID\] id=(\d+) zone=DECK zonePos=\d+ cardId= player=(\d+)\] zone from OPPOSING HAND -> OPPOSING DECK""".r
      val ENEMY_CARD_RETURN = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - id=.+ local=.+ \[name=(.+) id=(\d+) zone=HAND zonePos=.+ cardId=(.+) player=(\d+)\] zone from OPPOSING PLAY -> OPPOSING HAND""".r
      val KNOWN_ENEMY_CARD_PLAYED = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - processing index=\d+ change=powerTask=\[power=\[type=TAG_CHANGE entity=\[id=\d+ cardId=.+ name=.+\] tag=JUST_PLAYED value=1\] complete=False\] entity=\[name=(.+) id=(\d+) zone=PLAY zonePos=(\d+) cardId=.+ player=(\d+)\] srcZoneTag=INVALID srcPos= dstZoneTag=INVALID dstPos=""".r
      val ENEMY_CARD_CREATED = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - processing index=\d+ change=powerTask=\[power=\[type=.+ entity=\[id=\d+ cardId= name=UNKNOWN ENTITY \[cardType=INVALID\]\] tag.+ complete=False\] entity=\[name=UNKNOWN ENTITY \[cardType=INVALID\] id=(\d+) zone=HAND zonePos=\d+ cardId= player=(\d+)\] srcZoneTag=INVALID srcPos= dstZoneTag=HAND dstPos=(\d+)""".r


      //Neutral HSActions
      val CARD_DRAWN = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - processing index=\d+ change=powerTask=\[power=\[type=TAG_CHANGE entity=\[id=\d+ cardId=.+ name=.+\] tag=ZONE_POSITION value=\d+\] complete=False\] entity=\[name=(.+) id=(\d+) zone=HAND zonePos=\d+ cardId=.+ player=(\d+)\] srcZoneTag=INVALID srcPos= dstZoneTag=INVALID dstPos=(\d+)""".r
      val SECRET_PLAYED = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - TRANSITIONING card .+id=(\d+).+zone=SECRET zonePos=\d+.+player=(\d+)\] to .+ SECRET""".r
      val RETURNED_CARD_PLAYED = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - processing index=\d+ change=powerTask=\[power=\[type=TAG_CHANGE entity=\[id=\d+ cardId=.+ name=.+\] tag=COST value=\d+\] complete=False\] entity=\[name=(.+) id=(\d+) zone=PLAY zonePos=(\d+) cardId=(.+) player=(\d+)\] srcZoneTag=INVALID srcPos= dstZoneTag=INVALID dstPos=""".r
      val CARD_PLAYED = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - processing index=\d+ change=powerTask=\[power=\[type=TAG_CHANGE entity=\[id=\d+ cardId=.+ name=.+\] tag=JUST_PLAYED value=1\] complete=False\] entity=\[name=(.+) id=(\d+) zone=PLAY zonePos=(\d+) cardId=(.+) player=(\d+)\] srcZoneTag=INVALID srcPos= dstZoneTag=INVALID dstPos=""".r
      val CARD_DEATH = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - TRANSITIONING card \[name=(.+) id=(\d+) zone=GRAVEYARD zonePos=\d+ cardId=.+ player=(\d+)] to .+ GRAVEYARD""".r
      val MINION_SUMMONED = """\[Power\] PowerTaskList.DebugPrintPower\(\) -     FULL_ENTITY - Updating \[name=(.+) id=(\d+) zone=PLAY zonePos=(\d+) cardId=(.+) player=(\d+)] CardID=.+""".r
      val TRANSFORM ="""\[Power\] PowerTaskList.DebugPrintPower\(\) -     TAG_CHANGE Entity=\[name=(.+) id=(\d+) zone=PLAY zonePos=(\d+) cardId=(.+) player=\d+] tag=LINKED_ENTITY value=(\d+)""".r
      val GAME_OVER = "[Power] GameState.DebugPrintPower() -     TAG_CHANGE Entity=GameEntity tag=NEXT_STEP value=FINAL_GAMEOVER"
      val DECK_TO_BOARD = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - id=\d+ local=False \[name=(.+) id=(\d+) zone=PLAY zonePos=0 cardId=(.+) player=(\d+)\] zone from .+ DECK -> .+ PLAY""".r
      val BOARD_SETASIDE_REMOVAL = """\[Power\] PowerProcessor.DoTaskListForCard\(\) - unhandled BlockType PLAY for sourceEntity \[name=(.+) id=(\d+) zone=SETASIDE zonePos=0 cardId=.+ player=(\d+)\]""".r
      val HAND_SETASIDE_REMOVAL = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - id=\d+ local=False \[name=.+ id=(\d+) zone=SETASIDE zonePos=\d+ cardId= player=(\d+)] zone from .+ HAND -> """.r
      val FROZEN = """\[Power\] PowerTaskList.DebugPrintPower\(\) -     TAG_CHANGE Entity=\[name=.+ id=(\d+) zone=PLAY zonePos=\d+ cardId=.+ player=(\d+)\] tag=FROZEN value=(\d+)""".r
      val SECRET_DESTROYED = """\[Power\] PowerTaskList.DebugPrintPower\(\) - BLOCK_START BlockType=TRIGGER Entity=\[name=.+ id=\d+ zone=SECRET zonePos=\d+ cardId=.+ player=(\d+)\] EffectCardId= EffectIndex=\d+ Target=\d+""".r
      val MINION_DAMAGED = """\[Power\] PowerTaskList.DebugPrintPower\(\) -     TAG_CHANGE Entity=\[name=.+ id=(\d+) zone=PLAY zonePos=\d+ cardId=.+ player=(\d+)\] tag=DAMAGE value=(\d+)""".r
      val MINION_STEALTHED = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - processing index=\d+ change=powerTask=\[power=\[type=TAG_CHANGE entity=\[id=\d+ cardId=.+ name=.+\] tag=CANT_BE_ATTACKED value=(\d+)\] complete=False\] entity=\[name=.+ id=(\d+) zone=PLAY zonePos=\d+ cardId=.+ player=(\d+)\] srcZoneTag=INVALID srcPos= dstZoneTag=INVALID dstPos=""".r
      val TAUNT_CHANGE = """\[Power\] GameState.DebugPrintPower\(\) -         TAG_CHANGE Entity=\[name=.+ id=(\d+) zone=PLAY zonePos=\d+ cardId=.+ player=(\d+)\] tag=TAUNT value=(\d+)""".r
      val DEATHRATTLE_CHANGE = """\[Power\] PowerTaskList.DebugPrintPower\(\) -     TAG_CHANGE Entity=\[name=.+ id=(\d+) zone=PLAY zonePos=\d+ cardId=.+ player=(\d+)\] tag=DEATHRATTLE value=(\d+)""".r
      val WEAPON_EQUIPPED = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - id=\d+ local=.+ \[name=.+ id=(\d+) zone=.+ zonePos=\d+ cardId=.+ player=(\d+)\] zone from.+-> .+ PLAY \(Weapon\)""".r
      val WEAPON_DESTROYED = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - id=\d+ local=.+ \[name=.+ id=(\d+) zone=GRAVEYARD zonePos=\d+ cardId=.+ player=(\d+)\] zone from .+ PLAY \(Weapon\) -> .+ GRAVEYARD""".r
      val COMBO_ACTIVE = """\[Power\] GameState.DebugPrintPower\(\) -     TAG_CHANGE Entity=(.+) tag=COMBO_ACTIVE value=(\d+)""".r
      val NEW_HERO_POWER = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - TRANSITIONING card \[name=.+ id=(\d+) zone=PLAY zonePos=0 cardId=(.+) player=(\d+)\] to .+ PLAY \(Hero Power\)""".r
      val CHANGE_ATTACK_VALUE = """\[Power\] PowerTaskList.DebugPrintPower\(\) -     TAG_CHANGE Entity=\[name=.+ id=(.+) zone=.+ zonePos=(\d+) cardId=.+ player=(\d+)\] tag=ATK value=(\d+)""".r
      val NEW_HERO = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - TRANSITIONING card \[name=(.+) id=(\d+) zone=PLAY zonePos=0 cardId=(.+) player=(\d+)\] to (.+) PLAY \(Hero\)""".r
      val REPLACE_HERO = """\[Power\] PowerTaskList.DebugPrintPower\(\) -     TAG_CHANGE Entity=\[name=.+ id=(\d+) zone=PLAY zonePos=0 cardId=(.+) player=(\d+)\] tag=LINKED_ENTITY value=(\d+)""".r



      val OPTION_CHOICE =  """\[Power\] GameState.DebugPrintOptions\(\) -   option (\d+) type=(.+) mainEntity=(.*) error=(.*) errorParam=(.*)""".r
      val OPTION_TARGET = """\[Power\] GameState.DebugPrintOptions\(\) -     target (\d+) entity=(.*) error=(.*) errorParam=(.*)""".r
      val SUBOPTION = """\[Power\] GameState.DebugPrintOptions\(\) -     subOption (\d+) entity=(.*) error=(.*) errorParam=(.*)""".r




    }

    object GameStateStrings {


      val MULLIGAN_START = "[Power] PowerTaskList.DebugPrintPower() -     TAG_CHANGE Entity=GameEntity tag=STEP value=BEGIN_MULLIGAN"
      val MULLIGAN_OPTION = """\[Power\] PowerTaskList.DebugPrintPower\(\) -     SHOW_ENTITY - Updating Entity=\[name=UNKNOWN ENTITY \[cardType=INVALID\] id=\d+ zone=DECK zonePos=0 cardId= player=\d+\] CardID=.+""".r
      val DISCOVER_OPTION = """\[Power\] GameState.DebugPrintEntityChoices\(\) -   Entities\[(\d+)\]=\[name=.+ id=\d+ zone=SETASIDE zonePos=0 cardId=.+ player=\d+\]""".r
      val CHOOSE_ONE_OPTION = """""".r
      val CHOOSE_ONE_CHOOSE = """""".r
      val TURN_START = """\[Power\] PowerTaskList.DebugPrintPower\(\) -     TAG_CHANGE Entity=(.+) tag=CURRENT_PLAYER value=1""".r
      val TURN_END = """\[Power\] PowerTaskList.DebugPrintPower\(\) -     TAG_CHANGE Entity=(.+) tag=CURRENT_PLAYER value=0""".r
    }

    object MiscStrings {
      val ENEMY_CARD_RETURN = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - id=.+ local=.+ \[name=(.+) id=(\d+) zone=HAND zonePos=.+ cardId=.+ player=(\d+)\] zone from OPPOSING PLAY -> OPPOSING HAND""".r
      val OLD_ZONE_CHANGE = """^\[Power\] PowerTaskList.+TAG_CHANGE Entity=.+id=(\d+).+zone=(.+) zonePos=.+ player=(\d+)\] tag=ZONE value=(.+)""".r
      val ZONE_CHANGE = """\[Zone\] ZoneChangeList.ProcessChanges\(\) - id=\d+ local=False .+id=(\d+) zone=.+ zonePos=\d+ cardId=.+ player=(\d+)\] zone from (.+) -> (.+)$""".r
    }

  }

}
