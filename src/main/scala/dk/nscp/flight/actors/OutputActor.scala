package dk.nscp.flight.actors

import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat

import akka.actor.Actor
import com.typesafe.config.ConfigFactory

import dk.nscp.flight.actors.ActorObjects._

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
