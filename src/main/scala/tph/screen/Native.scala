package tph.screen

import com.sun.jna.Pointer
import com.sun.jna.platform.win32.WinBase.STARTUPINFO
import com.sun.jna.platform.win32._
import com.sun.jna.platform.win32.WinDef.{RECT, DWORD, LRESULT, HWND}
import com.sun.jna.ptr.IntByReference
import jna.User32Ext
import sun.java2d.loops.ProcessPath.ProcessHandler

object Native {
  type WindowHandle = Option[HWND]

  val WM_LBUTTONDOWN = 0x201
  val WM_LBUTTONUP = 0x202
  val WM_MOUSEACTIVATE = 0x0021

  val STATE_BUTTON_DOWN = 1.asInstanceOf[Byte]
  val STATE_BUTTON_UP = 0.asInstanceOf[Byte]
  val HWND_TOP: WinDef.HWND = new WinDef.HWND(new Pointer(0))

  val PROCESS_QUERY_INFORMATION = 0x0400
  val PROCESS_VM_READ = 0x0010
  val PROCESS_VM_WRITE = 0x0020
  val PROCESS_VM_OPERATION = 0x0008

  val STILL_ACTIVE = 259
}

class Native {

  import Native._

  def findWindow(title: String): WindowHandle = Option(User32.INSTANCE.FindWindow(null, title))

  def closeWindow(wh: WindowHandle): Boolean = {
    val timeout = System.currentTimeMillis() + 3000L

    while (System.currentTimeMillis < timeout) {
      if (User32Ext.USER32EXT.SendMessageA(wh.get, WinUser.WM_CLOSE, 0, null) == 0)
        return true
    }
    false
  }

  def createProcess(cmd: String):Boolean =
    Kernel32.INSTANCE.CreateProcess(
      null,
      cmd,
      null,
      null,
      true,
      new DWORD(0x00000020), //new DWORD(0x00000001)
      null,
      null,
      new STARTUPINFO(),
      new WinBase.PROCESS_INFORMATION.ByReference())

  def leftButtonClick(wh:WindowHandle, x:Int, y:Int) = {
    val pos = (y << 16) | x
    if (User32Ext.USER32EXT.SendMessageA(wh.get, WM_LBUTTONDOWN, STATE_BUTTON_DOWN, pos) != 0)
      throw new RuntimeException("Could not push button")
    if (User32Ext.USER32EXT.SendMessageA(wh.get, WM_LBUTTONUP, STATE_BUTTON_UP, pos) != 0)
      throw new RuntimeException("Could not release button")
  }

  def setWindowPos(wh:WindowHandle, x:Int, y:Int, width:Int, height:Int):Boolean =
    User32.INSTANCE.MoveWindow(wh.get, x,y,width, height, true) == 0
  //User32.INSTANCE.SetWindowPos(wh.get, HWND_TOP, x,y,width,height, 0)

  def getWindowRect(wh:WindowHandle):Rectangle = {
    var rect = new RECT
    User32.INSTANCE.GetWindowRect(wh.get, rect)
    new Rectangle(rect.left, rect.top, rect.right - rect.left, rect.bottom - rect.top)
  }

}
