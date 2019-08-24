## Quill Interval Bug

This repo is demonstrating a bug with quoting when using custom encodings for `OffsetDatetime`
along with inline SQL. When you run this with `sbt run` you get the following error message

```
[error] Exception in thread "main" org.postgresql.util.PSQLException: The column index is out of range: 1, number of columns: 0.
[error] 	at org.postgresql.core.v3.SimpleParameterList.bind(SimpleParameterList.java:65)
[error] 	at org.postgresql.core.v3.SimpleParameterList.setBinaryParameter(SimpleParameterList.java:132)
[error] 	at org.postgresql.jdbc.PgPreparedStatement.bindBytes(PgPreparedStatement.java:983)
[error] 	at org.postgresql.jdbc.PgPreparedStatement.setLong(PgPreparedStatement.java:279)
[error] 	at com.zaxxer.hikari.pool.HikariProxyPreparedStatement.setLong(HikariProxyPreparedStatement.java)
[error] 	at io.getquill.context.jdbc.Encoders.$anonfun$longEncoder$2(Encoders.scala:50)
[error] 	at scala.runtime.java8.JFunction2$mcVIJ$sp.apply(JFunction2$mcVIJ$sp.java:23)
[error] 	at io.getquill.context.jdbc.Encoders.$anonfun$encoder$2(Encoders.scala:27)
[error] 	at io.getquill.context.jdbc.Encoders.$anonfun$encoder$2$adapted(Encoders.scala:27)
[error] 	at io.getquill.context.jdbc.Encoders.$anonfun$encoder$1(Encoders.scala:22)
[error] 	at io.getquill.context.jdbc.Encoders.$anonfun$encoder$1$adapted(Encoders.scala:21)
[error] 	at io.getquill.context.jdbc.Encoders$JdbcEncoder.apply(Encoders.scala:17)
[error] 	at io.getquill.context.jdbc.Encoders$JdbcEncoder.apply(Encoders.scala:15)
[error] 	at io.getquill.context.Expand.$anonfun$prepare$2(Expand.scala:30)
[error] 	at scala.collection.LinearSeqOptimized.foldLeft(LinearSeqOptimized.scala:126)
[error] 	at scala.collection.LinearSeqOptimized.foldLeft$(LinearSeqOptimized.scala:122)
[error] 	at scala.collection.immutable.List.foldLeft(List.scala:89)
[error] 	at io.getquill.context.Expand.$anonfun$prepare$1(Expand.scala:27)
[error] 	at io.getquill.context.jdbc.JdbcContextBase.$anonfun$executeQuery$1(JdbcContextBase.scala:39)
[error] 	at io.getquill.context.jdbc.JdbcContextBase.$anonfun$withConnectionWrapped$2(JdbcContextBase.scala:28)
[error] 	at io.getquill.context.jdbc.JdbcContext$$anon$1.wrap(JdbcContext.scala:31)
[error] 	at io.getquill.context.jdbc.JdbcContextBase.$anonfun$withConnectionWrapped$1(JdbcContextBase.scala:28)
[error] 	at io.getquill.context.jdbc.JdbcContext.$anonfun$withConnection$1(JdbcContext.scala:61)
[error] 	at scala.Option.getOrElse(Option.scala:189)
[error] 	at io.getquill.context.jdbc.JdbcContext.withConnection(JdbcContext.scala:59)
[error] 	at io.getquill.context.jdbc.JdbcContextBase.withConnectionWrapped(JdbcContextBase.scala:28)
[error] 	at io.getquill.context.jdbc.JdbcContextBase.withConnectionWrapped$(JdbcContextBase.scala:27)
[error] 	at io.getquill.context.jdbc.JdbcContext.withConnectionWrapped(JdbcContext.scala:15)
[error] 	at io.getquill.context.jdbc.JdbcContextBase.executeQuery(JdbcContextBase.scala:38)
[error] 	at io.getquill.context.jdbc.JdbcContextBase.executeQuery$(JdbcContextBase.scala:37)
[error] 	at io.getquill.context.jdbc.JdbcContext.executeQuery(JdbcContext.scala:40)
```

Curiously if you remove the lifting of the value and instead hardcode it, i.e.

```scala
private def nowFromMinutes = quote { minutes: Long =>
  infix"INTERVAL '15 minutes'".as[OffsetDateTime]
}
```

Instead of

```scala
private def nowFromMinutes = quote { minutes: Long =>
  infix"INTERVAL '$minutes minutes'".as[OffsetDateTime]
}
```

Then it works fine, which means that it appears to be a bug with encoding the values in a `ResultSet`
when using JDBC and encoding values.

If you use `String` instead of `Long` for minutes the bug also happens, likewise if you interpolate
the whole expression, i.e. `'15 minutes'` it also fails.

### Running

To reproduce the bug you first need to have a running Docker container with Postgres, this can
easily be done by doing

```
docker run --name quill-interval-bug -p 5432:5432  -e POSTGRES_PASSWORD=mysecretpassword -e POSTGRES_DB=postgres -d postgres
```

And then you can start the sbt shell and just do `run`