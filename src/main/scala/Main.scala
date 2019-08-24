import java.time.OffsetDateTime

import com.typesafe.scalalogging.StrictLogging
import io.getquill.{PostgresMonixJdbcContext, SnakeCase}
import io.getquill.context.monix.Runner
import monix.execution.Scheduler

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Main extends App with StrictLogging {
  implicit val scheduler: Scheduler = Scheduler.global

  lazy val ctx: PostgresMonixJdbcContext[SnakeCase] =
    new PostgresMonixJdbcContext(SnakeCase, "database", Runner.using(Scheduler.io()))

  lazy val queries = new Queries(ctx)

  logger.info("Migrating")
  queries.migrate()

  val task = ctx.transaction {
    for {
      _      <- queries.clear()
      _      <- queries.insert(OffsetDateTime.now().minusMinutes(10))
      before <- queries.retriveAll
      _      = println(before)
      after <- queries.retrieve(15)
      _      = println(after)
    } yield ()
  }

  Await.result(task.runToFuture, Duration.Inf)
}
