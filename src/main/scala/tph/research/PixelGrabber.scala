package tph.research

import java.awt.{Robot, Point, MouseInfo, PointerInfo}

import com.typesafe.config.ConfigFactory

/**
  * Created by rconaway on 1/1/16.
  */
object PixelGrabber extends App {

  val config = ConfigFactory.load()
  val robot = new Robot()

  val xOffset = config.getInt("tph.screen-rectangle.x")
  val yOffset = config.getInt("tph.screen-rectangle.y")

  while (true) {
    System.in.read()
    val current = MouseInfo.getPointerInfo.getLocation
      val rgb = robot.getPixelColor(current.getX.toInt, current.getY.toInt)
      println(s"{x=${current.getX.toInt - xOffset}, y=${current.getY.toInt - yOffset}, r=${rgb.getRed}, g=${rgb.getGreen}, b=${rgb.getBlue}}")
  }

}
