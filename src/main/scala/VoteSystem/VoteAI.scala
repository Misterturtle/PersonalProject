package VoteSystem

import GameState.GameState
import com.typesafe.scalalogging.LazyLogging
import tph.Constants.ActionVotes._
import tph.{NoCard, HSCard}

import scala.collection.mutable.ListBuffer

/**
  * Created by Harambe on 3/19/2017.
  */


class VoteAI(vs: VoteState, gs: GameState) extends LazyLogging {


  def makeDecision(): List[(ActionVote, (HSCard, HSCard))] = {
    val decision = buildDecision()
    if (decision.indexList.nonEmpty)
      finalizePattern(decision)
    else
      List((ActionUninit(), (NoCard(), NoCard())))
  }


  def buildDecision(): Pattern = {

    def loop(loopingPattern: Pattern): Pattern = {
      val basePattern = buildPattern(loopingPattern)

      basePattern match {
        case Pattern(Nil) =>
          logger.debug("No patterns could be detected. This should mean no votes were cast.")
          Pattern(Nil)

        case `loopingPattern` =>
          logger.debug(s"We have reached the most popular Decision. Return this Decision. VoteSize: ${loopingPattern.indexList.size}")
          loopingPattern

        case _ =>
          logger.debug(s"We have established another pattern. Checking if the pattern is popular enough to combine with the current pattern. Current VoteSize: ${loopingPattern.indexList.size}. New Pattern VoteSize: ${basePattern.indexList.size}")

          var loopingPatternTally = 0.0
          var bothPatternTally = 0.0

          forEachVoter(containsPattern(loopingPattern, _), voter => loopingPatternTally += voter.personalVotePower)
          forEachVoter(voteList => containsPattern(loopingPattern, voteList) && containsPattern(basePattern, voteList), voter => bothPatternTally += voter.personalVotePower)
          if (bothPatternTally >= loopingPatternTally / 2.0) {
            logger.debug(s"The majority of the current pattern voters also think we should do the 2nd pattern. Combining the patterns and continuing the loop.")
            loop(combinePatterns(loopingPattern, basePattern))
          }
          else {
            if (loopingPattern == Pattern(Nil)) {
              logger.debug(s"First step of building a pattern detected. Starting loop with first base pattern.")
              loop(basePattern)
            }
            else{
              logger.debug(s"The next order pattern is not as popular as the base pattern. Return base pattern.")
              basePattern}
          }
      }
    }
    loop(Pattern(Nil))
  }

  def checkForBattlecryTarget(decision: List[(ActionVote, (HSCard, HSCard))]): List[(ActionVote, (HSCard, HSCard))] = {

    decision.foldLeft(List[(ActionVote, (HSCard, HSCard))]()) { case (r, c) =>
      c match {
        case (vote, st) =>
          vote match {
            case x: CardPlayWithEnemyTarget =>
              st match {
                case (source, target) =>
                  if (source.cardInfo.cardType.getOrElse("None") == "MINION")
                    r :+(CardPlayWithEnemyTargetWithPosition(vote.card, vote.enemyTarget, gs.friendlyPlayer.board.size + 1), st)
                  else
                    r :+ c
              }

            case x: CardPlayWithFriendlyTarget =>
              st match {
                case (source, target) =>
                  if (source.cardInfo.cardType.getOrElse("None") == "MINION")
                    r :+(CardPlayWithFriendlyTargetWithPosition(vote.card, vote.friendlyTarget, gs.friendlyPlayer.board.size + 1), st)
                  else
                    r :+ c


              }

            case x: FutureCardPlayWithEnemyTarget =>
              st match {
                case (source, target) =>
                  if (source.cardInfo.cardType.getOrElse("None") == "MINION")
                    r :+(CardPlayWithEnemyTargetWithPosition(vote.card, vote.enemyTarget, gs.friendlyPlayer.board.size + 1), st)
                  else
                    r :+ c
              }

            case x: FutureCardPlayWithFriendlyTarget =>
              st match {
                case (source, target) =>
                  if (source.cardInfo.cardType.getOrElse("None") == "MINION")
                    r :+(CardPlayWithFriendlyTargetWithPosition(vote.card, vote.friendlyTarget, gs.friendlyPlayer.board.size + 1), st)
                  else
                    r :+ c


              }





            case _ =>
              r :+ c
          }
      }
    }
  }

  def finalizePattern(pattern: Pattern): List[(ActionVote, (HSCard, HSCard))] = {
    val arr = new Array[(ActionVote, Int)](pattern.indexList.last._2 + 1)
    pattern.indexList.foreach { case (vote, index) =>
      arr(index) = (vote, index)
    }

    arr.foreach { case (vote, index) =>
      if (!arr.isDefinedAt(index)) {
        var nextVoteTallyMap = Map[ActionVote, Double]()

        forEachVoter(containsPattern(pattern, _), { voter =>
          val patternSlice = getPatternSlice(pattern, voter.actionVoteList)
          nextVoteTallyMap += (patternSlice(index) -> voter.personalVotePower)
        })
        arr(index) = (findHighestValue(nextVoteTallyMap.toList), index)
      }
    }

    val builtPattern = Pattern(arr.toList)
    val decision = builtPattern.indexList.foldLeft(List[(ActionVote, (HSCard, HSCard))]()) { case (r, c) =>
      r :+(c._1, gs.getSourceAndTarget(c._1))
    }
    checkForBattlecryTarget(decision)
  }


  def getPatternSlice(pattern: Pattern, voteList: List[ActionVote]): List[ActionVote] = {
    val patternBounds = findPatternBounds(voteList, pattern)
    val droppedList = voteList.dropWhile(_ != pattern.indexList.head._1)
    if (containsPattern(pattern, voteList)) {
      if (containsPattern(pattern, droppedList.tail)) {
        getPatternSlice(pattern, droppedList.tail)
      }
      else {
        droppedList.slice(0, patternBounds.get._2 + 1)
      }
    }
    else {
      voteList.slice(0, patternBounds.get._2 + 1)
    }
  }


  def combinePatterns(basePattern: Pattern, addPattern: Pattern): Pattern = {

    //Finds the most common index gap between the end of the additional pattern and the start of the base pattern
    //Positive indicates additional pattern comes after base pattern

    if(basePattern.indexList.isEmpty && addPattern.indexList.isEmpty){
      logger.debug("When combining patterns, both patterns are empty. Returning an empty pattern.")
      return Pattern(Nil)
    }
    if(basePattern.indexList.isEmpty && addPattern.indexList.nonEmpty){
      logger.debug("When combining patterns, the base pattern is empty but the additional is not. Returning additional.")
      return addPattern
    }

    if(basePattern.indexList.nonEmpty && addPattern.indexList.isEmpty){
      logger.debug("When combining patterns, the additional pattern is empty but the base pattern is not. Returning base.")
      return basePattern
    }




    var offsetTallyMap = Map[Int, Double]()

    forEachVoter(voteList => containsPattern(basePattern, voteList) && containsPattern(addPattern, voteList), { voter =>

      val basePatternBounds = findPatternBounds(voter.actionVoteList, basePattern)
      val addPatternBounds = findPatternBounds(voter.actionVoteList, addPattern)

      val offset = addPatternBounds.get._2 - basePatternBounds.get._1
      if (offsetTallyMap.isDefinedAt(offset))
        offsetTallyMap += (offset -> (offsetTallyMap(offset) + voter.personalVotePower))
      else
        offsetTallyMap += (offset -> voter.personalVotePower)
    }
    )

    if (offsetTallyMap.nonEmpty) {
      val offsetTallyMapWinner = offsetTallyMap.toList.sortBy(x => x._2).reverse.head
      basePattern.combine(addPattern, offsetTallyMapWinner._1)
    }
    else {
      //Not sure if this will ever get ran. Only scenario is if no voteList contains both patterns?
      basePattern.combine(addPattern, 0)
    }
  }


  def forEachVoter(condition: List[ActionVote] => Boolean, effect: Voter => Any) = {
    vs.voterMap.foreach {
      case (name, voter) =>
        if (condition(voter.actionVoteList))
          effect(voter)
    }
  }


  def buildPattern(startingPattern: Pattern): Pattern = {

    def loop(loopingPattern: Pattern): Pattern = {
      //Finds the most popular vote DIRECTLY before or after the pattern.
      val nextOrderPattern = findNextOrder(loopingPattern, startingPattern)
      //Tallys the occurrences of this next order pattern

      if (nextOrderPattern != loopingPattern) {
        var nextOrderPatternTally = 0.0
        forEachVoter(
          containsPattern(nextOrderPattern, _), {
            voter => nextOrderPatternTally += voter.personalVotePower
          })

        //If this tally is 50% or more than the starting pattern occurrences, then repeat with this new pattern else return the starting pattern
        var loopingPatternTally = 0.0
        forEachVoter(containsPattern(loopingPattern, _), { voter => loopingPatternTally += voter.personalVotePower })

        if (loopingPattern.indexList != Nil) {
          if (nextOrderPatternTally >= loopingPatternTally / 2.0)
            loop(nextOrderPattern)
          else
            loopingPattern
        }
        else
          loop(nextOrderPattern)
      }
      else
        loopingPattern
    }

    //Tallys the individual votes from all voteLists that contain the starting pattern. The starting pattern is removed in order to not be double tallied.
    val nonPatternIndividualVotes = findIndividualVotes(startingPattern)
    //Get the max vote
    if (nonPatternIndividualVotes.nonEmpty) {
      val popularNonPatternVote = findHighestValue(nonPatternIndividualVotes)
      loop(Pattern(List((popularNonPatternVote, 0))))
    }
    else {
      startingPattern
    }
  }

  def tallyFunction(condition: (List[ActionVote]) => Boolean, filter: (List[ActionVote]) => List[ActionVote]): Map[ActionVote, Double] = {

    vs.voterMap.foldLeft(Map[ActionVote, Double]()) { (accumTallyMap, voterMapElement) =>
      voterMapElement match {
        case (name, voter) =>
          if (condition(voter.actionVoteList)) {
            filter(voter.actionVoteList).foldLeft(accumTallyMap) { (r, c) =>
              if (r.isDefinedAt(c))
                r + (c -> (r(c) + voter.personalVotePower))
              else {
                r + (c -> voter.personalVotePower)
              }
            }
          }
          else {
            accumTallyMap
          }
      }
    }
  }


  def tallyPattern(pattern: Pattern): Double = {

    vs.voterMap.foldLeft(0.0) { (accumTally, voterMapElement) =>
      voterMapElement match {
        case (name, voter) =>
          if (containsPattern(pattern, voter.actionVoteList)) {
            accumTally + voter.personalVotePower
          }
          else accumTally
      }
    }
  }

  def findHighestValue(voteTallyList: List[(ActionVote, Double)]): ActionVote = {
    voteTallyList.sortBy(x => x._2).reverse.head._1
  }

  def containsPattern(pattern: Pattern, voteList: List[ActionVote]): Boolean = {

    if (pattern.indexList.isEmpty)
      return true
    if (!voteList.contains(pattern.indexList.head._1))
      false
    else {
      val droppedList = voteList.dropWhile(_ != pattern.indexList.head._1)

      val comparison = pattern.indexList.foldLeft(None: Option[Boolean]) { (r, c) =>
        c match {
          case (vote, index) =>
            if (droppedList.isDefinedAt(index) && droppedList(index) == vote) {
              if (index == pattern.indexList.last._2) {
                Some(true)
              }
              else {
                None
              }
            }
            else
              return false
        }
      }
      comparison match {
        case Some(boolean) =>
          boolean
        case None =>
          false
      }
    }
  }


  def findIndividualVotes(requiredPattern: Pattern): List[(ActionVote, Double)] = {
    //Fold the voterMap
    //If voter's voteList contains the pattern (which if the pattern.indexList is Nil, always returns true) then continue, else return the accumTallyMap
    //Remove the pattern votes from the voteList (removePatternFromVoteList method)
    //Fold the resulting list with the starting value as the accumTalylMap and create a new tally map with the individual votes tallied
    //Return this local tallyMap as the accumTallyMap
    //Convert the completeTotalTallyMap to a list and return

    vs.voterMap.foldLeft(Map[ActionVote, Double]()) { (accumTallyMap, voterMapElement) =>
      voterMapElement match {
        case (name, voter) =>
          if (containsPattern(requiredPattern, voter.actionVoteList)) {
            val uniquePatternVoteList = makeVotesInPatternUnique(voter.actionVoteList, requiredPattern)
            uniquePatternVoteList.foldLeft(accumTallyMap) { (localTally, vote) =>
              if (vote != PatternVote()) {
                if (localTally.isDefinedAt(vote))
                  localTally + (vote -> (localTally(vote) + voter.personalVotePower))
                else
                  localTally + (vote -> voter.personalVotePower)
              }
              else
                localTally
            }
          }
          else
            accumTallyMap
      }
    }.toList
  }


  def findPatternBounds(voteList: List[ActionVote], pattern: Pattern): Option[(Int, Int)] = {

    if (voteList.nonEmpty) {
      if (containsPattern(pattern, voteList)) {
        val zippedList = voteList.zipWithIndex

        val firstNonContainingSlice = zippedList.dropWhile {
          case (vote, index) =>
            val slice = voteList.slice(index, voteList.size)
            containsPattern(pattern, slice)
        }

        val startingIndex = {
          if (firstNonContainingSlice.nonEmpty) {
            firstNonContainingSlice.head._2 - 1
          }
          else {
            zippedList.last._2
          }
        }
        Some(startingIndex, startingIndex + pattern.indexList.last._2)
      }
      else {
        None
      }
    }
    else
      {
        None
      }
  }

  def getPreviousVote(pattern: Pattern, voteList: List[ActionVote]): Option[ActionVote] = {

    val patternBounds = findPatternBounds(voteList, pattern)

    if(patternBounds.nonEmpty) {
      if (voteList.isDefinedAt(patternBounds.get._1 - 1))
        Some(voteList(patternBounds.get._1 - 1))
      else
        None
    }
    else
      None
  }

  def getNextVote(pattern: Pattern, voteList: List[ActionVote]): Option[ActionVote] = {
    val patternBounds = findPatternBounds(voteList, pattern)

    if(patternBounds.nonEmpty) {
      if (voteList.isDefinedAt(patternBounds.get._2 + 1))
        Some(voteList(patternBounds.get._2 + 1))
      else
        None
    }
    else
      None
  }

  def findNextOrder(lowerOrderPattern: Pattern, basePattern: Pattern): Pattern = {

    val beforeTallyMap = tallyFunction(voteList => containsPattern(lowerOrderPattern, voteList) && containsPattern(basePattern, voteList), { voteList =>
      val uniqueVoteList = makeVotesInPatternUnique(voteList, basePattern)
      val previousVote = getPreviousVote(lowerOrderPattern, uniqueVoteList)

      if (previousVote.getOrElse(PatternVote()) != PatternVote())
        List(previousVote.get)
      else
        Nil
    })

    val afterTallyMap = tallyFunction(voteList => containsPattern(lowerOrderPattern, voteList) && containsPattern(basePattern, voteList), { voteList =>
      val uniqueVoteList = makeVotesInPatternUnique(voteList, basePattern)
      val nextVote = getNextVote(lowerOrderPattern, uniqueVoteList)
      if (nextVote.getOrElse(PatternVote()) != PatternVote())
        List(nextVote.get)
      else
        Nil
    })

    if (beforeTallyMap.isEmpty) {
      if (afterTallyMap.isEmpty) {
        return lowerOrderPattern
      }
      else {
        val highestAfterVote = afterTallyMap.toList.sortBy(x => x._2).reverse.head
        return Pattern(lowerOrderPattern.indexList :+(highestAfterVote._1, lowerOrderPattern.indexList.last._2 + 1))
      }
    }

    if (afterTallyMap.isEmpty) {
      if (beforeTallyMap.isEmpty) {
        return lowerOrderPattern
      }
      else {
        val highestBeforeVote = beforeTallyMap.toList.sortBy(x => x._2).reverse.head
        return Pattern((highestBeforeVote._1, 0) :: lowerOrderPattern.indexList.map { case (k, v) => (k, v + 1) })
      }

    }

    val highestBeforeVote = beforeTallyMap.toList.sortBy(x => x._2).reverse.head
    val highestAfterVote = afterTallyMap.toList.sortBy(x => x._2).reverse.head

    if (highestBeforeVote._2 > highestAfterVote._2)
      Pattern((highestBeforeVote._1, 0) :: lowerOrderPattern.indexList.map { case (k, v) => (k, v + 1) })
    else
      Pattern(lowerOrderPattern.indexList :+(highestAfterVote._1, lowerOrderPattern.indexList.last._2 + 1))
  }


  def makeVotesInPatternUnique(voteList: List[ActionVote], pattern: Pattern): List[ActionVote] = {

    //If the voteList contains the pattern then continue else return the voteList
    //If the voteList.head == pattern.head then slice the correct size else return the 0 index and recall method without head
    //If the slice contains the pattern, we know it is the correct 0 index else we return the 0 index and recall method without the head
    //Fold the slice and create a List that contains the non-pattern votes
    //Combine this list with the rest of the non-sliced original list
    //Return that list

    if (pattern.indexList.isEmpty)
      return voteList
    if (containsPattern(pattern, voteList)) {
      if (voteList.head == pattern.indexList.head._1) {
        val slice = voteList.slice(0, pattern.indexList.last._2 + 1)
        if (containsPattern(pattern, slice)) {
          val patternVoteList = pattern.indexList.map { case (k, v) => k }
          val uniqueVoteList = slice.foldLeft(List[ActionVote]()) { (r, c) =>
            if (patternVoteList.contains(c))
              r :+ PatternVote()
            else
              r :+ c
          }

          uniqueVoteList ::: voteList.drop(pattern.indexList.last._2 + 1)
        }
        else {
          voteList.head :: makeVotesInPatternUnique(voteList.drop(1), pattern)
        }
      }
      else
        voteList.head :: makeVotesInPatternUnique(voteList.drop(1), pattern)
    }
    else
      voteList
  }

}




