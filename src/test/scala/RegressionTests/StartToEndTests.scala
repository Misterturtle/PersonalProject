package RegressionTests

import java.io.{FileReader, BufferedReader, FileWriter, PrintWriter}

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
    val logFileWriter = new PrintWriter(new FileWriter(config.getString("tph.outputLog.path")))
    val actionFileReader = new BufferedReader(new FileReader(config.getString("tph.actionLog.path")))


    logFileWriter.println("This is a test line")
    logFileWriter.flush()

    val logFileReader = new LogFileReader()
    logFileReader.poll()

    logFileWriter.println("This should erase the previous file")
    logFileWriter.println("This mocks hearthstones output_log.txt strings")
    logFileWriter.println("actionFileWriter should write known commands to actionLog")
    logFileWriter.println("some id=55 local=False [name=Friendly Minion 7 id=7 zone=HAND zonePos=7 cardId=some player=1] pos from 55 -> 55")
    logFileWriter.println("[Power] PowerProcessor.DoTaskListForCard() - unhandled BlockType PLAY for sourceEntity [name=Enemy Card 3 id=23 zone=PLAY zonePos=5 cardId=some player=2]")
    logFileWriter.flush()

    Thread.sleep(1000)


    val actualStringList = Stream.continually(actionFileReader.readLine()).takeWhile(_ != null).toList

    actualStringList.size shouldEqual 2
    actualStringList(0) shouldEqual "some id=55 local=False [name=Friendly Minion 7 id=7 zone=HAND zonePos=7 cardId=some player=1] pos from 55 -> 55"
    actualStringList(1) shouldEqual "[Power] PowerProcessor.DoTaskListForCard() - unhandled BlockType PLAY for sourceEntity [name=Enemy Card 3 id=23 zone=PLAY zonePos=5 cardId=some player=2]"
  }

  it should "Initialize ircBot" in {

    val ircBot = new IRCBot()
    val testBot = new PircBot { }







  }


  "TPH GameState" should "Detect and filter changes in hearthstone's output.log into actionLog.txt" in {





  }




}
