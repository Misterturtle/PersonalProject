package GUI

import java.io.{PrintWriter, FileWriter, File}
import java.util.concurrent.{TimeUnit, ScheduledThreadPoolExecutor}
import javafx.animation.{PauseTransition, KeyValue, KeyFrame, Timeline}
import javafx.concurrent.Task
import javafx.event.{ActionEvent, EventHandler}
import javafx.util.Duration

import GameState.GameState
import Logic.IRCState
import VoteSystem.{VoteValidator, VoteManager, VoteAI, VoteState}
import com.typesafe.config.ConfigFactory
import tph.{IRCBot, HearthStone, TheBrain}
import FileReaders.{LogFileReader, LogParser}

import scalafx.application.{Platform, JFXApp}
import scalafx.scene.Scene
import scalafxml.core.{FXMLView, NoDependencyResolver, DependenciesByType, FXMLLoader}
import javafx.{scene => jfxs}
import scalafx.Includes._

/**
  * Created by Harambe on 2/22/2017.
  */
object MainDebug extends JFXApp {

  val config = ConfigFactory.load()

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

  val controller = new Controller(gs, logFileReader, ircBot, hs, ircState, vm, tb)


  stage = new JFXApp.PrimaryStage() {
    title = "TPH Debug"
    scene = new Scene(controller.root, 1000, 800)
  }
}