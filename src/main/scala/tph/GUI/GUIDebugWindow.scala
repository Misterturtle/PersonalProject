package tph.GUI

import tph.TheBrain

import scalafx.scene.control.{MenuItem, MenuButton}
import scalafx.scene.layout.{HBox, FlowPane}
import scalafxml.core.macros.sfxml

/**
  * Created by Harambe on 2/12/2017.
  */
trait GUIDebugInterface {


}

@sfxml
class GUIDebugWindow(private val enemyBoard: FlowPane,
                     private val enemyHand: FlowPane,
                     private val friendlyHand: FlowPane,
                     private val friendlyBoard: FlowPane) extends GUIDebugInterface {


  CreateEnemyBoard()


  def CreateEnemyBoard(theBrain: TheBrain): Unit = {

    import tph.GameStatus

    val gameStatus = theBrain.gameStatus.GetGameStatus()

    for (a <- 0 until gameStatus(1).board.size) {

      val hbox = new HBox()
      val menuButton = new MenuButton()
      menuButton.setText(gameStatus(1).board(a).boardPosition.toString)

      val cardName = new MenuItem()
      cardName.setText(gameStatus(1).board(a).name)

      val cardID = new MenuItem()
      cardID.setText(gameStatus(1).board(a).id.toString)

      val cardHandPosition = new MenuItem()
      cardHandPosition.setText(gameStatus(1).board(a).handPosition.toString)

      val cardBoardPosition = new MenuItem()
      menuButton.setText(gameStatus(1).board(a).boardPosition.toString)


      menuButton.items.addAll(cardName, cardID, cardHandPosition, cardBoardPosition)
      hbox.children = menuButton

      enemyBoard.children.add(hbox)
    }


  }


}
