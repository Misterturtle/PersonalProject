package tph

import tph.IrcMessages._

import scala.reflect.internal.Types.ConstantType


/**
  * Created by Harambe on 10/29/2016.
  */


//!future, !Att my 1 his 2, !future, !Play 1 spot target


//Order Bind, Freeze Bind

class Vote() {

  var voteType = Constants.UNINIT

  var flagSystem = new FlagSystem

  var knownCommands = 0



  var card = Constants.UNINIT
  var name = ""
  var spot = Constants.UNINIT
  var target = Constants.UNINIT
  var battlecry = Constants.UNINIT
  var sender = ""




  name match {
    case Constants.Votes.DISCOVER =>


    //Battlecry Play Type
    case Constants.Votes.CARD_PLAY_WITH_FRIENDLY_OPTION =>


    case Constants.Votes.CARD_PLAY_WITH_FRIENDLY_FACE_OPTION =>

    case Constants.Votes.CARD_PLAY_WITH_ENEMY_OPTION =>

    case Constants.Votes.CARD_PLAY_WITH_ENEMY_FACE_OPTION =>


    //Battlecry and Position Type
    case Constants.Votes.CARD_PLAY_WITH_FRIENDLY_OPTION_WITH_POSITION =>

    case Constants.Votes.CARD_PLAY_WITH_FRIENDLY_FACE_OPTION_WITH_POSITION =>

    case Constants.Votes.CARD_PLAY_WITH_ENEMY_OPTION_WITH_POSITION =>

    case Constants.Votes.CARD_PLAY_WITH_ENEMY_FACE_OPTION_WITH_POSITION =>


    //Normal Turn Play Type
    case Constants.Votes.CARD_PLAY =>

    case Constants.Votes.CARD_PLAY_WITH_POSITION =>

    case Constants.Votes.CARD_PLAY_WITH_FRIENDLY_TARGET =>

    case Constants.Votes.CARD_PLAY_WITH_ENEMY_TARGET =>

    case Constants.Votes.CARD_PLAY_WITH_FRIENDLY_FACE_TARGET =>

    case Constants.Votes.CARD_PLAY_WITH_ENEMY_FACE_TARGET =>


    case Constants.Votes.HERO_POWER =>

    case Constants.Votes.HERO_POWER_WITH_ENEMY_FACE =>

    case Constants.Votes.HERO_POWER_WITH_ENEMY_TARGET =>

    case Constants.Votes.HERO_POWER_WITH_FRIENDLY_FACE =>

    case Constants.Votes.HERO_POWER_WITH_FRIENDLY_TARGET =>



    //Attack Type
    case Constants.Votes.NORMAL_ATTACK_WITH_ENEMY_TARGET =>

    case Constants.Votes.NORMAL_ATTACK_WITH_ENEMY_FACE_TARGET =>

    case Constants.Votes.FACE_ATTACK_WITH_ENEMY_TARGET =>

    case Constants.Votes.FACE_ATTACK_WITH_ENEMY_FACE_TARGET =>

    case _ =>


  }


  def SetVoteType(voteType: Int): Unit = {
    this.voteType = voteType
  }

  def GetVoteType(): Int = {
    return voteType
  }

}
