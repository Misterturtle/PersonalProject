package tph.tests

import java.io.{FileWriter, PrintWriter}

import com.typesafe.scalalogging.LazyLogging
import tph._

import scala.collection.mutable.ListBuffer

/**
  * Created by Harambe on 1/29/2017.
  */
class MockHearthstone(theBrain: TheBrain) extends LazyLogging {

  var currentFile = "testsituations/blank"
  var currentWriterFile = "testsituations/blankcommands.txt"
  var writer = new PrintWriter(new FileWriter(currentWriterFile + ".txt"))

  def MockVoteExecution(vote: Vote): Unit = {

    logger.debug("Mock Vote Entry: " + vote.voteCode)

    vote.voteCode match {

      case (Constants.EmojiVoteCodes.Greetings()) =>

      case (Constants.EmojiVoteCodes.Thanks()) =>

      case (Constants.EmojiVoteCodes.WellPlayed()) =>

      case (Constants.EmojiVoteCodes.Wow()) =>

      case (Constants.EmojiVoteCodes.Oops()) =>

      case (Constants.EmojiVoteCodes.Threaten()) =>

      //Probably removing concede
      //case (Constants.MiscVoteCodes.Concede(decision)) =>

      case (Constants.ActionVoteCodes.Discover(option: Int)) =>

      case (Constants.ActionVoteCodes.CardPlayWithFriendlyOption(card: Int, boardTarget: Int)) =>
        val frozenStatus = GetGameStatus()
        val gameStatus = frozenStatus.frozenPlayers
        val cardsInHand = gameStatus(0).hand.size
        val cardIndex = gameStatus(0).hand.indexWhere(_.handPosition == card)
        val cardName = gameStatus(0).hand(cardIndex).name
        val cardID = gameStatus(0).hand(cardIndex).id
        val cardsOnBoard = gameStatus(0).board.size
        //Play card on furthest right position
        MoveCards(card, 1, gameStatus)
        theBrain.logFileReader.MockLogLine("[Power] PowerProcessor.DoTaskListForCard() - unhandled BlockType PLAY for sourceEntity [name=" + cardName + " id=" + cardID + " zone=PLAY zonePos=" + (cardsOnBoard + 1) + " cardId=.+ player=1]")
        if (cardsOnBoard > 7)
          logger.error("More than 7 cards on board. Size = " + cardsOnBoard)
        if (cardsInHand > 10)
          logger.error("More than 10 cards in hand. Size = " + cardsInHand)

      case (Constants.ActionVoteCodes.CardPlayWithFriendlyFaceOption(card: Int)) =>
        val frozenStatus = GetGameStatus()
        val gameStatus = frozenStatus.frozenPlayers
        val cardsInHand = gameStatus(0).hand.size
        val cardIndex = gameStatus(0).hand.indexWhere(_.handPosition == card)
        val cardName = gameStatus(0).hand(cardIndex).name
        val cardID = gameStatus(0).hand(cardIndex).id
        val cardsOnBoard = gameStatus(0).board.size
        //Play card on furthest right position
        MoveCards(card, 1, gameStatus)
        theBrain.logFileReader.MockLogLine("[Power] PowerProcessor.DoTaskListForCard() - unhandled BlockType PLAY for sourceEntity [name=" + cardName + " id=" + cardID + " zone=PLAY zonePos=" + (cardsOnBoard + 1) + " cardId=.+ player=1]")
        if (cardsOnBoard > 7)
          logger.error("More than 7 cards on board. Size = " + cardsOnBoard)
        if (cardsInHand > 10)
          logger.error("More than 10 cards in hand. Size = " + cardsInHand)


      case (Constants.ActionVoteCodes.CardPlayWithEnemyOption(card: Int, boardTarget: Int)) =>
        val frozenStatus = GetGameStatus()
        val gameStatus = frozenStatus.frozenPlayers
        val cardsInHand = gameStatus(0).hand.size
        val cardIndex = gameStatus(0).hand.indexWhere(_.handPosition == card)
        val cardName = gameStatus(0).hand(cardIndex).name
        val cardID = gameStatus(0).hand(cardIndex).id
        val cardsOnBoard = gameStatus(0).board.size
        MoveCards(card, 1, gameStatus)
        //Play card on furthest right position
        theBrain.logFileReader.MockLogLine("[Power] PowerProcessor.DoTaskListForCard() - unhandled BlockType PLAY for sourceEntity [name=" + cardName + " id=" + cardID + " zone=PLAY zonePos=" + (cardsOnBoard + 1) + " cardId=.+ player=1]")
        if (cardsOnBoard > 7)
          logger.error("More than 7 cards on board. Size = " + cardsOnBoard)
        if (cardsInHand > 10)
          logger.error("More than 10 cards in hand. Size = " + cardsInHand)


      case (Constants.ActionVoteCodes.CardPlayWithEnemyFaceOption(card: Int)) =>
        val frozenStatus = GetGameStatus()
        val gameStatus = frozenStatus.frozenPlayers
        val cardsInHand = gameStatus(0).hand.size
        val cardIndex = gameStatus(0).hand.indexWhere(_.handPosition == card)
        val cardName = gameStatus(0).hand(cardIndex).name
        val cardID = gameStatus(0).hand(cardIndex).id
        val cardsOnBoard = gameStatus(0).board.size
        MoveCards(card, 1, gameStatus)
        //Play card on furthest right position
        theBrain.logFileReader.MockLogLine("[Power] PowerProcessor.DoTaskListForCard() - unhandled BlockType PLAY for sourceEntity [name=" + cardName + " id=" + cardID + " zone=PLAY zonePos=" + (cardsOnBoard + 1) + " cardId=.+ player=1]")
        if (cardsOnBoard > 7)
          logger.error("More than 7 cards on board. Size = " + cardsOnBoard)
        if (cardsInHand > 10)
          logger.error("More than 10 cards in hand. Size = " + cardsInHand)

      //Normal Turn Play Type
      case (Constants.ActionVoteCodes.CardPlay(card: Int)) =>
        val frozenStatus = GetGameStatus()
        val gameStatus = frozenStatus.frozenPlayers
        val cardsInHand = gameStatus(0).hand.size
        val cardIndex = gameStatus(0).hand.indexWhere(_.handPosition == card)
        val cardName = gameStatus(0).hand(cardIndex).name
        val cardID = gameStatus(0).hand(cardIndex).id
        val cardsOnBoard = gameStatus(0).board.size
        MoveCards(card, 1, gameStatus)
        //Play card on furthest right position
        theBrain.logFileReader.MockLogLine("[Power] PowerProcessor.DoTaskListForCard() - unhandled BlockType PLAY for sourceEntity [name=" + cardName + " id=" + cardID + " zone=PLAY zonePos=" + (cardsOnBoard + 1) + " cardId=.+ player=1]")
        if (cardsOnBoard > 7)
          logger.error("More than 7 cards on board. Size = " + cardsOnBoard)
        if (cardsInHand > 10)
          logger.error("More than 10 cards in hand. Size = " + cardsInHand)

      case (Constants.ActionVoteCodes.CardPlayWithPosition(card: Int, position: Int)) =>
        val frozenStatus = GetGameStatus()
        val gameStatus = frozenStatus.frozenPlayers
        val cardsInHand = gameStatus(0).hand.size
        val cardIndex = gameStatus(0).hand.indexWhere(_.handPosition == card)
        val cardName = gameStatus(0).hand(cardIndex).name
        val cardID = gameStatus(0).hand(cardIndex).id
        val cardsOnBoard = gameStatus(0).board.size

        MoveCards(card, 1, gameStatus)
        MoveMinions(position, 1, gameStatus)


        //Play card at position
        theBrain.logFileReader.MockLogLine("[Power] PowerProcessor.DoTaskListForCard() - unhandled BlockType PLAY for sourceEntity [name=" + cardName + " id=" + cardID + " zone=PLAY zonePos=" + position + " cardId=.+ player=1]")
        if (cardsOnBoard > 7)
          logger.error("More than 7 cards on board. Size = " + cardsOnBoard)
        if (cardsInHand > 10)
          logger.error("More than 10 cards in hand. Size = " + cardsInHand)


      case (Constants.ActionVoteCodes.CardPlayWithFriendlyBoardTarget(card: Int, target: Int)) =>
        val frozenStatus = GetGameStatus()
        val gameStatus = frozenStatus.frozenPlayers
        val cardsInHand = gameStatus(0).hand.size
        val cardIndex = gameStatus(0).hand.indexWhere(_.handPosition == card)
        val cardName = gameStatus(0).hand(cardIndex).name
        val cardID = gameStatus(0).hand(cardIndex).id
        val cardsOnBoard = gameStatus(0).board.size
        MoveCards(card, 1, gameStatus)
        //Play card on furthest right position
        theBrain.logFileReader.MockLogLine("[Power] PowerProcessor.DoTaskListForCard() - unhandled BlockType PLAY for sourceEntity [name=" + cardName + " id=" + cardID + " zone=PLAY zonePos=" + (cardsOnBoard + 1) + " cardId=.+ player=1]")
        if (cardsOnBoard > 7)
          logger.error("More than 7 cards on board. Size = " + cardsOnBoard)
        if (cardsInHand > 10)
          logger.error("More than 10 cards in hand. Size = " + cardsInHand)


      case (Constants.ActionVoteCodes.CardPlayWithEnemyBoardTarget(card: Int, target: Int)) =>
        val frozenStatus = GetGameStatus()
        val gameStatus = frozenStatus.frozenPlayers
        val cardsInHand = gameStatus(0).hand.size
        val cardIndex = gameStatus(0).hand.indexWhere(_.handPosition == card)
        val cardName = gameStatus(0).hand(cardIndex).name
        val cardID = gameStatus(0).hand(cardIndex).id
        val cardsOnBoard = gameStatus(0).board.size
        MoveCards(card, 1, gameStatus)
        //Play card on furthest right position
        theBrain.logFileReader.MockLogLine("[Power] PowerProcessor.DoTaskListForCard() - unhandled BlockType PLAY for sourceEntity [name=" + cardName + " id=" + cardID + " zone=PLAY zonePos=" + (cardsOnBoard + 1) + " cardId=.+ player=1]")
        if (cardsOnBoard > 7)
          logger.error("More than 7 cards on board. Size = " + cardsOnBoard)
        if (cardsInHand > 10)
          logger.error("More than 10 cards in hand. Size = " + cardsInHand)


      case (Constants.ActionVoteCodes.CardPlayWithFriendlyFaceTarget(card: Int)) =>
        val frozenStatus = GetGameStatus()
        val gameStatus = frozenStatus.frozenPlayers
        val cardsInHand = gameStatus(0).hand.size
        val cardIndex = gameStatus(0).hand.indexWhere(_.handPosition == card)
        val cardName = gameStatus(0).hand(cardIndex).name
        val cardID = gameStatus(0).hand(cardIndex).id
        val cardsOnBoard = gameStatus(0).board.size
        MoveCards(card, 1, gameStatus)
        //Play card on furthest right position
        theBrain.logFileReader.MockLogLine("[Power] PowerProcessor.DoTaskListForCard() - unhandled BlockType PLAY for sourceEntity [name=" + cardName + " id=" + cardID + " zone=PLAY zonePos=" + (cardsOnBoard + 1) + " cardId=.+ player=1]")
        if (cardsOnBoard > 7)
          logger.error("More than 7 cards on board. Size = " + cardsOnBoard)
        if (cardsInHand > 10)
          logger.error("More than 10 cards in hand. Size = " + cardsInHand)


      case (Constants.ActionVoteCodes.CardPlayWithEnemyFaceTarget(card: Int)) =>
        val frozenStatus = GetGameStatus()
        val gameStatus = frozenStatus.frozenPlayers
        val cardsInHand = gameStatus(0).hand.size
        val cardIndex = gameStatus(0).hand.indexWhere(_.handPosition == card)
        val cardName = gameStatus(0).hand(cardIndex).name
        val cardID = gameStatus(0).hand(cardIndex).id
        val cardsOnBoard = gameStatus(0).board.size
        MoveCards(card, 1, gameStatus)
        //Play card on furthest right position
        theBrain.logFileReader.MockLogLine("[Power] PowerProcessor.DoTaskListForCard() - unhandled BlockType PLAY for sourceEntity [name=" + cardName + " id=" + cardID + " zone=PLAY zonePos=" + (cardsOnBoard + 1) + " cardId=.+ player=1]")
        if (cardsOnBoard > 7)
          logger.error("More than 7 cards on board. Size = " + cardsOnBoard)
        if (cardsInHand > 10)
          logger.error("More than 10 cards in hand. Size = " + cardsInHand)

      case (Constants.MiscVoteCodes.EndTurn()) =>

      case (Constants.ActionVoteCodes.HeroPower()) =>

      case (Constants.ActionVoteCodes.HeroPowerWithEnemyFace()) =>

      case (Constants.ActionVoteCodes.HeroPowerWithEnemyTarget(target: Int)) =>

      case (Constants.ActionVoteCodes.HeroPowerWithFriendlyFace()) =>

      case (Constants.ActionVoteCodes.HeroPowerWithFriendlyTarget(target: Int)) =>

      //Attack Type
      case (Constants.ActionVoteCodes.NormalAttack(friendlyTarget: Int, enemyTarget: Int)) =>
        RemoveMinionOnBoard(1, friendlyTarget)
        RemoveMinionOnBoard(2, enemyTarget)

      case (Constants.ActionVoteCodes.FaceAttack(position: Int)) =>


      case (Constants.ActionVoteCodes.NormalAttackToFace(position: Int)) =>


      case (Constants.ActionVoteCodes.FaceAttackToFace()) =>

      case (Constants.ActionVoteCodes.MulliganVote(first: Boolean, second: Boolean, third: Boolean, fourth: Boolean)) =>


      case x =>
        x match {
          //Changing Reactions To Votes
          case vote1 =>

          case _ => logger.debug("Unexpected vote to TestBrain: " + x + "\n From: " + vote.sender)
        }
    }
  }


  def RemoveMinionOnBoard(player: Int, boardPosition: Int): Unit = {
    val frozenStatus = GetGameStatus()
    val gameStatus = frozenStatus.frozenPlayers
    val removedCardIndex = gameStatus(player - 1).board.indexWhere(_.boardPosition == boardPosition)
    val removedCardID = gameStatus(player - 1).board(removedCardIndex).id
    val minionsOnBoard = gameStatus(player - 1).board.size
    theBrain.logFileReader.MockLogLine("[Power] PowerTaskList some TAG_CHANGE Entity=[name=" + gameStatus(player - 1).board(removedCardIndex).name + " id=" + removedCardID + " zone=some zonePos=99 some player=" + player + "some tag=ZONE value=GRAVEYARD")

    for (a <- boardPosition until minionsOnBoard) {
      val movingMinionIndex = gameStatus(player - 1).board.indexWhere(_.boardPosition == (a + 1))
      val movingMinionID = gameStatus(player - 1).board(movingMinionIndex).id
      theBrain.logFileReader.MockLogLine("[Zone] ZoneChangeList.ProcessChanges() - processing index=55 change=powerTask=[power=[type=TAG_CHANGE entity=[id=55 cardId=some name=some] tag=ZONE_POSITION value=55] complete=False] entity=[name=some id=" + movingMinionID + " zone=PLAY zonePos=55 cardId=some player=" + player + "] srcZoneTag=INVALID srcPos= dstZoneTag=INVALID dstPos=" + a)
    }
  }


  def CreatePlayer(name: String, playerNumber: Int, handSize: Int, boardSize: Int): Player = {

    val hand = CreateHand(handSize)
    val board = CreateBoard(boardSize)

    val player1 = new Player()
    player1.name = name
    player1.hand = hand
    player1.board = board

    player1.player = playerNumber
    return player1
  }

  def CreateHand(size: Int): ListBuffer[Card] = {

    val hand = new ListBuffer[Card]

    for (a <- 0 until size) {
      val card = new Card()
      card.handPosition = a + 1
      card.id = a + 1
      card.name = "TestCard"
      hand.append(card)
    }

    return hand

  }

  def CreateBoard(size: Int): ListBuffer[Card] = {

    val board = new ListBuffer[Card]

    for (a <- 0 until size) {
      val card = new Card()
      card.boardPosition = a + 1
      card.id = a + 11
      card.name = "TestCard"
      board.append(card)
    }

    return board

  }

  def GetGameStatus(): FrozenGameStatus = {
    theBrain.GetGameStatus()
  }


  def MoveCards(playedCardPos: Int, player: Int, gameStatus: Array[FrozenPlayer]): Unit = {
    val cardsInHand = gameStatus(player - 1).hand.size


    //Move Cards
    for (a <- (playedCardPos + 1) until cardsInHand + 1) {
      val movingCardIndex = gameStatus(player - 1).hand.indexWhere(_.handPosition == a)
      val movingCardID = gameStatus(player - 1).hand(movingCardIndex).id
      theBrain.logFileReader.MockLogLine("[Zone] ZoneChangeList.ProcessChanges() - processing index=55 change=powerTask=[power=[type=TAG_CHANGE entity=[id=55 cardId=some name=some tag=ZONE_POSITION value=55] complete=False] entity=some id=" + movingCardID + " some zone=HAND zonePos=" + a + " player=" + player + " dstPos=" + (a - 1))
    }
  }

  def MoveMinions(playedMinionPos: Int, player: Int, gameStatus: Array[FrozenPlayer]): Unit = {
    val cardsOnBoard = gameStatus(player - 1).board.size

    //Move Cards
    for (a <- playedMinionPos until (cardsOnBoard + 1)) {
      val movingCardIndex = gameStatus(player - 1).board.indexWhere(_.boardPosition == a)
      val movingCardID = gameStatus(player - 1).board(movingCardIndex).id
      theBrain.logFileReader.MockLogLine("[Zone] ZoneChangeList.ProcessChanges() - processing index=55 change=powerTask=[power=[type=TAG_CHANGE entity=[id=55 cardId=some name=some] tag=ZONE_POSITION value=55] complete=False] entity=[name=some id=" + movingCardID + " zone=PLAY zonePos=55 cardId=some player=" + player + "] srcZoneTag=INVALID srcPos= dstZoneTag=INVALID dstPos=" + (a + 1))
    }
  }

}
