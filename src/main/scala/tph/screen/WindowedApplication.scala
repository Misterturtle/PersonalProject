package tph.screen

import com.sun.jna.Pointer
import com.sun.jna.platform.win32._
import com.typesafe.scalalogging.StrictLogging
import tph.screen.Native.WindowHandle

/**
  * Created by rob on 1/8/2016.
  */
class WindowedApplication(title: String) extends StrictLogging {

  val native = new Native
  var window: WindowHandle = native.findWindow(title)

  def isStarted = {
    if (window.isEmpty)
      window = native.findWindow(title)
    window.isDefined
  }

  def startApplication(cmd: String): Boolean = {
    if(native.createProcess(cmd)) {
      waitOpen(title)
      window = native.findWindow(title)
    } else {
      window = None
      logger.error("Could not start application {} - {}", title, cmd)
    }

    isStarted
  }

  def waitOpen(title:String):Unit = {
    val timeout = System.currentTimeMillis() + 30000L
    while (native.findWindow(title).isEmpty && System.currentTimeMillis() < timeout)
      Thread.sleep(100)
  }

  def click(point:Point):Unit = click(point.x, point.y)

  def click(x:Int, y:Int):Unit = {
    assertWindow()
    native.leftButtonClick(window, x, y)
  }


  def positionWindow(where:Rectangle):Unit = positionWindow(where.x,where.y,where.width,where.height)

  def positionWindow(x:Int, y:Int, width:Int, height:Int):Unit = {
    assertWindow()
    native.setWindowPos(window, x, y, width, height)
  }

  def stop():Unit = {
    assertWindow()
    val pid = processId

    native.closeWindow(window)

    while(native.findWindow(title).isDefined) {
      println(s"stop(): waiting for window $title to go away" )
      Thread.sleep(1000)
    }
    while(processActive(pid)) {
      println(s"stop(): waiting for pid $pid for $title to go away")
      Thread.sleep(100)
    }

    window = None
  }

  def rectangle:Rectangle = {
    assertWindow()
    native.getWindowRect(window)
  }

  def processId:Int = {
    assertWindow()
    native.getWindowThreadProcesId(window)
  }

  def processActive(processId:Int): Boolean = native.getExitCodeProcess(window) == 259

  def assertWindow():Unit = if (!window.isDefined) throw new RuntimeException(s"Application has not been started: $title")
}
