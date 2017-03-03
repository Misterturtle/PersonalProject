package tph

import FileReaders.LogFileReader

/**
  * Created by Harambe on 2/22/2017.
  */
object Main extends App {

  val theBrain = new TheBrain()
  //Reads hearthstone's output_log.txt file and filters known HSActions to actionLog.txt
  val logFileReader = new LogFileReader()
  logFileReader.poll()
}
