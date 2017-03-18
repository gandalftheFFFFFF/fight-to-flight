package dk.nscp.flight

import org.joda.time.DateTime
import net.liftweb.json._

object Implicits {

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

