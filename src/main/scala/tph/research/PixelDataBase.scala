package tph.research

import scala.collection.mutable

/**
  * Created by RC on 8/25/2016.
  */
object PixelDataBase {


  val mainMenu = new Menu()
  val PLAY = "play"
  //MY_COLLECTION located under playMenu
  val OPEN_PACKS = "openPacks"
  val QUEST_LOG = "questLog"
  val SHOP = "shop"
  mainMenu.AddClickLocation(PLAY, new ClickLocation(657, 254))
  mainMenu.AddClickLocation("soloAdventures", new ClickLocation(659, 304))
  mainMenu.AddClickLocation("theArena", new ClickLocation(658, 355))
  mainMenu.AddClickLocation("tavernBrawl", new ClickLocation(655, 413))
  mainMenu.AddClickLocation(MY_COLLECTION, new ClickLocation(720, 646))
  mainMenu.AddClickLocation(OPEN_PACKS, new ClickLocation(535, 634))
  mainMenu.AddClickLocation(QUEST_LOG, new ClickLocation(369, 664))
  mainMenu.AddClickLocation(SHOP, new ClickLocation(246, 673))
  mainMenu.AddClickLocation("optionMenu", new ClickLocation(1282, 744))
  mainMenu.AddClickLocation("friendsList", new ClickLocation(39, 743))


  val playMenu = new Menu()
  val CASUAL = "casual"
  val RANKED = "ranked"
  //PLAY located under MainMenu
  val BACK_BUTTON = "backButton"
  val NEXT_PAGE = "nextPage"
  val PREVIOUS_PAGE = "previousPage"
  val MY_COLLECTION = "myCollection"
  val FIRST_DECK = "firstDeck"
  val SECOND_DECK = "secondDeck"
  val THIRD_DECK = "thirdDeck"
  val FOURTH_DECK = "fourthDeck"
  val FIFTH_DECK = "fifthDeck"
  val SIXTH_DECK = "sixthDeck"
  val SEVENTH_DECK = "seventhDeck"
  val EIGHTH_DECK = "eighthDeck"
  val NINTH_DECK = "ninthDeck"
  playMenu.AddClickLocation(CASUAL, new ClickLocation(884, 171))
  playMenu.AddClickLocation(RANKED, new ClickLocation(1019, 160))
  playMenu.AddClickLocation("gameModeToggle", new ClickLocation(959, 71))
  playMenu.AddClickLocation(PLAY, new ClickLocation(951, 629))
  playMenu.AddClickLocation(BACK_BUTTON, new ClickLocation(1074, 704))
  playMenu.AddClickLocation(NEXT_PAGE, new ClickLocation(769, 388))
  playMenu.AddClickLocation(PREVIOUS_PAGE, new ClickLocation(202, 383))
  playMenu.AddClickLocation(MY_COLLECTION, new ClickLocation(499, 696))
  playMenu.AddClickLocation(FIRST_DECK, new ClickLocation(338, 229))
  playMenu.AddClickLocation(SECOND_DECK, new ClickLocation(502, 225))
  playMenu.AddClickLocation(THIRD_DECK, new ClickLocation(660, 225))
  playMenu.AddClickLocation(FOURTH_DECK, new ClickLocation(341, 376))
  playMenu.AddClickLocation(FIFTH_DECK, new ClickLocation(494, 383))
  playMenu.AddClickLocation(SIXTH_DECK, new ClickLocation(664, 375))
  playMenu.AddClickLocation(SEVENTH_DECK, new ClickLocation(331, 527))
  playMenu.AddClickLocation(EIGHTH_DECK, new ClickLocation(500, 529))
  playMenu.AddClickLocation(NINTH_DECK, new ClickLocation(665, 528))
  playMenu.AddClickLocation("friendsList", new ClickLocation(39, 743))


  val arenaMenu = new Menu()


  val shopMenu = new Menu()
  shopMenu.AddClickLocation("whisperOfTheOldGods", new ClickLocation(358, 186))
  shopMenu.AddClickLocation("classic", new ClickLocation(360, 366))
  shopMenu.AddClickLocation("theGrandTournament", new ClickLocation(354, 541))
  shopMenu.AddClickLocation("buyWithGoldButton", new ClickLocation(967, 550))
  shopMenu.AddClickLocation("exit", new ClickLocation(128, 416))


  val collectionMenu = new Menu()

  val SEARCH_BAR = "searchBar"
  val CRAFTING_BUTTON = "craftingButton"
  //BACK_BUTTON located in playMenu
  val FIRST_CARD = "firstCard"
  val DISENCHANT = "disenchant"
  val ENCHANT = "enchant"
  val DECK_NAME = "deckName"
  val CONVERT_DECK_BUTTON = "convertDeckButton"
  val CONFIRM_DELETE_DECK = "confirmDeleteDeck"
  //FIRST_DECK through NINTH_DECK located in playMenu
  val SCROLL_DOWN = "scrollDown"
  val SCROLL_UP = "scrollUp"
  val DELETE_DECK_ONE = "deleteDeckOne"
  val DELETE_DECK_TWO = "deleteDeckTwo"
  val DELETE_DECK_THREE = "deleteDeckThree"
  val DELETE_DECK_FOUR = "deleteDeckThree"
  val DELETE_DECK_FIVE = "deleteDeckFive"
  val DELETE_DECK_SIX = "deleteDeckSix"
  val DELETE_DECK_SEVEN = "deleteDeckSeven"
  val DELETE_DECK_EIGHT = "deleteDeckEight"
  val DELETE_DECK_NINE = "deleteDeckNine"
  val WARRIOR = "warrior"
  val ROGUE = "rogue"
  val PALADIN = "paladin"
  val MAGE = "mage"
  val WARLOCK = "warlock"
  val PRIEST = "priest"
  val HUNTER = "hunter"
  val SHAMAN = "shaman"
  val DRUID = "druid"
  val CHOOSE_HERO = "chooseHero"
  val CHOOSE_CREATE_DECK = "chooseCreateDeck"
  collectionMenu.AddClickLocation("searchBar", new ClickLocation(656, 699))
  collectionMenu.AddClickLocation("craftingButton", new ClickLocation(656, 699))
  collectionMenu.AddClickLocation("backButton", new ClickLocation(1069, 708))

  collectionMenu.AddClickLocation("firstCard", new ClickLocation(282, 258))
  collectionMenu.AddClickLocation("disenchant", new ClickLocation(545, 642))
  collectionMenu.AddClickLocation("enchant", new ClickLocation(703, 641))

  collectionMenu.AddClickLocation("deckName", new ClickLocation(938, 78))
  collectionMenu.AddClickLocation("convertDeckButton", new ClickLocation(1001, 123))
  collectionMenu.AddClickLocation("confirmDeleteDeck", new ClickLocation(574, 444))

  collectionMenu.AddClickLocation("firstDeck", new ClickLocation(1008, 126))
  collectionMenu.AddClickLocation("secondDeck", new ClickLocation(1002, 192))
  collectionMenu.AddClickLocation("thirdDeck", new ClickLocation(1006, 260))
  collectionMenu.AddClickLocation("forthDeck", new ClickLocation(1004, 334))
  collectionMenu.AddClickLocation("fifthDeck", new ClickLocation(1004, 401))
  collectionMenu.AddClickLocation("sixthDeck", new ClickLocation(1006, 468))
  collectionMenu.AddClickLocation("seventhDeck", new ClickLocation(1004, 532))
  collectionMenu.AddClickLocation("eigthDeck", new ClickLocation(1000, 611))
  collectionMenu.AddClickLocation("ninthDeck", new ClickLocation(1009, 668))
  collectionMenu.AddClickLocation("scrollDownDecks", new ClickLocation(1122, 620))
  collectionMenu.AddClickLocation("scrollUpDecks", new ClickLocation(1122, 66))

  collectionMenu.AddClickLocation("deleteDeckOne", new ClickLocation(1082, 106))
  collectionMenu.AddClickLocation("deleteDeckTwo", new ClickLocation(1081, 177))
  collectionMenu.AddClickLocation("deleteDeckThree", new ClickLocation(1079, 248))
  collectionMenu.AddClickLocation("deleteDeckFour", new ClickLocation(1078, 316))
  collectionMenu.AddClickLocation("deleteDeckFive", new ClickLocation(1078, 387))
  collectionMenu.AddClickLocation("deleteDeckSix", new ClickLocation(1078, 455))
  collectionMenu.AddClickLocation("deleteDeckSeven", new ClickLocation(1078, 525))
  collectionMenu.AddClickLocation("deleteDeckEight", new ClickLocation(1078, 595))
  collectionMenu.AddClickLocation("deleteDeckNine", new ClickLocation(1080, 666))

  collectionMenu.AddClickLocation("warrior", new ClickLocation(338, 229))
  collectionMenu.AddClickLocation("shaman", new ClickLocation(502, 225))
  collectionMenu.AddClickLocation("rogue", new ClickLocation(660, 225))
  collectionMenu.AddClickLocation("paladin", new ClickLocation(341, 376))
  collectionMenu.AddClickLocation("hunter", new ClickLocation(494, 383))
  collectionMenu.AddClickLocation("druid", new ClickLocation(664, 375))
  collectionMenu.AddClickLocation("warlock", new ClickLocation(331, 527))
  collectionMenu.AddClickLocation("mage", new ClickLocation(500, 529))
  collectionMenu.AddClickLocation("priest", new ClickLocation(665, 528))
  collectionMenu.AddClickLocation("chooseHero", new ClickLocation(951, 629))
  collectionMenu.AddClickLocation("chooseCreateDeck", new ClickLocation(811, 633))


  val questMenu = new Menu()
  val QUEST_ONE = "questOne"
  val QUEST_TWO = "questTwo"
  val QUEST_THREE = "questThree"
  //BACK located in PlayMenu
  questMenu.AddClickLocation(QUEST_ONE, new ClickLocation(559, 536))
  questMenu.AddClickLocation(QUEST_TWO, new ClickLocation(710, 537))
  questMenu.AddClickLocation(QUEST_THREE, new ClickLocation(866, 535))
  questMenu.AddClickLocation(BACK_BUTTON, new ClickLocation(158, 412))


  val enchantMenu = new Menu()
  //All defined in CollectionMenu
  enchantMenu.AddClickLocation("firstCard", new ClickLocation(282, 258))
  enchantMenu.AddClickLocation("disenchant", new ClickLocation(545, 642))
  enchantMenu.AddClickLocation("enchant", new ClickLocation(703, 641))
  enchantMenu.AddClickLocation("searchBar", new ClickLocation(656, 699))
  enchantMenu.AddClickLocation("doneButton", new ClickLocation(1069, 708))

  val openPacksMenu = new Menu()


  val inGame = new Menu()
  //Constant Options
  inGame.AddClickLocation("endTurn", new ClickLocation(1058, 359))
  inGame.AddClickLocation("heroPower", new ClickLocation(777, 585))
  inGame.AddClickLocation("myFace", new ClickLocation(663, 582))
  inGame.AddClickLocation("hisFace", new ClickLocation(658, 164))
  inGame.AddClickLocation("gameOptions", new ClickLocation(1276, 743))
  inGame.AddClickLocation("concedeButton", new ClickLocation(658, 296))


  //Mulligan Options
  inGame.AddClickLocation("mulliganOneOfThree", new ClickLocation(425, 380))
  inGame.AddClickLocation("mulliganTwoOfThree", new ClickLocation(666, 372))
  inGame.AddClickLocation("mulliganThreeOfThree", new ClickLocation(899, 373))
  inGame.AddClickLocation("mulliganConfirm", new ClickLocation(659, 612))

  inGame.AddClickLocation("mulliganOneOfFour", new ClickLocation(400, 380))
  inGame.AddClickLocation("mulliganTwoOfFour", new ClickLocation(576, 370))
  inGame.AddClickLocation("mulliganThreeOfFour", new ClickLocation(745, 375))
  inGame.AddClickLocation("mulliganFourOfFour", new ClickLocation(918, 377))
  inGame.AddClickLocation("mulliganConfirm", new ClickLocation(658, 610))


  //My Hand Options

  inGame.AddClickLocation("cardOneOfOne", new ClickLocation(633, 704))

  inGame.AddClickLocation("cardOneOfTwo", new ClickLocation(578, 713))
  inGame.AddClickLocation("cardTwoOfTwo", new ClickLocation(671, 708))

  inGame.AddClickLocation("cardOneOfThree", new ClickLocation(536, 721))
  inGame.AddClickLocation("cardTwoOfThree", new ClickLocation(634, 706))
  inGame.AddClickLocation("cardThreeOfThree", new ClickLocation(727, 705))

  inGame.AddClickLocation("cardOneOfFour", new ClickLocation(497, 726))
  inGame.AddClickLocation("cardTwoOfFour", new ClickLocation(580, 719))
  inGame.AddClickLocation("cardThreeOfFour", new ClickLocation(671, 716))
  inGame.AddClickLocation("cardFourOfFour", new ClickLocation(764, 717))

  inGame.AddClickLocation("cardOneOfFive", new ClickLocation(468, 733))
  inGame.AddClickLocation("cardTwoOfFive", new ClickLocation(548, 726))
  inGame.AddClickLocation("cardThreeOfFive", new ClickLocation(623, 724))
  inGame.AddClickLocation("cardFourOfFive", new ClickLocation(703, 709))
  inGame.AddClickLocation("cardFiveOfFive", new ClickLocation(771, 720))

  inGame.AddClickLocation("cardOneOfSix", new ClickLocation(457, 740))
  inGame.AddClickLocation("cardTwoOfSix", new ClickLocation(520, 712))
  inGame.AddClickLocation("cardThreeOfSix", new ClickLocation(583, 695))
  inGame.AddClickLocation("cardFourOfSix", new ClickLocation(650, 681))
  inGame.AddClickLocation("cardFiveOfSix", new ClickLocation(717, 700))
  inGame.AddClickLocation("cardSixOfSix", new ClickLocation(800, 713))

  inGame.AddClickLocation("cardOneOfSeven", new ClickLocation(453, 734))
  inGame.AddClickLocation("cardTwoOfSeven", new ClickLocation(503, 722))
  inGame.AddClickLocation("cardThreeOfSeven", new ClickLocation(552, 705))
  inGame.AddClickLocation("cardFourOfSeven", new ClickLocation(614, 696))
  inGame.AddClickLocation("cardFiveOfSeven", new ClickLocation(663, 695))
  inGame.AddClickLocation("cardSixOfSeven", new ClickLocation(720, 696))
  inGame.AddClickLocation("cardSevenOfSeven", new ClickLocation(803, 722))

  inGame.AddClickLocation("cardOneOfEight", new ClickLocation(443, 737))
  inGame.AddClickLocation("cardTwoOfEight", new ClickLocation(491, 707))
  inGame.AddClickLocation("cardThreeOfEight", new ClickLocation(539, 689))
  inGame.AddClickLocation("cardFourOfEight", new ClickLocation(590, 682))
  inGame.AddClickLocation("cardFiveOfEight", new ClickLocation(639, 674))
  inGame.AddClickLocation("cardSixOfEight", new ClickLocation(691, 676))
  inGame.AddClickLocation("cardSevenOfEight", new ClickLocation(745, 685))
  inGame.AddClickLocation("cardEightOfEight", new ClickLocation(802, 713))

  inGame.AddClickLocation("cardOneOfNine", new ClickLocation(434, 740))
  inGame.AddClickLocation("cardTwoOfNine", new ClickLocation(483, 719))
  inGame.AddClickLocation("cardThreeOfNine", new ClickLocation(522, 697))
  inGame.AddClickLocation("cardFourOfNine", new ClickLocation(559, 687))
  inGame.AddClickLocation("cardFiveOfNine", new ClickLocation(598, 683))
  inGame.AddClickLocation("cardSixOfNine", new ClickLocation(644, 679))
  inGame.AddClickLocation("cardSevenOfNine", new ClickLocation(697, 684))
  inGame.AddClickLocation("cardEightOfNine", new ClickLocation(738, 703))
  inGame.AddClickLocation("cardNineOfNine", new ClickLocation(809, 719))

  inGame.AddClickLocation("cardOneOfTen", new ClickLocation(423, 741))
  inGame.AddClickLocation("cardTwoOfTen", new ClickLocation(455, 723))
  inGame.AddClickLocation("cardThreeOfTen", new ClickLocation(495, 699))
  inGame.AddClickLocation("cardFourOfTen", new ClickLocation(538, 680))
  inGame.AddClickLocation("cardFiveOfTen", new ClickLocation(576, 667))
  inGame.AddClickLocation("cardSixOfTen", new ClickLocation(625, 666))
  inGame.AddClickLocation("cardSevenOfTen", new ClickLocation(673, 669))
  inGame.AddClickLocation("cardEightOfTen", new ClickLocation(712, 678))
  inGame.AddClickLocation("cardNineOfTen", new ClickLocation(756, 680))
  inGame.AddClickLocation("cardTenOfTen", new ClickLocation(820, 715))

  //My Board Options
  inGame.AddClickLocation("myBoardFarRight", new ClickLocation(1042, 434))

  inGame.AddClickLocation("myBoardOneOfOne", new ClickLocation(633, 427))

  inGame.AddClickLocation("myBoardOneOfTwo", new ClickLocation(581, 429))
  inGame.AddClickLocation("myBoardTwoOfTwo", new ClickLocation(680, 430))

  inGame.AddClickLocation("myBoardOneOfThree", new ClickLocation(540, 429))
  inGame.AddClickLocation("myBoardTwoOfThree", new ClickLocation(634, 428))
  inGame.AddClickLocation("myBoardThreeOfThree", new ClickLocation(729, 427))

  inGame.AddClickLocation("myBoardOneOfFour", new ClickLocation(495, 430))
  inGame.AddClickLocation("myBoardTwoOfFour", new ClickLocation(589, 427))
  inGame.AddClickLocation("myBoardThreeOfFour", new ClickLocation(682, 429))
  inGame.AddClickLocation("myBoardFourOfFour", new ClickLocation(776, 427))

  inGame.AddClickLocation("myBoardOneOfFive", new ClickLocation(445, 429))
  inGame.AddClickLocation("myBoardTwoOfFive", new ClickLocation(543, 430))
  inGame.AddClickLocation("myBoardThreeOfFive", new ClickLocation(636, 428))
  inGame.AddClickLocation("myBoardFourOfFive", new ClickLocation(730, 428))
  inGame.AddClickLocation("myBoardFixOfFive", new ClickLocation(824, 429))

  inGame.AddClickLocation("myBoardOneOfSix", new ClickLocation(397, 425))
  inGame.AddClickLocation("myBoardTwoOfSix", new ClickLocation(497, 428))
  inGame.AddClickLocation("myBoardThreeOfSix", new ClickLocation(590, 428))
  inGame.AddClickLocation("myBoardFourOfSix", new ClickLocation(681, 424))
  inGame.AddClickLocation("myBoardFiveOfSix", new ClickLocation(777, 425))
  inGame.AddClickLocation("myBoardSixOfSix", new ClickLocation(872, 430))

  inGame.AddClickLocation("myBoardOneOfSeven", new ClickLocation(349, 429))
  inGame.AddClickLocation("myBoardTwoOfSeven", new ClickLocation(451, 429))
  inGame.AddClickLocation("myBoardThreeOfSeven", new ClickLocation(545, 427))
  inGame.AddClickLocation("myBoardFourOfSeven", new ClickLocation(635, 430))
  inGame.AddClickLocation("myBoardFiveOfSeven", new ClickLocation(730, 429))
  inGame.AddClickLocation("myBoardSixOfSeven", new ClickLocation(826, 431))
  inGame.AddClickLocation("myBoardSevenOfSeven", new ClickLocation(915, 427))



  //His Board Options
  inGame.AddClickLocation("hisBoardOneOfOne", new ClickLocation(629, 298))

  inGame.AddClickLocation("hisBoardOneOfTwo", new ClickLocation(610, 306))
  inGame.AddClickLocation("hisBoardTwoOfTwo", new ClickLocation(705, 305))

  inGame.AddClickLocation("hisBoardOneOfThree", new ClickLocation(561, 301))
  inGame.AddClickLocation("hisBoardTwoOfThree", new ClickLocation(657, 307))
  inGame.AddClickLocation("hisBoardThreeOfThree", new ClickLocation(751, 303))

  inGame.AddClickLocation("hisBoardOneOfFour", new ClickLocation(517, 302))
  inGame.AddClickLocation("hisBoardTwoOfFour", new ClickLocation(611, 299))
  inGame.AddClickLocation("hisBoardThreeOfFour", new ClickLocation(704, 301))
  inGame.AddClickLocation("hisBoardFourOfFour", new ClickLocation(801, 304))

  inGame.AddClickLocation("hisBoardOneOfFive", new ClickLocation(471, 307))
  inGame.AddClickLocation("hisBoardTwoOfFive", new ClickLocation(564, 303))
  inGame.AddClickLocation("hisBoardThreeOfFive", new ClickLocation(659, 295))
  inGame.AddClickLocation("hisBoardFourOfFive", new ClickLocation(751, 301))
  inGame.AddClickLocation("hisBoardFiveOfFive", new ClickLocation(847, 298))

  inGame.AddClickLocation("hisBoardOneOfSix", new ClickLocation(427, 298))
  inGame.AddClickLocation("hisBoardTwoOfSix", new ClickLocation(519, 307))
  inGame.AddClickLocation("hisBoardThreeOfSix", new ClickLocation(612, 303))
  inGame.AddClickLocation("hisBoardFourOfSix", new ClickLocation(708, 299))
  inGame.AddClickLocation("hisBoardFiveOfSix", new ClickLocation(800, 303))
  inGame.AddClickLocation("hisBoardSixOfSix", new ClickLocation(892, 303))

  inGame.AddClickLocation("hisBoardOneOfSeven", new ClickLocation(377, 305))
  inGame.AddClickLocation("hisBoardTwoOfSeven", new ClickLocation(468, 305))
  inGame.AddClickLocation("hisBoardThreeOfSeven", new ClickLocation(561, 303))
  inGame.AddClickLocation("hisBoardFourOfSeven", new ClickLocation(653, 302))
  inGame.AddClickLocation("hisBoardFiveOfSeven", new ClickLocation(747, 303))
  inGame.AddClickLocation("hisBoardSixOfSeven", new ClickLocation(844, 306))
  inGame.AddClickLocation("hisBoardSevenOfSeven", new ClickLocation(936, 303))


  //Discover Options
  inGame.AddClickLocation("discoverCardOne", new ClickLocation(399, 373))
  inGame.AddClickLocation("discoverCardTwo", new ClickLocation(666, 378))
  inGame.AddClickLocation("discoverCardThree", new ClickLocation(913, 378))


  //Emote Menu
  val emoteMenu = new Menu()
  //Emote Locations
  emoteMenu.AddClickLocation("myFace", new ClickLocation(657, 593))
  emoteMenu.AddClickLocation("greetings", new ClickLocation(531, 610))
  emoteMenu.AddClickLocation("wellPlayed", new ClickLocation(536, 551))
  emoteMenu.AddClickLocation("thanks", new ClickLocation(559, 493))
  emoteMenu.AddClickLocation("wow", new ClickLocation(759, 491))
  emoteMenu.AddClickLocation("oops", new ClickLocation(791, 554))
  emoteMenu.AddClickLocation("threaten", new ClickLocation(791, 612))


}


class ClickLocation(x: Int, y: Int) {
  val position = (x, y)
}

class ClickLocationWithColor(x: Int, y: Int, rValue: Int, gValue: Int, bValue: Int) {
  val position = (x, y)
  val red = rValue
  val green = gValue
  val blue = bValue
}


class Menu() {
  val clickLocations = mutable.Map[String, ClickLocation]()
  val confirmLocations = mutable.Map[String, ClickLocationWithColor]()

  def AddClickLocation(name: String, clickLocation: ClickLocation): Unit = {
    clickLocations(name) = clickLocation
  }


  def AddConfirmLocation(name: String, confirmLocation: ClickLocationWithColor): Unit = {
    confirmLocations(name) = confirmLocation
  }
}

