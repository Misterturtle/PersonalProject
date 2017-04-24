import scala.collection.mutable.ListBuffer

val lb = ListBuffer[Int](1,2,3,2,4,2,5,6)

lb.foreach{
  case x =>
    val twosRemaining = lb.filter(_ == 2).size
    println(s"Current value is $x and there are $twosRemaining 2's remaining")
    if(x == 4) {
      val twos = lb.filter(_ == 2)
      lb --= twos
    }
}