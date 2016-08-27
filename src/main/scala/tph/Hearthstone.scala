package tph


import java.io.{File, InputStream}
import java.util.concurrent._

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem}
import akka.pattern.ask
import akka.util.Timeout
import autoitx4java.AutoItX
import com.jacob.com.LibraryLoader
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.commons.io.{FileUtils, IOUtils}
import tph.IrcMessages._
import tph.research.PixelDataBase._

import scala.concurrent.Await
import scala.concurrent.duration._




class Hearthstone(system: ActorSystem, controller: ActorRef, config: Config = ConfigFactory.load(), scraper: ScreenScraper, clicker: MouseClicker) extends Actor with ActorLogging {
  val TITLE = "Hearthstone"
  val transformMap = Map[Int, String](1 -> "One", 2 -> "Two", 3 -> "Three", 4 -> "Four", 5 -> "Five", 6 -> "Six", 7 -> "Seven", 8 -> "Eight", 9 -> "Nine", 10 -> "Ten")


  //Load Jacob Dll
  val input: InputStream = getClass.getResourceAsStream("/jacob-1.18-x64.dll")
  val fileOut = new File(System.getProperty("java.io.tmpdir") + "/jacob/Hearthstone/jacob-1.18-x64.dll")
  println("Writing dll to: " + fileOut.getAbsolutePath())
  val out = FileUtils.openOutputStream(fileOut)
  IOUtils.copy(input, out)
  input.close()
  out.close()
  System.setProperty(LibraryLoader.JACOB_DLL_PATH, fileOut.getPath)
  val ax = new AutoItX()


  def receive = {

    case "Start" => initialize()



    //Messages from ircLogic
    case (Greetings()) =>
      clicker.RightClick(emoteMenu.clickLocations("myFace").position)
      TimeUnit.MILLISECONDS.sleep(50)
      clicker.Click(emoteMenu.clickLocations("greetings").position)

    case (Thanks()) =>
      clicker.RightClick(emoteMenu.clickLocations("myFace").position)
      TimeUnit.MILLISECONDS.sleep(50)
      clicker.Click(emoteMenu.clickLocations("thanks").position)

    case (WellPlayed()) =>
      clicker.RightClick(emoteMenu.clickLocations("myFace").position)
      TimeUnit.MILLISECONDS.sleep(50)
      clicker.Click(emoteMenu.clickLocations("wellPlayed").position)

    case (Wow()) =>
      clicker.RightClick(emoteMenu.clickLocations("myFace").position)
      TimeUnit.MILLISECONDS.sleep(50)
      clicker.Click(emoteMenu.clickLocations("wow").position)

    case (Oops()) =>
      clicker.RightClick(emoteMenu.clickLocations("myFace").position)
      TimeUnit.MILLISECONDS.sleep(50)
      clicker.Click(emoteMenu.clickLocations("oops").position)

    case (Threaten()) =>
      clicker.RightClick(emoteMenu.clickLocations("myFace").position)
      TimeUnit.MILLISECONDS.sleep(50)
      clicker.Click(emoteMenu.clickLocations("threaten").position)

    case (Concede(vote)) =>
      clicker.Click(inGame.clickLocations("gameOptions").position)
      TimeUnit.MILLISECONDS.sleep(50)
      clicker.Click(inGame.clickLocations("concede").position)

    case (Discover(option: Int)) =>
      clicker.Click(inGame.clickLocations("discoverCard" + transformMap(option)).position)

    case (CardPlayWithFriendlyOption(card: Int, boardTarget: Int)) =>
      val currentGameStatus = GetGameStatus()
      val cardsInHand = currentGameStatus(0).hand.length
      val cardsOnBoard = currentGameStatus(0).board.length
      clicker.Click(inGame.clickLocations("card" + transformMap(card) + "Of" + transformMap(cardsInHand)).position)
      TimeUnit.MILLISECONDS.sleep(50)
      clicker.Click(inGame.clickLocations("myBoardFarRight").position)
      TimeUnit.SECONDS.sleep(1)
      clicker.Click(inGame.clickLocations("myBoard" + transformMap(boardTarget) + "Of" + transformMap(cardsOnBoard + 1)).position)

    case (CardPlayWithFriendlyFaceOption(card: Int)) =>
      val currentGameStatus = GetGameStatus()
      val cardsInHand = currentGameStatus(0).hand.length
      clicker.Click(inGame.clickLocations("card" + transformMap(card) + "Of" + transformMap(cardsInHand)).position)
      TimeUnit.MILLISECONDS.sleep(50)
      clicker.Click(inGame.clickLocations("myBoardFarRight").position)
      TimeUnit.SECONDS.sleep(1)
      clicker.Click(inGame.clickLocations("myFace").position)

    case (CardPlayWithEnemyOption(card: Int, boardTarget: Int)) =>
      val currentGameStatus = GetGameStatus()
      val cardsInHand = currentGameStatus(0).hand.length
      val cardsOnBoard = currentGameStatus(1).board.length
      clicker.Click(inGame.clickLocations("card" + transformMap(card) + "Of" + transformMap(cardsInHand)).position)
      TimeUnit.MILLISECONDS.sleep(50)
      clicker.Click(inGame.clickLocations("myBoardFarRight").position)
      TimeUnit.SECONDS.sleep(1)
      clicker.Click(inGame.clickLocations("hisBoard" + transformMap(boardTarget) + "Of" + transformMap(cardsOnBoard + 1)).position)


    case (CardPlayWithEnemyFaceOption(card: Int)) =>
      val currentGameStatus = GetGameStatus()
      val cardsInHand = currentGameStatus(0).hand.length
      clicker.Click(inGame.clickLocations("card" + transformMap(card) + "Of" + transformMap(cardsInHand)).position)
      TimeUnit.MILLISECONDS.sleep(50)
      clicker.Click(inGame.clickLocations("myBoardFarRight").position)
      TimeUnit.SECONDS.sleep(1)
      clicker.Click(inGame.clickLocations("hisFace").position)

    //Normal Turn Play Type
    case (CardPlay(card: Int)) =>
      val currentGameStatus = GetGameStatus()
      val cardsInHand = currentGameStatus(0).hand.length
      clicker.Click(inGame.clickLocations("card" + transformMap(card) + "Of" + transformMap(cardsInHand)).position)
      TimeUnit.MILLISECONDS.sleep(50)
      clicker.Click(inGame.clickLocations("myBoardFarRight").position)

    case (CardPlayWithPosition(card: Int, position: Int)) =>
      val currentGameStatus = GetGameStatus()
      val cardsInHand = currentGameStatus(0).hand.length
      val cardsOnBoard = currentGameStatus(0).board.length
      clicker.Click(inGame.clickLocations("card" + transformMap(card) + "Of" + transformMap(cardsInHand)).position)
      TimeUnit.MILLISECONDS.sleep(100)
      clicker.Click(inGame.clickLocations("myBoard" + transformMap(position) + "Of" + transformMap(cardsOnBoard + 1)).position)

    case (CardPlayWithFriendlyBoardTarget(card: Int, target: Int)) =>
      val currentGameStatus = GetGameStatus()
      val cardsInHand = currentGameStatus(0).hand.length
      val cardsOnBoard = currentGameStatus(0).board.length
      clicker.Click(inGame.clickLocations("card" + transformMap(card) + "Of" + transformMap(cardsInHand)).position)
      TimeUnit.MILLISECONDS.sleep(100)
      clicker.Click(inGame.clickLocations("myBoard" + transformMap(target) + "Of" + transformMap(cardsOnBoard)).position)

    case (CardPlayWithEnemyBoardTarget(card: Int, target: Int)) =>
      val currentGameStatus = GetGameStatus()
      val cardsInHand = currentGameStatus(0).hand.length
      val cardsOnBoard = currentGameStatus(1).board.length
      clicker.Click(inGame.clickLocations("card" + transformMap(card) + "Of" + transformMap(cardsInHand)).position)
      TimeUnit.MILLISECONDS.sleep(50)
      clicker.Click(inGame.clickLocations("hisBoard" + transformMap(target) + "Of" + transformMap(cardsOnBoard)).position)

    case (CardPlayWithFriendlyFaceTarget(card: Int)) =>
      val currentGameStatus = GetGameStatus()
      val cardsInHand = currentGameStatus(0).hand.length
      clicker.Click(inGame.clickLocations("card" + transformMap(card) + "Of" + transformMap(cardsInHand)).position)
      TimeUnit.MILLISECONDS.sleep(100)
      clicker.Click(inGame.clickLocations("myFace").position)


    case (CardPlayWIthEnemyFaceTarget(card: Int)) =>
      val currentGameStatus = GetGameStatus()
      val cardsInHand = currentGameStatus(0).hand.length
      clicker.Click(inGame.clickLocations("card" + transformMap(card) + "Of" + transformMap(cardsInHand)).position)
      TimeUnit.MILLISECONDS.sleep(100)
      clicker.Click(inGame.clickLocations("hisFace").position)


    case (EndTurn()) =>
      clicker.Click(inGame.clickLocations("endTurn").position)

    case (HeroPower()) =>
      clicker.Click(inGame.clickLocations("heroPower").position)

    case (HeroPowerWithEnemyFace()) =>
      clicker.Click(inGame.clickLocations("heroPower").position)
      TimeUnit.MILLISECONDS.sleep(50)
      clicker.Click(inGame.clickLocations("hisFace").position)

    case (HeroPowerWithEnemyTarget(target: Int)) =>
      val currentGameStatus = GetGameStatus()
      val cardsOnBoard = currentGameStatus(1).board.length
      clicker.Click(inGame.clickLocations("heroPower").position)
      TimeUnit.MILLISECONDS.sleep(50)
      clicker.Click(inGame.clickLocations("hisBoard" + transformMap(target) + "Of" + transformMap(cardsOnBoard)).position)

    case (HeroPowerWithFriendlyFace()) =>
      clicker.Click(inGame.clickLocations("heroPower").position)
      TimeUnit.MILLISECONDS.sleep(50)
      clicker.Click(inGame.clickLocations("myFace").position)


    case (HeroPowerWithFriendlyTarget(target: Int)) =>
      val currentGameStatus = GetGameStatus()
      val cardsOnBoard = currentGameStatus(0).board.length
      clicker.Click(inGame.clickLocations("heroPower").position)
      TimeUnit.MILLISECONDS.sleep(50)
      clicker.Click(inGame.clickLocations("myBoard" + transformMap(target) + "Of" + transformMap(cardsOnBoard)).position)

    //Attack Type
    case (NormalAttack(friendlyPosition: Int, enemyPosition: Int)) =>
      val currentGameStatus = GetGameStatus()
      val friendlyCardsOnBoard = currentGameStatus(0).board.length
      val enemyCardsOnBoard = currentGameStatus(1).board.length
      clicker.Click(inGame.clickLocations("myBoard" + transformMap(friendlyPosition) + "Of" + transformMap(friendlyCardsOnBoard)).position)
      TimeUnit.MILLISECONDS.sleep(50)
      clicker.Click(inGame.clickLocations("hisBoard" + transformMap(enemyPosition) + "Of" + transformMap(enemyCardsOnBoard)).position)

    case (FaceAttack(position: Int)) =>
      val currentGameStatus = GetGameStatus()
      val enemyCardsOnBoard = currentGameStatus(1).board.length
      clicker.Click(inGame.clickLocations("myFace").position)
      TimeUnit.MILLISECONDS.sleep(50)
      clicker.Click(inGame.clickLocations("hisBoard" + transformMap(position) + "Of" + transformMap(enemyCardsOnBoard)).position)


    case (NormalAttackToFace(position: Int)) =>
      val currentGameStatus = GetGameStatus()
      val friendlyCardsOnBoard = currentGameStatus(0).board.length
      clicker.Click(inGame.clickLocations("myBoard" + transformMap(position) + "Of" + transformMap(friendlyCardsOnBoard)).position)
      TimeUnit.MILLISECONDS.sleep(50)
      clicker.Click(inGame.clickLocations("hisFace").position)


    case (FaceAttackToFace()) =>
      clicker.Click(inGame.clickLocations("myFace").position)
      TimeUnit.MILLISECONDS.sleep(50)
      clicker.Click(inGame.clickLocations("hisFace").position)

    case (MulliganVote(vote), mulliganOptions: Int) =>
      val clicks = vote.size
      for (i <- 0 until clicks) {
        clicker.Click(inGame.clickLocations("mulligan" + transformMap(vote(i)) + "Of" + transformMap(mulliganOptions)).position)
      }
      TimeUnit.MILLISECONDS.sleep(50)
      clicker.Click(inGame.clickLocations("mulliganConfirm").position)
  }


  def initialize(): Unit = {
    if (!ax.winExists(TITLE))
      throw new RuntimeException("Hearthstone is not running")

    ax.winMove(TITLE, "", 0, 0, 1366, 768)

  }

  def GetGameStatus(): Array[Player] = {
    implicit val timeout = Timeout(30 seconds)
    val future = controller ? "GetGameStatus"
    val result = Await.result(future, timeout.duration)
    if (result == None) {
      GetGameStatus()
    }
    else return result.asInstanceOf[Array[Player]]
  }


  //def clickEndTurn(): Unit =  ax.mouseClick("main", 1507, 495 )
}
