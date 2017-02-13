package tph

/**
  * Created by Harambe on 1/24/2017.
  */
class FrozenGameStatus(oldGameStatus: Array[Player]) {


    val frozenPlayers = new Array[FrozenPlayer](2)
    frozenPlayers(0) = new FrozenPlayer(oldGameStatus(0))
    frozenPlayers(1) = new FrozenPlayer(oldGameStatus(1))


}
