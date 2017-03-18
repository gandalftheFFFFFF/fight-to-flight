package dk.nscp.flight.actors

import akka.actor.Actor
import com.typesafe.scalalogging._

import dk.nscp.flight.actors.ActorObjects._
import dk.nscp.flight.request.RequestStructure._

class RequestActor extends Actor {

  val logger = Logger(classOf[RequestActor])

  def receive = {
    case request: Request => 
      val response = request.execute
      response match {
        case Some(record) => println(record)
        case None => println("Aww...")
      }
  }
}
