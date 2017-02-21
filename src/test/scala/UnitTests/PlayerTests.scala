package UnitTests

import org.scalatest.{Matchers, FlatSpec}
import tph._
import tph.HSAction.KnownCardDrawn

/**
  * Created by Harambe on 2/20/2017.
  */
class PlayerTests extends FlatSpec with Matchers {

  "A Player" should "Add card to hand" in {
    val player = new Player(1)
    val newCard = new Card("Friendly TesterCard", 10, 5, tph.Constants.INT_UNINIT, 1)
    player.AddCard(newCard, true) shouldEqual new Player(1, List(newCard))

    val player2 = new Player(2)
    val newEnemyCard = new Card("Enemy TesterCard", 10, tph.Constants.INT_UNINIT, 5, 1)
    player2.AddCard(newEnemyCard, true) shouldEqual new Player(2, List(newEnemyCard))
  }

  it should "Add card to board" in {
    val player = new Player(1)
    val newCard = new Card("TesterCard", 10, tph.Constants.INT_UNINIT, 5, 1)
    player.AddCard(newCard, false) shouldEqual new Player(1, List[HSCard](), List(newCard))


    val player2 = new Player(2)
    val newEnemyCard = new Card("Enemy TesterCard", 10, tph.Constants.INT_UNINIT, 5, 2)
    player2.AddCard(newEnemyCard, false) shouldEqual new Player(2, List[HSCard](), List(newEnemyCard))

  }

  it should "Remove card from hand" in {

    val cardBeingRemoved = new Card("Removed Card", 5, 4, tph.Constants.INT_UNINIT, 1)
    val constantCard = new Card("Constants Card", 10, 5, tph.Constants.INT_UNINIT, 1)
    val adjustedCard = new Card("Constants Card", 10, 4, tph.Constants.INT_UNINIT, 1)
    val player = new Player(1, List(cardBeingRemoved, constantCard))

    player.RemoveCard(cardBeingRemoved) shouldEqual new Player(1, List(adjustedCard))


    val p2CardBeingRemoved = new Card("Removed Card", 5, 4, tph.Constants.INT_UNINIT, 2)
    val p2ConstantCard = new Card("Constants Card", 10, 5, tph.Constants.INT_UNINIT, 2)
    val p2AdjustedCard = new Card("Constants Card", 10, 4, tph.Constants.INT_UNINIT, 2)
    val player2 = new Player(2, List(p2CardBeingRemoved, p2ConstantCard))

    player2.RemoveCard(p2CardBeingRemoved) shouldEqual new Player(2, List(p2AdjustedCard))


  }

  it should "Remove card from board" in {
    val cardBeingRemoved = new Card("Removed Card", 5, tph.Constants.INT_UNINIT, 4, 1)
    val constantCard = new Card("Constants Card", 10, tph.Constants.INT_UNINIT, 5, 1)
    val adjustedCard = new Card("Constants Card", 10, tph.Constants.INT_UNINIT, 4, 1)
    val player = new Player(1, List[HSCard](), List(cardBeingRemoved, constantCard))

    player.RemoveCard(cardBeingRemoved) shouldEqual new Player(1, List[HSCard](), List(adjustedCard))


    val p2CardBeingRemoved = new Card("Removed Card", 5, tph.Constants.INT_UNINIT, 4, 2)
    val p2ConstantCard = new Card("Constants Card", 10, tph.Constants.INT_UNINIT, 5, 2)
    val p2AdjustedCard = new Card("Constants Card", 10, tph.Constants.INT_UNINIT, 4, 2)
    val player2 = new Player(2, List[HSCard]() ,List(p2CardBeingRemoved, p2ConstantCard))

    player2.RemoveCard(p2CardBeingRemoved) shouldEqual new Player(2, List[HSCard](), List(p2AdjustedCard))


  }


}