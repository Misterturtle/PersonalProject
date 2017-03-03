package GUI

import java.util.concurrent.{TimeUnit, ScheduledThreadPoolExecutor}
import javafx.concurrent.Task

import tph.TheBrain
import FileReaders.{LogFileReader, LogParser}

import scalafx.application.{Platform, JFXApp}
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.layout.AnchorPane
import scalafxml.core.{FXMLView, NoDependencyResolver, DependenciesByType, FXMLLoader}
import javafx.{scene => jfxs}
import scalafx.Includes._

/**
  * Created by Harambe on 2/22/2017.
  */
object Main extends JFXApp {

  val controller = new Controller()
  val theBrain = new TheBrain()
  //Reads hearthstone's output_log.txt file and filters known HSActions to actionLog.txt
  val logFileReader = new LogFileReader()
  logFileReader.poll()

  val scheduler = new ScheduledThreadPoolExecutor(1)
  val poll = new Runnable{
    def run() = Poll()
  }

  val updateGUI = new Runnable{
    def run() = controller.UpdateGUIWindow()
  }

  stage = new JFXApp.PrimaryStage() {
    title = "TPH Debug"
    scene = new Scene(controller.root, 1000, 800)
  }

  def Poll(): Unit ={

    Platform.runLater(updateGUI)
    scheduler.schedule(poll, 1, TimeUnit.SECONDS)
  }
  Poll()




}