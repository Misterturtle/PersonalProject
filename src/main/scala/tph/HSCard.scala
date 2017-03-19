package tph

import scala.util.parsing.json._



/**
  * Created by Harambe on 2/20/2017.
  */
case class Card(name: String, id: Int, handPosition: Int, boardPosition: Int, player: Int, cardID: String)
