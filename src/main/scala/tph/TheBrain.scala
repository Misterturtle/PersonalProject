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
  val fullTurnDelay = 60000
  val mulliganDelay = 25000
  val discoverDelay = 8000
  val chooseOneDelay = 5000
  val threadLoopDelay = 1000

  var threadLoopStartTimeStamp = 0.0
  var behindScheduleAmount = 0.0


  def init(logFileReader: LogFileReader, ircBot: IRCBot, hs: Hearthstone): Unit = {
    logFileReader.init()
    ircBot.init()
    logFileReader.update()
    hs.init()
  }


  def startDebug(logFileReader: LogFileReader, gs: GameState, ircState: IRCState, vm: VoteManager, hs: Hearthstone, controller: Controller, validator:VoteValidator): Unit = {
    threadLoopStartTimeStamp = System.currentTimeMillis()
    logFileReader.update()
    updateGameState(logFileReader.gameStateActions, gs)
    controller.updateGUIWindow()
    updateIRCState(logFileReader.ircActions, ircState, vm)
    checkDecision(ircState, vm)
    checkExecution(ircState, hs, logFileReader.lastTimeActive, vm, gs, validator)
    Thread.sleep(scheduleDebugLoop())
  }

  def scheduleDebugLoop(): Int = {
    val expectedNextThreadStartTime = threadLoopStartTimeStamp + threadLoopDelay
    val timeRemaining = expectedNextThreadStartTime - System.currentTimeMillis()

    //If there is time remaining
    if (timeRemaining >= 0) {
      //Remove our behindScheduleAmount from the sleep time
      if (timeRemaining >= behindScheduleAmount) {
        behindScheduleAmount = 0
        (timeRemaining - behindScheduleAmount).toInt
      }
      else {
        //If we are too far behind schedule, start immediately and reduce our behindSchedule amount
        behindScheduleAmount -= timeRemaining
        0
      }
    }
    else {
      //If the thread execution longer than expected, start immediately and add time debt to our behindScheduleAmount
      behindScheduleAmount += timeRemaining
      0
    }
  }


  def start(logFileReader: LogFileReader, gs: GameState, ircState: IRCState, vm: VoteManager, hs: Hearthstone, validator:VoteValidator): Unit = {
    threadLoopStartTimeStamp = System.currentTimeMillis()
    logFileReader.update()
    updateGameState(logFileReader.gameStateActions, gs)
    updateIRCState(logFileReader.ircActions, ircState, vm)
    checkDecision(ircState, vm)
    checkExecution(ircState, hs, logFileReader.lastTimeActive, vm, gs, validator)
    scheduleLoop(logFileReader: LogFileReader, gs: GameState, ircState: IRCState, vm: VoteManager, hs: Hearthstone, validator)
  }

  def scheduleLoop(logFileReader: LogFileReader, gs: GameState, ircState: IRCState, vm: VoteManager, hs: Hearthstone, validator:VoteValidator): Unit = {
    val expectedNextThreadStartTime = threadLoopStartTimeStamp + threadLoopDelay
    val timeRemaining = expectedNextThreadStartTime - System.currentTimeMillis()

    //If there is time remaining
    if (timeRemaining >= 0) {
      //Remove our behindScheduleAmount from the sleep time
      if (timeRemaining >= behindScheduleAmount) {
        behindScheduleAmount = 0
        Thread.sleep((timeRemaining - behindScheduleAmount).toInt)
        start(logFileReader, gs, ircState, vm, hs,validator)
      }
      else {
        //If we are too far behind schedule, start immediately and reduce our behindSchedule amount
        behindScheduleAmount -= timeRemaining
        start(logFileReader, gs, ircState, vm, hs, validator)
      }
    }
    else {
      //If the thread execution longer than expected, start immediately and add time debt to our behindScheduleAmount
      behindScheduleAmount += timeRemaining
      start(logFileReader, gs, ircState, vm, hs, validator)
    }
  }


  def updateGameState(actionList: ListBuffer[GameStateAction], gs: GameState): Unit = {
    //My way of checking if we are in a game.
    if (gs.friendlyPlayer.playerNumber != Constants.INT_UNINIT) {
      while (actionList.nonEmpty) {
        actionList.head.updateGameState(gs)
        actionList.remove(0)
      }
    }
    else{
      actionList foreach{
        case definePlayers:DefinePlayers=>
          definePlayers.updateGameState(gs)

        case _ =>

      }
    }
  }


  def updateIRCState(actionList: ListBuffer[IRCAction], ircState: IRCState, vm: VoteManager): Unit = {
    actionList.zipWithIndex.foreach {
      case (vote, index) =>
        vote match {
          case action: TurnStart =>
            if (action.playerName == accountName) {
              if (ircState.mulliganComplete) {
                logger.debug("Starting my turn. Clearing all votes")
                action.updateIRC(ircState)
                val turnStarts = actionList.filter {
                  case vote: TurnStart => true
                  case _ => false
                }
                logger.debug(s"Removing ${turnStarts.size} startTurns from ircAction list")
                actionList --= turnStarts
                vm.clearAllVotes()
              }
            }
            else {
              actionList.remove(index)
            }

          case action: TurnEnd =>
            if (action.playerName == accountName) {
              if (ircState.myTurn) {
                logger.debug("Ending my turn. Clearing All Votes")
                action.updateIRC(ircState)
              }
              val endTurns = actionList.filter {
                case vote: EndTurn => true
                case _ => false
              }
              logger.debug(s"Removing ${endTurns.size} endTurns from ircAction list")
              actionList --= endTurns
              vm.clearAllVotes()
            }
            else
              actionList.remove(index)


          case action: MulliganStart =>
            if (!ircState.isMulligan) {
              logger.debug("Starting mulligan. Clearing All Votes.")
              action.updateIRC(ircState)
              val mulliganStarts = actionList.filter {
                case vote: MulliganStart => true
                case _ => false
              }
              logger.debug(s"Removing ${mulliganStarts.size} mulligan starts from ircAction list")
              actionList --= mulliganStarts
              vm.clearAllVotes()
            }

          case action: GameOver =>
            logger.debug("Ending the game")
            action.updateIRC(ircState)
            val gameOvers = actionList.filter {
              case vote: GameOver => true
              case _ => false
            }
            logger.debug(s"Removing ${gameOvers.size} gameovers from ircAction list")
            actionList --= gameOvers

          case action: DiscoverStart =>
            if (!ircState.isDiscover) {
              logger.debug("Starting discover")
              action.updateIRC(ircState)
              val discovers = actionList.filter {
                case vote: DiscoverStart => true
                case _ => false
              }
              logger.debug(s"Removing ${discovers.size} discovers from ircAction list")
              actionList --= discovers
            }

          case action: MulliganOption =>
            if (ircState.isMulligan) {
              logger.debug("Adding mulligan option")
              action.updateIRC(ircState)
              logger.debug(s"Removing mulligan option from ircAction list")
              actionList.remove(actionList.indexOf(action))
            }

          case x =>
            logger.debug("Unexpected IRCAction: " + x)
        }
    }
  }

  def checkDecision(ircState: IRCState, vm: VoteManager): Unit = {

    if (ircState.isMulligan) {
      logger.debug("Mulligan detected")
      if ((ircState.mulliganStartTimeStamp + mulliganDelay) < System.currentTimeMillis()) {
        logger.debug(s"Mulligan time elasped. Adding decision to execution list and ending mulligan. Total of ${ircState.mulliganOptions} mulligan options.")
        val decision = vm.makeMulliganDecision()
        vm.clearActionVotes()
        if (decision != ActionUninit())
          ircState.voteExecutionList.append((decision, (NoCard(), NoCard())))
        else {
          logger.debug("ActionUninit returned from MulliganDecision. Creating blank mulligan vote in order to continue.")
          ircState.voteExecutionList.append((MulliganVote(false, false, false, false), (NoCard(), NoCard())))
        }
      }
    }
    else {
      if (ircState.myTurn) {
        logger.debug("My turn detected. Determining if any votes should be executed.")
        if (ircState.isChooseOne) {
          logger.debug("ChooseOne Detected")
          if ((ircState.chooseOneStartTimeStamp + chooseOneDelay) < System.currentTimeMillis()) {
            logger.debug("ChooseOne time elapsed. Prepending decision to execution list and ending ChooseOne")
            val decision = vm.makeChooseOneDecision()
            vm.clearActionVotes()
            if (decision._1 != ActionUninit())
              ircState.voteExecutionList.prepend(decision)
            else
              logger.debug("ActionUninit returned from ChooseOneDecision. Ending choose one with no action. Take manual action.")
            ircState.isChooseOne = false
          }
        }
        else {
          if (ircState.isDiscover) {
            logger.debug("Discover Detected")
            if ((ircState.discoverStartTimeStamp + discoverDelay) < System.currentTimeMillis()) {
              logger.debug("Discover time elapsed. Prepending decision to execution list and ending Discover")
              val decision = vm.makeDiscoverDecision()
              vm.clearActionVotes()
              if (decision != ActionUninit())
                ircState.voteExecutionList.prepend((decision, (NoCard(), NoCard())))
              else
                logger.debug("ActionUinit returned from DiscoverDecision. Ending discover with no action. Take manual action.")
              ircState.isDiscover = false
            }
          }
          else {
            if ((ircState.turnStartTimeStamp + fullTurnDelay) < System.currentTimeMillis() && ircState.voteExecutionList.isEmpty) {
              logger.debug("Adding decision to execution list because it has been 45 seconds")
              val decision = vm.makeDecision()
              if (decision.exists(_._1 == ActionUninit())) {
                logger.debug("ActionUninit found in the list returned from the main makeDecision method at the end of a full turn. Ending the turn without executing votes.")
                ircState.voteExecutionList.append((EndTurn(), (NoCard(), NoCard())))
              }
              else {
                ircState.voteExecutionList.appendAll(decision)
                ircState.voteExecutionList.append((EndTurn(), (NoCard(), NoCard())))
              }
              vm.clearActionVotes()
            }
            else {
              if ((ircState.lastVoteCheck + 5000) < System.currentTimeMillis()) {
                logger.debug("Checking for endturn and hurrys. Setting lastVoteCheckTimeStamp")
                val decision = vm.makeDecision()
                if (decision.last._1 == EndTurn()) {
                  if (decision.exists(_._1 == ActionUninit())) {
                    logger.debug("ActionUninit found in the list returned from the main makeDecision method when checking for end turn. Ending the turn without executing votes.")
                    ircState.voteExecutionList.append((EndTurn(), (NoCard(), NoCard())))
                  }
                  else {
                    logger.debug("Adding decision to execution list because endturn was detected")
                    ircState.voteExecutionList.appendAll(decision)
                  }
                  vm.clearActionVotes()
                }
                else {
                  if (decision.exists(_._1 == Hurry()) && ircState.voteExecutionList.isEmpty) {
                    if (decision.exists(_._1 == ActionUninit())) {
                      logger.debug("ActionUninit found in the list returned from the main makeDecision method when checking for hurry. This should never happen.")
                    }
                    else {
                      logger.debug("Adding decision to execution list because hurry was detected")
                      ircState.voteExecutionList.appendAll(decision)
                    }
                    vm.clearActionVotes()
                  }
                }
                ircState.lastVoteCheck = System.currentTimeMillis()
              }
              else {
                logger.debug("No reason to make any decision detected")
              }
            }
          }
        }
      }
      else {
        logger.debug("Not My Turn")
      }
    }
  }

  def checkExecution(ircState: IRCState, hs: Hearthstone, idleTimer: Long, vm: VoteManager, gs: GameState, validator:VoteValidator): Unit = {
    if (ircState.voteExecutionList.nonEmpty && (idleTimer + 2000) < System.currentTimeMillis()) {
      logger.debug("Detect valid time to execute next vote")
      ircState.voteExecutionList.head match {
        case (vote, st) =>
          vote match {
            case mulligan: MulliganVote =>
              if (ircState.isMulligan)
                logger.debug(s"Executing mulligan vote. Cards: ${mulligan.first}, ${mulligan.second}, ${mulligan.third}, ${mulligan.fourth}.  Total of ${ircState.mulliganOptions} mulligan options. Clearing votes")
              hs.executeMulligan(mulligan, ircState.mulliganOptions)
              ircState.isMulligan = false
              ircState.mulliganStartTimeStamp = 0.0
              ircState.mulliganOptions = 0
              ircState.mulliganComplete = true
              vm.clearActionVotes()
              ircState.voteExecutionList.remove(0)
              Thread.sleep(1000)

            case discover: Discover =>
              logger.debug(s"Executing discover vote. Card: ${discover.card} Clearing votes.")
              hs.executeDiscover(discover)
              ircState.isDiscover = false
              ircState.discoverStartTimeStamp = 0.0
              vm.clearActionVotes()
              ircState.voteExecutionList.remove(0)
              Thread.sleep(1000)

            case chooseOne: ChooseOne =>
              logger.debug(s"Executing chooseOne. Card: ${chooseOne.card}, Friendly Target: ${chooseOne.friendlyTarget}, Enemy Target: ${chooseOne.enemyTarget} Clearing votes.")
              hs.executeChooseOne(chooseOne)
              vm.clearActionVotes()
              ircState.voteExecutionList.remove(0)
              Thread.sleep(1000)

            case endTurn: EndTurn =>
              logger.debug(s"Executing endTurn.")
              ircState.myTurn = false
              hs.executeActionVote((EndTurn(), (NoCard(), NoCard())))
              vm.clearActionVotes()
              ircState.voteExecutionList.remove(0)
              Thread.sleep(1000)


            case hurry: Hurry =>
              logger.debug(s"Execution has reached the hurry. Discarding.")
              ircState.voteExecutionList.remove(0)
              Thread.sleep(1000)


            case actionVote: ActionVote =>
              ircState.voteExecutionList.head._1 match {
                case updateVotes: UpdateVotes =>
                  vm.updateDecision(updateVotes)
                  ircState.voteExecutionList.remove(0)

                case _ =>

                  if(validator.isValidVote(actionVote, voteEntry = false, voteExecution = true)) {
                    logger.debug(s"Executing action vote: $actionVote. Card: ${actionVote.card}. Friendly Target: ${actionVote.friendlyTarget}. Enemy Target: ${actionVote.enemyTarget}. Position: ${actionVote.position}.")
                    hs.executeActionVote((vote, st))
                    if (gs.isChooseOne(vote, st)) {
                      ircState.startChooseOne()
                    }
                    vm.clearActionVotes()
                    ircState.voteExecutionList.remove(0)
                    ircState.voteExecutionList.prepend((UpdateVotes(gs.friendlyPlayer.copy(), gs.enemyPlayer.copy(), gs), (NoCard(), NoCard())))
                  }
                  else
                    {
                      logger.debug(s"Found invalid vote when trying to execute. Vote: $actionVote.")
                      vm.clearActionVotes()
                      ircState.voteExecutionList.remove(0)
                    }
              }
          }
      }
    }
  }
}