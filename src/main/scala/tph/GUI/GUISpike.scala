package tph.GUI

import javafx.geometry.Insets
import javafx.scene.text.Text

import com.typesafe.scalalogging.LazyLogging
import javafx.application.Application
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.{Label, TextField, TextArea, Button}
import javafx.scene.layout.{GridPane, VBox, StackPane}
import javafx.stage.{Modality, Stage}


/**
  * Created by Harambe on 2/3/2017.
  */

object GUISpike {
  def main(args: Array[String]) {
    Application.launch(classOf[GUISpike], args: _*)
  }
}


class GUISpike extends Application with LazyLogging {


  override def start(primaryStage: Stage): Unit = {
    primaryStage.setTitle("TPH")


    //Text Field
    val textField = new TextField()
    val textFieldLabel = new Label("Label 0")
    textField.setPrefSize(50, 10)
    textField.setText("A")

    //Test
    val tf1 = new TextField()
    val tf1Label = new Label("Label 1")
    tf1.setPrefSize(50, 10)
    tf1.setText("tf1")

    val tf2 = new TextField()
    val tf2Label = new Label("Label 2")
    tf2.setPrefSize(50, 10)
    tf2.setText("tf2")

    val tf3 = new TextField()
    val tf3Label = new Label("Label 3")
    tf3.setPrefSize(50, 10)
    tf3.setText("tf3")

    val tf4 = new TextField()
    val tf4Label = new Label("Label 4")
    tf4.setPrefSize(50, 10)
    tf4.setText("tf4")

    val tf5 = new TextField()
    val tf5Label = new Label("Label 5")
    tf5.setPrefSize(50, 10)
    tf5.setText("tf5")

    val tf6 = new TextField()
    val tf6Label = new Label("Label 6")
    tf6.setPrefSize(50, 10)
    tf6.setText("tf6")

    //Grid Layout
    val grid = new GridPane()
    grid.setPadding(new Insets(10, 10, 10, 10))
    grid.setVgap(5)
    grid.setHgap(5)

    grid.getChildren.add(textField)
    GridPane.setConstraints(textField, 1, 1)

    grid.getChildren().add(tf1)
    GridPane.setConstraints(tf1, 1, 2)

    grid.getChildren().add(tf2)
    GridPane.setConstraints(tf2, 2, 1)

    grid.getChildren().add(tf3)
    GridPane.setConstraints(tf3, 3, 1)

    grid.getChildren().add(tf4)
    GridPane.setConstraints(tf4, 4, 1)

    grid.getChildren().add(tf5)
    GridPane.setConstraints(tf5, 5, 1)

    grid.getChildren().add(tf6)
    GridPane.setConstraints(tf6, 6, 1)

    grid.gridLinesVisibleProperty()



    val testScene = new Scene(grid, 300, 250)
    primaryStage.setScene(testScene)
    primaryStage.show()

    logger.debug("Grid columns: " + grid.getColumnConstraints())
    logger.debug("Grid rows: " + grid.getRowConstraints())
  }
}
