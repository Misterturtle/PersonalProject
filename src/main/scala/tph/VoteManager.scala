package tph

import scala.collection._


class VoteManager {

  var tallyActionMap = mutable.Map[(_), Int]()
  val tallyEmojiMap = mutable.Map[(_), Int]()
  val tallyMenuMap = mutable.Map[(_), Int]()

  val listOfVoters = mutable.Map[String, Voter]()


  def VoteEntry(vote: Vote): Unit = {

    //If voter is not in listOfVoters
    if (!listOfVoters.isDefinedAt(vote.sender)) {
      //Assign a new voter to our voterList
      listOfVoters(vote.sender) = new Voter(vote.sender)
    }
    //Call that voter's VoteEntry method
    listOfVoters(vote.sender).VoteEntry(vote)
  }


  def AdjustVotes(previousDecision: Vote): Unit = {

    //Adjust votes for all senders with an actionList. Emoji and Menu votes do not need to be adjusted.
    listOfVoters foreach {
      case (sender, voter) =>
        listOfVoters(sender).AdjustVotes(previousDecision)

    }
  }


  def DecideAction(): (_) = {

    //Each Voter needs to tally his normal, bind, and future action list.
    //**It will be in the form of Map[Case, Int]. (Case = voteCode)
    //Voters then combine the vote values. (How much they're worth)
    //Voters then report a map of [Vote, Int] to this message

    TallyActionVotes()
    return tallyActionMap.max

  }


  def TallyActionVotes(): Unit = {


    listOfVoters foreach {
      case (sender, voter) =>
        val newTallyMap = listOfVoters(sender).TallyActionVotes()

        newTallyMap foreach {
          case (vote, value) =>

            if (!tallyActionMap.contains(vote)) {
              tallyActionMap(vote) = value
            }
            else {
              tallyActionMap(vote) += value
            }
        }
    }
  }

  def EmojiDecide(): (_) = {

    listOfVoters foreach {
      case (sender, voter) =>
        val emojiVote = voter.GetEmojiVote()

            if (!tallyEmojiMap.contains(emojiVote)) {
              tallyEmojiMap(emojiVote) = 1
            }
            else {
              tallyEmojiMap(emojiVote) += 1
            }
        }
    return tallyEmojiMap.max
    }


  def MenuDecide(): (_) = {

    listOfVoters foreach {
      case (sender, voter) =>
        val menuVote = listOfVoters(sender).GetMenuVote()

        if (!tallyMenuMap.contains(menuVote)) {
          tallyMenuMap(menuVote) = 1
        }
        else {
          tallyMenuMap(menuVote) += 1
        }
    }

    return tallyMenuMap.max
  }




}

