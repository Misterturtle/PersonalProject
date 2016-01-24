package tph.screen

import java.awt.Robot
import java.awt.event.InputEvent

import org.scalatest.{Matchers, FreeSpec}


class PlayWindowSpec extends FreeSpec with Matchers {

  val p = new PlayWindow()


  "try robot" in {
      val robot = new Robot
    robot.setAutoDelay(1000)
      robot.mouseMove(938,988)
      robot.mousePress(InputEvent.BUTTON1_MASK)
      robot.mouseRelease(InputEvent.BUTTON1_MASK)
      robot.mousePress(InputEvent.BUTTON1_MASK)
      robot.mouseRelease(InputEvent.BUTTON1_MASK)
  }

}
