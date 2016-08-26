package tph

//I believe it creates a new scraper and clicker in the class arguments
//Checks if hearth is running and adjusts screen to specific screen location
//Not sure if I agree with hard coding player and opponent


import java.util.concurrent._

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem}
import autoitx4java.AutoItX
import com.jacob.com.LibraryLoader
import com.typesafe.config.{Config, ConfigFactory}
import tph.IrcMessages._
import tph.research.PixelDataBase._


class Hearthstone(system: ActorSystem, controller: ActorRef, config: Config = ConfigFactory.load(), scraper: ScreenScraper, clicker: MouseClicker) extends Actor with ActorLogging {
  //Need help debugging this
  val TITLE = "Hearthstone"
  val libFile = ClassLoader.getSystemClassLoader.getResource("jacob-1.18-x64.dll").getFile
  System.setProperty(LibraryLoader.JACOB_DLL_PATH, libFile)
  val ax = new AutoItX()

  def receive = {

    case "Start" => initialize()



    //Messages from ircLogic
    case (Greetings()) =>
      clicker.RightClick(emoteMenu.clickLocations("myFace").position)
      TimeUnit.MILLISECONDS.sleep(100)
      clicker.Click(emoteMenu.clickLocations("greetings").position)

    case (Thanks()) =>
      clicker.RightClick(emoteMenu.clickLocations("myFace").position)
      TimeUnit.MILLISECONDS.sleep(100)
      clicker.Click(emoteMenu.clickLocations("thanks").position)

    case (WellPlayed()) =>
      clicker.RightClick(emoteMenu.clickLocations("myFace").position)
      TimeUnit.MILLISECONDS.sleep(100)
      clicker.Click(emoteMenu.clickLocations("wellPlayed").position)

    case (Wow()) =>
      clicker.RightClick(emoteMenu.clickLocations("myFace").position)
      TimeUnit.MILLISECONDS.sleep(100)
      clicker.Click(emoteMenu.clickLocations("wow").position)

    case (Oops()) =>
      clicker.RightClick(emoteMenu.clickLocations("myFace").position)
      TimeUnit.MILLISECONDS.sleep(100)
      clicker.Click(emoteMenu.clickLocations("oops").position)

    case (Threaten()) =>
      clicker.RightClick(emoteMenu.clickLocations("myFace").position)
      TimeUnit.MILLISECONDS.sleep(100)
      clicker.Click(emoteMenu.clickLocations("threaten").position)

    case (Concede(vote)) =>

    case DiscoverOptions(option: Int) =>

    case (Discover(option: Int)) =>

    case (CardPlayWithFriendlyOption(card: Int, boardTarget: Int)) =>

    case (CardPlayWithFriendlyFaceOption(card: Int)) =>

    case (CardPlayWithEnemyOption(card: Int, boardTarget: Int)) =>

    case (CardPlayWithEnemyFaceOption(card: Int)) =>


    //Normal Turn Play Type
    case (CardPlay(card: Int)) =>

    case (CardPlayWithPosition(card: Int, position: Int)) =>

    case (CardPlayWithFriendlyBoardTarget(card: Int, target: Int)) =>

    case (CardPlayWithEnemyBoardTarget(card: Int, target: Int)) =>

    case (CardPlayWithFriendlyFaceTarget(card: Int)) =>

    case (CardPlayWIthEnemyFaceTarget(card: Int)) =>

    case (EndTurn()) =>

    case (HeroPower()) =>

    case (HeroPowerWithEnemyFace()) =>
    case (HeroPowerWithEnemyTarget(target: Int)) =>
    case (HeroPowerWithFriendlyFace()) =>
    case (HeroPowerWithFriendlyTarget(target: Int)) =>


    //Attack Type
    case (NormalAttack(friendlyPosition: Int, enemyPosition: Int)) =>

    case (FaceAttack(position: Int)) =>

    case (NormalAttackToFace(position: Int)) =>

    case (FaceAttackToFace()) =>

    //Always Type
    case (Wait()) =>

    case (Hurry()) =>

  }


  def initialize(): Unit = {
    if (!ax.winExists(TITLE))
      throw new RuntimeException("Hearthstone is not running")
    ax.winMove(TITLE, "", 0, 0, 1884, 1080)
    scraper.GetScreen()
    ax.mouseGetPosX()

  }


  //def clickEndTurn(): Unit =  ax.mouseClick("main", 1507, 495 )
}
