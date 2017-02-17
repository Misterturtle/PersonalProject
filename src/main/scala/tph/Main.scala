package tph


import javafx.scene

import tph.GUI.GUIDebugInterface
import tph.tests.TestBrain

import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafxml.core.{FXMLLoader, NoDependencyResolver, FXMLView}
import scalafx.Includes._


/**
  * Created by rconaway on 2/12/16.
  */
object Main extends JFXApp {

  val testMode = false


  if (testMode) {
    val testBrain = new TestBrain(testMode)
    testBrain.Init()
  }
  else {
    val theBrain = new TheBrain(testMode)
    theBrain.ChangeState(theBrain.initState)
  }

}