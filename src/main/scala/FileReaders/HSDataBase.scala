package FileReaders

import net.liftweb.json.JsonParser._
import java.io.{BufferedReader, FileReader}

import net.liftweb.json.{JObject, DefaultFormats}

/**
  * Created by Harambe on 3/18/2017.
  */
class HSDataBase {

  val reader = new BufferedReader(new FileReader(getClass.getResource("/cards.json").getPath))
  implicit val formats = DefaultFormats


  val hsCards = parse(reader).extract[List[CardInfo]].filter(_.name.nonEmpty)
  val cardIDMap = hsCards.map(card => card.id.get -> card).toMap

}

case class CardInfo(id: Option[String], name:Option[String], cost:Option[Int], mechanics:Option[List[String]], attack:Option[Int], race:Option[String], playRequirements:Option[JObject]){
  val playReqMap = playRequirements.getOrElse(new JObject(Nil)).obj.foldLeft(Map[String,Int]()){(r,c) => r + (c.values._1 -> c.values._2.toString.toInt)}
}
