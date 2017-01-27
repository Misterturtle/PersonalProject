package tph

/**
  * Created by Harambe on 1/24/2017.
  */
class FrozenGameStatus(oldGameStatus: Array[Player]) {


  def GetFrozenPlayers(): Array[FrozenPlayer] = {

    val frozenGameStatus = new Array[FrozenPlayer](2)
    frozenGameStatus(0) = new FrozenPlayer(oldGameStatus(0))
    frozenGameStatus(1) = new FrozenPlayer(oldGameStatus(1))

    return frozenGameStatus
  }

}
