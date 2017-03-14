package RegressionTests

import java.io._

import VoteSystem.{VoteParser, VoteManager}
import com.typesafe.config.ConfigFactory
import org.jibble.pircbot.PircBot
import org.scalatest.tagobjects.Slow
import org.scalatest.{Tag, Matchers, FlatSpec}
import tph.IRCBot
import FileReaders.LogFileReader


import scala.collection.mutable.ListBuffer

/**
  * Created by Harambe on 2/22/2017.
  */
object Acceptance extends Tag("com.tph.tags.Acceptance")

class StartToEndTests extends FlatSpec with Matchers {

  "TPH" should "Initialize LogFileReader" taggedAs Acceptance in {

    val config = ConfigFactory.load()
    val logFileWriter = new PrintWriter(new FileWriter(config.getString("tph.readerFiles.outputLog")))
    val actionFileReader = new BufferedReader(new FileReader(config.getString("tph.writerFiles.actionLog")))


    logFileWriter.println("This is a test line")
    logFileWriter.flush()

    val logFileReader = new LogFileReader()
    logFileReader.poll()

    logFileWriter.println("This should erase the previous file")
    logFileWriter.println("This mocks hearthstones output_log.txt strings")
    logFileWriter.println("actionFileWriter should write known commands to actionLog")
    logFileWriter.println("[Zone] ZoneChangeList.ProcessChanges() - processing index=55 change=powerTask=[power=[type=TAG_CHANGE entity=[id=55 cardId=some name=some] tag=ZONE_POSITION value=55] complete=False] entity=[name=Friendly Hand 7 id=7 zone=HAND zonePos=0 cardId=some player=1] srcZoneTag=INVALID srcPos= dstZoneTag=INVALID dstPos=7")
    logFileWriter.println("[Zone] ZoneChangeList.ProcessChanges() - processing index=55 change=powerTask=[power=[type=TAG_CHANGE entity=[id=55 cardId=some name=some] tag=JUST_PLAYED value=1] complete=False] entity=[name=Enemy Card 3 id=23 zone=PLAY zonePos=5 cardId=some player=2] srcZoneTag=INVALID srcPos= dstZoneTag=INVALID dstPos=")
    logFileWriter.flush()

    Thread.sleep(1000)


    val actualStringList = Stream.continually(actionFileReader.readLine()).takeWhile(_ != null).toList

    actualStringList.size shouldEqual 2
    actualStringList(0) shouldEqual "[Zone] ZoneChangeList.ProcessChanges() - processing index=55 change=powerTask=[power=[type=TAG_CHANGE entity=[id=55 cardId=some name=some] tag=ZONE_POSITION value=55] complete=False] entity=[name=Friendly Hand 7 id=7 zone=HAND zonePos=0 cardId=some player=1] srcZoneTag=INVALID srcPos= dstZoneTag=INVALID dstPos=7"
    actualStringList(1) shouldEqual "[Zone] ZoneChangeList.ProcessChanges() - processing index=55 change=powerTask=[power=[type=TAG_CHANGE entity=[id=55 cardId=some name=some] tag=JUST_PLAYED value=1] complete=False] entity=[name=Enemy Card 3 id=23 zone=PLAY zonePos=5 cardId=some player=2] srcZoneTag=INVALID srcPos= dstZoneTag=INVALID dstPos="
  }

  "The ircBot" should "initialize properly" taggedAs (Acceptance, Slow) in {
    val vm = new VoteManager()
    val ircBot = new IRCBot(vm)
    ircBot.init()
    //Channel list does not load until server responds initially. Waiting an arbitrary 10 seconds for connection to process.
    Thread.sleep(10000)
    ircBot.isConnected shouldEqual true
    ircBot.getName shouldEqual "TPHBot"
    ircBot.getChannels shouldEqual Array[String]("#tph")
    ircBot.getServer shouldEqual "irc.freenode.net"
  }

  "TPH GameState" should "Detect and filter changes in hearthstone's output.log into actionLog.txt" in {





  }




}
