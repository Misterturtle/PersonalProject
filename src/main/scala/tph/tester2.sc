case class Test(id:String){
  var testVal = 1
}


val test1 = Test("thisisatest")
test1.testVal = 2

val test2 = Test("thisisatest")

test1 == test2

test1.testVal