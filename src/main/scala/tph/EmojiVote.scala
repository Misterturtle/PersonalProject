package tph

/**
  * Created by Harambe on 10/30/2016.
  */




class EmojiVote(sender:String, voteCode:String) extends Vote(sender:String, voteCode:String) {



  voteCase = Constants.EmoteVotes.OOPS

  def SetVoteCase(): Any ={

//    voteCode match {
//
//      case Constants.EmoteVotes.GREETINGS =>

        println("Changing to oops. Currently" + voteCase)
        println(voteCase)





    }

  }



