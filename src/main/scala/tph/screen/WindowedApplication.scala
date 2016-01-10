package tph.screen

import java.awt.Rectangle
import java.io.File

import com.sun.jna.Pointer
import com.sun.jna.platform.win32.WinBase.{PROCESS_INFORMATION, STARTUPINFO}
import com.sun.jna.platform.win32._
import com.sun.jna.platform.win32.WinDef.{DWORD, HWND}
import com.typesafe.scalalogging.StrictLogging
import jna.User32Ext

/**
  * Created by rob on 1/8/2016.
  */
class WindowedApplication(title: String) extends StrictLogging {

  val WM_LBUTTONDOWN = 0x201
  val WM_LBUTTONUP = 0x202
  val STATE_BUTTON_DOWN = 1.asInstanceOf[Byte]
  val STATE_BUTTON_UP = 0.asInstanceOf[Byte]
  val HWND_TOP: WinDef.HWND = new WinDef.HWND(new Pointer(0))

  var window: Option[HWND] = findWindow

  def isStarted = {
    if (window.isEmpty)
      window = findWindow
    window.isDefined
  }

  def startApplication(cmd: String): Boolean = {
    if(createProcess(cmd)) {
      window = findWindow
    } else {
      window = None
      logger.error("Could not start application {} - {}", title, cmd)
    }

    isStarted
  }

  def click(x:Int, y:Int):Unit = {
    assertWindow()
    val pos = (y << 16) | x
    User32Ext.USER32EXT.SendMessageA(window.get, WM_LBUTTONDOWN, STATE_BUTTON_DOWN, pos)
    User32Ext.USER32EXT.SendMessageA(window.get, WM_LBUTTONUP, STATE_BUTTON_UP, pos)
  }

  def positionWindow(x:Int, y:Int, width:Int, height:Int):Unit = {
    assertWindow()
    User32.INSTANCE.SetWindowPos(window.get, HWND_TOP, x,y,width,height, 0)
  }

  private def findWindow = Option(User32.INSTANCE.FindWindow(null, title))

  private def createProcess(cmd: String): Boolean = {
    val processInformation = new WinBase.PROCESS_INFORMATION.ByReference();
    val startupInfo = new STARTUPINFO();
    Kernel32.INSTANCE.CreateProcess(
      null,
      cmd,
      null,
      null,
      true,
      new DWORD(0x00000020), //new DWORD(0x00000001)
      null,
      null,
      startupInfo,
      processInformation)
  }

  private def assertWindow():Unit = if (window.isEmpty) throw new RuntimeException(s"Application has not been started: $title")
}
