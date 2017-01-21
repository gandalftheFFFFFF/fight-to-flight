package dk.nscp

import scalaj.http._
import org.joda.time.DateTime
import dk.nscp.actors.ActorObjects._
import dk.nscp.actors.{OutputActor, RequestActor, RequestRouterActor, TimeTableActor}
import akka.actor.{Actor, Props, ActorSystem}
import scala.concurrent.duration._
import com.typesafe.scalalogging._
import com.typesafe.config.ConfigFactory

object Runner extends App {

  val conf = ConfigFactory.load()

  val logger = Logger("Runner")

  val system = ActorSystem("flightSystem")

  val outputter = system.actorOf(Props[OutputActor], name = "outputactor")
  val timeSetActor = system.actorOf(Props[TimeTableActor], name = "timesetactor")
  val requestRouterActor = system.actorOf(Props[RequestRouterActor], name = "routingactor")

  val departureDates = Helpers.generateDateRange(conf.getString("outdate"), conf.getInt("outdate-plusminus"))
  val returnDates = Helpers.generateDateRange(conf.getString("indate"), conf.getInt("indate-plusminus"))

  Helpers.initializeRequests(departureDates, returnDates, conf.getString("from"), conf.getString("to"), timeSetActor)

  import system.dispatcher
  
  // implicit class to convert java.time.Duration to scala.concurrent.duration.Duration minutes
  import Helpers.SDuration 
  
  val refreshTimeSet = system.scheduler.schedule(0 milliseconds, conf.getDuration("check-interval").toMinutes minutes, timeSetActor, RefreshRecords)
}

