package dk.nscp.actors

import java.util.Date
import org.joda.time.DateTime

object ActorObjects {

  // case class that describes a pair of tickets
  case class Record(
    searchTime: DateTime,
    totalPrice: Float,
    outPrice: Float,
    inPrice: Float,
    outTime: DateTime,
    inTime: DateTime
  )

  case class CheckFlight(
    outDate: DateTime,
    inDate: DateTime,
    from: String,
    to: String
  )

}
