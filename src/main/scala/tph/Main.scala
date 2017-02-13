package tph


import tph.tests.TestBrain


/**
  * Created by rconaway on 2/12/16.
  */
class Main() extends {

  val testMode = false


  if (testMode) {
    val testBrain = new TestBrain(testMode)
    testBrain.Init()
  }
  else {
    val theBrain = new TheBrain(testMode)
    theBrain.ChangeState(theBrain.initState)
  }







}