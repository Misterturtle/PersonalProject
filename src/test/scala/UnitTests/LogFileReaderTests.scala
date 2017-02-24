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

    writer.println("someid=55 local=some [id=21 cardId=some type=some zone=HAND zonePos=1 player=2] pos from 55 -> 55")
    writer.flush()

    writer.println("Someother text that isnt a heartstone action")
    writer.flush()

    writer.println("someid=55 local=some [id=22 cardId=some type=some zone=HAND zonePos=2 player=2] pos from 55 -> 55")
    writer.flush()

    writer.println("someid=55 local=some [id=23 cardId=some type=some zone=HAND zonePos=3 player=2] pos from 55 -> 55")
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
      "someid=55 local=some [id=21 cardId=some type=some zone=HAND zonePos=1 player=2] pos from 55 -> 55",
      "someid=55 local=some [id=22 cardId=some type=some zone=HAND zonePos=2 player=2] pos from 55 -> 55",
      "someid=55 local=some [id=23 cardId=some type=some zone=HAND zonePos=3 player=2] pos from 55 -> 55",
      "some FULL_ENTITY - Updating [name=Enemy Board 1 id=31 zone=PLAY zonePos=1 some player=2 some",
      "some FULL_ENTITY - Updating [name=Enemy Board 2 id=32 zone=PLAY zonePos=2 some player=2 some")

    actualActionLogStrings shouldEqual expectedActionLogStrings
  }


  "LogFileReader scenarios" should "detect and define player" in {

    val actualGameState = new LogParser().ConstructGameState(new File(getClass.getResource("/debugsituations/DefinePlayers.txt").getPath))
    val actualFriendlyHand = actualGameState.friendlyPlayer.hand
    val actualFriendlyBoard = actualGameState.friendlyPlayer.board
    val actualEnemyHand =actualGameState.enemyPlayer.hand
    val actualEnemyBoard = actualGameState.enemyPlayer.board

    val expectedFriendlyHand = List[HSCard](
      new Card("Equality", 67, 1, 500, 2),
    new Card("Leeroy Jenkins", 71, 2, 500, 2),
    new Card("Don Han'Cho", 79, 3, 500, 2),
    new Card("Blessed Champion", 53, 4, 500, 2)
    )

    val expectedFriendlyBoard = List[HSCard](
      new Card("The Coin", 86, 500, 0, 2),
    new Card("Uther Lightbringer", 84, 500, 3, 2),
    new Card("Reinforce", 85, 500, 2, 2),
    new Card("Wild Pyromancer", 80, 500, 1, 2)

    )

    val expectedEnemyHand = List[HSCard](

    )
    val expectedEnemyBoard = List[HSCard](
      new Card("Shapeshift", 83, 500, 1, 1),
    new Card("Sapling", 87, 500, 2, 1),
    new Card("Malfurion Stormrage", 82, 500, 4, 1),
    new Card("Sapling", 88, 500, 3, 1),
    new Card("Living Roots", 35, 500, 0, 1)
    )

    actualFriendlyHand shouldEqual expectedFriendlyHand
    actualFriendlyBoard shouldEqual expectedFriendlyBoard
    actualEnemyHand shouldEqual expectedEnemyHand
    actualEnemyBoard shouldEqual expectedEnemyBoard

  }




}
