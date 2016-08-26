package tph.research

import java.awt.{MouseInfo, Robot}
import java.util.concurrent.TimeUnit

import autoitx4java.AutoItX
import com.jacob.com.LibraryLoader
import com.typesafe.config.ConfigFactory

/**
  * Created by rconaway on 1/1/16.
  */
object PixelGrabber extends App {

  val TITLE = "Hearthstone"
  val libFile = ClassLoader.getSystemClassLoader.getResource("jacob-1.18-x64.dll").getFile
  System.setProperty(LibraryLoader.JACOB_DLL_PATH, libFile)
  val ax = new AutoItX()

  val config = ConfigFactory.load()
  val robot = new Robot()

  //val xOffset = config.getInt("tph.screen-rectangle.x")
  //val yOffset = config.getInt("tph.screen-rectangle.y")
  val xOffset = 0
  val yOffset = 0

  if (!ax.winExists(TITLE))
    throw new RuntimeException("Hearthstone is not running")
  ax.winMove(TITLE, "", 0, 0, 1366, 768)


  while (true) {

    System.in.read()
    TimeUnit.SECONDS.sleep(3)
    val current = MouseInfo.getPointerInfo.getLocation
      val rgb = robot.getPixelColor(current.getX.toInt, current.getY.toInt)
      println(s"{x=${current.getX.toInt - xOffset}, y=${current.getY.toInt - yOffset}, r=${rgb.getRed}, g=${rgb.getGreen}, b=${rgb.getBlue}}")

  }


}

