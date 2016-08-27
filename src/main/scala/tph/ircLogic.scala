
//Duplicate votes??

//45 second vote time
//Different timing for mulligan?
// How are discovers going to work???

//Theory crafting - Ordered mode - 45 second vote time
//Allow hurry after bind? Example: warlock !hero power, !bind, !hurry
//Maybe have a !order to get around taunts etc? Doesn't affect vote influence
//Maybe have temporary decision? Tally votes and then look for that vote as the 2nd part of a bind. Increase 1st part if so and retally.

//Theory crafting - Chaos is single decisions.
//Chaos Mode checks every 2 seconds for if highest vote is VOTE_PERCENTAGE(200%?)(double) ahead of 2nd vote.
//If no unanimous decision after 10 seconds just do highest vote.
//Only one vote per person allowed in chaos mode






package tph

import akka.actor.{Actor, ActorRef, ActorSystem}
import akka.pattern.ask
import akka.util.Timeout
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
  val DECIDE = "Decide"
  val CHECK = "Check"
  val STARTGAME = "Start Game"
  val GAMEOVER = "Game Over"
  val MULLIGAN = "Mulligan"
  val MULLIGAN_OPTION = "NewMulliganOption"
  val MULLIGAN_DECIDE = "MulliganDecide"
  val DELAY_AMOUNT = 10000

  var currentMenu = "MainMenu"
  var previousMenu = ""

  var inGame = false
  var myTurn = false
  var hisTurn = false
  var active = false

  var waitSpeed = false
  var hurrySpeed = false
  var skipDecide = false

  //Just to create it
  var previousDecision = Wait()
}


class ircLogic(system: ActorSystem, controller: ActorRef, hearthstone: ActorRef) extends Actor with akka.actor.ActorLogging {
  //Input comes in
  //Input is organized into Arrays?
  import ircLogic._

  val mainMenuVoterMap = mutable.Map[String, Any]()
  val mainMenuVoteCount = mutable.Map[Any, Int]()
  val playMenuVoterMap = mutable.Map[String, Any]()
  val playMenuVoteCount = mutable.Map[Any, Int]()
  val collectionMenuVoterMap = mutable.Map[String, Any]()
  val collectionMenuVoteCount = mutable.Map[Any, Int]()
  val enchantMenuVoterMap = mutable.Map[String, Any]()
  val enchantMenuVoteCount = mutable.Map[Any, Int]()

  val voterMap = mutable.Map[String, ListBuffer[Any]]()
  val bindMap = mutable.Map[String, ListBuffer[Any]]()
  var tallyMap = mutable.Map[Any, Int]()
  val numberOfVote = new ListBuffer[Int]

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

  var savedGameStatus = new Array[Player](2)
  var mulliganOptions = 0
  var mulligan = false
  var mulliganVoteMap = mutable.Map[String, Array[Int]]()
  var mulliganCount = mutable.Map[Array[Int], Int]()


  def receive = {
    case "Activate" => active = true
    case "Start" => {
      active = true
      system.scheduler.scheduleOnce(10000.milli, this.self, DECIDE)
    }
    case STARTGAME => StartGame()

    case DiscoverOption(option: Int) =>
      possibleDiscoverOptions.append(option)


    case x => {

      if (active) {
        if (currentMenu == "MainMenu") {
          x match {
            case DECIDE =>
              SimpleDecide(mainMenuVoterMap, mainMenuVoteCount)

            case (Play(), sender: String) =>
              mainMenuVoterMap(sender) = Play()

            case (OpenPacks(), sender: String) =>
              mainMenuVoterMap(sender) = OpenPacks()

            case (Shop(), sender: String) =>
              mainMenuVoterMap(sender) = Shop()

            case (Collection(), sender: String) =>
              mainMenuVoterMap(sender) = Collection()
          }
        }

        if (currentMenu == "PlayMenu") {
          x match {
            case DECIDE =>
              SimpleDecide(playMenuVoterMap, playMenuVoteCount)

            case (Casual(), sender: String) =>
              playMenuVoterMap(sender) = Casual()
            case (Ranked(), sender: String) =>
              playMenuVoterMap(sender) = Ranked()
            case (Play(), sender: String) =>
              playMenuVoterMap(sender) = Play()
            case (Collection(), sender: String) =>
              playMenuVoterMap(sender) = Collection()
            case (Back(), sender: String) =>
              playMenuVoterMap(sender) = Back()
            case (Deck(deckNumber), sender: String) =>
              playMenuVoterMap(sender) = Deck(deckNumber)
            case (FirstPage(), sender: String) =>
              playMenuVoterMap(sender) = FirstPage()
            case (SecondPage(), sender: String) =>
              playMenuVoterMap(sender) = SecondPage()
          }
        }

        if (currentMenu == "CollectionMenu") {


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

            case (Concede(vote), sender: String) if (vote == "yes" || vote == "no") =>
              ConcedeVoteEntry(Concede(vote), sender)

            case GAMEOVER => GameOver()

            case TURNSTART => TurnStart()
            case TURNEND => TurnEnd()
            case DECIDE => Decide()
            case CHECK => Check()
            case MULLIGAN => Mulligan()
            case MULLIGAN_OPTION => mulliganOptions = mulliganOptions + 1

            case _ =>

          }



          if (myTurn) {
            x match {


              case Command(builtCommand: Command) =>
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
                  case "CardPlayWIthEnemyFaceTarget" if (builtCommand.card >= 1 && builtCommand.card <= savedGameStatus(0).hand.length) =>
                    VoteEntry(CardPlayWIthEnemyFaceTarget(builtCommand.card: Int), builtCommand.sender)


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

                if (mulligan)
                  x match {
                    case (MulliganVote(vote), sender: String) =>
                      mulliganVoteMap(sender) = vote
                  }

              //Always Type
              case (Wait(), sender: String) =>
                SpeedVoteEntry(Wait(), sender)

              case (Hurry(), sender: String) =>
                SpeedVoteEntry(Hurry(), sender)

              case (EndTurn(), sender: String) =>
                VoteEntry(EndTurn(), sender)

              case (Bind(), sender: String) =>
                VoteEntry(Bind(), sender)

              case (Cancel(), sender: String) =>
                VoteEntry(Cancel(), sender)
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
    myTurn = false
    hisTurn = false
    currentMenu = previousMenu
  }

  def TurnStart(): Unit = {
    GetGameStatus()
    myTurn = true
    system.scheduler.scheduleOnce(45000.milli, this.self, DECIDE)

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

  def CalculateAmountOfMoves(): Int = {
    voterMap foreach {
      case (senders, voteLists) =>
        if (voteLists.last == EndTurn() || voteLists.last == Hurry())
          numberOfVote.append(voteLists.size)
    }

    val totalVotes = numberOfVote.sum
    val totalPeople = numberOfVote.size
    val averageNumberOfVotes = totalVotes / totalPeople

    return averageNumberOfVotes

  }

  def AdjustVotes(): Unit = {
    val oldGameStatus = savedGameStatus
    GetGameStatus()
    val newGameStatus = savedGameStatus
    //unfold oldHand
    //unfold myOldBoard
    //unfold hisNewBoard
    //unfold newHand
    //unfold myNewBoard
    //unfold HisNewBoard

    //Desired Function: Adjust VoteEntry's for when a minion dies or a card is played
    //Solution: Compare old and new, detect changes in card ID's.

    //Maybe adjust votes in voterMap but leave binded votes (besides first) alone
    //Make binded cards worth 0?
    //Make binded leads worth more?

    //Map original position to ID's?
    //Map changed positions to ID's?
    //Non-binded votes will find their ID after tally and adjust

    //Maybe allow !future


  }

  def CalculateDecision(): Any = {
    active = false
    for (i <- 0 until CalculateAmountOfMoves()) {
      TallyVotes()
      DecisionMaintenance()
      val maxValue: Int = tallyMap.values.max
      val decision = tallyMap(tallyMap.find(_._2 == maxValue).getOrElse(None, -2)._1)


      return decision
    }
  }

  def DecisionMaintenance(): Unit = {

    bindMap foreach {
      case (sender, voteList) =>
        val index = (voteList.indexWhere(_ == previousDecision))
        if (index != -1)
          voteList.remove(index)
    }

    voterMap foreach {
      case (sender, voteList) =>
        val index = (voteList.indexWhere(_ == previousDecision))
        if (index != -1)
          voteList.remove(index)
    }

    tallyMap(previousDecision) = 0

  }

  def TallyVotes(): Unit = {
    voterMap foreach {
      case (key, value) =>
        value foreach {
          case (vote: Any) =>
            if (tallyMap.isDefinedAt(vote))
              tallyMap(vote) = tallyMap(vote) + 10
            else tallyMap(vote) = 10
        }

        val voterMapBuffer = value
        if (tallyMap.isDefinedAt())
          tallyMap(value) = tallyMap(value) + 10
        else tallyMap(value) = 10
    }


    bindMap foreach {
      case (key, list) =>
        var currentBind = 0
        var bindActive = false

        list foreach {
          case x =>

            var index = list.indexWhere(_ == x)

            if (x == Break()) {
              currentBind = 0
              bindActive = false
            }

            if (x == previousDecision) {
              bindActive = true
              currentBind = currentBind + 1
            }

            if (x != previousDecision && x != Break()) {
              if (tallyMap.isDefinedAt(x)) {
                if (!bindActive) {
                  if (currentBind == 0)
                    tallyMap(x) = tallyMap(x) + 10

                  if (currentBind == 1)
                    tallyMap(x) = tallyMap(x) + 5

                  if (currentBind == 2)
                    tallyMap(x) = tallyMap(x) + 5

                  if (currentBind == 3)
                    tallyMap(x) = tallyMap(x) + 2
                }


                if (bindActive)
                  tallyMap(x) == tallyMap(x) + 20
              }

              if (!tallyMap.isDefinedAt(x)) {
                if (!bindActive) {

                  if (currentBind == 0)
                    tallyMap(x) = 10

                  if (currentBind == 1)
                    tallyMap(x) = 5

                  if (currentBind == 2)
                    tallyMap(x) = 5

                  if (currentBind == 3)
                    tallyMap(x) = 2
                }
                if (bindActive)
                  tallyMap(x) = 20
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

      //Check if a wait or hurry needs to be added
      if (myTurn) {
        val speedReturn = CheckSpeed()
        if (speedReturn != None) hearthstone ! speedReturn
      }

      //Reschedule Check()
      system.scheduler.scheduleOnce(1000.milli, this.self, CHECK)
    }
  }


  //Might need modified

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


  //Needs modified
  def CheckSpeed(): Unit = {
    if (!speedVoterMap.isEmpty) {
      val currentVoterSet = CreateCurrentVoterSet()
      if (speedCounts.size >= currentVoterSet.size / 3 && currentVoterSet.size >= averageVoterAmount / 3) {
        val maxValue = speedCounts.valuesIterator.max
        val maxVote = speedCounts.find(_._2 == maxValue).getOrElse(None, -2)._1
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
  }


  def GetGameStatus(): Unit = {
    active = false

    implicit val timeout = Timeout(30 seconds)
    val future = controller ? "GetGameStatus"
    val result = Await.result(future, timeout.duration)

    if (result == None) {

      GetGameStatus()
    }
    else {
      this.self ! "Activate"
      savedGameStatus = result.asInstanceOf[Array[Player]]
    }


  }

  def VoteEntry(vote: Any, sender: String): Unit = {

    if (!voterMap(sender).isEmpty) {
      vote match {

        case Bind() =>
          if (voterMap(sender).last != Bind() && voterMap(sender).last != Cancel() && voterMap(sender).last != EndTurn()) {
            voterMap(sender).append(vote)
          }

        case Cancel() =>
          if (voterMap(sender).last != Bind() && voterMap(sender).last != Cancel() && voterMap(sender).last != EndTurn()) {
            voterMap(sender).append(vote)
          }

        case Discover(option) =>

        case _ =>
          voterMap(sender).last match {
            case Bind() =>
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

            case Bound() => {
              bindMap(sender).append(new Break())
              voterMap(sender).trimEnd(1)
              voterMap(sender).append(vote)
            }

            case Cancel() => {
              voterMap(sender).trimEnd(1)
              if (voterMap(sender).indexWhere(_ == vote) != -1)
                voterMap(sender).remove(voterMap(sender).indexWhere(_ == vote))
              val index = bindMap(sender).indexWhere(_ == vote)
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

            case EndTurn() =>
              voterMap(sender).trimEnd(1)
              voterMap(sender).append(vote)

            case _ =>
              voterMap(sender).append(vote)

          }

      }
    }
    if (!voterMap(sender).isEmpty) {
      vote match {
        case Bind() =>

        case Cancel() =>

        case Discover(option) =>


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

  def SpeedVoteEntry(vote: Any, sender: String): Unit = {

    if (speedVoterMap.contains(sender)) {
      val oldVote = speedVoterMap(sender)
      speedCounts(oldVote) = speedCounts(oldVote) - 1
      speedVoterMap(sender) = vote
      speedCounts(vote) = speedCounts(vote) + 1
    }
    else {
      speedVoterMap(sender) = vote
      if (speedCounts.contains(vote))
        speedCounts(vote) = speedCounts(vote) + 1
      else speedCounts(vote) = 1
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
      if (concedeCounts.contains(vote))
        concedeCounts(vote) = concedeCounts(vote) + 1
      else concedeCounts(vote) = 1
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

  def Mulligan(): Unit = {

    if (mulligan) {
      if (mulliganCount.nonEmpty)
        hearthstone !(MulliganVote(TallyMulliganVotes()), mulliganOptions)
      mulligan = false
      mulliganOptions = 0
    }


    if (!mulligan) {
      mulligan = true
      system.scheduler.scheduleOnce(10000.milli, this.self, MULLIGAN)
    }
  }

  def TallyMulliganVotes(): Array[Int] = {

    mulliganVoteMap foreach {
      case (sender, vote) =>
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
    val maxValue = voteCount.values.max
    val decision = voteCount.find(_._2 == maxValue).getOrElse(None, -2)._1

    hearthstone ! decision
  }


}

