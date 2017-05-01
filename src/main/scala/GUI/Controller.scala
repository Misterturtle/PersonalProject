package GUI

import java.io.{FileWriter, PrintWriter, File}
import java.util.concurrent.{TimeUnit, ScheduledThreadPoolExecutor}
import javafx.event
import javafx.event.{ActionEvent, EventHandler}

import FileReaders.{LogFileReader, LogParser}
import GameState.GameState
import Logic.IRCState
import VoteSystem.{VoteManager, VoteAI, VoteState}
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import tph._

import scala.collection.mutable.ListBuffer
import scalafx.animation.PauseTransition

import scalafx.geometry.Pos
import scalafx.scene.input.MouseEvent
import scalafx.scene.{Node, Scene}
import scalafx.scene.control._
import scalafx.scene.layout.{Priority, AnchorPane, VBox, FlowPane}
import scalafx.Includes._
import scalafx.stage.{Stage, FileChooser}

/**
  * Created by Harambe on 2/22/2017.
  */


class Controller(gs:GameState, logFileReader:LogFileReader, ircBot:IRCBot, hs:HearthStone, ircState:IRCState, vm:VoteManager, tb:TheBrain) extends LazyLogging{

  val root = new AnchorPane()
  val config = ConfigFactory.load()
  var defaultReaderFile = new File(config.getString("tph.writerFiles.actionLog"))
  var defaultWriterFile = new File(config.getString("tph.writerFiles.guiPrintFile"))




  val backgroundContainer = new VBox()
  backgroundContainer.setAlignment(Pos.Center)
  backgroundContainer.setMinSize(800,600)
  backgroundContainer.setFillWidth(true)
  backgroundContainer.prefWidthProperty().bind(root.width)



  //Create Friendly Hand VBox
  val friendlyHandPane = new FlowPane()
  friendlyHandPane.vgrow = Priority.Always
  friendlyHandPane.hgrow = Priority.Always
  friendlyHandPane.setAlignment(Pos.BottomCenter)
  friendlyHandPane.setHgap(20)
  val friendlyHandVbox = new VBox()
  friendlyHandVbox.setAlignment(Pos.Center)
  val friendlyHandLabel = new Label("Friendly Hand")
  val friendlyHandSeparator = new Separator()
  var friendlyHandCards = List[CardVbox](
    new CardVbox(),
    new CardVbox(),
    new CardVbox(),
    new CardVbox(),
    new CardVbox(),
    new CardVbox(),
    new CardVbox(),
    new CardVbox(),
    new CardVbox(),
    new CardVbox()
  )
  friendlyHandPane.children = friendlyHandCards
  friendlyHandVbox.setFillWidth(true)
  friendlyHandVbox.prefWidthProperty().bind(backgroundContainer.width)
  friendlyHandVbox.children.addAll(friendlyHandSeparator, friendlyHandLabel, friendlyHandPane)


  //Create Friendly Board VBox
  val friendlyBoardPane = new FlowPane()
  friendlyBoardPane.vgrow = Priority.Always
  friendlyBoardPane.hgrow = Priority.Always
  friendlyBoardPane.setAlignment(Pos.BottomCenter)
  friendlyBoardPane.setHgap(20)
  val friendlyBoardVBox = new VBox()
  friendlyBoardVBox.setAlignment(Pos.Center)
  val friendlyBoardSeparator = new Separator()
  val friendlyBoardLabel = new Label("Friendly Board")
  var friendlyBoardCards = List[CardVbox](
    new CardVbox(),
    new CardVbox(),
    new CardVbox(),
    new CardVbox(),
    new CardVbox(),
    new CardVbox(),
    new CardVbox(),
    new CardVbox(),
    new CardVbox(),
    new CardVbox()
  )
  friendlyBoardPane.children = friendlyBoardCards
  friendlyBoardVBox.children.addAll(friendlyBoardSeparator, friendlyBoardLabel, friendlyBoardPane)



  val enemyHandPane = new FlowPane()
  enemyHandPane.vgrow = Priority.Always
  enemyHandPane.hgrow = Priority.Always
  enemyHandPane.setAlignment(Pos.TopCenter)
  enemyHandPane.setHgap(20)
  val enemyHandVBox = new VBox()
  enemyHandVBox.setAlignment(Pos.Center)
  val enemyHandSeparator = new Separator()
  val enemyHandLabel = new Label("Enemy Hand")
  var enemyHandCards = List[CardVbox](
    new CardVbox(),
    new CardVbox(),
    new CardVbox(),
    new CardVbox(),
    new CardVbox(),
    new CardVbox(),
    new CardVbox(),
    new CardVbox(),
    new CardVbox(),
    new CardVbox()
  )

  enemyHandPane.children = enemyHandCards
  enemyHandVBox.children.addAll(enemyHandSeparator, enemyHandLabel, enemyHandPane)




  val enemyBoardPane = new FlowPane()
  enemyBoardPane.vgrow = Priority.Always
  enemyBoardPane.hgrow = Priority.Always
  enemyBoardPane.setAlignment(Pos.TopCenter)
  enemyBoardPane.setHgap(20)
  val enemyBoardVBox = new VBox()
  enemyBoardVBox.setAlignment(Pos.Center)
  val enemyBoardSeparator = new Separator()
  val enemyBoardLabel = new Label("Enemy Board")
  var enemyBoardCards = List[CardVbox](
    new CardVbox(),
    new CardVbox(),
    new CardVbox(),
    new CardVbox(),
    new CardVbox(),
    new CardVbox(),
    new CardVbox(),
    new CardVbox(),
    new CardVbox(),
    new CardVbox()
  )

  enemyBoardPane.children = enemyBoardCards
  enemyBoardVBox.children.addAll(enemyBoardSeparator, enemyBoardLabel, enemyBoardPane)

  val menuBar = new MenuBar()
  val saveMenu = new Menu("Save")
  val loadMenu = new Menu("Load")
  val menuList = Iterable[Menu](saveMenu,loadMenu)
  val defaultSaveItem = new MenuItem("Print to file")
  defaultSaveItem.onAction = {
    e: scalafx.event.ActionEvent =>
      printGameState(gs, defaultWriterFile)

  }
  val setReaderFileItem = new MenuItem("Set file to read GameState")
  setReaderFileItem.onAction = {
    e: scalafx.event.ActionEvent =>
      defaultReaderFile = new FileChooser().showOpenDialog(root.getScene.getWindow())
  }
  val saveMenuItems = Iterable(defaultSaveItem)
  saveMenu.items = saveMenuItems

  val loadMenuItems = Iterable(setReaderFileItem)
  loadMenu.items = loadMenuItems

  menuBar.menus = menuList
  menuBar.setVisible(true)

  root.children.addAll(backgroundContainer)
  backgroundContainer.children.addAll(menuBar,enemyHandVBox,enemyBoardVBox, friendlyBoardVBox, friendlyHandVbox)


  def printGameState(gameState: GameState, writerFile:File): Unit ={

    val writer = new PrintWriter(new FileWriter(writerFile))
    val friendlyHand = gameState.friendlyPlayer.hand
    val friendlyBoard = gameState.friendlyPlayer.board
    val enemyHand = gameState.enemyPlayer.hand
    val enemyBoard = gameState.enemyPlayer.board

    writer.println("val expectedFriendlyHand = List[Card](")
    writer.flush()
    for(a<-1 to friendlyHand.size) {
      if(a == friendlyHand.size)
      writer.println("new Card(\""+friendlyHand(a-1).name+"\", "+ friendlyHand(a - 1).id + ", "+friendlyHand(a - 1).handPosition + ", "+ friendlyHand(a - 1).boardPosition + ", "+friendlyHand(a - 1).player+ ", \""+friendlyHand(a-1).cardID+"\"))")
      else
        writer.println("new Card(\""+friendlyHand(a-1).name+"\", "+ friendlyHand(a - 1).id + ", "+friendlyHand(a - 1).handPosition + ", "+ friendlyHand(a - 1).boardPosition + ", "+friendlyHand(a - 1).player+ ", \""+friendlyHand(a-1).cardID+"\"),")
      writer.flush()
    }
    writer.println("val expectedFriendlyBoard = List[Card](")
    writer.flush()
    for(a<-1 to friendlyBoard.size) {
      if(a == friendlyBoard.size)
      writer.println("new Card(\""+friendlyBoard(a-1).name+"\", "+ friendlyBoard(a - 1).id + ", "+friendlyBoard(a - 1).handPosition + ", "+ friendlyBoard(a - 1).boardPosition + ", "+friendlyBoard(a - 1).player+ ", \""+friendlyBoard(a-1).cardID+"\"))")
      else
        writer.println("new Card(\""+friendlyBoard(a-1).name+"\", "+ friendlyBoard(a - 1).id + ", "+friendlyBoard(a - 1).handPosition + ", "+ friendlyBoard(a - 1).boardPosition + ", "+friendlyBoard(a - 1).player+ ", \""+friendlyBoard(a-1).cardID+"\"),")
      writer.flush()
    }
    writer.println("val expectedEnemyHand = List[Card](")
    writer.flush()
    for(a<-1 to enemyHand.size) {
      if(a == enemyHand.size)
      writer.println("new Card(\""+enemyHand(a-1).name+"\", "+ enemyHand(a - 1).id + ", "+enemyHand(a - 1).handPosition + ", "+ enemyHand(a - 1).boardPosition + ", "+enemyHand(a - 1).player+ ", \""+enemyHand(a-1).cardID+"\"))")
      else
        writer.println("new Card(\""+enemyHand(a-1).name+"\", "+ enemyHand(a - 1).id + ", "+enemyHand(a - 1).handPosition + ", "+ enemyHand(a - 1).boardPosition + ", "+enemyHand(a - 1).player+ ", \""+enemyHand(a-1).cardID+"\"),")
      writer.flush()
    }

    writer.println("val expectedEnemyBoard = List[Card](")
    writer.flush()
    for(a<-1 to enemyBoard.size) {

      if(a == enemyBoard.size)
        writer.println("new Card(\""+enemyBoard(a-1).name+"\", "+ enemyBoard(a - 1).id + ", "+enemyBoard(a - 1).handPosition + ", "+ enemyBoard(a - 1).boardPosition + ", "+enemyBoard(a - 1).player+ ", \""+enemyBoard(a-1).cardID+ "\"))")
        else
      writer.println("new Card(\""+enemyBoard(a-1).name+"\", "+ enemyBoard(a - 1).id + ", "+enemyBoard(a - 1).handPosition + ", "+ enemyBoard(a - 1).boardPosition + ", "+enemyBoard(a - 1).player+ ", \""+enemyBoard(a-1).cardID+ "\"),")
      writer.flush()
    }

  }


  def updateFriendlyHand(gameState:GameState): Unit = {

    if(gameState.friendlyPlayer.hand.nonEmpty) {
      for (a <- 0 until 10){
        if(gameState.friendlyPlayer.hand.isDefinedAt(a)){
      friendlyHandCards(a).UpdateCard(gameState.friendlyPlayer.hand(a))
          friendlyHandCards(a).setVisible(true)}

      else{
        friendlyHandCards(a).setVisible(false)}
      }
    }else {
      friendlyHandCards foreach {_.setVisible(false)}
    }
  }

  def updateFriendlyBoard(gameState:GameState): Unit = {

    if(gameState.friendlyPlayer.board.nonEmpty) {
      for (a <- 0 until 10){
        if(gameState.friendlyPlayer.board.isDefinedAt(a)){
          friendlyBoardCards(a).UpdateCard(gameState.friendlyPlayer.board(a))
          friendlyBoardCards(a).setVisible(true)}

        else{
          friendlyBoardCards(a).setVisible(false)}
      }
    }else {
      friendlyBoardCards foreach {_.setVisible(false)}
    }
  }

  def updateEnemyHand(gameState: GameState): Unit = {
    if(gameState.enemyPlayer.hand.nonEmpty) {
      for (a <- 0 until 10){
        if(gameState.enemyPlayer.hand.isDefinedAt(a)){
          enemyHandCards(a).UpdateCard(gameState.enemyPlayer.hand(a))
          enemyHandCards(a).setVisible(true)}

        else{
          enemyHandCards(a).setVisible(false)}
      }
    }else {
      enemyHandCards foreach {_.setVisible(false)}
    }
  }

  def updateEnemyBoard(gameState:GameState): Unit = {
    if(gameState.enemyPlayer.board.nonEmpty) {
      for (a <- 0 until 10){
        if(gameState.enemyPlayer.board.isDefinedAt(a)){
          enemyBoardCards(a).UpdateCard(gameState.enemyPlayer.board(a))
          enemyBoardCards(a).setVisible(true)}

        else{
          enemyBoardCards(a).setVisible(false)}
      }
    }
    else {
      enemyBoardCards foreach {_.setVisible(false)}
    }
  }

  def updateGUIWindow():Unit ={
    logger.debug("Updating GameState")
    updateFriendlyHand(gs)
    updateFriendlyBoard(gs)
    updateEnemyHand(gs)
    updateEnemyBoard(gs)
  }

}
