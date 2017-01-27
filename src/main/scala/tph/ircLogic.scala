//What am I currently working on?
//Current Thought: Working on adjusting votes. Left a line red.


//Problem: How will I balance the amount of votes required to execute a chaos mode, to execute a Hurry. How to balance numbers of moves executed?
//Solution: Have a log output of tallyMap as votes get executed. This will give me more data to see how drastic 1st and 2nd votes are.


//Problem: Multiple vote entries for a single vote on a single sender?

//Problem: when adding to bindMap, it should have a break() at the end that gets trimmed and replaced every time

//Theory crafting - Chaos is single decisions.
//Chaos Mode checks every 2 seconds for if highest vote is VOTE_PERCENTAGE(200%?)(double) ahead of 2nd vote.
//If no unanimous decision after 10 seconds just do highest vote.
//Only one vote per person allowed in chaos mode


//D


package tph

///// TO DO::

//Update adjust votes to update voteCode, not just the vote data

//If a vote is played in the middle of a bind without the bind being active.
//Do I want to remove previous votes and active the bind at that vote?

import java.util.concurrent
import java.util.concurrent.{ScheduledThreadPoolExecutor, TimeUnit}

import akka.actor.{Cancellable, Actor, ActorRef, ActorSystem}
import akka.event.LoggingReceive
import akka.pattern.ask
import akka.util.Timeout
import tph.Constants.StateSignatures

import tph.LogFileEvents.DiscoverOption
import tph.StateManagement._

import scala.collection.mutable._
import scala.collection.{Set, mutable}
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._


/**
  * Created by RC on 8/16/2016.
  */
object ircLogic {


  val VOTE_DIFFERENCE_FACTOR = 1.3
  val TEMP_DECISION_VOTE_ADJUSTMENT_FACTOR = 1.5
  val CONCEDE_PERCENTAGE = .8
  val HURRY_VOTER_PERCENTAGE = .7
  val MULLIGAN_PERCENTAGE = .5

  //Decision Times
  val DECISION_TIME = 40
  val MULLIGAN_DECISION_TIME = 10
  val MENU_DECISION_TIME = 5

  //Deciding how many turns
  val FINISHED_FACTOR = 1
  val UNFINISHED_FACTOR = .7
  val ONE_OFF_FACTOR = .5

  //Vote values
  val NORMAL_VOTE_VALUE = 10

  val NORMAL_BIND_VOTE_VALUE = 10
  val ACTIVE_BIND_VOTE_VALUE = 10
  val NONACTIVE_BIND_VOTE_VALUE = 10

  val NORMAL_FUTURE_VOTE_VALUE = 10
  val ACTIVE_FUTURE_VOTE_VALUE = 10
  val NONACTIVE_FUTURE_VOTE_VALUE = 10
}


class ircLogic(theBrain: TheBrain, hearthstone: Hearthstone) {
  //Input comes in
  //Input is organized into Arrays?
  import ircLogic._


  val voteManager = new VoteManager()
  val scheduler = new ScheduledThreadPoolExecutor(1)


  def VoteEntry(vote: Vote): Unit = {


    theBrain.currentStatus match {

      case inMulligan: InMulligan =>
        theBrain.inMulligan.VoteEntry(vote, voteManager)

      case hisTurn: HisTurn =>
        theBrain.hisTurn.VoteEntry(vote, voteManager)

      case myTurn: MyTurn =>
        theBrain.myTurn.VoteEntry(vote, voteManager)

      case inMenu: InMenu =>
        theBrain.inMenu.VoteEntry(vote, voteManager)

    }
  }

  def EmojiDecide(): EmojiVote = {
    voteManager.EmojiDecide()
  }

  def MenuDecide(): MenuVote = {
    voteManager.MenuDecide()
  }

  def StartMenuDecide(): Unit = {
    val menuDecide = new Runnable {
      override def run(): Unit = MenuDecide()
    }
    scheduler.schedule(menuDecide, ircLogic.MENU_DECISION_TIME, TimeUnit.SECONDS)
  }

  def ResetEmojiVotes(): Unit = {
    voteManager.ResetEmojiVotes()
  }

  def ResetMenuVotes(): Unit = {
    voteManager.ResetMenuVotes()
  }

  def MulliganDecide(): Vote = {

    val percentages = voteManager.GetMulliganPercentages()
    var first = false
    var second = false
    var third = false
    var fourth = false

    if (percentages._1 > MULLIGAN_PERCENTAGE)
      first = true

    if (percentages._2 > MULLIGAN_PERCENTAGE)
      second = true

    if (percentages._3 > MULLIGAN_PERCENTAGE)
      third = true

    if (percentages._4 > MULLIGAN_PERCENTAGE)
      fourth = true

    new Vote("ircLogic", Constants.ActionVoteCodes.MulliganVote(first, second, third, fourth))

  }


  def GameOver(): Unit = {

    voteManager.GameOver()
  }

  def TurnStart(): Unit = {
    //Reset everything
    voteManager.Reset()


    //Wait Decicion Time
    //Decide()
    val decide = new Runnable {
      def run() = Decide()
    }
    scheduler.schedule(decide, ircLogic.DECISION_TIME, TimeUnit.SECONDS)


  }

  def StartGame(): Unit = {

    voteManager.Reset()
  }

  def StartMulligan(): Unit = {

    val mulliganDecide = new Runnable {
      def run() = MulliganDecide()
    }
    scheduler.schedule(mulliganDecide, ircLogic.MULLIGAN_DECISION_TIME, TimeUnit.SECONDS)
  }

  def TurnEnd(emojiVote: EmojiVote): Unit = {

    hearthstone.ExecuteEmojiVote(emojiVote)
    scheduler.shutdownNow()
    hearthstone.EndTurn()
  }


  def Decide(): Unit = {

    //Calculate number of moves with some weighted values. See ircLogic.Values.
    val numberOfTurns = voteManager.GetTurnAmount()

    //Each turn we...
    for (a <- 0 until numberOfTurns) {
      //Get the decision from voteManager
      val decision = GetDecision()
      //Get the current game stuatus(Before executing action)
      val previousGameStatus = theBrain.GetGameStatus()
      ExecuteVote(decision)
      WaitForHearthstone()
      RemovePreviousDecision(decision)
      //Adjust the votes based on the new game status
      AdjustVotes(previousGameStatus, theBrain.GetGameStatus())
    }
    val emojiVote = EmojiDecide()
    TurnEnd(emojiVote)
  }

  def RemovePreviousDecision(vote: ActionVote): Unit = {
    voteManager.RemovePreviousDecision(vote)
  }

  def ExecuteVote(decision: ActionVote): Unit = {

    hearthstone.ExecuteActionVote(decision)
  }

  def WaitForHearthstone(): Unit = {

    //Not sure a better way to do this

    //Just wait a flat time
    TimeUnit.SECONDS.sleep(5)
  }


  def AdjustVotes(previousGameStatus: FrozenGameStatus, currentGameStatus: FrozenGameStatus): Unit = {
    voteManager.AdjustVotes(previousGameStatus, currentGameStatus)
  }


  def GetDecision(): ActionVote = {

    voteManager.DecideAction()
  }
}

