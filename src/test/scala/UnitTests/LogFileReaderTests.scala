package UnitTests

import java.io._

import com.typesafe.config.ConfigFactory
import org.scalatest.{Matchers, FlatSpec}
import tph._

/**
  * Created by Harambe on 2/22/2017.
  */
class LogFileReaderTests extends FlatSpec with Matchers {

  val config = ConfigFactory.load()
  val hearthstoneLogFile = new File(config.getString("tph.readerFiles.outputLog"))
  val actionLogFile = new File(config.getString("tph.writerFiles.actionLog"))


  "LogFileReader" should "monitor changes in Hearthstone output log and print to actionLog" in {


    val writer = new PrintWriter(new FileWriter(hearthstoneLogFile))
    val reader = new BufferedReader(new FileReader(actionLogFile))

    new LogFileReader().poll()


    writer.println("[Zone] ZoneChangeList.ProcessChanges() - TRANSITIONING card [name=some id=some zone=PLAY zonePos=0 cardId=some player=1] to FRIENDLY PLAY (Hero)")
    writer.flush()

    writer.println("some id=55 local=False [name=Friendly Hand 1 id=1 zone=HAND zonePos=1 cardId=some player=1] pos from 55 -> 55")
    writer.flush()

    writer.println("some id=55 local=False [name=Friendly Hand 2 id=2 zone=HAND zonePos=2 cardId=some player=1] pos from 55 -> 55")
    writer.flush()

    writer.println("some id=55 local=False [name=Friendly Hand 3 id=3 zone=HAND zonePos=3 cardId=some player=1] pos from 55 -> 55")
    writer.flush()

    writer.println("Some other text that isnt a HSAction")
    writer.flush()

    writer.println("some FULL_ENTITY - Updating [name=Friendly Board 1 id=11 zone=PLAY zonePos=1 some player=1 some")
    writer.flush()

    writer.println("some FULL_ENTITY - Updating [name=Friendly Board 2 id=12 zone=PLAY zonePos=2 some player=1 some")
    writer.flush()

    writer.println("[Zone] ZoneChangeList.ProcessChanges() - id=55 local=False [name=UNKNOWN ENTITY [cardType=INVALID] id=21 zone=HAND zonePos=1 cardId= player=2] pos from .* -> .*")
    writer.flush()

    writer.println("Someother text that isnt a heartstone action")
    writer.flush()

    writer.println("[Zone] ZoneChangeList.ProcessChanges() - id=55 local=False [name=UNKNOWN ENTITY [cardType=INVALID] id=22 zone=HAND zonePos=2 cardId= player=2] pos from .* -> .*")
    writer.flush()

    writer.println("[Zone] ZoneChangeList.ProcessChanges() - id=55 local=False [name=UNKNOWN ENTITY [cardType=INVALID] id=23 zone=HAND zonePos=3 cardId= player=2] pos from .* -> .*")
    writer.flush()

    writer.println("some FULL_ENTITY - Updating [name=Enemy Board 1 id=31 zone=PLAY zonePos=1 some player=2 some")
    writer.flush()

    writer.println("some FULL_ENTITY - Updating [name=Enemy Board 2 id=32 zone=PLAY zonePos=2 some player=2 some")
    writer.flush()

    Thread.sleep(1000)




    val actualActionLogStrings = Stream.continually(reader.readLine()).takeWhile(_ != null).toList
    val expectedActionLogStrings = List(
      "[Zone] ZoneChangeList.ProcessChanges() - TRANSITIONING card [name=some id=some zone=PLAY zonePos=0 cardId=some player=1] to FRIENDLY PLAY (Hero)",
      "some id=55 local=False [name=Friendly Hand 1 id=1 zone=HAND zonePos=1 cardId=some player=1] pos from 55 -> 55",
      "some id=55 local=False [name=Friendly Hand 2 id=2 zone=HAND zonePos=2 cardId=some player=1] pos from 55 -> 55",
      "some id=55 local=False [name=Friendly Hand 3 id=3 zone=HAND zonePos=3 cardId=some player=1] pos from 55 -> 55",
      "some FULL_ENTITY - Updating [name=Friendly Board 1 id=11 zone=PLAY zonePos=1 some player=1 some",
      "some FULL_ENTITY - Updating [name=Friendly Board 2 id=12 zone=PLAY zonePos=2 some player=1 some",
      "[Zone] ZoneChangeList.ProcessChanges() - id=55 local=False [name=UNKNOWN ENTITY [cardType=INVALID] id=21 zone=HAND zonePos=1 cardId= player=2] pos from .* -> .*",
      "[Zone] ZoneChangeList.ProcessChanges() - id=55 local=False [name=UNKNOWN ENTITY [cardType=INVALID] id=22 zone=HAND zonePos=2 cardId= player=2] pos from .* -> .*",
      "[Zone] ZoneChangeList.ProcessChanges() - id=55 local=False [name=UNKNOWN ENTITY [cardType=INVALID] id=23 zone=HAND zonePos=3 cardId= player=2] pos from .* -> .*",
      "some FULL_ENTITY - Updating [name=Enemy Board 1 id=31 zone=PLAY zonePos=1 some player=2 some",
      "some FULL_ENTITY - Updating [name=Enemy Board 2 id=32 zone=PLAY zonePos=2 some player=2 some")

    actualActionLogStrings shouldEqual expectedActionLogStrings
  }


  "LogFileReader scenarios" should "detect and define player" in {

    val actualGameState = new LogParser().ConstructGameState(new File(getClass.getResource("/debugsituations/DefinePlayers.txt").getPath))
    val actualFriendlyHand = actualGameState.friendlyPlayer.hand
    val actualFriendlyBoard = actualGameState.friendlyPlayer.board
    val actualEnemyHand = actualGameState.enemyPlayer.hand
    val actualEnemyBoard = actualGameState.enemyPlayer.board

    val expectedFriendlyHand = List[HSCard](
      new Card("Equality", 67, 1, 500, 2),
      new Card("Leeroy Jenkins", 71, 2, 500, 2),
      new Card("Don Han'Cho", 79, 3, 500, 2),
      new Card("Blessed Champion", 53, 4, 500, 2)
    )

    val expectedFriendlyBoard = List[HSCard](
      new Card("Wild Pyromancer", 80, 500, 1, 2)
    )

    val expectedEnemyHand = List[HSCard](
      new Card("Constant Uninitialized", 4, 1, 500, 1),
      new Card("Constant Uninitialized", 43, 2, 500, 1),
      new Card("Constant Uninitialized", 48, 3, 500, 1),
      new Card("Constant Uninitialized", 17, 4, 500, 1)

    )
    val expectedEnemyBoard = List[HSCard](
      new Card("Sapling", 87, 500, 1, 1),
      new Card("Sapling", 88, 500, 2, 1)
    )

    actualFriendlyHand shouldEqual expectedFriendlyHand
    actualFriendlyBoard shouldEqual expectedFriendlyBoard
    actualEnemyHand shouldEqual expectedEnemyHand
    actualEnemyBoard shouldEqual expectedEnemyBoard

  }


  it should "Detect MulliganRedraw" in {

    val playerNumbers = new LogParser().GetPlayerNumbers(new File(getClass.getResource("/debugsituations/Mulligan.txt").getPath))

    val actualGameState = new LogParser().ConstructGameState(new File(getClass.getResource("/debugsituations/Mulligan.txt").getPath))
    val actualFriendlyHand = actualGameState.friendlyPlayer.hand
    val actualEnemyHand = actualGameState.enemyPlayer.hand

    val expectedFriendlyHand = List(
      new Card("Lay on Hands", 36, 1, Constants.INT_UNINIT, playerNumbers._1),
      new Card("Spellbreaker", 44, 2, Constants.INT_UNINIT, playerNumbers._1),
      new Card("Aldor Peacekeeper", 40, 3, Constants.INT_UNINIT, playerNumbers._1),
      new Card("Acidic Swamp Ooze", 58, 4, Constants.INT_UNINIT, playerNumbers._1),
      new Card("Solemn Vigil", 38, 5, Constants.INT_UNINIT, playerNumbers._1)
    )

    val expectedEnemyHand = List(
      new Card(Constants.STRING_UNINIT, 21, 1, Constants.INT_UNINIT, 1),
      new Card(Constants.STRING_UNINIT, 4, 2, Constants.INT_UNINIT, 1),
      new Card(Constants.STRING_UNINIT, 10, 3, Constants.INT_UNINIT, 1),
      new Card(Constants.STRING_UNINIT, 25, 4, Constants.INT_UNINIT, 1),
      new Card(Constants.STRING_UNINIT, 32, 5, Constants.INT_UNINIT, 1))


    actualFriendlyHand shouldEqual expectedFriendlyHand
    actualEnemyHand shouldEqual expectedEnemyHand
  }


  it should "Detect CardPlayed" in {

    val playerNumbers = new LogParser().GetPlayerNumbers(new File(getClass.getResource("/debugsituations/Mulligan.txt").getPath))

    val actualGameState = new LogParser().ConstructGameState(new File(getClass.getResource("/debugsituations/Mulligan.txt").getPath))
    val actualFriendlyHand = actualGameState.friendlyPlayer.hand
    val actualFriendlyBoard = actualGameState.friendlyPlayer.board
    val actualEnemyHand = actualGameState.enemyPlayer.hand
    val actualEnemyBoard = actualGameState.enemyPlayer.board

    val expectedFriendlyHand = List(
      new Card("Lay on Hands", 36, 1, Constants.INT_UNINIT, playerNumbers._1),
      new Card("Spellbreaker", 44, 2, Constants.INT_UNINIT, playerNumbers._1),
      new Card("Aldor Peacekeeper", 40, 3, Constants.INT_UNINIT, playerNumbers._1),
      new Card("Acidic Swamp Ooze", 58, 4, Constants.INT_UNINIT, playerNumbers._1),
      new Card("Solemn Vigil", 38, 5, Constants.INT_UNINIT, playerNumbers._1))

    val expectedFriendlyBoard = List(
      new Card("Acolyte of Pain", 48, Constants.INT_UNINIT, 1, 2))

    val expectedEnemyHand = List(
      new Card(Constants.STRING_UNINIT, 21, 1, Constants.INT_UNINIT, 1),
      new Card(Constants.STRING_UNINIT, 4, 2, Constants.INT_UNINIT, 1),
      new Card(Constants.STRING_UNINIT, 10, 3, Constants.INT_UNINIT, 1),
      new Card(Constants.STRING_UNINIT, 25, 4, Constants.INT_UNINIT, 1),
      new Card(Constants.STRING_UNINIT, 32, 5, Constants.INT_UNINIT, 1))

    val expectedEnemyBoard = List[HSCard]()


    actualFriendlyHand shouldEqual expectedFriendlyHand
    actualFriendlyBoard shouldEqual expectedFriendlyBoard
    actualEnemyHand shouldEqual expectedEnemyHand
    actualEnemyBoard shouldEqual expectedEnemyBoard
  }


  it should "Detect Card Death" in {

    val playerNumbers = new LogParser().GetPlayerNumbers(new File(getClass.getResource("/debugsituations/CardDeath.txt").getPath))

    val actualGameState = new LogParser().ConstructGameState(new File(getClass.getResource("/debugsituations/CardDeath.txt").getPath))
    val actualFriendlyHand = actualGameState.friendlyPlayer.hand
    val actualFriendlyBoard = actualGameState.friendlyPlayer.board
    val actualEnemyHand = actualGameState.enemyPlayer.hand
    val actualEnemyBoard = actualGameState.enemyPlayer.board

    val expectedFriendlyHand = List(
      new Card("Truesilver Champion", 28, 1, 500, 1),
      new Card("Wild Pyromancer", 15, 3, 500, 1),
      new Card("The Coin", 68, 4, 500, 1),
      new Card("Acolyte of Pain", 25, 2, 500, 1),
      new Card("Solemn Vigil", 12, 5, 500, 1),
      new Card("Eater of Secrets", 32, 6, 500, 1),
      new Card("Spellbreaker", 30, 7, 500, 1))

    val expectedFriendlyBoard = List(


    )

    val expectedEnemyHand = List(
      new Card("Constant Uninitialized", 39, 1, 500, 2),
      new Card("Constant Uninitialized", 59, 2, 500, 2),
      new Card("Constant Uninitialized", 34, 3, 500, 2),
      new Card("Constant Uninitialized", 37, 4, 500, 2))

    val expectedEnemyBoard = List(
      new Card("Stonetusk Boar", 60, 500, 1, 2),
      new Card("Healing Totem", 69, 500, 2, 2)
    )

    actualFriendlyHand shouldEqual expectedFriendlyHand
    actualFriendlyBoard shouldEqual expectedFriendlyBoard
    actualEnemyHand shouldEqual expectedEnemyHand
    actualEnemyBoard shouldEqual expectedEnemyBoard
  }

  it should "Detect Hex"


}
