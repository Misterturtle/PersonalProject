//What am I currently working on?
//Current Thought: Working on adjusting votes. Left a line red.



package tph

///// TO DO::

//I reluctantly changed code in KnownCardDrawn in gamestatus. If something breaks remember I did that.
//Update adjust votes to update voteCode, not just the vote data

//If a vote is played in the middle of a bind without the bind being active.
//Do I want to remove previous votes and active the bind at that vote?

import java.util.concurrent
import java.util.concurrent.{ScheduledFuture, ScheduledThreadPoolExecutor, TimeUnit}

import akka.actor.{Cancellable, Actor, ActorRef, ActorSystem}
import akka.event.LoggingReceive
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import tph.Constants.StateSignatures

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

  val CHECK_PERCENTAGE = .3
  //Probably removing concede
  //  val CONCEDE_PERCENTAGE = .8
  val HURRY_VOTER_PERCENTAGE = .7
  val MULLIGAN_PERCENTAGE = .5

  //Decision Times
  val DECISION_TIME = 40
  val MULLIGAN_DECISION_TIME = 45
  val MENU_DECISION_TIME = 5

  //Deciding how many turns
  val FINISHED_FACTOR = 1
  val UNFINISHED_FACTOR = .7
  val ONE_OFF_FACTOR = .5

  //Vote values
  val NORMAL_VOTE_VALUE = 10

  val ACTIVE_BIND_VOTE_VALUE = 15
  val NONACTIVE_BIND_VOTE_VALUE = 5

  val ACTIVE_FUTURE_VOTE_VALUE = 15
  val NONACTIVE_FUTURE_VOTE_VALUE = 0
}


class ircLogic(theBrain: TheBrain, hearthstone: Hearthstone) extends LazyLogging {
  //Input comes in
  //Input is organized into Arrays?
  import ircLogic._


  val scheduler = new ScheduledThreadPoolExecutor(1)
  scheduler.setRemoveOnCancelPolicy(true)

  val schedulerQueue = mutable.Map[String, ScheduledFuture[_]]()

  val decide = new Runnable {
    def run() = Decide()
  }

  val check = new Runnable {
    override def run(): Unit = Check()
  }


  def VoteEntry(vote: Vote): Unit = {


    theBrain.currentStatus match {

      case inMulligan: InMulligan =>
        theBrain.inMulligan.VoteEntry(vote, theBrain.voteManager)

      case hisTurn: HisTurn =>
        theBrain.hisTurn.VoteEntry(vote, theBrain.voteManager)

      case myTurn: MyTurn =>
        theBrain.myTurn.VoteEntry(vote, theBrain.voteManager)

      case inMenu: InMenu =>
        theBrain.inMenu.VoteEntry(vote, theBrain.voteManager)

    }
  }

  def EmojiDecide(): EmojiVote = {
    theBrain.voteManager.EmojiDecide()
  }

  def MenuDecide() = {
    logger.debug("Menu Decide")
    val menuVote = theBrain.voteManager.MenuDecide()
    hearthstone.ExecuteMenuVote(menuVote)

    if (theBrain.currentStatus == theBrain.inMenu)
      StartMenuDecide()
  }

  def StartMenuDecide(): Unit = {
    val menuDecide = new Runnable {
      override def run(): Unit = MenuDecide()
    }
    scheduler.schedule(menuDecide, ircLogic.MENU_DECISION_TIME, TimeUnit.SECONDS)
  }

  def ResetEmojiVotes(): Unit = {
    theBrain.voteManager.ResetEmojiVotes()
    logger.debug("Resetting emoji votes")
  }

  def ResetMenuVotes(): Unit = {
    theBrain.voteManager.ResetMenuVotes()
    logger.debug("Resetting menu votes")
  }

  def MulliganDecide(): Unit = {

    logger.debug("Starting to decide mulligan")

    val percentages = theBrain.voteManager.GetMulliganPercentages()
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


    val mulliganVote = new Vote("ircLogic", Constants.ActionVoteCodes.MulliganVote(first, second, third, fourth))
    logger.debug("New mulligan vote created with voteCode: " + mulliganVote.voteCode)



    theBrain.ircLogic.ExecuteMulliganVote(mulliganVote)

    //If mulligan options is 3, you start first
    if (theBrain.inMulligan.mulliganOptions == 3) {
      theBrain.ChangeState(theBrain.myTurn)
    }
    if (theBrain.inMulligan.mulliganOptions == 4) {
      theBrain.ChangeState(theBrain.hisTurn)
    }

    theBrain.inMulligan.mulliganOptions = 0
    theBrain.mulliganComplete = true



  }


  def GameOver(): Unit = {

    theBrain.voteManager.Reset()
    logger.debug("Game Over")
  }

  def TurnStart(): Unit = {
    //Reset everything

    theBrain.voteManager.Reset()
    logger.debug("Turn starting in ircLogic")

    //Wait Decicion Time
    //Decide()

    val decideFuture = scheduler.schedule(decide, ircLogic.DECISION_TIME, TimeUnit.SECONDS)
    schedulerQueue("decide") = decideFuture

  }

  def StartGame(): Unit = {

    theBrain.voteManager.Reset()
    logger.debug("The game is starting in ircLogic")
  }

  def StartMulligan(): Unit = {

    val mulliganDecide = new Runnable {
      def run() = MulliganDecide()
    }
    scheduler.schedule(mulliganDecide, ircLogic.MULLIGAN_DECISION_TIME, TimeUnit.SECONDS)
    logger.debug("Mulligan has started in ircLogic")
  }

  def TurnEnd(): Unit = {

    val emojiVote = theBrain.voteManager.EmojiDecide()
    hearthstone.ExecuteEmojiVote(emojiVote)
    hearthstone.EndTurn()
    logger.debug("The turn has ended in ircLogic. The Emoji vote is " + emojiVote)
  }


  def Decide(): Unit = {

    logger.debug("Starting Decide method in ircLogic")

    //Calculate number of moves with some weighted values. See ircLogic.Values.
    val numberOfTurns = theBrain.voteManager.GetTurnAmount()

    logger.debug("There are " + numberOfTurns + " turns to play.")

    //Each turn we...
    for (a <- 0 until numberOfTurns) {
      //Get the decision from theBrain.voteManager
      val decision = GetDecision()
      logger.debug("Final decision is: " + decision)

      //Get the current game stuatus(Before executing action)
      val previousGameStatus = theBrain.GetGameStatus()
      ExecuteVote(decision)
      WaitForHearthstone()
      RemovePreviousDecision(decision)
      //Adjust the votes based on the new game status
      AdjustVotes(previousGameStatus, theBrain.GetGameStatus())
    }
    if (!theBrain.testMode) {
      theBrain.ChangeState(theBrain.hisTurn)
      hearthstone.EndTurn()
    }
  }

  def RemovePreviousDecision(vote: ActionVote): Unit = {
    theBrain.voteManager.RemovePreviousDecision(vote)
  }

  def ExecuteVote(decision: ActionVote): Unit = {

    if (!theBrain.testMode) {
    hearthstone.ExecuteActionVote(decision)
      logger.debug("Executing Vote: " + decision)
    }
    else {
      theBrain.mockHearthstone.MockVoteExecution(decision)
    }
  }

  def ExecuteMulliganVote(mulliganVote: Vote): Unit = {
    hearthstone.ExecuteMulliganVote(mulliganVote)
  }

  def WaitForHearthstone(): Unit = {

    //Not sure a better way to do this

    //Just wait a flat time
    TimeUnit.SECONDS.sleep(5)
  }


  def AdjustVotes(previousGameStatus: FrozenGameStatus, currentGameStatus: FrozenGameStatus): Unit = {
    theBrain.voteManager.AdjustVotes(previousGameStatus, currentGameStatus)
    logger.debug("Adjusting Votes")
  }


  def GetDecision(): ActionVote = {

    theBrain.voteManager.DecideAction()
  }

  def Check(): Unit = {


    //Probably removing concede
    //    if(CheckConcede()) {
    //      ExecuteConcede()
    //      logger.debug("Concede vote executed")
    //    }

    if (CheckHurry()) {
      if (theBrain.currentStatus == theBrain.myTurn) {
        logger.debug("Removing decide from scheduler")
        schedulerQueue("decide").cancel(false)
        logger.debug("Completed removing decide from scheduler")
        Decide()
        logger.debug("Hurry vote executed")
      }
    }




    scheduler.schedule(check, 5, TimeUnit.SECONDS)
    logger.debug("Checking")
  }


  //Probably removing concede

  //  def ExecuteConcede(): Unit ={
  //
  //    hearthstone.ExecuteEmojiVote(EmojiDecide())
  //    hearthstone.Concede()
  //    theBrain.ChangeState(theBrain.inMenu)
  //  }
  //
  //  def CheckConcede(): Boolean ={
  //    theBrain.voteManager.CheckConcede()
  //  }

  def CheckHurry(): Boolean = {
    theBrain.voteManager.CheckHurry()
  }

}

