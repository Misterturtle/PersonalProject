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
  val NO_CONCEDE = "!noconcede"
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
  val MULLIGAN = """!mulligan(.*)""".r

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
              theBrain.VoteEntry(new MenuVote(sender, Constants.MenuVoteCodes.Play(theBrain.currentMenu)))
            case SHOP =>
              theBrain.VoteEntry(new MenuVote(sender, Constants.MenuVoteCodes.Shop(theBrain.currentMenu)))
            case OPEN_PACKS =>
              theBrain.VoteEntry(new MenuVote(sender, Constants.MenuVoteCodes.OpenPacks(theBrain.currentMenu)))
            case COLLECTION =>
              theBrain.VoteEntry(new MenuVote(sender, Constants.MenuVoteCodes.Collection(theBrain.currentMenu)))
            case QUEST_LOG =>
              theBrain.VoteEntry(new MenuVote(sender, Constants.MenuVoteCodes.QuestLog(theBrain.currentMenu)))
            case CASUAL =>
              theBrain.VoteEntry(new MenuVote(sender, Constants.MenuVoteCodes.Casual(theBrain.currentMenu)))
            case RANKED =>
              theBrain.VoteEntry(new MenuVote(sender, Constants.MenuVoteCodes.Ranked(theBrain.currentMenu)))
            case BACK =>
              theBrain.VoteEntry(new MenuVote(sender, Constants.MenuVoteCodes.Back(theBrain.currentMenu)))
            case DECK(deckNumber) =>
              theBrain.VoteEntry(new MenuVote(sender, Constants.MenuVoteCodes.Deck(deckNumber.toInt, theBrain.currentMenu)))
            case FIRST_PAGE =>
              theBrain.VoteEntry(new MenuVote(sender, Constants.MenuVoteCodes.FirstPage(theBrain.currentMenu)))
            case SECOND_PAGE =>
              theBrain.VoteEntry(new MenuVote(sender, Constants.MenuVoteCodes.SecondPage(theBrain.currentMenu)))
            case QUEST(number) =>
              theBrain.VoteEntry(new MenuVote(sender, Constants.MenuVoteCodes.Quest(number.toInt, theBrain.currentMenu)))
            case GREETINGS =>
              theBrain.VoteEntry(new EmojiVote(sender, Constants.EmojiVoteCodes.Greetings()))
            case THANKS =>
              theBrain.VoteEntry(new EmojiVote(sender, Constants.EmojiVoteCodes.Thanks()))
            case WELL_PLAYED =>
              theBrain.VoteEntry(new EmojiVote(sender, Constants.EmojiVoteCodes.WellPlayed()))
            case WOW =>
              theBrain.VoteEntry(new EmojiVote(sender, Constants.EmojiVoteCodes.Wow()))
            case OOPS =>
              theBrain.VoteEntry(new EmojiVote(sender, Constants.EmojiVoteCodes.Oops()))
            case THREATEN =>
              theBrain.VoteEntry(new Vote(sender, Constants.EmojiVoteCodes.Threaten()))

            //Misc Type
            case HURRY =>
              theBrain.VoteEntry(new Vote(sender, Constants.MiscVoteCodes.Hurry()))
            //Probably removing concede
            //            case CONCEDE =>
            //              theBrain.VoteEntry(new Vote(sender, Constants.MiscVoteCodes.Concede(true)))
            //            case NO_CONCEDE =>
            //              theBrain.VoteEntry(new Vote(sender, Constants.MiscVoteCodes.Concede(false)))
            case END_TURN =>
              theBrain.VoteEntry(new Vote(sender, Constants.MiscVoteCodes.EndTurn()))
            case BIND =>
              theBrain.VoteEntry(new ActionVote(sender, Constants.ActionVoteCodes.Bind()))
            case FUTURE =>
              theBrain.VoteEntry(new ActionVote(sender, Constants.ActionVoteCodes.Future()))

            case MULLIGAN(stringCommand: String) =>

              // returns a Constants.voteCodes.MulliganVote()
              val mulliganVote = ParseMulligan(sender, stringCommand)

              theBrain.VoteEntry(new Vote(sender, Constants.ActionVoteCodes.MulliganVote(mulliganVote.first, mulliganVote.second, mulliganVote.third, mulliganVote.fourth)))


            case _ =>
              val actionVote = CreateVote(message, sender)
              logger.debug("IrcBot has created a new vote: " + actionVote)
              AssignVoteCode(actionVote)
              logger.debug("The assigned votecode is " + actionVote.actionVoteCode)


              if (actionVote.actionVoteCode != Constants.ActionVoteCodes.ActionUninit())
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
                newVote.friendlyTarget = targetPos.toInt
              case HIS_REGEX_NUMBER(targetPos) =>
                newVote.secondCommand = "WithEnemyTarget"
                newVote.enemyTarget = targetPos.toInt
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
                newVote.friendlyTarget = targetPos.toInt
              case HIS_REGEX_NUMBER(targetPos) =>
                newVote.secondCommand = "WithEnemyOption"
                newVote.enemyTarget = targetPos.toInt
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
                newVote.friendlyTarget = targetPos.toInt
              case HIS_REGEX_NUMBER(targetPos) =>
                newVote.thirdCommand = "WithEnemyTarget"
                newVote.enemyTarget = targetPos.toInt
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
                newVote.friendlyTarget = targetPos.toInt
              case HIS_REGEX_NUMBER(targetPos) =>
                newVote.thirdCommand = "WithEnemyOption"
                newVote.enemyTarget = targetPos.toInt
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
                newVote.friendlyTarget = attackerPos.toInt
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
                newVote.friendlyTarget = targetPos.toInt
              case HIS_REGEX_NUMBER(targetPos) =>
                newVote.secondCommand = "WithEnemyTarget"
                newVote.enemyTarget = targetPos.toInt
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
                newVote.friendlyTarget = targetPos.toInt
              case HIS_REGEX_NUMBER(targetPos) =>
                newVote.secondCommand = "WithEnemyOption"
                newVote.enemyTarget = targetPos.toInt
              case _ =>
            }
          case _ =>
        }


      case ONE_PART_COMMAND(command) =>
        command match {
          case DISCOVER_COMMAND(option) =>
            newVote.fullCommand = "Discover"
            newVote.card = option.toInt
          case "hero power" =>
            newVote.fullCommand = "HeroPower"
          case HERO_POWER_COMMAND(target) =>
            newVote.firstCommand = "HeroPower"
            target match {
              case MY_REGEX_NUMBER(targetPos) =>
                newVote.friendlyTarget = targetPos.toInt
                newVote.secondCommand = "WithFriendlyTarget"
              case HIS_REGEX_NUMBER(targetPos) =>
                newVote.enemyTarget = targetPos.toInt
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
      "CardPlay" -> Constants.ActionVoteCodes.CardPlay(vote.card),
      "CardPlayWithFriendlyOption" -> Constants.ActionVoteCodes.CardPlayWithFriendlyOption(vote.card, vote.friendlyTarget),
      "CardPlayWithFriendlyFaceOption" -> Constants.ActionVoteCodes.CardPlayWithFriendlyFaceOption(vote.card),
      "CardPlayWithEnemyOption" -> Constants.ActionVoteCodes.CardPlayWithEnemyOption(vote.card, vote.enemyTarget),
      "CardPlayWithEnemyFaceOption" -> Constants.ActionVoteCodes.CardPlayWithEnemyFaceOption(vote.card),
      "CardPlayWithFriendlyOptionWithPosition" -> Constants.ActionVoteCodes.CardPlayWithFriendlyOptionWithPosition(vote.card, vote.friendlyTarget, vote.spot),
      "CardPlayWithFriendlyFaceOptionWithPosition" -> Constants.ActionVoteCodes.CardPlayWithFriendlyFaceOptionWithPosition(vote.card, vote.spot),
      "CardPlayWithEnemyOptionWithPosition" -> Constants.ActionVoteCodes.CardPlayWithEnemyOptionWithPosition(vote.card, vote.enemyTarget, vote.spot),
      "CardPlayWithEnemyFaceOptionWithPosition" -> Constants.ActionVoteCodes.CardPlayWithEnemyFaceOptionWithPosition(vote.card, vote.spot),
      "CardPlayWithPosition" -> Constants.ActionVoteCodes.CardPlayWithPosition(vote.card, vote.spot),
      "CardPlayWithFriendlyTarget" -> Constants.ActionVoteCodes.CardPlayWithFriendlyBoardTarget(vote.card, vote.friendlyTarget),
      "CardPlayWithEnemyTarget" -> Constants.ActionVoteCodes.CardPlayWithEnemyBoardTarget(vote.card, vote.enemyTarget),
      "CardPlayWithFriendlyFaceTarget" -> Constants.ActionVoteCodes.CardPlayWithFriendlyFaceTarget(vote.card),
      "HeroPower" -> Constants.ActionVoteCodes.HeroPower(),
      "HeroPowerWithEnemyFace" -> Constants.ActionVoteCodes.HeroPowerWithEnemyFace(),
      "HeroPowerWithEnemyTarget" -> Constants.ActionVoteCodes.HeroPowerWithEnemyTarget(vote.enemyTarget),
      "HeroPowerWithFriendlyFace" -> Constants.ActionVoteCodes.HeroPowerWithFriendlyFace(),
      "HeroPowerWithFriendlyTarget" -> Constants.ActionVoteCodes.HeroPowerWithFriendlyTarget(vote.friendlyTarget),
      "NormalAttackWithEnemyTarget" -> Constants.ActionVoteCodes.NormalAttack(vote.friendlyTarget, vote.enemyTarget),
      "NormalAttackWithEnemyFaceTarget" -> Constants.ActionVoteCodes.NormalAttackToFace(vote.friendlyTarget),
      "FaceAttackWithEnemyTarget" -> Constants.ActionVoteCodes.FaceAttack(vote.enemyTarget),
      "FaceAttackWithEnemyFaceTarget" -> Constants.ActionVoteCodes.FaceAttackToFace()
    )

    if (vote.fullCommand != "") {
      vote.actionVoteCode = voteCodeMap(vote.fullCommand)
      vote.voteCode = voteCodeMap(vote.fullCommand)
    }
    else {
      logger.debug("Attempting to assign voteCode to a vote with an uninit fullCommand")
    }



    //  1   ,  3

  }

  def ParseMulligan(sender: String, vote: String): Constants.ActionVoteCodes.MulliganVote = {
    val ONE = """.*(\d).*""".r
    val TWO = """.*(\d).*,.*(\d).*""".r
    val THREE = """.*(\d).*,.*(\d).*,.*(\d).*""".r
    val FOUR = """.*(\d).*,.*(\d).*,.*(\d).*,.*(\d).*""".r

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

      case _ =>
        logger.debug("Unexpected mulligan string " + vote)
        logger.debug("Creating empty vote to match return type")

        return Constants.ActionVoteCodes.MulliganVote(false, false, false, false)
    }
  }
}
