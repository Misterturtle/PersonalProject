package GUI

import java.util.concurrent.{TimeUnit, ScheduledThreadPoolExecutor}
import javafx.beans.property.SimpleStringProperty

import tph.{Card, NoCards, LogParser, GameState}

import scala.collection.mutable.ListBuffer
import scalafx.geometry.Pos
import scalafx.scene.{Node, Scene}
import scalafx.scene.control.{Separator, Label}
import scalafx.scene.layout.{Priority, AnchorPane, VBox, FlowPane}

/**
  * Created by Harambe on 2/22/2017.
  */


class Controller(){

  val root = new AnchorPane()

  val vboxContainer = new VBox()
  vboxContainer.setAlignment(Pos.Center)
  vboxContainer.setMinSize(800,600)
  vboxContainer.setFillWidth(true)
  vboxContainer.prefWidthProperty().bind(root.width)



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
    new CardVbox(NoCards()),
    new CardVbox(NoCards()),
    new CardVbox(NoCards()),
    new CardVbox(NoCards()),
    new CardVbox(NoCards()),
    new CardVbox(NoCards()),
    new CardVbox(NoCards()),
    new CardVbox(NoCards()),
    new CardVbox(NoCards()),
    new CardVbox(NoCards())
  )
  friendlyHandPane.children = friendlyHandCards
  friendlyHandVbox.setFillWidth(true)
  friendlyHandVbox.prefWidthProperty().bind(vboxContainer.width)
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
    new CardVbox(NoCards()),
    new CardVbox(NoCards()),
    new CardVbox(NoCards()),
    new CardVbox(NoCards()),
    new CardVbox(NoCards()),
    new CardVbox(NoCards()),
    new CardVbox(NoCards()),
    new CardVbox(NoCards()),
    new CardVbox(NoCards()),
    new CardVbox(NoCards())
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
    new CardVbox(NoCards()),
    new CardVbox(NoCards()),
    new CardVbox(NoCards()),
    new CardVbox(NoCards()),
    new CardVbox(NoCards()),
    new CardVbox(NoCards()),
    new CardVbox(NoCards()),
    new CardVbox(NoCards()),
    new CardVbox(NoCards()),
    new CardVbox(NoCards())
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
    new CardVbox(NoCards()),
    new CardVbox(NoCards()),
    new CardVbox(NoCards()),
    new CardVbox(NoCards()),
    new CardVbox(NoCards()),
    new CardVbox(NoCards()),
    new CardVbox(NoCards()),
    new CardVbox(NoCards()),
    new CardVbox(NoCards()),
    new CardVbox(NoCards())
  )

  enemyBoardPane.children = enemyBoardCards
  enemyBoardVBox.children.addAll(enemyBoardSeparator, enemyBoardLabel, enemyBoardPane)


  root.children.add(vboxContainer)
  vboxContainer.children.addAll(enemyHandVBox,enemyBoardVBox, friendlyBoardVBox, friendlyHandVbox)



  def UpdateFriendlyHand(gameState:GameState): Unit = {

    if(gameState.friendlyPlayer.hand.nonEmpty) {
      for (a <- 0 until 10){
        if(gameState.friendlyPlayer.hand.isDefinedAt(a)){
      friendlyHandCards(a).UpdateCard(gameState.friendlyPlayer.hand(a))
          friendlyHandCards(a).setVisible(true)}

      else{
      friendlyHandCards(a).UpdateCard(NoCards())
        friendlyHandCards(a).setVisible(false)}
      }
    }else {
      friendlyHandCards foreach {_.setVisible(false)}
    }
  }

  def UpdateFriendlyBoard(gameState:GameState): Unit = {

    if(gameState.friendlyPlayer.board.nonEmpty) {
      for (a <- 0 until 10){
        if(gameState.friendlyPlayer.board.isDefinedAt(a)){
          friendlyBoardCards(a).UpdateCard(gameState.friendlyPlayer.board(a))
          friendlyBoardCards(a).setVisible(true)}

        else{
          friendlyBoardCards(a).UpdateCard(NoCards())
          friendlyBoardCards(a).setVisible(false)}
      }
    }else {
      friendlyBoardCards foreach {_.setVisible(false)}
    }
  }

  def UpdateEnemyHand(gameState: GameState): Unit = {
    if(gameState.enemyPlayer.hand.nonEmpty) {
      for (a <- 0 until 10){
        if(gameState.enemyPlayer.hand.isDefinedAt(a)){
          enemyHandCards(a).UpdateCard(gameState.enemyPlayer.hand(a))
          enemyHandCards(a).setVisible(true)}

        else{
          enemyHandCards(a).UpdateCard(NoCards())
          enemyHandCards(a).setVisible(false)}
      }
    }else {
      enemyHandCards foreach {_.setVisible(false)}
    }
  }

  def UpdateEnemyBoard(gameState:GameState): Unit = {
    if(gameState.enemyPlayer.board.nonEmpty) {
      for (a <- 0 until 10){
        if(gameState.enemyPlayer.board.isDefinedAt(a)){
          enemyBoardCards(a).UpdateCard(gameState.enemyPlayer.board(a))
          enemyBoardCards(a).setVisible(true)}

        else{
          enemyBoardCards(a).UpdateCard(NoCards())
          enemyBoardCards(a).setVisible(false)}
      }
    }
    else {
      enemyBoardCards foreach {_.setVisible(false)}
    }
  }

  def UpdateGUIWindow():Unit ={
    val gameState = new LogParser().ConstructGameState()
    UpdateFriendlyHand(gameState)
    UpdateFriendlyBoard(gameState)
    UpdateEnemyHand(gameState)
    UpdateEnemyBoard(gameState)
  }





}
