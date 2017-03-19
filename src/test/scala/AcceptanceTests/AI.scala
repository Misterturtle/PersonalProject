package AcceptanceTests

import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by Harambe on 3/15/2017.
  */
class AI extends FlatSpec with Matchers {

  //todo As the AI, I need to be able to let people cast the same vote only twice, because of windfury minions.
  //todo As the AI, I need to be able to only give vote power to the second vote if the first was already used, because you almost never use a windfury once.
  //todo As the AI, I need to be able to identify "or patterns", because a decision with multiple options only executes one.
  //todo As the AI, I need to be able to give more vote power to votes that are immediately after the previous decision, because order of cards play is important.
  //todo As the AI, I need to be able to identify "enhancement combos", because after you buff a minion you normally want to do something with it.
  //todo As the AI, I need to be able to increase the vote power of the buff card in a detected "enhancement combo", because the buff card should come first.
  //todo As the AI, I need to be able to decrease the vote power of the minion action in a detected "enhancement combo", because you don't want to use the minion before the buff.
  //todo As the AI, I need to be able to identify "heal combos", because order is particularly important for "heal combos".
  //todo As the AI, I need to be able to be very accurate on the number of actions to execute, because you don't always want to play every available option.
  //todo As the AI, I need to be able to identify "charge combos", because you almost never want to ignore charge if available.
  //todo *** As the AI, I need to be able to identify which card will cause future dependencies, so I decide to begin partial execution.
  //todo *** As the AI, I need to be able to give each vote appropriate properties based on the card it is referring to, so that I can be more intelligent.
  //---------Timing---------//
  //todo As the AI, I need to be able to give a warning before preemptively executing a vote, so that slow users can send their commands.
  //todo As the AI, I need to be able to decide that a preemptive decision should not be executed, because slow users should be taken into consideration.
  //todo As the AI, I need to be able to give a warning before executing an "end turn vote series", so that slow users can prevent me from ending the turn if not appropriate.
  //todo As the AI, I need to be able to decide that an "end turn vote series" should not be executed, because slow users should be taken into consideration.
  //todo As the AI, I need to know the maximum amount of time left in a turn, so I don't let time run out.
  //todo As the AI, I need to know roughly how long my execution will take, so that I don't let time run out.
  //todo As the AI, I need to know when the dragon card that reduces time is played, so that I can behave quite differently.

}
