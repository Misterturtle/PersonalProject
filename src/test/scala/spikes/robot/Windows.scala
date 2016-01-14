package spikes.robot

import java.util.ArrayList
import javax.script.{ScriptEngine, ScriptEngineManager}

import com.sun.jna.Platform
import org.scalatest.{FreeSpec, Matchers}

/**
  * Created by rconaway on 12/29/15.
  */
class Windows extends FreeSpec with Matchers {

  "Get mac window titles" in {
    if(Platform.isMac()) {

      val script =
        """
          |tell application "System Events" to tell application process "Hearthstone"
          |	 tell window 1
          |		 size
          |	 end tell
          |end tell
          |
        """.stripMargin

      val appleScript:ScriptEngine=new ScriptEngineManager().getEngineByName("AppleScriptEngine")
      appleScript should not be (null)

      val result=appleScript.eval(script).asInstanceOf[ArrayList[String]]
      print(result)
    }
  }

}

object Script extends App {
  if(Platform.isMac()) {

    val script =
      """
        |tell application "System Events" to tell application process "Hearthstone"
        |	 tell window 1
        |		 position
        |	 end tell
        |end tell
        |
      """.stripMargin

    val appleScript:ScriptEngine=new ScriptEngineManager().getEngineByName("AppleScriptEngine")

    val result=appleScript.eval(script).asInstanceOf[ArrayList[String]]
    print(result)
  }

}