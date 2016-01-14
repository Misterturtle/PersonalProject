package tph.screen

object HearthstoneApplication {
  val POSITION = Rectangle(0,0,1920,1080)
}

class HearthstoneApplication {
  import tph.screen.HearthstoneApplication._

  val window = new WindowedApplication("Hearthstone")

  def init() = {
    window.assertWindow()
    window.positionWindow(POSITION)
  }

  def isStarted = window.isStarted
}
