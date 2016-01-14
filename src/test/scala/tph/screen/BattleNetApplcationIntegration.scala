package tph.screen

import org.scalatest.{FreeSpec, FlatSpec, Matchers}

class BattleNetApplcationIntegration extends FreeSpec with Matchers {

  "Battle.net will start and position if not started" in {
    val w = new BattleNetApplication
    if (w.isStarted)
      w.stop()

    w.init()
    w should be ('started)

    w.rectangle should be (BattleNetApplication.POSITION)
  }

  "Battle.net will position window if already started" in {
    val w = new BattleNetApplication

    if (!w.isStarted)
      w.init()

    w.position(100,100, 200, 200)
    w.rectangle should not be BattleNetApplication.POSITION

    w.init()

    w.rectangle shouldBe BattleNetApplication.POSITION
  }

  "Hearthstone can be started and positioned from scratch" in {
    val hs = new WindowedApplication("Hearthstone")
    if (hs.isStarted)
      hs.stop()
    hs should not be 'started

    val w = new BattleNetApplication
    if (w.isStarted)
      w.stop()
    w should not be 'started

    w.init()

    hs should not be 'started
    w.startHearthstone()
    hs shouldBe 'started
    hs.rectangle shouldBe HearthstoneApplication.POSITION
    println(s"hs.rectangle = ${hs.rectangle}")
  }

}
