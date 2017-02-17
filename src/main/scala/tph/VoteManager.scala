package tph

import com.typesafe.scalalogging.LazyLogging
import tph.Constants.ActionVoteCodes.ActionVoteCode
import tph.Constants.{ActionVoteCodes, MenuVoteCodes, EmojiVoteCodes}
import tph.Constants.EmojiVoteCodes.EmojiVoteCode
import tph.Constants.MenuVoteCodes.MenuVoteCode


import scala.collection._

import scala.collection.mutable.ListBuffer


class VoteManager extends LazyLogging {


  def curry[A, B, C](f(a: A, B

  :: => A => B => C)

  var tallyActionMap = mutable.Map[ActionVote, Int]()
  val tallyMenuMap = mutable.Map[MenuVote, Int]()
  val tallyMulliganMap = mutable.Map[String, Int]()

  val listOfVoters = mutable.Map[String, Voter]()

  val voterAmountHistory = ListBuffer[Int]()


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
    val rawTallyMap = mutable.Map[Int, Double](0 -> 0, 1 -> 0, 2 -> 0, 3 -> 0, 4 -> 0, 5 -> 0, 6 -> 0, 7 -> 0, 8 -> 0, 9 -> 0, 10 -> 0)
    val weightedTallyMap = mutable.Map[Int, Double](0 -> 0, 1 -> 0, 2 -> 0, 3 -> 0, 4 -> 0, 5 -> 0, 6 -> 0, 7 -> 0, 8 -> 0, 9 -> 0, 10 -> 0)


    //If voter.isComplete then we store votelist.size as 1 multiplied with an END_TURN_FACTOR
    //If voter.isComplete != then we store votelist.size with INCOMPLETE_FACTOR = .7
    //For (a<- 0 until votelist.size) votelist
    //Now the votelist.size (between 1 and 10) with the highest value is the number of turns

    listOfVoters foreach {

      case (sender, voter) =>
        if (voter.GetNumberOfTurns() <= 10 && voter.GetNumberOfTurns() >= 0) {
          if (voter.finished) {
            rawTallyMap(voter.GetNumberOfTurns()) += (1 * ircLogic.FINISHED_FACTOR)
            logger.debug("Voter: " + voter.sender + " had added " + ((1 * ircLogic.FINISHED_FACTOR)) + " number of turns to the turn tally. Actual turn amount is: " + voter.GetNumberOfTurns())
          }
          if (!voter.finished) {
            rawTallyMap(voter.GetNumberOfTurns()) += (1 * ircLogic.UNFINISHED_FACTOR)
            logger.debug("Voter: " + voter.sender + " had added " + ((1 * ircLogic.UNFINISHED_FACTOR)) + " number of turns to the turn tally. Actual turn amount is: " + voter.GetNumberOfTurns())
          }
        }
    }

    for (a <- 0 until 10) {
      weightedTallyMap(a) += rawTallyMap(a)
    }

    for (a <- 0 until 10) {

      if (a != 0)
        weightedTallyMap(a) += rawTallyMap(a - 1) * ircLogic.ONE_OFF_FACTOR

      if (a != 10)
        weightedTallyMap(a) += rawTallyMap(a + 1) * ircLogic.ONE_OFF_FACTOR

    }

    val highestValue = weightedTallyMap.values.max
    val numberOfTurns = weightedTallyMap.find(_._2 == highestValue).getOrElse((-5, -5))._1
    if (highestValue != 0)
      numberOfTurns
    else 0
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

    val default = (ActionVoteCodes.ActionUninit(), Constants.INT_UNINIT)
    val tallyMap = TallyActionVotes()
    //

    if (!tallyMap.values.isEmpty) {

      val highestValue = tallyMap.values.max
      val chosenVoteCode = tallyMap.find(_._2 == highestValue).getOrElse(default)._1


      val decision = new ActionVote("voteManager", chosenVoteCode)
      decision.Init()
      return decision
    }
    else
      return new ActionVote("voteManger, DecideAction()", Constants.ActionVoteCodes.ActionUninit())
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

    RecordData()
    return totalTallyMap
  }

  def RecordVoterAmount(): Unit = {
    var voterTally = 0
    listOfVoters foreach {
      case (sender, voter) =>
        if (voter.actionVoteList.activeVoter)
          voterTally += 1
    }
    voterAmountHistory.append(voterTally)

    if (voterAmountHistory.size > 20) {
      voterAmountHistory.remove(0)
    }
  }

  def RemovePreviousDecision(vote: ActionVote): Unit = {
    listOfVoters foreach {
      case (sender, voter) =>
        voter.RemovePreviousDecision(vote)
    }
    logger.debug("Previous Decision Votes Removed")
  }

  def EmojiDecide(): EmojiVote = {
    val tallyEmojiMap = mutable.Map[EmojiVoteCode, Int]()

    listOfVoters foreach {
      case (sender, voter) =>
        val emojiVoteCode = voter.GetEmojiVoteCode()



        if (emojiVoteCode != EmojiVoteCodes.EmojiUninit())
          tallyEmojiMap(emojiVoteCode) += 1
    }

    if (tallyEmojiMap.isEmpty) {
      logger.debug("Emoji Map is empty after EmojiDecide()")
      val uninitDecision = new EmojiVote("voteManager", EmojiVoteCodes.EmojiUninit())
      uninitDecision
    }
    else {
      val default = (EmojiVoteCodes.EmojiUninit(), Constants.INT_UNINIT)
      val highestValue: Int = tallyEmojiMap.values.max
      val chosenVoteCode = tallyEmojiMap.find(_._2 == highestValue).getOrElse(default)._1

      val decision = new EmojiVote("voteManager", chosenVoteCode)
      ResetEmojiVotes()
      decision
    }












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
        val menuVoteCode = voter.menuVote.menuVoteCode



        if (voter.menuVote.voteCode != MenuVoteCodes.MenuUninit()) {
          if (!tallyMenuMap.isDefinedAt(menuVoteCode))
            tallyMenuMap(menuVoteCode) = 1
          else
            tallyMenuMap(menuVoteCode) += 1
        }
    }


    val default = (MenuVoteCodes.MenuUninit(), Constants.INT_UNINIT)
    if (tallyMenuMap.nonEmpty) {
      val highestValue = tallyMenuMap.values.max
      val chosenVoteCode = tallyMenuMap.find(_._2 == highestValue).getOrElse(default)._1

      val decision = new MenuVote("voteManager", chosenVoteCode)

      ResetMenuVotes()
      decision
    }
    else
      new MenuVote("VoteManager", Constants.MenuVoteCodes.MenuUninit())
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
        val mulliganVote: Vote = listOfVoters(sender).GetMulliganVote()


        if (mulliganVote.voteCode != Constants.UninitVoteCode()) {

          mulliganVote.voteCode match {
            case x: Constants.ActionVoteCodes.MulliganVote =>

              if (x.first)
                tallyMulliganMap("first") += 1
              if (x.second)
                tallyMulliganMap("second") += 1
              if (x.third)
                tallyMulliganMap("third") += 1
              if (x.fourth)
                tallyMulliganMap("fourth") += 1


            case _ =>

              logger.debug("Uninit Mulligan Vote Detected.")
          }
        }
    }

    if (tallyMulliganMap("voters") != 0) {
      val firstPercentage: Double = tallyMulliganMap("first") / tallyMulliganMap("voters")
      val secondPercentage: Double = tallyMulliganMap("second") / tallyMulliganMap("voters")
      val thirdPercentage: Double = tallyMulliganMap("third") / tallyMulliganMap("voters")
      val fourthPercentage: Double = tallyMulliganMap("fourth") / tallyMulliganMap("voters")

      (firstPercentage, secondPercentage, thirdPercentage, fourthPercentage)
    }
    else
      (0, 0, 0, 0)
  }

  def Reset(): Unit = {
    tallyActionMap.clear()
    tallyMenuMap.clear()
    tallyMulliganMap.clear()
    listOfVoters.clear()

    initialize()


  }

  //Probably removing concede
  //  def CheckConcede(): Boolean ={
  //
  //    var concedeTally = 0
  //    var activeVoters = 0
  //
  //    listOfVoters foreach {
  //      case (sender, voter) =>
  //        if(voter.concedeVote)
  //          concedeTally += 1
  //    }
  //
  //    listOfVoters foreach{
  //      case (sender, voter) =>
  //        if(voter.actionVoteList.activeVoter)
  //          activeVoters += 1
  //    }
  //
  //
  //
  //    if(concedeTally > (activeVoters * ircLogic.CONCEDE_PERCENTAGE) && activeVoters > (GetAverageVoters()/3))
  //      true
  //    else
  //      false
  //  }


  def GetAverageVoters(): Int = {
    var runningTotal = 0
    if (voterAmountHistory.nonEmpty) {
      voterAmountHistory foreach {
        case voterAmount =>
          runningTotal += voterAmount

      }
      runningTotal / voterAmountHistory.size
    }
    else 0
  }


  def RecordData(): Unit = {
    RecordVoterAmount()
  }

  def CheckHurry(): Boolean = {

    var hurryTally = 0
    var activeVoters = 0

    listOfVoters foreach {
      case (sender, voter) =>
        if (voter.hurryVote)
          hurryTally += 1
    }

    listOfVoters foreach {
      case (sender, voter) =>
        if (voter.actionVoteList.activeVoter)
          activeVoters += 1
    }



    if (hurryTally > (activeVoters * ircLogic.HURRY_VOTER_PERCENTAGE)) {
      if (activeVoters > (GetAverageVoters() / 3)) {
        true
      }
      else false
    }
    else
      false

  }




}

