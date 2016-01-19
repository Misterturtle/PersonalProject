package spikes.jna

import com.sun.jna.platform.win32.{Kernel32, WinDef, User32}
import jna.User32Ext
import org.scalatest.{Matchers, FreeSpec}

/**
  * Created by rconaway on 1/17/16.
  */
class HearthstoneControl extends FreeSpec with Matchers {

  "activate window" in {
    val w = findWindow("Hearthstone")
    Kernel32.INSTANCE.
    User32Ext.USER32EXT.SendMessageA(w, WM_LBUTTONDOWN, STATE_BUTTON_DOWN, pos) != 0)
  }

  def findWindow(title: String):WinDef.HWND = User32.INSTANCE.FindWindow(null, title)

}
