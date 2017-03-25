package tph

import FileReaders.CardInfo
import net.liftweb.json.JObject


/**
  * Created by Harambe on 2/20/2017.
  */


case class Card(name: String, id: Int, handPosition: Int, boardPosition: Int, player: Int, cardID: String, isDamaged:Boolean = false, isFrozen:Boolean = false, cardInfo:CardInfo = new CardInfo(Some(Constants.STRING_UNINIT), Some(Constants.STRING_UNINIT), Some(Constants.INT_UNINIT), Some(Nil), Some(Constants.INT_UNINIT), Some(Constants.STRING_UNINIT), Some(new JObject(Nil))))




