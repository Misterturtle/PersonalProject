package tph

/**
  * Created by RC on 8/16/2016.
  */

import akka.actor.ActorRef
import com.typesafe.config.ConfigFactory
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





import tph.IrcBot._

class IrcBot(voteManager: VoteManager, theBrain: TheBrain) extends PircBot with LazyLogging {

  val config = ConfigFactory.load()


  val hostName = config.getString("tph.irc.host")
  val channel = config.getString("tph.irc.channel")
  val nickname = IrcBot.NICKNAME

  setName(nickname)
  logger.debug(s"nickname = $nickname")
  setVerbose(false)
  connect(hostName)
  logger.debug(s"connected to $hostName")
  joinChannel(channel)
  logger.debug(s"joined $channel")


  override def onMessage(channel: String, sender: String, login: String, hostName: String, message: String): Unit = {
    logger.debug(s"IRC message: sender=$sender, login=$login message=$message")
    message.toLowerCase match {

            case PLAY =>
              theBrain.VoteEntry(new Vote(sender, Constants.MenuVoteCodes.Play("Unknown")))
            case SHOP =>
              theBrain.VoteEntry(new Vote(sender, Constants.MenuVoteCodes.Shop("Unknown")))
            case OPEN_PACKS =>
              theBrain.VoteEntry(new Vote(sender, Constants.MenuVoteCodes.OpenPacks("Unknown")))
            case COLLECTION =>
              theBrain.VoteEntry(new Vote(sender, Constants.MenuVoteCodes.Collection("Unknown")))
            case QUEST_LOG =>
              theBrain.VoteEntry(new Vote(sender, Constants.MenuVoteCodes.QuestLog("Unknown")))
            case CASUAL =>
              theBrain.VoteEntry(new Vote(sender, Constants.MenuVoteCodes.Casual("Unknown")))
            case RANKED =>
              theBrain.VoteEntry(new Vote(sender, Constants.MenuVoteCodes.Ranked("Unknown")))
            case BACK =>
              theBrain.VoteEntry(new Vote(sender, Constants.MenuVoteCodes.Back("Unknown")))
            case DECK(deckNumber) =>
              theBrain.VoteEntry(new Vote(sender, Constants.MenuVoteCodes.Deck(deckNumber.toInt, "Unknown")))
            case FIRST_PAGE =>
              theBrain.VoteEntry(new Vote(sender, Constants.MenuVoteCodes.FirstPage("Unknown")))
            case SECOND_PAGE =>
              theBrain.VoteEntry(new Vote(sender, Constants.MenuVoteCodes.SecondPage("Unknown")))
            case QUEST(number) =>
              theBrain.VoteEntry(new Vote(sender, Constants.MenuVoteCodes.Quest(number.toInt, "Unknown")))
            case GREETINGS =>
              theBrain.VoteEntry(new Vote(sender, Constants.EmojiVoteCodes.Greetings()))
            case THANKS =>
              theBrain.VoteEntry(new Vote(sender, Constants.EmojiVoteCodes.Thanks()))
            case WELL_PLAYED =>
              theBrain.VoteEntry(new Vote(sender, Constants.EmojiVoteCodes.WellPlayed()))
            case WOW =>
              theBrain.VoteEntry(new Vote(sender, Constants.EmojiVoteCodes.Wow()))
            case OOPS =>
              theBrain.VoteEntry(new Vote(sender, Constants.EmojiVoteCodes.Oops()))
            case THREATEN =>
              theBrain.VoteEntry(new Vote(sender, Constants.EmojiVoteCodes.Threaten()))

            //Misc Type
            case WAIT =>
              theBrain.VoteEntry(new Vote(sender, Constants.ActionVoteCodes.Wait()))
            case HURRY =>
              theBrain.VoteEntry(new Vote(sender, Constants.ActionVoteCodes.Hurry()))
            case CONCEDE =>
              theBrain.VoteEntry(new Vote(sender, Constants.ActionVoteCodes.Concede()))
            case END_TURN =>
              theBrain.VoteEntry(new Vote(sender, Constants.ActionVoteCodes.EndTurn()))
            case BIND =>
              theBrain.VoteEntry(new Vote(sender, Constants.ActionVoteCodes.Bind()))
            case FUTURE =>
              theBrain.VoteEntry(new Vote(sender, Constants.ActionVoteCodes.Future()))

            case MULLIGAN(stringCommand: String) =>

              // returns a Constants.voteCodes.MulliganVote()
              val mulliganVote = ParseMulligan(stringCommand)

              theBrain.VoteEntry(new Vote(sender, Constants.ActionVoteCodes.MulliganVote(mulliganVote.first, mulliganVote.second, mulliganVote.third, mulliganVote.fourth)))

            case _ =>
              val actionVote = CreateVote(message, sender)
              AssignVoteCode(actionVote)


              if (actionVote.voteCode != Constants.ActionVoteCodes.ActionUninit())
                theBrain.VoteEntry(actionVote)
    }
  }


  def CreateVote(message: String, sender: String): ActionVote = {
    var newVote = new ActionVote(sender, Constants.ActionVoteCodes.ActionUninit())

    message match {
      case THREE_PART_COMMAND(firstCommand, secondCommand, thirdCommand) =>

        firstCommand match {
          case PLAY_COMMAND(cardNumber) =>
            newVote.firstCommand = "CardPlay"
            newVote.card = cardNumber.toInt
          case _ =>
        }


        secondCommand match {
          case SPOT_COMMAND(position) =>
            newVote.secondCommand = "WithPosition"
            newVote.spot = position.toInt
          case TARGET_COMMAND(target) =>
            target match {
              case "my face" =>
                newVote.secondCommand = "WithFriendlyFaceTarget"
              case "his face" =>
                newVote.secondCommand = "WithEnemyFaceTarget"
              case MY_REGEX_NUMBER(targetPos) =>
                newVote.secondCommand = "WithFriendlyTarget"
                newVote.target = targetPos.toInt
              case HIS_REGEX_NUMBER(targetPos) =>
                newVote.secondCommand = "WithEnemyTarget"
                newVote.target = targetPos.toInt
              case _ =>
            }
          case BATTLECRY_COMMAND(target) =>
            target match {
              case "my face" =>
                newVote.secondCommand = "WithFriendlyFaceOption"
              case "his face" =>
                newVote.secondCommand = "WithEnemyFaceOption"
              case MY_REGEX_NUMBER(targetPos) =>
                newVote.secondCommand = "WithFriendlyOption"
                newVote.battlecry = targetPos.toInt
              case HIS_REGEX_NUMBER(targetPos) =>
                newVote.secondCommand = "WithEnemyOption"
                newVote.battlecry = targetPos.toInt
              case _ =>
            }
          case _ =>
        }


        thirdCommand match {
          case SPOT_COMMAND(position) =>
            newVote.thirdCommand = "WithPosition"
            newVote.spot = position.toInt
          case TARGET_COMMAND(target) =>
            target match {
              case "my face" =>
                newVote.thirdCommand = "WithFriendlyFaceTarget"
              case "his face" =>
                newVote.thirdCommand = "WithEnemyFaceTarget"
              case MY_REGEX_NUMBER(targetPos) =>
                newVote.thirdCommand = "WithFriendlyTarget"
                newVote.target = targetPos.toInt
              case HIS_REGEX_NUMBER(targetPos) =>
                newVote.thirdCommand = "WithEnemyTarget"
                newVote.target = targetPos.toInt
              case _ =>
            }
          case BATTLECRY_COMMAND(target) =>
            target match {
              case "my face" =>
                newVote.thirdCommand = "WithFriendlyFaceOption"
              case "his face" =>
                newVote.thirdCommand = "WithEnemyFaceOption"
              case MY_REGEX_NUMBER(targetPos) =>
                newVote.thirdCommand = "WithFriendlyOption"
                newVote.battlecry = targetPos.toInt
              case HIS_REGEX_NUMBER(targetPos) =>
                newVote.thirdCommand = "WithEnemyOption"
                newVote.battlecry = targetPos.toInt
              case _ =>
            }
          case _ =>
        }


      case TWO_PART_COMMAND(firstCommand, secondCommand) =>

        firstCommand match {
          case PLAY_COMMAND(cardNumber) =>
            newVote.firstCommand = "CardPlay"
            newVote.card = cardNumber.toInt
          case ATTACK_COMMAND(attacker) =>
            attacker match {
              case MY_REGEX_NUMBER(attackerPos) =>
                newVote.firstCommand = "NormalAttack"
                newVote.card = attackerPos.toInt
              case "my face" =>
                newVote.firstCommand = "FaceAttack"
              case _ =>
            }
          case _ =>
        }


        secondCommand match {
          case SPOT_COMMAND(position) =>
            newVote.secondCommand = "WithPosition"
            newVote.spot = position.toInt
          case TARGET_COMMAND(target) =>
            target match {
              case "my face" =>
                newVote.secondCommand = "WithFriendlyFaceTarget"
              case "his face" =>
                newVote.secondCommand = "WithEnemyFaceTarget"
              case MY_REGEX_NUMBER(targetPos) =>
                newVote.secondCommand = "WithFriendlyTarget"
                newVote.target = targetPos.toInt
              case HIS_REGEX_NUMBER(targetPos) =>
                newVote.secondCommand = "WithEnemyTarget"
                newVote.target = targetPos.toInt
              case _ =>
            }
          case BATTLECRY_COMMAND(target) =>
            target match {
              case "my face" =>
                newVote.secondCommand = "WithFriendlyFaceOption"
              case "his face" =>
                newVote.secondCommand = "WithEnemyFaceOption"
              case MY_REGEX_NUMBER(targetPos) =>
                newVote.secondCommand = "WithFriendlyOption"
                newVote.battlecry = targetPos.toInt
              case HIS_REGEX_NUMBER(targetPos) =>
                newVote.secondCommand = "WithEnemyOption"
                newVote.battlecry = targetPos.toInt
              case _ =>
            }
          case _ =>
        }


      case ONE_PART_COMMAND(command) =>
        command match {
          case DISCOVER_COMMAND(option) =>
            newVote.fullCommand = "Discover"
            newVote.target = option.toInt
          case "hero power" =>
            newVote.fullCommand = "HeroPower"
          case HERO_POWER_COMMAND(target) =>
            newVote.firstCommand = "HeroPower"
            target match {
              case MY_REGEX_NUMBER(targetPos) =>
                newVote.target = targetPos.toInt
                newVote.secondCommand = "WithFriendlyTarget"
              case HIS_REGEX_NUMBER(targetPos) =>
                newVote.target = targetPos.toInt
                newVote.secondCommand = "WithEnemyTarget"
              case "my face" =>
                newVote.secondCommand = "WithFriendlyFace"
              case "his face" =>
                newVote.secondCommand = "WithEnemyFace"
              case _ =>

            }
          case PLAY_COMMAND(cardNumber) =>
            newVote.fullCommand = "CardPlay"
            newVote.card = cardNumber.toInt


          case _ =>
        }
      case _ =>
    }

    newVote = OrganizeVote(newVote)
    return newVote
  }


  def OrganizeVote(newVote: ActionVote): ActionVote = {
    if (newVote.secondCommand == "WithPosition") {
      val oldSecondCommand = newVote.secondCommand
      val oldThirdCommand = newVote.thirdCommand
      newVote.thirdCommand = oldSecondCommand
      newVote.secondCommand = oldThirdCommand
    }

    if (newVote.thirdCommand != "") {
      newVote.fullCommand = newVote.firstCommand + newVote.secondCommand + newVote.thirdCommand
      return newVote
    }

    if (newVote.secondCommand != "") {
      newVote.fullCommand = newVote.firstCommand + newVote.secondCommand
      return newVote
    }

    return newVote

  }

  def AssignVoteCode(vote: ActionVote): Unit = {


    val voteCodeMap = Map[String, Constants.ActionVoteCodes.ActionVoteCode](

      "Discover" -> Constants.ActionVoteCodes.Discover(vote.card),
      "CardPlayWithFriendlyOption" -> Constants.ActionVoteCodes.CardPlayWithFriendlyOption(vote.card, vote.battlecry),
      "CardPlayWithFriendlyFaceOption" -> Constants.ActionVoteCodes.CardPlayWithFriendlyFaceOption(vote.card),
      "CardPlayWithEnemyOption" -> Constants.ActionVoteCodes.CardPlayWithEnemyFaceOption(vote.card),
      "CardPlayWithEnemyFaceOption" -> Constants.ActionVoteCodes.CardPlayWithEnemyFaceOption(vote.card),
      "CardPlayWithFriendlyOptionWithPosition" -> Constants.ActionVoteCodes.CardPlayWithFriendlyOptionWithPosition(vote.card, vote.target, vote.spot),
      "CardPlayWithFriendlyFaceOptionWithPosition" -> Constants.ActionVoteCodes.CardPlayWithFriendlyFaceOptionWithPosition(vote.card, vote.spot),
      "CardPlayWithEnemyOptionWithPosition" -> Constants.ActionVoteCodes.CardPlayWithEnemyOptionWithPosition(vote.card, vote.target, vote.spot),
      "CardPlayWithEnemyFaceOptionWithPosition" -> Constants.ActionVoteCodes.CardPlayWithEnemyFaceOptionWithPosition(vote.card, vote.spot),
      "CardPlayWithPosition" -> Constants.ActionVoteCodes.CardPlayWithPosition(vote.card, vote.spot),
      "CardPlayWithFriendlyTarget" -> Constants.ActionVoteCodes.CardPlayWithFriendlyBoardTarget(vote.card, vote.target),
      "CardPlayWithEnemyTarget" -> Constants.ActionVoteCodes.CardPlayWithEnemyBoardTarget(vote.card, vote.target),
      "CardPlayWithFriendlyFaceTarget" -> Constants.ActionVoteCodes.CardPlayWithFriendlyFaceTarget(vote.card),
      "HeroPower" -> Constants.ActionVoteCodes.HeroPower(),
      "HeroPowerWithEnemyFace" -> Constants.ActionVoteCodes.HeroPowerWithEnemyFace(),
      "HeroPowerWithEnemyTarget" -> Constants.ActionVoteCodes.HeroPowerWithEnemyTarget(vote.target),
      "HeroPowerWithFriendlyFace" -> Constants.ActionVoteCodes.HeroPowerWithFriendlyFace(),
      "HeroPowerWithFriendlyTarget" -> Constants.ActionVoteCodes.HeroPowerWithFriendlyTarget(vote.target),
      "NormalAttackWithEnemyTarget" -> Constants.ActionVoteCodes.NormalAttack(vote.spot, vote.target),
      "NormalAttackWithEnemyFaceTarget" -> Constants.ActionVoteCodes.NormalAttackToFace(vote.spot),
      "FaceAttackWithEnemyTarget" -> Constants.ActionVoteCodes.FaceAttack(vote.spot),
      "FaceAttackWithEnemyFaceTarget" -> Constants.ActionVoteCodes.FaceAttackToFace()
    )

    if (vote.fullCommand != "") {
      vote.voteCode = voteCodeMap(vote.fullCommand)
    }
    else {
      logger.debug("Attempting to assign voteCode to a vote with an uninit fullCommand")
    }

  }

  def ParseMulligan(vote: String): Constants.ActionVoteCodes.MulliganVote = {
    val ONE = """(\d+)""".r
    val TWO = """(\d+), (\d+)""".r
    val THREE = """(\d+), (\d+), (\d+)""".r
    val FOUR = """(\d+), (\d+), (\d+), (\d+)""".r

    vote match {
      case FOUR(first, second, third, fourth) =>
        var firstCard = false
        var secondCard = false
        var thirdCard = false
        var fourthCard = false


        first match {

          case "1" =>
            firstCard = true
          case "2" =>
            secondCard = true
          case "3" =>
            thirdCard = true
          case "4" =>
            fourthCard = true
        }

        second match {

          case "1" =>
            firstCard = true
          case "2" =>
            secondCard = true
          case "3" =>
            thirdCard = true
          case "4" =>
            fourthCard = true
        }

        third match {

          case "1" =>
            firstCard = true
          case "2" =>
            secondCard = true
          case "3" =>
            thirdCard = true
          case "4" =>
            fourthCard = true
        }

        fourth match {

          case "1" =>
            firstCard = true
          case "2" =>
            secondCard = true
          case "3" =>
            thirdCard = true
          case "4" =>
            fourthCard = true
        }

        return Constants.ActionVoteCodes.MulliganVote(firstCard, secondCard, thirdCard, fourthCard)


      case THREE(first, second, third) =>
        var firstCard = false
        var secondCard = false
        var thirdCard = false
        var fourthCard = false


        first match {

          case "1" =>
            firstCard = true
          case "2" =>
            secondCard = true
          case "3" =>
            thirdCard = true
          case "4" =>
            fourthCard = true
        }

        second match {

          case "1" =>
            firstCard = true
          case "2" =>
            secondCard = true
          case "3" =>
            thirdCard = true
          case "4" =>
            fourthCard = true
        }

        third match {

          case "1" =>
            firstCard = true
          case "2" =>
            secondCard = true
          case "3" =>
            thirdCard = true
          case "4" =>
            fourthCard = true
        }

        return Constants.ActionVoteCodes.MulliganVote(firstCard, secondCard, thirdCard, fourthCard)


      case TWO(first, second) =>
        var firstCard = false
        var secondCard = false
        var thirdCard = false
        var fourthCard = false


        first match {

          case "1" =>
            firstCard = true
          case "2" =>
            secondCard = true
          case "3" =>
            thirdCard = true
          case "4" =>
            fourthCard = true
        }

        second match {

          case "1" =>
            firstCard = true
          case "2" =>
            secondCard = true
          case "3" =>
            thirdCard = true
          case "4" =>
            fourthCard = true
        }

        return Constants.ActionVoteCodes.MulliganVote(firstCard, secondCard, thirdCard, fourthCard)

      case ONE(first) =>
        var firstCard = false
        var secondCard = false
        var thirdCard = false
        var fourthCard = false


        first match {

          case "1" =>
            firstCard = true
          case "2" =>
            secondCard = true
          case "3" =>
            thirdCard = true
          case "4" =>
            fourthCard = true
        }


        return Constants.ActionVoteCodes.MulliganVote(firstCard, secondCard, thirdCard, fourthCard)
    }
  }
}
