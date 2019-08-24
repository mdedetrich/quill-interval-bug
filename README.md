## Quill Interval Bug

This repo is demonstrating a bug with quoting when using custom encodings for `OffsetDatetime`
along with inline SQL. When you run this with `sbt run` you get the following error message

```
[error] (run-main-0) org.postgresql.util.PSQLException: The column index is out of range: 1, number of columns: 0.
[error] org.postgresql.util.PSQLException: The column index is out of range: 1, number of columns: 0.
```

Curiously if you remove the lifting of the value and instead hardcode it, i.e.

```
  private def nowFromMinutes = quote { minutes: Long =>
    infix"INTERVAL '15 minutes'".as[OffsetDateTime]
  }
```

Instead of

```
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