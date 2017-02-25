package RegressionTests

import java.io._

import com.typesafe.config.ConfigFactory
import org.jibble.pircbot.PircBot
import org.scalatest.{Matchers, FlatSpec}
import tph.{IRCBot, LogFileReader}

import scala.collection.mutable.ListBuffer

/**
  * Created by Harambe on 2/22/2017.
  */
class StartToEndTests extends FlatSpec with Matchers {

  "TPH" should "Initialize LogFileReader" in {

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
    logFileWriter.println("[Power] PowerProcessor.DoTaskListForCard() - unhandled BlockType PLAY for sourceEntity [name=Enemy Card 3 id=23 zone=PLAY zonePos=5 cardId=some player=2]")
    logFileWriter.flush()

    Thread.sleep(1000)


    val actualStringList = Stream.continually(actionFileReader.readLine()).takeWhile(_ != null).toList

    actualStringList.size shouldEqual 2
    actualStringList(0) shouldEqual "[Zone] ZoneChangeList.ProcessChanges() - processing index=55 change=powerTask=[power=[type=TAG_CHANGE entity=[id=55 cardId=some name=some] tag=ZONE_POSITION value=55] complete=False] entity=[name=Friendly Hand 7 id=7 zone=HAND zonePos=0 cardId=some player=1] srcZoneTag=INVALID srcPos= dstZoneTag=INVALID dstPos=7"
    actualStringList(1) shouldEqual "[Power] PowerProcessor.DoTaskListForCard() - unhandled BlockType PLAY for sourceEntity [name=Enemy Card 3 id=23 zone=PLAY zonePos=5 cardId=some player=2]"
  }

  it should "Initialize ircBot" ignore {
    val ircBot = new IRCBot()
    val config = ConfigFactory.load()
    val testBot = new PircBot {
      val config = ConfigFactory.load()
      val hostName = config.getString("tph.irc.host")
      val channel = config.getString("tph.irc.channel")
      val nickname = "TPHTesterBot1"

      setName(nickname)
      setVerbose(false)
      connect(hostName)
      joinChannel(channel)}

    testBot.sendMessage("#tph", "!greetings")
    testBot.sendMessage("#tph", "!wow")

    val reader = new BufferedReader(new FileReader(config.getString("tph.voteLog.path")))
    val actualStringList = Stream.continually(reader.readLine()).takeWhile(_ != null).toList
    actualStringList.size shouldEqual 2
    actualStringList(0) shouldEqual "!greetings"
    actualStringList(1) shouldEqual "!wow"

  }

  "TPH GameState" should "Detect and filter changes in hearthstone's output.log into actionLog.txt" in {





  }




}
