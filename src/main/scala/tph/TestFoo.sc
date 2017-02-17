val list = List(1, 3, 9, 5, 11, 13)

list.find(_ == 3)

list.reverse

list.foldLeft(0)((a, b) => a + b)

list.foldLeft(1)((a, b) => a * b)

Range(5, 10)

Range(5, 10).map(_ * 2)

Seq("a", "c", "b")

Seq("alpha", "beta", "gamma").map(_.toUpperCase())

list.drop(3)

list.dropWhile(_ < 6)

list.foreach(print _)

list.span(_ < 6)

list.splitAt(3)

list.takeRight(2)

list.take(2)

var accum = List[Int]()

def times2(x: Int) = {
  accum = x :: accum
  x * 2
}

Stream(1, 2, 3, 4, 5, 6, 7, 8, 9).map(times2).toList

accum





