package tph.research

import java.io.{FileWriter, PrintWriter, File}
import java.util.concurrent.TimeUnit

import akka.actor.{Props, ActorRef, ActorSystem, Actor}
import akka.event.LoggingReceive
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import org.apache.commons.io.FileUtils
import tph.IrcMessages.ChangeMenu
import tph.{LogFileReader, IrcBot, Player, Card}
import tph.IrcMessages._
import akka.pattern.ask

import scala.collection.mutable.ListBuffer
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global


class ircLogicTests(system: ActorSystem, controller: ActorRef, ircLogic: ActorRef, gameStatusActor: ActorRef, logFileReader: ActorRef, ircBot: IrcBot) extends Actor with akka.actor.ActorLogging {

  val FULL_TEST = "Full Test"
  val ADJUST_VOTES = "Adjust Votes"
  val START = "Start"

  val config = ConfigFactory.load()
  var currentFile = "testsituations/blank"
  var currentWriterFile = "testsituations/blankcommands.txt"
  var writer = new PrintWriter(new FileWriter(currentWriterFile + ".txt"))

  var readyForTest = true
  var cardPlayedAdjustmentComplete = false
  var minionDeathAdjustmentComplete = false
  var multipleBindAdjustmentComplete = false
  var multipleFutureAdjustmentComplete = false


  override def receive: Receive = LoggingReceive({


    case START =>
      initialize()

    case FULL_TEST =>
//              if(!cardPlayedAdjustmentComplete && readyForTest)
//              CardPlayedAdjustmentTest()
//
//              if(!minionDeathAdjustmentComplete && readyForTest)
//              MinionDeathAdjustmentTest()

//            if(!multipleBindAdjustmentComplete && readyForTest)
//              MultipleBindAdjustmentTest()

      if (!multipleFutureAdjustmentComplete && readyForTest)
        MultipleFutureAdjustmentTest()

      system.scheduler.scheduleOnce(5.seconds, this.self, FULL_TEST)

    case "CardPlayedAdjustmentResults" =>
      CardPlayedAdjustmentResults()

    case "MinionDeathAdjustmentResults" =>
      MinionDeathAdjustmentResults()

    case "MultipleBindAdjustmentResults" =>
      MultipleBindAdjustmentResults()

    case "MultipleFutureAdjustmentResults" =>
      MultipleFutureAdjustmentResults()


    case (Greetings()) =>

    case (Thanks()) =>

    case (WellPlayed()) =>

    case (Wow()) =>

    case (Oops()) =>

    case (Threaten()) =>

    case (Concede()) =>

    case (Discover(option: Int)) =>

    case (CardPlayWithFriendlyOption(card: Int, boardTarget: Int)) =>
      val gameStatus = GetGameStatus()
      val cardsInHand = gameStatus(0).hand.size
      val cardIndex = gameStatus(0).hand.indexWhere(_.handPosition == card)
      val cardName = gameStatus(0).hand(cardIndex).name
      val cardID = gameStatus(0).hand(cardIndex).id
      val cardsOnBoard = gameStatus(0).board.size
      //Play card on furthest right position
      MoveCards(card, 1, gameStatus)
      WriteCommand(currentFile + "commands.txt", "[Power] PowerProcessor.DoTaskListForCard() - unhandled BlockType PLAY for sourceEntity [name=" + cardName + " id=" + cardID + " zone=PLAY zonePos=" + (cardsOnBoard + 1) + " cardId=.+ player=1]")
      if (cardsOnBoard > 7)
        log.error("More than 7 cards on board. Size = " + cardsOnBoard)
      if (cardsInHand > 10)
        log.error("More than 10 cards in hand. Size = " + cardsInHand)

    case (CardPlayWithFriendlyFaceOption(card: Int)) =>
      val gameStatus = GetGameStatus()
      val cardsInHand = gameStatus(0).hand.size
      val cardIndex = gameStatus(0).hand.indexWhere(_.handPosition == card)
      val cardName = gameStatus(0).hand(cardIndex).name
      val cardID = gameStatus(0).hand(cardIndex).id
      val cardsOnBoard = gameStatus(0).board.size
      //Play card on furthest right position
      MoveCards(card, 1, gameStatus)
      WriteCommand(currentFile + "commands.txt", "[Power] PowerProcessor.DoTaskListForCard() - unhandled BlockType PLAY for sourceEntity [name=" + cardName + " id=" + cardID + " zone=PLAY zonePos=" + (cardsOnBoard + 1) + " cardId=.+ player=1]")
      if (cardsOnBoard > 7)
        log.error("More than 7 cards on board. Size = " + cardsOnBoard)
      if (cardsInHand > 10)
        log.error("More than 10 cards in hand. Size = " + cardsInHand)


    case (CardPlayWithEnemyOption(card: Int, boardTarget: Int)) =>
      val gameStatus = GetGameStatus()
      val cardsInHand = gameStatus(0).hand.size
      val cardIndex = gameStatus(0).hand.indexWhere(_.handPosition == card)
      val cardName = gameStatus(0).hand(cardIndex).name
      val cardID = gameStatus(0).hand(cardIndex).id
      val cardsOnBoard = gameStatus(0).board.size
      MoveCards(card, 1, gameStatus)
      //Play card on furthest right position
      WriteCommand(currentFile + "commands.txt", "[Power] PowerProcessor.DoTaskListForCard() - unhandled BlockType PLAY for sourceEntity [name=" + cardName + " id=" + cardID + " zone=PLAY zonePos=" + (cardsOnBoard + 1) + " cardId=.+ player=1]")
      if (cardsOnBoard > 7)
        log.error("More than 7 cards on board. Size = " + cardsOnBoard)
      if (cardsInHand > 10)
        log.error("More than 10 cards in hand. Size = " + cardsInHand)


    case (CardPlayWithEnemyFaceOption(card: Int)) =>
      val gameStatus = GetGameStatus()
      val cardsInHand = gameStatus(0).hand.size
      val cardIndex = gameStatus(0).hand.indexWhere(_.handPosition == card)
      val cardName = gameStatus(0).hand(cardIndex).name
      val cardID = gameStatus(0).hand(cardIndex).id
      val cardsOnBoard = gameStatus(0).board.size
      MoveCards(card, 1, gameStatus)
      //Play card on furthest right position
      WriteCommand(currentFile + "commands.txt", "[Power] PowerProcessor.DoTaskListForCard() - unhandled BlockType PLAY for sourceEntity [name=" + cardName + " id=" + cardID + " zone=PLAY zonePos=" + (cardsOnBoard + 1) + " cardId=.+ player=1]")
      if (cardsOnBoard > 7)
        log.error("More than 7 cards on board. Size = " + cardsOnBoard)
      if (cardsInHand > 10)
        log.error("More than 10 cards in hand. Size = " + cardsInHand)

    //Normal Turn Play Type
    case (CardPlay(card: Int)) =>
      val gameStatus = GetGameStatus()
      val cardsInHand = gameStatus(0).hand.size
      val cardIndex = gameStatus(0).hand.indexWhere(_.handPosition == card)
      val cardName = gameStatus(0).hand(cardIndex).name
      val cardID = gameStatus(0).hand(cardIndex).id
      val cardsOnBoard = gameStatus(0).board.size
      MoveCards(card, 1, gameStatus)
      //Play card on furthest right position
      WriteCommand(currentFile + "commands.txt", "[Power] PowerProcessor.DoTaskListForCard() - unhandled BlockType PLAY for sourceEntity [name=" + cardName + " id=" + cardID + " zone=PLAY zonePos=" + (cardsOnBoard + 1) + " cardId=.+ player=1]")
      if (cardsOnBoard > 7)
        log.error("More than 7 cards on board. Size = " + cardsOnBoard)
      if (cardsInHand > 10)
        log.error("More than 10 cards in hand. Size = " + cardsInHand)

    case (CardPlayWithPosition(card: Int, position: Int)) =>
      val gameStatus = GetGameStatus()
      val cardsInHand = gameStatus(0).hand.size
      val cardIndex = gameStatus(0).hand.indexWhere(_.handPosition == card)
      val cardName = gameStatus(0).hand(cardIndex).name
      val cardID = gameStatus(0).hand(cardIndex).id
      val cardsOnBoard = gameStatus(0).board.size

      MoveCards(card, 1, gameStatus)
      MoveMinions(position, 1, gameStatus)


      //Play card at position
      WriteCommand(currentFile + "commands.txt", "[Power] PowerProcessor.DoTaskListForCard() - unhandled BlockType PLAY for sourceEntity [name=" + cardName + " id=" + cardID + " zone=PLAY zonePos=" + position + " cardId=.+ player=1]")
      if (cardsOnBoard > 7)
        log.error("More than 7 cards on board. Size = " + cardsOnBoard)
      if (cardsInHand > 10)
        log.error("More than 10 cards in hand. Size = " + cardsInHand)


    case (CardPlayWithFriendlyBoardTarget(card: Int, target: Int)) =>
      val gameStatus = GetGameStatus()
      val cardsInHand = gameStatus(0).hand.size
      val cardIndex = gameStatus(0).hand.indexWhere(_.handPosition == card)
      val cardName = gameStatus(0).hand(cardIndex).name
      val cardID = gameStatus(0).hand(cardIndex).id
      val cardsOnBoard = gameStatus(0).board.size
      MoveCards(card, 1, gameStatus)
      //Play card on furthest right position
      WriteCommand(currentFile + "commands.txt", "[Power] PowerProcessor.DoTaskListForCard() - unhandled BlockType PLAY for sourceEntity [name=" + cardName + " id=" + cardID + " zone=PLAY zonePos=" + (cardsOnBoard + 1) + " cardId=.+ player=1]")
      if (cardsOnBoard > 7)
        log.error("More than 7 cards on board. Size = " + cardsOnBoard)
      if (cardsInHand > 10)
        log.error("More than 10 cards in hand. Size = " + cardsInHand)


    case (CardPlayWithEnemyBoardTarget(card: Int, target: Int)) =>
      val gameStatus = GetGameStatus()
      val cardsInHand = gameStatus(0).hand.size
      val cardIndex = gameStatus(0).hand.indexWhere(_.handPosition == card)
      val cardName = gameStatus(0).hand(cardIndex).name
      val cardID = gameStatus(0).hand(cardIndex).id
      val cardsOnBoard = gameStatus(0).board.size
      MoveCards(card, 1, gameStatus)
      //Play card on furthest right position
      WriteCommand(currentFile + "commands.txt", "[Power] PowerProcessor.DoTaskListForCard() - unhandled BlockType PLAY for sourceEntity [name=" + cardName + " id=" + cardID + " zone=PLAY zonePos=" + (cardsOnBoard + 1) + " cardId=.+ player=1]")
      if (cardsOnBoard > 7)
        log.error("More than 7 cards on board. Size = " + cardsOnBoard)
      if (cardsInHand > 10)
        log.error("More than 10 cards in hand. Size = " + cardsInHand)


    case (CardPlayWithFriendlyFaceTarget(card: Int)) =>
      val gameStatus = GetGameStatus()
      val cardsInHand = gameStatus(0).hand.size
      val cardIndex = gameStatus(0).hand.indexWhere(_.handPosition == card)
      val cardName = gameStatus(0).hand(cardIndex).name
      val cardID = gameStatus(0).hand(cardIndex).id
      val cardsOnBoard = gameStatus(0).board.size
      MoveCards(card, 1, gameStatus)
      //Play card on furthest right position
      WriteCommand(currentFile + "commands.txt", "[Power] PowerProcessor.DoTaskListForCard() - unhandled BlockType PLAY for sourceEntity [name=" + cardName + " id=" + cardID + " zone=PLAY zonePos=" + (cardsOnBoard + 1) + " cardId=.+ player=1]")
      if (cardsOnBoard > 7)
        log.error("More than 7 cards on board. Size = " + cardsOnBoard)
      if (cardsInHand > 10)
        log.error("More than 10 cards in hand. Size = " + cardsInHand)


    case (CardPlayWithEnemyFaceTarget(card: Int)) =>
      val gameStatus = GetGameStatus()
      val cardsInHand = gameStatus(0).hand.size
      val cardIndex = gameStatus(0).hand.indexWhere(_.handPosition == card)
      val cardName = gameStatus(0).hand(cardIndex).name
      val cardID = gameStatus(0).hand(cardIndex).id
      val cardsOnBoard = gameStatus(0).board.size
      MoveCards(card, 1, gameStatus)
      //Play card on furthest right position
      WriteCommand(currentFile + "commands.txt", "[Power] PowerProcessor.DoTaskListForCard() - unhandled BlockType PLAY for sourceEntity [name=" + cardName + " id=" + cardID + " zone=PLAY zonePos=" + (cardsOnBoard + 1) + " cardId=.+ player=1]")
      if (cardsOnBoard > 7)
        log.error("More than 7 cards on board. Size = " + cardsOnBoard)
      if (cardsInHand > 10)
        log.error("More than 10 cards in hand. Size = " + cardsInHand)

    case (EndTurn()) =>

    case (HeroPower()) =>

    case (HeroPowerWithEnemyFace()) =>

    case (HeroPowerWithEnemyTarget(target: Int)) =>

    case (HeroPowerWithFriendlyFace()) =>

    case (HeroPowerWithFriendlyTarget(target: Int)) =>

    //Attack Type
    case (NormalAttack(friendlyPosition: Int, enemyPosition: Int)) =>
      RemoveMinionOnBoard(1, friendlyPosition)
      RemoveMinionOnBoard(2, enemyPosition)

    case (FaceAttack(position: Int)) =>


    case (NormalAttackToFace(position: Int)) =>


    case (FaceAttackToFace()) =>

    case (MulliganVote(vote), mulliganOptions: Int) =>


    case x =>
      x match {
        //Changing Reactions To Votes
        case vote1 =>

        case _ => log.debug("Unexpected message to ircLogicTest: " + x + "\n From: " + sender)
      }
  })


  def RemoveMinionOnBoard(player: Int, boardPosition: Int): Unit = {
    val gameStatus = GetGameStatus()
    val removedCardIndex = gameStatus(player - 1).board.indexWhere(_.boardPosition == boardPosition)
    val removedCardID = gameStatus(player - 1).board(removedCardIndex).id
    val minionsOnBoard = gameStatus(player - 1).board.size
    WriteCommand(currentFile + "commands.txt", "[Power] PowerTaskList some TAG_CHANGE Entity=[name=some id=" + removedCardID + " zone=some zonePos=55 some player=" + player + " some tag=ZONE value=GRAVEYARD")

    for (a <- boardPosition until minionsOnBoard) {
      val movingMinionIndex = gameStatus(player - 1).board.indexWhere(_.boardPosition == (a + 1))
      val movingMinionID = gameStatus(player - 1).board(movingMinionIndex).id
      WriteCommand(currentFile + "commands.txt", "[Zone] ZoneChangeList.ProcessChanges() - processing index=55 change=powerTask=[power=[type=TAG_CHANGE entity=[id=55 cardId=some name=some] tag=ZONE_POSITION value=55] complete=False] entity=[name=some id=" + movingMinionID + " zone=PLAY zonePos=55 cardId=some player=" + player + "] srcZoneTag=INVALID srcPos= dstZoneTag=INVALID dstPos=" + a)
    }
  }


  def initialize(): Unit = {
    logFileReader ! "LogFileReader.start"

    controller ! ChangeMenu("mainMenu", "inGame")

    ircLogic ! "Start Tests"
    ircLogic ! "Start Game"
    ircLogic ! "Skip Mulligan"
    ircLogic ! "Turn Start"
    ircLogic ! "Activate"

    TimeUnit.SECONDS.sleep(5)
    this.self ! FULL_TEST
  }

  def CreatePlayer(name: String, playerNumber: Int, handSize: Int, boardSize: Int): Player = {

    val hand = CreateHand(handSize)
    val board = CreateBoard(boardSize)

    val player1 = new Player(name, hand, board)

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

  def GetGameStatus(): Array[Player] = {
    implicit val timeout = Timeout(30 seconds)
    val future = controller ? "GetGameStatus"
    val result = Await.result(future, timeout.duration)
    if (result == None) {
      GetGameStatus()
    }
    else return result.asInstanceOf[Array[Player]]
  }

  def WriteCommand(fileName: String, command: String): Unit = {
    if (currentWriterFile != fileName) {
      val newWriter = new PrintWriter(new FileWriter(fileName))
      writer.close()
      writer = newWriter
      currentWriterFile = fileName
      writer.println(command + "\n")
      writer.flush()

      logFileReader ! ChangeReaderFile(fileName)
    }
    else {
      writer.println(command + "\n")
      writer.flush()
    }
  }

  def MoveCards(playedCardPos: Int, player: Int, gameStatus: Array[Player]): Unit = {
    val cardsInHand = gameStatus(player - 1).hand.size


    //Move Cards
    for (a <- (playedCardPos + 1) until cardsInHand + 1) {
      val movingCardIndex = gameStatus(player - 1).hand.indexWhere(_.handPosition == a)
      val movingCardID = gameStatus(player - 1).hand(movingCardIndex).id
      WriteCommand(currentFile + "commands.txt", "[Zone] ZoneChangeList.ProcessChanges() - processing index=55 change=powerTask=[power=[type=TAG_CHANGE entity=[id=55 cardId=some name=some tag=ZONE_POSITION value=55] complete=False] entity=some id=" + movingCardID + " some zone=HAND zonePos=" + a + " player=" + player + " dstPos=" + (a - 1))
    }
  }

  def MoveMinions(playedMinionPos: Int, player: Int, gameStatus: Array[Player]): Unit = {
    val cardsOnBoard = gameStatus(player - 1).board.size

    //Move Cards
    for (a <- playedMinionPos until (cardsOnBoard + 1)) {
      val movingCardIndex = gameStatus(player - 1).board.indexWhere(_.boardPosition == a)
      val movingCardID = gameStatus(player - 1).board(movingCardIndex).id
      WriteCommand(currentFile + "commands.txt", "[Zone] ZoneChangeList.ProcessChanges() - processing index=55 change=powerTask=[power=[type=TAG_CHANGE entity=[id=55 cardId=some name=some] tag=ZONE_POSITION value=55] complete=False] entity=[name=some id=" + movingCardID + " zone=PLAY zonePos=55 cardId=some player=" + player + "] srcZoneTag=INVALID srcPos= dstZoneTag=INVALID dstPos=" + (a + 1))
    }
  }


  def MockIRCMessage(sender: String, vote: String) {

    ircBot.onMessage(config.getString("tph.irc.channel"), sender, sender, config.getString("tph.irc.host"), vote)
  }

  def CheckTestResults(friendlyHandIds: Array[Int], friendlyBoardIds: Array[Int], enemyBoardIds: Array[Int]): Boolean = {
    val gameStatus = GetGameStatus()

    if (gameStatus(0).hand.size != friendlyHandIds.size) {
      log.error("Test Failed. My hand size is " + gameStatus(0).hand.size + " instead of " + friendlyHandIds.size + ".")
      return false
    }

    if (gameStatus(0).hand.size != friendlyHandIds.size) {
      log.error("Test Failed. My hand size is " + gameStatus(0).hand.size + " instead of " + friendlyHandIds.size + ".")
      return false
    }

    for (a <- 0 until gameStatus(0).hand.size) {
      val index = gameStatus(0).hand.indexWhere(_.handPosition == (a + 1))

      if (index != -1) {
        if(gameStatus(0).hand(index).id != friendlyHandIds(a)) {
          log.error("Test Failed. Card " + (a + 1) + " ID is " + gameStatus(0).hand(index).id + " instead of " + friendlyHandIds(a) + ".")
          return false
        }
      }
      else {
        log.error("Test Failed. Friendly Hand Position " + (a + 1) + "does not exist.")
        return false
      }
    }

    for (a <- 0 until gameStatus(0).board.size) {
      val index = gameStatus(0).board.indexWhere(_.boardPosition == (a + 1))

      if (index != -1) {
        if(gameStatus(0).board(index).id != friendlyBoardIds(a)) {
        log.error("Test Failed. Friendly Minion " + (a + 1) + " ID is " + gameStatus(0).board(index).id + " instead of " + friendlyBoardIds(a) + " .")
        return false
      }}
      else {
        log.error("Test Failed. Friendly Hand Position " + (a + 1) + "does not exist.")
        return false
      }
    }

    for (a <- 0 until gameStatus(1).board.size) {
      val index = gameStatus(1).board.indexWhere(_.boardPosition == (a + 1))

      if (index != -1) {
        if(gameStatus(1).board(index).id != enemyBoardIds(a)) {
        log.error("Test Failed. Enemy Minion "+ (a + 1) + " ID is " + gameStatus(1).board(index).id + " instead of " + enemyBoardIds(a) + " .")
        return false
      }}
      else {
        log.error("Test Failed. Enemy Board Position " + (a + 1) + "does not exist.")
        return false
      }
    }

    return true

  }


  ////////////////////////////////////////////////////  TESTS BELOW HERE //////////////////////////////////


  def CardPlayedAdjustmentTest(): Unit = {

    logFileReader ! "CLEAR_STATUS"
    readyForTest = false
    currentFile = "testsituations/normalsetup"
    logFileReader ! ChangeReaderFile(currentFile + ".txt")


    TimeUnit.SECONDS.sleep(1)
    for (a <- 0 until 5) {
      MockIRCMessage("voter" + (a+1), "!play 1")
      MockIRCMessage("voter" + (a+1), "!play " + (a + 1))
    }
    MockIRCMessage("voter1", "!play 2")

    TimeUnit.SECONDS.sleep(1)
    ircLogic ! "Direct Decide"

    system.scheduler.scheduleOnce(20.seconds, this.self, "CardPlayedAdjustmentResults")
  }

  def CardPlayedAdjustmentResults(): Unit = {

    //3,4,5,6,7,8,9,10
    val friendlyHandIds = new Array[Int](8)
    friendlyHandIds(0) = 3
    friendlyHandIds(1) = 4
    friendlyHandIds(2) = 5
    friendlyHandIds(3) = 6
    friendlyHandIds(4) = 7
    friendlyHandIds(5) = 8
    friendlyHandIds(6) = 9
    friendlyHandIds(7) = 10

    //21,22,23,24,25,1,2
    val friendlyBoardIds = new Array[Int](7)
    friendlyBoardIds(0) = 21
    friendlyBoardIds(1) = 22
    friendlyBoardIds(2) = 23
    friendlyBoardIds(3) = 24
    friendlyBoardIds(4) = 25
    friendlyBoardIds(5) = 1
    friendlyBoardIds(6) = 2

    //32,34,35,36,37
    val enemyBoardIds = new Array[Int](5)
    enemyBoardIds(0) = 31
    enemyBoardIds(1) = 32
    enemyBoardIds(2) = 33
    enemyBoardIds(3) = 34
    enemyBoardIds(4) = 35

    val passed = CheckTestResults(friendlyHandIds, friendlyBoardIds, enemyBoardIds)

    if (passed) {
      log.error("CardPlayedAdjustmentTest Passed!")
    }
    if (!passed) {
      log.error("CardPlayedAdjustmentTest Failed!")
    }

    cardPlayedAdjustmentComplete = true
    readyForTest = true
  }


  def MinionDeathAdjustmentTest(): Unit = {
    logFileReader ! "CLEAR_STATUS"
    readyForTest = false
    currentFile = "testsituations/fullboard"
    logFileReader ! ChangeReaderFile(currentFile + ".txt")

    TimeUnit.SECONDS.sleep(1)
    for (a <- 0 until 5) {
      MockIRCMessage("voter" + (a+1), "!att my 1, target his 1")
      MockIRCMessage("voter" + (a+1), "!att my 1, target his " + (a + 1))
    }
    MockIRCMessage("voter1", "!att my 3, target his 3")
    MockIRCMessage("voter2", "!att my 3, target his 3")

    TimeUnit.SECONDS.sleep(3)
    ircLogic ! "Direct Decide"

    system.scheduler.scheduleOnce(15.seconds, this.self, "MinionDeathAdjustmentResults")

  }


  def MinionDeathAdjustmentResults(): Unit = {
    val friendlyHandIds = new Array[Int](0)

    //22,24,25,26,27
    val friendlyBoardIds = new Array[Int](5)
    friendlyBoardIds(0) = 22
    friendlyBoardIds(1) = 24
    friendlyBoardIds(2) = 25
    friendlyBoardIds(3) = 26
    friendlyBoardIds(4) = 27

    //32,34,35,36,37
    val enemyBoardIds = new Array[Int](5)
    enemyBoardIds(0) = 32
    enemyBoardIds(1) = 34
    enemyBoardIds(2) = 35
    enemyBoardIds(3) = 36
    enemyBoardIds(4) = 37

    val passed = CheckTestResults(friendlyHandIds, friendlyBoardIds, enemyBoardIds)

    if (passed) {
      log.error("MinionDeathAdjustmentTest Passed!")
    }
    if (!passed) {
      log.error("MinionDeathAdjustmentTest Failed!")
    }

    minionDeathAdjustmentComplete = true
    readyForTest = true
  }


  def MultipleBindAdjustmentTest(): Unit = {
    logFileReader ! "CLEAR_STATUS"
    readyForTest = false
    currentFile = "testsituations/normalsetup"
    logFileReader ! ChangeReaderFile(currentFile + ".txt")
    TimeUnit.SECONDS.sleep(1)

    for (a <- 0 until 5) {
      MockIRCMessage("voter" + (a+1), "!play 1, spot 1")
      MockIRCMessage("voter" + (a+1), "!bind")
      MockIRCMessage("voter" + (a+1), "!att my 5, target his 1")
      MockIRCMessage("voter" + (a+1), "!bind")
      MockIRCMessage("voter" + (a+1), "!att my 1, target his 5")
    }
    MockIRCMessage("voter1", "!att my 5, target his 1")
    MockIRCMessage("voter2", "!att my 5, target his 1")
    MockIRCMessage("voter3", "!att my 1, target his 5")
    MockIRCMessage("voter4", "!att my 1, target his 5")

    TimeUnit.SECONDS.sleep(3)
    ircLogic ! "Direct Decide"

    system.scheduler.scheduleOnce(35.seconds, this.self, "MultipleBindAdjustmentResults")

  }


  def MultipleBindAdjustmentResults(): Unit = {
    //my board: 1,22,23,24
    //my hand: 2,3,4,5,6,7,8,9,10
    //his board: 32,33,34

    val friendlyHandIds = new Array[Int](9)
    friendlyHandIds(0) = 2
    friendlyHandIds(1) = 3
    friendlyHandIds(2) = 4
    friendlyHandIds(3) = 5
    friendlyHandIds(4) = 6
    friendlyHandIds(5) = 7
    friendlyHandIds(6) = 8
    friendlyHandIds(7) = 9
    friendlyHandIds(8) = 10

    val friendlyBoardIds = new Array[Int](4)
    friendlyBoardIds(0) = 1
    friendlyBoardIds(1) = 22
    friendlyBoardIds(2) = 23
    friendlyBoardIds(3) = 24

    val enemyBoardIds = new Array[Int](3)
    enemyBoardIds(0) = 32
    enemyBoardIds(1) = 33
    enemyBoardIds(2) = 34

    val passed = CheckTestResults(friendlyHandIds, friendlyBoardIds, enemyBoardIds)

    if (passed) {
      log.error("MultipleBindAdjustmentTest Passed!")
    }
    if (!passed) {
      log.error("MultipleBindAdjustmentTest Failed!")
    }


    multipleBindAdjustmentComplete = true
    readyForTest = true
  }

  def MultipleFutureAdjustmentTest(): Unit = {

    logFileReader ! "CLEAR_STATUS"
    readyForTest = false
    currentFile = "testsituations/normalsetup"
    logFileReader ! ChangeReaderFile(currentFile + ".txt")

    TimeUnit.SECONDS.sleep(1)
    //Play 1 spot 1
    //Play 3 spot 1 (Play 2 spot 2)
    //Attack my 4, target his 1 (Attack my 6, target his 1)
    //Play 2 spot 1  (Play 1 spot 3)
    //Future
    //Attack 1, target his 2    (Attack 3, target his 1)
    //Future
    //Play 4 spot 1 (Play 2 spot 3)
    //Future
    //Attack my 1, target his 2 (Attack 3, target his 1)


    //Attack my 1, target his 2
    //1st: Attack my 2, target his 2
    //2nd: Attack my 3, target his 2
    //3rd: Attack my 3, target his 1
    //Does not change from 3,1
    //4th: Attack my 4, target his 1 (Frozen starts after here)
    //5th: ...
    //6th:...
    //7th: Attack my 4, target his 1


    for (a <- 0 until 4) {
      MockIRCMessage("voter" + (a+1), "!play 1, spot 1")
      MockIRCMessage("voter" + (a+1), "!play 3, spot 1")
      MockIRCMessage("voter" + (a+1), "!att my 4, target his 1")
      MockIRCMessage("voter" + (a+1), "!play 2, spot 1")
      MockIRCMessage("voter" + (a+1), "!future")
      MockIRCMessage("voter" + (a+1), "!att my 1, target his 2")
      MockIRCMessage("voter" + (a+1), "!future")
      MockIRCMessage("voter" + (a+1), "!play 4, spot 1")
      MockIRCMessage("voter" + (a+1), "!future")
      MockIRCMessage("voter" + (a+1), "!att my 1, target his 2")
    }
    MockIRCMessage("voter5", "!play 1, spot 1")
    MockIRCMessage("voter5", "!play 3, spot 1")
    MockIRCMessage("voter5", "!play 1, spot 6")
    MockIRCMessage("voter5", "!play 1, spot 2")
    MockIRCMessage("voter5", "!play 1, spot 3")
    MockIRCMessage("voter5", "!play 1, spot 4")
    MockIRCMessage("voter5", "!play 1, spot 5")


    TimeUnit.SECONDS.sleep(3)
    ircLogic ! "Direct Decide"

    system.scheduler.scheduleOnce(55.seconds, this.self, "MultipleFutureAdjustmentResults")
  }


  def MultipleFutureAdjustmentResults(): Unit = {
    val friendlyHandIds = new Array[Int](6)
    friendlyHandIds(0) = 4
    friendlyHandIds(1) = 6
    friendlyHandIds(2) = 7
    friendlyHandIds(3) = 8
    friendlyHandIds(4) = 9
    friendlyHandIds(5) = 10

    val friendlyBoardIds = new Array[Int](6)
    friendlyBoardIds(0) = 1
    friendlyBoardIds(1) = 3
    friendlyBoardIds(2) = 21
    friendlyBoardIds(3) = 22
    friendlyBoardIds(4) = 23
    friendlyBoardIds(5) = 25

    val enemyBoardIds = new Array[Int](2)
    enemyBoardIds(0) = 34
    enemyBoardIds(1) = 35

    val passed = CheckTestResults(friendlyHandIds, friendlyBoardIds, enemyBoardIds)

    if (passed == true) {
      log.info("MultipleFutureAdjustmentTest Passed!")
    }

    multipleFutureAdjustmentComplete = true
    readyForTest = true
  }

  //Bind and Future Mix Test
  //Triple Future Test
  //Hurry Popularity Test
  //End Turn Popularity Test?
  //Multiple of the same command test
  //Bind Value Test
  //Future Value Test


}
