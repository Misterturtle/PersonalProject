package tph

/**
  * Created by RC on 8/16/2016.
  */

import akka.actor.ActorRef
import com.typesafe.scalalogging.LazyLogging
import org.jibble.pircbot.PircBot


object IrcBot {
  val NICKNAME = "TPHBot"
  var currentMenu = "mainMenu"
  var previousMenu = ""

  trait Message

  //Emote Type
  val GREETINGS = "!greetings"
  val THANKS = "!thanks"
  val WELL_PLAYED = "!well played"
  val WOW = "!wow"
  val OOPS = "!oops"
  val THREATEN = "!threaten"

  //In Game Always Type
  val WAIT = "!wait"
  val HURRY = "!hurry"
  val CONCEDE = "!concede"
  val END_TURN = "!end turn"
  val BIND = "!bind"
  val FUTURE = "!future"


  //Parsing
  val ONE_PART_COMMAND =
    """!(.+)""".r
  val TWO_PART_COMMAND = """!(.+), (.+)""".r
  val THREE_PART_COMMAND = """!(.+), (.+), (.+)""".r
  val PLAY_COMMAND = """play (\d+)""".r
  val ATTACK_COMMAND = """att (.+)""".r
  val MY_REGEX_NUMBER = """my (\d+)""".r
  val HIS_REGEX_NUMBER = """his (\d+)""".r
  val SPOT_COMMAND = """spot (\d+)""".r
  val TARGET_COMMAND = """target (.+)""".r
  val BATTLECRY_COMMAND = """battlecry (.+)""".r
  val HERO_POWER_COMMAND = """hero power (.+)""".r
  val DISCOVER_COMMAND = """discover (\d+)""".r
  val MULLIGAN = """!mulligan (.+)""".r

  //Main Menu
  val PLAY = "!play"
  val COLLECTION = "!collection"
  val OPEN_PACKS = "!open packs"
  val SHOP = "!shop"
  val QUEST_LOG = "!quest log"

  //Play Menu
  val BACK = "!back"
  val CASUAL = "!casual"
  val RANKED = "!ranked"
  val DECK = """!deck (\d+)""".r
  val FIRST_PAGE = "!first page"
  val SECOND_PAGE = "!second page"

  //Quest Menu
  val QUEST =
    """!quest (\d+)""".r

  //All of CollectionMenu will be automated at subscribers request


}


object IrcMessages {

  case class ChangeReaderFile(fileName: String)

  case class CommandVote(builtCommand: Command)

  case class ChangeMenu(previousMenu: String, changeToMenu: String)


  //Discover Type
  case class Discover(option: Int)

  // Battlecry Option Type
  case class CardPlayWithFriendlyOption(card: Int, boardTarget: Int)

  case class CardPlayWithFriendlyFaceOption(card: Int)

  case class CardPlayWithEnemyOption(card: Int, boardTarget: Int)

  case class CardPlayWithEnemyFaceOption(card: Int)

  //Battlecry Option with Position Type
  case class CardPlayWithFriendlyOptionWithPosition(card: Int, target: Int, position: Int)

  case class CardPlayWithFriendlyFaceOptionWithPosition(card: Int, position: Int)

  case class CardPlayWithEnemyOptionWithPosition(card: Int, target: Int, position: Int)

  case class CardPlayWithEnemyFaceOptionWithPosition(card: Int, position: Int)

  //Normal Turn Play Type
  case class CardPlay(card: Int)

  case class CardPlayWithPosition(card: Int, position: Int)

  case class CardPlayWithFriendlyBoardTarget(card: Int, target: Int)

  case class CardPlayWithEnemyBoardTarget(card: Int, target: Int)

  case class CardPlayWithFriendlyFaceTarget(card: Int)

  case class CardPlayWithEnemyFaceTarget(card: Int)

  case class HeroPower()

  case class HeroPowerWithFriendlyTarget(target: Int)

  case class HeroPowerWithEnemyTarget(target: Int)

  case class HeroPowerWithFriendlyFace()

  case class HeroPowerWithEnemyFace()


  //Attack Type
  case class NormalAttack(friendlyPosition: Int, enemyPosition: Int)

  case class FaceAttack(position: Int)

  case class NormalAttackToFace(position: Int)

  case class FaceAttackToFace()

  //Emote Type
  case class Greetings()

  case class Thanks()

  case class WellPlayed()

  case class Wow()

  case class Oops()

  case class Threaten()

  //Misc Type
  case class Wait()

  case class Hurry()

  case class Concede()

  case class EndTurn()

  case class Bind()

  case class Future()

  case class Bound()

  case class FutureBound()

  case class Break()

  case class MulliganVote(vote: Array[Int])

  //MultiMenu
  case class Back(menu: String)

  case class Play(menu: String)

  case class Collection(menu: String)

  //Main Menu

  case class Shop()

  case class OpenPacks()

  case class QuestLog()

  //Play Menu

  case class Casual()

  case class Ranked()

  case class Deck(deckNumber: Int)

  case class FirstPage()

  case class SecondPage()

  //Quest Menu

  case class Quest(number: Int)

}


import tph.IrcBot._
import tph.IrcMessages._

class IrcBot(hostName: String, channel: String, watcher: ActorRef, nickname: String = IrcBot.NICKNAME) extends PircBot with LazyLogging {


//  setName(nickname)
//  logger.debug(s"nickname = $nickname")
//  setVerbose(false)
//  connect(hostName)
//  logger.debug(s"connected to $hostName")
//  joinChannel(channel)
//  logger.debug(s"joined $channel")


  override def onMessage(channel: String, sender: String, login: String, hostName: String, message: String): Unit = {
    logger.debug(s"IRC message: sender=$sender, login=$login message=$message")
    message.toLowerCase match {

      case x =>


        //Main Menu Type
        if (currentMenu == "mainMenu") {
          x match {
            case PLAY =>
              watcher !(Play("mainMenu"), sender)
            case SHOP =>
              watcher !(Shop(), sender)
            case OPEN_PACKS =>
              watcher !(OpenPacks(), sender)
            case COLLECTION =>
              watcher !(Collection("mainMenu"), sender)
            case QUEST_LOG =>
              watcher !(QuestLog(), sender)
            case _ =>



          }
        }


        //Play Menu Type
        if (currentMenu == "playMenu") {
          x match {
            case CASUAL =>
              watcher !(Casual(), sender)
            case RANKED =>
              watcher !(Ranked(), sender)
            case PLAY =>
              watcher !(Play("playMenu"), sender)
            case BACK =>
              watcher !(Back("playMenu"), sender)
            case DECK(deckNumber) =>
              watcher !(Deck(deckNumber.toInt), sender)
            case FIRST_PAGE =>
              watcher !(FirstPage(), sender)
            case SECOND_PAGE =>
              watcher !(SecondPage(), sender)
            case COLLECTION =>
              watcher !(Collection("playmenu"), sender)
            case _ =>
          }
        }


        //Quest Menu
        if (currentMenu == "questMenu") {
          x match {
            case QUEST(number) =>
              watcher !(Quest(number.toInt), sender)
            case BACK =>
              watcher !(Back("questMenu"), sender)
            case _ =>
          }
        }



        //Emote Type
        if (currentMenu == "inGame") {
          x match {
            case GREETINGS =>
              watcher !(Greetings(), sender)
            case THANKS =>
              watcher !(Thanks(), sender)
            case WELL_PLAYED =>
              watcher !(WellPlayed(), sender)
            case WOW =>
              watcher !(Wow(), sender)
            case OOPS =>
              watcher !(Oops(), sender)
            case THREATEN =>
              watcher !(Threaten(), sender)

            //Misc Type
            case WAIT =>
              watcher !(Wait(), sender)
            case HURRY =>
              watcher !(Hurry(), sender)
            case CONCEDE =>
              watcher !(Concede(), sender)
            case END_TURN =>
              watcher !(EndTurn(), sender)
            case BIND =>
              watcher !(Bind(), sender)
            case FUTURE =>
              watcher !(Future(), sender)

            case MULLIGAN(vote: String) =>
              watcher !(MulliganVote(ParseMulligan(vote)), sender)

            case _ =>
              val command = ParseCommand(message, sender)
              if (command.name != "")
                watcher ! CommandVote(command)
          }
        }
    }
  }


  def ParseMulligan(vote: String): Array[Int] = {
    val ONE = """(\d+)""".r
    val TWO = """(\d+), (\d+)""".r
    val THREE = """(\d+), (\d+), (\d+)""".r
    val FOUR = """(\d+), (\d+), (\d+), (\d+)""".r

    vote match {
      case FOUR(first, second, third, fourth) =>
        val list = new Array[Int](4)
        list(0) = first.toInt
        list(1) = second.toInt
        list(2) = third.toInt
        list(3) = fourth.toInt
        java.util.Arrays.sort(list)
        return list

      case THREE(first, second, third) =>
        val list = new Array[Int](3)
        list(0) = first.toInt
        list(1) = second.toInt
        list(2) = third.toInt
        java.util.Arrays.sort(list)
        return list


      case TWO(first, second) =>
        val list = new Array[Int](2)
        list(0) = first.toInt
        list(1) = second.toInt
        java.util.Arrays.sort(list)
        return list

      case ONE(first) =>
        val list = new Array[Int](1)
        list(0) = first.toInt
        java.util.Arrays.sort(list)
        return list
    }

  }




  def ParseCommand(message: String, sender: String): Command = {
    var builtCommand = new Command()
    builtCommand.sender = sender

    message match {
      case THREE_PART_COMMAND(firstCommand, secondCommand, thirdCommand) =>

        firstCommand match {
          case PLAY_COMMAND(cardNumber) =>
            builtCommand.firstName = "CardPlay"
            builtCommand.card = cardNumber.toInt
          case _ =>
        }


        secondCommand match {
          case SPOT_COMMAND(position) =>
            builtCommand.secondName = "WithPosition"
            builtCommand.spot = position.toInt
          case TARGET_COMMAND(target) =>
            target match {
              case "my face" =>
                builtCommand.secondName = "WithFriendlyFaceTarget"
              case "his face" =>
                builtCommand.secondName = "WithEnemyFaceTarget"
              case MY_REGEX_NUMBER(targetPos) =>
                builtCommand.secondName = "WithFriendlyTarget"
                builtCommand.target = targetPos.toInt
              case HIS_REGEX_NUMBER(targetPos) =>
                builtCommand.secondName = "WithEnemyTarget"
                builtCommand.target = targetPos.toInt
              case _ =>
            }
          case BATTLECRY_COMMAND(target) =>
            target match {
              case "my face" =>
                builtCommand.secondName = "WithFriendlyFaceOption"
              case "his face" =>
                builtCommand.secondName = "WithEnemyFaceOption"
              case MY_REGEX_NUMBER(targetPos) =>
                builtCommand.secondName = "WithFriendlyOption"
                builtCommand.battlecry = targetPos.toInt
              case HIS_REGEX_NUMBER(targetPos) =>
                builtCommand.secondName = "WithEnemyOption"
                builtCommand.battlecry = targetPos.toInt
              case _ =>
            }
          case _ =>
        }


        thirdCommand match {
          case SPOT_COMMAND(position) =>
            builtCommand.thirdName = "WithPosition"
            builtCommand.spot = position.toInt
          case TARGET_COMMAND(target) =>
            target match {
              case "my face" =>
                builtCommand.thirdName = "WithFriendlyFaceTarget"
              case "his face" =>
                builtCommand.thirdName = "WithEnemyFaceTarget"
              case MY_REGEX_NUMBER(targetPos) =>
                builtCommand.thirdName = "WithFriendlyTarget"
                builtCommand.target = targetPos.toInt
              case HIS_REGEX_NUMBER(targetPos) =>
                builtCommand.thirdName = "WithEnemyTarget"
                builtCommand.target = targetPos.toInt
              case _ =>
            }
          case BATTLECRY_COMMAND(target) =>
            target match {
              case "my face" =>
                builtCommand.thirdName = "WithFriendlyFaceOption"
              case "his face" =>
                builtCommand.thirdName = "WithEnemyFaceOption"
              case MY_REGEX_NUMBER(targetPos) =>
                builtCommand.thirdName = "WithFriendlyOption"
                builtCommand.battlecry = targetPos.toInt
              case HIS_REGEX_NUMBER(targetPos) =>
                builtCommand.thirdName = "WithEnemyOption"
                builtCommand.battlecry = targetPos.toInt
              case _ =>
            }
          case _ =>
        }


      case TWO_PART_COMMAND(firstCommand, secondCommand) =>

        firstCommand match {
          case PLAY_COMMAND(cardNumber) =>
            builtCommand.firstName = "CardPlay"
            builtCommand.card = cardNumber.toInt
          case ATTACK_COMMAND(attacker) =>
            attacker match {
              case MY_REGEX_NUMBER(attackerPos) =>
                builtCommand.firstName = "NormalAttack"
                builtCommand.card = attackerPos.toInt
              case "my face" =>
                builtCommand.firstName = "FaceAttack"
              case _ =>
            }
          case _ =>
        }


        secondCommand match {
          case SPOT_COMMAND(position) =>
            builtCommand.secondName = "WithPosition"
            builtCommand.spot = position.toInt
          case TARGET_COMMAND(target) =>
            target match {
              case "my face" =>
                builtCommand.secondName = "WithFriendlyFaceTarget"
              case "his face" =>
                builtCommand.secondName = "WithEnemyFaceTarget"
              case MY_REGEX_NUMBER(targetPos) =>
                builtCommand.secondName = "WithFriendlyTarget"
                builtCommand.target = targetPos.toInt
              case HIS_REGEX_NUMBER(targetPos) =>
                builtCommand.secondName = "WithEnemyTarget"
                builtCommand.target = targetPos.toInt
              case _ =>
            }
          case BATTLECRY_COMMAND(target) =>
            target match {
              case "my face" =>
                builtCommand.secondName = "WithFriendlyFaceOption"
              case "his face" =>
                builtCommand.secondName = "WithEnemyFaceOption"
              case MY_REGEX_NUMBER(targetPos) =>
                builtCommand.secondName = "WithFriendlyOption"
                builtCommand.battlecry = targetPos.toInt
              case HIS_REGEX_NUMBER(targetPos) =>
                builtCommand.secondName = "WithEnemyOption"
                builtCommand.battlecry = targetPos.toInt
              case _ =>
            }
          case _ =>
        }


      case ONE_PART_COMMAND(command) =>
        command match {
          case DISCOVER_COMMAND(option) =>
            builtCommand.name = "Discover"
            builtCommand.target = option.toInt
          case "hero power" =>
            builtCommand.name = "HeroPower"
          case HERO_POWER_COMMAND(target) =>
            builtCommand.firstName = "HeroPower"
            target match {
              case MY_REGEX_NUMBER(targetPos) =>
                builtCommand.target = targetPos.toInt
                builtCommand.secondName = "WithFriendlyTarget"
              case HIS_REGEX_NUMBER(targetPos) =>
                builtCommand.target = targetPos.toInt
                builtCommand.secondName = "WithEnemyTarget"
              case "my face" =>
                builtCommand.secondName = "WithFriendlyFace"
              case "his face" =>
                builtCommand.secondName = "WithEnemyFace"
              case _ =>

            }
          case PLAY_COMMAND(cardNumber) =>
            builtCommand.name = "CardPlay"
            builtCommand.card = cardNumber.toInt


          case _ =>
        }
      case _ =>
    }

    builtCommand = OrganizeBuiltCommand(builtCommand)
    return builtCommand
  }

  def OrganizeBuiltCommand(builtCommand: Command): Command = {
    if (builtCommand.secondName == "WithPosition") {
      val oldSecondName = builtCommand.secondName
      val oldThirdName = builtCommand.thirdName
      builtCommand.thirdName = oldSecondName
      builtCommand.secondName = oldThirdName
    }

    if (builtCommand.thirdName != "") {
      builtCommand.name = builtCommand.firstName + builtCommand.secondName + builtCommand.thirdName
      return builtCommand
    }

    if (builtCommand.secondName != "") {
      builtCommand.name = builtCommand.firstName + builtCommand.secondName
      return builtCommand
    }

    return builtCommand

  }

}

class Command() {
  var name = ""
  var firstName = ""
  var secondName = ""
  var thirdName = ""
  var card = -2
  var spot = -2
  var target = -2
  var battlecry = -2
  var sender = ""
}
