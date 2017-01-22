package dk.nscp.actors

import akka.actor.Actor
import dk.nscp.actors.ActorObjects._
import java.text.SimpleDateFormat
import java.io.FileWriter
import java.io.File
import com.typesafe.config.ConfigFactory

class OutputActor extends Actor {

  val conf = ConfigFactory.load()

  val outputDir = "output"

  def nextFileNumber: Int = {
    val regex = """(output\d*.csv)""".r
    ((new File(outputDir))
      .listFiles
      .map(_.getName)
      .filter(file => regex.findFirstIn(file).isDefined)
      .length)
  }

  def receive = {
    case record: Record => 
      // print to output file
      val fw = new FileWriter("output/output.csv", true)
      fw.write(record.csvFormat + "\n")
      fw.close()
      checkFiles
  }

  def checkFiles: Unit = {
    val oldFile = new File(outputDir + "/output.csv")
    if (oldFile.length > conf.getInt("max-filesize-bytes")) {
      val asArchiveFile = oldFile.renameTo(new File(s"$outputDir/output$nextFileNumber.csv"))
    }
  }



}
