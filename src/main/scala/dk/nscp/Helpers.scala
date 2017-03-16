package dk.nscp

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import akka.actor.ActorRef
import dk.nscp.actors.ActorObjects._
import dk.nscp.actors.SASRequest
import net.liftweb.json._
import scala.util.{Try, Success, Failure}
import com.typesafe.config.ConfigFactory
import scala.collection.JavaConversions._

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
        timeSetActor ! SASRequest("CPH", "TKY", departureDate, returnDate)
      }
    }
  }

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

  implicit class SDateTime(dt: DateTime) {
    def plusRange(days: Int): Seq[DateTime] = {
      def loop(days: Int, result: Seq[DateTime]): Seq[DateTime] = {
        if (days == 0) dt +: result
        else loop(days - 1, dt.plusDays(days) +: result)
      }
      loop(days, Seq())
    }
  }
    

  
      
      




}
