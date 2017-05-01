package GameState

import tph.Constants

/**
  * Created by Harambe on 4/26/2017.
  */

trait Entity extends Cloneable{
  private var secretID = 0

  def SetID(newID:Int):Entity = {
    val clone = super.clone().asInstanceOf[Entity]
    clone.secretID = newID
    clone
  }




  val tagList:List[TagChange]
  val id:Option[Int]
  def addTag(tag:TagChange):Entity = copyEntity(newTagList = tag :: tagList)
  def setID(newID:Int):Entity = copyEntity(id = Some(newID))
  def copyEntity(id:Option[Int] = id, newTagList:List[TagChange] = tagList):Entity
  def updateGame(game:Game): Game
}

case class GameEntity(id:Option[Int] = None, tagList:List[TagChange] = Nil) extends Entity{
  override def setID(newID:Int): GameEntity = copy(id = Some(newID))
  override def copyEntity(id:Option[Int] = id, newTagList:List[TagChange]):Entity = copy(tagList = newTagList)
  override def updateGame(game:Game): Game = game.copy(gameEntity = this)

}

case class Deck(){}

case class PlayerEntity(tagList:List[TagChange] = Nil) extends Entity{
  //todo: Correctly implement methods.
  override val id: Option[Int] = Some(Constants.INT_UNINIT)
  override def updateGame(game: Game): Game = game
  override def copyEntity(id: Option[Int], newTagList: List[TagChange]): Entity = this
}

case class Game(gameEntity:GameEntity, deck:Deck, friendlyPlayer:PlayerEntity, enemyPlayer:PlayerEntity)

case class TagChange(tag:String, value:Any){
  def addTag(entity:Entity): Unit = entity.addTag(this)
}
