package tph

import autoitx4java.AutoItX
import com.jacob.com.LibraryLoader


/**
  * Created by rconaway on 1/30/16.
  */
class MouseClicker {

  val libFile = ClassLoader.getSystemClassLoader.getResource("jacob-1.18-x64.dll").getFile
  System.setProperty(LibraryLoader.JACOB_DLL_PATH, libFile)
  val ax = new AutoItX()

  // click
  def Click(position: (Int, Int)): Unit = {
    ax.mouseClick("", position._1, position._2, 1, 500)
  }

  def RightClick(position: (Int, Int)): Unit = {
    ax.mouseClick("right", position._1, position._2, 1, 500)
  }
}
