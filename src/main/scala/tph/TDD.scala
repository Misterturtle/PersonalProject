package tph

import java.io._

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import org.apache.commons.io.FileUtils
import tph.Main._

import scala.concurrent.Await
import scala.concurrent.duration._

object TDDFileReader{
  val FULL_TEST = "FULL_TEST"
  val SINGLE_TEST = """SingleTest_(.+)""".r

  //Bug List:
  //Compares files before PrintState is completed (Aka: It compares an older result file)
  //Keep an eye on incorrect pass/fail results (Can't reproduce the bug mentioned above)
}



//DisplayLogs.scala calls for Test Mode
//TDD asks LogFileReader to analyze a specific file
//GameStatus outputs a log file with his hand and my hand
//TDD waits until LogFileReader is idle
//TDD compares that log file with a preset debug log file with an expected result
//



class TDD (system: ActorSystem) extends Actor with akka.actor.ActorLogging {

  import TDDFileReader._


  def receive = {
    case FULL_TEST => FullTest()
    case SINGLE_TEST =>
      val pattern = """SingleTest_(.+)""".r
      var name = new String
      pattern.findAllIn(SINGLE_TEST.toString()).matchData foreach {
        m => name = m.group(1)
      }
      SingleTest(new File("C:\\Users\\RC\\Documents\\GitHubRepository\\TwitchPlaysHearthstone\\debugsituations\\" + name), system.actorOf(Props(new LogFileReader(system, new File("C:\\Users\\RC\\Documents\\GitHubRepository\\TwitchPlaysHearthstone\\debugsituations\\" + name), gameStatus, controller)), "testFileReader"))
  }


  def WaitForIdle(actor: ActorRef) {

    implicit val timeout = Timeout(10 seconds)
    val future = actor ? "IsComplete"
    val result = Await.result(future, timeout.duration)
    if (result == false) {
      //You really need to change this.
      WaitForIdle(actor)
    }
  }

  def CompareResults(fileName: String): Boolean = {
    val testFile = new File("C:\\Users\\RC\\Documents\\GitHubRepository\\TwitchPlaysHearthstone\\debugsituations\\Results\\" + fileName)
    val verifiedFile = new File("C:\\Users\\RC\\Documents\\GitHubRepository\\TwitchPlaysHearthstone\\debugsituations\\Verified\\" + fileName)
    val comparedResult = FileUtils.contentEquals(testFile,verifiedFile)

    return comparedResult
  }


  def SingleTest(file: File, testFileReader:ActorRef): Unit = {

    val fileName = file.getName()

    testFileReader ! "LogFileReader.start"

    WaitForIdle(testFileReader)
    val results:Boolean = CompareResults(fileName)

    if (results == true) {
      log.warning(fileName + " Passed")
      testFileReader ! "CLEAR_STATUS"
      testFileReader ! PoisonPill
      return true
    }
    if (results == false){
      log.warning(fileName + " Failed")
    testFileReader ! "CLEAR_STATUS"
    testFileReader ! PoisonPill
    return false
  }
  }

  def FullTest(): Unit = {
    def getListOfFiles(dir: File, extensions: List[String]): List[File] = {
      dir.listFiles.filter(_.isFile).toList.filter { file =>
        extensions.exists(file.getName.endsWith(_))
      }
    }
    val debugDirectory = new File("C:\\Users\\RC\\Documents\\GitHubRepository\\TwitchPlaysHearthstone\\debugsituations")
    val situations: List[File] = getListOfFiles(debugDirectory,List("txt"))
    val testFileReaders = new Array[ActorRef](situations.length)

    for (a <- 0 until situations.length) {
      testFileReaders(a) = system.actorOf(Props(new LogFileReader(system, situations(a), gameStatus, controller)), "testFileReader" + a)
      SingleTest(situations(a),testFileReaders(a))

    }
  }
}