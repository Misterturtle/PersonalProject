package tph

import tph.Constants.ActionVoteCodes.ActionVoteCode
import tph.Constants.{MenuVoteCodes, EmojiVoteCodes}
import tph.Constants
import tph.Constants.EmojiVoteCodes.EmojiVoteCode
import tph.Constants.MenuVoteCodes.MenuVoteCode


import scala.collection._
import tph.ircLogic


class VoteManager {

  var tallyActionMap = mutable.Map[ActionVote, Int]()
  val tallyMenuMap = mutable.Map[MenuVote, Int]()
  val tallyMulliganMap = mutable.Map[String, Int]()

  val listOfVoters = mutable.Map[String, Voter]()


  initialize()


  def initialize(): Unit = {

    tallyMulliganMap("first") = 0
    tallyMulliganMap("second") = 0
    tallyMulliganMap("third") = 0
    tallyMulliganMap("fourth") = 0
    tallyMulliganMap("voters") = 0
  }


  def VoteEntry(vote: Vote): Unit = {

    if (!listOfVoters.isDefinedAt(vote.sender))
      listOfVoters(vote.sender) = new Voter(vote.sender)

    listOfVoters(vote.sender).VoteEntry(vote)
  }

  def MulliganVoteEntry(first: Boolean, second: Boolean, third: Boolean, fourth: Boolean): Unit = {


    if (first) {
      tallyMulliganMap("first") += 1
    }
    if (second) {
      tallyMulliganMap("second") += 1
    }
    if (third) {
      tallyMulliganMap("third") += 1
    }
    if (fourth) {
      tallyMulliganMap("fourth") += 1
    }

    tallyMulliganMap("voters") += 1
  }


  def GetTurnAmount(): Int = {

    var voteAverage = 0
    val endTurnMap = mutable.Map[Int, Float](0 -> 0, 1 -> 0, 2 -> 0, 3 -> 0, 4 -> 0, 5 -> 0, 6 -> 0, 7 -> 0, 8 -> 0, 9 -> 0, 10 -> 0)
    val tallyEndTurnMap = mutable.Map[Int, Float](0 -> 0, 1 -> 0, 2 -> 0, 3 -> 0, 4 -> 0, 5 -> 0, 6 -> 0, 7 -> 0, 8 -> 0, 9 -> 0, 10 -> 0)


    //If voter.isComplete then we store votelist.size as 1 multiplied with an END_TURN_FACTOR
    //If voter.isComplete != then we store votelist.size with INCOMPLETE_FACTOR = .7
    //For (a<- 0 until votelist.size) votelist
    //Now the votelist.size (between 1 and 10) with the highest value is the number of turns

    listOfVoters foreach {

      case (sender, voter) =>
        if (voter.GetNumberOfTurns() <= 10 && voter.GetNumberOfTurns() >= 0) {
          if (voter.finished) {
            endTurnMap(voter.GetNumberOfTurns()) += (1 * ircLogic.FINISHED_FACTOR)
          }
          if (!voter.finished) {
            endTurnMap(voter.GetNumberOfTurns()) += (1 * ircLogic.UNFINISHED_FACTOR)
          }
        }
    }

    for (a <- 0 until 10) {

      tallyEndTurnMap(a) += endTurnMap(a)

      if (a != 0)
        tallyEndTurnMap(a) += endTurnMap(a - 1) * ircLogic.ONE_OFF_FACTOR

      if (a != 10)
        tallyEndTurnMap(a) += endTurnMap(a + 1) * ircLogic.ONE_OFF_FACTOR

      val oneOffValue = endTurnMap(a) * ircLogic.ONE_OFF_FACTOR

      if (a != 0) {
        endTurnMap(a) += (oneOffValue * endTurnMap(a - 1))
      }

      if (a != 10) {
        endTurnMap(a) += (oneOffValue * endTurnMap(a + 1))
      }
    }

    tallyEndTurnMap.max._1

  }


  def AdjustVotes(previousGameStatus: FrozenGameStatus, currentGameStatus: FrozenGameStatus): Unit = {
    //Tell all voters to AdjustVotes

    listOfVoters foreach {
      case (sender, voter) =>
        voter.AdjustVotes(previousGameStatus, currentGameStatus)
    }
  }


  def DecideAction(): ActionVote = {

    //Each Voter needs to tally his normal, bind, and future action list.
    //**It will be in the form of Map[VoteCode, Int].
    //Voters then combine the vote values. (How much they're worth)
    //Voters then report a map of [Vote, Int] to this message

    val voteCode = TallyActionVotes().max._1
    val decision = new ActionVote("voteManager", voteCode)
    decision.Init()
    return decision
  }


  def TallyActionVotes(): mutable.Map[ActionVoteCode, Int] = {

    val totalTallyMap = mutable.Map[ActionVoteCode, Int]()

    //For all voters
    listOfVoters foreach {
      case (sender, voter) =>

        //Get TallyMap and combine with totalTallyMap
        val tallyMap = voter.TallyActionVotes()

        tallyMap foreach {
          case (vote, value) =>
            if (totalTallyMap.contains(vote))
              totalTallyMap(vote) += value
            else
              totalTallyMap(vote) = value
        }
    }
    return totalTallyMap
  }

  def RemovePreviousDecision(vote: ActionVote): Unit = {
    listOfVoters foreach {
      case (sender, voter) =>
        voter.RemovePreviousDecision(vote)
    }
  }

  def EmojiDecide(): EmojiVote = {

    val tallyEmojiMap = mutable.Map[EmojiVoteCode, Int]()

    listOfVoters foreach {
      case (sender, voter) =>
        val emojiVoteCode = voter.GetEmojiVoteCode()



        if (emojiVoteCode != EmojiVoteCodes.EmojiUninit())
          tallyEmojiMap(emojiVoteCode) += 1
        }


    val decisionVoteCode = tallyEmojiMap.max._1
    val decision = new EmojiVote("voteManager", decisionVoteCode)
    ResetEmojiVotes()
    decision
    }

  def ResetEmojiVotes(): Unit = {

    listOfVoters foreach {
      case (sender, voter) =>
        voter.emojiVote.voteCode = EmojiVoteCodes.EmojiUninit()
        voter.emojiVote.emojiVoteCode = EmojiVoteCodes.EmojiUninit()
    }

  }


  def MenuDecide(): MenuVote = {

    val tallyMenuMap = mutable.Map[MenuVoteCode, Int]()

    listOfVoters foreach {
      case (sender, voter) =>
        val menuVoteCode = voter.GetMenuVoteCode()



        if (menuVoteCode != MenuVoteCodes.MenuUninit())
          tallyMenuMap(menuVoteCode) += 1
    }


    val decisionVoteCode = tallyMenuMap.max._1
    val decision = new MenuVote("voteManager", decisionVoteCode)
    ResetMenuVotes()
    decision
  }

  def ResetMenuVotes(): Unit = {

    listOfVoters foreach {
      case (sender, voter) =>
        voter.menuVote.voteCode = MenuVoteCodes.MenuUninit()
        voter.menuVote.menuVoteCode = MenuVoteCodes.MenuUninit()
    }

  }

  def GetMulliganPercentages(): (Double, Double, Double, Double) = {

    listOfVoters foreach {
      case (sender, voter) =>
        val mulliganVote = listOfVoters(sender).GetMulliganVote()

        if (mulliganVote._1)
          tallyMulliganMap("first") += 1
        if (mulliganVote._2)
          tallyMulliganMap("second") += 1
        if (mulliganVote._3)
          tallyMulliganMap("third") += 1
        if (mulliganVote._4)
          tallyMulliganMap("fourth") += 1
    }

    val firstPercentage: Double = tallyMulliganMap("first") / tallyMulliganMap("voters")
    val secondPercentage: Double = tallyMulliganMap("second") / tallyMulliganMap("voters")
    val thirdPercentage: Double = tallyMulliganMap("third") / tallyMulliganMap("voters")
    val fourthPercentage: Double = tallyMulliganMap("fourth") / tallyMulliganMap("voters")

    (firstPercentage, secondPercentage, thirdPercentage, fourthPercentage)
  }

  def Reset(): Unit = {
    tallyActionMap.clear()
    tallyMenuMap.clear()
    tallyMulliganMap.clear()

    initialize()

    listOfVoters foreach {

      case (sender, voter) =>
        voter.Reset()
    }

  }

  def GameOver(): Unit = {

    Reset()
  }




}

