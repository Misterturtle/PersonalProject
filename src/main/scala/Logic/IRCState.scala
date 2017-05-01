package Logic

import java.util.concurrent.{TimeUnit, Executors, ScheduledExecutorService, ScheduledFuture}

import FileReaders.HSAction._
import FileReaders.LogFileReader
import GUI.Display
import GameState.GameState
import VoteSystem.{VoteValidator, VoteManager}
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import tph.Constants.Vote
import tph._
import tph.Constants.ActionVotes._

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  * Created by Harambe on 2/23/2017.
  */


class IRCState() extends LazyLogging {

  val config = ConfigFactory.load()
  val accountName = config.getString("tph.hearthstone.accountName")

  var mulliganOptions = 0

  var isChooseOne = false
  var isDiscover = false
  var isMulligan = false
  var mulliganComplete = false

  var myTurn = false

  var turnStartTimeStamp = 0L
  var mulliganStartTimeStamp = 0L
  var discoverStartTimeStamp = 0L
  var chooseOneStartTimeStamp = 0L

  val fullTurnDelay = 60000
  val mulliganDelay = 25000
  val discoverDelay = 8000
  val chooseOneDelay = 5000

  var voteExecutionList: ListBuffer[(ActionVote, (HSCard, HSCard))] = ListBuffer[(ActionVote, (HSCard, HSCard))]()
  var lastVoteCheck: Long = System.currentTimeMillis()

  def startMulligan(): Unit = {
    isMulligan = true
    mulliganStartTimeStamp = System.currentTimeMillis()
    mulliganOptions = 0
  }

  def startMyTurn(): Unit = {
    myTurn = true
    turnStartTimeStamp = System.currentTimeMillis()
  }

  def startDiscover(): Unit = {
    isDiscover = true
    discoverStartTimeStamp = System.currentTimeMillis()
  }

  def startChooseOne(): Unit = {
    isChooseOne = true
    chooseOneStartTimeStamp = System.currentTimeMillis()
  }


  def gameOver(): Unit = {
    isDiscover = false
    isChooseOne = false
    isMulligan = false
    myTurn = false
    mulliganComplete = false
    mulliganOptions = 0
    turnStartTimeStamp = 0
    mulliganStartTimeStamp = 0
    discoverStartTimeStamp = 0
    chooseOneStartTimeStamp = 0
  }

  def endMyTurn(): Unit = {
    isChooseOne = false
    isDiscover = false
    isMulligan = false
    myTurn = false
    turnStartTimeStamp = 0
    chooseOneStartTimeStamp = 0
    discoverStartTimeStamp = 0
    mulliganStartTimeStamp = 0
  }

  def update(actionList: ListBuffer[IRCAction], vm: VoteManager): Unit = {
    val newActionList = actionList.foldLeft(actionList) {
      case (r, c) =>
        c match {
          case action: TurnStart =>
            if (action.playerName == accountName) {
              if (mulliganComplete) {
                logger.debug("Starting my turn. Clearing all votes")
                action.updateIRC(this)

                vm.clearAllVotes()

                val turnStarts = r.filter {
                  case vote: TurnStart => true
                  case _ => false
                }
                logger.debug(s"Removing ${turnStarts.size} startTurns from ircAction list")
                r diff turnStarts
              }
              else
                r
            }
            else {
              r diff List(c)
            }

          case action: MulliganStart =>
            if (!isMulligan) {
              logger.debug("Starting mulligan. Clearing All Votes.")
              action.updateIRC(this)
              vm.clearAllVotes()
              val mulliganStarts = r.filter {
                case vote: MulliganStart => true
                case _ => false
              }
              logger.debug(s"Removing ${mulliganStarts.size} mulligan starts from ircAction list")
              r diff mulliganStarts
            }
            else
              r

          case action: GameOver =>
            logger.debug("Ending the game and clearing all votes")
            action.updateIRC(this)
            vm.clearAllVotes()
            val gameOvers = r.filter {
              case vote: GameOver => true
              case _ => false
            }
            logger.debug(s"Removing ${gameOvers.size} gameovers from ircAction list")
            r diff gameOvers

          case action: DiscoverStart =>
            if (!isDiscover) {
              logger.debug("Starting discover")
              action.updateIRC(this)
              vm.clearAllVotes()
              val discovers = r.filter {
                case vote: DiscoverStart => true
                case _ => false
              }
              logger.debug(s"Removing ${discovers.size} discovers from ircAction list")
              r diff discovers
            }
            else
              r

          case action: MulliganOption =>
            if (isMulligan) {
              logger.debug("Adding mulligan option")
              action.updateIRC(this)
              logger.debug(s"Removing mulligan option from ircAction list")
              r diff List(c)
            }
            else
              r
        }
    }.toList
    actionList.clear()
    actionList.appendAll(newActionList)
  }


  def checkDecision(vm: VoteManager): Unit = {

    if (isMulligan) {
      logger.debug("Mulligan detected")
      if ((mulliganStartTimeStamp + mulliganDelay) < System.currentTimeMillis()) {
        logger.debug(s"Mulligan time elasped. Adding decision to execution list and ending mulligan. Total of ${mulliganOptions} mulligan options.")
        val decision = vm.makeMulliganDecision()
        vm.clearActionVotes()
        if (decision != ActionUninit())
          voteExecutionList.append((decision, (NoCard(), NoCard())))
        else {
          logger.debug("ActionUninit returned from MulliganDecision. Creating blank mulligan vote in order to continue.")
          voteExecutionList.append((MulliganVote(false, false, false, false), (NoCard(), NoCard())))
        }
      }
    }
    else {
      if (myTurn) {
        logger.debug("My turn detected. Determining if any votes should be executed.")
        if (isChooseOne) {
          logger.debug("ChooseOne Detected")
          if ((chooseOneStartTimeStamp + chooseOneDelay) < System.currentTimeMillis()) {
            logger.debug("ChooseOne time elapsed. Prepending decision to execution list and ending ChooseOne")
            val decision = vm.makeChooseOneDecision()
            vm.clearActionVotes()
            if (decision._1 != ActionUninit())
              voteExecutionList.prepend(decision)
            else {
              logger.debug("ActionUninit returned from ChooseOneDecision. Creating ChooseOne(1) and prepending to list to continue.")
              voteExecutionList.prepend((ChooseOne(1), (NoCard(), NoCard())))
            }
          }
        }
        else {
          if (isDiscover) {
            logger.debug("Discover Detected")
            if ((discoverStartTimeStamp + discoverDelay) < System.currentTimeMillis()) {
              logger.debug("Discover time elapsed. Prepending decision to execution list and ending Discover")
              val decision = vm.makeDiscoverDecision()
              vm.clearActionVotes()
              if (decision != ActionUninit())
                voteExecutionList.prepend((decision, (NoCard(), NoCard())))
              else {
                logger.debug("ActionUninit returned from DiscoverDecision. Creating Discover(1) and prepending to list to continue.")
                voteExecutionList.prepend((Discover(1), (NoCard(), NoCard())))
              }
            }
          }
          else {
            if ((turnStartTimeStamp + fullTurnDelay) < System.currentTimeMillis() && voteExecutionList.isEmpty) {
              logger.debug("Adding decision to execution list because it has been 45 seconds")
              val decision = vm.makeDecision()
              if (decision.exists(_._1 == ActionUninit())) {
                logger.debug("ActionUninit found in the list returned from the main makeDecision method at the end of a full turn. Ending the turn without executing votes.")
                voteExecutionList.append((EndTurn(), (NoCard(), NoCard())))
              }
              else {
                voteExecutionList.appendAll(decision)
                voteExecutionList.append((EndTurn(), (NoCard(), NoCard())))
              }
              vm.clearActionVotes()
            }
            else {
              if ((lastVoteCheck + 5000) < System.currentTimeMillis()) {
                logger.debug("Checking for endturn and hurrys. Setting lastVoteCheckTimeStamp")
                val decision = vm.makeDecision()
                if (decision.last._1 == EndTurn()) {
                  logger.debug("Adding decision to execution list because endturn was detected")
                  voteExecutionList.appendAll(decision)
                  vm.clearActionVotes()
                }
                else {
                  if (decision.exists(_._1 == Hurry()) && voteExecutionList.isEmpty) {
                    logger.debug("Adding decision to execution list because hurry was detected")
                    voteExecutionList.appendAll(decision)
                    vm.clearActionVotes()
                  }
                }
                lastVoteCheck = System.currentTimeMillis()
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


  def checkExecution(hs: HearthStone, idleTimer: Long, vm: VoteManager, gs: GameState, validator: VoteValidator): Unit = {
    if (voteExecutionList.nonEmpty && (idleTimer + 2000) < System.currentTimeMillis()) {
      logger.debug("Detect valid time to execute next vote")
      voteExecutionList.head match {
        case (vote, st) =>
          vote match {
            case mulligan: MulliganVote =>
              logger.debug(s"Executing mulligan vote. Cards: ${mulligan.first}, ${mulligan.second}, ${mulligan.third}, ${mulligan.fourth}.  Total of ${mulliganOptions} mulligan options. Clearing votes")
              hs.executeMulligan(mulligan, mulliganOptions)
              isMulligan = false
              mulliganStartTimeStamp = 0
              mulliganOptions = 0
              mulliganComplete = true
              vm.clearActionVotes()
              voteExecutionList.remove(0)

            case discover: Discover =>
              logger.debug(s"Executing discover vote. Card: ${discover.card} Clearing votes.")
              hs.executeDiscover(discover)
              isDiscover = false
              discoverStartTimeStamp = 0
              vm.clearActionVotes()
              voteExecutionList.remove(0)

            case chooseOne: ChooseOne =>
              logger.debug(s"Executing chooseOne. Card: ${chooseOne.card}, Friendly Target: ${chooseOne.friendlyTarget}, Enemy Target: ${chooseOne.enemyTarget} Clearing votes.")
              hs.executeChooseOne(chooseOne)
              isChooseOne = false
              chooseOneStartTimeStamp = 0
              vm.clearActionVotes()
              voteExecutionList.remove(0)

            case endTurn: EndTurn =>
              logger.debug(s"Executing endTurn.")
              hs.executeActionVote((EndTurn(), (NoCard(), NoCard())))
              vm.clearActionVotes()
              voteExecutionList.remove(0)


            case hurry: Hurry =>
              logger.debug(s"Execution has reached the hurry. Discarding.")
              voteExecutionList.remove(0)

            case updateVotes: UpdateVotes =>
              vm.updateDecision(updateVotes)
              voteExecutionList.remove(0)


            case actionVote: ActionVote =>
              if (validator.isValidVote(actionVote, voteEntry = false, voteExecution = true)) {
                logger.debug(s"Executing action vote: $actionVote. Card: ${actionVote.card}. Friendly Target: ${actionVote.friendlyTarget}. Enemy Target: ${actionVote.enemyTarget}. Position: ${actionVote.position}.")
                hs.executeActionVote((vote, st))
                if (gs.isChooseOne(vote, st)) {
                  startChooseOne()
                }
                vm.clearActionVotes()
                voteExecutionList.remove(0)
                voteExecutionList.prepend((UpdateVotes(gs.friendlyPlayer.copy(), gs.enemyPlayer.copy(), gs), (NoCard(), NoCard())))
              }
              else {
                logger.debug(s"Found invalid vote when trying to execute. Vote: $actionVote.")
                vm.clearActionVotes()
                voteExecutionList.remove(0)
              }
          }
      }
    }
  }
}
