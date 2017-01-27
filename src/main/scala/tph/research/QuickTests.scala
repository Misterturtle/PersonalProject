package tph.research

import java.io.BufferedReader
import java.util.Scanner

import tph.Constants.ActionVoteCodes.Greetings

import scala.collection.mutable
import tph.{Constants, Controller}


/**
  * Created by RC on 8/27/2016.
  */


object QuickTests extends App {


  val test = new QuickTests

}

class QuickTests() {

  case class Test(first: Int, second: Int, third: Int)

  val test1 = new Test(1, 1, 1)
  val test2 = new Test(2, 2, 2)


  if (test1 == test2) {
    println("They are the same")
  }

  if (test1 != test2) {
    println("They are different")
  }

}

