package spikes.robot

import java.awt.Robot
import java.awt.event.InputEvent
import java.util.ArrayList
import javax.script.{ScriptEngineManager, ScriptEngine}

import com.sun.jna.Platform
import org.scalatest.{Matchers, FreeSpec}
import scala.collection.JavaConversions._

class Click extends FreeSpec with Matchers {

  "Running on a Mac with HearthStone maximized in a separate monitor" - {
    "Get size of hearthstone window" in {
      sizeOfHearthStoneWindow should be(1920, 1080)
    }
    "Get position of hearthstone window" in {
      positionOfHearthStoneWindow should be (1920, 0)
    }
    "Move cursor to middle of hearthstone window" in {
      positionCursorOnScreen(1920 + 960, 0 + 540)
    }
    "Click End Turn button" in {
      primaryClickCursor(1920 + 1550, 0 + 500)
    }
  }

  def sizeOfHearthStoneWindow: (Int, Int) = {
    Platform.isMac should be(true)

    val script =
      """
        |tell application "System Events" to tell application process "Hearthstone"
        |	 tell window 1
        |		 size
        |	 end tell
        |end tell
        |
      """.stripMargin

    val appleScript: ScriptEngine = new ScriptEngineManager().getEngineByName("AppleScriptEngine")
    val size = appleScript.eval(script).asInstanceOf[ArrayList[Long]].toList.map(_.toInt)

    (size(0), size(1))
  }

  def positionOfHearthStoneWindow: (Int, Int) = {
    Platform.isMac should be(true)

    val script =
      """
        |tell application "System Events" to tell application process "Hearthstone"
        |	 tell window 1
        |		 position
        |	 end tell
        |end tell
        |
      """.stripMargin

    val appleScript: ScriptEngine = new ScriptEngineManager().getEngineByName("AppleScriptEngine")
    val position = appleScript.eval(script).asInstanceOf[ArrayList[Long]].toList.map(_.toInt)

    (position(0), position(1))
  }

  def positionCursorOnScreen(x:Int, y:Int): Unit = {
    val robot = new Robot
    robot.mouseMove(x,y)
  }

  def primaryClickCursor(x:Int, y:Int) = {
    val robot = new Robot
    robot.mouseMove(x,y)
    robot.mousePress(InputEvent.BUTTON1_MASK)
    robot.mouseRelease(InputEvent.BUTTON1_MASK)
    robot.mousePress(InputEvent.BUTTON1_MASK)
    robot.mouseRelease(InputEvent.BUTTON1_MASK)
  }
}
