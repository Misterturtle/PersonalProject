package tph.screen

import com.sun.jna.Pointer
import com.sun.jna.platform.win32.WinBase.STARTUPINFO
import com.sun.jna.platform.win32._
import com.sun.jna.platform.win32.WinDef.{RECT, DWORD, LRESULT, HWND}
import com.sun.jna.ptr.IntByReference
import jna.User32Ext

object Native {
  type WindowHandle = Option[HWND]

  val WM_LBUTTONDOWN = 0x201
  val WM_LBUTTONUP = 0x202
  val STATE_BUTTON_DOWN = 1.asInstanceOf[Byte]
  val STATE_BUTTON_UP = 0.asInstanceOf[Byte]
  val HWND_TOP: WinDef.HWND = new WinDef.HWND(new Pointer(0))
}

class Native {

  import Native._

  def findWindow(title: String): WindowHandle = Option(User32.INSTANCE.FindWindow(null, title))

  def closeWindow(wh: WindowHandle): Int = User32Ext.USER32EXT.SendMessageA(wh.get, WinUser.WM_CLOSE, 0, null)

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
    User32Ext.USER32EXT.SendMessageA(wh.get, WM_LBUTTONDOWN, STATE_BUTTON_DOWN, pos)
    User32Ext.USER32EXT.SendMessageA(wh.get, WM_LBUTTONUP, STATE_BUTTON_UP, pos)
  }

  def setWindowPos(wh:WindowHandle, x:Int, y:Int, width:Int, height:Int):Boolean = User32.INSTANCE.SetWindowPos(wh.get, HWND_TOP, x,y,width,height, 0)

  def getWindowRect(wh:WindowHandle):Rectangle = {
    var rect = new RECT
    User32.INSTANCE.GetWindowRect(wh.get, rect)
    new Rectangle(rect.left, rect.top, rect.right - rect.left, rect.bottom - rect.top)
  }

  def getWindowThreadProcesId(wh:WindowHandle):Int = {
    val intRef:IntByReference = new IntByReference(0)
    User32.INSTANCE.GetWindowThreadProcessId(wh.get, intRef)
    intRef.getValue
  }

  def getExitCodeProcess(wh:WindowHandle):Int = {
    val intRef:IntByReference = new IntByReference(0)
    Kernel32.INSTANCE.GetExitCodeProcess(wh.get, intRef)
    intRef.getValue
  }

}
