package tph


import autoitx4java.AutoItX
import com.jacob.com.LibraryLoader
import org.scalatest.{FreeSpec, Matchers}


class ScreenScraper extends FreeSpec with Matchers {
  val libFile = ClassLoader.getSystemClassLoader.getResource("jacob-1.18-x64.dll").getFile
  System.setProperty(LibraryLoader.JACOB_DLL_PATH, libFile)
  val ax = new AutoItX()


  def GetScreen(): Unit = {

  }




}
