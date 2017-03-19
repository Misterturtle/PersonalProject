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
  case class HSCardData(id: String, name:Option[String], mechanics:Option[List[String]], playRequirements: Option[JObject])

  val hsCards = parse(reader).extract[List[HSCardData]].filter(_.name.nonEmpty)
  val cardsWithPlayReq = hsCards.filter(_.playRequirements.getOrElse(new JObject(Nil)).obj != Nil)
  val cardIDMap = hsCards.map(card => card.id -> card).toMap
  val playReqList = cardsWithPlayReq.flatMap(card => card.playRequirements.getOrElse(new JObject(Nil)).obj).map{case obj => obj.name}.foldLeft(List[String]()){ case (r,c) =>
    if(!r.contains(c))
      c :: r
    else r
  }
  playReqList.foreach(x => println(x))
  cardsWithPlayReq.map{ case card => card.name.get}
  val playReqMap = playReqList.map(playReqName => playReqName -> cardsWithPlayReq.flatMap{ case card => if(card.playRequirements.get.obj.exists(obj => obj.name == playReqName))Some(card.name.getOrElse("")) else None}).toMap
  for(a<-0 until playReqList.size){
    println(s"${playReqList(a)}: ${playReqMap(playReqList(a))}")
  }
  playReqList(0)
  //playReqMap.foreach{case (playReq, cardNameList) => cardNameList.foreach(cardName => println(cardName))}




}
