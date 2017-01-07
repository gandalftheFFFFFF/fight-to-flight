package dk.nscp

import scalaj.http._
import net.liftweb.json._
import net.liftweb.json.DefaultFormats
import java.util.Date
import java.util.Calendar
import org.joda.time.DateTime
import java.text.SimpleDateFormat
import dk.nscp.actors.ActorObjects._
import dk.nscp.actors.{OutputActor, RequestActor}
import akka.actor.{Actor, Props, ActorSystem}

object Runner extends App {
  val system = ActorSystem("flightSystem")

  val outputter = system.actorOf(Props[OutputActor], name = "outputactor")
  val checker = system.actorOf(Props[RequestActor], name = "checker")

  def generateDateRange(defaultDate: DateTime, numberOfDays: Int): Seq[DateTime] = {
    val jodaDate = new DateTime(defaultDate)
    def helper(d: DateTime, dates: Seq[DateTime], daysRemaining: Int): Seq[DateTime] = {
      if (daysRemaining == 0) dates
      else {
        val nextDate = d.plusDays(1)

        helper(nextDate, dates :+ d, daysRemaining - 1)
      }
    }
    helper(defaultDate, Seq(), numberOfDays)
  }

  val departureDates = generateDateRange(new DateTime(2017, 7, 1, 0, 0), 7)
  val returnDates = generateDateRange(new DateTime(2017, 7, 7, 0, 0), 7)


  println(s"Departure dates: $departureDates")
  println(s"Return dates: $returnDates")

  val outDate = departureDates(0)
  val inDate = returnDates(0)

  checker ! CheckFlight(outDate, inDate, "CPH", "ALC")
}
