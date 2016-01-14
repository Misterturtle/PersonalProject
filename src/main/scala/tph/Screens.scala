package tph

import com.typesafe.config.{ConfigObject, ConfigValue, Config}
import collection.JavaConversions._

/**
  * Created by rconaway on 1/1/16.
  */
class Screens(config:Config) {

  val screens:Map[String,Screen] = {
    val ss = config.getConfig("tph.screens")
    ss.root.map { case (k:String, v:ConfigObject) =>
      k -> new Screen(k, v.toConfig)
    }.toMap
  }

  def require(name:String): Screen = screens(name)

}


class Screen(name:String, screenConfig:Config) {

  val pixelMap:Map[String, Pixel] = {
    val c = screenConfig.getConfig("pixels")
    c.root.map { case (k:String, v:ConfigObject) =>
      val name = k
      val p = v.toConfig
      val pixel = Pixel(p.getInt("x"), p.getInt("y"), p.getInt("r"), p.getInt("g"), p.getInt("b"))
      name -> pixel
    }.toMap
  }

  val pixels =  pixelMap.values.toList.sortBy(p => (p.x, p.y))

  val hotspots:Map[String,Hotspot] = {
    val h = screenConfig.getConfig("hotspots")
    h.root.map { case (k:String, v:ConfigObject) =>
      val name = k
      val h = v.toConfig
      val hotspot = Hotspot(h.getInt("x"), h.getInt("y"))
      name -> hotspot
    }.toMap
  }

}

case class Pixel(x:Int,y:Int, r:Int, g:Int, b:Int)

case class Hotspot(x:Int, y:Int)