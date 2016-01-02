package tph.research

import java.awt.{Robot, Point, MouseInfo, PointerInfo}

/**
  * Created by rconaway on 1/1/16.
  */
object PixelGrabber extends App {

  val robot = new Robot()

  while (true) {
    System.in.read()
    val current = MouseInfo.getPointerInfo.getLocation
      val rgb = robot.getPixelColor(current.getX.toInt, current.getY.toInt)
      println(s"{x=${current.getX.toInt - 1920}, y=${current.getY.toInt}, r=${rgb.getRed}, g=${rgb.getGreen}, b=${rgb.getBlue}}")
  }

}
