package tph

import FileReaders.CardInfo
import net.liftweb.json.JObject


/**
  * Created by Harambe on 2/20/2017.
  */
trait HSCard{
  def name:String
  def id:Int
  def handPosition:Int
  def boardPosition:Int
  def player:Int
  def cardID:String
  def isDamaged:Boolean
  def isFrozen:Boolean
  def cardInfo:CardInfo
  def isStealthed:Boolean
  def isTaunt:Option[Boolean]
  def isDeathrattle:Option[Boolean]
  def attack:Option[Int]
  def health:Option[Int]
  def enhancementID:Option[String]
}

case class Card(name: String, id: Int, handPosition: Int, boardPosition: Int, player: Int, cardID: String, enhancementID: Option[String] = None, attack:Option[Int] = None, health:Option[Int] = None, isDeathrattle:Option[Boolean] = None, isTaunt:Option[Boolean] = None, isDamaged:Boolean = false, isFrozen:Boolean = false, isStealthed:Boolean = false, cardInfo:CardInfo = Constants.emptyCardInfo) extends HSCard

case class FutureCard() extends HSCard {
  val name:String = Constants.STRING_UNINIT
  val id:Int = Constants.INT_UNINIT
  val handPosition:Int = Constants.INT_UNINIT
  val boardPosition:Int = Constants.INT_UNINIT
  val player:Int = Constants.INT_UNINIT
  val cardID:String = Constants.STRING_UNINIT
  val isDamaged:Boolean = false
  val isFrozen:Boolean = false
  val cardInfo:CardInfo = Constants.emptyCardInfo
  val isStealthed:Boolean = false
  val isTaunt:Option[Boolean] = None
  val isDeathrattle:Option[Boolean] = None
  val health:Option[Int] = None
  val attack:Option[Int] = None
  val enhancementID:Option[String] = None
}

case class NoCard() extends HSCard {
  val name:String = Constants.STRING_UNINIT
  val id:Int = Constants.INT_UNINIT
  val handPosition:Int = Constants.INT_UNINIT
  val boardPosition:Int = Constants.INT_UNINIT
  val player:Int = Constants.INT_UNINIT
  val cardID:String = Constants.STRING_UNINIT
  val isDamaged:Boolean = false
  val isFrozen:Boolean = false
  val cardInfo:CardInfo = Constants.emptyCardInfo
  val isStealthed:Boolean = false
  val isTaunt:Option[Boolean] = None
  val isDeathrattle:Option[Boolean] = None
  val health:Option[Int] = None
  val attack:Option[Int] = None
  val enhancementID:Option[String] = None
}







