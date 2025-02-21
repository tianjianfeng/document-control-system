package com.doccontrol.config

import pureconfig._
import pureconfig.generic.derivation.default._
import scala.concurrent.duration.FiniteDuration

final case class ServerConfig(
  host: String,
  port: Int
) derives ConfigReader

final case class DatabaseConfig(
  driver: String,
  host: String,
  port: Int,
  name: String,
  url: String,
  user: String,
  password: String,
  poolSize: Int,
  maxLifetime: FiniteDuration
) derives ConfigReader

final case class AppConfig(
  server: ServerConfig,
  database: DatabaseConfig
) derives ConfigReader 