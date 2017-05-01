package GameStateTests

import FileReaders.HSAction.CardPlayed
import GameState.{Player, GameState}
import Logic.IRCState
import VoteSystem.{VoteValidator, VoteAI, VoteState, VoteManager}
import org.scalatest.{FreeSpec, Matchers}
import tph.Constants.ActionVotes._
import tph._
import Constants.TestConstants._

import scala.collection.mutable.ListBuffer

/**
  * Created by Harambe on 4/8/2017.
  */
class UpdateVotesTests extends FreeSpec with Matchers {


  "Votes involving friendly hand cards should adjust when a card has been played." in {

    val vs = new VoteState()
    val gs = new GameState()
    val ircState = new IRCState()
    val ai = new VoteAI(vs,gs)
    val validator = new VoteValidator(gs)
    val vm = new VoteManager(gs, vs, ai, ircState, validator)

    val oldGS = Constants.TestConstants.defaultGameState
    //GameState has played card 4
    val newFriendlyHand = List(createFriendlyHandCard(1),createFriendlyHandCard(2),createFriendlyHandCard(4).copy(handPosition = 3),createFriendlyHandCard(5).copy(handPosition = 4),createFriendlyHandCard(6).copy(handPosition = 5))

    gs.friendlyPlayer = Player(1, hand = newFriendlyHand, board = oldGS.friendlyPlayer.board)
    gs.enemyPlayer = oldGS.enemyPlayer



    ircState.voteExecutionList.appendAll(List((CardPlay(4), (NoCard(), NoCard())), (CardPlayWithPosition(4,3), (NoCard(), NoCard())), (CardPlayWithFriendlyTarget(4,1), (NoCard(), NoCard())), (CardPlayWithEnemyTarget(4,1), (NoCard(), NoCard())),(CardPlayWithFriendlyTargetWithPosition(4, 1, 3), (NoCard(), NoCard())), (CardPlayWithEnemyTargetWithPosition(4,1,3), (NoCard(), NoCard()))))
    vm.updateDecision(UpdateVotes(oldGS.friendlyPlayer, oldGS.enemyPlayer, gs))

    ircState.voteExecutionList(0)._1 shouldBe CardPlay(3)
    ircState.voteExecutionList(1)._1 shouldBe CardPlayWithPosition(3,3)
    ircState.voteExecutionList(2)._1 shouldBe CardPlayWithFriendlyTarget(3,1)
    ircState.voteExecutionList(3)._1 shouldBe CardPlayWithEnemyTarget(3,1)
    ircState.voteExecutionList(4)._1 shouldBe CardPlayWithFriendlyTargetWithPosition(3, 1, 3)
    ircState.voteExecutionList(5)._1 shouldBe CardPlayWithEnemyTargetWithPosition(3,1,3)
  }


  "Votes involving enemy board cards should adjust when a card has been played." in {

    val vs = new VoteState()
    val gs = new GameState()
    val ircState = new IRCState()
    val ai = new VoteAI(vs,gs)
    val validator = new VoteValidator(gs)
    val vm = new VoteManager(gs, vs, ai, ircState, validator)


    val oldGS = Constants.TestConstants.defaultGameState
    //Minion 2 has died
    val newEnemyBoard = List(createEnemyBoardCard(1), createEnemyBoardCard(3).copy(boardPosition = 2),createEnemyBoardCard(4).copy(boardPosition = 3),createEnemyBoardCard(5).copy(boardPosition = 4),createEnemyBoardCard(6).copy(boardPosition = 5))

    gs.friendlyPlayer = oldGS.friendlyPlayer
    gs.enemyPlayer = Player(2, hand = oldGS.enemyPlayer.hand, board = newEnemyBoard)

    val vote1 = (CardPlayWithEnemyTarget(1, 4), (NoCard(), NoCard()))
    val vote2 = (CardPlayWithEnemyTargetWithPosition(1, 3, 4), (NoCard(), NoCard()))
    val vote3 = (HeroPowerWithEnemyTarget(4), (NoCard(), NoCard()))
    val vote4 = (NormalAttack(4,3), (NoCard(), NoCard()))



    ircState.voteExecutionList.appendAll(List(vote1,vote2,vote3,vote4))
    vm.updateDecision(UpdateVotes(oldGS.friendlyPlayer, oldGS.enemyPlayer, gs))

    ircState.voteExecutionList(0)._1 shouldBe CardPlayWithEnemyTarget(1,3)
    ircState.voteExecutionList(1)._1 shouldBe CardPlayWithEnemyTargetWithPosition(1,2,4)
    ircState.voteExecutionList(2)._1 shouldBe HeroPowerWithEnemyTarget(3)
    ircState.voteExecutionList(3)._1 shouldBe NormalAttack(4,2)
  }
  
  "Votes should be able to adjust multiple positions and remove votes that are no longer valid" in{

    val vs = new VoteState()
    val gs = new GameState()
    val ircState = new IRCState()
    val ai = new VoteAI(vs,gs)
    val validator = new VoteValidator(gs)
    val vm = new VoteManager(gs, vs, ai, ircState, validator)

    val oldFriendlyPlayer = Player(1, hand = List(createFriendlyHandCard(1), createFriendlyHandCard(2), createFriendlyHandCard(3), createFriendlyHandCard(4), createFriendlyHandCard(5)), board = List(createFriendlyBoardCard(1), createFriendlyBoardCard(2), createFriendlyBoardCard(3),createFriendlyBoardCard(4),createFriendlyBoardCard(5),createFriendlyBoardCard(6)))
    val oldEnemyPlayer = Player(2, board = List(createEnemyBoardCard(1), createEnemyBoardCard(2), createEnemyBoardCard(3),createEnemyBoardCard(4),createEnemyBoardCard(5),createEnemyBoardCard(6)))
    //Friendly Cards 1 and 2 have been played.
    //Friendly Minions 1 and 2 have died
    //Enemy Minions 1 and 2 have died
    gs.enemyPlayer = Player(2,List(createEnemyBoardCard(3).copy(boardPosition = 1),createEnemyBoardCard(4).copy(boardPosition = 2),createEnemyBoardCard(5).copy(boardPosition = 3),createEnemyBoardCard(6).copy(boardPosition = 4)))
    gs.friendlyPlayer = Player(1, hand = List(createFriendlyHandCard(3).copy(handPosition = 1), createFriendlyHandCard(4).copy(handPosition = 2), createFriendlyHandCard(5).copy(handPosition = 3)), board = List(createFriendlyBoardCard(3).copy(boardPosition = 1),createFriendlyBoardCard(4).copy(boardPosition = 2),createFriendlyBoardCard(5).copy(boardPosition = 3),createFriendlyBoardCard(6).copy(boardPosition = 4), createFriendlyHandCard(1).copy(handPosition = Constants.INT_UNINIT, boardPosition = 5), createFriendlyHandCard(2).copy(handPosition = Constants.INT_UNINIT, boardPosition = 6)))

    val vote1 = (CardPlayWithFriendlyTargetWithPosition(4,3,5), (NoCard(), NoCard()))
    //Vote 2 will be removed when adjusted due to enemy target dieing
    val vote2 = (CardPlayWithEnemyTargetWithPosition(5, 2, 4), (NoCard(), NoCard()))
    val vote3 = (CardPlayWithPosition(4,1), (NoCard(), NoCard()))
    val vote4 = (CardPlay(3), (NoCard(), NoCard()))
    val vote5 = (CardPlayWithEnemyTarget(5, 5), (NoCard(), NoCard()))
    //Vote 6 will be removed when adjusted due to friendly target dieing
    val vote6 = (CardPlayWithFriendlyTarget(4, 1), (NoCard(), NoCard()))
    val vote7 = (HeroPowerWithFriendlyTarget(4), (NoCard(), NoCard()))
    val vote8 = (HeroPowerWithEnemyTarget(5) , (NoCard(), NoCard()))
    val vote9 = (NormalAttack(4,4), (NoCard(), NoCard()))

    ircState.voteExecutionList.appendAll(List(vote1,vote2,vote3,vote4,vote5,vote6,vote7,vote8,vote9))
    vm.updateDecision(UpdateVotes(oldFriendlyPlayer, oldEnemyPlayer, gs))

    ircState.voteExecutionList(0)._1 shouldBe CardPlayWithFriendlyTargetWithPosition(2,1,3)
    //Vote2 removed.
    ircState.voteExecutionList(1)._1 shouldBe CardPlayWithPosition(2,1)
    ircState.voteExecutionList(2)._1 shouldBe CardPlay(1)
    ircState.voteExecutionList(3)._1 shouldBe CardPlayWithEnemyTarget(3,3)
    //Vote 6 removed.
    ircState.voteExecutionList(4)._1 shouldBe HeroPowerWithFriendlyTarget(2)
    ircState.voteExecutionList(5)._1 shouldBe HeroPowerWithEnemyTarget(3)
    ircState.voteExecutionList(6)._1 shouldBe NormalAttack(2,2)
  }


  "Future votes should adjust and also convert to regular votes when a new card is in the spot they are referencing" in {


    val vs = new VoteState()
    val gs = new GameState()
    val ircState = new IRCState()
    val ai = new VoteAI(vs,gs)
    val validator = new VoteValidator(gs)
    val vm = new VoteManager(gs, vs, ai, ircState, validator)

    val vote1 = (FutureCardPlay(1, true), (NoCard(), NoCard()))
    val vote2 = (FutureCardPlay(3, true), (NoCard(), NoCard()))
    val oldGS = Constants.TestConstants.defaultGameState
    //A new card is placed at hand position 2.
    //Card 1 has been removed
    gs.friendlyPlayer = oldGS.friendlyPlayer.copy(hand = List(createFriendlyHandCard(7).copy(handPosition = 1), createFriendlyHandCard(3).copy(handPosition = 2), createFriendlyHandCard(4).copy(handPosition = 3), createFriendlyHandCard(5).copy(handPosition = 4), createFriendlyHandCard(6).copy(handPosition = 5)))




    ircState.voteExecutionList.appendAll(List(vote1, vote2))
    vm.updateDecision(UpdateVotes(oldGS.friendlyPlayer, oldGS.enemyPlayer, gs))

    ircState.voteExecutionList(0)._1 shouldBe CardPlay(1)
    ircState.voteExecutionList(1)._1 shouldBe FutureCardPlay(2)
  }



  "Future votes should adjust and also convert to regular votes when a new Minion is in the spot they are referencing" in {


    val vs = new VoteState()
    val gs = new GameState()
    val ircState = new IRCState()
    val ai = new VoteAI(vs,gs)
    val validator = new VoteValidator(gs)
    val vm = new VoteManager(gs, vs, ai, ircState, validator)

    val vote1 = (FutureCardPlayWithEnemyTarget(4,1, true, true), (NoCard(), NoCard()))
    val vote2 = (FutureNormalAttack(1,1, true,true), (NoCard(), NoCard()))
    val vote3 = (FutureCardPlayWithEnemyTarget(1,1, true, true), (NoCard(), NoCard()))
    val oldGS = Constants.TestConstants.defaultGameState
    //Friendly Minion 1 has died
    //Friendly Card 1 has been played into spot one
    //Enemy Minion 2 has died
    //A new enemy Minion has been placed at spot 1
    //Enemy Minion 1 is now in spot 2
    val newFriendlyHand = List(createFriendlyHandCard(2).copy(handPosition = 1), createFriendlyHandCard(3).copy(handPosition = 2), createFriendlyHandCard(4).copy(handPosition = 3), createFriendlyHandCard(5).copy(handPosition = 4), createFriendlyHandCard(6).copy(handPosition = 5))
    gs.friendlyPlayer = oldGS.friendlyPlayer.copy(hand = newFriendlyHand, board = List(createFriendlyHandCard(1).copy(handPosition = Constants.INT_UNINIT, boardPosition = 1), createFriendlyBoardCard(2), createFriendlyBoardCard(3), createFriendlyBoardCard(4)))
    gs.enemyPlayer = oldGS.enemyPlayer.copy(board = List(createEnemyBoardCard(9).copy(boardPosition = 1), createEnemyBoardCard(1).copy(boardPosition = 2), createEnemyBoardCard(3), createEnemyBoardCard(4)))

    ircState.voteExecutionList.appendAll(List(vote1, vote2, vote3))
    vm.updateDecision(UpdateVotes(oldGS.friendlyPlayer, oldGS.enemyPlayer, gs))

    ircState.voteExecutionList(0)._1 shouldBe FutureCardPlayWithEnemyTarget(3, 1, true, false)
    ircState.voteExecutionList(1)._1 shouldBe NormalAttack(1,1)
    ircState.voteExecutionList(2)._1 shouldBe FutureCardPlayWithEnemyTarget(1,1, true, false)
  }

  "Problem situation 1" in {

    val vs = new VoteState()
    val gs = new GameState()
    val ircState = new IRCState()
    val ai = new VoteAI(vs,gs)
    val validator = new VoteValidator(gs)
    val vm = new VoteManager(gs, vs, ai, ircState, validator)

    val vote1 = (NormalAttack(1,0), (NoCard(), NoCard()))

    val enemyHero = Card("Alleria Windrunner", 200, Constants.INT_UNINIT, 0, 2, "HERO_05a")
    val oldFriendlyHand = List(createFriendlyHandCard(1))
    val oldFriendlyBoard = List(createFriendlyBoardCard(1))

    gs.friendlyPlayer = Player(1, hand = Nil, board = List(createFriendlyHandCard(1).copy(handPosition = Constants.INT_UNINIT, boardPosition = 1), createFriendlyBoardCard(1).copy(boardPosition = 2)))
    gs.enemyPlayer = Player(2, hero = Some(enemyHero))

    ircState.voteExecutionList.appendAll(List(vote1))
    vm.updateDecision(UpdateVotes(Player(1, hand = oldFriendlyHand, board = oldFriendlyBoard), Player(2, hero = Some(enemyHero)), gs))

    ircState.voteExecutionList(0)._1 shouldBe NormalAttack(2,0)




  }




}
