package UnitTests

import java.io._

import com.typesafe.config.ConfigFactory
import org.scalatest.{Matchers, FlatSpec}
import tph.LogFileReader

/**
  * Created by Harambe on 2/22/2017.
  */
class LogFileReaderTests extends FlatSpec with Matchers {

  val config = ConfigFactory.load()
  val hearthstoneLogFile = new File(config.getString("tph.outputLog.path"))
  val actionLogFile = new File(config.getString("tph.actionLog.path"))



  "LogFileReader" should "monitor changes in Hearthstone output log" in {


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





  }




}
