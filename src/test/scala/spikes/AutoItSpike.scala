package spikes

import autoitx4java.AutoItX
import com.jacob.com.LibraryLoader
import org.scalatest.{FreeSpec, Matchers}

import scala.collection.mutable.ListBuffer


/**
  * Created by rconaway on 1/22/16.
  */


class AutoItSpike extends FreeSpec with Matchers {
  val libFile = ClassLoader.getSystemClassLoader.getResource("jacob-1.18-x64.dll").getFile
  println(s"Loading DLL from $libFile")
  System.setProperty(LibraryLoader.JACOB_DLL_PATH, libFile)
  val ax = new AutoItX()

  "AutoIt" in {
    startHearthstone()
    selectTodaysQuests()
    selectSoloAdventures()
    selectPractice()
    selectDeck()
    selectConfirm()
//    selectCard3of5()
//    selectCardOf7(5)
    endTurn()
  }

  "colorAt" in {
    startHearthstone()
    waitForEndTurn()
    println(colorAt(1509, 495))
  }

  "colorAt of region" in {
    val start = System.currentTimeMillis()
    (1 to 10).foreach ( x => colorAt(0, 0, 5, 5))
    println(System.currentTimeMillis() - start)
  }

  def startBattleNet(): Unit = {
    if (!ax.winExists("Battle.net")) {
      ax.run("""C:\Program Files (x86)\Battle.net\Battle.net.6526\Battle.net.exe""", """C:\Program Files (x86)\Battle.net\Battle.net.6526""", AutoItX.SW_MAXIMIZE)
    }
    ax.winWait("Battle.net")

    ax.winActivate("Battle.net")
    ax.winMove("Battle.net", "", 0, 0, 1024, 768)


  }

  def startHearthstone(): Unit = {
    if (!ax.winExists("Hearthstone")) {
      startBattleNet()
      ax.mouseClick("main", 284, 703)
      ax.winWait("Hearthstone")
    }

    ax.winMove("Hearthstone", "", 0, 0, 1884, 1080)
  }

  def colorAt(x: Int, y: Int): String = {
    Integer.toHexString(ax.pixelGetColor(x, y).toInt).toUpperCase()
  }

  def colorAt(left:Int, top:Int, width:Int, height:Int):Region = {
    val buffer = ListBuffer[Int]()
    for (x <- left until left + width; y <- top until top + height) {
      buffer += ax.pixelGetColor(x,y).toInt
    }
    Region(left, top, width, height, buffer.toList)
  }


  def selectTodaysQuests(): Unit = {
    waitFor(922, 515, "312CE7")
    ax.mouseClick("main", 933, 517)
  }

  def selectSoloAdventures(): Unit = {
    ax.mouseMove(882,417)
    waitFor(882,417, "FFFFCF")
    ax.mouseClick("main", 882,417)
  }

  def selectPractice(): Unit = {
    waitFor(647, 237, "AA311B")
    ax.mouseClick("main", 1347, 887)
  }


  def selectDeck(): Unit = {
    waitFor(453, 399, "FFDF63")
    ax.mouseClick("main", 1362, 880)
    waitFor(1384, 214, "42454B")
    ax.mouseClick("main", 1370, 220)
    Thread.sleep(1000)
    ax.mouseMove(1378,871)
    ax.mouseClick("main", 1378, 871)
  }

  def selectConfirm(): Unit = {
    waitFor(954, 242, "FDD129")
    ax.mouseClick("main", 944, 860)
  }

  def waitFor(x: Int, y: Int, color: String): Unit = {
    val timeout = System.currentTimeMillis() + 30000
    while (System.currentTimeMillis < timeout) {
      val pixel = colorAt(x, y)
      if (pixel == color)
        return
      ax.toolTip(pixel)
      Thread.sleep(100)
    }
    throw new RuntimeException(s"Timed out waiting for pixel $color")
  }

  def waitForEndTurn(): Unit = {
    val timeout = System.currentTimeMillis() + 30000
    while (System.currentTimeMillis < timeout) {
      val pixel = colorAt(1509, 495)
      if (pixel == "26D306"  || pixel == "E2D904")
        return
      ax.toolTip(pixel)
      Thread.sleep(100)
    }
  }

  def endTurn(): Unit = {
    waitForEndTurn()
    ax.mouseClick("main", 1507, 495)
  }

  def click(x: Int, y: Int): Unit = {
    ax.mouseClick("main", x, y)
  }

  def selectCard5of6(): Unit = {
    waitForEndTurn()
    ax.mouseClick("main", 1018, 1025)
    ax.mouseClick("main", 963, 524)
  }

  def selectCard3Of5(): Unit = {
    waitForEndTurn()
    ax.mouseClick("main", 890, 1002)
    ax.mouseClick("main", 963, 524)
  }

  def selectCardOf7(slot: Int):Unit = {
    click(654 + (slot - 1) * 76, 1062)
  }

  def placeCardInField():Unit = {
    click(963, 524)
  }


  case class Region(x:Int, y:Int, width:Int, height:Int, pixels:List[Int])

}
