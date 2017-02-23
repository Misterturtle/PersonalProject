package VoteSystem

import tph.{Constants, IRCBot}

/**
  * Created by Harambe on 2/22/2017.
  */
class VoteParser {
import IRCBot._

//  def ParseVote(stringVote:String, sender:String): Vote ={
//
//    stringVote.toLowerCase match {
//
//      case PLAY =>
//        new MenuVote(sender, Constants.MenuVoteCodes.Play())
//      case SHOP =>
//        theBrain.VoteEntry(new MenuVote(sender, Constants.MenuVoteCodes.Shop(theBrain.currentMenu)))
//      case OPEN_PACKS =>
//        theBrain.VoteEntry(new MenuVote(sender, Constants.MenuVoteCodes.OpenPacks(theBrain.currentMenu)))
//      case COLLECTION =>
//        theBrain.VoteEntry(new MenuVote(sender, Constants.MenuVoteCodes.Collection(theBrain.currentMenu)))
//      case QUEST_LOG =>
//        theBrain.VoteEntry(new MenuVote(sender, Constants.MenuVoteCodes.QuestLog(theBrain.currentMenu)))
//      case CASUAL =>
//        theBrain.VoteEntry(new MenuVote(sender, Constants.MenuVoteCodes.Casual(theBrain.currentMenu)))
//      case RANKED =>
//        theBrain.VoteEntry(new MenuVote(sender, Constants.MenuVoteCodes.Ranked(theBrain.currentMenu)))
//      case BACK =>
//        theBrain.VoteEntry(new MenuVote(sender, Constants.MenuVoteCodes.Back(theBrain.currentMenu)))
//      case DECK(deckNumber) =>
//        theBrain.VoteEntry(new MenuVote(sender, Constants.MenuVoteCodes.Deck(deckNumber.toInt, theBrain.currentMenu)))
//      case FIRST_PAGE =>
//        theBrain.VoteEntry(new MenuVote(sender, Constants.MenuVoteCodes.FirstPage(theBrain.currentMenu)))
//      case SECOND_PAGE =>
//        theBrain.VoteEntry(new MenuVote(sender, Constants.MenuVoteCodes.SecondPage(theBrain.currentMenu)))
//      case QUEST(number) =>
//        theBrain.VoteEntry(new MenuVote(sender, Constants.MenuVoteCodes.Quest(number.toInt, theBrain.currentMenu)))
//      case GREETINGS =>
//        theBrain.VoteEntry(new EmojiVote(sender, Constants.EmojiVoteCodes.Greetings()))
//      case THANKS =>
//        theBrain.VoteEntry(new EmojiVote(sender, Constants.EmojiVoteCodes.Thanks()))
//      case WELL_PLAYED =>
//        theBrain.VoteEntry(new EmojiVote(sender, Constants.EmojiVoteCodes.WellPlayed()))
//      case WOW =>
//        theBrain.VoteEntry(new EmojiVote(sender, Constants.EmojiVoteCodes.Wow()))
//      case OOPS =>
//        theBrain.VoteEntry(new EmojiVote(sender, Constants.EmojiVoteCodes.Oops()))
//      case THREATEN =>
//        theBrain.VoteEntry(new Vote(sender, Constants.EmojiVoteCodes.Threaten()))
//
//      //Misc Type
//      case HURRY =>
//        theBrain.VoteEntry(new Vote(sender, Constants.MiscVoteCodes.Hurry()))
//      //Probably removing concede
//      //            case CONCEDE =>
//      //              theBrain.VoteEntry(new Vote(sender, Constants.MiscVoteCodes.Concede(true)))
//      //            case NO_CONCEDE =>
//      //              theBrain.VoteEntry(new Vote(sender, Constants.MiscVoteCodes.Concede(false)))
//      case END_TURN =>
//        theBrain.VoteEntry(new Vote(sender, Constants.MiscVoteCodes.EndTurn()))
//      case BIND =>
//        theBrain.VoteEntry(new ActionVote(sender, Constants.ActionVoteCodes.Bind()))
//      case FUTURE =>
//        theBrain.VoteEntry(new ActionVote(sender, Constants.ActionVoteCodes.Future()))
//
//      case MULLIGAN(stringCommand: String) =>
//
//        // returns a Constants.voteCodes.MulliganVote()
//        val mulliganVote = ParseMulligan(sender, stringCommand)
//
//        theBrain.VoteEntry(new Vote(sender, Constants.ActionVoteCodes.MulliganVote(mulliganVote.first, mulliganVote.second, mulliganVote.third, mulliganVote.fourth)))
//
//
//      case _ =>
//        val actionVote = CreateVote(message, sender)
//        logger.debug("IrcBot has created a new vote: " + actionVote)
//        AssignVoteCode(actionVote)
//        logger.debug("The assigned votecode is " + actionVote.actionVoteCode)
//
//
//        if (actionVote.actionVoteCode != Constants.ActionVoteCodes.ActionUninit())
//          theBrain.VoteEntry(actionVote)
//    }
//
//
//  }
//


}
