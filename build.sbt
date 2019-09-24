val quillVersion    = "3.4.3"
val postgresVersion = "42.2.6"
val flywayVersion   = "5.2.4"

libraryDependencies ++= Seq(
  "io.getquill"    %% "quill-jdbc"       % quillVersion,
  "org.postgresql" % "postgresql"        % postgresVersion,
  "org.flywaydb"   % "flyway-core"       % flywayVersion
)

scalaVersion := "2.12.9"

fork := false 
