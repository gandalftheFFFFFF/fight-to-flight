package dk.nscp.actors

import akka.actor.Actor
import akka.actor.Props
import org.joda.time.{DateTime, Duration}
import scala.concurrent.duration._
import dk.nscp.actors.ActorObjects._

class TimeTableActor extends Actor {

  var timeSet = scala.collection.mutable.Set[Record]()
  var timeMap = scala.collection.mutable.Map[(DateTime, DateTime), Record]()

  val requestRouterActor = context.actorSelection("/user/routingactor")
  val outputActor = context.actorSelection("/user/outputactor")

  def receive = {
    case record: Record =>
      val oldValue = timeMap.get(record.uniqueKey)
      val newValue = record
      // If the newValue.totalPrice is lower, the fligh is cheaper for
      // the same dates, and we should notify someone!
      oldValue match {
        case Some(record) =>
          if (newValue.totalPrice < record.totalPrice) {
            println("time to let someone know!")
            timeMap += (newValue.uniqueKey -> newValue)
          }
        case None =>
          timeMap += (newValue.uniqueKey -> newValue)
      }
      outputActor ! record
    case check: CheckFlight =>
      requestRouterActor ! check
    case RefreshRecords =>
      timeMap.foreach(
        pair => {
          val record = pair._2
          val now = (new DateTime())
          val searchTime = record.searchTime
          val diff = (new Duration(searchTime, now)).getStandardMinutes
          if (diff > 5) {
            requestRouterActor ! CheckFlight(record.outTime, record.inTime, "CPH", "TYO")
          }
        }
      )
  }
}
