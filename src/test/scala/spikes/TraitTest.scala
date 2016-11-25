package spikes

/**
  * Created by Harambe on 10/29/2016.
  */
class TraitTest {

  class Vote(voteCode:Int){

    val testVoteCode = voteCode*2

  }

  trait Command{
    val target = -5
      //Possibly create a Board Class?
    val spot = -5
  }


  class ActionVote(command:Command) extends Vote(-5){

    val name =



  }



  class EmojiVote extends Vote{


  }





}
