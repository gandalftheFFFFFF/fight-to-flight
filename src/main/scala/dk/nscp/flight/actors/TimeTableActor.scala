package dk.nscp.flight.actors

import scala.concurrent.duration._

import akka.actor.{Actor, Props}
import org.joda.time.{DateTime, Duration}

import dk.nscp.flight.actors.ActorObjects._
import dk.nscp.flight.request.RequestStructure._

class TimeTableActor extends Actor {

  val requestRouterActor = context.actorSelection("/user/routingactor")
  val outputActor = context.actorSelection("/user/outputactor")

  def receive = {
    case request: Request =>
      requestRouterActor ! request
    case record: Record =>
      outputActor ! record
    case check: CheckFlight =>
      requestRouterActor ! check
  }
}

