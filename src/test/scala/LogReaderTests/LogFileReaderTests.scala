package LogReaderTests

import java.io._

import FileReaders.{HSDataBase, LogFileReader, LogParser}
import Logic.IRCState
import VoteSystem.{VoteValidator, VoteAI, VoteState, VoteManager}
import com.typesafe.config.ConfigFactory
import org.scalatest.{FlatSpec, Matchers}
import tph._



/*
Possible tests:



//COMPLETE:

Card from deck to board:
-Desert Camel

Duels:
-King's Elekk

Discard:
-Soulfire

Cards into the deck:
-Gang Up



Card Death:
-Counter spell
-Redemption

New Hero Power:
-Justicar


Transform:
-PolyMorph
-Evolve or devolve
-Renounce Darkness
-Faceless Manipulator
-Druid of the saber

Hero Transform:
-Jaraxxus
-Majordomo Executus - Haven't tested but should be able to handle




//TO_DO_LIST:

Misc:
-Mayor Noggenfogger
-Yogg Saron



 */



/**
  * Created by Harambe on 2/22/2017.
  */
class LogFileReaderTests extends FlatSpec with Matchers {

  val config = ConfigFactory.load()
  val hearthstoneLogFile = new File(config.getString("tph.readerFiles.outputLog"))
  val actionLogFile = new File(config.getString("tph.writerFiles.actionLog"))
  val dataBase = new HSDataBase()
  val dummyGS = new GameState()
  val dummyIRCState = new IRCState()
  val dummyVS = new VoteState()
  val dummyAI = new VoteAI(dummyVS, dummyGS)
  val dummyValidator = new VoteValidator(dummyGS)
  val dummyVM = new VoteManager(dummyGS,dummyVS, dummyAI, dummyIRCState, dummyValidator)
  val dummyHS = new Hearthstone(dummyGS)


  def compareActualToExpected(file:File, friendlyHand: List[Card] = Nil, friendlyBoard: List[Card] = Nil, enemyHand: List[Card] = Nil, enemyBoard: List[Card] = Nil, friendlyWeaponValue: Option[Int] = None, enemyWeaponValue: Option[Int] = None): Unit = {

    val actualGameState = new GameState()
    val lp = new LogParser(actualGameState)
    val logFileReader = new LogFileReader(lp, actualGameState)
    logFileReader.parseFile(file)
    val actualFriendlyHand = actualGameState.friendlyPlayer.hand
    val actualFriendlyBoard = actualGameState.friendlyPlayer.board
    val actualEnemyHand = actualGameState.enemyPlayer.hand
    val actualEnemyBoard = actualGameState.enemyPlayer.board
    val actualFriendlyWeaponValue = actualGameState.friendlyPlayer.hero.getOrElse(NoCard()).attack
    val actualEnemyWeaponValue = actualGameState.enemyPlayer.hero.getOrElse(NoCard()).attack

    actualFriendlyHand shouldEqual friendlyHand
    actualFriendlyBoard shouldEqual friendlyBoard
    actualEnemyHand shouldEqual enemyHand
    actualEnemyBoard shouldEqual enemyBoard
    actualFriendlyWeaponValue shouldEqual friendlyWeaponValue
    actualEnemyWeaponValue shouldEqual enemyWeaponValue
  }


  "LogFileReader scenarios" should "detect and define player" in {

    val expectedFriendlyHand = List[Card](
      new Card("Equality", 67, 1, 500, 2, "EX1_619", cardInfo = dataBase.cardIDMap("EX1_619")),
      new Card("Leeroy Jenkins", 71, 2, 500, 2, "EX1_116", cardInfo = dataBase.cardIDMap("EX1_116")),
      new Card("Don Han'Cho", 79, 3, 500, 2, "CFM_685", cardInfo = dataBase.cardIDMap("CFM_685")),
      new Card("Blessed Champion", 53, 4, 500, 2, "EX1_355", cardInfo = dataBase.cardIDMap("EX1_355")))
    val expectedFriendlyBoard = List[Card](
      new Card("Wild Pyromancer", 80, 500, 1, 2, "NEW1_020", cardInfo = dataBase.cardIDMap("NEW1_020")))
    val expectedEnemyHand = List[Card](
      new Card("Constant Uninitialized", 43, 2, 500, 1, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 4, 1, 500, 1, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 48, 3, 500, 1, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 17, 4, 500, 1, "Constant Uninitialized"))
    val expectedEnemyBoard = List[Card](
      new Card("Sapling", 87, 500, 1, 1, "AT_037t", cardInfo = dataBase.cardIDMap("AT_037t")),
      new Card("Sapling", 88, 500, 2, 1, "AT_037t", cardInfo = dataBase.cardIDMap("AT_037t")))

    compareActualToExpected(new File(getClass.getResource("/debugsituations/DefinePlayers.txt").getPath), expectedFriendlyHand, expectedFriendlyBoard, expectedEnemyHand, expectedEnemyBoard)
  }


  it should "Detect MulliganRedraw" in {

    val expectedFriendlyHand = List[Card](
      new Card("Lay on Hands", 36, 1, 500, 2, "EX1_354", cardInfo = dataBase.cardIDMap("EX1_354")),
      new Card("Spellbreaker", 44, 2, 500, 2, "EX1_048", cardInfo = dataBase.cardIDMap("EX1_048")),
      new Card("Aldor Peacekeeper", 40, 3, 500, 2, "EX1_382", cardInfo = dataBase.cardIDMap("EX1_382")),
      new Card("Acidic Swamp Ooze", 58, 4, 500, 2, "EX1_066", cardInfo = dataBase.cardIDMap("EX1_066")),
      new Card("Solemn Vigil", 38, 5, 500, 2, "BRM_001", cardInfo = dataBase.cardIDMap("BRM_001")))
    val expectedFriendlyBoard = List[Card](
      new Card("Acolyte of Pain", 48, 500, 1, 2, "EX1_007", cardInfo = dataBase.cardIDMap("EX1_007")))
    val expectedEnemyHand = List[Card](
      new Card("Constant Uninitialized", 21, 1, 500, 1, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 4, 2, 500, 1, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 10, 3, 500, 1, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 25, 4, 500, 1, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 32, 5, 500, 1, "Constant Uninitialized"))

      compareActualToExpected(new File(getClass.getResource("/debugsituations/Mulligan.txt").getPath), expectedFriendlyHand, expectedFriendlyBoard, expectedEnemyHand, enemyWeaponValue = Some(0))
  }

  it should "Detect CardPlayed" in {

    val expectedFriendlyHand = List[Card](
      new Card("Lay on Hands", 36, 1, 500, 2, "EX1_354", cardInfo = dataBase.cardIDMap("EX1_354")),
      new Card("Spellbreaker", 44, 2, 500, 2, "EX1_048", cardInfo = dataBase.cardIDMap("EX1_048")),
      new Card("Aldor Peacekeeper", 40, 3, 500, 2, "EX1_382", cardInfo = dataBase.cardIDMap("EX1_382")),
      new Card("Acidic Swamp Ooze", 58, 4, 500, 2, "EX1_066", cardInfo = dataBase.cardIDMap("EX1_066")),
      new Card("Solemn Vigil", 38, 5, 500, 2, "BRM_001", cardInfo = dataBase.cardIDMap("BRM_001")))
    val expectedFriendlyBoard = List[Card](
      new Card("Acolyte of Pain", 48, 500, 1, 2, "EX1_007", cardInfo = dataBase.cardIDMap("EX1_007")))
    val expectedEnemyHand = List[Card](
      new Card("Constant Uninitialized", 21, 1, 500, 1, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 4, 2, 500, 1, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 10, 3, 500, 1, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 25, 4, 500, 1, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 32, 5, 500, 1, "Constant Uninitialized"))

      compareActualToExpected(new File(getClass.getResource("/debugsituations/Mulligan.txt").getPath), expectedFriendlyHand, expectedFriendlyBoard, expectedEnemyHand, enemyWeaponValue = Some(0))
  }

  it should "Detect Card Death" in {

    val expectedFriendlyHand = List[Card](
      new Card("Truesilver Champion", 28, 1, 500, 1, "CS2_097", cardInfo = dataBase.cardIDMap("CS2_097")),
      new Card("Wild Pyromancer", 15, 3, 500, 1, "NEW1_020", cardInfo = dataBase.cardIDMap("NEW1_020")),
      new Card("The Coin", 68, 4, 500, 1, "GAME_005", cardInfo = dataBase.cardIDMap("GAME_005")),
      new Card("Acolyte of Pain", 25, 2, 500, 1, "EX1_007", cardInfo = dataBase.cardIDMap("EX1_007")),
      new Card("Solemn Vigil", 12, 5, 500, 1, "BRM_001", cardInfo = dataBase.cardIDMap("BRM_001")),
      new Card("Eater of Secrets", 32, 6, 500, 1, "OG_254", cardInfo = dataBase.cardIDMap("OG_254")),
      new Card("Spellbreaker", 30, 7, 500, 1, "EX1_048", cardInfo = dataBase.cardIDMap("EX1_048")))
    val expectedEnemyHand = List[Card](
      new Card("Constant Uninitialized", 39, 1, 500, 2, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 59, 2, 500, 2, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 34, 3, 500, 2, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 37, 4, 500, 2, "Constant Uninitialized"))
    val expectedEnemyBoard = List[Card](
      new Card("Stonetusk Boar", 60, 500, 1, 2, "CS2_171", cardInfo = dataBase.cardIDMap("CS2_171")),
      new Card("Healing Totem", 69, 500, 2, 2, "NEW1_009", cardInfo = dataBase.cardIDMap("NEW1_009")))


    compareActualToExpected(new File(getClass.getResource("/debugsituations/CardDeath.txt").getPath), expectedFriendlyHand, enemyHand = expectedEnemyHand, enemyBoard = expectedEnemyBoard)
  }

  it should "Detect friendly and enemy Hex" in {

    val expectedFriendlyHand = List[Card](
      new Card("Fire Elemental", 8, 1, 500, 1, "CS2_042", cardInfo = dataBase.cardIDMap("CS2_042")),
      new Card("Coldlight Oracle", 15, 2, 500, 1, "EX1_050", cardInfo = dataBase.cardIDMap("EX1_050")),
      new Card("Azure Drake", 4, 3, 500, 1, "EX1_284", cardInfo = dataBase.cardIDMap("EX1_284")),
      new Card("Coldlight Oracle", 22, 4, 500, 1, "EX1_050", cardInfo = dataBase.cardIDMap("EX1_050")),
      new Card("Ancestral Knowledge", 5, 5, 500, 1, "AT_053", cardInfo = dataBase.cardIDMap("AT_053")),
      new Card("Loot Hoarder", 33, 6, 500, 1, "EX1_096", cardInfo = dataBase.cardIDMap("EX1_096")))
    val expectedFriendlyBoard = List[Card](
      new Card("Polluted Hoarder", 70, 500, 2, 1, "OG_323", cardInfo = dataBase.cardIDMap("OG_323")),
      new Card("Flame Juggler", 10, 500, 1, 1, "AT_094", cardInfo = dataBase.cardIDMap("AT_094")))
    val expectedEnemyHand = List[Card](
      new Card("Constant Uninitialized", 57, 1, 500, 2, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 54, 2, 500, 2, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 51, 3, 500, 2, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 39, 4, 500, 2, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 60, 5, 500, 2, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 62, 6, 500, 2, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 36, 7, 500, 2, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 55, 8, 500, 2, "Constant Uninitialized"))


      compareActualToExpected(new File(getClass.getResource("/debugsituations/Hex.txt").getPath), expectedFriendlyHand, expectedFriendlyBoard,expectedEnemyHand)
  }

  it should "Detect friendly and enemy card return" in {

    val expectedFriendlyHand = List[Card](
      new Card("Cult Master", 35, 1, 500, 2, "EX1_595", cardInfo = dataBase.cardIDMap("EX1_595")),
      new Card("The Coin", 68, 3, 500, 2, "GAME_005", cardInfo = dataBase.cardIDMap("GAME_005")),
      new Card("Novice Engineer", 36, 2, 500, 2, "EX1_015", cardInfo = dataBase.cardIDMap("EX1_015")),
      new Card("Cult Master", 51, 4, 500, 2, "EX1_595", cardInfo = dataBase.cardIDMap("EX1_595")),
      new Card("Fan of Knives", 44, 5, 500, 2, "EX1_129", cardInfo = dataBase.cardIDMap("EX1_129")))
    val expectedFriendlyBoard = List[Card](
      new Card("Loot Hoarder", 42, 500, 1, 2, "EX1_096", cardInfo = dataBase.cardIDMap("EX1_096")))
    val expectedEnemyHand = List[Card](
      new Card("Constant Uninitialized", 4, 1, 500, 1, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 14, 2, 500, 1, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 7, 3, 500, 1, "Constant Uninitialized"),
      new Card("Loot Hoarder", 23, 4, 500, 1, "EX1_096", cardInfo = dataBase.cardIDMap("EX1_096")))


      compareActualToExpected(new File(getClass.getResource("/debugsituations/ShadowStep.txt").getPath), expectedFriendlyHand, expectedFriendlyBoard, expectedEnemyHand)
  }

  it should "Detect vanish" in{

    val expectedFriendlyHand = List[Card](
      new Card("Sprint", 48, 1, 500, 2, "CS2_077", cardInfo = dataBase.cardIDMap("CS2_077")),
      new Card("Gnomish Inventor", 58, 2, 500, 2, "CS2_147", cardInfo = dataBase.cardIDMap("CS2_147")),
      new Card("Coldlight Oracle", 35, 3, 500, 2, "EX1_050", cardInfo = dataBase.cardIDMap("EX1_050")),
      new Card("Shiv", 38, 4, 500, 2, "EX1_278", cardInfo = dataBase.cardIDMap("EX1_278")),
      new Card("Shiv", 53, 5, 500, 2, "EX1_278", cardInfo = dataBase.cardIDMap("EX1_278")),
      new Card("Coldlight Oracle", 39, 6, 500, 2, "EX1_050", cardInfo = dataBase.cardIDMap("EX1_050")),
      new Card("Sap", 36, 7, 500, 2, "EX1_581", cardInfo = dataBase.cardIDMap("EX1_581")),
      new Card("Fan of Knives", 45, 8, 500, 2, "EX1_129", cardInfo = dataBase.cardIDMap("EX1_129")),
      new Card("Acolyte of Pain", 37, 9, 500, 2, "EX1_007", cardInfo = dataBase.cardIDMap("EX1_007")),
      new Card("Gnomish Inventor", 56, 10, 500, 2, "CS2_147", cardInfo = dataBase.cardIDMap("CS2_147")))
    val expectedEnemyHand = List[Card](
      new Card("Coldlight Oracle", 10, 1, 500, 1, "EX1_050", cardInfo = dataBase.cardIDMap("EX1_050")),
      new Card("Constant Uninitialized", 11, 2, 500, 1, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 12, 3, 500, 1, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 6, 4, 500, 1, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 15, 5, 500, 1, "Constant Uninitialized"),
      new Card("Runic Egg", 14, 6, 500, 1, "KAR_029", cardInfo = dataBase.cardIDMap("KAR_029")),
      new Card("Bloodmage Thalnos", 20, 7, 500, 1, "EX1_012", cardInfo = dataBase.cardIDMap("EX1_012")),
      new Card("Gnomish Inventor", 4, 8, 500, 1, "CS2_147", cardInfo = dataBase.cardIDMap("CS2_147")),
      new Card("Loot Hoarder", 22, 9, 500, 1, "EX1_096", cardInfo = dataBase.cardIDMap("EX1_096")))
    val expectedEnemyBoard = List[Card](
      new Card("Cult Master", 17, 500, 1, 1, "EX1_595", cardInfo = dataBase.cardIDMap("EX1_595")))


    compareActualToExpected(new File(getClass.getResource("/debugsituations/Vanish.txt").getPath), expectedFriendlyHand, enemyHand = expectedEnemyHand, enemyBoard = expectedEnemyBoard)
  }

  it should "Detect friendly and enemy minions being controlled" in {

    val expectedFriendlyHand = List[Card](
      new Card("Cult Master", 4, 1, 500, 1, "EX1_595", cardInfo = dataBase.cardIDMap("EX1_595")),
      new Card("Gnomish Inventor", 5, 2, 500, 1, "CS2_147", cardInfo = dataBase.cardIDMap("CS2_147")),
      new Card("Coldlight Oracle", 27, 3, 500, 1, "EX1_050", cardInfo = dataBase.cardIDMap("EX1_050")),
      new Card("Azure Drake", 26, 4, 500, 1, "EX1_284", cardInfo = dataBase.cardIDMap("EX1_284")),
      new Card("Gnomish Inventor", 8, 5, 500, 1, "CS2_147", cardInfo = dataBase.cardIDMap("CS2_147")),
      new Card("Polluted Hoarder", 33, 6, 500, 1, "OG_323", cardInfo = dataBase.cardIDMap("OG_323")))
    val expectedEnemyHand = List[Card](
      new Card("Constant Uninitialized", 61, 2, 500, 2, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 43, 1, 500, 2, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 62, 3, 500, 2, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 60, 4, 500, 2, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 37, 5, 500, 2, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 36, 6, 500, 2, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 34, 7, 500, 2, "Constant Uninitialized"))
    val expectedEnemyBoard = List[Card](
      new Card("Gnomish Inventor", 51, 500, 2, 2, "CS2_147", cardInfo = dataBase.cardIDMap("CS2_147")),
      new Card("Cabal Shadow Priest", 55, 500, 1, 2, "EX1_091", cardInfo = dataBase.cardIDMap("EX1_091")),
      new Card("Novice Engineer", 25, 500, 3, 2, "EX1_015", cardInfo = dataBase.cardIDMap("EX1_015")))


    compareActualToExpected(new File(getClass.getResource("/debugsituations/MinionsControlled.txt").getPath), expectedFriendlyHand, enemyHand = expectedEnemyHand, enemyBoard = expectedEnemyBoard)
  }

  it should "Detect weapon played" in {
    val expectedFriendlyHand = List[Card](
      new Card("Equality", 48, 1, 500, 2, "EX1_619", cardInfo = dataBase.cardIDMap("EX1_619")),
      new Card("The Coin", 68, 4, 500, 2, "GAME_005", cardInfo = dataBase.cardIDMap("GAME_005")),
      new Card("Eater of Secrets", 49, 2, 500, 2, "OG_254", cardInfo = dataBase.cardIDMap("OG_254")),
      new Card("Tirion Fordring", 40, 3, 500, 2, "EX1_383", cardInfo = dataBase.cardIDMap("EX1_383")),
      new Card("Solemn Vigil", 54, 5, 500, 2, "BRM_001", cardInfo = dataBase.cardIDMap("BRM_001")),
      new Card("Equality", 46, 6, 500, 2, "EX1_619", cardInfo = dataBase.cardIDMap("EX1_619")),
      new Card("Acidic Swamp Ooze", 63, 7, 500, 2, "EX1_066", cardInfo = dataBase.cardIDMap("EX1_066")),
      new Card("Aldor Peacekeeper", 59, 8, 500, 2, "EX1_382", cardInfo = dataBase.cardIDMap("EX1_382")))
    val expectedEnemyHand = List[Card](
      new Card("Constant Uninitialized", 27, 1, 500, 1, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 4, 2, 500, 1, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 16, 3, 500, 1, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 22, 4, 500, 1, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 23, 5, 500, 1, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 10, 6, 500, 1, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 5, 7, 500, 1, "Constant Uninitialized"))


    val expectedFriendlyWeaponValue = Some(0)
    val expectedEnemyWeaponValue = Some(5)

    compareActualToExpected(new File(getClass.getResource("/debugsituations/WeaponsEquipped.txt").getPath), expectedFriendlyHand, enemyHand = expectedEnemyHand, friendlyWeaponValue = expectedFriendlyWeaponValue, enemyWeaponValue = expectedEnemyWeaponValue)
  }

  it should "Detect weapon destroyed" in {

    val actualGameState = new GameState
    val lp = new LogParser(actualGameState)
    val logFileReader = new LogFileReader(lp, actualGameState)
    logFileReader.parseFile(new File(getClass.getResource("/debugsituations/WeaponsDestroyed.txt").getPath))
    val actualFriendlyWeaponValue = actualGameState.friendlyPlayer.hero.get.attack
    val actualEnemyWeaponValue = actualGameState.enemyPlayer.hero.get.attack

    actualFriendlyWeaponValue shouldEqual Some(0)
    actualEnemyWeaponValue shouldEqual Some(0)
  }

  it should "Detect when a game is over" in {
    compareActualToExpected(new File(getClass.getResource("/debugsituations/Concede.txt").getPath))
  }

  it should "Detect an overdraw" in {

    val expectedFriendlyHand = List[Card](
      new Card("Brann Bronzebeard", 60, 1, 500, 2, "LOE_077", cardInfo = dataBase.cardIDMap("LOE_077")),
      new Card("Solemn Vigil", 57, 2, 500, 2, "BRM_001", cardInfo = dataBase.cardIDMap("BRM_001")),
      new Card("The Coin", 68, 3, 500, 2, "GAME_005", cardInfo = dataBase.cardIDMap("GAME_005")),
      new Card("Hammer of Wrath", 55, 4, 500, 2, "CS2_094", cardInfo = dataBase.cardIDMap("CS2_094")),
      new Card("Loot Hoarder", 47, 5, 500, 2, "EX1_096", cardInfo = dataBase.cardIDMap("EX1_096")),
      new Card("Blessed Champion", 48, 6, 500, 2, "EX1_355", cardInfo = dataBase.cardIDMap("EX1_355")),
      new Card("Equality", 34, 7, 500, 2, "EX1_619", cardInfo = dataBase.cardIDMap("EX1_619")),
      new Card("Lay on Hands", 62, 8, 500, 2, "EX1_354", cardInfo = dataBase.cardIDMap("EX1_354")),
      new Card("Tirion Fordring", 44, 9, 500, 2, "EX1_383", cardInfo = dataBase.cardIDMap("EX1_383")),
      new Card("Wild Pyromancer", 51, 10, 500, 2, "NEW1_020", cardInfo = dataBase.cardIDMap("NEW1_020")))
    val expectedFriendlyBoard = List[Card](
      new Card("Aldor Peacekeeper", 38, 500, 2, 2, "EX1_382", cardInfo = dataBase.cardIDMap("EX1_382")),
      new Card("Silver Hand Recruit", 70, 500, 3, 2, "CS2_101t", cardInfo = dataBase.cardIDMap("CS2_101t")),
      new Card("Acolyte of Pain", 63, 500, 1, 2, "EX1_007", cardInfo = dataBase.cardIDMap("EX1_007")))
    val expectedEnemyHand = List[Card](
      new Card("Constant Uninitialized", 27, 1, 500, 1, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 15, 2, 500, 1, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 4, 3, 500, 1, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 33, 4, 500, 1, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 12, 5, 500, 1, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 26, 6, 500, 1, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 10, 7, 500, 1, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 6, 8, 500, 1, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 23, 9, 500, 1, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 9, 10, 500, 1, "Constant Uninitialized"))
    val expectedEnemyBoard = List[Card](
      new Card("Dread Corsair", 14, 500, 1, 1, "NEW1_022", cardInfo = dataBase.cardIDMap("NEW1_022")))


    compareActualToExpected(new File(getClass.getResource("/debugsituations/OverDraw.txt").getPath), expectedFriendlyHand, expectedFriendlyBoard, expectedEnemyHand, expectedEnemyBoard)
  }

  it should "Detect Enemy Kazakus's custom spell creation" in {

    val expectedFriendlyHand = List[Card](
      new Card("Solemn Vigil", 62, 1, 500, 2, "BRM_001", cardInfo = dataBase.cardIDMap("BRM_001")),
      new Card("Eater of Secrets", 47, 2, 500, 2, "OG_254", cardInfo = dataBase.cardIDMap("OG_254")),
      new Card("Blessed Champion", 55, 3, 500, 2, "EX1_355", cardInfo = dataBase.cardIDMap("EX1_355")),
      new Card("Solemn Vigil", 63, 4, 500, 2, "BRM_001", cardInfo = dataBase.cardIDMap("BRM_001")),
      new Card("Spellbreaker", 53, 5, 500, 2, "EX1_048", cardInfo = dataBase.cardIDMap("EX1_048")),
      new Card("Aldor Peacekeeper", 60, 6, 500, 2, "EX1_382", cardInfo = dataBase.cardIDMap("EX1_382")),
      new Card("Acolyte of Pain", 52, 7, 500, 2, "EX1_007", cardInfo = dataBase.cardIDMap("EX1_007")),
      new Card("Aldor Peacekeeper", 59, 8, 500, 2, "EX1_382", cardInfo = dataBase.cardIDMap("EX1_382")))
    val expectedEnemyHand = List[Card](
      new Card("Constant Uninitialized", 20, 1, 500, 1, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 13, 2, 500, 1, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 31, 3, 500, 1, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 68, 4, 500, 1, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 24, 5, 500, 1, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 6, 6, 500, 1, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 4, 7, 500, 1, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 78, 8, 500, 1, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 88, 9, 500, 1, "Constant Uninitialized"))
    val expectedEnemyBoard = List[Card](
      new Card("Brann Bronzebeard", 12, 500, 2, 1, "LOE_077", cardInfo = dataBase.cardIDMap("LOE_077")),
      new Card("Kazakus", 5, 500, 1, 1, "CFM_621", cardInfo = dataBase.cardIDMap("CFM_621")))


    compareActualToExpected(new File(getClass.getResource("/debugsituations/EnemyCustomSpell.txt").getPath), expectedFriendlyHand, Nil, expectedEnemyHand, expectedEnemyBoard)

  }

  it should "Detect Friendly Kazakus's custom spell creation" in {

    val expectedFriendlyHand = List[Card](
      new Card("Kabal Courier", 23, 1, 500, 1, "CFM_649", cardInfo = dataBase.cardIDMap("CFM_649")),
      new Card("Polymorph", 24, 2, 500, 1, "CS2_022", cardInfo = dataBase.cardIDMap("CS2_022")),
      new Card("Arcane Blast", 27, 3, 500, 1, "AT_004", cardInfo = dataBase.cardIDMap("AT_004")),
      new Card("Ice Lance", 18, 4, 500, 1, "CS2_031", cardInfo = dataBase.cardIDMap("CS2_031")),
      new Card("Frostbolt", 20, 5, 500, 1, "CS2_024", cardInfo = dataBase.cardIDMap("CS2_024")),
      new Card("Kazakus Potion", 87, 6, 500, 1, "CFM_621t15", cardInfo = dataBase.cardIDMap("CFM_621t15")))
    val expectedFriendlyBoard = List[Card](
      new Card("Kazakus", 16, 500, 1, 1, "CFM_621", cardInfo = dataBase.cardIDMap("CFM_621")))
    val expectedEnemyHand = List[Card](
      new Card("Constant Uninitialized", 56, 1, 500, 2, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 43, 2, 500, 2, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 51, 3, 500, 2, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 48, 4, 500, 2, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 35, 5, 500, 2, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 41, 6, 500, 2, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 47, 7, 500, 2, "Constant Uninitialized"))
    val expectedEnemyBoard = List[Card](
      new Card("Loot Hoarder", 46, 500, 1, 2, "EX1_096", cardInfo = dataBase.cardIDMap("EX1_096")))


    compareActualToExpected(new File(getClass.getResource("/debugsituations/FriendlyCustomSpell.txt").getPath), expectedFriendlyHand, expectedFriendlyBoard, expectedEnemyHand, expectedEnemyBoard, Some(1), Some(0))

  }

  it should "Detect Deck to board directly" in {

    val expectedFriendlyHand = List[Card](
      new Card("Call of the Wild", 37, 1, 500, 2, "OG_211", cardInfo = dataBase.cardIDMap("OG_211")),
      new Card("Call of the Wild", 38, 2, 500, 2, "OG_211", cardInfo = dataBase.cardIDMap("OG_211")),
      new Card("Houndmaster", 52, 3, 500, 2, "DS1_070", cardInfo = dataBase.cardIDMap("DS1_070")))
    val expectedFriendlyBoard = List[Card](
      new Card("Injured Kvaldir", 60, 500, 3, 2, "AT_105", cardInfo = dataBase.cardIDMap("AT_105"), isDamaged = true),
      new Card("Savannah Highmane", 34, 500, 1, 2, "EX1_534", cardInfo = dataBase.cardIDMap("EX1_534"), isDamaged = true),
      new Card("Desert Camel", 59, 500, 2, 2, "LOE_020", cardInfo = dataBase.cardIDMap("LOE_020")),
      new Card("Injured Kvaldir", 54, 500, 4, 2, "AT_105", cardInfo = dataBase.cardIDMap("AT_105")))
    val expectedEnemyHand = List[Card](
      new Card("Constant Uninitialized", 12, 2, 500, 1, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 17, 1, 500, 1, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 68, 3, 500, 1, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 33, 4, 500, 1, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 14, 5, 500, 1, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 22, 6, 500, 1, "Constant Uninitialized"))
    val expectedEnemyBoard = List[Card](
      new Card("Knife Juggler", 28, 500, 1, 1, "NEW1_019", cardInfo = dataBase.cardIDMap("NEW1_019")),
      new Card("Mind Control Tech", 9, 500, 2, 1, "EX1_085", cardInfo = dataBase.cardIDMap("EX1_085")),
      new Card("Argent Squire", 31, 500, 3, 1, "EX1_008", cardInfo = dataBase.cardIDMap("EX1_008")),
      new Card("Desert Camel", 6, 500, 4, 1, "LOE_020", cardInfo = dataBase.cardIDMap("LOE_020")))


    compareActualToExpected(new File(getClass.getResource("/debugsituations/DesertCamel.txt").getPath), expectedFriendlyHand, expectedFriendlyBoard, expectedEnemyHand, expectedEnemyBoard, Some(3))
  }

  it should "Detect Joust Draw" in {

    val expectedFriendlyHand = List[Card](
      new Card("Call of the Wild", 37, 1, 500, 2, "OG_211", cardInfo = dataBase.cardIDMap("OG_211")),
      new Card("Call of the Wild", 38, 2, 500, 2, "OG_211", cardInfo = dataBase.cardIDMap("OG_211")),
      new Card("Deadly Shot", 36, 3, 500, 2, "EX1_617", cardInfo = dataBase.cardIDMap("EX1_617")),
      new Card("Desert Camel", 39, 4, 500, 2, "LOE_020", cardInfo = dataBase.cardIDMap("LOE_020")),
      new Card("Infested Wolf", 55, 5, 500, 2, "OG_216", cardInfo = dataBase.cardIDMap("OG_216")))
    val expectedFriendlyBoard = List[Card](
      new Card("Huge Toad", 63, 500, 3, 2, "LOE_046", cardInfo = dataBase.cardIDMap("LOE_046")),
      new Card("Hyena", 93, 500, 2, 2, "EX1_534t", cardInfo = dataBase.cardIDMap("EX1_534t")),
      new Card("Hyena", 94, 500, 1, 2, "EX1_534t", cardInfo = dataBase.cardIDMap("EX1_534t")),
      new Card("King's Elekk", 35, 500, 4, 2, "AT_058", cardInfo = dataBase.cardIDMap("AT_058")))
    val expectedEnemyHand = List[Card](
      new Card("Constant Uninitialized", 12, 2, 500, 1, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 17, 1, 500, 1, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 68, 3, 500, 1, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 14, 4, 500, 1, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 15, 5, 500, 1, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 26, 6, 500, 1, "Constant Uninitialized"))
    val expectedEnemyBoard = List[Card](
      new Card("Emperor Thaurissan", 18, 500, 1, 1, "BRM_028", cardInfo = dataBase.cardIDMap("BRM_028")))


    compareActualToExpected(new File(getClass.getResource("/debugsituations/KingElekkDraw.txt").getPath), expectedFriendlyHand, expectedFriendlyBoard, expectedEnemyHand, expectedEnemyBoard, Some(3))
  }

  it should "Detect discard" in {

    val expectedFriendlyHand = List[Card](
      new Card("Curse of Rafaam", 10, 1, 500, 1, "LOE_007", cardInfo = dataBase.cardIDMap("LOE_007")),
      new Card("Coldlight Oracle", 22, 2, 500, 1, "EX1_050", cardInfo = dataBase.cardIDMap("EX1_050")))
    val expectedEnemyHand = List[Card](
      new Card("Constant Uninitialized", 36, 1, 500, 2, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 68, 3, 500, 2, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 53, 2, 500, 2, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 48, 4, 500, 2, "Constant Uninitialized"))


      compareActualToExpected(new File(getClass.getResource("/debugsituations/Discard.txt").getPath), expectedFriendlyHand, Nil, expectedEnemyHand)
  }

  it should "Detect card creation directly to deck (Gang up scenario)" in{

    val expectedFriendlyHand = List[Card](
      new Card("Gnomish Inventor", 62, 1, 500, 2, "CS2_147", cardInfo = dataBase.cardIDMap("CS2_147")),
      new Card("Coldlight Oracle", 43, 2, 500, 2, "EX1_050", cardInfo = dataBase.cardIDMap("EX1_050")),
      new Card("Ragnaros the Firelord", 60, 3, 500, 2, "EX1_298", cardInfo = dataBase.cardIDMap("EX1_298")),
      new Card("Sprint", 55, 4, 500, 2, "CS2_077", cardInfo = dataBase.cardIDMap("CS2_077")),
      new Card("Polluted Hoarder", 35, 5, 500, 2, "OG_323", cardInfo = dataBase.cardIDMap("OG_323")),
      new Card("Fan of Knives", 52, 6, 500, 2, "EX1_129", cardInfo = dataBase.cardIDMap("EX1_129")),
      new Card("Gang Up", 41, 7, 500, 2, "BRM_007", cardInfo = dataBase.cardIDMap("BRM_007")),
      new Card("Assassin's Blade", 39, 8, 500, 2, "CS2_080", cardInfo = dataBase.cardIDMap("CS2_080")),
      new Card("Acolyte of Pain", 70, 9, 500, 2, "EX1_007", cardInfo = dataBase.cardIDMap("EX1_007")))
    val expectedFriendlyBoard = List[Card](
      new Card("Acolyte of Pain", 56, 500, 1, 2, "EX1_007", cardInfo = dataBase.cardIDMap("EX1_007"), isDamaged = true),
      new Card("Undercity Valiant", 48, 500, 2, 2, "AT_030", cardInfo = dataBase.cardIDMap("AT_030"), isDamaged = true))
    val expectedEnemyHand = List[Card](
      new Card("Constant Uninitialized", 7, 1, 500, 1, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 24, 2, 500, 1, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 26, 3, 500, 1, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 17, 4, 500, 1, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 10, 5, 500, 1, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 29, 6, 500, 1, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 74, 7, 500, 1, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 31, 8, 500, 1, "Constant Uninitialized"))
    val expectedEnemyBoard = List[Card](
      new Card("Flame Juggler", 19, 500, 1, 1, "AT_094", cardInfo = dataBase.cardIDMap("AT_094")))


    compareActualToExpected(new File(getClass.getResource("/debugsituations/GangUp.txt").getPath), expectedFriendlyHand, expectedFriendlyBoard, expectedEnemyHand, expectedEnemyBoard, Some(3))
  }

  it should "Detect Jaraxxus" in {
    val expectedFriendlyHand = List[Card](
      new Card("Arcane Intellect", 21, 1, 500, 1, "CS2_023", cardInfo = dataBase.cardIDMap("CS2_023")),
      new Card("The Coin", 68, 2, 500, 1, "GAME_005", cardInfo = dataBase.cardIDMap("GAME_005")),
      new Card("Arcane Explosion", 30, 3, 500, 1, "CS2_025", cardInfo = dataBase.cardIDMap("CS2_025")),
      new Card("Arcane Intellect", 26, 4, 500, 1, "CS2_023", cardInfo = dataBase.cardIDMap("CS2_023")))
    val expectedFriendlyBoard = List[Card](
      new Card("River Crocolisk", 12, 500, 2, 1, "CS2_120", cardInfo = dataBase.cardIDMap("CS2_120"), attack = Some(3)),
      new Card("Oasis Snapjaw", 28, 500, 1, 1, "CS2_119", cardInfo = dataBase.cardIDMap("CS2_119"), isDamaged = true, attack = Some(3)),
      new Card("Raid Leader", 11, 500, 3, 1, "CS2_122", cardInfo = dataBase.cardIDMap("CS2_122")))
    val expectedEnemyHand = List[Card](
      new Card("Constant Uninitialized", 37, 1, 500, 2, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 40, 2, 500, 2, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 47, 3, 500, 2, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 45, 4, 500, 2, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 54, 5, 500, 2, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 50, 6, 500, 2, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 59, 7, 500, 2, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 44, 8, 500, 2, "Constant Uninitialized"))
    val expectedEnemyBoard = List[Card](
      new Card("Blood Imp", 72, 500, 1, 2, "CS2_059", cardInfo = dataBase.cardIDMap("CS2_059"), isStealthed = true))


    compareActualToExpected(new File(getClass.getResource("/debugsituations/Jaraxxus.txt").getPath), expectedFriendlyHand, expectedFriendlyBoard, expectedEnemyHand, expectedEnemyBoard, enemyWeaponValue = Some(3))


  }

  it should "Detect Evolve " in {

    val expectedFriendlyHand = List[Card](
      new Card("The Coin", 68, 2, 500, 2, "GAME_005", cardInfo = dataBase.cardIDMap("GAME_005")),
      new Card("Arcane Missiles", 39, 1, 500, 2, "EX1_277", cardInfo = dataBase.cardIDMap("EX1_277")),
      new Card("Sen'jin Shieldmasta", 43, 3, 500, 2, "CS2_179", cardInfo = dataBase.cardIDMap("CS2_179")),
      new Card("Nightblade", 54, 4, 500, 2, "EX1_593", cardInfo = dataBase.cardIDMap("EX1_593")),
      new Card("River Crocolisk", 63, 5, 500, 2, "CS2_120", cardInfo = dataBase.cardIDMap("CS2_120")),
      new Card("Oasis Snapjaw", 52, 6, 500, 2, "CS2_119", cardInfo = dataBase.cardIDMap("CS2_119")))
    val expectedFriendlyBoard = List[Card](
      new Card("Novice Engineer", 50, 500, 1, 2, "EX1_015", cardInfo = dataBase.cardIDMap("EX1_015")))
    val expectedEnemyHand = List[Card](
      new Card("Constant Uninitialized", 28, 1, 500, 1, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 4, 2, 500, 1, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 8, 3, 500, 1, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 19, 4, 500, 1, "Constant Uninitialized"))
    val expectedEnemyBoard = List[Card](
      new Card("Tunnel Trogg", 70, 500, 2, 1, "LOE_018", cardInfo = dataBase.cardIDMap("LOE_018"), isDamaged = true, attack = Some(2)),
      new Card("Damaged Golem", 72, 500, 1, 1, "skele21", cardInfo = dataBase.cardIDMap("skele21")))


    compareActualToExpected(new File(getClass.getResource("/debugsituations/Evolve.txt").getPath), expectedFriendlyHand, expectedFriendlyBoard, expectedEnemyHand, expectedEnemyBoard)
  }

  it should "Detect Renounce Darkness" in {
    val expectedFriendlyHand = List[Card](
      new Card("Arcane Intellect", 13, 1, 500, 1, "CS2_023", cardInfo = dataBase.cardIDMap("CS2_023")),
      new Card("Polymorph", 33, 2, 500, 1, "CS2_022", cardInfo = dataBase.cardIDMap("CS2_022")),
      new Card("Boulderfist Ogre", 6, 3, 500, 1, "CS2_200", cardInfo = dataBase.cardIDMap("CS2_200")),
      new Card("River Crocolisk", 5, 4, 500, 1, "CS2_120", cardInfo = dataBase.cardIDMap("CS2_120")),
      new Card("Fireball", 8, 5, 500, 1, "CS2_029", cardInfo = dataBase.cardIDMap("CS2_029")),
      new Card("Murloc Raider", 24, 6, 500, 1, "CS2_168", cardInfo = dataBase.cardIDMap("CS2_168")))
    val expectedFriendlyBoard = List[Card](
      new Card("Novice Engineer", 23, 500, 1, 1, "EX1_015", cardInfo = dataBase.cardIDMap("EX1_015")))
    val expectedEnemyHand = List[Card](
      new Card("Constant Uninitialized", 60, 1, 500, 2, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 62, 2, 500, 2, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 70, 3, 500, 2, "Constant Uninitialized"),
      new Card("Constant Uninitialized", 56, 4, 500, 2, "Constant Uninitialized"))
    val expectedEnemyBoard = List[Card](
      new Card("Shattered Sun Cleric", 63, 500, 1, 2, "EX1_019", cardInfo = dataBase.cardIDMap("EX1_019")))


    compareActualToExpected(new File(getClass.getResource("/debugsituations/RenounceDarkness.txt").getPath), expectedFriendlyHand, expectedFriendlyBoard, expectedEnemyHand, expectedEnemyBoard)
  }


  it should "Detect Frozen and Unfrozen Minions" in {
    val actualGameState = new GameState()

    val file = new File(getClass.getResource("/debugsituations/Freeze.txt").getPath)
    val lp = new LogParser(actualGameState)
    val logFileReader = new LogFileReader(lp, actualGameState)
    logFileReader.parseFile(file)

    val frozenCard = actualGameState.getCardByID(37).get
    val nonfrozenCard = actualGameState.getCardByID(58).get

    frozenCard.isFrozen shouldBe true
    nonfrozenCard.isFrozen shouldBe false
  }


  it should "Detect when a secret is destroyed" in {
    val actualGameState = new GameState()
    val file = new File(getClass.getResource("/debugsituations/SecretDestroyed.txt").getPath)
    val lp = new LogParser(actualGameState)
    val logFileReader = new LogFileReader(lp, actualGameState)
    logFileReader.parseFile(file)

    actualGameState.friendlyPlayer.secretsInPlay shouldBe 1
    actualGameState.enemyPlayer.secretsInPlay shouldBe 0
  }


  it should "Detect when a minion is damaged" in {
    val actualGameState = new GameState()
    val file = new File(getClass.getResource("/debugsituations/SecretDestroyed.txt").getPath)
    val lp = new LogParser(actualGameState)
    val logFileReader = new LogFileReader(lp, actualGameState)
    logFileReader.parseFile(file)

    val damagedCard = actualGameState.getCardByID(4).get
    val undamagedCard = actualGameState.getCardByID(25).get

    damagedCard.isDamaged shouldBe true
    undamagedCard.isDamaged shouldBe false
  }


  it should "Detect when a minion is stealthed" in {
    val actualGameState = new GameState()
    val file = new File(getClass.getResource("/debugsituations/Stealth.txt").getPath)
    val lp = new LogParser(actualGameState)
    val logFileReader = new LogFileReader(lp, actualGameState)
    logFileReader.parseFile(file)

    val stealthedMinion = actualGameState.getCardByID(26).get
    val nonstealthedMinion = actualGameState.getCardByID(16).get

    stealthedMinion.isStealthed shouldBe true
    nonstealthedMinion.isStealthed shouldBe false
  }

  it should "Detect when a minion is taunted" in {
    val actualGameState = new GameState()
    val file = new File(getClass.getResource("/debugsituations/Taunt.txt").getPath)
    val lp = new LogParser(actualGameState)
    val logFileReader = new LogFileReader(lp, actualGameState)
    logFileReader.parseFile(file)
    val nontauntedMinion = actualGameState.getCardByID(21).get
    nontauntedMinion.isTaunt.get shouldBe false
  }

  it should "Detect when a minion is deathrattle" in {
    val actualGameState = new GameState()
    val file = new File(getClass.getResource("/debugsituations/Deathrattle.txt").getPath)
    val lp = new LogParser(actualGameState)
    val logFileReader = new LogFileReader(lp, actualGameState)
    logFileReader.parseFile(file)
    val deathrattleMinion = actualGameState.getCardByID(12).get
    val deathrattleMinion2 = actualGameState.getCardByID(33).get
    deathrattleMinion.isDeathrattle.get shouldBe true
    deathrattleMinion2.isDeathrattle.isEmpty shouldBe true
    deathrattleMinion2.cardInfo.mechanics.get.contains("DEATHRATTLE") shouldBe true
  }

  it should "Detect when a player has a weapon equipped" in {
    val actualGameState = new GameState()
    val file = new File(getClass.getResource("/debugsituations/weapons.txt").getPath)
    val lp = new LogParser(actualGameState)
    val logFileReader = new LogFileReader(lp, actualGameState)
    logFileReader.parseFile(file)

    actualGameState.friendlyPlayer.isWeaponEquipped shouldBe true
    actualGameState.enemyPlayer.isWeaponEquipped shouldBe false
  }

  it should "Detect when a player has an active combo" in {
    val actualGameState = new GameState()
    val file = new File(getClass.getResource("/debugsituations/Combo.txt").getPath)
    val lp = new LogParser(actualGameState)
    val logFileReader = new LogFileReader(lp, actualGameState)
    logFileReader.parseFile(file)

    actualGameState.friendlyPlayer.isComboActive shouldBe true
    actualGameState.enemyPlayer.isComboActive shouldBe false
  }

  it should "Detect when a new Hero Power is assigned" in {
    val actualGameState = new GameState()
    val file = new File(getClass.getResource("/debugsituations/HeroPower.txt").getPath)
    val lp = new LogParser(actualGameState)
    val logFileReader = new LogFileReader(lp, actualGameState)
    logFileReader.parseFile(file)

    val friendlyHP = Card("Friendly Hero Power", 74, Constants.INT_UNINIT, Constants.INT_UNINIT, 1,"AT_132_PRIEST")
    val enemyHP = Card("Enemy Hero Power", 67, Constants.INT_UNINIT, Constants.INT_UNINIT, 2,"CS2_017")

    actualGameState.friendlyPlayer.heroPower shouldBe Some(friendlyHP)
    actualGameState.enemyPlayer.heroPower shouldBe Some(enemyHP)
  }


  it should "Detect when a cards attack value changes" in {
    val actualGameState = new GameState()
    val file = new File(getClass.getResource("/debugsituations/ChangeAttack.txt").getPath)
    val lp = new LogParser(actualGameState)
    val logFileReader = new LogFileReader(lp, actualGameState)
    logFileReader.parseFile(file)

    val changedAttMinion = actualGameState.getCardByID(19).get

    changedAttMinion.attack shouldBe Some(3)
  }








}
