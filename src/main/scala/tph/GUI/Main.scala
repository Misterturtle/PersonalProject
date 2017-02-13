package tph.GUI

import javafx.scene
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.event.ActionEvent
import scalafx.scene
import scalafx.scene.{Scene, Parent}
import scalafxml.core.{FXMLView, NoDependencyResolver, FXMLLoader}
import javafx.{scene => jfxs}
import scalafx.Includes._

import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafxml.core.{FXMLLoader, NoDependencyResolver, FXMLView}

/**
  * Created by Harambe on 2/12/2017.
  */
object Main extends JFXApp {
  //val root = FXMLView(getClass.getResource("/GUIDebugWindow.fxml"), NoDependencyResolver)


  val loader = new FXMLLoader(getClass.getResource("/GUIDebugWindow.fxml"), NoDependencyResolver)
  loader.load()

  val rootInstance = loader.getRoot[jfxs.Parent]
  //println(s"root is $root")
  //println("root class is " + root.getClass())


  val controller = loader.getController[GUIDebugInterface]
  println("controller is " + controller)


  stage = new JFXApp.PrimaryStage() {

    title = "Hay Day Calculator"
    //    scene = new Scene(root)
  }

  val backEndMain = new tph.Main()

}
