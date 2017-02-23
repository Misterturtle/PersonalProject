package tph

import java.io._
import java.util.concurrent.{ScheduledThreadPoolExecutor, TimeUnit}

import com.typesafe.config.ConfigFactory
import tph.HSAction.HSActionUninit

/**
  * Created by Harambe on 2/22/2017.
  */
class LogFileReader() {

  val config = ConfigFactory.load()
  val defaultFile = new File(config.getString("tph.outputLog.path"))
  val scheduler = new ScheduledThreadPoolExecutor(1)
  val pollRunnable = new Runnable {
    def run() = poll()
  }


  def poll(): Unit = {

    val reader = new BufferedReader(new FileReader(defaultFile))

    val actionLogFile = new File(config.getString("tph.actionLog.path"))
    val writer = new PrintWriter(new FileWriter(actionLogFile))

    while (reader.ready()) {
      val line = reader.readLine()
      if (new LogParser().IdentifyHSAction(line) != HSActionUninit()) {
        writer.println(line)
        writer.flush()
      }
    }
    scheduler.schedule(pollRunnable, 100, TimeUnit.MILLISECONDS)
  }
}
