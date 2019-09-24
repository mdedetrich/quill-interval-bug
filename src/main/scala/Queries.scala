import java.time.OffsetDateTime

import io.getquill.{PostgresJdbcContext, SnakeCase}
import org.flywaydb.core.Flyway

class Queries(override val ctx: PostgresJdbcContext[SnakeCase]) extends PostgresEncodings with Quotes[SnakeCase] {

  import ctx._

  def clear() = ctx.run(clearQuote)

  def insert(timestamp: OffsetDateTime) = ctx.run(insertQuote(timestamp))

  def retrieveAll = ctx.run(retrieveAllQuote)

  def retrieve(minutes: Int) = ctx.run(retrieveQuote(minutes))

  def retrieve2(counter: Long) = ctx.run(retrieve2Quote(counter))

  def migrate() = {
    val flyway = Flyway.configure().dataSource(ctx.dataSource)
    flyway.load().migrate()
  }

}
