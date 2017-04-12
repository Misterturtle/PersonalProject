package Logic

import java.util.concurrent.{TimeUnit, Executors, ScheduledExecutorService, ScheduledFuture}

import FileReaders.HSAction.HSAction
import GUI.Display
import VoteSystem.VoteManager
import tph.Constants.Vote
import tph.{HSCard, GameState, Constants}
import tph.Constants.ActionVotes._

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  * Created by Harambe on 2/23/2017.
  */



class IRCState() {

  var mulliganOptions = 0

  var isChooseOne = false
  var isDiscover = false
  var isMulligan = false
  var mulliganComplete = false

  var myTurn = false

  var turnStartTimeStamp = 0.0
  var mulliganStartTimeStamp = 0.0
  var discoverStartTimeStamp = 0.0
  var chooseOneStartTimeStamp = 0.0

  var voteExecutionList = ListBuffer[(ActionVote, (HSCard, HSCard))]()
  var lastVoteCheck = System.currentTimeMillis()

  def startMulligan(): Unit = {
    isMulligan = true
    mulliganStartTimeStamp = System.currentTimeMillis()
    mulliganOptions = 0
  }

  def startTurn(): Unit ={
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
    turnStartTimeStamp = 0.0
    mulliganStartTimeStamp = 0.0
    discoverStartTimeStamp = 0.0
    chooseOneStartTimeStamp = 0.0
  }

  def endTurn(): Unit = {
    isChooseOne = false
    isDiscover = false
    isMulligan = false
    myTurn = false
    turnStartTimeStamp = 0.0
    chooseOneStartTimeStamp = 0.0
    discoverStartTimeStamp = 0.0
    mulliganStartTimeStamp = 0.0
  }

}
