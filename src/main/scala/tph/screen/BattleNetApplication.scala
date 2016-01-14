package tph.screen

import com.sun.jna.platform.win32.{User32, WinUser}

object BattleNetApplication {
  val POSITION = Rectangle(0,0,1920,1080)
  val HEARTHSTONE_BUTTON = Point(60,420)
  val PLAY_BUTTON = Point(287, 1015)
}

class BattleNetApplication  {
  def position(x: Int, y: Int, width: Int, height: Int): Unit = window.positionWindow(x,y,width,height)


  import BattleNetApplication._

  val CMD = """C:\Program Files (x86)\Battle.net\Battle.net.6526\Battle.net.exe"""

  val window = new WindowedApplication("Battle.net")

  def init() = {
    if (!window.isStarted)
      window.startApplication(CMD)
    window.positionWindow(POSITION)
  }

  def stop() = {
    window.stop()
  }


  def isStarted = window.isStarted

  def position() = window.positionWindow(POSITION)

  def startHearthstone(): Unit = {
    window.assertWindow()
    window.click(HEARTHSTONE_BUTTON)
    window.click(HEARTHSTONE_BUTTON)
    window.click(PLAY_BUTTON)
    window.click(PLAY_BUTTON)

    val hs = new HearthstoneApplication

    while (!hs.isStarted) {
      println("startHearthstone(): waiting for Heartstone to start")
      Thread.sleep(1000)
    }

    hs.init()
  }

  def rectangle = window.rectangle

}
