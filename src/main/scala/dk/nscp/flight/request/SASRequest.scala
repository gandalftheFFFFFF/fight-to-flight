package dk.nscp.flight.request

import java.text.SimpleDateFormat

import akka.actor.Actor
import com.typesafe.scalalogging._
import scalaj.http._
import net.liftweb.json._
import net.liftweb.json.DefaultFormats
import org.joda.time.DateTime

import dk.nscp.flight.actors.ActorObjects._
import dk.nscp.flight.request.RequestStructure._
import dk.nscp.flight.Helpers

case class SASRequest(
  origin: String,
  destination: String,
  outTime: DateTime,
  inTime: DateTime
) extends Request {
  
  def airline: String = "SAS"
  
  def airlineReadable: String = "Scandinavian Airlines"
  
  def execute: Option[Record] = {
    implicit val defaultFormats = DefaultFormats
    val searchTime = new DateTime()

    // Make sure request date format is yyyyMMdd'0000'
    val dateFormatter = org.joda.time.format.DateTimeFormat.forPattern("yyyyMMdd'0000'")

    val outDate = dateFormatter.print(new DateTime(this.outTime))
    val inDate = dateFormatter.print(new DateTime(this.inTime))
    val from = this.origin
    val to = this.destination
    
    val tokenRequest = (Http("https://www.sas.dk/bin/sas/d360/getOauthToken")
    ).postData("").timeout(connTimeoutMs = 5000, readTimeoutMs = 5000)
    
    val tokenResponse = tokenRequest.asString
    
    val authToken = (parse(tokenResponse.body) \ "access_token").extract[String]

    val url = s"https://api.flysas.com/offers/flightproducts?outDate=$outDate&inDate=$inDate&adt=1&bookingFlow=REVENUE&lng=GB&pos=DK&from=$from&to=$to"
  
    val flightRequest = (Http(url)
      .headers("Authorization" -> authToken)
      .timeout(connTimeoutMs = 5000, readTimeoutMs = 5000)
    )

    val flightResponse = flightRequest.asString

    if (flightResponse.code == 200) {

      val jsonResult = parse(flightResponse.body)

      // implicit class with a "has" method on JObject
      import dk.nscp.flight.Implicits._
      if (jsonResult.has("errors")) {
        println(jsonResult)
        None
      } else {

        // outbound is going
        // inbound is returning
        val outbound = jsonResult \ "outboundFlightProducts"
        val inbound = jsonResult \ "inboundFlightProducts"

        val outboundFlights = (jsonResult \ "outboundFlights")(0)

        val inboundFlights = (jsonResult \ "inboundFlights")(0)

        val outTimeJson = outboundFlights \ "startTimeInLocal"
        val inTimeJson = inboundFlights \ "endTimeInLocal"

        val outTimeString = outTimeJson.extract[String]
        val inTimeString = inTimeJson.extract[String]

        val outTime = new DateTime(outTimeString)
        val inTime = new DateTime(inTimeString)

        val outPrice = (outbound(0) \ "price" \ "totalPrice").extract[Float]
        val inPrice = (inbound(0) \ "price" \ "totalPrice").extract[Float]

        val totalPrice = outPrice + inPrice

        Some(Record(searchTime, totalPrice, outPrice, inPrice, outTime, inTime))
      }
    } else {
      None
    }
  }

}
