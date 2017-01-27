package tph


import java.io.{File, InputStream}
import java.util.concurrent._

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem}
import akka.event.LoggingReceive
import akka.pattern.ask
import akka.util.Timeout
import autoitx4java.AutoItX
import com.jacob.com.LibraryLoader
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.commons.io.{FileUtils, IOUtils}
//import tph.Controller.ChangeMenu
import tph.research.PixelDataBase._

import scala.collection.mutable
import scala.concurrent.Await
import scala.concurrent.duration._
import tph.Constants.ActionVoteCodes._
import tph.Constants.EmojiVoteCodes._
import tph.Constants.MenuVoteCodes._


class Hearthstone(theBrain: TheBrain) {
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
  val clicker = new MouseClicker()
  val config = ConfigFactory.load()


  def Start(): Unit = {
    initialize()
  }

  def GameOver() = {
    TimeUnit.SECONDS.sleep(15)
    //Click anywhere
    clicker.Click(emoteMenu.clickLocations("greetings").position)
    TimeUnit.SECONDS.sleep(1)
    clicker.Click(emoteMenu.clickLocations("greetings").position)
    TimeUnit.SECONDS.sleep(1)
    clicker.Click(emoteMenu.clickLocations("greetings").position)
    TimeUnit.SECONDS.sleep(1)
    clicker.Click(emoteMenu.clickLocations("greetings").position)
  }


  def ExecuteActionVote(vote: ActionVote): Unit = {

    case Discover(option: Int) =>
      clicker.Click(inGame.clickLocations("discoverCard" + transformMap(option)).position)

    case CardPlayWithFriendlyOption(card: Int, boardTarget: Int) =>
      val currentGameStatus = GetGameStatus()
      val cardsInHand = currentGameStatus(0).hand.length
      val cardsOnBoard = currentGameStatus(0).board.length
      clicker.Click(inGame.clickLocations("card" + transformMap(card) + "Of" + transformMap(cardsInHand)).position)
      TimeUnit.MILLISECONDS.sleep(50)
      clicker.Click(inGame.clickLocations("myBoardFarRight").position)
      TimeUnit.SECONDS.sleep(1)
      clicker.Click(inGame.clickLocations("myBoard" + transformMap(boardTarget) + "Of" + transformMap(cardsOnBoard + 1)).position)

    case CardPlayWithFriendlyFaceOption(card: Int) =>
      val currentGameStatus = GetGameStatus()
      val cardsInHand = currentGameStatus(0).hand.length
      clicker.Click(inGame.clickLocations("card" + transformMap(card) + "Of" + transformMap(cardsInHand)).position)
      TimeUnit.MILLISECONDS.sleep(50)
      clicker.Click(inGame.clickLocations("myBoardFarRight").position)
      TimeUnit.SECONDS.sleep(1)
      clicker.Click(inGame.clickLocations("myFace").position)

    case CardPlayWithEnemyOption(card: Int, boardTarget: Int) =>
      val currentGameStatus = GetGameStatus()
      val cardsInHand = currentGameStatus(0).hand.length
      val cardsOnBoard = currentGameStatus(1).board.length
      clicker.Click(inGame.clickLocations("card" + transformMap(card) + "Of" + transformMap(cardsInHand)).position)
      TimeUnit.MILLISECONDS.sleep(50)
      clicker.Click(inGame.clickLocations("myBoardFarRight").position)
      TimeUnit.SECONDS.sleep(1)
      clicker.Click(inGame.clickLocations("hisBoard" + transformMap(boardTarget) + "Of" + transformMap(cardsOnBoard + 1)).position)


    case CardPlayWithEnemyFaceOption(card: Int) =>
      val currentGameStatus = GetGameStatus()
      val cardsInHand = currentGameStatus(0).hand.length
      clicker.Click(inGame.clickLocations("card" + transformMap(card) + "Of" + transformMap(cardsInHand)).position)
      TimeUnit.MILLISECONDS.sleep(50)
      clicker.Click(inGame.clickLocations("myBoardFarRight").position)
      TimeUnit.SECONDS.sleep(1)
      clicker.Click(inGame.clickLocations("hisFace").position)

    //Normal Turn Play Type
    case CardPlay(card: Int) =>
      val currentGameStatus = GetGameStatus()
      val cardsInHand = currentGameStatus(0).hand.length
      clicker.Click(inGame.clickLocations("card" + transformMap(card) + "Of" + transformMap(cardsInHand)).position)
      TimeUnit.MILLISECONDS.sleep(50)
      clicker.Click(inGame.clickLocations("myBoardFarRight").position)

    case CardPlayWithPosition(card: Int, position: Int) =>
      val currentGameStatus = GetGameStatus()
      val cardsInHand = currentGameStatus(0).hand.length
      val cardsOnBoard = currentGameStatus(0).board.length
      clicker.Click(inGame.clickLocations("card" + transformMap(card) + "Of" + transformMap(cardsInHand)).position)
      TimeUnit.MILLISECONDS.sleep(100)
      clicker.Click(inGame.clickLocations("myBoard" + transformMap(position) + "Of" + transformMap(cardsOnBoard + 1)).position)

    case CardPlayWithFriendlyBoardTarget(card: Int, target: Int) =>
      val currentGameStatus = GetGameStatus()
      val cardsInHand = currentGameStatus(0).hand.length
      val cardsOnBoard = currentGameStatus(0).board.length
      clicker.Click(inGame.clickLocations("card" + transformMap(card) + "Of" + transformMap(cardsInHand)).position)
      TimeUnit.MILLISECONDS.sleep(100)
      clicker.Click(inGame.clickLocations("myBoard" + transformMap(target) + "Of" + transformMap(cardsOnBoard)).position)

    case CardPlayWithEnemyBoardTarget(card: Int, target: Int) =>
      val currentGameStatus = GetGameStatus()
      val cardsInHand = currentGameStatus(0).hand.length
      val cardsOnBoard = currentGameStatus(1).board.length
      clicker.Click(inGame.clickLocations("card" + transformMap(card) + "Of" + transformMap(cardsInHand)).position)
      TimeUnit.MILLISECONDS.sleep(50)
      clicker.Click(inGame.clickLocations("hisBoard" + transformMap(target) + "Of" + transformMap(cardsOnBoard)).position)

    case CardPlayWithFriendlyFaceTarget(card: Int) =>
      val currentGameStatus = GetGameStatus()
      val cardsInHand = currentGameStatus(0).hand.length
      clicker.Click(inGame.clickLocations("card" + transformMap(card) + "Of" + transformMap(cardsInHand)).position)
      TimeUnit.MILLISECONDS.sleep(100)
      clicker.Click(inGame.clickLocations("myFace").position)


    case CardPlayWithEnemyFaceTarget(card: Int) =>
      val currentGameStatus = GetGameStatus()
      val cardsInHand = currentGameStatus(0).hand.length
      clicker.Click(inGame.clickLocations("card" + transformMap(card) + "Of" + transformMap(cardsInHand)).position)
      TimeUnit.MILLISECONDS.sleep(100)
      clicker.Click(inGame.clickLocations("hisFace").position)


    case HeroPower() =>
      clicker.Click(inGame.clickLocations("heroPower").position)

    case HeroPowerWithEnemyFace() =>
      clicker.Click(inGame.clickLocations("heroPower").position)
      TimeUnit.MILLISECONDS.sleep(50)
      clicker.Click(inGame.clickLocations("hisFace").position)

    case HeroPowerWithEnemyTarget(target: Int) =>
      val currentGameStatus = GetGameStatus()
      val cardsOnBoard = currentGameStatus(1).board.length
      clicker.Click(inGame.clickLocations("heroPower").position)
      TimeUnit.MILLISECONDS.sleep(50)
      clicker.Click(inGame.clickLocations("hisBoard" + transformMap(target) + "Of" + transformMap(cardsOnBoard)).position)

    case HeroPowerWithFriendlyFace() =>
      clicker.Click(inGame.clickLocations("heroPower").position)
      TimeUnit.MILLISECONDS.sleep(50)
      clicker.Click(inGame.clickLocations("myFace").position)


    case HeroPowerWithFriendlyTarget(target: Int) =>
      val currentGameStatus = GetGameStatus()
      val cardsOnBoard = currentGameStatus(0).board.length
      clicker.Click(inGame.clickLocations("heroPower").position)
      TimeUnit.MILLISECONDS.sleep(50)
      clicker.Click(inGame.clickLocations("myBoard" + transformMap(target) + "Of" + transformMap(cardsOnBoard)).position)

    //Attack Type
    case NormalAttack(friendlyPosition: Int, enemyPosition: Int) =>
      val currentGameStatus = GetGameStatus()
      val friendlyCardsOnBoard = currentGameStatus(0).board.length
      val enemyCardsOnBoard = currentGameStatus(1).board.length
      clicker.Click(inGame.clickLocations("myBoard" + transformMap(friendlyPosition) + "Of" + transformMap(friendlyCardsOnBoard)).position)
      TimeUnit.MILLISECONDS.sleep(50)
      clicker.Click(inGame.clickLocations("hisBoard" + transformMap(enemyPosition) + "Of" + transformMap(enemyCardsOnBoard)).position)

    case FaceAttack(position: Int) =>
      val currentGameStatus = GetGameStatus()
      val enemyCardsOnBoard = currentGameStatus(1).board.length
      clicker.Click(inGame.clickLocations("myFace").position)
      TimeUnit.MILLISECONDS.sleep(50)
      clicker.Click(inGame.clickLocations("hisBoard" + transformMap(position) + "Of" + transformMap(enemyCardsOnBoard)).position)


    case NormalAttackToFace(position: Int) =>
      val currentGameStatus = GetGameStatus()
      val friendlyCardsOnBoard = currentGameStatus(0).board.length
      clicker.Click(inGame.clickLocations("myBoard" + transformMap(position) + "Of" + transformMap(friendlyCardsOnBoard)).position)
      TimeUnit.MILLISECONDS.sleep(50)
      clicker.Click(inGame.clickLocations("hisFace").position)


    case FaceAttackToFace() =>
      clicker.Click(inGame.clickLocations("myFace").position)
      TimeUnit.MILLISECONDS.sleep(50)
      clicker.Click(inGame.clickLocations("hisFace").position)

  }

  def EndTurn(): Unit = {
    clicker.Click(inGame.clickLocations("endTurn").position)
    theBrain.EndTurn()
  }


  def ExecuteEmojiVote(vote: EmojiVote): Unit = {

    vote.emojiVoteCode match {
      //Messages from ircLogic
      case Greetings() =>
        clicker.RightClick(emoteMenu.clickLocations("myFace").position)
        TimeUnit.MILLISECONDS.sleep(50)
        clicker.Click(emoteMenu.clickLocations("greetings").position)


      case Thanks() =>
        clicker.RightClick(emoteMenu.clickLocations("myFace").position)
        TimeUnit.MILLISECONDS.sleep(50)
        clicker.Click(emoteMenu.clickLocations("thanks").position)


      case WellPlayed() =>
        clicker.RightClick(emoteMenu.clickLocations("myFace").position)
        TimeUnit.MILLISECONDS.sleep(50)
        clicker.Click(emoteMenu.clickLocations("wellPlayed").position)


      case Wow() =>
        clicker.RightClick(emoteMenu.clickLocations("myFace").position)
        TimeUnit.MILLISECONDS.sleep(50)
        clicker.Click(emoteMenu.clickLocations("wow").position)


      case Oops() =>
        clicker.RightClick(emoteMenu.clickLocations("myFace").position)
        TimeUnit.MILLISECONDS.sleep(50)
        clicker.Click(emoteMenu.clickLocations("oops").position)


      case Threaten() =>
        clicker.RightClick(emoteMenu.clickLocations("myFace").position)
        TimeUnit.MILLISECONDS.sleep(50)
        clicker.Click(emoteMenu.clickLocations("threaten").position)

    }
  }

  def Concede(): Unit = {
    clicker.Click(inGame.clickLocations("gameOptions").position)
      TimeUnit.MILLISECONDS.sleep(50)
    clicker.Click(inGame.clickLocations("concedeButton").position)
  }


  def MulliganVote(first: Boolean, second: Boolean, third: Boolean, fourth: Boolean): Unit = {

    if (first) {
      clicker.Click(inGame.clickLocations("mulligan" + transformMap(1) + "Of" + transformMap(theBrain.inMulligan.mulliganOptions)).position)
    }
    TimeUnit.MILLISECONDS.sleep(50)
    if (second) {
      clicker.Click(inGame.clickLocations("mulligan" + transformMap(2) + "Of" + transformMap(theBrain.inMulligan.mulliganOptions)).position)
    }
    TimeUnit.MILLISECONDS.sleep(50)
    if (third) {
      clicker.Click(inGame.clickLocations("mulligan" + transformMap(3) + "Of" + transformMap(theBrain.inMulligan.mulliganOptions)).position)
    }
    TimeUnit.MILLISECONDS.sleep(50)


    if (theBrain.inMulligan.mulliganOptions >= 3 && fourth) {
      clicker.Click(inGame.clickLocations("mulligan" + transformMap(4) + "Of" + transformMap(theBrain.inMulligan.mulliganOptions)).position)
    }

    TimeUnit.MILLISECONDS.sleep(50)
    clicker.Click(inGame.clickLocations("mulliganConfirm").position)
  }

    //-------------MENU CASES----------------
    //Multi Menu
    def Back(menu: String): Unit = {

      if (menu == "playMenu") {
        clicker.Click(playMenu.clickLocations("backButton").position)
        theBrain.ChangeMenu("mainMenu")
      }

      //Most likely removing due to only subscribers accessing collections
      //      if(menu == "collectionMenu"){
      //        clicker.Click(playMenu.clickLocations("backButton").position)
      //        ChangeMenu("collectionMenu", previousMenu)
      //      }
      if (menu == "questMenu") {
        clicker.Click(questMenu.clickLocations("backButton").position)
        theBrain.ChangeMenu("mainMenu")
      }
    }

  def Play(menu: String): Unit = {
    if (menu == "mainMenu") {
        clicker.Click(mainMenu.clickLocations("play").position)
      theBrain.ChangeMenu("playMenu")
      }

    if (menu == "playMenu") {
        clicker.Click(playMenu.clickLocations("play").position)
      theBrain.ChangeMenu("inGame")
      theBrain.StartGame()
      }
  }


      // Most likely removing due to only subscribers accessing colletions
//
  //    def  Collection(menu:String) :Unit ={
//      if(menu == "mainMenu"){
//        clicker.Click(mainMenu.clickLocations("myCollection").position)
//        ChangeMenu("mainMenu", "playMenu")
//      }
//
//      if(menu == "playMenu") {
//        clicker.Click(playMenu.clickLocations("myCollection").position)
//        ChangeMenu("playMenu", "inGame")
//      }




    //Main Menu


  def Shop(): Unit = {
      clicker.Click(mainMenu.clickLocations("shop").position)
    theBrain.ChangeMenu("shopMenu")
  }

  def OpenPacks(): Unit = {
      clicker.Click(mainMenu.clickLocations(OPEN_PACKS).position)
    theBrain.ChangeMenu("openPackMenu")
  }

  def QuestLog(): Unit = {
      clicker.Click(mainMenu.clickLocations(QUEST_LOG).position)
    theBrain.ChangeMenu("questMenu")
  }



      //Play Menu
      def Casual(): Unit = {
        clicker.Click(playMenu.clickLocations(CASUAL).position)
      }

  def Ranked(): Unit = {
    clicker.Click(playMenu.clickLocations(RANKED).position)
  }

  def Deck(deckNumber: Int): Unit = {
        val transMap = mutable.Map[Int, String](1->"first", 2->"second",3->"third",4->"fourth",5->"fifth",6->"sixth",7->"seventh",8->"eighth",9->"ninth")
    clicker.Click(playMenu.clickLocations(transMap(deckNumber) + "Deck").position)
  }

  def FirstPage(): Unit = {
    clicker.Click(playMenu.clickLocations(PREVIOUS_PAGE).position)
  }

  def SecondPage(): Unit = {
    clicker.Click(playMenu.clickLocations(NEXT_PAGE).position)
  }



    //Collection Menu


    //Quest Menu
    def Quest(number: Int): Unit = {
      clicker.Click(questMenu.clickLocations("quest" + transformMap(number)).position)
    }





  def initialize(): Unit = {
    if (!ax.winExists(TITLE))
      throw new RuntimeException("Hearthstone is not running")

    ax.winMove(TITLE, "", 0, 0, 1366, 768)

  }

  def GetGameStatus(): Array[FrozenPlayer] = {
    val frozenGameStatus = theBrain.GetGameStatus()

    return frozenGameStatus.GetFrozenPlayers()


  }


}
