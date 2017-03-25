//Topic
println("Scenario 1:")
//Scenario 1
val voteList1 = List[(String, Int)](
  ("a", 100)
)
decide(voteList1)
println("Scenario 2:")
val voteList2 = List[(String, Int)](
  ("a", 40),
  ("bc", 35),
  ("bd", 25)
)
decide(voteList2)
println("Scenario 3")
val voteList3 = List[(String, Int)](
  ("a", 40),
  ("abc", 25),
  ("ab", 35)
)
decide(voteList3)
def decide(voteList: List[(String, Int)]): Option[(String,Int)] = {
  def createMPIV(baseMPV: Option[(String, Int)]): Map[String, Option[String]] = {
    val before = s""".*(.)${baseMPV.getOrElse(("None", 0))._1}.*""".r
    val after = s""".*${baseMPV.getOrElse(("None", 0))._1}(.).*""".r
    var beforeTallyMap = Map[Char, Int]()
    var afterTallyMap = Map[Char, Int]()
    var baseTallyMap = Map[Char, Int]()

    voteList.foreach { vote =>
      vote._1 match {
        case before(previousLetter) if baseMPV.nonEmpty =>
          val previousChar = previousLetter.charAt(0)
          if (beforeTallyMap.isDefinedAt(previousChar))
            beforeTallyMap = beforeTallyMap + (previousChar -> (beforeTallyMap(previousChar) + vote._2))
          else
            beforeTallyMap = beforeTallyMap + (previousChar -> vote._2)
        case _ =>
      }
      vote._1 match {
        case after(nextLetter) if baseMPV.nonEmpty =>
          val nextChar = nextLetter.charAt(0)
          if (afterTallyMap.isDefinedAt(nextChar))
            afterTallyMap = afterTallyMap + (nextChar -> (afterTallyMap(nextChar) + vote._2))
          else
            afterTallyMap = afterTallyMap + (nextChar -> vote._2)

        case _ =>
      }
    }

    if (baseMPV.isEmpty) {
      voteList.foreach { vote =>
        vote._1.toCharArray.foreach { char =>
          if (baseTallyMap.isDefinedAt(char))
            baseTallyMap = baseTallyMap + (char -> (baseTallyMap(char) + vote._2))
          else
            baseTallyMap = baseTallyMap + (char -> vote._2)
        }
      }
    }
    val beforeWinner = {
      if (beforeTallyMap.nonEmpty) Some(beforeTallyMap.toList.sortBy(x => x._2).reverse.head._1.toString) else None
    }
    val afterWinner = {
      if (afterTallyMap.nonEmpty) Some(afterTallyMap.toList.sortBy(x => x._2).reverse.head._1.toString) else None
    }
    val baseWinner =  if (baseTallyMap.nonEmpty) Some( baseTallyMap.toList.sortBy(x => x._2).reverse.head._1.toString) else None



    Map[String, Option[String]]("before" -> beforeWinner, "after" -> afterWinner, "base" -> baseWinner)
  }

  def createMPV(seedMPV: Option[(String,Int)]): Option[(String, Int)] = {

    val mpivWinners = createMPIV(seedMPV)
    println(s"seedMPV: $seedMPV, mpivWinners: $mpivWinners")

    val firstMPV = mpivWinners("base") match {
      case Some(vote) =>
        voteList.filter(_._1.contains(vote)).foldLeft(None: Option[(String, Int)])((r, c) =>
          if (c._2 > r.getOrElse(c)._2)
            Some(c)
          else
            Some(r.getOrElse(c))
        )
      case None =>
        None
    }

    val mpvWithSuffix = mpivWinners("after") match {
      case Some(vote) =>
        voteList.filter(_._1.contains(vote)).foldLeft(None: Option[(String, Int)])((r, c) =>
          if (c._2 > r.getOrElse(c)._2)
            Some(c)
          else
            Some(r.getOrElse(c))
        )
      case None =>
        None
    }



    val mpvWithPrefix = mpivWinners("before") match {
      case Some(vote) =>
        voteList.filter(_._1.contains(vote)).foldLeft(None: Option[(String, Int)])((r, c) =>
          if (c._2 > r.getOrElse(c)._2)
            Some(c)
          else
            Some(r.getOrElse(c))
        )
      case None =>
        None
    }


    println(s"Firstmpv: $firstMPV, mpvWithSuffix: $mpvWithSuffix, mpvWithPrefix: $mpvWithPrefix")

    List[Option[(String,Int)]](firstMPV, mpvWithPrefix, mpvWithSuffix, seedMPV).foldLeft(None:Option[(String,Int)]){ case (r, c) =>
      println(s"Comparing $c to $r")
      var winner = None:Option[(String,Int)]
        c match {
        case Some(vote) =>
          if(vote._2 >= r.getOrElse(vote)._2){
            println(s"Winner MPV: C (${Some(vote)})")
            winner = c}
          else{
            println(s"Winner MPV: None because the vote ($c) was not bigger than the previous MPV ($r)")
            winner = None}

        case None =>
            println(s"Winner MPV: R ($r) because the current element is None")
          winner = r
      }
      println(s"Returning winner ($winner) to the foldLeft iteration")
        winner
    }
  }

  var currentMPV = createMPV(None)

  if(currentMPV.nonEmpty) {
    println("Base MPV: " + currentMPV)
    //
    while (createMPV(currentMPV) != currentMPV) {
      println("Previous MPV: " + currentMPV)
          currentMPV = createMPV(currentMPV)
          println("Next MPV: " + currentMPV)
    }
  }

  println("Final Decision: " + currentMPV)
  currentMPV
}



println("The End")
