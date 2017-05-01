package tph

import FileReaders.{LogParser, LogFileReader}
import GUI.Display
import GameState.GameState
import Logic.IRCState
import VoteSystem.{VoteValidator, VoteState, VoteAI, VoteManager}

/**
  * Created by Harambe on 4/6/2017.
  */
object Main extends App {


  //---------------------------State dependent classes----------------------------//
  val gs = new GameState()
  val vs = new VoteState()
  val ircState = new IRCState()
  //---------------------------State dependent classes----------------------------//

  val ai = new VoteAI(vs, gs)
  val validator = new VoteValidator(gs)
  val vm = new VoteManager(gs, vs, ai, ircState, validator)
  val hs = new HearthStone(gs)
  val display = new Display()
  val ircBot = new IRCBot(vm)
  val logParser = new LogParser(gs)
  val logFileReader = new LogFileReader(logParser, gs)
  val tb = new TheBrain


  tb.init(logFileReader, ircBot, hs)
  tb.start(logFileReader, gs, ircState, vm, hs, validator, vs, tb.INDEFINITE)




}