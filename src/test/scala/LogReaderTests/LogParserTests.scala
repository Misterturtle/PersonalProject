package LogReaderTests

import java.io._

import FileReaders.{HSAction, LogParser}
import org.scalatest.{FlatSpec, Matchers}
import tph.{Card, Constants}

/**
  * Created by Harambe on 2/21/2017.
  */
class LogParserTests extends FlatSpec with Matchers {

  val mockActionFile = new File(getClass.getResource( "/debugsituations/defaultMockActionLog.txt").getPath)

  "A Log Parser" should "Get Player Numbers" in {

    val actualPlayerNumbers = new LogParser().GetPlayerNumbers(mockActionFile)
    val expectedPlayerNumbers = (2,1)
    actualPlayerNumbers shouldEqual expectedPlayerNumbers
  }

  it should "Identify HSAction" in{
    import HSAction._
    val tempFile = new File(this.getClass.getResource("/tempHSActionLog.txt").getPath)
    val writer = new PrintWriter(new FileWriter(tempFile))
    val reader = new BufferedReader(new FileReader(tempFile))

    //Friendly Minion Controlled
    writer.println("some [name=Friendly Minion 1 id=11 zone=PLAY zonePos=1 some zone from FRIENDLY PLAY -> OPPOSING PLAY")
    writer.flush()
    //Enemy Minion Controlled
    writer.println("some[name=Enemy Minion 1 id=21 zone=PLAY zonePos=1 .+ zone from OPPOSING PLAY -> FRIENDLY PLAY")
    writer.flush()
    //Enemy Card Drawn

    writer.println("[Zone] ZoneChangeList.ProcessChanges() - processing index=55 change=powerTask=[power=[type=TAG_CHANGE entity=[id=55 cardId= name=UNKNOWN ENTITY [cardType=INVALID]] tag=ZONE_POSITION value=55] complete=False] entity=[name=UNKNOWN ENTITY [cardType=INVALID] id=27 zone=HAND zonePos=0 cardId= player=2] srcZoneTag=INVALID srcPos= dstZoneTag=INVALID dstPos=7")
    writer.flush()
    //Face Attack Value
    writer.println("[Power] PowerTaskList.DebugPrintPower() -     TAG_CHANGE Entity=[name=some id=55 zone=PLAY zonePos=0 cardId=HEROsome player=1] tag=ATK value=5")
    writer.flush()
    //Secret Played
    writer.println("[Zone] ZoneChangeList.ProcessChanges() - TRANSITIONING card someid=2 some zone=SECRET zonePos=55 some player=1] to some SECRET")
    writer.flush()
    //Known Card Drawn
    writer.println("[Zone] ZoneChangeList.ProcessChanges() - processing index=55 change=powerTask=[power=[type=TAG_CHANGE entity=[id=55 cardId=some name=some] tag=ZONE_POSITION value=55] complete=False] entity=[name=Friendly Minion 7 id=7 zone=HAND zonePos=0 cardId=some player=1] srcZoneTag=INVALID srcPos= dstZoneTag=INVALID dstPos=7")
    writer.flush()
    //Card Played
    writer.println("[Zone] ZoneChangeList.ProcessChanges() - processing index=55 change=powerTask=[power=[type=TAG_CHANGE entity=[id=55 cardId=some name=some] tag=JUST_PLAYED value=1] complete=False] entity=[name=Enemy Card 3 id=23 zone=PLAY zonePos=5 cardId=some player=2] srcZoneTag=INVALID srcPos= dstZoneTag=INVALID dstPos=")
    writer.flush()
    //Card Death
    writer.println("[Zone] ZoneChangeList.ProcessChanges() - TRANSITIONING card [name=Friendly Minion 2 id=12 zone=GRAVEYARD zonePos=55 cardId=some player=1] to some GRAVEYARD")
    writer.flush()
    //Minion Summoned
    writer.println("some FULL_ENTITY - Updating [name=Friendly Minion 5 id=15 zone=PLAY zonePos=5 some player=1 some")
    writer.flush()
    //Transform
    writer.println("[Power] PowerTaskList.DebugPrintPower() -     TAG_CHANGE Entity=[name=some id=3 zone=PLAY zonePos=3 cardId=some player=55] tag=LINKED_ENTITY value=102")
    writer.flush()
    //Sap
    writer.println("[Zone] ZoneChangeList.ProcessChanges() - id=55 local=some [name=Friendly Minion 5 id=15 zone=HAND zonePos=55 cardId=some player=1] zone from FRIENDLY PLAY -> FRIENDLY HAND")
    writer.flush()

    val streams = Stream.continually(reader.readLine()).takeWhile(_ != null)
    val actualHSActionList = streams.foldLeft(List[HSAction]()) {(r, c) =>
      val hsAction = new LogParser().IdentifyHSAction(c)
      if (hsAction != new HSActionUninit())
        r ::: List(hsAction)
      else
        r
    }

      val expectedHSActionList = List(
        new FriendlyMinionControlled("Friendly Minion 1", 11, 1),
        new EnemyMinionControlled("Enemy Minion 1", 21, 1),
        new CardDrawn(Constants.STRING_UNINIT, 27, 7, 2),
        new ChangeFaceAttackValue(1, 5),
        new SecretPlayed(2, 1),
        new CardDrawn("Friendly Minion 7", 7, 7, 1),
        new CardPlayed("Enemy Card 3", 23, 5, 2),
        new CardDeath("Friendly Minion 2", 12, 1),
        new MinionSummoned("Friendly Minion 5", 15, 5, 1),
        new Transform(3, 3, 102),
        new FriendlyCardReturn("Friendly Minion 5", 15, 1))

    new FriendlyMinionControlled("test", 1, 1) shouldEqual new FriendlyMinionControlled("test", 1, 1)

    actualHSActionList.head shouldEqual expectedHSActionList.head
    actualHSActionList(1) shouldEqual expectedHSActionList(1)
    actualHSActionList(2) shouldEqual expectedHSActionList(2)
    actualHSActionList(3) shouldEqual expectedHSActionList(3)
    actualHSActionList(4) shouldEqual expectedHSActionList(4)
    actualHSActionList(5) shouldEqual expectedHSActionList(5)
    actualHSActionList(6) shouldEqual expectedHSActionList(6)
    actualHSActionList(7) shouldEqual expectedHSActionList(7)
    actualHSActionList(8) shouldEqual expectedHSActionList(8)
    actualHSActionList(9) shouldEqual expectedHSActionList(9)
    actualHSActionList(10) shouldEqual expectedHSActionList(10)

    }

  it should "construct a GameState" in {

    val mockActionLog = new File(getClass.getResource("/mockActionLog.txt").getPath)
    val actualFriendlyHand = new LogParser().ConstructGameState(mockActionLog).friendlyPlayer.hand
    val expectedFriendlyHand = Constants.TestConstants.defaultGameState.friendlyPlayer.hand

    val actualFriendlyBoard = new LogParser().ConstructGameState(mockActionLog).friendlyPlayer.board
    val expectedFriendlyBoard = Constants.TestConstants.defaultGameState.friendlyPlayer.board

    val actualEnemyHand = new LogParser().ConstructGameState(mockActionLog).enemyPlayer.hand
    val expectedEnemyHand = List(
      new Card(Constants.STRING_UNINIT, 21, 1, Constants.INT_UNINIT, 2),
      new Card(Constants.STRING_UNINIT, 22, 2, Constants.INT_UNINIT, 2),
      new Card(Constants.STRING_UNINIT, 23, 3, Constants.INT_UNINIT, 2),
      new Card(Constants.STRING_UNINIT, 24, 4, Constants.INT_UNINIT, 2),
      new Card(Constants.STRING_UNINIT, 25, 5, Constants.INT_UNINIT, 2),
      new Card(Constants.STRING_UNINIT, 26, 6, Constants.INT_UNINIT, 2))

    val actualEnemyBoard = new LogParser().ConstructGameState(mockActionLog).enemyPlayer.board
    val expectedEnemyBoard = Constants.TestConstants.defaultGameState.enemyPlayer.board

    actualFriendlyHand shouldEqual expectedFriendlyHand
    actualFriendlyBoard shouldEqual expectedFriendlyBoard
    actualEnemyHand shouldEqual expectedEnemyHand
    actualEnemyBoard shouldEqual expectedEnemyBoard
  }








}
