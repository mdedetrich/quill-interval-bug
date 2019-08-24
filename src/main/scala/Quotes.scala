import java.time.OffsetDateTime

import io.getquill.context.jdbc.PostgresJdbcContextBase
import io.getquill.{NamingStrategy, PostgresMonixJdbcContext, SnakeCase}
import org.flywaydb.core.Flyway

trait Quotes[N <: NamingStrategy] extends PostgresEncodings {

  val ctx: PostgresJdbcContextBase[N]

  import ctx._

  private def nowFromMinutes = quote { minutes: Long =>
    infix"INTERVAL '$minutes minutes'".as[OffsetDateTime]
  }

  def clearQuote = quote {
    query[TestTable].delete
  }

  def insertQuote(timestamp: OffsetDateTime) = quote {
    query[TestTable].insert(_.timestamp -> liftScalar(timestamp))
  }

  def retrieveAllQuote = quote {
    query[TestTable]
  }

  def retrieveQuote(minutes: Long) = quote {
    query[TestTable].filter(t => minus(now(), t.timestamp) < nowFromMinutes(lift(minutes)))
  }
}
