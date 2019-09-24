import java.time.OffsetDateTime

import com.typesafe.scalalogging.StrictLogging
import io.getquill.{PostgresJdbcContext, SnakeCase}

object Main extends App with StrictLogging {

  lazy val ctx: PostgresJdbcContext[SnakeCase] =
    new PostgresJdbcContext(SnakeCase, "database")

  lazy val queries = new Queries(ctx)

  logger.info("Migrating")
  queries.migrate()

  queries.clear()
  queries.insert(OffsetDateTime.now().minusMinutes(10))
  val before = queries.retrieveAll
  println(before)
  println(before)
//  queries.retrieve2(0)
  val after = queries.retrieve(15)
  println(after)
}
