package UnitTests

import java.io.{FileWriter, PrintWriter, File}

import org.scalatest.{FlatSpec, Matchers}
import tph.LogParser

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






}
