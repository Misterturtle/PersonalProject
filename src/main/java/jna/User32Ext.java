package jna;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.win32.W32APIOptions;

public interface User32Ext extends User32 {
    User32Ext USER32EXT = (User32Ext) Native.loadLibrary("user32",

            User32Ext.class, W32APIOptions.DEFAULT_OPTIONS);

    WinDef.HWND FindWindowEx(WinDef.HWND lpParent, WinDef.HWND lpChild, String lpClassName,
                             String lpWindowName);

    WinDef.HWND GetTopWindow(WinDef.HWND hwnd);

    WinDef.HWND GetParent(WinDef.HWND hwnd);

    WinDef.HWND GetDesktopWindow();

    int SendMessageA(WinDef.HWND hWnd, int dwFlags, byte bVk, int dwExtraInfo);

    int SendMessageA(WinDef.HWND hWnd, int Msg, int wParam, String lParam);

    void keybd_event(byte bVk, byte bScan, int dwFlags, int dwExtraInfo);

    void SwitchToThisWindow(WinDef.HWND hWnd, boolean fAltTab);

}