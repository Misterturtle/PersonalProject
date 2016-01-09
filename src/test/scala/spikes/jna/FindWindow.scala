package spikes.jna

import com.sun.jna.platform.win32.WinBase.STARTUPINFO
import com.sun.jna.platform.win32.WinDef.DWORD
import com.sun.jna.platform.win32._
import jna.User32Ext
import org.scalatest.{FreeSpec, Matchers}

/**
  * Created by rob on 1/8/2016.
  */
class FindWindow extends FreeSpec with Matchers {

  import com.sun.jna._

  val HWND_TOP: WinDef.HWND = new WinDef.HWND(new Pointer(0))

  "go" in {

  }


  "start battle.net window if necessary" in {
    var hWnd = User32.INSTANCE.FindWindow(null, "Battle.net")
    if (hWnd == null) {

      println("Need to start it")
      val cmd = """C:\Program Files (x86)\Battle.net\Battle.net.6526\Battle.net.exe"""
      val processInformation = new WinBase.PROCESS_INFORMATION.ByReference();
      val startupInfo = new STARTUPINFO();

      // Create the child process.
      if (!Kernel32.INSTANCE.CreateProcess(
        null,
        cmd,
        null,
        null,
        true,
        new DWORD(0x00000020), //new DWORD(0x00000001)
        null,
        null,
        startupInfo,
        processInformation))
        throw new RuntimeException("Could not start Battle.net")

      var hWnd = User32.INSTANCE.FindWindow(null, "Battle.net")
      if (hWnd == null)
        throw new RuntimeException("Battle.net window did not show up")

    }

    User32.INSTANCE.SetWindowPos(hWnd, HWND_TOP, 10, 10, 1510, 910, 0 )
  }

  "select hearthstone in battle.net" in {
    val WM_LBUTTONDOWN = 0x201
    val WM_LBUTTONUP = 0x202

    var hWnd = User32.INSTANCE.FindWindow(null, "Battle.net")
    hWnd should not be(null)

//    User32.INSTANCE.ShowWindow(hWnd, WinUser.SW_NORMAL)
//    User32.INSTANCE.SetFocus(hWnd)
    Thread.sleep(1000)

    var pos:Int = (423 << 16) | 61
    val one:Byte = 1
    val zero:Byte = 0
    User32Ext.USER32EXT.SendMessageA(hWnd, WM_LBUTTONDOWN, one, pos)
    User32Ext.USER32EXT.SendMessageA(hWnd, WM_LBUTTONUP, zero, pos)
    User32Ext.USER32EXT.SendMessageA(hWnd, WM_LBUTTONDOWN, one, pos)
    User32Ext.USER32EXT.SendMessageA(hWnd, WM_LBUTTONUP, zero, pos)

    pos = (843 << 16) | 284
    User32Ext.USER32EXT.SendMessageA(hWnd, WM_LBUTTONDOWN, one, pos)
    User32Ext.USER32EXT.SendMessageA(hWnd, WM_LBUTTONUP, zero, pos)
    User32Ext.USER32EXT.SendMessageA(hWnd, WM_LBUTTONDOWN, one, pos)
    User32Ext.USER32EXT.SendMessageA(hWnd, WM_LBUTTONUP, zero, pos)
  }


  "start hearthstone in battle.net" in {
    val WM_LBUTTONDOWN = 0x201
    val WM_LBUTTONUP = 0x202

    var hWnd = User32.INSTANCE.FindWindow(null, "Battle.net")
    hWnd should not be(null)

    User32.INSTANCE.ShowWindow(hWnd, WinUser.SW_NORMAL)

    val pos:Int = (843 << 16) | 284
    val one:Byte = 1
    val zero:Byte = 0
    User32Ext.USER32EXT.SendMessageA(hWnd, WM_LBUTTONDOWN, one, pos)
    User32Ext.USER32EXT.SendMessageA(hWnd, WM_LBUTTONUP, zero, pos)
    User32Ext.USER32EXT.SendMessageA(hWnd, WM_LBUTTONDOWN, one, pos)
    User32Ext.USER32EXT.SendMessageA(hWnd, WM_LBUTTONUP, zero, pos)
  }

}
