val Http4sVersion = "0.23.30"
val CirceVersion = "0.14.10"
val DoobieVersion = "1.0.0-RC7"
val LogbackVersion = "1.5.16"
val PureConfigVersion = "0.17.8"
val FlywayVersion = "11.3.1"
val PrometheusVersion = "0.16.0"
val OpenTelemetryVersion = "1.47.0"
val ScalaTestVersion = "3.2.17"
val ScalaTestPlusVersion = "3.2.17.0"
val TestContainersVersion = "0.41.0"

lazy val root = (project in file("."))
  .settings(
    organization := "com.doccontrol",
    name := "document-control",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := "3.6.3",
    libraryDependencies ++= Seq(
      "org.http4s"      %% "http4s-ember-server" % Http4sVersion,
      "org.http4s"      %% "http4s-ember-client" % Http4sVersion,
      "org.http4s"      %% "http4s-circe"        % Http4sVersion,
      "org.http4s"      %% "http4s-dsl"          % Http4sVersion,
      "io.circe"        %% "circe-core"          % CirceVersion,
      "io.circe"        %% "circe-generic"       % CirceVersion,
      "io.circe"        %% "circe-parser"        % CirceVersion,
      "org.tpolecat"    %% "doobie-core"         % DoobieVersion,
      "org.tpolecat"    %% "doobie-postgres"     % DoobieVersion,
      "org.tpolecat"    %% "doobie-hikari"       % DoobieVersion,
      "ch.qos.logback"  %  "logback-classic"     % LogbackVersion,
      "org.scalatest"   %% "scalatest"           % ScalaTestVersion     % Test,
      "com.github.pureconfig" %% "pureconfig-core"             % PureConfigVersion,
      "com.github.pureconfig" %% "pureconfig-cats-effect" % PureConfigVersion,
      "org.flywaydb" % "flyway-core" % FlywayVersion,
      "org.flywaydb" % "flyway-database-postgresql" % FlywayVersion % "runtime",
      "org.postgresql" % "postgresql" % "42.7.1",
      "io.prometheus" % "simpleclient"                    % PrometheusVersion,
      "io.prometheus" % "simpleclient_hotspot"           % PrometheusVersion,
      "io.prometheus" % "simpleclient_common"            % PrometheusVersion,
      "io.opentelemetry" % "opentelemetry-api"           % OpenTelemetryVersion,
      "io.opentelemetry" % "opentelemetry-sdk"           % OpenTelemetryVersion,
      "io.opentelemetry" % "opentelemetry-exporter-otlp" % OpenTelemetryVersion,
      "org.scalatestplus" %% "scalacheck-1-17"               % ScalaTestPlusVersion % Test,
      "org.typelevel"     %% "cats-effect-testing-scalatest" % "1.5.0"             % Test,
      "com.dimafeng"      %% "testcontainers-scala-postgresql" % TestContainersVersion % Test,
      "com.dimafeng"      %% "testcontainers-scala-scalatest"  % TestContainersVersion % Test
    ),
    // Add assembly plugin
    assembly / assemblyJarName := "document-control-assembly-0.1.0-SNAPSHOT.jar",
    // Merge strategy for assembly
    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", xs @ _*) => MergeStrategy.discard
      case "application.conf"            => MergeStrategy.concat
      case PathList("module-info.class") => MergeStrategy.discard
      case x if x.endsWith("/module-info.class") => MergeStrategy.discard
      case x =>
        val oldStrategy = (assembly / assemblyMergeStrategy).value
        oldStrategy(x)
    },
    Compile / mainClass := Some("com.doccontrol.Main"),
    Compile / run / mainClass := Some("com.doccontrol.Main"),
    Compile / run / fork := true,
    // Add Flyway settings
    flywayUrl := sys.env.getOrElse("FLYWAY_URL", "jdbc:postgresql://localhost:5432/document_control"),
    flywayUser := sys.env.getOrElse("FLYWAY_USER", "postgres"),
    flywayPassword := sys.env.getOrElse("FLYWAY_PASSWORD", "postgres"),
    flywayLocations := Seq("filesystem:src/main/resources/db/migration"),
    flywayBaselineOnMigrate := true
  ) 