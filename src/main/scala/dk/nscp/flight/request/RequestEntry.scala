package dk.nscp.flight.request

import scala.util.{Try, Success, Failure}

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

import dk.nscp.flight.actors.ActorObjects._
import dk.nscp.flight.request.RequestStructure._
import dk.nscp.flight.Helpers._
import dk.nscp.flight.Implicits._

case class RequestEntry(
  airline: String,
  origin: String,
  destination: String,
  inTime: DateTime,
  outTime: DateTime,
  variableDays: Int
) {
    def generateRequests: Seq[Request] = {
      val inDates = inTime.plusRange(variableDays)
      val outDates = outTime.plusRange(variableDays)
      inDates.flatMap(ins =>
        outDates.map(outs =>
          airline match {
            case "SAS" => SASRequest(origin, destination, ins, outs)
          }
        )
      )
    }
  }

object RequestEntry {
  def fromString(csv: String): Try[RequestEntry] = {
    val dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd")
    val values = csv.split(",")
    Try {
      new RequestEntry(
        values(0),
        values(1),
        values(2),
        dateTimeFormatter.parseDateTime(values(3)),
        dateTimeFormatter.parseDateTime(values(4)),
        values(5).toInt
      )
    }
  }
}
