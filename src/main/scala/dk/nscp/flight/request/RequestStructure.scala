package dk.nscp.flight.request

import org.joda.time.DateTime

import dk.nscp.flight.actors.ActorObjects._

object RequestStructure {

  abstract trait Request {
    
    val origin: String
    val destination: String
    val outTime: DateTime
    val inTime: DateTime
    
    def execute: Option[Record]
    def airline: String
    def airlineReadable: String

  }

  case class NAXRequest(
    origin: String,
    destination: String,
    inTime: DateTime,
    outTime: DateTime
  ) extends Request {
    def execute: Option[Record] = None
    def airline = "NAX"
    def airlineReadable = "Norwegian Air Shuttle"
  }

}
