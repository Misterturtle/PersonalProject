package tph

import scala.collection.mutable._


/**
  * Created by Harambe on 10/29/2016.
  */



class Voter(sender: String) {

  val flagSystem = new FlagSystem
  val bindFlag = new Flag()
  val futureFlag = new Flag()
  flagSystem.AddFlag(bindFlag)
  flagSystem.AddFlag(futureFlag)


  val actionVoteList = new ActionVoteList()
  var emojiVote = new Vote("sender", "voteCode")
  var menuVote = new Vote("sender", "voteCode")

  val voteLog = new VoteList



      def AdjustVotes(previousDecision: Vote): Unit ={

        actionVoteList.AdjustVotes(previousDecision)
      }


      def VoteEntry(vote:Vote): Unit = {

          val voteType = vote.getClass
          if(voteType == Class[ActionVote])
            {
              actionVoteList.AddVote(vote)
            }

          if(voteType == Class[EmojiVote])
            {
              emojiVote = vote
            }
        if(voteType == Class[MenuVote])
          {
            menuVote = vote
          }
      }


      def GetActionVotes(): scala.collection.mutable.Map[(_), Int] = {

        return actionVoteList.TallyVotes()

      }

      def GetEmojiVote(): (_) = {
          return emojiVote

      }

      def GetMenuVote(): (_) = {
          return menuVote

      }
}
