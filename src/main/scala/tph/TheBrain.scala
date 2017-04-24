package tph

import FileReaders.HSAction._
import FileReaders.{LogParser, LogFileReader}
import GUI.{Controller, Display}
import Logic.IRCState
import VoteSystem.{VoteValidator, VoteAI, VoteState, VoteManager}
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import tph.Constants.ActionVotes._

import scala.collection.mutable.ListBuffer



//todo: Elemental Invocation



class TheBrain extends LazyLogging {


  val config = ConfigFactory.load()
  val accountName = config.getString("tph.hearthstone.accountName")
  val threadLoopDelay = 1000
  val INDEFINITE = -1

  var threadLoopStartTimeStamp = 0L
  var behindScheduleAmount = 0L


  def init(logFileReader:LogFileReader, ircBot:IRCBot, hs:HearthStone):Unit = {
    logFileReader.init()
    ircBot.init()
    hs.init()
  }


  def start(logFileReader: LogFileReader, gs: GameState, ircState: IRCState, vm: VoteManager, hs: HearthStone, validator: VoteValidator, vs: VoteState, loopAmount:Int): Unit = {
    loopAmount match{
      case `INDEFINITE` =>
        loop(logFileReader: LogFileReader, gs: GameState, ircState: IRCState, vm: VoteManager, hs: HearthStone, validator: VoteValidator, vs: VoteState)
        scheduleLoop(logFileReader: LogFileReader, gs: GameState, ircState: IRCState, vm: VoteManager, hs: HearthStone, validator, vs, -1)
      case 0 =>
      case _ =>
        loop(logFileReader: LogFileReader, gs: GameState, ircState: IRCState, vm: VoteManager, hs: HearthStone, validator: VoteValidator, vs: VoteState)
        scheduleLoop(logFileReader: LogFileReader, gs: GameState, ircState: IRCState, vm: VoteManager, hs: HearthStone, validator, vs, loopAmount-1)
    }
  }

  private def loop(logFileReader: LogFileReader, gs: GameState, ircState: IRCState, vm: VoteManager, hs: HearthStone, validator: VoteValidator, vs: VoteState): Unit ={
    threadLoopStartTimeStamp = System.currentTimeMillis()
    logFileReader.read()
    logFileReader.updateGameState()
    ircState.update(logFileReader.ircActions, vm)
    ircState.checkDecision(vm)
    ircState.checkExecution(hs, logFileReader.lastTimeActive, vm, gs, validator)
  }


  private def scheduleLoop(logFileReader: LogFileReader, gs: GameState, ircState: IRCState, vm: VoteManager, hs: HearthStone, validator: VoteValidator, vs: VoteState, loopCounter:Int): Unit = {
    val expectedNextThreadStartTime = threadLoopStartTimeStamp + threadLoopDelay
    val timeRemaining = expectedNextThreadStartTime - System.currentTimeMillis()

    //If there is time remaining
    if (timeRemaining >= 0) {
      //Remove our behindScheduleAmount from the sleep time
      if (timeRemaining >= behindScheduleAmount) {
        behindScheduleAmount = 0
        Thread.sleep((timeRemaining - behindScheduleAmount).toInt)
        start(logFileReader, gs, ircState, vm, hs, validator, vs, loopCounter)
      }
      else {
        //If we are too far behind schedule, start immediately and reduce our behindSchedule amount
        behindScheduleAmount -= timeRemaining
        start(logFileReader, gs, ircState, vm, hs, validator, vs, loopCounter)
      }
    }
    else {
      //If the thread execution longer than expected, start immediately and add time debt to our behindScheduleAmount
      behindScheduleAmount += timeRemaining
      start(logFileReader, gs, ircState, vm, hs, validator, vs, loopCounter)
    }
  }


  private def updateGameState(actionList: ListBuffer[GameStateAction], gs: GameState): Unit = {
    if (gs.friendlyPlayer.playerNumber != Constants.INT_UNINIT || gs.enemyPlayer.playerNumber != Constants.INT_UNINIT) {
      while (actionList.nonEmpty) {
        actionList.head.updateGameState(gs)
        actionList.remove(0)
      }
    }
    else {
      actionList foreach {
        case newHero: NewHero =>
          newHero.updateGameState(gs)

        case _ =>

      }
    }
  }

  private def updatePowerOptions(logFileReader: LogFileReader, voteState: VoteState): Unit = {

    voteState.optionDumpList.appendAll(logFileReader.optionDumpList)
    logFileReader.optionDumpList.clear()
    voteState.updatePowerOptions()
  }



}