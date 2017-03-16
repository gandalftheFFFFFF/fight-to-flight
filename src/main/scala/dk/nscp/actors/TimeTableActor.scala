package dk.nscp.actors

import akka.actor.Actor
import akka.actor.Props
import org.joda.time.{DateTime, Duration}
import scala.concurrent.duration._
import dk.nscp.actors.ActorObjects._

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
