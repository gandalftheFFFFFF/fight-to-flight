package dk.nscp.flight.actors

import akka.actor.{Actor, Props}
import akka.routing.{ActorRefRoutee, RoundRobinRoutingLogic, Router}

import dk.nscp.flight.actors.ActorObjects._
import dk.nscp.flight.request.RequestStructure._

class RequestRouterActor extends Actor {

  var router = {
    val routees = Vector.fill(3) {
      val r = context.actorOf(Props[RequestActor])
      context watch r
      ActorRefRoutee(r)
    }
    Router(RoundRobinRoutingLogic(), routees)
  }

  def receive = {
    case request: Request =>
      router.route(request, sender())
  }
}
