import java.time.OffsetDateTime

import io.getquill.{PostgresMonixJdbcContext, SnakeCase}
import org.flywaydb.core.Flyway

class Queries(override val ctx: PostgresMonixJdbcContext[SnakeCase]) extends PostgresEncodings with Quotes[SnakeCase] {

  import ctx._

  def clear() = ctx.run(clearQuote)

  def insert(timestamp: OffsetDateTime) = ctx.run(insertQuote(timestamp))

  def retrieveAll = ctx.run(retrieveAllQuote)

  def retrieve(minutes: Long) = ctx.run(retrieveQuote(minutes))

  def migrate() = {
    val flyway = Flyway.configure().dataSource(ctx.dataSource)
    flyway.load().migrate()
  }

}
