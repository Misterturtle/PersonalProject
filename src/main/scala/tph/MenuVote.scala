package tph

import tph.Constants.MenuVoteCodes.MenuVoteCode

/**
  * Created by Harambe on 10/30/2016.
  */
class MenuVote(sender: String, voteCode: Constants.MenuVoteCodes.MenuVoteCode) extends Vote(sender: String, voteCode: Constants.MenuVoteCodes.MenuVoteCode) {

  var menuVoteCode: MenuVoteCode = voteCode

}
