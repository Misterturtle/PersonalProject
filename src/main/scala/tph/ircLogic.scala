package tph

import akka.actor.{Actor, ActorRef, ActorSystem}
import akka.pattern.ask
import akka.util.Timeout
import tph.IrcMessages._

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
  val DECIDE = "Decide"
  val CHECK = "Check"
  val STARTGAME = "Start Game"
  val GAMEOVER = "Game Over"
  val DELAY_AMOUNT = 10000

  var inGame = false
  var myTurn = false
  var hisTurn = false
  var inArena = false
  var inMainMenu = false
  var active = true

  var waitSpeed = false
  var hurrySpeed = false
  var skipDecide = false
}


class ircLogic(system: ActorSystem, controller: ActorRef, hearthstone: ActorRef) extends Actor with akka.actor.ActorLogging {
  //Input comes in
  //Input is organized into Arrays?
  import ircLogic._


  val voterMap = mutable.Map[String, Any]()
  val voteCounts = mutable.Map[Any, Int]()
  val emoteCounts = mutable.Map[Any, Int]()
  val emoteVoterMap = mutable.Map[String, Any]()
  val speedCounts = mutable.Map[Any, Int]()
  val speedVoterMap = mutable.Map[String, Any]()
  val concedeCounts = mutable.Map[Any, Int]()
  val concedeVoterMap = mutable.Map[String, Any]()
  var averageVoterList: ListBuffer[Int] = new ListBuffer[Int]
  var averageVoterAmount = 0
  var possibleDiscoverOptions: ListBuffer[Int] = new ListBuffer[Int]

  var currentGameStatus = new Array[Player](2)

  def receive = {
    case "Activate" => active = true
    case y => {
      if (active) {
        y match {
          case STARTGAME => StartGame()
          case GAMEOVER => GameOver()
          case TURNSTART => TurnStart()
          case TURNEND => TurnEnd()
          case DECIDE => Decide()
          case CHECK => Check()


          case x => {
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

                case (Concede(vote), sender: String) if (vote == "yes" || vote == "no") =>
                  ConcedeVoteEntry(Concede(vote), sender)

              }



              if (myTurn) {
                x match {

                  case DiscoverOptions(option: Int) =>
                    possibleDiscoverOptions.append(option)

                  case (Discover(option: Int), sender: String) if (option >= 1 && option <= possibleDiscoverOptions.max) =>
                    VoteEntry(Discover(option), sender)

                  case (CardPlayWithFriendlyOption(card: Int, boardTarget: Int), sender: String) if (card >= 1 && card <= currentGameStatus(0).hand.length && boardTarget >= 1 && boardTarget <= currentGameStatus(0).board.length) =>
                    VoteEntry(CardPlayWithFriendlyOption(card: Int, boardTarget: Int), sender)

                  case (CardPlayWithFriendlyFaceOption(card: Int), sender: String) =>
                    VoteEntry(CardPlayWithFriendlyFaceOption(card: Int), sender)

                  case (CardPlayWithEnemyOption(card: Int, boardTarget: Int), sender: String) if (boardTarget >= 1 && boardTarget <= currentGameStatus(1).board.length) =>
                    VoteEntry(CardPlayWithEnemyOption(card: Int, boardTarget: Int), sender)

                  case (CardPlayWithEnemyFaceOption(card: Int), sender: String) =>
                    VoteEntry(CardPlayWithEnemyFaceOption(card: Int), sender)


                  //Normal Turn Play Type
                  case (CardPlay(card: Int), sender: String) if (card >= 1 && card <= currentGameStatus(0).hand.length) =>
                    VoteEntry(CardPlay(card: Int), sender)

                  case (CardPlayWithPosition(card: Int, position: Int), sender: String) if (card >= 1 && card <= currentGameStatus(0).hand.length && position >= 1 && position <= currentGameStatus(0).hand.length + 1) =>
                    VoteEntry(CardPlayWithPosition(card: Int, position: Int), sender)

                  case (CardPlayWithFriendlyBoardTarget(card: Int, target: Int), sender: String) if (card >= 1 && card <= currentGameStatus(0).hand.length && target >= 1 && target <= currentGameStatus(0).board.length) =>
                    VoteEntry(CardPlayWithFriendlyBoardTarget(card: Int, target: Int), sender)

                  case (CardPlayWithEnemyBoardTarget(card: Int, target: Int), sender: String) if (card >= 1 && card <= currentGameStatus(0).hand.length && target >= 1 && target <= currentGameStatus(1).board.length) =>
                    VoteEntry(CardPlayWithEnemyBoardTarget(card: Int, target: Int), sender)

                  case (CardPlayWithFriendlyFaceTarget(card: Int), sender: String) if (card >= 1 && card <= currentGameStatus(0).hand.length) =>
                    VoteEntry(CardPlayWithFriendlyFaceTarget(card: Int), sender)

                  case (CardPlayWIthEnemyFaceTarget(card: Int), sender: String) if (card >= 1 && card <= currentGameStatus(0).hand.length) =>
                    VoteEntry(CardPlayWIthEnemyFaceTarget(card: Int), sender)

                  case (EndTurn(), sender: String) =>
                    VoteEntry(EndTurn(), sender)

                  case (HeroPower(), sender: String) =>
                    VoteEntry(HeroPower(), sender)

                  case (HeroPowerWithEnemyFace(), sender: String) =>
                    VoteEntry(HeroPowerWithEnemyFace(), sender)
                  case (HeroPowerWithEnemyTarget(target: Int), sender: String) =>
                    VoteEntry(HeroPowerWithEnemyTarget(target: Int), sender)
                  case (HeroPowerWithFriendlyFace(), sender: String) =>
                    VoteEntry(HeroPowerWithFriendlyFace(), sender)
                  case (HeroPowerWithFriendlyTarget(target: Int), sender: String) =>
                    VoteEntry(HeroPowerWithFriendlyTarget(target: Int), sender)


                  //Attack Type
                  case (NormalAttack(friendlyPosition: Int, enemyPosition: Int), sender: String) if (friendlyPosition >= 1 && friendlyPosition <= currentGameStatus(0).board.length && enemyPosition >= 1 && enemyPosition <= currentGameStatus(1).board.length) =>
                    VoteEntry(NormalAttack(friendlyPosition: Int, enemyPosition: Int), sender)

                  case (FaceAttack(position: Int), sender: String) if (position >= 1 && position <= currentGameStatus(1).board.length) =>
                    VoteEntry(FaceAttack(position: Int), sender)

                  case (NormalAttackToFace(position: Int), sender: String) if (position >= 1 && position <= currentGameStatus(0).board.length) =>
                    VoteEntry(NormalAttackToFace(position: Int), sender)

                  case (FaceAttackToFace(), sender: String) =>
                    VoteEntry(FaceAttackToFace(), sender)

                  //Always Type
                  case (Wait(), sender: String) =>
                    SpeedVoteEntry(Wait(), sender)

                  case (Hurry(), sender: String) =>
                    SpeedVoteEntry(Hurry(), sender)
                }
              }
            }
          }
        }
      }
    }
  }


  def StartGame(): Unit = {
    inGame = true
    system.scheduler.scheduleOnce(1000.milli, this.self, CHECK)
  }

  def GameOver(): Unit = {
    inGame = false
  }

  def TurnStart(): Unit = {
    GetGameStatus()
    myTurn = true
    system.scheduler.scheduleOnce(10000.milli, this.self, DECIDE)

  }

  def TurnEnd(): Unit = {
    myTurn = false
  }


  def Decide(): Unit = {
    if (skipDecide) {
      skipDecide = false
    }
    else {
      if (myTurn) {
        if (hurrySpeed) {
          val decision = CalculateDecision()
          hearthstone ! decision
          hurrySpeed = false
          skipDecide = true
        }
        if (waitSpeed) {
          system.scheduler.scheduleOnce(DELAY_AMOUNT.milli, this.self, DECIDE)
          waitSpeed = false
        }
        if (!hurrySpeed && !waitSpeed) {
          val decision = CalculateDecision()
          hearthstone ! decision
          GetGameStatus()
          system.scheduler.scheduleOnce(10000.milli, this.self, DECIDE)
        }
      }
    }
  }

  def CalculateDecision(): Any = {
    val maxValue = voteCounts.valuesIterator.max
    val maxVote = voteCounts.find(_._2 == maxValue).getOrElse(None, -1)._1
    val completeVoterSet = CreateCurrentVoterSet().size
    averageVoterList.append(completeVoterSet)
    MaintainAverageVoterSize()
    voterMap.clear()
    voteCounts.clear()
    return maxVote

  }


  def Check(): Unit = {
    //Check if emotes need to be ran
    if (inGame) {
      val emoteReturn = CheckEmoteList()
      if (emoteReturn != None) controller ! emoteReturn

      //Check if a wait or hurry needs to be added
      if (myTurn) {
        val speedReturn = CheckSpeed()
        if (speedReturn != None) controller ! speedReturn
      }

      //Reschedule Check()
      system.scheduler.scheduleOnce(1000.milli, this.self, CHECK)
    }
  }

  def CheckEmoteList(): Any = {
    //If 33% of voters want to emote

    val emoteTally = emoteVoterMap.size
    val completeVoterSet = CreateCurrentVoterSet()
    val totalTally = completeVoterSet.size

    if (emoteTally >= totalTally / 3 && totalTally >= averageVoterAmount / 3) {
      val maxValue = emoteCounts.valuesIterator.max
      val maxVote = emoteCounts.find(_._2 == maxValue).getOrElse(None, -1)._1

      emoteCounts.clear()
      emoteVoterMap.clear()
      return maxVote
    }
    else return None
  }


  def CheckSpeed(): Unit = {

    val currentVoterSet = CreateCurrentVoterSet()
    if (speedCounts.size >= currentVoterSet.size / 3 && currentVoterSet.size >= averageVoterAmount / 3) {
      val maxValue = speedCounts.valuesIterator.max
      val maxVote = speedCounts.find(_._2 == maxValue).getOrElse(None, -1)._1
      speedCounts.clear()
      speedVoterMap.clear()
      if (maxVote == Wait()) {
        waitSpeed = true
      }

      if (maxVote == Hurry()) {
        hurrySpeed = true

        this.self ! DECIDE
      }
    }
  }


  def GetGameStatus(): Unit = {
    active = false

    implicit val timeout = Timeout(30 seconds)
    val future = controller ? "GetGameStatus"
    val result = Await.result(future, timeout.duration).asInstanceOf[Array[Player]]
    currentGameStatus = result

    this.self ! "Activate"
  }

  def VoteEntry(vote: Any, sender: String): Unit = {

    if (voterMap.contains(sender)) {
      val oldVote = voterMap(sender)
      voteCounts(oldVote) = voteCounts(oldVote) - 1
      voterMap(sender) = vote
      voteCounts(vote) = voteCounts(vote) + 1
    }
    else {
      voterMap(sender) = vote
      voteCounts(vote) = voteCounts(vote) + 1
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
      emoteCounts(vote) = emoteCounts(vote) + 1
    }
  }

  def SpeedVoteEntry(vote: Any, sender: String): Unit = {

    if (speedVoterMap.contains(sender)) {
      val oldVote = speedVoterMap(sender)
      speedCounts(oldVote) = speedCounts(oldVote) - 1
      speedVoterMap(sender) = vote
      speedCounts(vote) = speedCounts(vote) + 1
    }
    else {
      speedVoterMap(sender) = vote
      speedCounts(vote) = speedCounts(vote) + 1
    }
  }

  def ConcedeVoteEntry(vote: Any, sender: String): Unit = {

    if (concedeVoterMap.contains(sender)) {
      val oldVote = concedeVoterMap(sender)
      concedeCounts(oldVote) = concedeCounts(oldVote) - 1
      concedeVoterMap(sender) = vote
      concedeCounts(vote) = concedeCounts(vote) + 1
    }
    else {
      concedeVoterMap(sender) = vote
      concedeCounts(vote) = concedeCounts(vote) + 1
    }
  }

  def CreateCurrentVoterSet(): Set[String] = {
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

}

