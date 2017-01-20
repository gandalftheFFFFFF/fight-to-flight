package dk.nscp.actors

import akka.actor.Actor
import dk.nscp.actors.ActorObjects._
import java.text.SimpleDateFormat
import java.io.FileWriter

class OutputActor extends Actor {
  def receive = {
    case record: Record => 
      // print to output file
      val fw = new FileWriter("output/output.csv", true)
      fw.write(record.csvFormat + "\n")
      fw.close()
  }

}
