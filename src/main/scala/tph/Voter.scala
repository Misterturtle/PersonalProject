package tph

import tph.Constants.ActionVoteCodes.ActionVoteCode
import tph.Constants.EmojiVoteCodes.EmojiVoteCode
import tph.Constants.MenuVoteCodes.MenuVoteCode

import scala.collection.mutable._


/**
  * Created by Harambe on 10/29/2016.
  */



class Voter(sender: String) {

  val actionVoteList = new ActionVoteList()
  var emojiVote = new EmojiVote(sender, Constants.EmojiVoteCodes.EmojiUninit())
  var menuVote = new MenuVote(sender, Constants.MenuVoteCodes.MenuUninit())
  var mulliganVote = new Tuple4(false, false, false, false)

  val voteLog = new VoteList
  val finished = false

  def AdjustVotes(previousGameStatus: FrozenGameStatus, currentGameStatus: FrozenGameStatus): Unit = {
    //ActionList is the only list that needs to be adjusted.

    actionVoteList.AdjustVotes(previousGameStatus, currentGameStatus)
      }


  def VoteEntry(vote: Vote): Unit = {
    vote match {

      case actVote: ActionVote =>
        actionVoteList.AddVote(actVote)

      case emoVote: EmojiVote =>
        emojiVote = emoVote

      case mVote: MenuVote =>
        menuVote = mVote
    }
      }


  def TallyActionVotes(): scala.collection.mutable.Map[ActionVoteCode, Int] = {

        return actionVoteList.TallyVotes()

      }

  def GetEmojiVoteCode(): EmojiVoteCode = {
    return emojiVote.emojiVoteCode

      }

  def GetMenuVoteCode(): MenuVoteCode = {
    return menuVote.menuVoteCode

      }

  def RemovePreviousDecision(vote: ActionVote): Unit = {
    actionVoteList.RemovePreviousDecision(vote)
  }

  def GetMulliganVote(): (Boolean, Boolean, Boolean, Boolean) = {

    return mulliganVote

  }

  def GetNumberOfTurns(): Int = {

    actionVoteList.GetNumberOfTurns()
  }

  def Reset(): Unit = {

    emojiVote = new EmojiVote(sender, Constants.EmojiVoteCodes.EmojiUninit())
    menuVote = new MenuVote(sender, Constants.MenuVoteCodes.MenuUninit())
    actionVoteList.Reset()

  }

  def Kill(): Unit = {

    //Reserved for tasks right before death.

    // EX: Reporting how many of his votes were used.
  }



}
