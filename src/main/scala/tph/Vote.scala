package tph

import scala.Tuple1


/**
  * Created by Harambe on 10/29/2016.
  */



class Vote(ircSender : String, origVoteCode: (_)) {

  val voteType = this.getClass
  val sender = ircSender
  var voteCode = origVoteCode



  def GetVoteCode(): (_) ={
    return voteCode
  }

  def SetVoteCode(newVoteCode:(_)): Unit = {
    voteCode = newVoteCode
  }






}
