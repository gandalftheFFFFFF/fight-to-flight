package dk.nscp.flight

import scala.concurrent.duration._
import scala.collection.JavaConversions._

import akka.actor.{Actor, Props, ActorSystem}
import com.typesafe.scalalogging._
import com.typesafe.config.ConfigFactory
import org.joda.time.DateTime
import scalaj.http._

import dk.nscp.flight.actors.ActorObjects._
import dk.nscp.flight.actors.{OutputActor, RequestActor, RequestRouterActor, TimeTableActor, ControlActor}

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
  import dk.nscp.flight.Implicits._

  val requests = Helpers.requestsFromConfig("requests.conf", "requests")

  // Prepare ControlActor
  controlActor ! Prepare(requests)
  controlActor ! RefreshRecords

  // val refreshTimeSet = system.scheduler.schedule(0 milliseconds, conf.getDuration("check-interval").toMinutes minutes, timeSetActor, RefreshRecords)
}

