package VoteSystem

/**
  * Created by Harambe on 3/30/2017.
  */
class VoteState {

  var voterMap = Map[String, Voter]()
  var voterHistory = List[Map[String,Voter]]()

  def averageVotersFromHistory:Int = voterHistory.foldLeft(0){case (r,c) => r + c.size}/voterHistory.size
}
