package dk.nscp.actors

import akka.actor.Actor
import dk.nscp.actors.ActorObjects._
import com.typesafe.scalalogging._

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
