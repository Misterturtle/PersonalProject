package LogReaderTests

import java.io._

import FileReaders.{LogFileReader, LogParser}
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

  def compareActualToExpected(file:File, friendlyHand: List[HSCard] = Nil, friendlyBoard: List[HSCard] = Nil, enemyHand: List[HSCard] = Nil, enemyBoard: List[HSCard] = Nil, friendlyWeaponValue: Int = 0, enemyWeaponValue: Int = 0): Unit = {

    val playerNumbers = new LogParser().GetPlayerNumbers(file)
    val actualGameState = new LogParser().ConstructGameState(file)
    val actualFriendlyHand = actualGameState.friendlyPlayer.hand
    val actualFriendlyBoard = actualGameState.friendlyPlayer.board
    val actualEnemyHand = actualGameState.enemyPlayer.hand
    val actualEnemyBoard = actualGameState.enemyPlayer.board
    val actualFriendlyWeaponValue = actualGameState.firstPlayer.weaponValue
    val actualEnemyWeaponValue = actualGameState.enemyPlayer.weaponValue

    actualFriendlyHand shouldEqual friendlyHand
    actualFriendlyBoard shouldEqual friendlyBoard
    actualEnemyHand shouldEqual enemyHand
    actualEnemyBoard shouldEqual enemyBoard
    actualFriendlyWeaponValue shouldEqual friendlyWeaponValue
    actualEnemyWeaponValue shouldEqual enemyWeaponValue
  }

  "LogFileReader" should "monitor changes in Hearthstone output log and print to actionLog" in {


    val writer = new PrintWriter(new FileWriter(hearthstoneLogFile))
    val reader = new BufferedReader(new FileReader(actionLogFile))

    new LogFileReader().poll()


    writer.println("[Zone] ZoneChangeList.ProcessChanges() - TRANSITIONING card [name=some id=some zone=PLAY zonePos=0 cardId=some player=1] to FRIENDLY PLAY (Hero)")
    writer.flush()

    writer.println("[Zone] ZoneChangeList.ProcessChanges() - processing index=55 change=powerTask=[power=[type=TAG_CHANGE entity=[id=55 cardId=some name=some] tag=ZONE_POSITION value=55] complete=False] entity=[name=Friendly Hand 1 id=1 zone=HAND zonePos=0 cardId=some player=1] srcZoneTag=INVALID srcPos= dstZoneTag=INVALID dstPos=1")
    writer.flush()

    writer.println("[Zone] ZoneChangeList.ProcessChanges() - processing index=55 change=powerTask=[power=[type=TAG_CHANGE entity=[id=55 cardId=some name=some] tag=ZONE_POSITION value=55] complete=False] entity=[name=Friendly Hand 2 id=2 zone=HAND zonePos=0 cardId=some player=1] srcZoneTag=INVALID srcPos= dstZoneTag=INVALID dstPos=2")
    writer.flush()

    writer.println("[Zone] ZoneChangeList.ProcessChanges() - processing index=55 change=powerTask=[power=[type=TAG_CHANGE entity=[id=55 cardId=some name=some] tag=ZONE_POSITION value=55] complete=False] entity=[name=Friendly Hand 3 id=3 zone=HAND zonePos=0 cardId=some player=1] srcZoneTag=INVALID srcPos= dstZoneTag=INVALID dstPos=3")
    writer.flush()

    writer.println("Some other text that isnt a HSAction")
    writer.flush()

    writer.println("some FULL_ENTITY - Updating [name=Friendly Board 1 id=11 zone=PLAY zonePos=1 some player=1 some")
    writer.flush()

    writer.println("some FULL_ENTITY - Updating [name=Friendly Board 2 id=12 zone=PLAY zonePos=2 some player=1 some")
    writer.flush()

    writer.println("[Zone] ZoneChangeList.ProcessChanges() - processing index=55 change=powerTask=[power=[type=TAG_CHANGE entity=[id=55 cardId= name=UNKNOWN ENTITY [cardType=INVALID]] tag=ZONE_POSITION value=55] complete=False] entity=[name=UNKNOWN ENTITY [cardType=INVALID] id=21 zone=HAND zonePos=0 cardId= player=2] srcZoneTag=INVALID srcPos= dstZoneTag=INVALID dstPos=1")
    writer.flush()

    writer.println("Someother text that isnt a heartstone action")
    writer.flush()

    writer.println("[Zone] ZoneChangeList.ProcessChanges() - processing index=55 change=powerTask=[power=[type=TAG_CHANGE entity=[id=55 cardId= name=UNKNOWN ENTITY [cardType=INVALID]] tag=ZONE_POSITION value=55] complete=False] entity=[name=UNKNOWN ENTITY [cardType=INVALID] id=22 zone=HAND zonePos=0 cardId= player=2] srcZoneTag=INVALID srcPos= dstZoneTag=INVALID dstPos=2")
    writer.flush()

    writer.println("[Zone] ZoneChangeList.ProcessChanges() - processing index=55 change=powerTask=[power=[type=TAG_CHANGE entity=[id=55 cardId= name=UNKNOWN ENTITY [cardType=INVALID]] tag=ZONE_POSITION value=55] complete=False] entity=[name=UNKNOWN ENTITY [cardType=INVALID] id=23 zone=HAND zonePos=0 cardId= player=2] srcZoneTag=INVALID srcPos= dstZoneTag=INVALID dstPos=3")
    writer.flush()

    writer.println("some FULL_ENTITY - Updating [name=Enemy Board 1 id=31 zone=PLAY zonePos=1 some player=2 some")
    writer.flush()

    writer.println("some FULL_ENTITY - Updating [name=Enemy Board 2 id=32 zone=PLAY zonePos=2 some player=2 some")
    writer.flush()

    Thread.sleep(1000)




    val actualActionLogStrings = Stream.continually(reader.readLine()).takeWhile(_ != null).toList
    val expectedActionLogStrings = List(
      "[Zone] ZoneChangeList.ProcessChanges() - TRANSITIONING card [name=some id=some zone=PLAY zonePos=0 cardId=some player=1] to FRIENDLY PLAY (Hero)",
      "[Zone] ZoneChangeList.ProcessChanges() - processing index=55 change=powerTask=[power=[type=TAG_CHANGE entity=[id=55 cardId=some name=some] tag=ZONE_POSITION value=55] complete=False] entity=[name=Friendly Hand 1 id=1 zone=HAND zonePos=0 cardId=some player=1] srcZoneTag=INVALID srcPos= dstZoneTag=INVALID dstPos=1",
      "[Zone] ZoneChangeList.ProcessChanges() - processing index=55 change=powerTask=[power=[type=TAG_CHANGE entity=[id=55 cardId=some name=some] tag=ZONE_POSITION value=55] complete=False] entity=[name=Friendly Hand 2 id=2 zone=HAND zonePos=0 cardId=some player=1] srcZoneTag=INVALID srcPos= dstZoneTag=INVALID dstPos=2",
      "[Zone] ZoneChangeList.ProcessChanges() - processing index=55 change=powerTask=[power=[type=TAG_CHANGE entity=[id=55 cardId=some name=some] tag=ZONE_POSITION value=55] complete=False] entity=[name=Friendly Hand 3 id=3 zone=HAND zonePos=0 cardId=some player=1] srcZoneTag=INVALID srcPos= dstZoneTag=INVALID dstPos=3",
      "some FULL_ENTITY - Updating [name=Friendly Board 1 id=11 zone=PLAY zonePos=1 some player=1 some",
      "some FULL_ENTITY - Updating [name=Friendly Board 2 id=12 zone=PLAY zonePos=2 some player=1 some",
      "[Zone] ZoneChangeList.ProcessChanges() - processing index=55 change=powerTask=[power=[type=TAG_CHANGE entity=[id=55 cardId= name=UNKNOWN ENTITY [cardType=INVALID]] tag=ZONE_POSITION value=55] complete=False] entity=[name=UNKNOWN ENTITY [cardType=INVALID] id=21 zone=HAND zonePos=0 cardId= player=2] srcZoneTag=INVALID srcPos= dstZoneTag=INVALID dstPos=1",
      "[Zone] ZoneChangeList.ProcessChanges() - processing index=55 change=powerTask=[power=[type=TAG_CHANGE entity=[id=55 cardId= name=UNKNOWN ENTITY [cardType=INVALID]] tag=ZONE_POSITION value=55] complete=False] entity=[name=UNKNOWN ENTITY [cardType=INVALID] id=22 zone=HAND zonePos=0 cardId= player=2] srcZoneTag=INVALID srcPos= dstZoneTag=INVALID dstPos=2",
      "[Zone] ZoneChangeList.ProcessChanges() - processing index=55 change=powerTask=[power=[type=TAG_CHANGE entity=[id=55 cardId= name=UNKNOWN ENTITY [cardType=INVALID]] tag=ZONE_POSITION value=55] complete=False] entity=[name=UNKNOWN ENTITY [cardType=INVALID] id=23 zone=HAND zonePos=0 cardId= player=2] srcZoneTag=INVALID srcPos= dstZoneTag=INVALID dstPos=3",
      "some FULL_ENTITY - Updating [name=Enemy Board 1 id=31 zone=PLAY zonePos=1 some player=2 some",
      "some FULL_ENTITY - Updating [name=Enemy Board 2 id=32 zone=PLAY zonePos=2 some player=2 some")

    actualActionLogStrings shouldEqual expectedActionLogStrings
  }

  "LogFileReader scenarios" should "detect and define player" in {

    val expectedFriendlyHand = List[HSCard](
      new Card("Equality", 67, 1, 500, 2),
      new Card("Leeroy Jenkins", 71, 2, 500, 2),
      new Card("Don Han'Cho", 79, 3, 500, 2),
      new Card("Blessed Champion", 53, 4, 500, 2))

    val expectedFriendlyBoard = List[HSCard](
      new Card("Wild Pyromancer", 80, 500, 1, 2))

    val expectedEnemyHand = List[HSCard](
      new Card("Constant Uninitialized", 43, 2, 500, 1),
      new Card("Constant Uninitialized", 4, 1, 500, 1),
      new Card("Constant Uninitialized", 48, 3, 500, 1),
      new Card("Constant Uninitialized", 17, 4, 500, 1))
    val expectedEnemyBoard = List[HSCard](
      new Card("Sapling", 87, 500, 1, 1),
      new Card("Sapling", 88, 500, 2, 1))
    compareActualToExpected(new File(getClass.getResource("/debugsituations/DefinePlayers.txt").getPath), expectedFriendlyHand, expectedFriendlyBoard, expectedEnemyHand, expectedEnemyBoard)
  }


  it should "Detect MulliganRedraw" in {

    val expectedFriendlyHand = List(
      new Card("Lay on Hands", 36, 1, Constants.INT_UNINIT, 2),
      new Card("Spellbreaker", 44, 2, Constants.INT_UNINIT, 2),
      new Card("Aldor Peacekeeper", 40, 3, Constants.INT_UNINIT, 2),
      new Card("Acidic Swamp Ooze", 58, 4, Constants.INT_UNINIT, 2),
      new Card("Solemn Vigil", 38, 5, Constants.INT_UNINIT, 2))
    val expectedEnemyHand = List(
      new Card(Constants.STRING_UNINIT, 21, 1, Constants.INT_UNINIT, 1),
      new Card(Constants.STRING_UNINIT, 4, 2, Constants.INT_UNINIT, 1),
      new Card(Constants.STRING_UNINIT, 10, 3, Constants.INT_UNINIT, 1),
      new Card(Constants.STRING_UNINIT, 25, 4, Constants.INT_UNINIT, 1),
      new Card(Constants.STRING_UNINIT, 32, 5, Constants.INT_UNINIT, 1))
    val expectedFriendlyBoard = List (new Card("Acolyte of Pain", 48, 500, 1, 2))


    compareActualToExpected(new File(getClass.getResource("/debugsituations/Mulligan.txt").getPath), expectedFriendlyHand, expectedFriendlyBoard, expectedEnemyHand)
  }

  it should "Detect CardPlayed" in {

    val expectedFriendlyHand = List(
      new Card("Lay on Hands", 36, 1, Constants.INT_UNINIT, 2),
      new Card("Spellbreaker", 44, 2, Constants.INT_UNINIT, 2),
      new Card("Aldor Peacekeeper", 40, 3, Constants.INT_UNINIT, 2),
      new Card("Acidic Swamp Ooze", 58, 4, Constants.INT_UNINIT, 2),
      new Card("Solemn Vigil", 38, 5, Constants.INT_UNINIT, 2))

    val expectedFriendlyBoard = List(
      new Card("Acolyte of Pain", 48, Constants.INT_UNINIT, 1, 2))

    val expectedEnemyHand = List(
      new Card(Constants.STRING_UNINIT, 21, 1, Constants.INT_UNINIT, 1),
      new Card(Constants.STRING_UNINIT, 4, 2, Constants.INT_UNINIT, 1),
      new Card(Constants.STRING_UNINIT, 10, 3, Constants.INT_UNINIT, 1),
      new Card(Constants.STRING_UNINIT, 25, 4, Constants.INT_UNINIT, 1),
      new Card(Constants.STRING_UNINIT, 32, 5, Constants.INT_UNINIT, 1))



    compareActualToExpected(new File(getClass.getResource("/debugsituations/Mulligan.txt").getPath), expectedFriendlyHand, expectedFriendlyBoard, expectedEnemyHand)
  }

  it should "Detect Card Death" in {

    val expectedFriendlyHand = List(
      new Card("Truesilver Champion", 28, 1, 500, 1),
      new Card("Wild Pyromancer", 15, 3, 500, 1),
      new Card("The Coin", 68, 4, 500, 1),
      new Card("Acolyte of Pain", 25, 2, 500, 1),
      new Card("Solemn Vigil", 12, 5, 500, 1),
      new Card("Eater of Secrets", 32, 6, 500, 1),
      new Card("Spellbreaker", 30, 7, 500, 1))

    val expectedEnemyHand = List(
      new Card("Constant Uninitialized", 39, 1, 500, 2),
      new Card("Constant Uninitialized", 59, 2, 500, 2),
      new Card("Constant Uninitialized", 34, 3, 500, 2),
      new Card("Constant Uninitialized", 37, 4, 500, 2))

    val expectedEnemyBoard = List(
      new Card("Stonetusk Boar", 60, 500, 1, 2),
      new Card("Healing Totem", 69, 500, 2, 2))

    compareActualToExpected(new File(getClass.getResource("/debugsituations/CardDeath.txt").getPath), expectedFriendlyHand, enemyHand = expectedEnemyHand, enemyBoard = expectedEnemyBoard)
  }

  it should "Detect friendly and enemy Hex" in {

    val expectedFriendlyHand = List(
      new Card("Fire Elemental", 8, 1, 500, 1),
      new Card("Coldlight Oracle", 15, 2, 500, 1),
      new Card("Azure Drake", 4, 3, 500, 1),
      new Card("Coldlight Oracle", 22, 4, 500, 1),
      new Card("Ancestral Knowledge", 5, 5, 500, 1),
      new Card("Loot Hoarder", 33, 6, 500, 1))

    val expectedFriendlyBoard = List(
      new Card("Transformed Friendly Minion", 70, 500, 2, 1),
      new Card("Flame Juggler", 10, 500, 1, 1))

    val expectedEnemyHand = List(
      new Card("Constant Uninitialized", 57, 1, 500, 2),
      new Card("Constant Uninitialized", 54, 2, 500, 2),
      new Card("Constant Uninitialized", 51, 3, 500, 2),
      new Card("Constant Uninitialized", 39, 4, 500, 2),
      new Card("Constant Uninitialized", 60, 5, 500, 2),
      new Card("Constant Uninitialized", 62, 6, 500, 2),
      new Card("Constant Uninitialized", 36, 7, 500, 2),
      new Card("Constant Uninitialized", 55, 8, 500, 2))

    compareActualToExpected(new File(getClass.getResource("/debugsituations/Hex.txt").getPath), expectedFriendlyHand, expectedFriendlyBoard,expectedEnemyHand)
  }

  it should "Detect friendly and enemy card return" in {

    val expectedFriendlyHand = List(
      new Card("Cult Master", 35, 1, 500, 2),
      new Card("The Coin", 68, 3, 500, 2),
      new Card("Novice Engineer", 36, 2, 500, 2),
      new Card("Cult Master", 51, 4, 500, 2),
      new Card("Fan of Knives", 44, 5, 500, 2))

    val expectedFriendlyBoard = List(
      new Card("Loot Hoarder", 42, 500, 1, 2))

    val expectedEnemyHand = List(
      new Card("Constant Uninitialized", 4, 1, 500, 1),
      new Card("Constant Uninitialized", 14, 2, 500, 1),

      //Loot Hoarder could possible have hand position 3 instead of 4.
      //Card with id=7 could possible have hand position 4 instead of 3.
      //Gave up trying to figure it out. Will return if other bugs show up.
      new Card("Constant Uninitialized", 7, 3, 500, 1),
      new Card("Loot Hoarder", 23, 4, 500, 1))

    compareActualToExpected(new File(getClass.getResource("/debugsituations/ShadowStep.txt").getPath), expectedFriendlyHand, expectedFriendlyBoard, expectedEnemyHand)
  }

  it should "Detect vanish" in{

    val expectedFriendlyHand  = List(
      new Card("Sprint", 48, 1, 500, 2),
      new Card("Gnomish Inventor", 58, 2, 500, 2),
      new Card("Coldlight Oracle", 35, 3, 500, 2),
      new Card("Shiv", 38, 4, 500, 2),
      new Card("Shiv", 53, 5, 500, 2),
      new Card("Coldlight Oracle", 39, 6, 500, 2),
      new Card("Sap", 36, 7, 500, 2),
      new Card("Fan of Knives", 45, 8, 500, 2),
      new Card("Acolyte of Pain", 37, 9, 500, 2),
      new Card("Gnomish Inventor", 56, 10, 500, 2))

    val expectedEnemyHand = List(
      new Card("Coldlight Oracle", 10, 1, 500, 1),
      new Card("Constant Uninitialized", 11, 2, 500, 1),
      new Card("Constant Uninitialized", 12, 3, 500, 1),
      new Card("Constant Uninitialized", 6, 4, 500, 1),
      new Card("Constant Uninitialized", 15, 5, 500, 1),
      new Card("Runic Egg", 14, 6, 500, 1),
      new Card("Bloodmage Thalnos", 20, 7, 500, 1),
      new Card("Gnomish Inventor", 4, 8, 500, 1),
      new Card("Loot Hoarder", 22, 9, 500, 1))

    val expectedEnemyBoard = List(new Card("Cult Master", 17, 500, 1, 1))

    compareActualToExpected(new File(getClass.getResource("/debugsituations/Vanish.txt").getPath), expectedFriendlyHand, enemyHand = expectedEnemyHand, enemyBoard = expectedEnemyBoard)
  }

  it should "Detect friendly and enemy minions being controlled" in {

    val expectedFriendlyHand = List(
      new Card("Cult Master", 4, 1, 500, 1),
      new Card("Gnomish Inventor", 5, 2, 500, 1),
      new Card("Coldlight Oracle", 27, 3, 500, 1),
      new Card("Azure Drake", 26, 4, 500, 1),
      new Card("Gnomish Inventor", 8, 5, 500, 1),
      new Card("Polluted Hoarder", 33, 6, 500, 1))

    val expectedEnemyHand = List(
      new Card("Constant Uninitialized", 61, 2, 500, 2),
      new Card("Constant Uninitialized", 43, 1, 500, 2),
      new Card("Constant Uninitialized", 62, 3, 500, 2),
      new Card("Constant Uninitialized", 60, 4, 500, 2),
      new Card("Constant Uninitialized", 37, 5, 500, 2),
      new Card("Constant Uninitialized", 36, 6, 500, 2),
      new Card("Constant Uninitialized", 34, 7, 500, 2))

    val expectedEnemyBoard = List(
      new Card("Gnomish Inventor", 51, 500, 2, 2),
      new Card("Cabal Shadow Priest", 55, 500, 1, 2),
      new Card("Novice Engineer", 25, 500, 3, 2))

    compareActualToExpected(new File(getClass.getResource("/debugsituations/MinionsControlled.txt").getPath), expectedFriendlyHand, enemyHand = expectedEnemyHand, enemyBoard = expectedEnemyBoard)
  }

  it should "Detect weapon played" in {
    val expectedFriendlyHand = List(
      new Card("Equality", 48, 1, 500, 2),
      new Card("The Coin", 68, 4, 500, 2),
      new Card("Eater of Secrets", 49, 2, 500, 2),
      new Card("Tirion Fordring", 40, 3, 500, 2),
      new Card("Solemn Vigil", 54, 5, 500, 2),
      new Card("Equality", 46, 6, 500, 2),
      new Card("Acidic Swamp Ooze", 63, 7, 500, 2),
      new Card("Aldor Peacekeeper", 59, 8, 500, 2))

    val expectedEnemyHand = List(
      new Card("Constant Uninitialized", 27, 1, 500, 1),
      new Card("Constant Uninitialized", 4, 2, 500, 1),
      new Card("Constant Uninitialized", 16, 3, 500, 1),
      new Card("Constant Uninitialized", 22, 4, 500, 1),
      new Card("Constant Uninitialized", 23, 5, 500, 1),
      new Card("Constant Uninitialized", 10, 6, 500, 1),
      new Card("Constant Uninitialized", 5, 7, 500, 1))

    val expectedFriendlyWeaponValue = 0
    val expectedEnemyWeaponValue = 5

    compareActualToExpected(new File(getClass.getResource("/debugsituations/WeaponsEquipped.txt").getPath), expectedFriendlyHand, enemyHand = expectedEnemyHand, friendlyWeaponValue = expectedFriendlyWeaponValue, enemyWeaponValue = expectedEnemyWeaponValue)
  }

  it should "Detect weapon destroyed" in {
    val actualGameState = new LogParser().ConstructGameState(new File(getClass.getResource("/debugsituations/WeaponsDestroyed.txt").getPath))
    val actualFriendlyWeaponValue = actualGameState.friendlyPlayer.weaponValue
    val actualEnemyWeaponValue = actualGameState.enemyPlayer.weaponValue

    actualFriendlyWeaponValue shouldEqual 0
    actualEnemyWeaponValue shouldEqual 0
  }

  it should "Detect when a game is over" in {
    compareActualToExpected(new File(getClass.getResource("/debugsituations/Concede.txt").getPath))
  }

  it should "Detect an overdraw" in {

    val expectedFriendlyHand = List(
      new Card("Brann Bronzebeard", 60, 1, 500, 2),
      new Card("Solemn Vigil", 57, 2, 500, 2),
      new Card("The Coin", 68, 3, 500, 2),
      new Card("Hammer of Wrath", 55, 4, 500, 2),
      new Card("Loot Hoarder", 47, 5, 500, 2),
      new Card("Blessed Champion", 48, 6, 500, 2),
      new Card("Equality", 34, 7, 500, 2),
      new Card("Lay on Hands", 62, 8, 500, 2),
      new Card("Tirion Fordring", 44, 9, 500, 2),
      new Card("Wild Pyromancer", 51, 10, 500, 2))

    val expectedFriendlyBoard = List(
      new Card("Aldor Peacekeeper", 38, 500, 2, 2),
      new Card("Silver Hand Recruit", 70, 500, 3, 2),
      new Card("Acolyte of Pain", 63, 500, 1, 2))

    val expectedEnemyHand = List(
      new Card("Constant Uninitialized", 27, 1, 500, 1),
      new Card("Constant Uninitialized", 15, 2, 500, 1),
      new Card("Constant Uninitialized", 4, 3, 500, 1),
      new Card("Constant Uninitialized", 33, 4, 500, 1),
      new Card("Constant Uninitialized", 12, 5, 500, 1),
      new Card("Constant Uninitialized", 26, 6, 500, 1),
      new Card("Constant Uninitialized", 10, 7, 500, 1),
      new Card("Constant Uninitialized", 6, 8, 500, 1),
      new Card("Constant Uninitialized", 23, 9, 500, 1),
      new Card("Constant Uninitialized", 9, 10, 500, 1))

    val expectedEnemyBoard = List(
      new Card("Dread Corsair", 14, 500, 1, 1))

    compareActualToExpected(new File(getClass.getResource("/debugsituations/OverDraw.txt").getPath), expectedFriendlyHand, expectedFriendlyBoard, expectedEnemyHand, expectedEnemyBoard)
  }

  it should "Detect Enemy Kazakus's custom spell creation" in {

    val expectedFriendlyHand = List(
      new Card("Solemn Vigil", 62, 1, 500, 2),
      new Card("Eater of Secrets", 47, 2, 500, 2),
      new Card("Blessed Champion", 55, 3, 500, 2),
      new Card("Solemn Vigil", 63, 4, 500, 2),
      new Card("Spellbreaker", 53, 5, 500, 2),
      new Card("Aldor Peacekeeper", 60, 6, 500, 2),
      new Card("Acolyte of Pain", 52, 7, 500, 2),
      new Card("Aldor Peacekeeper", 59, 8, 500, 2))


    val expectedEnemyHand = List(
      new Card("Constant Uninitialized", 20, 1, 500, 1),
      new Card("Constant Uninitialized", 13, 2, 500, 1),
      new Card("Constant Uninitialized", 31, 3, 500, 1),
      new Card("Constant Uninitialized", 68, 4, 500, 1),
      new Card("Constant Uninitialized", 24, 5, 500, 1),
      new Card("Constant Uninitialized", 6, 6, 500, 1),
      new Card("Constant Uninitialized", 4, 7, 500, 1),
      new Card("Constant Uninitialized", 78, 8, 500, 1),
      new Card("Constant Uninitialized", 88, 9, 500, 1))

    val expectedEnemyBoard = List(
      new Card("Brann Bronzebeard", 12, 500, 2, 1),
      new Card("Kazakus", 5, 500, 1, 1))

    compareActualToExpected(new File(getClass.getResource("/debugsituations/EnemyCustomSpell.txt").getPath), expectedFriendlyHand, Nil, expectedEnemyHand, expectedEnemyBoard)

  }

  it should "Detect Friendly Kazakus's custom spell creation" in {

    val expectedFriendlyHand = List(
      new Card("Kabal Courier", 23, 1, 500, 1),
      new Card("Polymorph", 24, 2, 500, 1),
      new Card("Arcane Blast", 27, 3, 500, 1),
      new Card("Ice Lance", 18, 4, 500, 1),
      new Card("Frostbolt", 20, 5, 500, 1),
      new Card("Kazakus Potion", 87, 6, 500, 1))

    val expectedFriendlyBoard = List(
      new Card("Kazakus", 16, 500, 1, 1))

    val expectedEnemyHand = List(
      new Card("Constant Uninitialized", 56, 1, 500, 2),
      new Card("Constant Uninitialized", 43, 2, 500, 2),
      new Card("Constant Uninitialized", 51, 3, 500, 2),
      new Card("Constant Uninitialized", 48, 4, 500, 2),
      new Card("Constant Uninitialized", 35, 5, 500, 2),
      new Card("Constant Uninitialized", 41, 6, 500, 2),
      new Card("Constant Uninitialized", 47, 7, 500, 2))

    val expectedEnemyBoard = List(
      new Card("Loot Hoarder", 46, 500, 1, 2))

    compareActualToExpected(new File(getClass.getResource("/debugsituations/FriendlyCustomSpell.txt").getPath), expectedFriendlyHand, expectedFriendlyBoard, expectedEnemyHand, expectedEnemyBoard)

  }

  it should "Detect Deck to board directly" in {

    val expectedFriendlyHand = List(
      new Card("Call of the Wild", 37, 1, 500, 2),
      new Card("Call of the Wild", 38, 2, 500, 2),
      new Card("Houndmaster", 52, 3, 500, 2))

    val expectedFriendlyBoard = List(
      new Card("Savannah Highmane", 34, 500, 1, 2),
      new Card("Desert Camel", 59, 500, 2, 2),
      new Card("Injured Kvaldir", 60, 500, 3, 2),
      new Card("Injured Kvaldir", 54, 500, 4, 2))

    val expectedEnemyHand = List(
      new Card("Constant Uninitialized", 12, 2, 500, 1),
      new Card("Constant Uninitialized", 17, 1, 500, 1),
      new Card("Constant Uninitialized", 68, 3, 500, 1),
      new Card("Constant Uninitialized", 33, 4, 500, 1),
      new Card("Constant Uninitialized", 14, 5, 500, 1),
      new Card("Constant Uninitialized", 22, 6, 500, 1))

    val expectedEnemyBoard = List(
      new Card("Knife Juggler", 28, 500, 1, 1),
      new Card("Mind Control Tech", 9, 500, 2, 1),
      new Card("Argent Squire", 31, 500, 3, 1),
      new Card("Desert Camel", 6, 500, 4, 1))

    compareActualToExpected(new File(getClass.getResource("/debugsituations/DesertCamel.txt").getPath), expectedFriendlyHand, expectedFriendlyBoard, expectedEnemyHand, expectedEnemyBoard)
  }

  it should "Detect Joust Draw" in {

    val expectedFriendlyHand = List(
      new Card("Call of the Wild", 37, 1, 500, 2),
      new Card("Call of the Wild", 38, 2, 500, 2),
      new Card("Deadly Shot", 36, 3, 500, 2),
      new Card("Desert Camel", 39, 4, 500, 2),
      new Card("Infested Wolf", 55, 5, 500, 2))

    val expectedFriendlyBoard = List(
      new Card("Huge Toad", 63, 500, 3, 2),
      new Card("Hyena", 93, 500, 2, 2),
      new Card("Hyena", 94, 500, 1, 2),
      new Card("King's Elekk", 35, 500, 4, 2))

    val expectedEnemyHand = List(
      new Card("Constant Uninitialized", 12, 2, 500, 1),
      new Card("Constant Uninitialized", 17, 1, 500, 1),
      new Card("Constant Uninitialized", 68, 3, 500, 1),
      new Card("Constant Uninitialized", 14, 4, 500, 1),
      new Card("Constant Uninitialized", 15, 5, 500, 1),
      new Card("Constant Uninitialized", 26, 6, 500, 1))

    val expectedEnemyBoard = List(
      new Card("Emperor Thaurissan", 18, 500, 1, 1))

    compareActualToExpected(new File(getClass.getResource("/debugsituations/KingElekkDraw.txt").getPath), expectedFriendlyHand, expectedFriendlyBoard, expectedEnemyHand, expectedEnemyBoard, 3)
  }

  it should "Detect discard" in {

    val expectedFriendlyHand = List(
      new Card("Curse of Rafaam", 10, 1, 500, 1),
      new Card("Coldlight Oracle", 22, 2, 500, 1))

    val expectedEnemyHand = List(
      new Card("Constant Uninitialized", 36, 1, 500, 2),
      new Card("Constant Uninitialized", 68, 3, 500, 2),
      new Card("Constant Uninitialized", 53, 2, 500, 2),
      new Card("Constant Uninitialized", 48, 4, 500, 2))

    compareActualToExpected(new File(getClass.getResource("/debugsituations/Discard.txt").getPath), expectedFriendlyHand, Nil, expectedEnemyHand)
  }

  it should "Detect card creation directly to deck (Gang up scenario)" in{

    val expectedFriendlyHand = List(
      new Card("Gnomish Inventor", 62, 1, 500, 2),
      new Card("Coldlight Oracle", 43, 2, 500, 2),
      new Card("Ragnaros the Firelord", 60, 3, 500, 2),
      new Card("Sprint", 55, 4, 500, 2),
      new Card("Polluted Hoarder", 35, 5, 500, 2),
      new Card("Fan of Knives", 52, 6, 500, 2),
      new Card("Gang Up", 41, 7, 500, 2),
      new Card("Assassin's Blade", 39, 8, 500, 2),
      new Card("Acolyte of Pain", 70, 9, 500, 2))

    val expectedFriendlyBoard = List(
      new Card("Acolyte of Pain", 56, 500, 1, 2),
      new Card("Undercity Valiant", 48, 500, 2, 2))

    val expectedEnemyHand = List(
      new Card("Constant Uninitialized", 7, 1, 500, 1),
      new Card("Constant Uninitialized", 24, 2, 500, 1),
      new Card("Constant Uninitialized", 26, 3, 500, 1),
      new Card("Constant Uninitialized", 17, 4, 500, 1),
      new Card("Constant Uninitialized", 10, 5, 500, 1),
      new Card("Constant Uninitialized", 29, 6, 500, 1),
      new Card("Constant Uninitialized", 74, 7, 500, 1),
      new Card("Constant Uninitialized", 31, 8, 500, 1))

    val expectedEnemyBoard = List(new Card("Flame Juggler", 19, 500, 1, 1))

    compareActualToExpected(new File(getClass.getResource("/debugsituations/GangUp.txt").getPath), expectedFriendlyHand, expectedFriendlyBoard, expectedEnemyHand, expectedEnemyBoard, 3)
  }

  it should "Detect Jaraxxus" in {
    val expectedFriendlyHand = List(
      new Card("Arcane Intellect", 21, 1, 500, 1),
      new Card("The Coin", 68, 2, 500, 1),
      new Card("Arcane Explosion", 30, 3, 500, 1),
      new Card("Arcane Intellect", 26, 4, 500, 1))

    val expectedFriendlyBoard = List(
      new Card("Oasis Snapjaw", 28, 500, 1, 1),
      new Card("River Crocolisk", 12, 500, 2, 1),
      new Card("Raid Leader", 11, 500, 3, 1))

    val expectedEnemyHand = List(
      new Card("Constant Uninitialized", 37, 1, 500, 2),
      new Card("Constant Uninitialized", 40, 2, 500, 2),
      new Card("Constant Uninitialized", 47, 3, 500, 2),
      new Card("Constant Uninitialized", 45, 4, 500, 2),
      new Card("Constant Uninitialized", 54, 5, 500, 2),
      new Card("Constant Uninitialized", 50, 6, 500, 2),
      new Card("Constant Uninitialized", 59, 7, 500, 2),
      new Card("Constant Uninitialized", 44, 8, 500, 2))

    val expectedEnemyBoard = List(new Card("Blood Imp", 72, 500, 1, 2))

    compareActualToExpected(new File(getClass.getResource("/debugsituations/Jaraxxus.txt").getPath), expectedFriendlyHand, expectedFriendlyBoard, expectedEnemyHand, expectedEnemyBoard, 0, 3)


  }

  it should "Detect Evolve " in {

    val expectedFriendlyHand = List(
      new Card("The Coin", 68, 2, 500, 2),
      new Card("Arcane Missiles", 39, 1, 500, 2),
      new Card("Sen'jin Shieldmasta", 43, 3, 500, 2),
      new Card("Nightblade", 54, 4, 500, 2),
      new Card("River Crocolisk", 63, 5, 500, 2),
      new Card("Oasis Snapjaw", 52, 6, 500, 2))

    val expectedFriendlyBoard = List(
      new Card("Novice Engineer", 50, 500, 1, 2))

    val expectedEnemyHand = List(
      new Card("Constant Uninitialized", 28, 1, 500, 1),
      new Card("Constant Uninitialized", 4, 2, 500, 1),
      new Card("Constant Uninitialized", 8, 3, 500, 1),
      new Card("Constant Uninitialized", 19, 4, 500, 1))

    val expectedEnemyBoard = List(
      new Card("Transformed Enemy Minion", 70, 500, 2, 1),
      new Card("Damaged Golem", 72, 500, 1, 1))

    compareActualToExpected(new File(getClass.getResource("/debugsituations/Evolve.txt").getPath), expectedFriendlyHand, expectedFriendlyBoard, expectedEnemyHand, expectedEnemyBoard)
  }

  it should "Detect Renounce Darkness" in {
    val expectedFriendlyHand = List(
      new Card("Arcane Intellect", 13, 1, 500, 1),
      new Card("Polymorph", 33, 2, 500, 1),
      new Card("Boulderfist Ogre", 6, 3, 500, 1),
      new Card("River Crocolisk", 5, 4, 500, 1),
      new Card("Fireball", 8, 5, 500, 1),
      new Card("Murloc Raider", 24, 6, 500, 1))

    val expectedFriendlyBoard = List(
      new Card("Novice Engineer", 23, 500, 1, 1))

    val expectedEnemyHand = List(
      new Card("Constant Uninitialized", 60, 1, 500, 2),
      new Card("Constant Uninitialized", 62, 2, 500, 2),
      new Card("Constant Uninitialized", 70, 3, 500, 2),
      new Card("Constant Uninitialized", 56, 4, 500, 2))

    val expectedEnemyBoard = List(
      new Card("Shattered Sun Cleric", 63, 500, 1, 2))

    compareActualToExpected(new File(getClass.getResource("/debugsituations/RenounceDarkness.txt").getPath), expectedFriendlyHand, expectedFriendlyBoard, expectedEnemyHand, expectedEnemyBoard)
  }










}
