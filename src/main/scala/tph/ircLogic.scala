//What am I currently working on?
//Current Thought: Working on adjusting votes. Left a line red.


//Problem: How will I balance the amount of votes required to execute a chaos mode, to execute a Hurry. How to balance numbers of moves executed?
//Solution: Have a log output of tallyMap as votes get executed. This will give me more data to see how drastic 1st and 2nd votes are.


//Problem: Multiple vote entries for a single vote on a single sender?

//Problem: when adding to bindMap, it should have a break() at the end that gets trimmed and replaced every time

//Theory crafting - Chaos is single decisions.
//Chaos Mode checks every 2 seconds for if highest vote is VOTE_PERCENTAGE(200%?)(double) ahead of 2nd vote.
//If no unanimous decision after 10 seconds just do highest vote.
//Only one vote per person allowed in chaos mode


package tph

import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorRef, ActorSystem}
import akka.event.LoggingReceive
import akka.pattern.ask
import akka.util.Timeout

//import tph.Controller.ChangeMenu
import tph.IrcMessages._
import tph.LogFileEvents.DiscoverOption

import scala.collection.mutable._
import scala.collection.{Set, mutable}
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._


/**
  * Created by RC on 8/16/2016.
  */
object ircLogic {


  val TURNSTART = "Turn Start"
  val TURNEND = "Turn End"
  val MENU_DECIDE = "Menu Decide"
  val DECIDE = "Decide"
  val CHECK = "Check"
  val STARTGAME = "Start Game"
  val GAMEOVER = "Game Over"
  val MULLIGAN = "Mulligan"
  val MULLIGAN_OPTION = "NewMulliganOption"
  val DELAY_AMOUNT = 10000
  val VOTE_DIFFERENCE_FACTOR = 1.3
  val TEMP_DECISION_VOTE_ADJUSTMENT_FACTOR = 1.5
  val CONCEDE_PERCENTAGE = .8
  val HURRY_VOTER_PERCENTAGE = .7


  var currentMenu = "mainMenu"
  var previousMenu = ""

  var gameMode = "ordered"
  var inGame = false
  var myTurn = false
  var hisTurn = false
  var active = false
  var hurrySpeed = false
  var doneWithMulligan = false

  //Just to create it
  var previousDecision: Any = None

  case class PreviousDecision()

}


class ircLogic(system: ActorSystem, controller: ActorRef, hearthstone: ActorRef) extends Actor with akka.actor.ActorLogging {
  //Input comes in
  //Input is organized into Arrays?
  import ircLogic._


  //Voter Maps and Count Maps
  //Menus
  val mainMenuVoterMap = mutable.Map[String, Any]()
  val mainMenuVoteCount = mutable.Map[Any, Int]()
  val playMenuVoterMap = mutable.Map[String, Any]()
  val playMenuVoteCount = mutable.Map[Any, Int]()
  val collectionMenuVoterMap = mutable.Map[String, Any]()
  val collectionMenuVoteCount = mutable.Map[Any, Int]()
  val enchantMenuVoterMap = mutable.Map[String, Any]()
  val enchantMenuVoteCount = mutable.Map[Any, Int]()
  val questMenuVoterMap = mutable.Map[String, Any]()
  val questMenuVoteCount = mutable.Map[Any, Int]()


  //In Game
  val voterMap = mutable.Map[String, ListBuffer[Any]]()
  val bindMap = mutable.Map[String, ListBuffer[Any]]()
  val futureMap = mutable.Map[String, ListBuffer[Any]]()
  var tallyMap = mutable.Map[Any, Int]()
  val emoteCounts = mutable.Map[Any, Int]()
  val emoteVoterMap = mutable.Map[String, Any]()
  val speedCounts = mutable.Map[Any, Int]()
  val speedVoterMap = mutable.Map[String, Any]()
  val concedeVoterMap = mutable.Map[String, Any]()
  var mulliganVoteMap = mutable.Map[String, Array[Int]]()
  var mulliganCount = mutable.Map[Array[Int], Int]()


  //IrcLogic Variables
  var averageVoterList: ListBuffer[Int] = new ListBuffer[Int]
  var averageVoterAmount = 0
  var possibleDiscoverOptions: ListBuffer[Int] = new ListBuffer[Int]
  var savedGameStatus = new Array[Player](2)
  var mulliganOptions = 0
  var mulligan = false

  override def receive: Receive = LoggingReceive({
    case "Activate" => active = true
    case "Start" => {
      active = true
      system.scheduler.scheduleOnce(10000.milli, this.self, MENU_DECIDE)
    }
    case STARTGAME => StartGame()
    case GAMEOVER => if (inGame) GameOver()
    case ChangeMenu(pastMenu, changeToMenu) =>
      previousMenu = currentMenu
      currentMenu = changeToMenu

    //TEST PURPOSES:
    case "Skip Mulligan" => doneWithMulligan = true


    case CHECK => if (inGame) Check()
    case MULLIGAN => if (inGame) Mulligan()
    case MULLIGAN_OPTION => mulliganOptions = mulliganOptions + 1


    case DiscoverOption(option: Int) =>
      possibleDiscoverOptions.append(option)

    case x => {


      if (active) {
        if (currentMenu == "mainMenu") {
          x match {
            case MENU_DECIDE =>
              SimpleDecide(mainMenuVoterMap, mainMenuVoteCount)

            case (Play(thisMenu), sender: String) =>
              mainMenuVoterMap(sender) = Play(thisMenu)

            case (OpenPacks(), sender: String) =>
              mainMenuVoterMap(sender) = OpenPacks()

            case (Shop(), sender: String) =>
              mainMenuVoterMap(sender) = Shop()

            case (QuestLog(), sender: String) =>
              mainMenuVoterMap(sender) = QuestLog()

            case (Collection(thisMenu), sender: String) =>
              mainMenuVoterMap(sender) = Collection(thisMenu)

            case _ =>
          }
        }

        if (currentMenu == "playMenu") {
          x match {
            case MENU_DECIDE =>
              SimpleDecide(playMenuVoterMap, playMenuVoteCount)


            case (Casual(), sender: String) =>
              playMenuVoterMap(sender) = Casual()
            case (Ranked(), sender: String) =>
              playMenuVoterMap(sender) = Ranked()
            case (Play(thisMenu), sender: String) =>
              playMenuVoterMap(sender) = Play(thisMenu)
            case (Collection(thisMenu), sender: String) =>
              playMenuVoterMap(sender) = Collection(thisMenu)
            case (Back(thisMenu), sender: String) =>
              playMenuVoterMap(sender) = Back(thisMenu)
            case (Deck(deckNumber), sender: String) =>
              playMenuVoterMap(sender) = Deck(deckNumber)
            case (FirstPage(), sender: String) =>
              playMenuVoterMap(sender) = FirstPage()
            case (SecondPage(), sender: String) =>
              playMenuVoterMap(sender) = SecondPage()
            case _ =>
          }
        }

        if (currentMenu == "questMenu") {
          x match {
            case MENU_DECIDE =>
              SimpleDecide(questMenuVoterMap, questMenuVoteCount)

            case (Quest(number: Int), sender: String) =>
              questMenuVoterMap(sender) = Quest(number)
            case (Back(menu), sender: String) =>
              questMenuVoterMap(sender) = Back(menu)
          }
        }



        if (inGame) {
          x match {
            //Emote Type
            case (Greetings(), sender: String) =>
              EmoteVoteEntry(Greetings(), sender)

            case (Thanks(), sender: String) =>
              EmoteVoteEntry(Thanks(), sender)

            case (WellPlayed(), sender: String) =>
              EmoteVoteEntry(WellPlayed(), sender)

            case (Wow(), sender: String) =>
              EmoteVoteEntry(Wow(), sender)

            case (Oops(), sender: String) =>
              EmoteVoteEntry(Oops(), sender)

            case (Threaten(), sender: String) =>
              EmoteVoteEntry(Threaten(), sender)

            case (Concede(), sender: String) =>
              concedeVoterMap(sender) = Concede()


            case TURNSTART => if (inGame) TurnStart()
            case TURNEND => TurnEnd()
            case DECIDE => Decide()


            case _ =>

          }

          if (mulligan) {
            x match {
              case (MulliganVote(vote), sender: String) =>
                mulliganVoteMap(sender) = vote

              case _ =>
            }
          }

          if (myTurn) {
            x match {
              case CommandVote(builtCommand) => {
                builtCommand.name match {
                  case "Discover" if (builtCommand.target >= 1 && builtCommand.target <= possibleDiscoverOptions.size) =>
                    VoteEntry(Discover(builtCommand.target), builtCommand.sender)


                  //Battlecry Play Type
                  case "CardPlayWithFriendlyOption" if (builtCommand.card >= 1 && builtCommand.card <= savedGameStatus(0).hand.length && builtCommand.target >= 1 && builtCommand.target <= savedGameStatus(0).board.length) =>
                    VoteEntry(CardPlayWithFriendlyOption(builtCommand.card: Int, builtCommand.target: Int), builtCommand.sender)
                  case "CardPlayWithFriendlyFaceOption" if (builtCommand.card >= 1 && builtCommand.card <= savedGameStatus(0).hand.length) =>
                    VoteEntry(CardPlayWithFriendlyFaceOption(builtCommand.card: Int), builtCommand.sender)
                  case "CardPlayWithEnemyOption" if (builtCommand.target >= 1 && builtCommand.target <= savedGameStatus(1).board.length) =>
                    VoteEntry(CardPlayWithEnemyOption(builtCommand.card: Int, builtCommand.target: Int), builtCommand.sender)
                  case "CardPlayWithEnemyFaceOption" if (builtCommand.card >= 1 && builtCommand.card <= savedGameStatus(0).hand.length) =>
                    VoteEntry(CardPlayWithEnemyFaceOption(builtCommand.card: Int), builtCommand.sender)

                  //Battlecry and Position Type
                  case "CardPlayWithFriendlyOptionWithPosition" if (builtCommand.card >= 1 && builtCommand.card <= savedGameStatus(0).hand.length && builtCommand.target >= 1 && builtCommand.target <= savedGameStatus(0).board.length && builtCommand.spot >= 1 && builtCommand.spot <= savedGameStatus(0).board.length) =>
                    VoteEntry(CardPlayWithFriendlyOption(builtCommand.card: Int, builtCommand.target: Int), builtCommand.sender)
                  case "CardPlayWithFriendlyFaceOptionWithPosition" if (builtCommand.card >= 1 && builtCommand.card <= savedGameStatus(0).hand.length && builtCommand.spot >= 1 && builtCommand.spot <= savedGameStatus(0).hand.length) =>
                    VoteEntry(CardPlayWithFriendlyFaceOption(builtCommand.card: Int), builtCommand.sender)
                  case "CardPlayWithEnemyOptionWithPosition" if (builtCommand.card >= 1 && builtCommand.card <= savedGameStatus(0).hand.length && builtCommand.target >= 1 && builtCommand.target <= savedGameStatus(1).board.length && builtCommand.spot >= 1 && builtCommand.spot <= savedGameStatus(0).board.length) =>
                    VoteEntry(CardPlayWithEnemyOption(builtCommand.card: Int, builtCommand.target: Int), builtCommand.sender)
                  case "CardPlayWithEnemyFaceOptionWithPosition" if (builtCommand.card >= 1 && builtCommand.card <= savedGameStatus(0).hand.length && builtCommand.spot >= 1 && builtCommand.spot <= savedGameStatus(0).board.length) =>
                    VoteEntry(CardPlayWithEnemyFaceOption(builtCommand.card: Int), builtCommand.sender)


                  //Normal Turn Play Type
                  case "CardPlay"
                    if (builtCommand.card >= 1 && builtCommand.card <= savedGameStatus(0).hand.length) =>
                    VoteEntry(CardPlay(builtCommand.card: Int), builtCommand.sender)
                  case "CardPlayWithPosition" if (builtCommand.card >= 1 && builtCommand.card <= savedGameStatus(0).hand.length && builtCommand.spot >= 1 && builtCommand.spot <= savedGameStatus(0).hand.length + 1) =>
                    VoteEntry(CardPlayWithPosition(builtCommand.card: Int, builtCommand.spot: Int), builtCommand.sender)
                  case "CardPlayWithFriendlyBoardTarget" if (builtCommand.card >= 1 && builtCommand.card <= savedGameStatus(0).hand.length && builtCommand.target >= 1 && builtCommand.target <= savedGameStatus(0).board.length) =>
                    VoteEntry(CardPlayWithFriendlyBoardTarget(builtCommand.card: Int, builtCommand.target: Int), builtCommand.sender)
                  case "CardPlayWithEnemyBoardTarget" if (builtCommand.card >= 1 && builtCommand.card <= savedGameStatus(0).hand.length && builtCommand.target >= 1 && builtCommand.target <= savedGameStatus(1).board.length) =>
                    VoteEntry(CardPlayWithEnemyBoardTarget(builtCommand.card: Int, builtCommand.target: Int), builtCommand.sender)
                  case "CardPlayWithFriendlyFaceTarget" if (builtCommand.card >= 1 && builtCommand.card <= savedGameStatus(0).hand.length) =>
                    VoteEntry(CardPlayWithFriendlyFaceTarget(builtCommand.card: Int), builtCommand.sender)
                  case "CardPlayWithEnemyFaceTarget" if (builtCommand.card >= 1 && builtCommand.card <= savedGameStatus(0).hand.length) =>
                    VoteEntry(CardPlayWithEnemyFaceTarget(builtCommand.card: Int), builtCommand.sender)


                  case "HeroPower" =>
                    VoteEntry(HeroPower(), builtCommand.sender)
                  case "HeroPowerWithEnemyFace" =>
                    VoteEntry(HeroPowerWithEnemyFace(), builtCommand.sender)
                  case "HeroPowerWithEnemyTarget" =>
                    VoteEntry(HeroPowerWithEnemyTarget(builtCommand.target: Int), builtCommand.sender)
                  case "HeroPowerWithFriendlyFace" =>
                    VoteEntry(HeroPowerWithFriendlyFace(), builtCommand.sender)
                  case "HeroPowerWithFriendlyTarget" =>
                    VoteEntry(HeroPowerWithFriendlyTarget(builtCommand.target: Int), builtCommand.sender)


                  //Attack Type
                  case "NormalAttack" if (builtCommand.card >= 1 && builtCommand.card <= savedGameStatus(0).board.length && builtCommand.target >= 1 && builtCommand.target <= savedGameStatus(1).board.length) =>
                    VoteEntry(NormalAttack(builtCommand.card: Int, builtCommand.target: Int), builtCommand.sender)

                  case "FaceAttack" if (builtCommand.target >= 1 && builtCommand.target <= savedGameStatus(1).board.length) =>
                    VoteEntry(FaceAttack(builtCommand.target: Int), builtCommand.sender)

                  case "NormalAttackWithEnemyTarget" if (builtCommand.target >= 1 && builtCommand.target <= savedGameStatus(0).board.length) =>
                    VoteEntry(NormalAttackToFace(builtCommand.target: Int), builtCommand.sender)

                  case "FaceAttackWithEnemyFaceTarget" =>
                    VoteEntry(FaceAttackToFace(), builtCommand.sender)

                  case _ =>
                }
              }



              //Always Type
              case (Hurry(), sender: String) =>
                VoteEntry(Hurry(), sender)

              case (EndTurn(), sender: String) =>
                VoteEntry(EndTurn(), sender)

              case (Bind(), sender: String) =>
                VoteEntry(Bind(), sender)
              case (Future(), sender: String) =>
                VoteEntry(Future(), sender)

              case _ =>

            }
          }
        }
      }
    }
  })


  def StartGame(): Unit = {
    inGame = true
    mulliganOptions = 0
    system.scheduler.scheduleOnce(3000.milli, this.self, CHECK)
  }

  def GameOver(): Unit = {
    if (inGame) {
      inGame = false
      myTurn = false
      hisTurn = false
      if (previousMenu != "") {
        hearthstone ! ChangeMenu(currentMenu, previousMenu)
      }
      system.scheduler.scheduleOnce(45.seconds, this.self, MENU_DECIDE)
      hearthstone ! "Game Over"
    }
  }

  def TurnStart(): Unit = {


    GetGameStatus()
    if (doneWithMulligan)
      system.scheduler.scheduleOnce(45.seconds, this.self, DECIDE)
    myTurn = true
  }

  def TurnEnd(): Unit = {
    myTurn = false
  }


  def Decide(): Unit = {
    //If in an ordered game:
    //Set active = false to not take anymore votes
    //Set active = true at end of decide
    //Sends as many decisions to hearthstone as averageAmountOfMoves
    //Calculates decision
    //Removes previousDecision from bindMap (Must be removed only AFTER calculation)
    //Sends hearthstone the decision
    //Removes old decision from voterMap
    //After all decisions made, clears maps associated with normal votes


    if (myTurn) {
      if (gameMode == "ordered") {
        active = false
        for (a <- 0 until CalculateAmountOfMoves()) {
          val decision = CalculateDecision()
          if (previousDecision != None) {
            RemoveBindVote("all", PreviousDecision(), false)
            RemoveFutureVote("all", PreviousDecision(), false)
            tallyMap(PreviousDecision()) = 0
          }
          hearthstone ! decision
          RemoveNormalVote("all", decision)
          previousDecision = decision
          TimeUnit.SECONDS.sleep(4)
          AdjustVotes()

          tallyMap.clear()
        }
        voterMap.clear()
        bindMap.clear()
        futureMap.clear()
        tallyMap.clear()
        previousDecision = None
        self ! "Activate"

        if (!hurrySpeed)
          hearthstone ! EndTurn()
        hurrySpeed = false
      }
    }
  }

  def CalculateAmountOfMoves(): Int = {

    val numberOfVotes = new ListBuffer[Int]

    voterMap foreach {
      case (sender, voteLists) =>
        val fillerVotes = voterMap(sender).count(_ == Bind()) + voterMap(sender).count(_ == Future()) + voterMap(sender).count(_ == EndTurn()) + voterMap(sender).count(_ == Hurry()) + voterMap(sender).count(_ == Bound()) + voterMap(sender).count(_ == FutureBound())
        var moves = voterMap(sender).size - fillerVotes


        if (bindMap.isDefinedAt(sender)) {
          val breaks = bindMap(sender).count(_ == Break())
          val bindMoves = bindMap(sender).size
          val bindList = bindMoves - breaks
          moves = moves + bindList
        }

        if (futureMap.isDefinedAt(sender)) {
          val breaks = futureMap(sender).count(_ == Break())
          val futureMoves = futureMap(sender).size
          val futureList = futureMoves - breaks
          moves = moves + futureList
        }

        numberOfVotes.append(moves)
    }


    val totalVotes = numberOfVotes.sum
    val totalPeople = numberOfVotes.size
    if (totalPeople != 0) {
      val averageNumberOfVotes = totalVotes / totalPeople
      return averageNumberOfVotes
    }
    else return 0
  }

  def AdjustVotes(): Unit = {

    val oldGameStatus = savedGameStatus
    val myOldHand = oldGameStatus(0).hand
    val myOldBoard = oldGameStatus(0).board
    val hisOldBoard = oldGameStatus(1).board
    GetGameStatus()
    val newGameStatus = savedGameStatus
    val myNewHand = newGameStatus(0).hand
    val myNewBoard = newGameStatus(0).board
    val hisNewBoard = newGameStatus(1).board



    //For each element in myOldHand, map the oldPos to the newPos if myNewHand contains the same id
    //If it does not contain the same id, -1 will be mapped
    val myChangedHandMap = mutable.Map[Int, Int]()
    myOldHand foreach {
      case x =>
        val index = myOldHand.indexWhere(_.id == x.id)
        if (myNewHand.isDefinedAt(index)) {
          if (myOldHand(index).id != myNewHand(index).id)
            myChangedHandMap(myOldHand(index).handPosition) = myNewHand.find(_.id == myOldHand(index).id).getOrElse(new Card).handPosition
        }
    }

    //For each changed card, search voterMap for the vote and reassign the modified position
    myChangedHandMap foreach {
      case (oldPos, newPos) => {
        //If myNewHand does contain the card
        if (newPos != -1) {
          voterMap foreach {
            case (sender, voteList) =>
              voteList foreach {
                case vote =>
                  val index = voteList.indexWhere(_ == vote)
                  if (vote != previousDecision) {
                    vote match {

                      //Normal Turn Play Type
                      case CardPlay(card) if card == oldPos =>
                        voterMap(sender)(index) = CardPlay(newPos)
                      case CardPlayWithPosition(card, position) if card == oldPos =>
                        voterMap(sender)(index) = CardPlayWithPosition(newPos, position)
                      case CardPlayWithFriendlyBoardTarget(card, target) if card == oldPos =>
                        voterMap(sender)(index) = CardPlayWithFriendlyBoardTarget(newPos, target)
                      case CardPlayWithEnemyBoardTarget(card, target) if card == oldPos =>
                        voterMap(sender)(index) = CardPlayWithEnemyBoardTarget(newPos, target)
                      case CardPlayWithFriendlyFaceTarget(card) if card == oldPos =>
                        voterMap(sender)(index) = CardPlayWithFriendlyFaceTarget(newPos)
                      case CardPlayWithEnemyFaceTarget(card) if card == oldPos =>
                        voterMap(sender)(index) = CardPlayWithEnemyFaceTarget(newPos)

                      //Batlecry Play Type
                      case CardPlayWithFriendlyOption(card, boardTarget) if card == oldPos =>
                        voterMap(sender)(index) = CardPlayWithFriendlyOption(newPos, boardTarget)
                      case CardPlayWithFriendlyFaceOption(card) if card == oldPos =>
                        voterMap(sender)(index) = CardPlayWithFriendlyFaceOption(newPos)
                      case CardPlayWithEnemyOption(card, boardTarget) if card == oldPos =>
                        voterMap(sender)(index) = CardPlayWithEnemyOption(newPos, boardTarget)
                      case CardPlayWithEnemyFaceOption(card) if card == oldPos =>
                        voterMap(sender)(index) = CardPlayWithEnemyFaceOption(newPos)

                      //Battlecry and Position Type
                      case CardPlayWithFriendlyOptionWithPosition(card, target, position) if card == oldPos =>
                        voterMap(sender)(index) = CardPlayWithFriendlyOptionWithPosition(newPos, target, position)
                      case CardPlayWithFriendlyFaceOptionWithPosition(card, position) if card == oldPos =>
                        voterMap(sender)(index) = CardPlayWithFriendlyFaceOptionWithPosition(newPos, position)
                      case CardPlayWithEnemyOptionWithPosition(card, target, position) if card == oldPos =>
                        voterMap(sender)(index) = CardPlayWithEnemyOptionWithPosition(newPos, target, position)
                      case CardPlayWithEnemyFaceOptionWithPosition(card, position) if card == oldPos =>
                        voterMap(sender)(index) = CardPlayWithEnemyFaceOptionWithPosition(newPos, position)
                      case _ => println("Unexpected vote: " + vote)
                    }
                  }
                  else voterMap(sender)(index) = PreviousDecision()
              }
          }

          //If vote follows a PreviousDecision() but is before a Break(), do not adjust votes

          bindMap foreach {
            case (sender, voteList) =>
              voteList foreach {
                case vote =>
                  val index = voteList.indexWhere(_ == vote)
                  if (vote != previousDecision) {
                    vote match {

                      //Normal Turn Play Type
                      case CardPlay(card) if card == oldPos =>
                        bindMap(sender)(index) = CardPlay(newPos)
                      case CardPlayWithPosition(card, position) if card == oldPos =>
                        bindMap(sender)(index) = CardPlayWithPosition(newPos, position)
                      case CardPlayWithFriendlyBoardTarget(card, target) if card == oldPos =>
                        bindMap(sender)(index) = CardPlayWithFriendlyBoardTarget(newPos, target)
                      case CardPlayWithEnemyBoardTarget(card, target) if card == oldPos =>
                        bindMap(sender)(index) = CardPlayWithEnemyBoardTarget(newPos, target)
                      case CardPlayWithFriendlyFaceTarget(card) if card == oldPos =>
                        bindMap(sender)(index) = CardPlayWithFriendlyFaceTarget(newPos)
                      case CardPlayWithEnemyFaceTarget(card) if card == oldPos =>
                        bindMap(sender)(index) = CardPlayWithEnemyFaceTarget(newPos)

                      //Batlecry Play Type
                      case CardPlayWithFriendlyOption(card, boardTarget) if card == oldPos =>
                        bindMap(sender)(index) = CardPlayWithFriendlyOption(newPos, boardTarget)
                      case CardPlayWithFriendlyFaceOption(card) if card == oldPos =>
                        bindMap(sender)(index) = CardPlayWithFriendlyFaceOption(newPos)
                      case CardPlayWithEnemyOption(card, boardTarget) if card == oldPos =>
                        bindMap(sender)(index) = CardPlayWithEnemyOption(newPos, boardTarget)
                      case CardPlayWithEnemyFaceOption(card) if card == oldPos =>
                        bindMap(sender)(index) = CardPlayWithEnemyFaceOption(newPos)

                      //Battlecry and Position Type
                      case CardPlayWithFriendlyOptionWithPosition(card, target, position) if card == oldPos =>
                        bindMap(sender)(index) = CardPlayWithFriendlyOptionWithPosition(newPos, target, position)
                      case CardPlayWithFriendlyFaceOptionWithPosition(card, position) if card == oldPos =>
                        bindMap(sender)(index) = CardPlayWithFriendlyFaceOptionWithPosition(newPos, position)
                      case CardPlayWithEnemyOptionWithPosition(card, target, position) if card == oldPos =>
                        bindMap(sender)(index) = CardPlayWithEnemyOptionWithPosition(newPos, target, position)
                      case CardPlayWithEnemyFaceOptionWithPosition(card, position) if card == oldPos =>
                        bindMap(sender)(index) = CardPlayWithEnemyFaceOptionWithPosition(newPos, position)
                      case _ => println("Unexpected vote: " + vote)
                    }
                  }
                  else bindMap(sender)(index) = PreviousDecision()
              }
          }
        }

        futureMap foreach {
          case (sender, voteList) =>
            var futureActive = false
            voteList foreach {
              case vote =>
                val index = voteList.indexWhere(_ == vote)
                if (vote == previousDecision) {
                  futureActive = true
                  futureMap(sender)(index) = PreviousDecision()
                }
                if (vote == Break() || !voteList.isDefinedAt(index + 1)) {
                  futureActive = false
                }


                if (vote != previousDecision) {
                  if (!futureActive) {
                    vote match {

                      //Normal Turn Play Type
                      case CardPlay(card) if card == oldPos =>
                        futureMap(sender)(index) = CardPlay(newPos)
                      case CardPlayWithPosition(card, position) if card == oldPos =>
                        futureMap(sender)(index) = CardPlayWithPosition(newPos, position)
                      case CardPlayWithFriendlyBoardTarget(card, target) if card == oldPos =>
                        futureMap(sender)(index) = CardPlayWithFriendlyBoardTarget(newPos, target)
                      case CardPlayWithEnemyBoardTarget(card, target) if card == oldPos =>
                        futureMap(sender)(index) = CardPlayWithEnemyBoardTarget(newPos, target)
                      case CardPlayWithFriendlyFaceTarget(card) if card == oldPos =>
                        futureMap(sender)(index) = CardPlayWithFriendlyFaceTarget(newPos)
                      case CardPlayWithEnemyFaceTarget(card) if card == oldPos =>
                        futureMap(sender)(index) = CardPlayWithEnemyFaceTarget(newPos)

                      //Batlecry Play Type
                      case CardPlayWithFriendlyOption(card, boardTarget) if card == oldPos =>
                        futureMap(sender)(index) = CardPlayWithFriendlyOption(newPos, boardTarget)
                      case CardPlayWithFriendlyFaceOption(card) if card == oldPos =>
                        futureMap(sender)(index) = CardPlayWithFriendlyFaceOption(newPos)
                      case CardPlayWithEnemyOption(card, boardTarget) if card == oldPos =>
                        futureMap(sender)(index) = CardPlayWithEnemyOption(newPos, boardTarget)
                      case CardPlayWithEnemyFaceOption(card) if card == oldPos =>
                        futureMap(sender)(index) = CardPlayWithEnemyFaceOption(newPos)

                      //Battlecry and Position Type
                      case CardPlayWithFriendlyOptionWithPosition(card, target, position) if card == oldPos =>
                        futureMap(sender)(index) = CardPlayWithFriendlyOptionWithPosition(newPos, target, position)
                      case CardPlayWithFriendlyFaceOptionWithPosition(card, position) if card == oldPos =>
                        futureMap(sender)(index) = CardPlayWithFriendlyFaceOptionWithPosition(newPos, position)
                      case CardPlayWithEnemyOptionWithPosition(card, target, position) if card == oldPos =>
                        futureMap(sender)(index) = CardPlayWithEnemyOptionWithPosition(newPos, target, position)
                      case CardPlayWithEnemyFaceOptionWithPosition(card, position) if card == oldPos =>
                        futureMap(sender)(index) = CardPlayWithEnemyFaceOptionWithPosition(newPos, position)
                      case _ => println("Unexpected vote: " + vote)
                    }
                  }
                }
            }
        }



        //If myNewHand does not contain the card anymore
        if (newPos == -1) {
          voterMap foreach {
            case (sender, voteList) =>
              voteList foreach {
                case vote =>
                  val index = voterMap(sender).indexWhere(_ == vote)
                  if (vote != previousDecision) {
                    vote match {
                      case CardPlay(card) if card == oldPos =>
                        voterMap(sender).remove(index)
                      case CardPlayWithPosition(card, position) if card == oldPos =>
                        voterMap(sender).remove(index)
                      case CardPlayWithFriendlyBoardTarget(card, target) if card == oldPos =>
                        voterMap(sender).remove(index)
                      case CardPlayWithEnemyBoardTarget(card, target) if card == oldPos =>
                        voterMap(sender).remove(index)
                      case CardPlayWithFriendlyFaceTarget(card) if card == oldPos =>
                        voterMap(sender).remove(index)
                      case CardPlayWithEnemyFaceTarget(card) if card == oldPos =>
                        voterMap(sender).remove(index)

                      //Batlecry Play Type
                      case CardPlayWithFriendlyOption(card, boardTarget) if card == oldPos =>
                        voterMap(sender).remove(index)
                      case CardPlayWithFriendlyFaceOption(card) if card == oldPos =>
                        voterMap(sender).remove(index)
                      case CardPlayWithEnemyOption(card, boardTarget) if card == oldPos =>
                        voterMap(sender).remove(index)
                      case CardPlayWithEnemyFaceOption(card) if card == oldPos =>
                        voterMap(sender).remove(index)

                      //Battlecry and Position Type
                      case CardPlayWithFriendlyOptionWithPosition(card, target, position) if card == oldPos =>
                        voterMap(sender).remove(index)
                      case CardPlayWithFriendlyFaceOptionWithPosition(card, position) if card == oldPos =>
                        voterMap(sender).remove(index)
                      case CardPlayWithEnemyOptionWithPosition(card, target, position) if card == oldPos =>
                        voterMap(sender).remove(index)
                      case CardPlayWithEnemyFaceOptionWithPosition(card, position) if card == oldPos =>
                        voterMap(sender).remove(index)
                      case _ => println("Unexpected vote: " + vote)
                    }
                  }
                  else voterMap(sender)(index) = PreviousDecision()
              }
          }

          bindMap foreach {
            case (sender, voteList) =>
              voteList foreach {
                case vote =>
                  val index = bindMap(sender).indexWhere(_ == vote)
                  if (vote != previousDecision) {
                    vote match {
                      case CardPlay(card) if card == oldPos =>
                        bindMap(sender).remove(index)
                      case CardPlayWithPosition(card, position) if card == oldPos =>
                        bindMap(sender).remove(index)
                      case CardPlayWithFriendlyBoardTarget(card, target) if card == oldPos =>
                        bindMap(sender).remove(index)
                      case CardPlayWithEnemyBoardTarget(card, target) if card == oldPos =>
                        bindMap(sender).remove(index)
                      case CardPlayWithFriendlyFaceTarget(card) if card == oldPos =>
                        bindMap(sender).remove(index)
                      case CardPlayWithEnemyFaceTarget(card) if card == oldPos =>
                        bindMap(sender).remove(index)

                      //Batlecry Play Type
                      case CardPlayWithFriendlyOption(card, boardTarget) if card == oldPos =>
                        bindMap(sender).remove(index)
                      case CardPlayWithFriendlyFaceOption(card) if card == oldPos =>
                        bindMap(sender).remove(index)
                      case CardPlayWithEnemyOption(card, boardTarget) if card == oldPos =>
                        bindMap(sender).remove(index)
                      case CardPlayWithEnemyFaceOption(card) if card == oldPos =>
                        bindMap(sender).remove(index)

                      //Battlecry and Position Type
                      case CardPlayWithFriendlyOptionWithPosition(card, target, position) if card == oldPos =>
                        bindMap(sender).remove(index)
                      case CardPlayWithFriendlyFaceOptionWithPosition(card, position) if card == oldPos =>
                        bindMap(sender).remove(index)
                      case CardPlayWithEnemyOptionWithPosition(card, target, position) if card == oldPos =>
                        bindMap(sender).remove(index)
                      case CardPlayWithEnemyFaceOptionWithPosition(card, position) if card == oldPos =>
                        bindMap(sender).remove(index)
                      case _ => println("Unexpected vote: " + vote)
                    }
                  }
                  else bindMap(sender)(index) = PreviousDecision()
              }
          }

          futureMap foreach {
            case (sender, voteList) =>
              var futureActive = false
              voteList foreach {
                case vote =>
                  val index = voteList.indexWhere(_ == vote)
                  if (vote == previousDecision) {
                    futureActive = true
                  }
                  if (vote == Break() || !voteList.isDefinedAt(index + 1)) {
                    futureActive = false
                  }


                  if (vote != previousDecision) {
                    if (!futureActive) {
                      vote match {

                        //Normal Turn Play Type
                        case CardPlay(card) if card == oldPos =>
                          futureMap(sender).remove(index)
                        case CardPlayWithPosition(card, position) if card == oldPos =>
                          futureMap(sender).remove(index)
                        case CardPlayWithFriendlyBoardTarget(card, target) if card == oldPos =>
                          futureMap(sender).remove(index)
                        case CardPlayWithEnemyBoardTarget(card, target) if card == oldPos =>
                          futureMap(sender).remove(index)
                        case CardPlayWithFriendlyFaceTarget(card) if card == oldPos =>
                          futureMap(sender).remove(index)
                        case CardPlayWithEnemyFaceTarget(card) if card == oldPos =>
                          futureMap(sender).remove(index)

                        //Batlecry Play Type
                        case CardPlayWithFriendlyOption(card, boardTarget) if card == oldPos =>
                          futureMap(sender).remove(index)
                        case CardPlayWithFriendlyFaceOption(card) if card == oldPos =>
                          futureMap(sender).remove(index)
                        case CardPlayWithEnemyOption(card, boardTarget) if card == oldPos =>
                          futureMap(sender).remove(index)
                        case CardPlayWithEnemyFaceOption(card) if card == oldPos =>
                          futureMap(sender).remove(index)

                        //Battlecry and Position Type
                        case CardPlayWithFriendlyOptionWithPosition(card, target, position) if card == oldPos =>
                          futureMap(sender).remove(index)
                        case CardPlayWithFriendlyFaceOptionWithPosition(card, position) if card == oldPos =>
                          futureMap(sender).remove(index)
                        case CardPlayWithEnemyOptionWithPosition(card, target, position) if card == oldPos =>
                          futureMap(sender).remove(index)
                        case CardPlayWithEnemyFaceOptionWithPosition(card, position) if card == oldPos =>
                          futureMap(sender).remove(index)
                        case _ => println("Unexpected vote: " + vote)
                      }
                    }
                  }
              }
          }
        }
      }

        //For each element in myOldBoard, map the oldPos to the newPos if myNewBoard contains the same id
        //If it does not contain the same id, -1 will be mapped
        val myChangedBoardMap = mutable.Map[Int, Int]()
        myOldBoard foreach {
          case x =>
            val index = myOldBoard.indexWhere(_.id == x.id)
            if (myNewBoard.isDefinedAt(index)) {
              if (myOldBoard(index).id != myNewBoard(index).id)
                myChangedBoardMap(myOldBoard(index).handPosition) = myNewBoard.find(_.id == myOldBoard(index).id).getOrElse(new Card).handPosition
            }
        }

        myChangedBoardMap foreach {
          case (oldPos, newPos) => {
            //If myNewBoard does contain the card
            if (newPos != -1) {
              voterMap foreach {
                case (sender, voteList) =>
                  voteList foreach {
                    case vote =>
                      val index = voteList.indexWhere(_ == vote)
                      if (vote != previousDecision) {
                        vote match {

                          //Card Play Type
                          case CardPlayWithFriendlyOption(card, boardTarget) if boardTarget == oldPos =>
                            voterMap(sender)(index) = CardPlayWithFriendlyOption(card, newPos)

                          //Battlecry Option with Position Type
                          case CardPlayWithFriendlyOptionWithPosition(card, target, position) if target == oldPos && position == oldPos =>
                            voterMap(sender)(index) = CardPlayWithFriendlyOptionWithPosition(card, newPos, newPos)
                          case CardPlayWithFriendlyOptionWithPosition(card, target, position) if target == oldPos && position != oldPos =>
                            voterMap(sender)(index) = CardPlayWithFriendlyOptionWithPosition(card, newPos, position)
                          case CardPlayWithFriendlyOptionWithPosition(card, target, position) if target != oldPos && position == oldPos =>
                            voterMap(sender)(index) = CardPlayWithFriendlyOptionWithPosition(card, target, newPos)
                          case CardPlayWithFriendlyFaceOptionWithPosition(card, position) if position == oldPos =>
                            voterMap(sender)(index) = CardPlayWithFriendlyFaceOptionWithPosition(card, newPos)
                          case CardPlayWithEnemyOptionWithPosition(card, target, position) if position == oldPos =>
                            voterMap(sender)(index) = CardPlayWithEnemyOptionWithPosition(card, target, newPos)
                          case CardPlayWithEnemyFaceOptionWithPosition(card, position) if position == oldPos =>
                            voterMap(sender)(index) = CardPlayWithEnemyFaceOptionWithPosition(card, newPos)

                          //Normal Turn Play Type
                          case CardPlayWithPosition(card, position) if position == oldPos =>
                            voterMap(sender)(index) = CardPlayWithPosition(card, newPos)
                          case CardPlayWithFriendlyBoardTarget(card, target) if target == oldPos =>
                            voterMap(sender)(index) = CardPlayWithFriendlyBoardTarget(card, newPos)
                          case HeroPowerWithFriendlyTarget(target) if target == oldPos =>
                            voterMap(sender)(index) = HeroPowerWithFriendlyTarget(newPos)

                          //Attack Type
                          case NormalAttack(friendlyPosition, enemyPosition) if friendlyPosition == oldPos =>
                            voterMap(sender)(index) = NormalAttack(newPos, enemyPosition)
                          case NormalAttackToFace(position) if position == oldPos =>
                            voterMap(sender)(index) = NormalAttackToFace(newPos)
                          case _ => println("Unexpected vote: " + vote)
                        }
                      }
                      else voterMap(sender)(index) = PreviousDecision()
                  }
              }

              //Cycle through BindMap
              bindMap foreach {
                case (sender, voteList) =>
                  voteList foreach {
                    case vote =>
                      val index = voteList.indexWhere(_ == vote)
                      if (vote != previousDecision) {
                        vote match {

                          //Card Play Type
                          case CardPlayWithFriendlyOption(card, boardTarget) if boardTarget == oldPos =>
                            bindMap(sender)(index) = CardPlayWithFriendlyOption(card, newPos)

                          //Battlecry Option with Position Type
                          case CardPlayWithFriendlyOptionWithPosition(card, target, position) if target == oldPos && position == oldPos =>
                            bindMap(sender)(index) = CardPlayWithFriendlyOptionWithPosition(card, newPos, newPos)
                          case CardPlayWithFriendlyOptionWithPosition(card, target, position) if target == oldPos && position != oldPos =>
                            bindMap(sender)(index) = CardPlayWithFriendlyOptionWithPosition(card, newPos, position)
                          case CardPlayWithFriendlyOptionWithPosition(card, target, position) if target != oldPos && position == oldPos =>
                            bindMap(sender)(index) = CardPlayWithFriendlyOptionWithPosition(card, target, newPos)
                          case CardPlayWithFriendlyFaceOptionWithPosition(card, position) if position == oldPos =>
                            bindMap(sender)(index) = CardPlayWithFriendlyFaceOptionWithPosition(card, newPos)
                          case CardPlayWithEnemyOptionWithPosition(card, target, position) if position == oldPos =>
                            bindMap(sender)(index) = CardPlayWithEnemyOptionWithPosition(card, target, newPos)
                          case CardPlayWithEnemyFaceOptionWithPosition(card, position) if position == oldPos =>
                            bindMap(sender)(index) = CardPlayWithEnemyFaceOptionWithPosition(card, newPos)

                          //Normal Turn Play Type
                          case CardPlayWithPosition(card, position) if position == oldPos =>
                            bindMap(sender)(index) = CardPlayWithPosition(card, newPos)
                          case CardPlayWithFriendlyBoardTarget(card, target) if target == oldPos =>
                            bindMap(sender)(index) = CardPlayWithFriendlyBoardTarget(card, newPos)
                          case HeroPowerWithFriendlyTarget(target) if target == oldPos =>
                            bindMap(sender)(index) = HeroPowerWithFriendlyTarget(newPos)

                          //Attack Type
                          case NormalAttack(friendlyPosition, enemyPosition) if friendlyPosition == oldPos =>
                            bindMap(sender)(index) = NormalAttack(newPos, enemyPosition)
                          case NormalAttackToFace(position) if position == oldPos =>
                            bindMap(sender)(index) = NormalAttackToFace(newPos)
                          case _ => println("Unexpected vote: " + vote)
                        }
                      }
                      else bindMap(sender)(index) = PreviousDecision()
                  }
              }


              futureMap foreach {
                case (sender, voteList) =>
                  var futureActive = false
                  voteList foreach {
                    case vote =>
                      val index = voteList.indexWhere(_ == vote)
                      if (vote == previousDecision) {
                        futureActive = true
                      }
                      if (vote == Break() || !voteList.isDefinedAt(index + 1)) {
                        futureActive = false
                      }


                      if (vote != previousDecision) {
                        if (!futureActive) {
                          vote match {


                            //Card Play Type
                            case CardPlayWithFriendlyOption(card, boardTarget) if boardTarget == oldPos =>
                              futureMap(sender)(index) = CardPlayWithFriendlyOption(card, newPos)

                            //Battlecry Option with Position Type
                            case CardPlayWithFriendlyOptionWithPosition(card, target, position) if target == oldPos && position == oldPos =>
                              futureMap(sender)(index) = CardPlayWithFriendlyOptionWithPosition(card, newPos, newPos)
                            case CardPlayWithFriendlyOptionWithPosition(card, target, position) if target == oldPos && position != oldPos =>
                              futureMap(sender)(index) = CardPlayWithFriendlyOptionWithPosition(card, newPos, position)
                            case CardPlayWithFriendlyOptionWithPosition(card, target, position) if target != oldPos && position == oldPos =>
                              futureMap(sender)(index) = CardPlayWithFriendlyOptionWithPosition(card, target, newPos)
                            case CardPlayWithFriendlyFaceOptionWithPosition(card, position) if position == oldPos =>
                              futureMap(sender)(index) = CardPlayWithFriendlyFaceOptionWithPosition(card, newPos)
                            case CardPlayWithEnemyOptionWithPosition(card, target, position) if position == oldPos =>
                              futureMap(sender)(index) = CardPlayWithEnemyOptionWithPosition(card, target, newPos)
                            case CardPlayWithEnemyFaceOptionWithPosition(card, position) if position == oldPos =>
                              futureMap(sender)(index) = CardPlayWithEnemyFaceOptionWithPosition(card, newPos)

                            //Normal Turn Play Type
                            case CardPlayWithPosition(card, position) if position == oldPos =>
                              futureMap(sender)(index) = CardPlayWithPosition(card, newPos)
                            case CardPlayWithFriendlyBoardTarget(card, target) if target == oldPos =>
                              futureMap(sender)(index) = CardPlayWithFriendlyBoardTarget(card, newPos)
                            case HeroPowerWithFriendlyTarget(target) if target == oldPos =>
                              futureMap(sender)(index) = HeroPowerWithFriendlyTarget(newPos)

                            //Attack Type
                            case NormalAttack(friendlyPosition, enemyPosition) if friendlyPosition == oldPos =>
                              futureMap(sender)(index) = NormalAttack(newPos, enemyPosition)
                            case NormalAttackToFace(position) if position == oldPos =>
                              futureMap(sender)(index) = NormalAttackToFace(newPos)
                            case _ => println("Unexpected vote: " + vote)
                          }
                        }
                      }
                  }
              }
            }

            //If myNewBoard does not contain the card
            if (newPos == -1) {
              voterMap foreach {
                case (sender, voteList) =>
                  voteList foreach {
                    case vote =>
                      val index = voteList.indexWhere(_ == vote)
                      if (vote != previousDecision) {
                        vote match {
                          case CardPlayWithFriendlyOption(card, boardTarget) if boardTarget == oldPos =>
                            voterMap(sender).remove(index)

                          //Battlecry Option with Position Type
                          case CardPlayWithFriendlyOptionWithPosition(card, target, position) if target == oldPos =>
                            voterMap(sender).remove(index)

                          //Normal Turn Play Type
                          case CardPlayWithFriendlyBoardTarget(card, target) if target == oldPos =>
                            voterMap(sender).remove(index)
                          case HeroPowerWithFriendlyTarget(target) if target == oldPos =>
                            voterMap(sender).remove(index)

                          //Attack Type
                          case NormalAttack(friendlyPosition, enemyPosition) if friendlyPosition == oldPos =>
                            voterMap(sender).remove(index)
                          case NormalAttackToFace(position) if position == oldPos =>
                            voterMap(sender).remove(index)
                          case _ => println("Unexpected vote: " + vote)
                        }
                      }
                      else voterMap(sender)(index) = PreviousDecision()
                  }
              }

              bindMap foreach {
                case (sender, voteList) =>
                  voteList foreach {
                    case vote =>
                      val index = voteList.indexWhere(_ == vote)
                      if (vote != previousDecision) {
                        vote match {
                          case CardPlayWithFriendlyOption(card, boardTarget) if boardTarget == oldPos =>
                            bindMap(sender).remove(index)

                          //Battlecry Option with Position Type
                          case CardPlayWithFriendlyOptionWithPosition(card, target, position) if target == oldPos =>
                            bindMap(sender).remove(index)

                          //Normal Turn Play Type
                          case CardPlayWithFriendlyBoardTarget(card, target) if target == oldPos =>
                            bindMap(sender).remove(index)
                          case HeroPowerWithFriendlyTarget(target) if target == oldPos =>
                            bindMap(sender).remove(index)

                          //Attack Type
                          case NormalAttack(friendlyPosition, enemyPosition) if friendlyPosition == oldPos =>
                            bindMap(sender).remove(index)
                          case NormalAttackToFace(position) if position == oldPos =>
                            bindMap(sender).remove(index)
                          case _ => println("Unexpected vote: " + vote)
                        }
                      }
                      else bindMap(sender)(index) = PreviousDecision()
                  }
              }


              futureMap foreach {
                case (sender, voteList) =>
                  var futureActive = false
                  voteList foreach {
                    case vote =>
                      val index = voteList.indexWhere(_ == vote)
                      if (vote == previousDecision) {
                        futureActive = true
                      }
                      if (vote == Break() || !voteList.isDefinedAt(index + 1)) {
                        futureActive = false
                      }


                      if (vote != previousDecision) {
                        if (!futureActive) {
                          vote match {


                            case CardPlayWithFriendlyOption(card, boardTarget) if boardTarget == oldPos =>
                              futureMap(sender).remove(index)

                            //Battlecry Option with Position Type
                            case CardPlayWithFriendlyOptionWithPosition(card, target, position) if target == oldPos =>
                              futureMap(sender).remove(index)

                            //Normal Turn Play Type
                            case CardPlayWithFriendlyBoardTarget(card, target) if target == oldPos =>
                              futureMap(sender).remove(index)
                            case HeroPowerWithFriendlyTarget(target) if target == oldPos =>
                              futureMap(sender).remove(index)

                            //Attack Type
                            case NormalAttack(friendlyPosition, enemyPosition) if friendlyPosition == oldPos =>
                              futureMap(sender).remove(index)
                            case NormalAttackToFace(position) if position == oldPos =>
                              futureMap(sender).remove(index)
                            case _ => println("Unexpected vote: " + vote)
                          }
                        }
                      }
                  }
              }
            }
          }
        }



        //For each element in hisOldBoard, map the oldPos to the newPos if hisNewBoard contains the same id
        //If it does not contain the same id, -1 will be mapped
        val hisChangedBoardMap = mutable.Map[Int, Int]()
        hisOldBoard foreach {
          case x =>
            val index = hisOldBoard.indexWhere(_.id == x.id)
            if (hisNewBoard.isDefinedAt(index)) {
              if (hisOldBoard(index).id != hisNewBoard(index).id)
                hisChangedBoardMap(hisOldBoard(index).handPosition) = hisNewBoard.find(_.id == hisOldBoard(index).id).getOrElse(new Card).handPosition
            }
        }


        hisChangedBoardMap foreach {
          case (oldPos, newPos) => {
            //If hisNewBoard does contain the card
            if (newPos != -1) {
              voterMap foreach {
                case (sender, voteList) =>
                  voteList foreach {
                    case vote =>
                      val index = voteList.indexWhere(_ == vote)
                      if (vote != previousDecision) {
                        vote match {
                          case CardPlayWithEnemyOption(card, boardTarget) if boardTarget == oldPos =>
                            voterMap(sender)(index) = CardPlayWithEnemyOption(card, newPos)

                          //Battlecry Option with Position Type
                          case CardPlayWithEnemyOptionWithPosition(card, target, position) if target == oldPos =>
                            voterMap(sender)(index) = CardPlayWithEnemyOptionWithPosition(card, newPos, position)

                          //Normal Turn Play Type
                          case CardPlayWithEnemyBoardTarget(card, target) if target == oldPos =>
                            voterMap(sender)(index) = CardPlayWithEnemyBoardTarget(card, newPos)
                          case HeroPowerWithEnemyTarget(target) =>
                            voterMap(sender)(index) = HeroPowerWithEnemyTarget(newPos)


                          //Attack Type
                          case NormalAttack(friendlyPosition, enemyPosition) if enemyPosition == oldPos =>
                            voterMap(sender)(index) = NormalAttack(friendlyPosition, newPos)
                          case FaceAttack(position) if position == oldPos =>
                            voterMap(sender)(index) = FaceAttack(newPos)
                          case _ => println("Unexpected vote: " + vote)
                        }
                      }
                      else voterMap(sender).remove(index)
                  }
              }

              bindMap foreach {
                case (sender, voteList) =>
                  voteList foreach {
                    case vote =>
                      val index = voteList.indexWhere(_ == vote)
                      if (vote != previousDecision) {
                        vote match {
                          case CardPlayWithEnemyOption(card, boardTarget) if boardTarget == oldPos =>
                            bindMap(sender)(index) = CardPlayWithEnemyOption(card, newPos)

                          //Battlecry Option with Position Type
                          case CardPlayWithEnemyOptionWithPosition(card, target, position) if target == oldPos =>
                            bindMap(sender)(index) = CardPlayWithEnemyOptionWithPosition(card, newPos, position)

                          //Normal Turn Play Type
                          case CardPlayWithEnemyBoardTarget(card, target) if target == oldPos =>
                            bindMap(sender)(index) = CardPlayWithEnemyBoardTarget(card, newPos)
                          case HeroPowerWithEnemyTarget(target) =>
                            bindMap(sender)(index) = HeroPowerWithEnemyTarget(newPos)


                          //Attack Type
                          case NormalAttack(friendlyPosition, enemyPosition) if enemyPosition == oldPos =>
                            bindMap(sender)(index) = NormalAttack(friendlyPosition, newPos)
                          case FaceAttack(position) if position == oldPos =>
                            bindMap(sender)(index) = FaceAttack(newPos)
                          case _ => println("Unexpected vote: " + vote)
                        }
                      }
                  }
              }


              futureMap foreach {
                case (sender, voteList) =>
                  var futureActive = false
                  voteList foreach {
                    case vote =>
                      val index = voteList.indexWhere(_ == vote)
                      if (vote == previousDecision) {
                        futureActive = true
                      }
                      if (vote == Break() || !voteList.isDefinedAt(index + 1)) {
                        futureActive = false
                      }


                      if (vote != previousDecision) {
                        if (!futureActive) {
                          vote match {


                            case CardPlayWithEnemyOption(card, boardTarget) if boardTarget == oldPos =>
                              futureMap(sender)(index) = CardPlayWithEnemyOption(card, newPos)

                            //Battlecry Option with Position Type
                            case CardPlayWithEnemyOptionWithPosition(card, target, position) if target == oldPos =>
                              futureMap(sender)(index) = CardPlayWithEnemyOptionWithPosition(card, newPos, position)

                            //Normal Turn Play Type
                            case CardPlayWithEnemyBoardTarget(card, target) if target == oldPos =>
                              futureMap(sender)(index) = CardPlayWithEnemyBoardTarget(card, newPos)
                            case HeroPowerWithEnemyTarget(target) =>
                              futureMap(sender)(index) = HeroPowerWithEnemyTarget(newPos)


                            //Attack Type
                            case NormalAttack(friendlyPosition, enemyPosition) if enemyPosition == oldPos =>
                              futureMap(sender)(index) = NormalAttack(friendlyPosition, newPos)
                            case FaceAttack(position) if position == oldPos =>
                              futureMap(sender)(index) = FaceAttack(newPos)
                            case _ => println("Unexpected vote: " + vote)
                          }
                        }
                      }
                  }
              }

            }
          }

            //If hisNewBoard does not contain the card anymore
            if (newPos == -1) {
              voterMap foreach {
                case (sender, voteList) =>
                  voteList foreach {
                    case vote =>
                      val index = voterMap(sender).indexWhere(_ == vote)
                      if (vote != previousDecision) {
                        vote match {
                          case CardPlayWithEnemyOption(card, boardTarget) if boardTarget == oldPos =>
                            voterMap(sender).remove(index)

                          //Battlecry Option with Position Type
                          case CardPlayWithEnemyOptionWithPosition(card, target, position) if target == oldPos =>
                            voterMap(sender).remove(index)

                          //Normal Turn Play Type
                          case CardPlayWithEnemyBoardTarget(card, target) if target == oldPos =>
                            voterMap(sender).remove(index)
                          case HeroPowerWithEnemyTarget(target) =>
                            voterMap(sender).remove(index)


                          //Attack Type
                          case NormalAttack(friendlyPosition, enemyPosition) if enemyPosition == oldPos =>
                            voterMap(sender).remove(index)
                          case FaceAttack(position) if position == oldPos =>
                            voterMap(sender).remove(index)
                          case _ => println("Unexpected vote: " + vote)
                        }
                      }
                      else voterMap(sender).remove(index)
                  }
              }

              bindMap foreach {
                case (sender, voteList) =>
                  voteList foreach {
                    case vote =>
                      val index = bindMap(sender).indexWhere(_ == vote)
                      if (vote != previousDecision) {
                        vote match {
                          case CardPlayWithEnemyOption(card, boardTarget) if boardTarget == oldPos =>
                            bindMap(sender).remove(index)

                          //Battlecry Option with Position Type
                          case CardPlayWithEnemyOptionWithPosition(card, target, position) if target == oldPos =>
                            bindMap(sender).remove(index)

                          //Normal Turn Play Type
                          case CardPlayWithEnemyBoardTarget(card, target) if target == oldPos =>
                            bindMap(sender).remove(index)
                          case HeroPowerWithEnemyTarget(target) =>
                            bindMap(sender).remove(index)


                          //Attack Type
                          case NormalAttack(friendlyPosition, enemyPosition) if enemyPosition == oldPos =>
                            bindMap(sender).remove(index)
                          case FaceAttack(position) if position == oldPos =>
                            bindMap(sender).remove(index)
                          case _ => println("Unexpected vote: " + vote)
                        }
                      }
                  }
              }


              futureMap foreach {
                case (sender, voteList) =>
                  var futureActive = false
                  voteList foreach {
                    case vote =>
                      val index = voteList.indexWhere(_ == vote)
                      if (vote == previousDecision) {
                        futureActive = true
                      }
                      if (vote == Break() || !voteList.isDefinedAt(index + 1)) {
                        futureActive = false
                      }


                      if (vote != previousDecision) {
                        if (!futureActive) {
                          vote match {


                            case CardPlayWithEnemyOption(card, boardTarget) if boardTarget == oldPos =>
                              futureMap(sender).remove(index)

                            //Battlecry Option with Position Type
                            case CardPlayWithEnemyOptionWithPosition(card, target, position) if target == oldPos =>
                              futureMap(sender).remove(index)

                            //Normal Turn Play Type
                            case CardPlayWithEnemyBoardTarget(card, target) if target == oldPos =>
                              futureMap(sender).remove(index)
                            case HeroPowerWithEnemyTarget(target) =>
                              futureMap(sender).remove(index)


                            //Attack Type
                            case NormalAttack(friendlyPosition, enemyPosition) if enemyPosition == oldPos =>
                              futureMap(sender).remove(index)
                            case FaceAttack(position) if position == oldPos =>
                              futureMap(sender).remove(index)
                            case _ => println("Unexpected vote: " + vote)
                          }
                        }
                      }
                  }
              }
            }

        }
    }
  }


  def CalculateDecision(): Any = {
    //Creates a temporary decision
    //Finds that temporary decision in bindMap and adds 10 value for any vote before the temp decision (In order to encourage an associated previous move)
    //Retallys and reports highest vote
    //TallyVotes and return the max value


    TallyVotes()
    if (tallyMap.isDefinedAt(EndTurn())) {
      tallyMap.remove(EndTurn())
    }
    if (tallyMap.nonEmpty) {
      val tempMaxValue: Int = tallyMap.values.max
      val tempDecision = tallyMap(tallyMap.find(_._2 == tempMaxValue).getOrElse(None, -2)._1)
      bindMap foreach {
        case (sender, voteList) =>
          val index = voteList.indexWhere(_ == tempDecision)
          if (voteList.isDefinedAt(index - 1)) {
            tallyMap(voteList(index - 1)) = tallyMap(voteList(index - 1)) + 10
          }
      }
      val maxValue = tallyMap.values.max
      val decision = tallyMap.find(_._2 == tempMaxValue).getOrElse(None, -2)._1

      return decision
    }
    else return None
  }


  def TallyVotes(): Unit = {
    //Go through each voterMap list and assign 10 points to tallyMap for each vote
    //Go through each bindMap list and assign 10 points to tallyMap for the first vote after every break.
    //Assign 0 points to all other votes
    //If a vote is equal to the previous decision, assign 20 points to the next vote.
    //DecisionMaintenance() will remove any votes that == previous decision. (Instead of doing it in this method)


    voterMap foreach {
      case (sender, voteList) =>
        voteList foreach {
          case vote =>
            if (vote != Bind() && vote != Break() && vote != EndTurn() && vote != Bound() && vote != Hurry() && vote != Future() && vote != FutureBound()) {
              if (tallyMap.isDefinedAt(vote)) {
                tallyMap(vote) = tallyMap(vote) + 10
              }
              else tallyMap(vote) = 10
            }
        }
    }


    bindMap foreach {
      case (sender, list) =>

        list foreach {
          case x =>

            val index = list.indexWhere(_ == x)
            if (index == 0) {
              if (tallyMap.isDefinedAt(x)) {
                tallyMap(x) = tallyMap(x) + 10
              }
              else tallyMap(x) = 10
            }

            if (list.isDefinedAt(index + 1)) {
              val nextVote = list(index + 1)

              if (x == Break()) {
                if (tallyMap.isDefinedAt(x)) {
                  tallyMap(nextVote) = tallyMap(nextVote) + 10
                }
                else tallyMap(nextVote) = 10
              }



              if (x == PreviousDecision()) {
                if (tallyMap.isDefinedAt(x)) {
                  tallyMap(nextVote) = tallyMap(nextVote) + 20
                }
                else {
                  tallyMap(nextVote) = 20
                }
              }
            }
        }
    }

    futureMap foreach {
      case (sender, list) =>

        list foreach {
          case x =>

            val index = list.indexWhere(_ == x)
            if (index == 0) {
              if (tallyMap.isDefinedAt(x)) {
                tallyMap(x) = tallyMap(x) + 10
              }
              else tallyMap(x) = 10
            }

            if (list.isDefinedAt(index + 1)) {
              val nextVote = list(index + 1)

              if (x == Break()) {
                if (tallyMap.isDefinedAt(x)) {
                  tallyMap(nextVote) = tallyMap(nextVote) + 10
                }
                else tallyMap(nextVote) = 10
              }



              if (x == PreviousDecision()) {
                if (tallyMap.isDefinedAt(nextVote)) {
                  tallyMap(nextVote) = tallyMap(nextVote) + 20
                }
                else {
                  tallyMap(nextVote) = 20
                }
              }
            }
        }
    }

  }


  def Check(): Unit = {
    //Check if emotes need to be ran
    if (inGame) {
      val emoteReturn = CheckEmoteList()
      if (emoteReturn != None) hearthstone ! emoteReturn

      //Check if concede has enough power to execute
      val currentVoterSet = CreateCurrentVoterSet()
      if (concedeVoterMap.size > (currentVoterSet.size * CONCEDE_PERCENTAGE) && currentVoterSet.size > averageVoterAmount / 3) {
        hearthstone ! Concede()
      }
      CheckHurryVotes()

      //Reschedule Check()
      system.scheduler.scheduleOnce(3000.milli, this.self, CHECK)
    }
  }


  def CheckHurryVotes(): Unit = {
    //If voterMap(sender) contains Hurry() add one to hurryTally
    // If hurrytally = HURRY_VOTER_PERCENTAGE of completeVoterSet execute a hurry
    var hurryTally = 0
    var currentVoterSet = CreateCurrentVoterSet()

    voterMap foreach {
      case (sender, voteList) =>
        if (voteList.contains(Hurry())) {
          hurryTally = hurryTally + 1
        }

        if (hurryTally >= currentVoterSet.size * HURRY_VOTER_PERCENTAGE && currentVoterSet.size > averageVoterAmount / 3) {
          this.self ! DECIDE
          RemoveNormalVote("all", Hurry())
          hurrySpeed = true
        }
    }

  }

  def CheckEmoteList(): Any = {
    //If 33% of voters want to emote
    if (!emoteVoterMap.isEmpty) {
      val emoteTally = emoteVoterMap.size


      if (emoteTally >= voterMap.size / 3 && voterMap.size >= averageVoterAmount / 3) {
        val maxValue = emoteCounts.values.max
        val maxVote = emoteCounts.find(_._2 == maxValue).getOrElse(None, -2)._1

        emoteCounts.clear()
        emoteVoterMap.clear()
        return maxVote
      }
      else return None
    }
    else return None
  }


  def GetGameStatus(): Unit = {
    implicit val timeout = Timeout(30 seconds)
    val future = controller ? "GetGameStatus"
    val result = Await.result(future, timeout.duration)

    if (result == None) {
      TimeUnit.SECONDS.sleep(2)
      GetGameStatus()
    }
    else {
      savedGameStatus = result.asInstanceOf[Array[Player]]
    }
  }


  def VoteEntry(vote: Any, sender: String): Unit = {
    // Different cases of possible vote entries
    if (!voterMap.isDefinedAt(sender))
      voterMap(sender) = ListBuffer[Any]()
    if (!bindMap.isDefinedAt(sender))
      bindMap(sender) = ListBuffer[Any]()
    if (!futureMap.isDefinedAt(sender))
      futureMap(sender) = ListBuffer[Any]()

    if (voterMap(sender).nonEmpty) {
      vote match {

        case Bind() =>
          //Add Bind() to voterMap if last vote is not Bind(), or EndTurn() or Hurry()
          if (voterMap(sender).last != Bind() && voterMap(sender).last != EndTurn() && voterMap(sender).last != Hurry() && voterMap(sender).last != Future()) {
            voterMap(sender).append(vote)
          }

        case Hurry() =>
          //Add Hurry() to voterMap if last vote is not Bind()
          if (voterMap(sender).last == Bind()) {
            voterMap(sender).trimEnd(1)
          }
          voterMap(sender).append(vote)

        case Future() =>
          //Add Future() to voterMap if last vote is not Bind(), or EndTurn() or Hurry(), or Future()
          if (voterMap(sender).last != Bind() && voterMap(sender).last != EndTurn() && voterMap(sender).last != Hurry() && voterMap(sender).last != Future()) {
            voterMap(sender).append(vote)
          }


        case Discover(option) =>

        case _ =>
          //Add a normal vote to voterMap with behavior based on the current last vote entry
          if (!voterMap(sender).contains(vote)) {
            voterMap(sender).last match {
              case Bind() =>
                //If bind is the last entry, remove the bind.
                //If the last entry is now Bound(), simply add the new vote to bindMap instead of voterMap
                //If the last entry is now a normaly vote, move that vote to bindMap
                //Add Bound() to voterMap in order to allow a multi-part bind
                voterMap(sender).trimEnd(1)
                if (voterMap(sender).last == Bound()) {
                  bindMap(sender).append(vote)
                }
                else {

                  val bindedVote = voterMap(sender).last
                  voterMap(sender).trimEnd(1)
                  bindMap(sender).append(bindedVote)
                  bindMap(sender).append(vote)
                  voterMap(sender).append(new Bound())
                }

              case Future() =>
                //If future is the last entry, remove the future.
                //If the last entry is now Bound(), simply add the new vote to futureMap instead of voterMap
                //If the last entry is now a normal vote, move that vote to futureMap
                //Add Bound() to voterMap in order to allow a multi-part bind
                voterMap(sender).trimEnd(1)
                if (voterMap(sender).last == FutureBound()) {
                  futureMap(sender).append(vote)
                }
                else {
                  val bindedVote = voterMap(sender).last
                  voterMap(sender).trimEnd(1)
                  futureMap(sender).append(bindedVote)
                  futureMap(sender).append(vote)
                  voterMap(sender).append(new FutureBound())
                }

              case Bound() =>
                //If Bound() is the last entry(which means the last vote was moved to bindMap), add a Break() to bindMap, remove the Bound(), and append the vote
                bindMap(sender).append(new Break())
                voterMap(sender).trimEnd(1)
                voterMap(sender).append(vote)


              case FutureBound() =>
                //If FutureBound() is the last entry(which means the last vote was moved to bindMap), add a Break() to bindMap, remove the FutureBound(), and append the vote
                futureMap(sender).append(new Break())
                voterMap(sender).trimEnd(1)
                voterMap(sender).append(vote)

              case EndTurn() =>
                //If EndTurn() is the last entry, remove the EndTurn and add the vote
                voterMap(sender).trimEnd(1)
                voterMap(sender).append(vote)

              case _ =>
                //If no special case, just add the vote
                voterMap(sender).append(vote)

            }
          }
      }
    }
    if (voterMap(sender).isEmpty) {
      vote match {
        //If voterMap is empty, do nothing unless a normal vote comes in
        case Bind() =>
        case Future() =>
        case Discover(option) =>
        case Hurry() =>


        case _ => voterMap(sender).append(vote)
      }
    }
  }

  def EmoteVoteEntry(vote: Any, sender: String): Unit = {

    if (emoteVoterMap.contains(sender)) {
      val oldVote = emoteVoterMap(sender)
      emoteCounts(oldVote) = emoteCounts(oldVote) - 1
      emoteVoterMap(sender) = vote
      emoteCounts(vote) = emoteCounts(vote) + 1
    }
    else {
      emoteVoterMap(sender) = vote
      if (emoteCounts.contains(vote))
        emoteCounts(vote) = emoteCounts(vote) + 1
      else emoteCounts(vote) = 1
    }
  }

  def RemoveNormalVote(sender: String, vote: Any): Unit = {

    if (sender == "all") {
      voterMap foreach {
        case (player, voteList) =>
          val index = voteList.indexWhere(_ == vote)
          while (voteList.contains(vote)) {
            voteList.remove(index)
          }
    }
    }

    else {
      while (voterMap(sender).contains(vote)) {
        val index = voterMap(sender).indexWhere(_ == vote)
        voterMap(sender).remove(index)
      }


    }

  }

  def RemoveBindVote(sender: String, vote: Any, removeBlock: Boolean): Unit = {
    //Search through a senders bindMap and find which index the vote is in.
    //Check if the index is after a Break(), which is the start of a bind, or index 0
    //Keep removing that index until you reach a break() or undefined
    //If the index is now a break, remove it.

    if (sender == "all") {
      bindMap foreach {
        case (player, voteList) =>
          while (voteList.contains(vote)) {
            val index = bindMap(player).indexWhere(_ == vote)
            if (index != -1) {
              if (removeBlock == true) {
                if (index == 0 || bindMap(player)(index - 1) == Break()) {
                  while (bindMap(player).isDefinedAt(index) && bindMap(player)(index) != Break()) {
                    bindMap(player).remove(index)
                  }
                  if (bindMap(player).isDefinedAt(index) && bindMap(player)(index) == Break())
                    bindMap(player).remove(index)
                }
              }
              else {
                bindMap(player).remove(index)
              }
            }
          }
      }
    }


    else {
      while (bindMap(sender).contains(vote)) {
        val index = bindMap(sender).indexWhere(_ == vote)
        if (removeBlock) {
          if (index != -1) {
            if (index == 0 || bindMap(sender)(index - 1) == Break()) {
              while (bindMap(sender).isDefinedAt(index) && bindMap(sender)(index) != Break()) {
                bindMap(sender).remove(index)
              }
              if (bindMap(sender).isDefinedAt(index) && bindMap(sender)(index) == Break())
                bindMap(sender).remove(index)
            }
          }
        }
        else
          bindMap(sender).remove(index)
      }
    }
  }


  def RemoveFutureVote(sender: String, vote: Any, removeBlock: Boolean): Unit = {
    //Search through a senders bindMap and find which index the vote is in.
    //Check if the index is after a Break(), which is the start of a bind, or index 0
    //Keep removing that index until you reach a break() or undefined
    //If the index is now a break, remove it.

    if (sender == "all") {
      futureMap foreach {
        case (player, voteList) =>
          while (voteList.contains(vote)) {
            val index = futureMap(player).indexWhere(_ == vote)
            if (index != -1) {
              if (removeBlock == true) {
                if (index == 0 || futureMap(player)(index - 1) == Break()) {
                  while (futureMap(player).isDefinedAt(index) && futureMap(player)(index) != Break()) {
                    futureMap(player).remove(index)
                  }
                  if (futureMap(player).isDefinedAt(index) && futureMap(player)(index) == Break())
                    futureMap(player).remove(index)
                }
              }
              else {
                futureMap(player).remove(index)
              }
            }
          }
      }
    }


    else {
      while (futureMap(sender).contains(vote)) {
        val index = futureMap(sender).indexWhere(_ == vote)
        if (removeBlock) {
          if (index != -1) {
            if (index == 0 || futureMap(sender)(index - 1) == Break()) {
              while (futureMap(sender).isDefinedAt(index) && futureMap(sender)(index) != Break()) {
                futureMap(sender).remove(index)
              }
              if (futureMap(sender).isDefinedAt(index) && futureMap(sender)(index) == Break())
                futureMap(sender).remove(index)
          }
        }
        }
        else
          futureMap(sender).remove(index)
      }
    }
  }


  def CreateCurrentVoterSet(): Set[String] = {
    //Combines all known voterMaps into one sender list
    //Essentially obtains amount of people that have voted this turn

    val firstMap = concedeVoterMap.keySet.diff(voterMap.keySet)
    val secondMap = emoteVoterMap.keySet.diff(voterMap.keySet)
    val thirdMap = speedVoterMap.keySet.diff(voterMap.keySet)
    var finalMap = voterMap.keySet ++ firstMap ++ secondMap ++ thirdMap

    return finalMap
  }

  def MaintainAverageVoterSize(): Unit = {
    if (averageVoterList.size > 20) {
      val excess = averageVoterList.size - 20
      averageVoterList.remove(0, excess)
    }
    averageVoterAmount = averageVoterList.sum / 20

  }

  def Mulligan(): Unit = {

    if (mulligan) {
      if (mulliganVoteMap.nonEmpty) {
        hearthstone !(MulliganVote(TallyMulliganVotes()), mulliganOptions)
        mulligan = false
        mulliganOptions = 0
        mulliganVoteMap.clear()
        mulliganCount.clear()
        doneWithMulligan = true
        if (myTurn)
          system.scheduler.scheduleOnce(45000.milli, this.self, DECIDE)
      }
      else {
        system.scheduler.scheduleOnce(10000.milli, this.self, MULLIGAN)
      }
    }


    else {
      mulligan = true
      system.scheduler.scheduleOnce(15000.milli, this.self, MULLIGAN)
    }
  }

  def TallyMulliganVotes(): Array[Int] = {

    mulliganVoteMap foreach {
      case (sender, vote) =>
        val testArray = Array[Int](1)
        testArray(0) = 1
        if (vote == testArray) {
          val testtest = 1
        }
        if (mulliganCount.isDefinedAt(vote)) {
          mulliganCount(vote) = mulliganCount(vote) + 1
        }
        else mulliganCount(vote) = 1
    }

    val maxValue: Int = mulliganCount.values.max
    val decision = mulliganCount.find(_._2 == maxValue).getOrElse(Array[Int](0), -2)._1
    return decision
  }


  def SimpleDecide(voterMap: mutable.Map[String, Any], voteCount: mutable.Map[Any, Int]): Unit = {
    //Tally Votes

    voterMap foreach {
      case (sender, vote) =>
        if (voteCount.isDefinedAt(vote)) {
          voteCount(vote) = voteCount(vote) + 1
        }
        if (!voteCount.isDefinedAt(vote))
          voteCount(vote) = 1
    }

    if (voteCount.nonEmpty) {

      val decision = voteCount.find(_._2 == voteCount.values.max).getOrElse(None, -2)._1
      voteCount.clear()
      voterMap.clear()
      hearthstone ! decision
      system.scheduler.scheduleOnce(10000.milli, this.self, MENU_DECIDE)
    }
    if (voteCount.isEmpty)
      system.scheduler.scheduleOnce(10000.milli, this.self, MENU_DECIDE)
  }

}

