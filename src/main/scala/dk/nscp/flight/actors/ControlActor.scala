package dk.nscp.flight.actors

import scala.concurrent.duration._

import akka.actor.{Actor, Props}
import org.joda.time.{DateTime, Duration}

import dk.nscp.flight.actors.ActorObjects._
import dk.nscp.flight.request.RequestStructure._

class ControlActor extends Actor {
  val requestRouterActor = context.actorSelection("/user/routingactor") //TODO: Rename routing actor to be more consistent
  val outputActor = context.actorSelection("/user/outputactor") //TODO: As above

  var requests = Seq(): Seq[Request]

  def receive = {
    case Prepare(initialRequests) =>
      this.requests = initialRequests
    case RefreshRecords =>
      for (request <- this.requests)
        requestRouterActor ! request
  }
}

