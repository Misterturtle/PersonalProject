package tph.tests

import java.io.{FileWriter, PrintWriter, File}
import java.util.concurrent.{ScheduledThreadPoolExecutor, TimeUnit}

import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import tph._

import scala.collection.mutable.ListBuffer

class TestBrain(testMode: Boolean) extends LazyLogging {


  val theBrain = new TheBrain(testMode)

  val config = ConfigFactory.load()


  var readyForTest = true

  var frozenGameStatusComplete = false
  var cardPlayedAdjustmentComplete = false
  var minionDeathAdjustmentComplete = false
  var multipleBindAdjustmentComplete = false
  var multipleFutureAdjustmentComplete = false


  val scheduler = new ScheduledThreadPoolExecutor(1)


  def Init(): Unit = {


    //STARTING STATE HERE
    var startingState = theBrain.myTurn
    var startingMenu = Constants.MenuNames.IN_GAME
    theBrain.mulliganComplete = true



    theBrain.logFileReader.poll("")
    theBrain.ChangeState(startingState)
    theBrain.ChangeMenu(startingMenu)

    RunTests()
  }

  def RunTests(): Unit = {

    if (readyForTest) {
      if (!frozenGameStatusComplete)
        FrozenGameStatusTest()
      else {
        if (!cardPlayedAdjustmentComplete)
          CardPlayedAdjustmentTest()
        else {
          if (!minionDeathAdjustmentComplete)
            MinionDeathAdjustmentTest()
          else {
            if (!multipleBindAdjustmentComplete)
              MultipleBindAdjustmentTest()
            else {
              if (!multipleFutureAdjustmentComplete)
                MultipleFutureAdjustmentTest()
            }
          }
        }
      }
    }
  }


  def MockIRCMessage(sender: String, vote: String) {

    theBrain.ircBot.onMessage(config.getString("tph.irc.channel"), sender, sender, config.getString("tph.irc.host"), vote)
  }

  def GetGameStatus(): FrozenGameStatus = {
    theBrain.GetGameStatus()
  }

  def CheckTestResults(friendlyHandIds: Array[Int], friendlyBoardIds: Array[Int], enemyBoardIds: Array[Int]): Boolean = {
    val frozenStatus = GetGameStatus()
    val gameStatus = frozenStatus.frozenPlayers

    if (gameStatus(0).hand.size != friendlyHandIds.size) {
      logger.error("Test Failed. My hand size is " + gameStatus(0).hand.size + " instead of " + friendlyHandIds.size + ".")
      return false
    }

    if (gameStatus(0).hand.size != friendlyHandIds.size) {
      logger.error("Test Failed. My hand size is " + gameStatus(0).hand.size + " instead of " + friendlyHandIds.size + ".")
      return false
    }

    for (a <- 0 until gameStatus(0).hand.size) {
      val index = gameStatus(0).hand.indexWhere(_.handPosition == (a + 1))

      if (index != -1) {
        if (gameStatus(0).hand(index).id != friendlyHandIds(a)) {
          logger.error("Test Failed. Card " + (a + 1) + " ID is " + gameStatus(0).hand(index).id + " instead of " + friendlyHandIds(a) + ".")
          return false
        }
      }
      else {
        logger.error("Test Failed. Friendly Hand Position " + (a + 1) + "does not exist.")
        return false
      }
    }

    for (a <- 0 until gameStatus(0).board.size) {
      val index = gameStatus(0).board.indexWhere(_.boardPosition == (a + 1))

      if (index != -1) {
        if (gameStatus(0).board(index).id != friendlyBoardIds(a)) {
          logger.error("Test Failed. Friendly Minion " + (a + 1) + " ID is " + gameStatus(0).board(index).id + " instead of " + friendlyBoardIds(a) + " .")
          return false
        }
      }
      else {
        logger.error("Test Failed. Friendly Hand Position " + (a + 1) + "does not exist.")
        return false
      }
    }

    for (a <- 0 until gameStatus(1).board.size) {
      val index = gameStatus(1).board.indexWhere(_.boardPosition == (a + 1))

      if (index != -1) {
        if (gameStatus(1).board(index).id != enemyBoardIds(a)) {
          logger.error("Test Failed. Enemy Minion " + (a + 1) + " ID is " + gameStatus(1).board(index).id + " instead of " + enemyBoardIds(a) + " .")
          return false
        }
      }
      else {
        logger.error("Test Failed. Enemy Board Position " + (a + 1) + "does not exist.")
        return false
      }
    }

    return true

  }


  ////////////////////////////////////////////////////  TESTS BELOW HERE //////////////////////////////////

  def FrozenGameStatusTest(): Unit = {


    logger.debug("Starting Frozen Game Status Test")
    theBrain.Reset()
    readyForTest = false


    //Setting up the game status
    theBrain.gameStatus.DefinePlayers(1)
    for (a <- 1 to 10) {
      theBrain.gameStatus.KnownCardDrawn("MyCard" + a, a, a, 1)
      theBrain.gameStatus.EnemyCardDrawnEvent(10 + a, a, 2)
    }

    for (a <- 1 to 5) {
      theBrain.gameStatus.MinionSummoned("MyBoardMinion" + a, 20 + a, a, 1)
      theBrain.gameStatus.MinionSummoned("HisBoardMinion" + a, 30 + a, a, 2)
    }

    val oldGameStatus: FrozenGameStatus = theBrain.GetGameStatus()

    theBrain.gameStatus.me.hand(0).name = "Changed Name"
    theBrain.gameStatus.me.hand(0).handPosition = 0
    theBrain.gameStatus.me.board(0).name = "Changed Name"
    theBrain.gameStatus.me.board(0).boardPosition = 0

    theBrain.gameStatus.him.hand(0).name = "Changed Name"
    theBrain.gameStatus.him.hand(0).handPosition = 0
    theBrain.gameStatus.him.board(0).name = "Changed Name"
    theBrain.gameStatus.him.board(0).boardPosition = 0



    val newGameStatus: FrozenGameStatus = theBrain.GetGameStatus()


    logger.debug("Checking the results of Frozen Game Status Test")

    if (oldGameStatus.frozenPlayers(0).hand(0).name == "Changed Name" ||
      oldGameStatus.frozenPlayers(0).hand(0).handPosition == 0 ||
      oldGameStatus.frozenPlayers(0).board(0).name == "Changed Name" ||
      oldGameStatus.frozenPlayers(0).board(0).boardPosition == 0 ||
      oldGameStatus.frozenPlayers(1).hand(0).name == "Changed Name" ||
      oldGameStatus.frozenPlayers(1).hand(0).handPosition == 0 ||
      oldGameStatus.frozenPlayers(1).board(0).name == "Changed Name" ||
      oldGameStatus.frozenPlayers(1).board(0).boardPosition == 0) {
      logger.debug("Frozen Game Status Test Failed")
    }
    else
      logger.debug("Frozen Game Status Test Passed!")

    frozenGameStatusComplete = true
    readyForTest = true
    RunTests()
  }


  def CardPlayedAdjustmentTest(): Unit = {

    logger.debug("Starting Card Played Adjustment Test")
    theBrain.Reset()
    readyForTest = false


    //Setting up the game status
    theBrain.gameStatus.DefinePlayers(1)
    for (a <- 1 to 10) {
      theBrain.gameStatus.KnownCardDrawn("MyCard" + a, a, a, 1)

    }
    for (a <- 1 to 5) {
      theBrain.gameStatus.MinionSummoned("MyBoardMinion" + a, 20 + a, a, 1)
      theBrain.gameStatus.MinionSummoned("HisBoardMinion" + a, 30 + a, a, 2)
    }
    logger.debug("Game status has been set for CardPlayedAdjustment Test")




    TimeUnit.SECONDS.sleep(1)
    for (a <- 1 to 5) {
      MockIRCMessage("voter" + (a), "!play 1")
      MockIRCMessage("voter" + (a), "!play " + (a))
    }
    MockIRCMessage("voter1", "!play 2")

    TimeUnit.SECONDS.sleep(1)
    theBrain.ircLogic.Decide()
    CardPlayedAdjustmentResults()

    //    val checkTest = new Runnable {
    //      override def run(): Unit = CardPlayedAdjustmentResults()
    //    }
    //
    //    scheduler.schedule(checkTest, 5, TimeUnit.SECONDS)
  }

  def CardPlayedAdjustmentResults(): Unit = {
    logger.debug("Checking results of Card Played Adjustment Test")

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
      logger.error("CardPlayedAdjustmentTest Passed!")
      readyForTest = true
    }
    if (!passed) {
      logger.error("CardPlayedAdjustmentTest Failed!")
    }

    cardPlayedAdjustmentComplete = true

    RunTests()
  }


  def MinionDeathAdjustmentTest(): Unit = {
    logger.debug("Starting Minion Death Adjustment Test")
    theBrain.Reset()

    readyForTest = false



    //Setting up the game status
    theBrain.gameStatus.DefinePlayers(1)
    for (a <- 1 to 7) {
      theBrain.gameStatus.MinionSummoned("MyBoardMinion" + a, 20 + a, a, 1)
      theBrain.gameStatus.MinionSummoned("HisBoardMinion" + a, 30 + a, a, 2)
    }
    logger.debug("Game status has been set for Minion Death Adjustment Test")



    TimeUnit.SECONDS.sleep(1)
    for (a <- 1 to 5) {
      MockIRCMessage("voter" + (a), "!att my 1, target his 1")
      MockIRCMessage("voter" + (a), "!att my 1, target his " + (a))
    }
    MockIRCMessage("voter1", "!att my 3, target his 3")
    MockIRCMessage("voter2", "!att my 3, target his 3")

    TimeUnit.SECONDS.sleep(3)
    theBrain.ircLogic.Decide()
    MinionDeathAdjustmentResults()

    //    val checkTest = new Runnable {
    //      override def run(): Unit = MinionDeathAdjustmentResults()
    //    }
    //
    //    scheduler.schedule(checkTest, 5, TimeUnit.SECONDS)

  }


  def MinionDeathAdjustmentResults(): Unit = {
    logger.debug("Checking results of Minion Death Adjustment Test")
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
      readyForTest = true
      logger.error("MinionDeathAdjustmentTest Passed!")
    }
    if (!passed) {
      logger.error("MinionDeathAdjustmentTest Failed!")
    }

    minionDeathAdjustmentComplete = true

    RunTests()
  }


  def MultipleBindAdjustmentTest(): Unit = {
    logger.debug("Starting Multiple Bind Adjustment Test")
    theBrain.Reset()
    readyForTest = false
    TimeUnit.SECONDS.sleep(1)


    //Setting up the game status
    theBrain.gameStatus.DefinePlayers(1)
    for (a <- 1 to 5) {
      theBrain.gameStatus.MinionSummoned("MyBoardMinion" + a, 20 + a, a, 1)
      theBrain.gameStatus.MinionSummoned("HisBoardMinion" + a, 30 + a, a, 2)
    }

    for (a <- 1 to 10) {
      theBrain.gameStatus.KnownCardDrawn("MyCard" + a, a, a, 1)

    }



    for (a <- 1 to 5) {
      MockIRCMessage("voter" + (a), "!bind")
      MockIRCMessage("voter" + (a), "!play 1, spot 1")
      MockIRCMessage("voter" + (a), "!bind")
      //Should really be att my 6, target his 1
      MockIRCMessage("voter" + (a), "!att my 5, target his 1")
      MockIRCMessage("voter" + (a), "!bind")
      //Should really be att my 2, target his 4
      MockIRCMessage("voter" + (a), "!att my 1, target his 5")
    }
    MockIRCMessage("voter1", "!att my 5, target his 1")
    MockIRCMessage("voter2", "!att my 5, target his 1")
    MockIRCMessage("voter3", "!att my 1, target his 5")
    MockIRCMessage("voter4", "!att my 1, target his 5")

    TimeUnit.SECONDS.sleep(3)
    theBrain.ircLogic.Decide()
    MultipleBindAdjustmentResults()

    //    val checkTest = new Runnable {
    //      override def run(): Unit = MultipleBindAdjustmentResults()
    //    }
    //
    //    scheduler.schedule(checkTest, 5, TimeUnit.SECONDS)


  }


  def MultipleBindAdjustmentResults(): Unit = {
    logger.debug("Checking results of Multiple Bind Adjustment Test")
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
      logger.error("MultipleBindAdjustmentTest Passed!")
      readyForTest = true
    }
    if (!passed) {
      logger.error("MultipleBindAdjustmentTest Failed!")
    }


    multipleBindAdjustmentComplete = true
    RunTests()
  }

  def MultipleFutureAdjustmentTest(): Unit = {
    logger.debug("Starting Multiple Future Adjustment Test")

    theBrain.Reset()
    readyForTest = false

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

    //Setting up the game status
    theBrain.gameStatus.DefinePlayers(1)
    for (a <- 1 to 5) {
      theBrain.gameStatus.MinionSummoned("MyBoardMinion" + a, 20 + a, a, 1)
      theBrain.gameStatus.MinionSummoned("HisBoardMinion" + a, 30 + a, a, 2)
    }

    for (a <- 1 to 10) {
      theBrain.gameStatus.KnownCardDrawn("MyCard" + a, a, a, 1)

    }




    for (a <- 1 to 4) {
      //4 main voters

      //This should play first
      MockIRCMessage("voter" + (a), "!play 1, spot 1")
      //This should play second
      //Should really be play 2, spot 2
      MockIRCMessage("voter" + (a), "!play 3, spot 1")
      //This should play third
      //Should really be att my 6, target his 1
      MockIRCMessage("voter" + (a), "!att my 4, target his 1")
      //Start future
      MockIRCMessage("voter" + (a), "!future")
      //Should really be play 1, spot 3
      MockIRCMessage("voter" + (a), "!play 2, spot 1")
      MockIRCMessage("voter" + (a), "!future")
      //Should really be att my 3, target his 1
      MockIRCMessage("voter" + (a), "!att my 1, target his 2")
      MockIRCMessage("voter" + (a), "!future")
      //Should really be play 2, spot 3
      MockIRCMessage("voter" + (a), "!play 4, spot 1")
      MockIRCMessage("voter" + (a), "!future")
      //Should really be att my 3, target his 1
      MockIRCMessage("voter" + (a), "!att my 1, target his 2")
    }
    //Fifth voter to add value to first three votes
    MockIRCMessage("voter5", "!play 1, spot 1")
    MockIRCMessage("voter5", "!play 3, spot 1")
    MockIRCMessage("voter5", "!att my 4, target his 1")
    MockIRCMessage("voter6", "!play 1, spot 1")
    MockIRCMessage("voter6", "!play 3, spot 1")
    MockIRCMessage("voter6", "!att my 4, target his 1")
    MockIRCMessage("voter7", "!play 1, spot 1")
    MockIRCMessage("voter7", "!play 3, spot 1")
    MockIRCMessage("voter7", "!att my 4, target his 1")

    MockIRCMessage("voter8", "!play 1, spot 1")
    MockIRCMessage("voter9", "!play 1, spot 1")

    MockIRCMessage("voter8", "!play 3, spot 1")


    TimeUnit.SECONDS.sleep(3)
    theBrain.ircLogic.Decide()
    MultipleFutureAdjustmentResults()


    //    val checkTest = new Runnable {
    //      override def run(): Unit = MultipleFutureAdjustmentResults()
    //    }
    //
    //    scheduler.schedule(checkTest, 15, TimeUnit.SECONDS)


  }


  def MultipleFutureAdjustmentResults(): Unit = {
    logger.debug("Checking results of Multiple Future Adjustment Test")
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
      readyForTest = true
      logger.info("MultipleFutureAdjustmentTest Passed!")
    }
    else
      logger.info("MultipleFutureAdjustmentTest Failed!")

    multipleFutureAdjustmentComplete = true
    RunTests()
  }

  //Bind and Future Mix Test
  //Triple Future Test
  //Hurry Popularity Test
  //End Turn Popularity Test?
  //Multiple of the same command test
  //Bind Value Test
  //Future Value Test


}