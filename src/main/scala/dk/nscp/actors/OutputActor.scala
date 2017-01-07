package dk.nscp.actors

import akka.actor.Actor
import dk.nscp.actors.ActorObjects._
import java.text.SimpleDateFormat

class OutputActor extends Actor {
  def receive = {
    case record: Record => println("Hello, output")
  }

}
