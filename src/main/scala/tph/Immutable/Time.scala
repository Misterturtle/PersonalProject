package tph.Immutable

/**
  * Created by Harambe on 2/17/2017.
  */
trait Time {

  //Any user input has to be define by a Time


  // Or maybe, every gameState creation must extend Time
  // That way, whenever a variable outside the system
  // For example: something Abstract: GameManipulator
  // //For Example: another Time instance: VoteManager - VoteManager will act similar to GameState and
  // have a log of votes stamped with a certain Time marker) references any variable inside the system (gameState),

  //Exciting Side Note: Maybe this is a way to address the problem of cards changing while the 45 sec decide time is going.
  //The idea is that there will be a TimeStamp that stamps all votes within that second with a unique TimeStamp.

  //-----------------------------------> In Regards To How People Will Control Decision Time <----------------------------------------//

  //Now I can control WHEN then program executes a decision.
  //Maybe it will start with a set value and then people can adjust it with commands such as !TooFast or !TooSlow   ------> Maybe check for input for 5 seconds after making a decision. (Which we can refer to by TimeStamps muahhaha)


  //-----------------------------------> In Regards To How Time Affects Votes <------------------------------------//
  //                            ***BrainStorm***
  //Essentially, the point is that with TimeStamps we can calculate the relevance? of a vote?
  //We can say ok, its decision time, what votes were just cast within the last second?
  //Well, those votes are not a big enough sample to make an accurate decision.
  //What votes were entered 2 seconds again?
  //


  //Recent Votes: Worth More
  //Old Votes: Worth Less
  //


  //Votes that are cast at the same time:

  // Possibly even different Time

}
