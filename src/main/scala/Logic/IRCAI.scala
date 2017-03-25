package Logic

import VoteSystem.{ActionVote, Vote, VoteManager}
import tph.Constants.ActionVotes._
import tph.{Card, Constants, GameState}

import scala.collection.mutable.ListBuffer

/**
  * Created by Harambe on 3/19/2017.
  */



/*


Good:

- Does not use over the given amount of mana?
- Similarity
    -

-






Bad:

- Similarity to votes from trolls




 */





class IRCAI(voteManager: VoteManager, gameState: GameState) {


  case class ManaSpendingPattern(startMana: Int, endMana: Int, votes: List[Vote])

  trait OrderPattern {
    def sourceVote: ActionVote

    def targetVote: ActionVote
  }

  case class NextVote(sourceVote: ActionVote, targetVote: ActionVote) extends OrderPattern

  case class SingleVote(sourceVote: ActionVote) extends OrderPattern {
    def targetVote = ActionUninit()
  }

  case class HigherOrderPattern(list1:(List[ActionVote], Double), list2:(List[ActionVote], Double), differenceIndex:List[Int])






  case class OrPattern(firstPattern:(OrderPattern, Int), secondPattern:(OrderPattern, Int)){
    def combine:(OrderPattern,Int) = {
      if(firstPattern._2 >= secondPattern._2)
        (firstPattern._1, firstPattern._2 + secondPattern._2)
      else
        (secondPattern._1, firstPattern._2 + secondPattern._2)
    }
  }


  def simplifyHigherOrderPatterns(list:List[HigherOrderPattern]): List[HigherOrderPattern] = {





  }

  def findHigherOrderPatterns(): Array[List[HigherOrderPattern]] = {

    def compareVoteLists(list1:List[ActionVote], list2:List[ActionVote]): List[Int] = {
      if(list1.size != list2.size)
        throw IllegalArgumentException
      else {
        val differenceList = ListBuffer[Int]()

        for (a <- 0 until list1.size) {
          if (list1(a) != list2(a))
            differenceList.append(a)
        }
        differenceList.toList
      }
    }

    val sizeArray = new Array[Map[List[ActionVote], Double]](42)
    voteManager.voterMap.foreach{case (name,voter) =>
      val size = voter.actionVoteList.size
      if(!sizeArray(size).isDefinedAt(voter.actionVoteList))
        sizeArray(size) = Map[List[ActionVote], Double](voter.actionVoteList -> voter.personalVotePower)
      else
        sizeArray(size) = Map[List[ActionVote], Double](voter.actionVoteList -> (voter.personalVotePower + sizeArray(size)(voter.actionVoteList)))
    }

    val higherOrderArray = new Array[List[HigherOrderPattern]](42)

    for(a<- 0 until sizeArray.size) {

        sizeArray(a).foreach { case tallyVoteList =>
          sizeArray(a).foreach { case comparisonTallyVoteList =>

            if (tallyVoteList != comparisonTallyVoteList)
              higherOrderArray(a) = HigherOrderPattern(tallyVoteList, comparisonTallyVoteList, compareVoteLists(tallyVoteList._1, comparisonTallyVoteList._1)) :: higherOrderArray(a)
          }
        }
      }
    higherOrderArray
    }


  def findOrderPatterns(): List[(OrderPattern, Int)] = {
    val voterOrderPatterns = ListBuffer[OrderPattern]()
    voteManager.voterMap.foreach { case (name, voter) =>
      voter.actionVoteList.foreach {
        vote =>
          val index = voter.actionVoteList.indexWhere(_ == vote)
          if(voter.actionVoteList.isDefinedAt(index +1))
            voterOrderPatterns.append(NextVote(voter.actionVoteList(index), voter.actionVoteList(index +1)))
          if(voter.actionVoteList.size == 1)
            voterOrderPatterns.append(SingleVote(voter.actionVoteList.head))

      }
    }
    voterOrderPatterns.foldLeft(Map[OrderPattern, Int]()) { (r, c) =>
      if (r.isDefinedAt(c))
        r + (c -> (r(c) + 1))
      else
        r + (c -> 1)
    }.toList.sortBy(x => x._2).reverse
  }


  def extractVotesFromOrderPattern(orderPatternList: List[(OrderPattern, Int)]): List[Vote] = {

    val voteSequence = ListBuffer[ActionVote]()
    val orderPattern = ListBuffer[(OrderPattern, Int)]()
    orderPatternList.foreach(x => orderPattern.append(x))


//    val firstVotePattern = orderPattern.find { x => x._1 match {
//      case op: FirstVote =>
//        true
//      case _ =>
//        false
//    }
//    }
//    if (firstVotePattern.nonEmpty)
//      voteSequence.append(firstVotePattern.get._1.sourceVote)

//    def nextVote(): ActionVote = {
//
//
//      val nextVoteOption = orderPattern.find{ x => x._1 match {
//        case nextPattern: NextVote =>
//          if (x._1.sourceVote == voteSequence.last)
//            true
//          else
//            false
//        case _ =>
//          false
//      }}
//
//      if (nextVoteOption.nonEmpty)
//        nextVoteOption.get._1.targetVote
//      else
//        ActionUninit()
//    }
//
//    while (nextVote() != ActionUninit())
//      voteSequence.append(nextVote())

    //voteSequence.toList

    orderPatternList.sortBy(x => x._2).foldRight(List[ActionVote]()){(c,r) =>
      if(c._1.targetVote != ActionUninit())
    c._1.targetVote :: c._1.sourceVote :: r
      else
        c._1.sourceVote :: r
    }.reverse

  }

  def simplify2CardOrPattern(orderPattern: List[(OrderPattern, Int)]):List[(OrderPattern, Int)] = {

    orderPattern.foldLeft(orderPattern){(r,c) =>
    val inverseExists = r.find(y => c._1.sourceVote == y._1.targetVote && c._1.targetVote == y._1.sourceVote)
      if(inverseExists.nonEmpty)
        new OrPattern(c, inverseExists.get).combine :: r.filter(x => x != c && x != inverseExists.get)
      else
        r
    }
  }


  def findManaSpendingPatterns() = {


    val listOfManaPatterns = voteManager.voterMap.map { voter =>
      voter._2.actionVoteList.foldLeft(List[Int]())((r, vote) =>
        vote match {
          case x: CardCostsMana =>
            gameState.friendlyPlayer.hand(x.card).cardInfo.cost.getOrElse(Constants.INT_UNINIT) :: r

          case x: HeroPowerCostsMana =>
            gameState.friendlyPlayer.heroPower.get.cardInfo.cost.getOrElse(Constants.INT_UNINIT) :: r

          case _ =>
            r
        }
      ).reverse
    }
  }

  def makeDecision(): List[Vote] = {

    val orderPatterns = findOrderPatterns()
    val simplify = simplify2CardOrPattern(orderPatterns)

    extractVotesFromOrderPattern(simplify)
  }
}
