package tph

/**
  * Created by Harambe on 2/20/2017.
  */
abstract class HSCard {
  def name: String
  def id: Int
  def handPosition: Int
  def boardPosition: Int
  def player: Int
}

case class Card(name: String, id: Int, handPosition: Int, boardPosition: Int, player: Int) extends HSCard

case class NoCards() extends HSCard {
  def name = Constants.STRING_UNINIT
  def id = Constants.INT_UNINIT
  def handPosition = Constants.INT_UNINIT
  def boardPosition = Constants.INT_UNINIT
  def player = Constants.INT_UNINIT
}