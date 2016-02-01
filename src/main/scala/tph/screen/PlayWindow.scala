package tph.screen

import org.scalatest.Matchers

class PlayWindow extends Matchers {

  val native = new Native()
  val wh = native.findWindow("Hearthstone")
  wh shouldBe 'defined
  native.setWindowPos(wh,0, 0, 1842,1080)
  val rect = native.getWindowRect(wh)

  def clickConfirm():Unit = {
    native.leftButtonClick(wh,1503,514)
    native.leftButtonClick(wh,1503,514)
  }

}
