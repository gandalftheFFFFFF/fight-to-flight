package dk.nscp.actors

import akka.actor.Actor
import akka.actor.Props
import akka.routing.{ActorRefRoutee, RoundRobinRoutingLogic, Router}
import dk.nscp.actors.ActorObjects._

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
