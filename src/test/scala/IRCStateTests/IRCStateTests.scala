package IRCStateTests

import javax.xml.stream.events.StartDocument

import FileReaders.HSAction._
import FileReaders.{LogParser, LogFileReader}
import Logic.IRCState
import VoteSystem.{VoteValidator, VoteAI, VoteState, VoteManager}
import com.typesafe.config.ConfigFactory
import org.scalatest.{FreeSpec, Matchers}
import tph.Constants.ActionVotes._
import tph._

import scala.collection.mutable.ListBuffer

/**
  * Created by Harambe on 4/22/2017.
  */
class IRCStateTests extends FreeSpec with Matchers {

  val config = ConfigFactory.load()
  val accountName = config.getString("tph.hearthstone.accountName")

  def checkCorrectState(ircState: IRCState, mulliganOptions: Int = 0, isChooseOne: Boolean = false, isDiscover: Boolean = false, isMulligan: Boolean = false, mulliganComplete: Boolean = false, myTurn: Boolean = false,
                        turnStartTimeStamp: Long = 0L, mulliganStartTimeStamp: Long = 0L, discoverStartTimeStamp: Long = 0L, chooseOneStartTimeStamp: Long = 0L, voteExecutionList: ListBuffer[(ActionVote, (HSCard, HSCard))] = ListBuffer[(ActionVote, (HSCard, HSCard))](), lastVoteCheck: Long = 0L): Unit = {

    val timeStampBuffer = 100

    ircState.mulliganOptions shouldBe mulliganOptions
    ircState.isChooseOne shouldBe isChooseOne
    ircState.isDiscover shouldBe isDiscover
    ircState.isMulligan shouldBe isMulligan
    ircState.mulliganComplete shouldBe mulliganComplete
    ircState.myTurn shouldBe myTurn

    ircState.turnStartTimeStamp shouldBe <=(turnStartTimeStamp)
    ircState.turnStartTimeStamp shouldBe >(turnStartTimeStamp - timeStampBuffer)

    ircState.mulliganStartTimeStamp shouldBe <=(mulliganStartTimeStamp)
    ircState.mulliganStartTimeStamp shouldBe >(mulliganStartTimeStamp - timeStampBuffer)

    ircState.discoverStartTimeStamp shouldBe <=(discoverStartTimeStamp)
    ircState.discoverStartTimeStamp shouldBe >(discoverStartTimeStamp - timeStampBuffer)

    ircState.chooseOneStartTimeStamp shouldBe <=(chooseOneStartTimeStamp)
    ircState.chooseOneStartTimeStamp shouldBe >(chooseOneStartTimeStamp - timeStampBuffer)

    ircState.voteExecutionList shouldBe voteExecutionList
  }


  "The state of the IRC should be changed, when" - {

    "startMulligan is called" in {
      val ircState = new IRCState
      ircState.startMulligan()

      checkCorrectState(ircState, isMulligan = true, mulliganOptions = 0, mulliganStartTimeStamp = System.currentTimeMillis())
    }


    "startMyTurn is called" in {
      val ircState = new IRCState
      ircState.startMyTurn()

      checkCorrectState(ircState, myTurn = true, turnStartTimeStamp = System.currentTimeMillis())
    }

    "startDiscover is called" in {
      val ircState = new IRCState
      ircState.startDiscover()

      checkCorrectState(ircState, isDiscover = true, discoverStartTimeStamp = System.currentTimeMillis())
    }

    "startChooseOne is called" in {
      val ircState = new IRCState
      ircState.startChooseOne()

      checkCorrectState(ircState, isChooseOne = true, chooseOneStartTimeStamp = System.currentTimeMillis())
    }

    "gameOver is called" in {
      val ircState = new IRCState
      ircState.gameOver()

      checkCorrectState(ircState, isDiscover = false, isChooseOne = false, isMulligan = false, myTurn = false, mulliganComplete = false, mulliganOptions = 0,
        turnStartTimeStamp = 0, mulliganStartTimeStamp = 0, discoverStartTimeStamp = 0, chooseOneStartTimeStamp = 0)
    }

    "endMyTurn is called" in {
      val ircState = new IRCState
      ircState.endMyTurn()

      checkCorrectState(ircState, myTurn = false, isChooseOne = false, isDiscover = false, isMulligan = false, turnStartTimeStamp = 0, mulliganStartTimeStamp = 0, chooseOneStartTimeStamp = 0, discoverStartTimeStamp = 0)
    }
  }


  "IRCState should execute IRCActions based on type" - {
    val gs = new GameState()
    val vs = new VoteState()
    val ai = new VoteAI(vs, gs)
    val validator = new VoteValidator(gs)

    "When executing a TurnStart" - {

      "If the TurnStart is not for account name, it should just delete the TurnStart" in {
        val ircState = new IRCState()
        val executionList = ListBuffer[IRCAction](TurnStart("SomeOtherName"))
        val vm = new VoteManager(gs, vs, ai, ircState, validator)


        ircState.update(executionList, vm)


        executionList shouldBe empty
      }

      "If the TurnStart is for my account name" - {

        "If mulligan is not complete, nothing should happen" in {
          val executionList = ListBuffer[IRCAction](TurnStart(accountName))
          val ircState = new IRCState()
          val vm = new VoteManager(gs, vs, ai, ircState, validator)
          ircState.mulliganComplete = false

          ircState.update(executionList, vm)


          executionList shouldBe ListBuffer[IRCAction](TurnStart(accountName))
          checkCorrectState(ircState, myTurn = false, turnStartTimeStamp = 0)
        }

        "If mulligan is complete" - {

          "IRCState should start my turn" in {
            val executionList = ListBuffer[IRCAction](TurnStart(accountName))
            val ircState = new IRCState()
            val vm = new VoteManager(gs, vs, ai, ircState, validator) {
              override def clearAllVotes(): Unit = {}
            }
            ircState.mulliganComplete = true

            ircState.update(executionList, vm)


            ircState.myTurn shouldBe true
            checkCorrectState(ircState, myTurn = true, turnStartTimeStamp = System.currentTimeMillis(), mulliganComplete = true)
          }

          "IRCState should remove all TurnStarts from the execution list" in {
            val executionList = ListBuffer[IRCAction](TurnStart(accountName), TurnStart(accountName), TurnStart("SomeOtherName"))
            val ircState = new IRCState()
            val vm = new VoteManager(gs, vs, ai, ircState, validator) {
              override def clearAllVotes(): Unit = {}
            }
            ircState.mulliganComplete = true

            ircState.update(executionList, vm)


            executionList shouldBe empty
          }

          "VoteManager should clear all votes" in {
            var votesCleared = false
            val executionList = ListBuffer[IRCAction](TurnStart(accountName), TurnStart(accountName), TurnStart("SomeOtherName"))
            val ircState = new IRCState()
            val vm = new VoteManager(gs, vs, ai, ircState, validator) {
              override def clearAllVotes(): Unit = {
                votesCleared = true
              }
            }
            ircState.mulliganComplete = true


            ircState.update(executionList, vm)


            votesCleared shouldBe true
          }
        }
      }
    }



    "When executing a MulliganStart" - {

      "If currently in a mulligan, do nothing" in {
        val executionList = ListBuffer[IRCAction](MulliganStart())
        val ircState = new IRCState()
        val vm = new VoteManager(gs, vs, ai, ircState, validator)
        ircState.isMulligan = true


        ircState.update(executionList, vm)


        executionList shouldBe ListBuffer[IRCAction](MulliganStart())
      }

      "If not currently in a mulligan," - {

        "Start mulligan" in {
          val executionList = ListBuffer[IRCAction](MulliganStart())
          val ircState = new IRCState()
          val vm = new VoteManager(gs, vs, ai, ircState, validator)
          ircState.isMulligan = false
          ircState.mulliganOptions = Constants.INT_UNINIT
          ircState.mulliganStartTimeStamp = Constants.INT_UNINIT


          ircState.update(executionList, vm)


          checkCorrectState(ircState, isMulligan = true, mulliganOptions = 0, mulliganStartTimeStamp = System.currentTimeMillis())
        }

        "Remove all mulliganStarts from the list" in {
          val executionList = ListBuffer[IRCAction](MulliganStart(), MulliganStart(), MulliganStart())
          val ircState = new IRCState()
          val vm = new VoteManager(gs, vs, ai, ircState, validator)
          ircState.isMulligan = false


          ircState.update(executionList, vm)


          ircState.voteExecutionList shouldBe empty
        }

        "Clear all votes" in {
          var votesCleared = false
          val executionList = ListBuffer[IRCAction](MulliganStart())
          val ircState = new IRCState()
          val vm = new VoteManager(gs, vs, ai, ircState, validator) {
            override def clearAllVotes(): Unit = {
              votesCleared = true
            }
          }
          ircState.isMulligan = false


          ircState.update(executionList, vm)


          votesCleared shouldBe true
        }
      }
    }



    "When executing a GameOver" - {

      "ircState should change to gameOver status" in {
        val ircState = new IRCState
        val vm = new VoteManager(gs, vs, ai, ircState, validator)
        val executionList = ListBuffer[IRCAction](GameOver())
        ircState.isDiscover = true
        ircState.isChooseOne = true
        ircState.isMulligan = true
        ircState.myTurn = true
        ircState.mulliganComplete = true
        ircState.mulliganOptions = Constants.INT_UNINIT
        ircState.turnStartTimeStamp = Constants.INT_UNINIT
        ircState.mulliganStartTimeStamp = Constants.INT_UNINIT
        ircState.discoverStartTimeStamp = Constants.INT_UNINIT
        ircState.chooseOneStartTimeStamp = Constants.INT_UNINIT


        ircState.update(executionList, vm)


        checkCorrectState(ircState, isDiscover = false, isChooseOne = false, isMulligan = false, myTurn = false, mulliganComplete = false, mulliganOptions = 0, turnStartTimeStamp = 0, mulliganStartTimeStamp = 0,
          discoverStartTimeStamp = 0, chooseOneStartTimeStamp = 0)
      }

      "Remove all GameOver's from the list" in {
        val executionList = ListBuffer[IRCAction](GameOver(), GameOver(), GameOver())
        val ircState = new IRCState()
        val vm = new VoteManager(gs, vs, ai, ircState, validator)


        ircState.update(executionList, vm)


        ircState.voteExecutionList shouldBe empty
      }


      "Clear all votes" in {
        var votesCleared = false
        val executionList = ListBuffer[IRCAction](GameOver())
        val ircState = new IRCState()
        val vm = new VoteManager(gs, vs, ai, ircState, validator) {
          override def clearAllVotes(): Unit = {
            votesCleared = true
          }
        }


        ircState.update(executionList, vm)


        votesCleared shouldBe true
      }
    }



    "When executing a DiscoverStart" - {

      "If already in a discover, do nothing" in {
        val executionList = ListBuffer[IRCAction](DiscoverStart())
        val ircState = new IRCState()
        val vm = new VoteManager(gs, vs, ai, ircState, validator)
        ircState.isDiscover = true


        ircState.update(executionList, vm)


        executionList shouldBe ListBuffer[IRCAction](DiscoverStart())
      }

      "If not already in a discover" - {

        "Start discover" in {
          val executionList = ListBuffer[IRCAction](DiscoverStart())
          val ircState = new IRCState()
          val vm = new VoteManager(gs, vs, ai, ircState, validator)
          ircState.isDiscover = false


          ircState.update(executionList, vm)


          checkCorrectState(ircState, isDiscover = true, discoverStartTimeStamp = System.currentTimeMillis())
        }

        "Remove all DiscoverStart's from the list" in {
          val executionList = ListBuffer[IRCAction](DiscoverStart(), DiscoverStart(), DiscoverStart())
          val ircState = new IRCState()
          val vm = new VoteManager(gs, vs, ai, ircState, validator)
          ircState.isDiscover = false


          ircState.update(executionList, vm)


          ircState.voteExecutionList shouldBe empty
        }


        "Clear all votes" in {
          var votesCleared = false
          val executionList = ListBuffer[IRCAction](DiscoverStart())
          val ircState = new IRCState()
          val vm = new VoteManager(gs, vs, ai, ircState, validator) {
            override def clearAllVotes(): Unit = {
              votesCleared = true
            }
          }
          ircState.isDiscover = false


          ircState.update(executionList, vm)


          votesCleared shouldBe true
        }
      }
    }


    "When executing MulliganOption" - {

      "If not already in a mulligan, do nothing" in {
        val executionList = ListBuffer[IRCAction](MulliganOption())
        val ircState = new IRCState()
        val vm = new VoteManager(gs, vs, ai, ircState, validator)
        ircState.isMulligan = false


        ircState.update(executionList, vm)


        executionList shouldBe ListBuffer[IRCAction](MulliganOption())
      }

      "If we are in a mulligan" - {

        "Add one to mulligan option" in {
          val executionList = ListBuffer[IRCAction](MulliganOption(), MulliganOption(), MulliganOption())
          val ircState = new IRCState()
          val vm = new VoteManager(gs, vs, ai, ircState, validator)
          ircState.isMulligan = true
          ircState.mulliganOptions = 0


          ircState.update(executionList, vm)


          ircState.mulliganOptions = 3
        }


        "Remove MulliganOptions from the list" in {
          val executionList = ListBuffer[IRCAction](MulliganOption(), MulliganOption(), MulliganOption())
          val ircState = new IRCState()
          val vm = new VoteManager(gs, vs, ai, ircState, validator)
          ircState.isMulligan = true
          ircState.mulliganOptions = 0


          ircState.update(executionList, vm)


          ircState.update(executionList, vm)
        }
      }
    }
  }



  "When Checking for a decision" - {
    val gs = new GameState()
    val vs = new VoteState()
    val ai = new VoteAI(vs, gs)
    val validator = new VoteValidator(gs)

    "If mulligan state is active" - {

      "If the mulligan time has not elapsed, do nothing" in {
        val ircState = new IRCState
        val vm = new VoteManager(gs, vs, ai, ircState, validator)
        ircState.isMulligan = true
        ircState.mulliganStartTimeStamp = System.currentTimeMillis()


        ircState.checkDecision(vm)


        ircState.voteExecutionList shouldBe empty
      }


      "If the mulligan time has elapsed" - {

        "If the decision returns an ActionUninit, add a blank mulligan vote just to continue" in {
          val ircState = new IRCState
          val vm = new VoteManager(gs, vs, ai, ircState, validator) {
            override def makeMulliganDecision(): ActionVote = {
              ActionUninit()
            }

            override def clearActionVotes(): Unit = {}
          }
          ircState.isMulligan = true
          ircState.mulliganStartTimeStamp = System.currentTimeMillis() - (ircState.mulliganDelay + 10)


          ircState.checkDecision(vm)


          ircState.voteExecutionList shouldBe ListBuffer((MulliganVote(false, false, false, false), (NoCard(), NoCard())))
        }


        "If there are mulligan votes, add the decision to the list" in {
          val ircState = new IRCState
          val vm = new VoteManager(gs, vs, ai, ircState, validator) {
            override def makeMulliganDecision(): ActionVote = {
              MulliganVote(true, false, true, false)
            }

            override def clearActionVotes(): Unit = {}
          }
          ircState.isMulligan = true
          ircState.mulliganStartTimeStamp = System.currentTimeMillis() - (ircState.mulliganDelay + 10)


          ircState.checkDecision(vm)


          ircState.voteExecutionList shouldBe ListBuffer((MulliganVote(true, false, true, false), (NoCard(), NoCard())))
        }

        "Also clear all action votes" in {
          var votesCleared = false
          val ircState = new IRCState
          val vm = new VoteManager(gs, vs, ai, ircState, validator) {
            override def makeMulliganDecision(): ActionVote = {
              MulliganVote(true, false, true, false)
            }

            override def clearActionVotes(): Unit = {
              votesCleared = true
            }
          }
          ircState.isMulligan = true
          ircState.mulliganStartTimeStamp = System.currentTimeMillis() - (ircState.mulliganDelay + 10)


          ircState.checkDecision(vm)


          votesCleared shouldBe true
        }
      }
    }


    "When it is my turn" - {

      "If we're in a chooseOne" - {

        "If the ChooseOne time has not elapsed, do nothing" in {
          var decisionMade = false
          val ircState = new IRCState
          val vm = new VoteManager(gs, vs, ai, ircState, validator) {
            override def makeChooseOneDecision(): (ActionVote, (HSCard, HSCard)) = {
              (ActionUninit(), (NoCard(), NoCard()))
            }
          }
          ircState.myTurn = true
          ircState.isChooseOne = true
          ircState.chooseOneStartTimeStamp = System.currentTimeMillis()


          ircState.checkDecision(vm)


          ircState.voteExecutionList shouldBe empty
        }

        "If the ChooseOne time has elapsed" - {

          "If the decision returns an ActionUninit, prepend ChooseOne(1) to the execution list" in {
            val ircState = new IRCState
            val vm = new VoteManager(gs, vs, ai, ircState, validator) {
              override def makeChooseOneDecision(): (ActionVote, (HSCard, HSCard)) = {
                (ActionUninit(), (NoCard(), NoCard()))
              }
            }
            ircState.myTurn = true
            ircState.isChooseOne = true
            ircState.chooseOneStartTimeStamp = System.currentTimeMillis() - (ircState.chooseOneDelay + 10)
            ircState.voteExecutionList.append((ActionUninit(), (NoCard(), NoCard())))


            ircState.checkDecision(vm)


            ircState.voteExecutionList shouldBe ListBuffer((ChooseOne(1), (NoCard(), NoCard())), (ActionUninit(), (NoCard(), NoCard())))
          }

          "If the decision returns a valid vote, prepend that vote to the execution list" in {
            val ircState = new IRCState
            val vm = new VoteManager(gs, vs, ai, ircState, validator) {
              override def makeChooseOneDecision(): (ActionVote, (HSCard, HSCard)) = {
                (ChooseOne(2), (NoCard(), NoCard()))
              }

              override def clearActionVotes(): Unit = {}
            }
            ircState.myTurn = true
            ircState.isChooseOne = true
            ircState.chooseOneStartTimeStamp = System.currentTimeMillis() - (ircState.chooseOneDelay + 10)
            ircState.voteExecutionList.append((ActionUninit(), (NoCard(), NoCard())))


            ircState.checkDecision(vm)


            ircState.voteExecutionList shouldBe ListBuffer((ChooseOne(2), (NoCard(), NoCard())), (ActionUninit(), (NoCard(), NoCard())))
          }

          "Also clear all votes" in {
            var votesCleared = false
            val ircState = new IRCState
            val vm = new VoteManager(gs, vs, ai, ircState, validator) {
              override def makeChooseOneDecision(): (ActionVote, (HSCard, HSCard)) = {
                (ChooseOne(2), (NoCard(), NoCard()))
              }

              override def clearActionVotes(): Unit = {
                votesCleared = true
              }
            }
            ircState.myTurn = true
            ircState.isChooseOne = true
            ircState.chooseOneStartTimeStamp = System.currentTimeMillis() - (ircState.chooseOneDelay + 10)
            ircState.voteExecutionList.append((ActionUninit(), (NoCard(), NoCard())))


            ircState.checkDecision(vm)


            votesCleared shouldBe true
          }
        }
      }


      "If we're in a discover" - {

        "If the Discover time has not elapsed, do nothing" in {
          val ircState = new IRCState
          val vm = new VoteManager(gs, vs, ai, ircState, validator) {
            override def makeDiscoverDecision(): ActionVote = {
              ActionUninit()
            }
          }
          ircState.myTurn = true
          ircState.isDiscover = true
          ircState.discoverStartTimeStamp = System.currentTimeMillis()


          ircState.checkDecision(vm)


          ircState.voteExecutionList shouldBe empty
        }

        "If the Discover time has elapsed" - {

          "If the decision returns an ActionUninit, prepend Discover(1) to the execution list" in {
            val ircState = new IRCState
            val vm = new VoteManager(gs, vs, ai, ircState, validator) {
              override def makeDiscoverDecision(): ActionVote = {
                ActionUninit()
              }
            }
            ircState.myTurn = true
            ircState.isDiscover = true
            ircState.discoverStartTimeStamp = System.currentTimeMillis() - (ircState.discoverDelay + 10)
            ircState.voteExecutionList.append((ActionUninit(), (NoCard(), NoCard())))


            ircState.checkDecision(vm)


            ircState.voteExecutionList shouldBe ListBuffer((Discover(1), (NoCard(), NoCard())), (ActionUninit(), (NoCard(), NoCard())))
          }

          "If the decision returns a valid vote, prepend that vote to the execution list" in {
            val ircState = new IRCState
            val vm = new VoteManager(gs, vs, ai, ircState, validator) {
              override def makeDiscoverDecision(): ActionVote = {
                Discover(2)
              }

              override def clearActionVotes(): Unit = {}
            }
            ircState.myTurn = true
            ircState.isDiscover = true
            ircState.discoverStartTimeStamp = System.currentTimeMillis() - (ircState.discoverDelay + 10)
            ircState.voteExecutionList.append((ActionUninit(), (NoCard(), NoCard())))


            ircState.checkDecision(vm)


            ircState.voteExecutionList shouldBe ListBuffer((Discover(2), (NoCard(), NoCard())), (ActionUninit(), (NoCard(), NoCard())))
          }

          "Also clear all votes" in {
            var votesCleared = false
            val ircState = new IRCState
            val vm = new VoteManager(gs, vs, ai, ircState, validator) {
              override def makeDiscoverDecision(): ActionVote = {
                Discover(2)
              }

              override def clearActionVotes(): Unit = {
                votesCleared = true
              }
            }
            ircState.myTurn = true
            ircState.isDiscover = true
            ircState.discoverStartTimeStamp = System.currentTimeMillis() - (ircState.discoverDelay + 10)
            ircState.voteExecutionList.append((ActionUninit(), (NoCard(), NoCard())))


            ircState.checkDecision(vm)


            votesCleared shouldBe true
          }
        }
      }


      "If the full turn time has elapsed" - {

        "If the decision returns an ActionUninit, just end the turn" in {
          val ircState = new IRCState
          val vm = new VoteManager(gs, vs, ai, ircState, validator) {
            override def makeDecision(): List[(ActionVote, (HSCard, HSCard))] = {
              List((ActionUninit(), (NoCard(), NoCard())))
            }

            override def clearActionVotes(): Unit = {}
          }
          ircState.myTurn = true
          ircState.turnStartTimeStamp = System.currentTimeMillis() - (ircState.fullTurnDelay + 10)


          ircState.checkDecision(vm)


          ircState.voteExecutionList shouldBe ListBuffer((EndTurn(), (NoCard(), NoCard())))
        }

        "Also clear all action votes when decision returns ActionUninit" in {
          var votesCleared = false
          val ircState = new IRCState
          val vm = new VoteManager(gs, vs, ai, ircState, validator) {
            override def makeDecision(): List[(ActionVote, (HSCard, HSCard))] = {
              List((ActionUninit(), (NoCard(), NoCard())))
            }

            override def clearActionVotes(): Unit = {
              votesCleared = true
            }
          }
          ircState.myTurn = true
          ircState.turnStartTimeStamp = System.currentTimeMillis() - (ircState.fullTurnDelay + 10)


          ircState.checkDecision(vm)


          votesCleared shouldBe true
        }


        "If the decision returns a valid decision, add the decision to the execution list with an EndTurn at the end" in {
          val ircState = new IRCState
          val vm = new VoteManager(gs, vs, ai, ircState, validator) {
            override def makeDecision(): List[(ActionVote, (HSCard, HSCard))] = {
              List((CardPlay(1), (NoCard(), NoCard())), (CardPlay(2), (NoCard(), NoCard())))
            }

            override def clearActionVotes(): Unit = {}
          }
          ircState.myTurn = true
          ircState.turnStartTimeStamp = System.currentTimeMillis() - (ircState.fullTurnDelay + 10)


          ircState.checkDecision(vm)


          ircState.voteExecutionList shouldBe ListBuffer((CardPlay(1), (NoCard(), NoCard())), (CardPlay(2), (NoCard(), NoCard())), (EndTurn(), (NoCard(), NoCard())))
        }


        "Also clear all action votes when decision is valid" in {
          var votesCleared = false
          val ircState = new IRCState
          val vm = new VoteManager(gs, vs, ai, ircState, validator) {
            override def makeDecision(): List[(ActionVote, (HSCard, HSCard))] = {
              List((CardPlay(1), (NoCard(), NoCard())), (CardPlay(2), (NoCard(), NoCard())))
            }

            override def clearActionVotes(): Unit = {
              votesCleared = true
            }
          }
          ircState.myTurn = true
          ircState.turnStartTimeStamp = System.currentTimeMillis() - (ircState.fullTurnDelay + 10)


          ircState.checkDecision(vm)


          votesCleared shouldBe true
        }
      }


      "If no other valid decision states are active and it's been 5 seconds since the last decision check" - {
        "Set the lastVoteCheck timestamp to current time" in {
          val ircState = new IRCState
          val vm = new VoteManager(gs, vs, ai, ircState, validator) {
            override def makeDecision(): List[(ActionVote, (HSCard, HSCard))] = {
              List((CardPlay(1), (NoCard(), NoCard())), (Hurry(), (NoCard(), NoCard())), (CardPlay(2), (NoCard(), NoCard())))
            }

            override def clearActionVotes(): Unit = {}
          }
          ircState.myTurn = true
          ircState.turnStartTimeStamp = System.currentTimeMillis() - 10000
          ircState.lastVoteCheck = System.currentTimeMillis() - 10000


          ircState.checkDecision(vm)


          ircState.lastVoteCheck shouldBe >(System.currentTimeMillis() - 100)
          ircState.lastVoteCheck shouldBe <=(System.currentTimeMillis())
        }

        "If the decision has an Endturn as the last vote" - {

          "Add the decision to the execution list" in {
            val ircState = new IRCState
            val vm = new VoteManager(gs, vs, ai, ircState, validator) {
              override def makeDecision(): List[(ActionVote, (HSCard, HSCard))] = {
                List((CardPlay(1), (NoCard(), NoCard())), (CardPlay(2), (NoCard(), NoCard())), (EndTurn(), (NoCard(), NoCard())))
              }

              override def clearActionVotes(): Unit = {}
            }
            ircState.myTurn = true
            ircState.turnStartTimeStamp = System.currentTimeMillis() - 10000
            ircState.lastVoteCheck = System.currentTimeMillis() - 10000


            ircState.checkDecision(vm)


            ircState.voteExecutionList shouldBe ListBuffer((CardPlay(1), (NoCard(), NoCard())), (CardPlay(2), (NoCard(), NoCard())), (EndTurn(), (NoCard(), NoCard())))
          }

          "Clear all votes" in {
            var votesCleared = false
            val ircState = new IRCState
            val vm = new VoteManager(gs, vs, ai, ircState, validator) {
              override def makeDecision(): List[(ActionVote, (HSCard, HSCard))] = {
                List((CardPlay(1), (NoCard(), NoCard())), (CardPlay(2), (NoCard(), NoCard())), (EndTurn(), (NoCard(), NoCard())))
              }

              override def clearActionVotes(): Unit = {
                votesCleared = true
              }
            }
            ircState.myTurn = true
            ircState.turnStartTimeStamp = System.currentTimeMillis() - 10000
            ircState.lastVoteCheck = System.currentTimeMillis() - 10000


            ircState.checkDecision(vm)


            votesCleared shouldBe true
          }
        }

        "If the decision has a Hurry anywhere" - {

          "Add the decision to the execution list" in {
            val ircState = new IRCState
            val vm = new VoteManager(gs, vs, ai, ircState, validator) {
              override def makeDecision(): List[(ActionVote, (HSCard, HSCard))] = {
                List((CardPlay(1), (NoCard(), NoCard())), (Hurry(), (NoCard(), NoCard())), (CardPlay(2), (NoCard(), NoCard())))
              }

              override def clearActionVotes(): Unit = {}
            }
            ircState.myTurn = true
            ircState.turnStartTimeStamp = System.currentTimeMillis() - 10000
            ircState.lastVoteCheck = System.currentTimeMillis() - 10000


            ircState.checkDecision(vm)


            ircState.voteExecutionList shouldBe ListBuffer((CardPlay(1), (NoCard(), NoCard())), (Hurry(), (NoCard(), NoCard())), (CardPlay(2), (NoCard(), NoCard())))
          }

          "Clear all votes" in {
            var votesCleared = false
            val ircState = new IRCState
            val vm = new VoteManager(gs, vs, ai, ircState, validator) {
              override def makeDecision(): List[(ActionVote, (HSCard, HSCard))] = {
                List((CardPlay(1), (NoCard(), NoCard())), (Hurry(), (NoCard(), NoCard())), (CardPlay(2), (NoCard(), NoCard())))
              }

              override def clearActionVotes(): Unit = {
                votesCleared = true
              }
            }
            ircState.myTurn = true
            ircState.turnStartTimeStamp = System.currentTimeMillis() - 10000
            ircState.lastVoteCheck = System.currentTimeMillis() - 10000


            ircState.checkDecision(vm)


            votesCleared shouldBe true
          }
        }
      }
    }
  }


  "IrcState should check if a vote is ready to be executed" - {
    val gs = new GameState()
    val vs = new VoteState()
    val ai = new VoteAI(vs, gs)
    val validator = new VoteValidator(gs)
    val lp = new LogParser(gs)

    "If the logFileReader has been active in the last two seconds, do nothing" in {
      val ircState = new IRCState
      val vm = new VoteManager(gs, vs, ai, ircState, validator)
      val lfr = new LogFileReader(lp, gs)
      val hs = new HearthStone(gs)
      lfr.lastTimeActive = System.currentTimeMillis() - 1000
      ircState.voteExecutionList.append((CardPlay(1), (NoCard(), NoCard())))

      ircState.checkExecution(hs, lfr.lastTimeActive, vm, gs, validator)


      ircState.voteExecutionList shouldBe ListBuffer((CardPlay(1), (NoCard(), NoCard())))
    }


    "If the logFileReader has been idle for enough time and the execution list has something in it" - {

      "If the head of the execution list is..." - {

        "A mulligan vote" - {

          "Execute the mulligan vote" in {
            var mulliganExecuted = false
            val ircState = new IRCState
            val vm = new VoteManager(gs, vs, ai, ircState, validator) {
              override def clearActionVotes(): Unit = {}
            }
            val lfr = new LogFileReader(lp, gs)
            val hs = new HearthStone(gs) {
              override def executeMulligan(mulligan: MulliganVote, mulliganOptions: Int): Unit = {
                mulliganExecuted = true
              }
            }
            lfr.lastTimeActive = System.currentTimeMillis() - 10000
            ircState.voteExecutionList.append((MulliganVote(true, false, true, false), (NoCard(), NoCard())))


            ircState.checkExecution(hs, lfr.lastTimeActive, vm, gs, validator)


            mulliganExecuted shouldBe true
          }

          "Set mulligan state back to default (End the mulligan)" in {
            val ircState = new IRCState
            val vm = new VoteManager(gs, vs, ai, ircState, validator) {
              override def clearActionVotes(): Unit = {}
            }
            val lfr = new LogFileReader(lp, gs)
            val hs = new HearthStone(gs) {
              override def executeMulligan(mulligan: MulliganVote, mulliganOptions: Int): Unit = {}
            }
            lfr.lastTimeActive = System.currentTimeMillis() - 10000
            ircState.voteExecutionList.append((MulliganVote(true, false, true, false), (NoCard(), NoCard())))
            ircState.isMulligan = true
            ircState.mulliganStartTimeStamp = System.currentTimeMillis()
            ircState.mulliganOptions = 4
            ircState.mulliganComplete = false


            ircState.checkExecution(hs, lfr.lastTimeActive, vm, gs, validator)


            ircState.isMulligan shouldBe false
            ircState.mulliganStartTimeStamp shouldBe 0
            ircState.mulliganOptions shouldBe 0
            ircState.mulliganComplete shouldBe true
          }

          "Clear all action votes" in {
            var votesCleared = false
            val ircState = new IRCState
            val vm = new VoteManager(gs, vs, ai, ircState, validator) {
              override def clearActionVotes(): Unit = {
                votesCleared = true
              }
            }
            val lfr = new LogFileReader(lp, gs)
            val hs = new HearthStone(gs) {
              override def executeMulligan(mulligan: MulliganVote, mulliganOptions: Int): Unit = {}
            }
            lfr.lastTimeActive = System.currentTimeMillis() - 10000
            ircState.voteExecutionList.append((MulliganVote(true, false, true, false), (NoCard(), NoCard())))


            ircState.checkExecution(hs, lfr.lastTimeActive, vm, gs, validator)


            votesCleared shouldBe true
          }

          "Remove the mulligan from the execution list" in {
            val ircState = new IRCState
            val vm = new VoteManager(gs, vs, ai, ircState, validator) {
              override def clearActionVotes(): Unit = {}
            }
            val lfr = new LogFileReader(lp, gs)
            val hs = new HearthStone(gs) {
              override def executeMulligan(mulligan: MulliganVote, mulliganOptions: Int): Unit = {}
            }
            lfr.lastTimeActive = System.currentTimeMillis() - 10000
            ircState.voteExecutionList.append((MulliganVote(true, false, true, false), (NoCard(), NoCard())))


            ircState.checkExecution(hs, lfr.lastTimeActive, vm, gs, validator)


            ircState.voteExecutionList shouldBe empty
          }
        }


        "A discover vote" - {

          "Execute the discover vote" in {
            var discoverExecuted = false
            val ircState = new IRCState
            val vm = new VoteManager(gs, vs, ai, ircState, validator) {
              override def clearActionVotes(): Unit = {}
            }
            val lfr = new LogFileReader(lp, gs)
            val hs = new HearthStone(gs) {
              override def executeDiscover(decision: ActionVote): Unit = {
                discoverExecuted = true
              }
            }
            lfr.lastTimeActive = System.currentTimeMillis() - 10000
            ircState.voteExecutionList.append((Discover(2), (NoCard(), NoCard())))


            ircState.checkExecution(hs, lfr.lastTimeActive, vm, gs, validator)


            discoverExecuted shouldBe true
          }

          "Set discover state back to default (End the discover)" in {
            val ircState = new IRCState
            val vm = new VoteManager(gs, vs, ai, ircState, validator) {
              override def clearActionVotes(): Unit = {}
            }
            val lfr = new LogFileReader(lp, gs)
            val hs = new HearthStone(gs) {
              override def executeDiscover(decision: ActionVote): Unit = {}
            }
            lfr.lastTimeActive = System.currentTimeMillis() - 10000
            ircState.voteExecutionList.append((Discover(2), (NoCard(), NoCard())))
            ircState.isDiscover = true
            ircState.discoverStartTimeStamp = System.currentTimeMillis()


            ircState.checkExecution(hs, lfr.lastTimeActive, vm, gs, validator)


            ircState.isDiscover shouldBe false
            ircState.discoverStartTimeStamp shouldBe 0
          }

          "Clear all action votes" in {
            var votesCleared = false
            val ircState = new IRCState
            val vm = new VoteManager(gs, vs, ai, ircState, validator) {
              override def clearActionVotes(): Unit = {
                votesCleared = true
              }
            }
            val lfr = new LogFileReader(lp, gs)
            val hs = new HearthStone(gs) {
              override def executeDiscover(decision: ActionVote): Unit = {}
            }
            lfr.lastTimeActive = System.currentTimeMillis() - 10000
            ircState.voteExecutionList.append((Discover(2), (NoCard(), NoCard())))


            ircState.checkExecution(hs, lfr.lastTimeActive, vm, gs, validator)


            votesCleared shouldBe true
          }

          "Remove the discover from the execution list" in {
            val ircState = new IRCState
            val vm = new VoteManager(gs, vs, ai, ircState, validator) {
              override def clearActionVotes(): Unit = {}
            }
            val lfr = new LogFileReader(lp, gs)
            val hs = new HearthStone(gs) {
              override def executeDiscover(decision: ActionVote): Unit = {}
            }

              lfr.lastTimeActive = System.currentTimeMillis() - 10000
              ircState.voteExecutionList.append((Discover(2), (NoCard(), NoCard())))


              ircState.checkExecution(hs, lfr.lastTimeActive, vm, gs, validator)


              ircState.voteExecutionList shouldBe empty
            }
          }




        "A chooseOne vote" - {

          "Execute the chooseOne vote" in {
            var chooseOneExecuted = false
            val ircState = new IRCState
            val vm = new VoteManager(gs, vs, ai, ircState, validator) {
              override def clearActionVotes(): Unit = {}
            }
            val lfr = new LogFileReader(lp, gs)
            val hs = new HearthStone(gs) {
              override def executeChooseOne(decision: ChooseOne): Unit = {
                chooseOneExecuted = true
              }
            }
            lfr.lastTimeActive = System.currentTimeMillis() - 10000
            ircState.voteExecutionList.append((ChooseOne(2), (NoCard(), NoCard())))


            ircState.checkExecution(hs, lfr.lastTimeActive, vm, gs, validator)


            chooseOneExecuted shouldBe true
          }

          "Set chooseOne state back to default (End the chooseOne)" in {
            val ircState = new IRCState
            val vm = new VoteManager(gs, vs, ai, ircState, validator) {
              override def clearActionVotes(): Unit = {}
            }
            val lfr = new LogFileReader(lp, gs)
            val hs = new HearthStone(gs) {
              override def executeChooseOne(decision: ChooseOne): Unit = {}
            }
            lfr.lastTimeActive = System.currentTimeMillis() - 10000
            ircState.voteExecutionList.append((ChooseOne(2), (NoCard(), NoCard())))
            ircState.isChooseOne = true
            ircState.chooseOneStartTimeStamp = System.currentTimeMillis()


            ircState.checkExecution(hs, lfr.lastTimeActive, vm, gs, validator)


            ircState.isChooseOne shouldBe false
            ircState.chooseOneStartTimeStamp shouldBe 0
          }

          "Clear all action votes" in {
            var votesCleared = false
            val ircState = new IRCState
            val vm = new VoteManager(gs, vs, ai, ircState, validator) {
              override def clearActionVotes(): Unit = {
                votesCleared = true
              }
            }
            val lfr = new LogFileReader(lp, gs)
            val hs = new HearthStone(gs) {
              override def executeChooseOne(decision: ChooseOne): Unit = {}
            }
            lfr.lastTimeActive = System.currentTimeMillis() - 10000
            ircState.voteExecutionList.append((ChooseOne(2), (NoCard(), NoCard())))


            ircState.checkExecution(hs, lfr.lastTimeActive, vm, gs, validator)


            votesCleared shouldBe true
          }

          "Remove the chooseOne from the execution list" in {
            val ircState = new IRCState
            val vm = new VoteManager(gs, vs, ai, ircState, validator) {
              override def clearActionVotes(): Unit = {}
            }
            val lfr = new LogFileReader(lp, gs)
            val hs = new HearthStone(gs) {
              override def executeChooseOne(decision: ChooseOne): Unit = {}
            }

              lfr.lastTimeActive = System.currentTimeMillis() - 10000
              ircState.voteExecutionList.append((ChooseOne(2), (NoCard(), NoCard())))


              ircState.checkExecution(hs, lfr.lastTimeActive, vm, gs, validator)


              ircState.voteExecutionList shouldBe empty
            }
          }


        "A EndTurn vote" - {

          "Execute the EndTurn vote" in {
            var endTurnExecuted = false
            val ircState = new IRCState
            val vm = new VoteManager(gs, vs, ai, ircState, validator) {
              override def clearActionVotes(): Unit = {}
            }
            val lfr = new LogFileReader(lp, gs)
            val hs = new HearthStone(gs) {
              override def executeActionVote(voteWithST: (ActionVote, (HSCard, HSCard))): Unit = {
                endTurnExecuted = true
              }
            }
            lfr.lastTimeActive = System.currentTimeMillis() - 10000
            ircState.voteExecutionList.append((EndTurn(), (NoCard(), NoCard())))


            ircState.checkExecution(hs, lfr.lastTimeActive, vm, gs, validator)


            endTurnExecuted shouldBe true
          }

          "Clear all action votes" in {
            var votesCleared = false
            val ircState = new IRCState
            val vm = new VoteManager(gs, vs, ai, ircState, validator) {
              override def clearActionVotes(): Unit = {
                votesCleared = true
              }
            }
            val lfr = new LogFileReader(lp, gs)
            val hs = new HearthStone(gs) {
              override def executeActionVote(voteWithST: (ActionVote, (HSCard, HSCard))): Unit = {}
            }
            lfr.lastTimeActive = System.currentTimeMillis() - 10000
            ircState.voteExecutionList.append((EndTurn(), (NoCard(), NoCard())))


            ircState.checkExecution(hs, lfr.lastTimeActive, vm, gs, validator)


            votesCleared shouldBe true
          }

          "Remove the EndTurn from the execution list" in {
            val ircState = new IRCState
            val vm = new VoteManager(gs, vs, ai, ircState, validator) {
              override def clearActionVotes(): Unit = {}
            }
            val lfr = new LogFileReader(lp, gs)
            val hs = new HearthStone(gs) {
              override def executeActionVote(voteWithST: (ActionVote, (HSCard, HSCard))): Unit = {}
            }
            lfr.lastTimeActive = System.currentTimeMillis() - 10000
            ircState.voteExecutionList.append((EndTurn(), (NoCard(), NoCard())))


            ircState.checkExecution(hs, lfr.lastTimeActive, vm, gs, validator)


            ircState.voteExecutionList shouldBe empty
          }
        }

        "A Hurry vote" - {

          "Remove the Hurry vote from the execution list" in {
            val ircState = new IRCState
            val vm = new VoteManager(gs, vs, ai, ircState, validator) {
              override def clearActionVotes(): Unit = {}
            }
            val lfr = new LogFileReader(lp, gs)
            val hs = new HearthStone(gs)
            lfr.lastTimeActive = System.currentTimeMillis() - 10000
            ircState.voteExecutionList.append((Hurry(), (NoCard(), NoCard())))


            ircState.checkExecution(hs, lfr.lastTimeActive, vm, gs, validator)


            ircState.voteExecutionList shouldBe empty
          }
        }


        "A UpdateVote Command" - {

          "Execute the UpdateVote" in {
            var decisionUpdated = false
            val ircState = new IRCState
            val vm = new VoteManager(gs, vs, ai, ircState, validator) {
              override def updateDecision(updateVotes: UpdateVotes): Unit = {
                decisionUpdated = true
              }
            }
            val lfr = new LogFileReader(lp, gs)
            val hs = new HearthStone(gs)
            lfr.lastTimeActive = System.currentTimeMillis() - 10000
            ircState.voteExecutionList.append((UpdateVotes(Player(1), Player(2), new GameState()), (NoCard(), NoCard())))


            ircState.checkExecution(hs, lfr.lastTimeActive, vm, gs, validator)


            decisionUpdated shouldBe true
          }

          "Remove the UpdateVote from the execution list" in {
            val ircState = new IRCState
            val vm = new VoteManager(gs, vs, ai, ircState, validator) {
              override def updateDecision(updateVotes: UpdateVotes): Unit = {}
            }
            val lfr = new LogFileReader(lp, gs)
            val hs = new HearthStone(gs)
            lfr.lastTimeActive = System.currentTimeMillis() - 10000
            ircState.voteExecutionList.append((UpdateVotes(Player(1), Player(2), new GameState()), (NoCard(), NoCard())))


            ircState.checkExecution(hs, lfr.lastTimeActive, vm, gs, validator)


            ircState.voteExecutionList shouldBe empty
          }
        }


        "An ActionVote (A normal vote)" - {

          "If the validator confirms a valid vote" - {

            "Execute the vote" in {
              var actionExecuted = false
              val ircState = new IRCState
              val validator = new VoteValidator(gs) {
                override def isValidVote(vote: ActionVote, voteEntry: Boolean, voteExecution: Boolean): Boolean = {
                  true
                }
              }
              val vm = new VoteManager(gs, vs, ai, ircState, validator) {
                override def clearActionVotes(): Unit = {}
              }
              val lfr = new LogFileReader(lp, gs)
              val hs = new HearthStone(gs) {
                override def executeActionVote(voteWithST: (ActionVote, (HSCard, HSCard))): Unit = {
                  actionExecuted = true
                }
              }
              lfr.lastTimeActive = System.currentTimeMillis() - 10000
              ircState.voteExecutionList.append((CardPlay(1), (NoCard(), NoCard())))


              ircState.checkExecution(hs, lfr.lastTimeActive, vm, gs, validator)


              actionExecuted shouldBe true
            }

            "Clear all action votes" in {
              var votesCleared = false
              val ircState = new IRCState
              val validator = new VoteValidator(gs) {
                override def isValidVote(vote: ActionVote, voteEntry: Boolean, voteExecution: Boolean): Boolean = {
                  true
                }
              }
              val vm = new VoteManager(gs, vs, ai, ircState, validator) {
                override def clearActionVotes(): Unit = {
                  votesCleared = true
                }
              }
              val lfr = new LogFileReader(lp, gs)
              val hs = new HearthStone(gs) {
                override def executeActionVote(voteWithST: (ActionVote, (HSCard, HSCard))): Unit = {}
              }
              lfr.lastTimeActive = System.currentTimeMillis() - 10000
              ircState.voteExecutionList.append((CardPlay(1), (NoCard(), NoCard())))


              ircState.checkExecution(hs, lfr.lastTimeActive, vm, gs, validator)


              votesCleared shouldBe true
            }


            "Remove the vote from the execution list" in {
              val ircState = new IRCState
              val validator = new VoteValidator(gs) {
                override def isValidVote(vote: ActionVote, voteEntry: Boolean, voteExecution: Boolean): Boolean = {
                  true
                }
              }
              val vm = new VoteManager(gs, vs, ai, ircState, validator) {
                override def clearActionVotes(): Unit = {}
              }
              val lfr = new LogFileReader(lp, gs)
              val hs = new HearthStone(gs) {
                override def executeActionVote(voteWithST: (ActionVote, (HSCard, HSCard))): Unit = {}
              }

              lfr.lastTimeActive = System.currentTimeMillis() - 10000
              ircState.voteExecutionList.append((CardPlay(1), (NoCard(), NoCard())))


              ircState.checkExecution(hs, lfr.lastTimeActive, vm, gs, validator)


              ircState.voteExecutionList shouldNot contain(CardPlay(1), (NoCard(), NoCard()))
            }


            "Update votes after the vote has been executed by prepending a UpdateVotes to the execution list" in {
              val ircState = new IRCState
              val gs = new GameState()
              val validator = new VoteValidator(gs) {
                override def isValidVote(vote: ActionVote, voteEntry: Boolean, voteExecution: Boolean): Boolean = {
                  true
                }
              }
              val vm = new VoteManager(gs, vs, ai, ircState, validator) {
                override def clearActionVotes(): Unit = {}
              }
              val lfr = new LogFileReader(lp, gs)
              val hs = new HearthStone(gs) {
                override def executeActionVote(voteWithST: (ActionVote, (HSCard, HSCard))): Unit = {}
              }
              lfr.lastTimeActive = System.currentTimeMillis() - 10000
              ircState.voteExecutionList.append((CardPlay(1), (NoCard(), NoCard())))


              ircState.checkExecution(hs, lfr.lastTimeActive, vm, gs, validator)


              ircState.voteExecutionList shouldBe ListBuffer((UpdateVotes(gs.friendlyPlayer, gs.enemyPlayer, gs), (NoCard(), NoCard())))
            }


            "If the card is a chooseOne card, start chooseOne" in {
              val gs = new GameState(){
                override def isChooseOne(vote: ActionVote, st: (HSCard, HSCard)): Boolean = {
                  true
                }
              }
              val ircState = new IRCState
              val validator = new VoteValidator(gs) {
                override def isValidVote(vote: ActionVote, voteEntry: Boolean, voteExecution: Boolean): Boolean = {
                  true
                }
              }
              val vm = new VoteManager(gs, vs, ai, ircState, validator) {
                override def clearActionVotes(): Unit = {}
              }
              val lfr = new LogFileReader(lp, gs)
              val hs = new HearthStone(gs) {
                override def executeActionVote(voteWithST: (ActionVote, (HSCard, HSCard))): Unit = {}
              }
              lfr.lastTimeActive = System.currentTimeMillis() - 10000
              ircState.voteExecutionList.append((CardPlay(1), (NoCard(), NoCard())))


              ircState.checkExecution(hs, lfr.lastTimeActive, vm, gs, validator)


              ircState.isChooseOne shouldBe true
              ircState.chooseOneStartTimeStamp shouldBe <= (System.currentTimeMillis())
              ircState.chooseOneStartTimeStamp shouldBe > (System.currentTimeMillis() - 100)
            }


          }

          "If the vote is not a valid vote" - {

            "Do not execute the vote" in {
              var actionExecuted = false
              val ircState = new IRCState
              val validator = new VoteValidator(gs) {
                override def isValidVote(vote: ActionVote, voteEntry: Boolean, voteExecution: Boolean): Boolean = {
                  false
                }
              }
              val vm = new VoteManager(gs, vs, ai, ircState, validator) {
                override def clearActionVotes(): Unit = {}
              }
              val lfr = new LogFileReader(lp, gs)
              val hs = new HearthStone(gs) {
                override def executeActionVote(voteWithST: (ActionVote, (HSCard, HSCard))): Unit = {
                  actionExecuted = true
                }
              }
              lfr.lastTimeActive = System.currentTimeMillis() - 10000
              ircState.voteExecutionList.append((CardPlay(1), (NoCard(), NoCard())))


              ircState.checkExecution(hs, lfr.lastTimeActive, vm, gs, validator)


              actionExecuted shouldBe false
            }

            "Clear action votes" in {
              var votesCleared = false
              val validator = new VoteValidator(gs) {
                override def isValidVote(vote: ActionVote, voteEntry: Boolean, voteExecution: Boolean): Boolean = {
                  false
                }
              }
              val ircState = new IRCState
              val vm = new VoteManager(gs, vs, ai, ircState, validator) {
                override def clearActionVotes(): Unit = {
                  votesCleared = true
                }
              }
              val lfr = new LogFileReader(lp, gs)
              val hs = new HearthStone(gs) {
                override def executeActionVote(voteWithST: (ActionVote, (HSCard, HSCard))): Unit = {}
              }
              lfr.lastTimeActive = System.currentTimeMillis() - 10000
              ircState.voteExecutionList.append((CardPlay(1), (NoCard(), NoCard())))


              ircState.checkExecution(hs, lfr.lastTimeActive, vm, gs, validator)


              votesCleared shouldBe true
            }

            "Remove the vote from the execution list" in {
              val ircState = new IRCState
              val validator = new VoteValidator(gs) {
                override def isValidVote(vote: ActionVote, voteEntry: Boolean, voteExecution: Boolean): Boolean = {
                  false
                }
              }
              val vm = new VoteManager(gs, vs, ai, ircState, validator) {
                override def clearActionVotes(): Unit = {}
              }
              val lfr = new LogFileReader(lp, gs)
              val hs = new HearthStone(gs) {
                override def executeActionVote(voteWithST: (ActionVote, (HSCard, HSCard))): Unit = {}
              }
              lfr.lastTimeActive = System.currentTimeMillis() - 10000
              ircState.voteExecutionList.append((CardPlay(1), (NoCard(), NoCard())))


              ircState.checkExecution(hs, lfr.lastTimeActive, vm, gs, validator)


              ircState.voteExecutionList shouldNot contain(CardPlay(1), (NoCard(), NoCard()))
            }
          }
        }
      }
    }
  }
}
