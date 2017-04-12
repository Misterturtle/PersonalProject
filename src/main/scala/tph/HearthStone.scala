package tph

import java.io.{File, InputStream}
import java.util.concurrent.{Executors, TimeUnit}

import autoitx4java.AutoItX
import com.jacob.com.LibraryLoader
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import org.apache.commons.io.{IOUtils, FileUtils}
import tph.Constants.ActionVotes._
import tph.Constants.EmojiVotes._
import tph.Constants.MenuVotes._
import scala.util.Random

/**
  * Created by Harambe on 3/31/2017.
  */
class HearthStone(gs: GameState) extends LazyLogging {

  val TITLE = "Hearthstone"
  val delay = 200
  val postActionDelay = 1000


  //Load Jacob Dll
  val input: InputStream = getClass.getResourceAsStream("/jacob-1.18-x64.dll")
  val randomNumber = Random.nextInt(100)
  val fileOut = new File(System.getProperty("java.io.tmpdir") + "/jacob/Hearthstone" + randomNumber + "/jacob-1.18-x64.dll")
  println("Writing dll to: " + fileOut.getAbsolutePath)
  val out = FileUtils.openOutputStream(fileOut)
  IOUtils.copy(input, out)
  input.close()
  out.close()
  System.setProperty(LibraryLoader.JACOB_DLL_PATH, fileOut.getPath)
  val ax = new AutoItX()
  val config = ConfigFactory.load()
  val scheduler = Executors.newScheduledThreadPool(1)

  object PixelDataBase {
    val fillerZeroIndex = (Constants.INT_UNINIT, Constants.INT_UNINIT)

    object mainMenu {
      val play = (657, 254)
      val soloAdventures = (659, 304)
      val theArena = (658, 355)
      val tavernBrawl = (655, 413)
      val myCollection = (720, 646)
      val openPacks = (535, 634)
      val questLog = (369, 664)
      val shop = (246, 673)
      val optionMenu = (1282, 744)
      val friendsList = (39, 743)
    }

    object playMenu {
      val casual = (884, 171)
      val ranked = (1019, 160)
      val gameModeToggle = (959, 71)
      val play = (951, 629)
      val back = (1074, 704)
      val nextPage = (769, 388)
      val previousPage = (202, 383)
      val myCollection = (499, 696)
      val firstDeck = (338, 229)
      val secondDeck = (502, 225)
      val thirdDeck = (660, 225)
      val fourthDeck = (341, 376)
      val fifthDeck = (494, 383)
      val sixthDeck = (664, 375)
      val seventhDeck = (331, 527)
      val eighthDeck = (500, 529)
      val ninthDeck = (665, 528)
      val friendsList = (39, 743)
    }

    object constantLoc {
      val endTurn = (1058, 359)
      val heroPower = (777, 585)
      val gameOptions = (1276, 743)
      val concedeButton = (658, 296)
    }


    object myBoardLoc {
      val myBoardFarRight = (1042, 434)

      val myFace = (663, 582)

      val myBoardOneOfOne = (633, 427)
      val maxOneList = List(myFace, myBoardOneOfOne)

      val myBoardOneOfTwo = (581, 429)
      val myBoardTwoOfTwo = (680, 430)
      val maxTwoList = List(myFace, myBoardOneOfTwo, myBoardTwoOfTwo)

      val myBoardOneOfThree = (540, 429)
      val myBoardTwoOfThree = (634, 428)
      val myBoardThreeOfThree = (729, 427)
      val maxThreeList = List(myFace, myBoardOneOfThree, myBoardTwoOfThree, myBoardThreeOfThree)

      val myBoardOneOfFour = (495, 430)
      val myBoardTwoOfFour = (589, 427)
      val myBoardThreeOfFour = (682, 429)
      val myBoardFourOfFour = (776, 427)
      val maxFourList = List(myFace, myBoardOneOfFour, myBoardTwoOfFour, myBoardThreeOfFour, myBoardFourOfFour)

      val myBoardOneOfFive = (445, 429)
      val myBoardTwoOfFive = (543, 430)
      val myBoardThreeOfFive = (636, 428)
      val myBoardFourOfFive = (730, 428)
      val myBoardFiveOfFive = (824, 429)
      val maxFiveList = List(myFace, myBoardOneOfFive, myBoardTwoOfFive, myBoardThreeOfFive, myBoardFourOfFive, myBoardFiveOfFive)

      val myBoardOneOfSix = (397, 425)
      val myBoardTwoOfSix = (497, 428)
      val myBoardThreeOfSix = (590, 428)
      val myBoardFourOfSix = (681, 424)
      val myBoardFiveOfSix = (777, 425)
      val myBoardSixOfSix = (872, 430)
      val maxSixList = List(myFace, myBoardOneOfSix, myBoardTwoOfSix, myBoardThreeOfSix, myBoardFourOfSix, myBoardFiveOfSix, myBoardSixOfSix)

      val myBoardOneOfSeven = (349, 429)
      val myBoardTwoOfSeven = (451, 429)
      val myBoardThreeOfSeven = (545, 427)
      val myBoardFourOfSeven = (635, 430)
      val myBoardFiveOfSeven = (730, 429)
      val myBoardSixOfSeven = (826, 431)
      val myBoardSevenOfSeven = (915, 427)
      val maxSevenList = List(myFace, myBoardOneOfSeven, myBoardTwoOfSeven, myBoardThreeOfSeven, myBoardFourOfSeven, myBoardFiveOfSeven, myBoardSixOfSeven, myBoardSevenOfSeven)

      val myBoardLocList = List[List[(Int, Int)]](
        List(myFace),
        maxOneList,
        maxTwoList,
        maxThreeList,
        maxFourList,
        maxFiveList,
        maxSixList,
        maxSevenList)
    }

    object hisBoardLoc {
      val hisFace = (658, 164)

      val hisBoardOneOfOne = (629, 298)
      val maxOneList = List(hisFace, hisBoardOneOfOne)

      val hisBoardOneOfTwo = (610, 306)
      val hisBoardTwoOfTwo = (705, 305)
      val maxTwoList = List(hisFace, hisBoardOneOfTwo, hisBoardTwoOfTwo)

      val hisBoardOneOfThree = (561, 301)
      val hisBoardTwoOfThree = (657, 307)
      val hisBoardThreeOfThree = (751, 303)
      val maxThreeList = List(hisFace, hisBoardOneOfThree, hisBoardTwoOfThree, hisBoardThreeOfThree)

      val hisBoardOneOfFour = (517, 302)
      val hisBoardTwoOfFour = (611, 299)
      val hisBoardThreeOfFour = (704, 301)
      val hisBoardFourOfFour = (801, 304)
      val maxFourList = List(hisFace, hisBoardOneOfFour, hisBoardTwoOfFour, hisBoardThreeOfFour, hisBoardFourOfFour)

      val hisBoardOneOfFive = (471, 307)
      val hisBoardTwoOfFive = (564, 303)
      val hisBoardThreeOfFive = (659, 295)
      val hisBoardFourOfFive = (751, 301)
      val hisBoardFiveOfFive = (847, 298)
      val maxFiveList = List(hisFace, hisBoardOneOfFive, hisBoardTwoOfFive, hisBoardThreeOfFive, hisBoardFourOfFive, hisBoardFiveOfFive)

      val hisBoardOneOfSix = (427, 298)
      val hisBoardTwoOfSix = (519, 307)
      val hisBoardThreeOfSix = (612, 303)
      val hisBoardFourOfSix = (708, 299)
      val hisBoardFiveOfSix = (800, 303)
      val hisBoardSixOfSix = (892, 303)
      val maxSixList = List(hisFace, hisBoardOneOfSix, hisBoardTwoOfSix, hisBoardThreeOfSix, hisBoardFourOfSix, hisBoardFiveOfSix, hisBoardSixOfSix)

      val hisBoardOneOfSeven = (377, 305)
      val hisBoardTwoOfSeven = (468, 305)
      val hisBoardThreeOfSeven = (561, 303)
      val hisBoardFourOfSeven = (653, 302)
      val hisBoardFiveOfSeven = (747, 303)
      val hisBoardSixOfSeven = (844, 306)
      val hisBoardSevenOfSeven = (936, 303)
      val maxSevenList = List(hisFace, hisBoardOneOfSeven, hisBoardTwoOfSeven, hisBoardThreeOfSeven, hisBoardFourOfSeven, hisBoardFiveOfSeven, hisBoardSixOfSeven, hisBoardSevenOfSeven)

      val hisBoardLocList = List[List[(Int, Int)]](
        List(hisFace),
        maxOneList,
        maxTwoList,
        maxThreeList,
        maxFourList,
        maxFiveList,
        maxSixList,
        maxSevenList)
    }


    object myHandLoc {
      val cardOneOfOne = (633, 704)
      val maxOneList = List(fillerZeroIndex, cardOneOfOne)

      val cardOneOfTwo = (578, 713)
      val cardTwoOfTwo = (671, 708)
      val maxTwoList = List(fillerZeroIndex, cardOneOfTwo, cardTwoOfTwo)

      val cardOneOfThree = (536, 721)
      val cardTwoOfThree = (634, 706)
      val cardThreeOfThree = (727, 705)
      val maxThreeList = List(fillerZeroIndex, cardOneOfThree, cardTwoOfThree, cardThreeOfThree)

      val cardOneOfFour = (497, 726)
      val cardTwoOfFour = (580, 719)
      val cardThreeOfFour = (671, 716)
      val cardFourOfFour = (764, 717)
      val maxFourList = List(fillerZeroIndex, cardOneOfFour, cardTwoOfFour, cardThreeOfFour, cardFourOfFour)

      val cardOneOfFive = (468, 733)
      val cardTwoOfFive = (548, 726)
      val cardThreeOfFive = (623, 724)
      val cardFourOfFive = (703, 709)
      val cardFiveOfFive = (771, 720)
      val maxFiveList = List(fillerZeroIndex, cardOneOfFive, cardTwoOfFive, cardThreeOfFive, cardFourOfFive, cardFiveOfFive)

      val cardOneOfSix = (457, 740)
      val cardTwoOfSix = (520, 712)
      val cardThreeOfSix = (583, 695)
      val cardFourOfSix = (650, 681)
      val cardFiveOfSix = (717, 700)
      val cardSixOfSix = (800, 713)
      val maxSixList = List(fillerZeroIndex, cardOneOfSix, cardTwoOfSix, cardThreeOfSix, cardFourOfSix, cardFiveOfSix, cardSixOfSix)

      val cardOneOfSeven = (453, 734)
      val cardTwoOfSeven = (503, 722)
      val cardThreeOfSeven = (552, 705)
      val cardFourOfSeven = (614, 696)
      val cardFiveOfSeven = (663, 695)
      val cardSixOfSeven = (720, 696)
      val cardSevenOfSeven = (803, 722)
      val maxSevenList = List(fillerZeroIndex, cardOneOfSeven, cardTwoOfSeven, cardThreeOfSeven, cardFourOfSeven, cardFiveOfSeven, cardSixOfSeven, cardSevenOfSeven)

      val cardOneOfEight = (443, 737)
      val cardTwoOfEight = (491, 707)
      val cardThreeOfEight = (539, 689)
      val cardFourOfEight = (590, 682)
      val cardFiveOfEight = (639, 674)
      val cardSixOfEight = (691, 676)
      val cardSevenOfEight = (745, 685)
      val cardEightOfEight = (802, 713)
      val maxEightList = List(fillerZeroIndex, cardOneOfEight, cardTwoOfEight, cardThreeOfEight, cardFourOfEight, cardFiveOfEight, cardSixOfEight, cardSevenOfEight, cardEightOfEight)

      val cardOneOfNine = (434, 740)
      val cardTwoOfNine = (483, 719)
      val cardThreeOfNine = (522, 697)
      val cardFourOfNine = (559, 687)
      val cardFiveOfNine = (598, 683)
      val cardSixOfNine = (644, 679)
      val cardSevenOfNine = (697, 684)
      val cardEightOfNine = (738, 703)
      val cardNineOfNine = (809, 719)
      val maxNineList = List(fillerZeroIndex, cardOneOfNine, cardTwoOfNine, cardThreeOfNine, cardFourOfNine, cardFiveOfNine, cardSixOfNine, cardSevenOfNine, cardEightOfNine, cardNineOfNine)

      val cardOneOfTen = (423, 741)
      val cardTwoOfTen = (455, 723)
      val cardThreeOfTen = (495, 699)
      val cardFourOfTen = (538, 680)
      val cardFiveOfTen = (576, 667)
      val cardSixOfTen = (625, 666)
      val cardSevenOfTen = (673, 669)
      val cardEightOfTen = (712, 678)
      val cardNineOfTen = (756, 680)
      val cardTenOfTen = (820, 715)
      val maxTenList = List(fillerZeroIndex, cardOneOfTen, cardTwoOfTen, cardThreeOfTen, cardFourOfTen, cardFiveOfTen, cardSixOfTen, cardSevenOfTen, cardEightOfTen, cardNineOfTen, cardTenOfTen)

      val myCardsLocList = List[List[(Int, Int)]](
        List(fillerZeroIndex),
        maxOneList,
        maxTwoList,
        maxThreeList,
        maxFourList,
        maxFiveList,
        maxSixList,
        maxSevenList,
        maxEightList,
        maxNineList,
        maxTenList)
    }

    object mulligan {
      val mulliganOneOfThree = (425, 380)
      val mulliganTwoOfThree = (666, 372)
      val mulliganThreeOfThree = (899, 373)
      val maxThreeList = List(fillerZeroIndex, mulliganOneOfThree, mulliganTwoOfThree, mulliganThreeOfThree)

      val mulliganOneOfFour = (400, 380)
      val mulliganTwoOfFour = (576, 370)
      val mulliganThreeOfFour = (745, 375)
      val mulliganFourOfFour = (918, 377)
      val maxFourList = List(fillerZeroIndex, mulliganOneOfFour, mulliganTwoOfFour, mulliganThreeOfFour, mulliganFourOfFour)

      val mulliganList = List(List(fillerZeroIndex), List(fillerZeroIndex), List(fillerZeroIndex), maxThreeList, maxFourList)
      val mulliganConfirm = (659, 612)

    }

    object discoverLoc {
      val discoverCardOne = (399, 373)
      val discoverCardTwo = (666, 378)
      val discoverCardThree = (913, 378)
      val discoverList = List(fillerZeroIndex, discoverCardOne, discoverCardTwo, discoverCardThree)
    }

    object chooseLoc {
      val chooseCardOne = (450, 373)
      val chooseCardTwo = (850, 378)
    }

    object emoteLoc {
      val myFace = (657, 593)
      val greetings = (531, 610)
      val wellPlayed = (536, 551)
      val thanks = (559, 493)
      val wow = (759, 491)
      val oops = (791, 554)
      val threaten = (791, 612)
    }

  }


  def executeMulligan(mulligan: MulliganVote, mulliganOptions: Int): Unit = {

    if (mulligan.first) {
      click(PixelDataBase.mulligan.mulliganList(mulliganOptions)(1))
      Thread.sleep(100)
    }
    if (mulligan.second) {
      click(PixelDataBase.mulligan.mulliganList(mulliganOptions)(2))
      Thread.sleep(100)
    }
    if (mulligan.third) {
      click(PixelDataBase.mulligan.mulliganList(mulliganOptions)(3))
      Thread.sleep(100)
    }

    if (mulligan.fourth && mulliganOptions == 4) {
      click(PixelDataBase.mulligan.mulliganList(mulliganOptions)(4))
      Thread.sleep(100)
    }
    Thread.sleep(100)
    click(PixelDataBase.mulligan.mulliganConfirm)
    Thread.sleep(postActionDelay)
  }


  def executeDiscover(decision: ActionVote): Unit = {
    if (decision != ActionUninit()) {
      decision match {
        case vote: Discover =>
          if (vote.card == 1) {
            click(PixelDataBase.discoverLoc.discoverCardOne)
            Thread.sleep(100)
          }
          if (vote.card == 2) {
            click(PixelDataBase.discoverLoc.discoverCardTwo)
            Thread.sleep(100)
          }
          if (vote.card == 3) {
            click(PixelDataBase.discoverLoc.discoverCardThree)
            Thread.sleep(100)
          }

          Thread.sleep(1000)
        case _ =>
      }
    }
    Thread.sleep(postActionDelay)
  }

  def executeChooseOne(decision: ChooseOne): Unit = {
    if (decision.card == 1) {
      click(PixelDataBase.chooseLoc.chooseCardOne)
      Thread.sleep(100)
    }
    if (decision.card == 2) {
      click(PixelDataBase.chooseLoc.chooseCardTwo)
      Thread.sleep(100)
    }
    if (decision.enemyTarget != Constants.INT_UNINIT)
      click(PixelDataBase.hisBoardLoc.hisBoardLocList(gs.enemyPlayer.board.size)(decision.enemyTarget))
    if (decision.friendlyTarget != Constants.INT_UNINIT)
      click(PixelDataBase.myBoardLoc.myBoardLocList(gs.friendlyPlayer.board.size)(decision.friendlyTarget))

    Thread.sleep(postActionDelay)
  }


  def click(position: (Int, Int)): Unit = {
    ax.mouseClick("", position._1, position._2, 1, 500)
  }

  def rightClick(position: (Int, Int)): Unit = {
    ax.mouseClick("right", position._1, position._2, 1, 500)
  }


  def init(): Unit = {
    if (!ax.winExists(TITLE))
      throw new RuntimeException("Hearthstone is not running")

    ax.winMove(TITLE, "", 0, 0, 1366, 768)
  }

  def gameOver() = {
    TimeUnit.SECONDS.sleep(15)
    //Click anywhere in order to clear any quest pop ups on the main menu when coming out of a game
    click(PixelDataBase.emoteLoc.greetings)
    TimeUnit.SECONDS.sleep(1)
    click(PixelDataBase.emoteLoc.greetings)
    TimeUnit.SECONDS.sleep(1)
    click(PixelDataBase.emoteLoc.greetings)
    TimeUnit.SECONDS.sleep(1)
    click(PixelDataBase.emoteLoc.greetings)
  }


  def executeActionVote(voteWithST: (ActionVote, (HSCard, HSCard))): Unit = {
    import PixelDataBase._

    val friendlyHandSize = gs.friendlyPlayer.hand.size
    val friendlyBoardSize = gs.friendlyPlayer.board.size
    val enemyBoardSize = gs.enemyPlayer.board.size

    voteWithST match {

      case (vote, st) =>

        vote match {

          case CardPlayWithFriendlyTarget(card, friendlyTarget)=>
            logger.debug(s"Hearthstone executing CardPlayWithFriendlyTarget. Friendly Hand Size: $friendlyHandSize, Card: $card, Friendly Board Size: $friendlyBoardSize, Friendly Target: $friendlyTarget")
            click(myHandLoc.myCardsLocList(friendlyHandSize)(card))
            TimeUnit.MILLISECONDS.sleep(delay)
            click(myBoardLoc.myBoardLocList(friendlyBoardSize)(friendlyTarget))

          case FutureCardPlayWithFriendlyTarget(card, friendlyTarget, isFutureCard, isFutureTarget)=>
            logger.debug(s"Hearthstone executing FutureCardPlayWithFriendlyTarget. Friendly Hand Size: $friendlyHandSize, Card: $card, Friendly Board Size: $friendlyBoardSize, Friendly Target: $friendlyTarget")
            click(myHandLoc.myCardsLocList(friendlyHandSize)(card))
            TimeUnit.MILLISECONDS.sleep(delay)
            click(myBoardLoc.myBoardLocList(friendlyBoardSize)(friendlyTarget))

          case CardPlayWithEnemyTarget(card, enemyTarget) =>
            logger.debug(s"Hearthstone executing CardPlayWithEnemyTarget. Friendly Hand Size: $friendlyHandSize, Card: $card, Enemy Board Size: $enemyBoardSize, Enemy Target: $enemyTarget")
            click(myHandLoc.myCardsLocList(friendlyHandSize)(card))
            TimeUnit.MILLISECONDS.sleep(delay)
            click(hisBoardLoc.hisBoardLocList(enemyBoardSize)(enemyTarget))

          case FutureCardPlayWithEnemyTarget(card, enemyTarget, not, important) =>
            logger.debug(s"Hearthstone executing FutureCardPlayWithEnemyTarget. Friendly Hand Size: $friendlyHandSize, Card: $card, Enemy Board Size: $enemyBoardSize, Enemy Target: $enemyTarget")
            click(myHandLoc.myCardsLocList(friendlyHandSize)(card))
            TimeUnit.MILLISECONDS.sleep(delay)
            click(hisBoardLoc.hisBoardLocList(enemyBoardSize)(enemyTarget))

          case CardPlay(card) =>
            logger.debug(s"Hearthstone executing CardPlay. Friendly Hand Size: $friendlyHandSize, Card: $card")
            click(myHandLoc.myCardsLocList(friendlyHandSize)(card))
            TimeUnit.MILLISECONDS.sleep(delay)
            click(myBoardLoc.myBoardFarRight)

          case FutureCardPlay(card, notImportant) =>
            logger.debug(s"Hearthstone executing FutureCardPlay. Friendly Hand Size: $friendlyHandSize, Card: $card")
            click(myHandLoc.myCardsLocList(friendlyHandSize)(card))
            TimeUnit.MILLISECONDS.sleep(delay)
            click(myBoardLoc.myBoardFarRight)

          case CardPlayWithPosition(card, position) =>
            logger.debug(s"Hearthstone executing CardPlayWithPosition. Friendly Hand Size: $friendlyHandSize, Card: $card, Position: $position")
            click(myHandLoc.myCardsLocList(friendlyHandSize)(card))
            TimeUnit.MILLISECONDS.sleep(delay)
            if(position == friendlyBoardSize+1){
              click(myBoardLoc.myBoardFarRight)
            }
            else {
              click(myBoardLoc.myBoardLocList(friendlyBoardSize)(position))
            }

          case FutureCardPlayWithPosition(card, position, not,important) =>
            logger.debug(s"Hearthstone executing FutureCardPlayWithPosition. Friendly Hand Size: $friendlyHandSize, Card: $card, Position: $position")
            click(myHandLoc.myCardsLocList(friendlyHandSize)(card))
            TimeUnit.MILLISECONDS.sleep(delay)
            if(position == friendlyBoardSize+1){
              click(myBoardLoc.myBoardFarRight)
            }
            else {
              click(myBoardLoc.myBoardLocList(friendlyBoardSize)(position))
            }



          case CardPlayWithFriendlyTargetWithPosition(card, friendlyTarget, position) =>
            logger.debug(s"Hearthstone executing CardPlayWithFriendlyTargetWithPosition. Friendly Hand Size: $friendlyHandSize, Card: $card, Friendly Board Size: $friendlyBoardSize, Friendly Target: $friendlyTarget, Position: $position")
            click(myHandLoc.myCardsLocList(friendlyHandSize)(card))
            TimeUnit.MILLISECONDS.sleep(delay)
            if(position == friendlyBoardSize+1){
              click(myBoardLoc.myBoardFarRight)
            }
            else {
              click(myBoardLoc.myBoardLocList(friendlyBoardSize)(position))
            }
            TimeUnit.MILLISECONDS.sleep(delay)
            click(myBoardLoc.myBoardLocList(friendlyBoardSize)(friendlyTarget))


          case FutureCardPlayWithFriendlyTargetWithPosition(card, friendlyTarget, position, not,important,foo) =>
            logger.debug(s"Hearthstone executing FutureCardPlayWithFriendlyTargetWithPosition. Friendly Hand Size: $friendlyHandSize, Card: $card, Friendly Board Size: $friendlyBoardSize, Friendly Target: $friendlyTarget, Position: $position")
            click(myHandLoc.myCardsLocList(friendlyHandSize)(card))
            TimeUnit.MILLISECONDS.sleep(delay)
            if(position == friendlyBoardSize+1){
              click(myBoardLoc.myBoardFarRight)
            }
            else {
              click(myBoardLoc.myBoardLocList(friendlyBoardSize)(position))
            }
            TimeUnit.MILLISECONDS.sleep(delay)
            click(myBoardLoc.myBoardLocList(friendlyBoardSize)(friendlyTarget))



          case CardPlayWithEnemyTargetWithPosition(card, enemyTarget, position) =>
            logger.debug(s"Hearthstone executing CardPlayWithEnemyTargetWithPosition. Friendly Hand Size: $friendlyHandSize, Card: $card, Enemy Board Size: $enemyBoardSize, Enemy Target: $enemyTarget, Friendly Board Size: $friendlyBoardSize, Position: $position")
            click(myHandLoc.myCardsLocList(friendlyHandSize)(card))
            TimeUnit.MILLISECONDS.sleep(delay)
            if(position == friendlyBoardSize+1){
              click(myBoardLoc.myBoardFarRight)
            }
            else {
              click(myBoardLoc.myBoardLocList(friendlyBoardSize)(position))
            }
            TimeUnit.MILLISECONDS.sleep(delay)
            click(hisBoardLoc.hisBoardLocList(enemyBoardSize)(enemyTarget))



          case FutureCardPlayWithEnemyTargetWithPosition(card, enemyTarget, position, not, important, foo) =>
            logger.debug(s"Hearthstone executing FutureCardPlayWithEnemyTargetWithPosition. Friendly Hand Size: $friendlyHandSize, Card: $card, Enemy Board Size: $enemyBoardSize, Enemy Target: $enemyTarget, Friendly Board Size: $friendlyBoardSize, Position: $position")
            click(myHandLoc.myCardsLocList(friendlyHandSize)(card))
            TimeUnit.MILLISECONDS.sleep(delay)
            if(position == friendlyBoardSize+1){
              click(myBoardLoc.myBoardFarRight)
            }
            else {
              click(myBoardLoc.myBoardLocList(friendlyBoardSize)(position))
            }
            TimeUnit.MILLISECONDS.sleep(delay)
            click(hisBoardLoc.hisBoardLocList(enemyBoardSize)(enemyTarget))





          case HeroPower() =>
            logger.debug(s"Hearthstone executing HP.")
            click(constantLoc.heroPower)

          case FutureHeroPower() =>
            logger.debug(s"Hearthstone executing Future HP.")
            click(constantLoc.heroPower)



          case HeroPowerWithEnemyTarget(enemyTarget) =>
            logger.debug(s"Hearthstone executing HP with Enemy Target. Enemy Board Size: $enemyBoardSize. Enemy Target: $enemyTarget")
            click(constantLoc.heroPower)
            TimeUnit.MILLISECONDS.sleep(delay)
            click(hisBoardLoc.hisBoardLocList(enemyBoardSize)(enemyTarget))


          case FutureHeroPowerWithEnemyTarget(enemyTarget, notImportant) =>
            logger.debug(s"Hearthstone executing Future HP with Enemy Target. Enemy Board Size: $enemyBoardSize. Enemy Target: $enemyTarget")
            click(constantLoc.heroPower)
            TimeUnit.MILLISECONDS.sleep(delay)
            click(hisBoardLoc.hisBoardLocList(enemyBoardSize)(enemyTarget))



          case HeroPowerWithFriendlyTarget(friendlyTarget) =>
            logger.debug(s"Hearthstone executing HP with Friendly Target. Friendly Board Size: $friendlyBoardSize. Friendly Target: $friendlyTarget")
            click(constantLoc.heroPower)
            TimeUnit.MILLISECONDS.sleep(delay)
            click(myBoardLoc.myBoardLocList(friendlyBoardSize)(friendlyTarget))


          case FutureHeroPowerWithFriendlyTarget(friendlyTarget, notImportant) =>
            logger.debug(s"Hearthstone executing Future HP with Friendly Target. Friendly Board Size: $friendlyBoardSize. Friendly Target: $friendlyTarget")
            click(constantLoc.heroPower)
            TimeUnit.MILLISECONDS.sleep(delay)
            click(myBoardLoc.myBoardLocList(friendlyBoardSize)(friendlyTarget))


          //Attack Type
          case NormalAttack(friendlyTarget, enemyTarget) =>
            logger.debug(s"Hearthstone executing normal attack. Friendly Target: $friendlyTarget, Friendly Board Size: $friendlyBoardSize, Enemy Target: $enemyTarget, Enemy Board Size: $enemyBoardSize.")
            click(myBoardLoc.myBoardLocList(friendlyBoardSize)(friendlyTarget))
            TimeUnit.MILLISECONDS.sleep(delay)
            click(hisBoardLoc.hisBoardLocList(enemyBoardSize)(enemyTarget))


          case FutureNormalAttack(friendlyTarget, enemyTarget, not,important) =>
            logger.debug(s"Hearthstone executing future normal attack. Friendly Target: $friendlyTarget, Friendly Board Size: $friendlyBoardSize, Enemy Target: $enemyTarget, Enemy Board Size: $enemyBoardSize.")
            click(myBoardLoc.myBoardLocList(friendlyBoardSize)(friendlyTarget))
            TimeUnit.MILLISECONDS.sleep(delay)
            click(hisBoardLoc.hisBoardLocList(enemyBoardSize)(enemyTarget))



          case EndTurn() =>
            logger.debug("Hearthstone executing end turn")
            click(PixelDataBase.constantLoc.endTurn)
            TimeUnit.MILLISECONDS.sleep(delay)


          case _ =>
        }
    }
    Thread.sleep(postActionDelay)
  }

  def EndTurn(): Unit = {
    click(PixelDataBase.constantLoc.endTurn)
  }


  def ExecuteEmojiVote(vote: EmojiVote): Unit = {

    vote match {
      //Messages from ircLogic
      case Greetings() =>
        rightClick(PixelDataBase.emoteLoc.myFace)
        TimeUnit.MILLISECONDS.sleep(delay)
        click(PixelDataBase.emoteLoc.greetings)


      case Thanks() =>
        rightClick(PixelDataBase.emoteLoc.myFace)
        TimeUnit.MILLISECONDS.sleep(delay)
        click(PixelDataBase.emoteLoc.thanks)


      case WellPlayed() =>
        rightClick(PixelDataBase.emoteLoc.myFace)
        TimeUnit.MILLISECONDS.sleep(delay)
        click(PixelDataBase.emoteLoc.wellPlayed)


      case Wow() =>
        rightClick(PixelDataBase.emoteLoc.myFace)
        TimeUnit.MILLISECONDS.sleep(delay)
        click(PixelDataBase.emoteLoc.wow)


      case Oops() =>
        rightClick(PixelDataBase.emoteLoc.myFace)
        TimeUnit.MILLISECONDS.sleep(delay)
        click(PixelDataBase.emoteLoc.oops)


      case Threaten() =>
        rightClick(PixelDataBase.emoteLoc.myFace)
        TimeUnit.MILLISECONDS.sleep(delay)
        click(PixelDataBase.emoteLoc.threaten)

      case _ =>


    }
  }


  def executeMenuVote(vote: MenuVote, menu: String): Unit = {
    vote match {
      case Play() =>
        if (menu == "mainMenu") {
          click(PixelDataBase.mainMenu.play)
        }

        if (menu == "playMenu") {
          click(PixelDataBase.playMenu.play)
        }

      case Casual() =>
        if (menu == "playMenu") {
          click(PixelDataBase.playMenu.casual)
        }

      case Ranked() =>
        if (menu == "playMenu") {
          click(PixelDataBase.playMenu.ranked)
        }

      case _ =>
    }
  }
}
