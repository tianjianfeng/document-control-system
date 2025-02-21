package com.doccontrol.migration

import cats.effect.IO
import org.flywaydb.core.Flyway
import com.doccontrol.config.AppConfig
import org.slf4j.LoggerFactory

class FlywayMigrator(config: AppConfig) {
  private val logger = LoggerFactory.getLogger(getClass)

  def migrate: IO[Unit] = IO.delay {
    logger.info(s"Running migrations on ${config.database.url}")
    val flyway = Flyway.configure()
      .dataSource(
        config.database.url,
        config.database.user,
        config.database.password
      )
      .load()

    val result = flyway.migrate()
    logger.info(s"Applied ${result.migrationsExecuted} migrations")
  }
  .handleErrorWith { error =>
    logger.error("Migration failed:", error)
    IO.raiseError(error)
  }
}

object FlywayMigrator {
  def apply(config: AppConfig): FlywayMigrator = new FlywayMigrator(config)
} 
