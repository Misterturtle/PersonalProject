package tph

/**
  * Created by Harambe on 1/24/2017.
  */
class FrozenPlayer(player: Player) {

  val tuple4 = player.deepCopy()
  val hand = tuple4._1.asInstanceOf[Array[Card]]
  val board = tuple4._2.asInstanceOf[Array[Card]]
  val playerValue = tuple4._3.asInstanceOf[Int]
  val playerFaceValue = tuple4._4.asInstanceOf[Int]

}
