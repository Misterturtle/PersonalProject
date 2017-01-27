package tph

import tph.Constants.EmojiVoteCodes.EmojiVoteCode

/**
  * Created by Harambe on 10/30/2016.
  */


class EmojiVote(sender: String, voteCode: Constants.EmojiVoteCodes.EmojiVoteCode) extends Vote(sender: String, voteCode: Constants.EmojiVoteCodes.EmojiVoteCode) {


  var emojiVoteCode: EmojiVoteCode = voteCode

  }



