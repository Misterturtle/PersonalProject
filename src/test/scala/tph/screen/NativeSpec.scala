package tph.screen

import org.scalatest.{FreeSpec, Matchers}

/**
  * Created by rob on 1/11/2016.
  */
class NativeSpec extends FreeSpec with Matchers {

  val CALCULATOR_TITLE = "Calculator"
  val CALCULATOR_CMD = """c:\windows\System32\calc.exe"""

  val native = new Native

  "createProcess" - {

    "creates calc process" in {
      closeCalculatorIfOpen()

      native.createProcess(CALCULATOR_CMD) shouldBe true
      waitOpen(CALCULATOR_TITLE)

      native.findWindow(CALCULATOR_TITLE) shouldBe 'defined
    }

  }

  "findWindow" - {
    "finds a window if it exists" in {
      openCalculatorIfClosed()

      native.findWindow(CALCULATOR_TITLE) shouldBe 'defined
    }

    "does not find a window if it does not exist" in {
      closeCalculatorIfOpen()

      native.findWindow(CALCULATOR_TITLE) shouldBe 'empty
    }
  }

  "closeWindow" - {
    "closes an open window" in {
      openCalculatorIfClosed()

      native.closeWindow(native.findWindow(CALCULATOR_TITLE))
      waitClose(CALCULATOR_TITLE)

      native.findWindow(CALCULATOR_TITLE) shouldBe 'empty
    }
  }

  def waitOpen(title:String):Unit = {
    val timeout = System.currentTimeMillis() + 30000L
    while (native.findWindow(title).isEmpty && System.currentTimeMillis() < timeout)
      Thread.sleep(100)
  }

  def waitClose(title:String):Unit = {
    val timeout = System.currentTimeMillis() + 30000L
    while (native.findWindow(title).isDefined && System.currentTimeMillis() < timeout)
      Thread.sleep(100)
  }

  def closeCalculatorIfOpen(): Unit = {
    val wh = native.findWindow(CALCULATOR_TITLE)
    if (wh.isDefined) {
      val rc = native.closeWindow(wh)
      if (rc != 0)
        throw new RuntimeException("Could not close window, rc = " + rc)
    }
    waitClose(CALCULATOR_TITLE)
  }

  def openCalculatorIfClosed(): Unit = {
    if (native.findWindow(CALCULATOR_TITLE).isEmpty) {
      native.createProcess(CALCULATOR_CMD)
    }
    waitOpen(CALCULATOR_TITLE)
  }


}
