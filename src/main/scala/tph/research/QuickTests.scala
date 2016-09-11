package tph.research

import scala.collection.mutable

/**
  * Created by RC on 8/27/2016.
  */
object QuickTests extends App {

  case class test()

  case class test2()

  case class test3()

  val map1 = mutable.Map[String, Any]()
  val map2 = mutable.Map[Any, Int]()

  map1("A") = test
  map1("B") = test2
  map1("C") = test

  map2(test) = 5
  map2(test2) = 10
  map2(test3) = 15

  val decision = map2.find(_._2 == 10).get._2

  println(decision)


}
