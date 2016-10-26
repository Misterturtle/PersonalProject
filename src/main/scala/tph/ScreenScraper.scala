package tph


import java.io.{File, InputStream}

import autoitx4java.AutoItX
import com.jacob.com.LibraryLoader
import org.apache.commons.io._
import org.scalatest.{FreeSpec, Matchers}


class ScreenScraper extends FreeSpec with Matchers {
  // val libFile = ClassLoader.getSystemClassLoader.getResource("jacob-1.18-x64.dll").getFile

  val input: InputStream = getClass.getResourceAsStream("/jacob-1.18-x64.dll")
  val fileOut = new File(System.getProperty("java.io.tmpdir") + "/jacob/ScreenScraper/jacob-1.18-x64.dll")
  println("Writing dll to: " + fileOut.getAbsolutePath())
  val out = FileUtils.openOutputStream(fileOut)
  IOUtils.copy(input, out)
  input.close()
  out.close()

  System.setProperty(LibraryLoader.JACOB_DLL_PATH, fileOut.getPath)
  val ax = new AutoItX()


  def GetScreen(): Unit = {

  }

  def Get1(): Unit =
  {

  }
  def Get2(): Unit =
  {

  }
  def Get3(): Unit =
  {

  }
  def Get4(): Unit =
  {

  }




}
