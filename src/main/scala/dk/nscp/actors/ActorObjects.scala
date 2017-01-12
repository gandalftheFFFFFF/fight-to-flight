package dk.nscp.actors

import java.util.Date
import org.joda.time.DateTime

object ActorObjects {

  case object RefreshRecords

  // case class that describes a pair of tickets
  // TODO: Add from and to
  case class Record(
    searchTime: DateTime,
    totalPrice: Float,
    outPrice: Float,
    inPrice: Float,
    outTime: DateTime,
    inTime: DateTime
  ) {
    def uniqueKey: (DateTime, DateTime) = (outTime, inTime)
    def csvFormat: String = s"$searchTime,$totalPrice,$outPrice,$inPrice,$outTime,$inTime"
  }

  case class CheckFlight(
    outDate: DateTime,
    inDate: DateTime,
    from: String,
    to: String
  )

}
