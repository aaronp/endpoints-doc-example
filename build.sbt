name := "endpoints-circe-docs"

libraryDependencies ++= List(
  "org.julienrf" %% "endpoints-algebra"             % "0.9.0",
  "org.julienrf" %% "endpoints-json-schema-generic" % "0.9.0",
  "org.julienrf" %% "endpoints-json-schema-circe"   % "0.9.0",
  "org.julienrf" %% "endpoints-akka-http-server"       % "0.9.0",
  "org.julienrf" %% "endpoints-algebra-circe"          % "0.9.0",
  "org.julienrf" %% "endpoints-openapi"                % "0.9.0"
)

libraryDependencies ++= List(
      "io.circe" %% "circe-core",
      "io.circe" %% "circe-generic",
      "io.circe" %% "circe-parser",
      "io.circe" %% "circe-java8",
      "io.circe" %% "circe-literal",
      "io.circe" %% "circe-shapes"
    ).map(_ % "0.11.1")