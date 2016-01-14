import scala.collection.mutable.ListBuffer

case class Variables(x:Int,y:Int,p:Int)

val v0 = Variables(0,0,-1)
val v1 = Variables(100,200,30)
val v2 = Variables(100,200,300)
val v3 = Variables(100, 500, 42)
var results = -1
var temp = -1

var ml = ListBuffer(v0, v1,v2, v3)


ml.indexWhere(_.y == 2000) match{
  case x => results = x

}

results

if (results >= 0){ temp =results +1}

temp