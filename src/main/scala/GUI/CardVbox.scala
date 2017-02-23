package GUI

import javafx.beans.property.{SimpleIntegerProperty, SimpleStringProperty}

import tph.{HSCard, Card}

import scalafx.beans.property.Property
import scalafx.scene.control.Label
import scalafx.scene.layout.{Priority, VBox}

/**
  * Created by Harambe on 2/22/2017.
  */
class CardVbox(firstCard:HSCard) extends VBox {

  this.hgrow = Priority.Always

  var card = firstCard
  var cardName = "Name: "+ card.name
  var cardID = "ID: "+ card.id
  var cardHandPosition ="Hand Position: "+ card.handPosition
  var cardBoardPosition = "Board Position "+ card.boardPosition

  val nameLabel = new Label(cardName)
  val idLabel = new Label(cardID)
  val handPositionLabel = new Label(cardHandPosition)
  val boardPositionLabel = new Label(cardBoardPosition)

  children.addAll(nameLabel, idLabel, handPositionLabel, boardPositionLabel)


  def UpdateCard(newCard:HSCard) = {
    card = newCard
    nameLabel.setText("Name: "+ card.name)
    idLabel.setText("ID: " + card.id)
    handPositionLabel.setText("Hand Position: " + card.handPosition)
    boardPositionLabel.setText("Board Position: "+ card.boardPosition)
  }
}
