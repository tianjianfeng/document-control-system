package com.doccontrol.test

import cats.effect.IO
import com.dimafeng.testcontainers.PostgreSQLContainer
import com.dimafeng.testcontainers.scalatest.TestContainerForAll
import doobie.util.transactor.Transactor
import org.flywaydb.core.Flyway
import org.testcontainers.utility.DockerImageName
import org.scalatest.Suite

trait PostgresTestContainer extends TestContainerForAll { self: Suite =>
  override val containerDef = PostgreSQLContainer.Def(
    dockerImageName = DockerImageName.parse("postgres:16"),
    databaseName = "document_control_test",
    username = "test",
    password = "test"
  )

  type Container = PostgreSQLContainer
  
  protected def initDb(container: PostgreSQLContainer): Unit = {
    val flyway = Flyway.configure()
      .dataSource(
        container.jdbcUrl,
        container.username,
        container.password
      )
      .locations("classpath:db/migration")
      .load()

    flyway.migrate()
  }

  protected def createTransactor(container: PostgreSQLContainer): Transactor[IO] = {
    Transactor.fromDriverManager[IO](
      driver = "org.postgresql.Driver",
      url = container.jdbcUrl,
      user = container.username,
      password = container.password,
      logHandler = None
    )
  }
} 