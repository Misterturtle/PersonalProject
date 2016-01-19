package tph.screen

import org.scalatest.{BeforeAndAfterEach, FreeSpec, Matchers}

/**
  * Created by rob on 1/11/2016.
  */
class NativeSpec extends FreeSpec with Matchers with BeforeAndAfterEach {

  val NOTEPAD_TITLE = "Untitled - Paint"
  val NOTEPAD_CMD = """c:\windows\System32\mspaint.exe"""

  val native = new Native

  override def afterEach(): Unit = {
    closeWindowIfOpen()
  }

  "createProcess" - {

    "creates calc process" in {
      closeWindowIfOpen()

      native.createProcess(NOTEPAD_CMD) shouldBe true
      waitOpen(NOTEPAD_TITLE)

      native.findWindow(NOTEPAD_TITLE) shouldBe 'defined
    }

  }

  "findWindow" - {
    "finds a window if it exists" in {
      openWindowIfClosed()

      native.findWindow(NOTEPAD_TITLE) shouldBe 'defined
    }

    "does not find a window if it does not exist" in {
      closeWindowIfOpen()

      native.findWindow(NOTEPAD_TITLE) shouldBe 'empty
    }
  }

  "closeWindow" - {
    "closes an open window" in {
      openWindowIfClosed()

      native.closeWindow(native.findWindow(NOTEPAD_TITLE))
      waitClose(NOTEPAD_TITLE)

      native.findWindow(NOTEPAD_TITLE) shouldBe 'empty
    }
  }

  "setWindowPos" - {
    "sets window position and size" in {
      openWindowIfClosed()

      val wh = native.findWindow(NOTEPAD_TITLE)
      native.setWindowPos(wh, 10, 20, 500, 400)
      native.getWindowRect(wh) shouldBe Rectangle(10,20,500,400)
      native.setWindowPos(wh, 100, 200, 600, 500)
      native.getWindowRect(wh) shouldBe Rectangle(100,200,600,500)

    }
  }

  "clickWindow" - {
    "closes window by clicking close button" in {
//      openWindowIfClosed()

      val wh = native.findWindow("Battle.net")
      native.setWindowPos(wh, 0,0,1000,800)
      native.leftButtonClick(wh, 291, 732)
      native.leftButtonClick(wh, 291, 732)
      waitClose(NOTEPAD_TITLE)

      native.findWindow(NOTEPAD_TITLE) shouldBe 'empty
    }
  }



  def waitOpen(title: String): Unit = {
    val timeout = System.currentTimeMillis() + 30000L
    while (native.findWindow(title).isEmpty && System.currentTimeMillis() < timeout)
      Thread.sleep(100)

    println(native.findWindow(title))
  }

  def waitClose(title: String): Unit = {
    val timeout = System.currentTimeMillis() + 30000L
    while (native.findWindow(title).isDefined && System.currentTimeMillis() < timeout)
      Thread.sleep(100)
  }

  def closeWindowIfOpen(): Unit = {
    val wh = native.findWindow(NOTEPAD_TITLE)
    if (wh.isDefined) {
      if (!native.closeWindow(wh))
        throw new RuntimeException("Could not close window")
    }
    waitClose(NOTEPAD_TITLE)
  }

  def openWindowIfClosed(): Unit = {
    if (native.findWindow(NOTEPAD_TITLE).isEmpty) {
      native.createProcess(NOTEPAD_CMD)
    }
    waitOpen(NOTEPAD_TITLE)
  }


}
