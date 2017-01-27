package tph

import java.io.File
import java.util.concurrent.TimeUnit

import akka.actor.{ActorRef, ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import tph.IrcMessages.ChangeMenu
import tph.tests.TestBrain

/**
  * Created by rconaway on 2/12/16.
  */
object Main extends App {

  val testMode = false
  val theBrain = new TheBrain()
  val testBrain = new TestBrain()


  theBrain.Init(testMode)



}