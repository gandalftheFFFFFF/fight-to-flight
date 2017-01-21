import org.scalatest._
import com.typesafe.config._

class ConfigTest extends FlatSpec with Matchers {

  val conf = ConfigFactory.load()

  "Config" should "return valid values" in {
    val outdate = conf.getString("outdate")
    outdate should be ("2017-04-15")
  }
}
