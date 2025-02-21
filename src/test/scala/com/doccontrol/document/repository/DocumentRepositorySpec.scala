package com.doccontrol.document.repository

import cats.effect.{IO, Resource}
import cats.effect.testing.scalatest.AsyncIOSpec
import com.dimafeng.testcontainers.PostgreSQLContainer
import com.dimafeng.testcontainers.scalatest.TestContainerForAll
import doobie.util.transactor.Transactor
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers
import org.testcontainers.utility.DockerImageName
import cats.effect.unsafe.implicits.global
import com.doccontrol.document.repository.DoobieDocumentRepository
import com.doccontrol.document.domain.Document

import java.time.Instant
import java.util.UUID

class DocumentRepositorySpec extends AsyncFreeSpec with AsyncIOSpec with Matchers with TestContainerForAll {
  override val containerDef = PostgreSQLContainer.Def(
    dockerImageName = DockerImageName.parse("postgres:14"),
    databaseName = "testdb",
    username = "test",
    password = "test"
  )

  "DocumentRepository" - {
    "should create and retrieve documents" in {
      withContainers { pgContainer =>
        val xa = Transactor.fromDriverManager[IO](
          "org.postgresql.Driver",
          pgContainer.container.getJdbcUrl,
          pgContainer.container.getUsername,
          pgContainer.container.getPassword
        )

        val repo = DoobieDocumentRepository(xa)

        val document = Document(
          id = UUID.randomUUID(),
          title = "Test Document",
          description = Some("Test Description"),
          documentTypeId = UUID.randomUUID(),
          projectId = UUID.randomUUID(),
          createdAt = Instant.now(),
          updatedAt = Instant.now()
        )

        for {
          created <- repo.create(document)
          retrieved <- repo.get(created.id)
        } yield {
          retrieved shouldBe Some(created)
        }
      }
    }
  }
} 