package rob

import akka.actor.Actor
import akka.actor.Actor.Receive
import autoitx4java.AutoItX
import com.jacob.com.LibraryLoader
import com.typesafe.config.{ConfigFactory, Config}
import com.typesafe.scalalogging.LazyLogging

object Hearthstone {
  val TITLE = "Hearthstone"
}

class Hearthstone(config:Config = ConfigFactory.load(), scraper: ScreenScraper = new ScreenScraper, clicker:MouseClicker = new MouseClicker()) extends LazyLogging  {
  import Hearthstone._

  val player = config.getString("tph.hearthstone.player")
  val opponent = config.getString("tph.hearthstone.opponent")

  val libFile = ClassLoader.getSystemClassLoader.getResource("jacob-1.18-x64.dll").getFile
  System.setProperty(LibraryLoader.JACOB_DLL_PATH, libFile)
  val ax = new AutoItX()

  def initialize(): Unit = {
    if (!ax.winExists(TITLE))
      throw new RuntimeException("Hearthstone is not running")
    ax.winMove(TITLE, "", 0,0,1884,1080)
  }

  var _isPlaying = false
  def isPlaying:Boolean = _isPlaying

  def resign():Unit = ???

  def message(message: GameLogReader.Message):Unit = {
    message match {
      case GameLogReader.PlayState(entity, value) if entity == player =>
        _isPlaying = (value == "PLAYING")
      case _ =>
    }
  }

  def clickEndTurn(): Unit =  ax.mouseClick("main", 1507, 495)


}
