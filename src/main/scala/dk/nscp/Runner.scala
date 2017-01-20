package dk.nscp

import scalaj.http._
import net.liftweb.json._
import net.liftweb.json.DefaultFormats
import java.util.Date
import java.util.Calendar
import org.joda.time.DateTime
import java.text.SimpleDateFormat
import dk.nscp.actors.ActorObjects._
import dk.nscp.actors.{OutputActor, RequestActor, RequestRouterActor, TimeTableActor}
import akka.actor.{Actor, Props, ActorSystem}
import scala.concurrent.duration._
import com.typesafe.scalalogging._

object Runner extends App {

  val logger = Logger("Runner")

  val system = ActorSystem("flightSystem")

  val outputter = system.actorOf(Props[OutputActor], name = "outputactor")
  val timeSetActor = system.actorOf(Props[TimeTableActor], name = "timesetactor")
  val requestRouterActor = system.actorOf(Props[RequestRouterActor], name = "routingactor")

  def generateDateRange(defaultDate: DateTime, numberOfDays: Int): Seq[DateTime] = {
    val jodaDate = new DateTime(defaultDate)
    def helper(d: DateTime, dates: Seq[DateTime], daysRemaining: Int): Seq[DateTime] = {
      val newDates = dates :+ d
      if (daysRemaining == 0) newDates
      else {
        val nextDate = d.plusDays(1)

        helper(nextDate, newDates, daysRemaining - 1)
      }
    }
    helper(defaultDate, scala.collection.immutable.Seq[DateTime](), numberOfDays)
  }

  def initializeRequests(departureDates: Seq[DateTime], returnDates: Seq[DateTime], origin: String, destination: String): Unit = {
    for (departureDate <- departureDates) {
      for (returnDate <- returnDates) {
        timeSetActor ! CheckFlight(departureDate, returnDate, origin, destination)
      }
    }
  }

  val departureDates = generateDateRange(new DateTime(2017, 4, 15, 0, 0), 4)
  val returnDates = generateDateRange(new DateTime(2017, 4, 20, 0, 0), 4)

  initializeRequests(departureDates, returnDates, "CPH", "TYO")

  import system.dispatcher
  val refreshTimeSet = system.scheduler.schedule(0 milliseconds, 30 minutes, timeSetActor, RefreshRecords)
}

