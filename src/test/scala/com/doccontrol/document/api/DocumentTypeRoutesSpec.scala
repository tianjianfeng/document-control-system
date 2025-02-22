package com.doccontrol.document.api

import cats.effect.{IO, Resource}
import cats.effect.testing.scalatest.AsyncIOSpec
import com.dimafeng.testcontainers.PostgreSQLContainer
import com.dimafeng.testcontainers.scalatest.TestContainerForAll
import com.doccontrol.document.domain.DocumentType
import com.doccontrol.document.model.CreateDocumentTypeRequest
import com.doccontrol.document.repository.{DoobieDocumentRepository, DoobieRevisionRepository}
import com.doccontrol.document.service.DocumentServiceImpl
import doobie.util.transactor.Transactor
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.implicits._
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers
import org.testcontainers.utility.DockerImageName
import org.flywaydb.core.Flyway
import java.util.UUID
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder

class DocumentTypeRoutesSpec extends AsyncFreeSpec with AsyncIOSpec with Matchers with TestContainerForAll {
  
  override val containerDef = PostgreSQLContainer.Def(
    dockerImageName = DockerImageName.parse("postgres:16"),
    databaseName = "document_control_test",
    username = "test",
    password = "test"
  )

  type Container = PostgreSQLContainer
  
  private def initDb(container: PostgreSQLContainer): Unit = {
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

  private def createTestApp(container: PostgreSQLContainer) = {
    val xa = Transactor.fromDriverManager[IO](
      driver = "org.postgresql.Driver",
      url = container.jdbcUrl,
      user = container.username,
      password = container.password,
      logHandler = None
    )

    val documentRepo = DoobieDocumentRepository(xa)
    val revisionRepo = DoobieRevisionRepository(xa)
    val documentService = new DocumentServiceImpl[IO](documentRepo, revisionRepo)
    new DocumentRoutes[IO](documentService).routes.orNotFound
  }

  "DocumentTypeRoutes" - {
    "should create and retrieve document types" in {
      withContainers { containers =>
        val container = containers.asInstanceOf[PostgreSQLContainer]
        initDb(container)
        val app = createTestApp(container)

        val createRequest = CreateDocumentTypeRequest(
          name = "Technical Specification",
          description = Some("Technical documentation for projects")
        )

        for {
          // Create document type
          createResponse <- app.run(
            Request[IO](
              method = Method.POST,
              uri = uri"/document-types"
            ).withEntity(createRequest)
          )
          
          createdDocType <- createResponse.as[DocumentType]
          
          // Verify response
          _ = createResponse.status shouldBe Status.Created
          _ = createdDocType.name shouldBe createRequest.name
          _ = createdDocType.description shouldBe createRequest.description
          
          // Get document type
          getResponse <- app.run(
            Request[IO](
              method = Method.GET,
              uri = Uri.unsafeFromString(s"/document-types/${createdDocType.id}")
            )
          )
          
          retrievedDocType <- getResponse.as[DocumentType]
          
          // Verify retrieved document type
          _ = getResponse.status shouldBe Status.Ok
          _ = retrievedDocType shouldBe createdDocType
          
          // Test not found case
          notFoundResponse <- app.run(
            Request[IO](
              method = Method.GET,
              uri = Uri.unsafeFromString(s"/document-types/${UUID.randomUUID()}")
            )
          )
          
        } yield {
          notFoundResponse.status shouldBe Status.NotFound
        }
      }
    }
  }
} 