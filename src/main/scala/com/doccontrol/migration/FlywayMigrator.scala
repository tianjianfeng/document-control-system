package com.doccontrol.migration

import cats.effect.IO
import org.flywaydb.core.Flyway
import com.doccontrol.config.AppConfig
import org.slf4j.LoggerFactory
import scala.concurrent.duration._
import cats.syntax.all._

class FlywayMigrator(config: AppConfig) {
  private val logger = LoggerFactory.getLogger(getClass)

  private def attemptMigration: IO[Unit] = IO.delay {
    logger.info(s"Running migrations on ${config.database.url}")
    val flyway = Flyway.configure()
      .dataSource(
        config.database.url,
        config.database.user,
        config.database.password
      )
      .locations("classpath:db/migration")
      .load()

    val result = flyway.migrate()
    logger.info(s"Applied ${result.migrationsExecuted} migrations")
  }

  def migrate: IO[Unit] = {
    attemptMigration
      .handleErrorWith { error =>
        logger.error("Migration failed, retrying in 5 seconds:", error)
        IO.sleep(5.seconds) >> migrate
      }
  }
}

object FlywayMigrator {
  def apply(config: AppConfig): FlywayMigrator = new FlywayMigrator(config)
} 
