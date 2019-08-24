import java.time.OffsetDateTime

import io.getquill.context.jdbc.PostgresJdbcContextBase

trait PostgresEncodings {

  val ctx: PostgresJdbcContextBase[_]

  import ctx._

  implicit val encodeOffsetDateTime: Encoder[OffsetDateTime] =
    encoder(
      java.sql.Types.OTHER,
      (index, value, row) => row.setObject(index, value)
    )

  implicit val decodeOffsetDateTime: Decoder[OffsetDateTime] =
    decoder(
      (index, row) => row.getObject(index, classOf[OffsetDateTime])
    )

  implicit final class DateTimeBooleanOps(left: OffsetDateTime) {
    def >(right: OffsetDateTime) = quote(infix"$left > $right".as[Boolean])
    def <(right: OffsetDateTime) = quote(infix"$left < $right".as[Boolean])
  }

  def age = quote { (date1: OffsetDateTime, date2: OffsetDateTime) =>
    infix"""age($date1, $date2)""".as[OffsetDateTime]
  }

  def now() = quote {
    infix"""now()""".as[OffsetDateTime]
  }

  def minus = quote { (left: OffsetDateTime, right: OffsetDateTime) =>
    infix"$left - $right".as[OffsetDateTime]
  }

}
