package UnitTests

import VoteSystem.{Vote, VoteParser}
import org.scalatest.{FlatSpec, Matchers}
import tph.Constants.EmojiVotes._
import tph.Constants.MenuVotes._
import tph.Constants.MiscVotes._
import tph.Constants.ActionVotes._

import scala.collection.mutable.ListBuffer

/**
  * Created by Harambe on 2/23/2017.
  */
class VoteParserTests extends FlatSpec with Matchers {

  val sender = "voteParser"



  "A VoteParser" should "Identify raw string input" in {
    val GREETINGS = "!greetings"
    val THANKS = "!thanks"
    val WELL_PLAYED = "!well played"
    val WOW = "!wow"
    val OOPS = "!oops"
    val THREATEN = "!threaten"

    //In Game Always Type
    val END_TURN = "!end turn"
    val BIND = "!bind"
    val FUTURE = "!future"

    //Parsing
    val TWO_PART_COMMAND = "!play 1, target his 2"
    val THREE_PART_COMMAND = "!play 1,spot 2,target my 4"
    val PLAY_COMMAND = "!play 3"
    val ATTACK_COMMAND = "!att 1, target his 3"
    val MY_REGEX_NUMBER = "!play 3, target my 2"
    val HIS_REGEX_NUMBER = "!play 3, battlecry his 5"
    val SPOT_COMMAND = "!play 4, spot 1"
    val TARGET_COMMAND = "!play 2, target my 2"
    val BATTLECRY_COMMAND = "!play 1, battlecry my 3"
    val HERO_POWER_COMMAND = "hero power his face"
    val DISCOVER_COMMAND = "discover 3"
    val MULLIGAN = "!mulligan 4,1,2"

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
    val DECK = "!deck 2"
    val FIRST_PAGE = "!first page"
    val SECOND_PAGE = "!second page"

    //Quest Menu
    val QUEST =
      "!quest 2"



    val voteParser = new VoteParser()
    val actualVotes = List[Vote](
      voteParser.ParseVote(GREETINGS, sender),
      voteParser.ParseVote(THANKS, sender),
      voteParser.ParseVote(WELL_PLAYED, sender),
      voteParser.ParseVote(WELL_PLAYED, sender),
      voteParser.ParseVote(WOW, sender),
      voteParser.ParseVote(OOPS, sender),
      voteParser.ParseVote(THREATEN, sender),
      voteParser.ParseVote(END_TURN, sender),
      voteParser.ParseVote(BIND, sender),
      voteParser.ParseVote(FUTURE, sender),
      voteParser.ParseVote(TWO_PART_COMMAND, sender),
      voteParser.ParseVote(THREE_PART_COMMAND, sender),
      voteParser.ParseVote(PLAY_COMMAND, sender),
      voteParser.ParseVote(ATTACK_COMMAND, sender),
      voteParser.ParseVote(MY_REGEX_NUMBER, sender),
      voteParser.ParseVote(HIS_REGEX_NUMBER, sender),
      voteParser.ParseVote(SPOT_COMMAND, sender),
      voteParser.ParseVote(TARGET_COMMAND, sender),
      voteParser.ParseVote(BATTLECRY_COMMAND, sender),
      voteParser.ParseVote(HERO_POWER_COMMAND, sender),
      voteParser.ParseVote(DISCOVER_COMMAND, sender),
      voteParser.ParseVote(PLAY, sender),
      voteParser.ParseVote(COLLECTION, sender),
      voteParser.ParseVote(OPEN_PACKS, sender),
      voteParser.ParseVote(SHOP, sender),
      voteParser.ParseVote(QUEST_LOG, sender),
      voteParser.ParseVote(BACK, sender),
      voteParser.ParseVote(CASUAL, sender),
      voteParser.ParseVote(RANKED, sender),
      voteParser.ParseVote(DECK, sender),
      voteParser.ParseVote(FIRST_PAGE, sender),
      voteParser.ParseVote(SECOND_PAGE, sender),
      voteParser.ParseVote(QUEST, sender))

    val expectedVotes = List[Vote](
      new Greetings(sender),
      new Thanks(sender),
      new WellPlayed(sender),
      new Wow(sender),
      new Oops(sender),
      new Threaten(sender),
      new EndTurn(sender),
      new Bind(sender),
      new Future(sender),
      new CardPlayWithEnemyBoardTarget(sender, 1, 2),
      new CardPlayWithFriendlyTargetWithPosition(sender, 1, 4, 2),
      new CardPlay(sender, 1),
      new NormalAttackToFace(sender, 1),
      new CardPlayWithFriendlyBoardTarget(sender, 3, 2),
      new CardPlayWithEnemyBoardTarget(sender, 3, 5),
      new CardPlayWithPosition(sender, 4, 1),
      new CardPlayWithFriendlyBoardTarget(sender, 2, 2),
      new CardPlayWithFriendlyBoardTarget(sender, 1, 3),
      new HeroPowerWithEnemyFace(sender),
      new Discover(sender, 3),
      new MulliganVote(sender, true, true, false, true),
      new Play(sender),
      new Collection(sender),
      new OpenPacks(sender),
      new Shop(sender),
      new QuestLog(sender),
      new Back(sender),
      new Casual(sender),
      new Ranked(sender),
      new Deck(sender, 2),
      new FirstPage(sender),
      new SecondPage(sender),
      new Quest(sender, 2))

    actualVotes shouldEqual expectedVotes
  }

  it should "Parse Mulligan" in{

    val voteParser = new VoteParser()
    val actualVote = voteParser.ParseMulligan(sender,"!mulligan 4,1 , 3" )
    val expectedVote = new MulliganVote(sender, true, false, true, true)

    actualVote shouldEqual expectedVote
  }








}


