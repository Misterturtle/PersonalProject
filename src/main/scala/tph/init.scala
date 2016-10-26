package tph

import java.io.{InputStreamReader, BufferedReader, File}

/**
  * Created by Harambe on 10/26/2016.
  */
object init extends App {

  val dllFile = new File("resources/AutoItX3_x64.dll")
  val dir = dllFile.getAbsolutePath


  val registerDLL = new ProcessBuilder("cmd.exe", "/C cd src/main/resources & ")
  registerDLL.redirectErrorStream(true)

  val p = registerDLL.start()
  val errCode = p.waitFor()

  val r = new BufferedReader(new InputStreamReader(p.getInputStream))



  while (r.readLine() != null) {
    val line = r.readLine()
    if (line == null) { scala.util.control.Breaks.break }
    System.out.println(line)
  }


}
