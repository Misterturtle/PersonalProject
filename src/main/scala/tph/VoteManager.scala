package tph

import scala.collection._


class VoteManager {

  val totalActionList = new ActionVoteList
  val totalEmojiList = new EmojiVoteList
  val totalMenuList = new MenuVoteList

  val listOfVoters = mutable.Map[String, Voter]()


  def VoteEntry(vote: Vote): Unit = {

      //If voter is not in listOfVoters
      if(!listOfVoters.isDefinedAt(vote.sender)){
        //Assign a new voter to our voterList
        listOfVoters(vote.sender) = new Voter(vote.sender)
      }

        //Call that voter's VoteEntry method
        listOfVoters(vote.sender).VoteEntry(vote)

    }


  def AdjustVotes(previousDecision: Vote): Unit =
  {

    listOfVoters foreach{
      case(sender,voter) =>




    }





  }


}

