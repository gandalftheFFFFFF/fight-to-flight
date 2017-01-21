package dk.nscp

import org.joda.time.DateTime
import akka.actor.ActorRef
import dk.nscp.actors.ActorObjects._
import net.liftweb.json._

object Helpers {

  implicit class RichJObject(jObject: JValue) {
    def has(childString: String): Boolean = { 
      (jObject \ childString) != JNothing
    }
  }

  implicit class SDuration(jd: java.time.Duration) {
    def toMinutes: scala.concurrent.duration.Duration = {
      scala.concurrent.duration.Duration(jd.getSeconds / 60, "minutes")
    }
  }

  def stringToDateTime(stringDate: String): DateTime = {
    val split = stringDate.split("-")
    new DateTime(split(0).toInt, split(1).toInt, split(2).toInt, 0, 0)
  }

  def generateDateRange(defaultDate: String, numberOfDays: Int): Seq[DateTime] = {
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

  def initializeRequests(departureDates: Seq[DateTime], returnDates: Seq[DateTime], origin: String, destination: String, timeSetActor: ActorRef): Unit = {
    for (departureDate <- departureDates) {
      for (returnDate <- returnDates) {
        timeSetActor ! CheckFlight(departureDate, returnDate, origin, destination)
      }
    }
  }



}
