package tph

import com.typesafe.config.ConfigFactory
import org.scalatest.{FreeSpec, Matchers}

/**
  * Created by rconaway on 1/1/16.
  */
class ScreensSpec extends FreeSpec with Matchers {

  val config = ConfigFactory.load()
  val screens = new Screens(config)

  "screens should have all the screens" in {
    screens.require("quests")
    screens.require("play-mode-menu")
    screens.require("practice-level-selection")
  }

  "quests should have all the pixels" in {
    val quests = screens.require("quests")
    quests.pixelMap("upper-left")
    quests.pixelMap("lower-right")
  }

  "play-mode-menu should have all the pixels" in {
    val playModeMenu = screens.require("play-mode-menu")
    playModeMenu.pixels should be(
      List(
        Pixel(910, 788, 129, 77, 22),
        Pixel(925, 69, 123, 69, 23),
        Pixel(960, 70, 21, 13, 9),
        Pixel(961, 785, 24, 15, 3),
        Pixel(1003, 66, 144, 85, 26),
        Pixel(1007, 782, 129, 78, 23)
      )
    )
  }

  "play-mode-menu should have all the hotspots" in {
    val playModeMenu = screens.require("play-mode-menu")
    playModeMenu.hotspots.get("solo-adventures")
  }

}
