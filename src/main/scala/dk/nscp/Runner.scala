package dk.nscp

import scalaj.http._
import org.joda.time.DateTime
import dk.nscp.actors.ActorObjects._
import dk.nscp.actors.{OutputActor, RequestActor, RequestRouterActor, TimeTableActor, ControlActor}
import akka.actor.{Actor, Props, ActorSystem}
import scala.concurrent.duration._
import com.typesafe.scalalogging._
import com.typesafe.config.ConfigFactory
import scala.collection.JavaConversions._

object Runner extends App {

  val conf = ConfigFactory.load()

  val logger = Logger("Runner")

  val system = ActorSystem("flightSystem")

  val outputter = system.actorOf(Props[OutputActor], name = "outputactor")
  val timeSetActor = system.actorOf(Props[TimeTableActor], name = "timesetactor")
  val requestRouterActor = system.actorOf(Props[RequestRouterActor], name = "routingactor")
  val controlActor = system.actorOf(Props[ControlActor], name = "controlActor")

  // Helpers.initializeRequests(departureDates, returnDates, conf.getString("from"), conf.getString("to"), timeSetActor)

  import system.dispatcher
  
  // implicit class to convert java.time.Duration to scala.concurrent.duration.Duration minutes
  import Helpers.SDuration 

  val requests = Helpers.requestsFromConfig("requests.conf", "requests")

  // Prepare ControlActor
  controlActor ! Prepare(requests)
  controlActor ! RefreshRecords

  // val refreshTimeSet = system.scheduler.schedule(0 milliseconds, conf.getDuration("check-interval").toMinutes minutes, timeSetActor, RefreshRecords)
}

