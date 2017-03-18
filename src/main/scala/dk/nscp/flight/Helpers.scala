package dk.nscp.flight

import scala.collection.JavaConversions._
import scala.util.{Try, Success, Failure}

import akka.actor.ActorRef
import com.typesafe.config.ConfigFactory
import net.liftweb.json._
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

import dk.nscp.flight.actors.ActorObjects._
import dk.nscp.flight.Implicits._
import dk.nscp.flight.request.RequestStructure._
import dk.nscp.flight.request.RequestEntry

object Helpers {

  def stringToDateTime(stringDate: String): DateTime = { //TODO: Can we delete?
    val split = stringDate.split("-")
    new DateTime(split(0).toInt, split(1).toInt, split(2).toInt, 0, 0)
  }

  def generateDateRange(defaultDate: String, numberOfDays: Int): Seq[DateTime] = { //TODO: Can we delete?
    val jodaDate = new DateTime(defaultDate)
    def helper(d: DateTime, dates: Seq[DateTime], daysRemaining: Int): Seq[DateTime] = {
      val newDates = dates :+ d
      if (daysRemaining == 0) newDates
      else {
        val nextDate = d.plusDays(1)

        helper(nextDate, newDates, daysRemaining - 1)
      }
    }
    val dateTimeDate = stringToDateTime(defaultDate)
    helper(dateTimeDate, scala.collection.immutable.Seq[DateTime](), numberOfDays)
  }

  /*
  def initializeRequests(departureDates: Seq[DateTime], returnDates: Seq[DateTime], origin: String, destination: String, timeSetActor: ActorRef): Unit = { //TODO: Can we delete?
    for (departureDate <- departureDates) {
      for (returnDate <- returnDates) {
        timeSetActor ! SASRequest("CPH", "TKY", departureDate, returnDate)
      }
    }
  }
  */

  def requestsFromConfig(configFile: String, configValue: String): Seq[Request] = {
    val requestsConf = ConfigFactory.load(configFile)
    val rawListOfEntries = requestsConf.getStringList(configValue).toList
    rawListOfEntries.map(entry =>
      RequestEntry.fromString(entry.toString)
    ).filter(entry => 
      entry.isSuccess
    ).map(entry => 
      entry.get
    ).flatMap(entry =>
      entry.generateRequests
    )

  }

}
