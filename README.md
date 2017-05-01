# TwitchPlaysHearthstone

Language used:
-Scala 2.11.8

Build Tool:
-SBT

Contact Email:
-Ryan@conaway58.com

How To Run:
-Just to see tests, make sure sbt is installed, navigate to the root of the project where built.sbt is located.
- Type: "sbt test"

-In order to actually use the program, extra steps are required, such as downloading hearthstone and setting up the output_log.txt file.
-If interested, contact me at Ryan@conaway58.com for details.
-It does currently work, bugs and some minor functionality excluded.

Breif explanation of required knowledge:
- Twitch.tv: A popular video game streaming service that allows anyone to livestream their desktop or game console while playing a game.
- Twitch.tv Chat: A large part of twitch.tv is the chat associated with each livestream. It allows the twitch community to interact with the stream.
- Hearthstone: A popular card based video game made by Blizzard.
- Outputlog.txt: A debug log provided by blizzard that outputs most in game actions to a text file.

Purpose of my program:
- Read and parse hearthstone's outputlog.txt into data to simulate the realtime status of a hearthstone game session.
- Stream hearthstone.
- Connect to twitch IRC Chat in order to receive string-based commands.
- Store these commands as votes by each individual viewer that is participating.
- At certain times, calculate the most popular vote (command).
- Execute this vote by automate clicking certain locations on the screen.


Current State of project:
- I've put a pause on the project in order to read "Functional Programming in Scala" by Paul Chiusano and Rúnar Bjarnason
- When I start working again, I am going to change the way the LogFileReader creates the GameState (See below) and also how decision are made.
- All tests are passing except the latest test that is starting to change the LogFileReader.
- I wouldn't claim it to be perfectly clean code by any means, it's been a personal project that I sometimes have strayed from good practices.
- Overall though, it has tests around most all code and looks drastically better than the first couple revisions.


History:
- This has been an ongoing project for me for quite a while. I've mainly been using it as a project that expands my programming knowledge.
- I've restarted a few times as I've learned better ways to implement code and developed good test practices.


Explanation of internal structure:

- LogFileReader: A class responsible for reading and parsing data from the outputlog.txt.
- HSAction: A data object containing case classes of actions to be performed on GameState that originated from the LogFileReader.
- GameState: A data structure that contains the status of the game.
             - Contains 2 players that each contain essential information such as which cards are on board or in hand.
      
- IRCState: A class responsible for knowing WHEN to execute certain actions that interact with the IRC Chat in some way.
- IRCBot: A PIRCBot (external library) that connects to IRC Chat, receives, and begins to store commands from viewers.
- VoteManager: A class responsible for the logic of retrieving or manipulating votes. All votes are entered through this class.
- Voter: A data structure that contains votes for each individual viewer participating.
- VoteAI: A class responsible for finding the most popular vote patterns and making a decision on which votes to execute.
- Hearthstone: A class responsible for physically interacting with hearthstone. All pixel click locations are stored and executed here.




