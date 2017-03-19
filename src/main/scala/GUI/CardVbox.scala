package GUI

import javafx.beans.property.{SimpleIntegerProperty, SimpleStringProperty}

import tph.{Constants, Card}

import scalafx.beans.property.Property
import scalafx.scene.control.Label
import scalafx.scene.layout.{Priority, VBox}

/**
  * Created by Harambe on 2/22/2017.
  */
class CardVbox() extends VBox {

  this.hgrow = Priority.Always

  val nameLabel = new Label("Name: ")
  val idLabel = new Label("ID: ")
  val handPositionLabel = new Label("Hand Position: ")
  val boardPositionLabel = new Label("Board Position: ")
  val cardIDLabel = new Label("Card ID: ")

  children.addAll(nameLabel, idLabel, handPositionLabel, boardPositionLabel, cardIDLabel)


  def UpdateCard(newCard:Card) = {
    nameLabel.setText("Name: " + newCard.name)
    idLabel.setText("ID: " + newCard.id)
    handPositionLabel.setText("Hand Position: "+ newCard.handPosition)
    boardPositionLabel.setText("Board Position: "+ newCard.boardPosition)
    cardIDLabel.setText("Card ID: " + newCard.cardID)
  }
}
