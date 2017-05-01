import FileReaders.HSAction.IRCAction
import FileReaders.{LogParser, LogFileReader}
import GameState.GameState
import Logic.IRCState
import VoteSystem._
import org.scalatest.{Matchers, FreeSpec}
import tph.{HearthStone, IRCBot, TheBrain}

import scala.collection.mutable.ListBuffer

/**
  * Created by Harambe on 4/22/2017.
  */
class TheBrainTests extends FreeSpec with Matchers {

  val gs = new GameState
  val lp = new LogParser(gs)
  val lfr = new LogFileReader(lp,gs)
  val vp = new VoteParser()
  val vs = new VoteState()
  val ai = new VoteAI(vs,gs)
  val ircState = new IRCState
  val validator = new VoteValidator(gs)
  val vm = new VoteManager(gs, vs, ai, ircState, validator)
  val ircBot = new IRCBot(vm, vp)
  val hs = new HearthStone(gs)

  "When the brain inits, it should run lfrInit, ircBotInit, and hsInit" in {

      var lfrInit = false
      var ircBotInit = false
      var hsInit = false
      val tb = new TheBrain
      val lfr = new LogFileReader(lp,gs){
        override def init() = {
          lfrInit = true
        }
      }
      val ircBot = new IRCBot(vm,vp){
        override def init(): Unit = {
          ircBotInit = true
        }
      }
      val hs = new HearthStone(gs){
        override def init(): Unit = {
          hsInit = true
        }
      }


      tb.init(lfr,ircBot,hs)


      lfrInit shouldBe true
      ircBotInit shouldBe true
      hsInit shouldBe true
  }


  "When the brain runs the start method, it should" - {
    val gs = new GameState()
    val lfr = new LogFileReader(lp, gs){
      override def read(): Unit = {}
      override def updateGameState():Unit = {}}
    val ircState = new IRCState(){
      override def update(actionList: ListBuffer[IRCAction], vm: VoteManager): Unit = {}
      override def checkDecision(vm: VoteManager): Unit = {}
      override def checkExecution(hs: HearthStone, idleTimer: Long, vm: VoteManager, gs: GameState, validator: VoteValidator): Unit = {}
    }
    val vm = new VoteManager(gs,vs,ai,ircState,validator)



    "time stamp the loop begin time" in {
      val tb = new TheBrain()
      val loopDelay = 1000


      tb.start(lfr, gs, ircState, vm, hs, validator, vs, 1)


      (tb.threadLoopStartTimeStamp + loopDelay) shouldBe <= (System.currentTimeMillis())
      (tb.threadLoopStartTimeStamp + loopDelay) shouldBe > (System.currentTimeMillis() - 100)
    }

    "Tell the logFileReader to read the log" in {
      var logFileRead = false
      val lfr = new LogFileReader(lp,gs){
        override def updateGameState():Unit = {}
        override def read(): Unit = {
          logFileRead = true
        }
      }
      val tb = new TheBrain


      tb.start(lfr,gs,ircState,vm,hs,validator, vs, 1)


      logFileRead shouldBe true
    }


    "Tell the logFileReader to update the GameState" in {
      var gameStateUpdated = false
      val gs = new GameState()
      val lfr = new LogFileReader(lp,gs){
        override def read():Unit = {}
        override def updateGameState(): Unit ={
          gameStateUpdated = true
        }
      }
      val tb = new TheBrain


      tb.start(lfr,gs,ircState,vm,hs,validator, vs, 1)


      gameStateUpdated shouldBe true
    }


    "Tell IRCState to update" in {
      var ircStateUpdated = false
      val ircState = new IRCState(){
        override def update(actionList: ListBuffer[IRCAction], vm: VoteManager): Unit = {
          ircStateUpdated = true
        }
        override def checkDecision(vm: VoteManager): Unit = {}
        override def checkExecution(hs: HearthStone, idleTimer: Long, vm: VoteManager, gs: GameState, validator: VoteValidator): Unit = {}
      }
      val tb = new TheBrain


      tb.start(lfr,gs,ircState,vm,hs,validator, vs, 1)


      ircStateUpdated shouldBe true
    }


    "Tell IRCState to checkDecision" in {

      var decisionChecked = false
      val ircState = new IRCState(){
        override def update(actionList: ListBuffer[IRCAction], vm: VoteManager): Unit = {}
        override def checkDecision(vm: VoteManager): Unit = {
          decisionChecked = true
        }
        override def checkExecution(hs: HearthStone, idleTimer: Long, vm: VoteManager, gs: GameState, validator: VoteValidator): Unit = {}
      }
      val tb = new TheBrain


      tb.start(lfr,gs,ircState,vm,hs,validator, vs, 1)


      decisionChecked = true
    }



    "Tell IRCState to checkExecution" in {
      var executionChecked = false
      val ircState = new IRCState(){
        override def update(actionList: ListBuffer[IRCAction], vm: VoteManager): Unit = {}
        override def checkDecision(vm: VoteManager): Unit = {}
        override def checkExecution(hs: HearthStone, idleTimer: Long, vm: VoteManager, gs: GameState, validator: VoteValidator): Unit = {
          executionChecked = true
        }
      }
      val tb = new TheBrain


      tb.start(lfr,gs,ircState,vm,hs,validator, vs, 1)


      executionChecked = true
    }



    "Loop the correct number of times" in {
      var loopCounter = 0
      val tb = new TheBrain
      val lfr = new LogFileReader(lp, gs){
        override def read(): Unit = {
          loopCounter += 1
        }
        override def updateGameState():Unit = {}}


      tb.start(lfr,gs,ircState,vm,hs,validator, vs, 3)


      loopCounter shouldBe 3
    }
  }





}
