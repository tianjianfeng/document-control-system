val Http4sVersion = "0.23.30"
val CirceVersion = "0.14.10"
val DoobieVersion = "1.0.0-RC7"
val LogbackVersion = "1.5.16"
val PureConfigVersion = "0.17.8"
val FlywayVersion = "11.3.3"
val PrometheusVersion = "0.16.0"
val OpenTelemetryVersion = "1.47.0"

lazy val root = (project in file("."))
  .settings(
    organization := "com.doccontrol",
    name := "document-service",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := "3.6.3",
    libraryDependencies ++= Seq(
      "org.http4s"      %% "http4s-ember-server" % Http4sVersion,
      "org.http4s"      %% "http4s-ember-client" % Http4sVersion,
      "org.http4s"      %% "http4s-circe"        % Http4sVersion,
      "org.http4s"      %% "http4s-dsl"          % Http4sVersion,
      "io.circe"        %% "circe-generic"       % CirceVersion,
      "io.circe"        %% "circe-parser"        % CirceVersion,
      "org.tpolecat"    %% "doobie-core"         % DoobieVersion,
      "org.tpolecat"    %% "doobie-postgres"     % DoobieVersion,
      "org.tpolecat"    %% "doobie-hikari"       % DoobieVersion,
      "ch.qos.logback"  %  "logback-classic"     % LogbackVersion,
      "org.scalatest"   %% "scalatest"           % "3.2.17" % Test,
      "com.github.pureconfig" %% "pureconfig-core"             % PureConfigVersion,
      "com.github.pureconfig" %% "pureconfig-cats-effect" % PureConfigVersion,
      "org.flywaydb" % "flyway-core" % FlywayVersion,
      "org.postgresql" % "postgresql" % "42.7.1",
      "io.prometheus" % "simpleclient"                    % PrometheusVersion,
      "io.prometheus" % "simpleclient_hotspot"           % PrometheusVersion,
      "io.prometheus" % "simpleclient_common"            % PrometheusVersion,
      "io.opentelemetry" % "opentelemetry-api"           % OpenTelemetryVersion,
      "io.opentelemetry" % "opentelemetry-sdk"           % OpenTelemetryVersion,
      "io.opentelemetry" % "opentelemetry-exporter-otlp" % OpenTelemetryVersion
    ),
    // Add assembly plugin
    assembly / assemblyJarName := "document-service-assembly-0.1.0-SNAPSHOT.jar",
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
    Compile / run / mainClass := Some("com.doccontrol.Main")
  ) 