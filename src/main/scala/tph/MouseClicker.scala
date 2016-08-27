package tph

import java.io.{File, InputStream}

import autoitx4java.AutoItX
import com.jacob.com.LibraryLoader
import org.apache.commons.io.{FileUtils, IOUtils}


/**
  * Created by rconaway on 1/30/16.
  */
class MouseClicker {

  //val libFile = ClassLoader.getSystemClassLoader.getResource("jacob-1.18-x64.dll").getFile
  val input: InputStream = getClass.getResourceAsStream("/jacob-1.18-x64.dll")
  val fileOut = new File(System.getProperty("java.io.tmpdir") + "/jacob/MouseClicker/jacob-1.18-x64.dll")
  println("Writing dll to: " + fileOut.getAbsolutePath())
  val out = FileUtils.openOutputStream(fileOut)
  IOUtils.copy(input, out)
  input.close()
  out.close()

  System.setProperty(LibraryLoader.JACOB_DLL_PATH, fileOut.getPath)
  val ax = new AutoItX()

  // click
  def Click(position: (Int, Int)): Unit = {
    ax.mouseClick("", position._1, position._2, 1, 500)
  }

  def RightClick(position: (Int, Int)): Unit = {
    ax.mouseClick("right", position._1, position._2, 1, 500)
  }
}
