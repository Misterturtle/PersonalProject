val testList = List("foo", "boo", "who", "you")


def IdentifyString(string: String): Int ={
  string match {
    case "foo" => 1
    case "who" => 2
    case _ => 0

  }

}


testList.map(f => f match{
  case "foo" => 1
  case _ => 0


})


