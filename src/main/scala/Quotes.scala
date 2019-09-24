import java.time.OffsetDateTime

import io.getquill.NamingStrategy
import io.getquill.context.jdbc.PostgresJdbcContextBase

trait Quotes[N <: NamingStrategy] extends PostgresEncodings {

  val ctx: PostgresJdbcContextBase[N]

  import ctx._

  private def nowFromMinutes = quote { minutes: Int =>
    infix"make_interval(mins => $minutes)".as[OffsetDateTime]
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

  def retrieveQuote(minutes: Int) = quote {
    query[TestTable].filter(t => minus(now(), t.timestamp) < nowFromMinutes(lift(minutes)))
  }

  def retrieve2Quote(counter: Long) = quote {
    query[TestTable].filter(_.counter == lift(counter))
  }
}
