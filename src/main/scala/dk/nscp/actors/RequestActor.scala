package dk.nscp.actors

import akka.actor.Actor
import dk.nscp.actors.ActorObjects._
import scalaj.http._
import net.liftweb.json._
import net.liftweb.json.DefaultFormats
import java.text.SimpleDateFormat
import org.joda.time.DateTime
import com.typesafe.scalalogging._
import dk.nscp.Helpers

class RequestActor extends Actor {

  val logger = Logger(classOf[RequestActor])

  def receive = {
    case checkFlight: CheckFlight => 
      getFlightDetails(checkFlight) match {
        case None => logger.debug(s"$self: got None response on checking flight $checkFlight")
        case Some(record) =>
          sender() ! record
      }
  }

  def getFlightDetails(check: CheckFlight): Option[Record] = {
    implicit val defaultFormats = DefaultFormats
    val searchTime = new DateTime()

    // Make sure request date format is yyyyMMdd'0000'
    val dateFormatter = org.joda.time.format.DateTimeFormat.forPattern("yyyyMMdd'0000'")

    val outDate = dateFormatter.print(new DateTime(check.outDate))
    val inDate = dateFormatter.print(new DateTime(check.inDate))
    val from = check.from
    val to = check.to
    
    val tokenRequest = (Http("https://www.sas.dk/bin/sas/d360/getOauthToken")
    ).postData("").timeout(connTimeoutMs = 5000, readTimeoutMs = 5000)
    
    val tokenResponse = tokenRequest.asString
    
    val authToken = (parse(tokenResponse.body) \ "access_token").extract[String]
  
    val flightRequest = (Http(s"https://api.flysas.com/offers/flightproducts?outDate=$outDate&inDate=$inDate&adt=1&bookingFlow=REVENUE&lng=GB&pos=DK&from=$from&to=$to&channel=web")
      .headers("Authorization" -> authToken)
      .timeout(connTimeoutMs = 5000, readTimeoutMs = 5000)
    )

    val flightResponse = flightRequest.asString

    if (flightResponse.code == 200) {

      val jsonResult = parse(flightResponse.body)

      logger.debug(pretty(render(jsonResult)))

      // implicit class with a "has" method on JObject
      import Helpers.RichJObject
      if (jsonResult.has("errors")) {
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
