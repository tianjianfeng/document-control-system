package com.doccontrol.document.api

import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import com.dimafeng.testcontainers.PostgreSQLContainer
import com.doccontrol.document.domain.{Document, DocumentType}
import com.doccontrol.document.model.{CreateDocumentRequest, CreateDocumentTypeRequest}
import com.doccontrol.document.repository.{DoobieDocumentRepository, DoobieRevisionRepository}
import com.doccontrol.document.service.DocumentServiceImpl
import com.doccontrol.test.PostgresTestContainer
import org.http4s._
import org.http4s.circe._
import org.http4s.implicits._
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers
import java.util.UUID
import org.http4s.circe.CirceEntityCodec.{circeEntityEncoder, circeEntityDecoder}

class DocumentApiSpec extends AsyncFreeSpec with AsyncIOSpec with Matchers with PostgresTestContainer {

  private def createTestApp(container: PostgreSQLContainer) = {
    val xa = createTransactor(container)
    val documentRepo = DoobieDocumentRepository(xa)
    val revisionRepo = DoobieRevisionRepository(xa)
    val documentService = new DocumentServiceImpl[IO](documentRepo, revisionRepo)
    new DocumentRoutes[IO](documentService).routes.orNotFound
  }

  "Document API" - {
    "DocumentType endpoints" - {
      "should create and retrieve document types" in {
        withContainers { containers =>
          val container = containers.asInstanceOf[PostgreSQLContainer]
          initDb(container)
          val app = createTestApp(container)

          val createRequest = CreateDocumentTypeRequest(
            name = "Technical Specification",
            description = Some("Technical documentation")
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

    "Document endpoints" - {
      "should create and retrieve documents" in {
        withContainers { containers =>
          val container = containers.asInstanceOf[PostgreSQLContainer]
          initDb(container)
          val app = createTestApp(container)

          val createDocTypeRequest = CreateDocumentTypeRequest(
            name = "Technical Specification",
            description = Some("Technical documentation")
          )

          for {
            // Create document type first
            docTypeResponse <- app.run(
              Request[IO](
                method = Method.POST,
                uri = uri"/document-types"
              ).withEntity(createDocTypeRequest)
            )
            
            docType <- docTypeResponse.as[DocumentType]
            
            // Then create document
            createDocRequest = CreateDocumentRequest(
              title = "Test Document",
              description = Some("Test Description"),
              documentTypeId = docType.id,
              projectId = UUID.randomUUID()
            )
            
            createResponse <- app.run(
              Request[IO](
                method = Method.POST,
                uri = uri"/documents"
              ).withEntity(createDocRequest)
            )
            
            createdDoc <- createResponse.as[Document]
            
            // Verify response
            _ = createResponse.status shouldBe Status.Created
            _ = createdDoc.title shouldBe createDocRequest.title
            _ = createdDoc.description shouldBe createDocRequest.description
            
            // Get document
            getResponse <- app.run(
              Request[IO](
                method = Method.GET,
                uri = Uri.unsafeFromString(s"/documents/${createdDoc.id}")
              )
            )
            
            retrievedDoc <- getResponse.as[Document]
            
            // Verify retrieved document
            _ = getResponse.status shouldBe Status.Ok
            _ = retrievedDoc shouldBe createdDoc
            
            // Test not found case
            notFoundResponse <- app.run(
              Request[IO](
                method = Method.GET,
                uri = Uri.unsafeFromString(s"/documents/${UUID.randomUUID()}")
              )
            )
            
          } yield {
            notFoundResponse.status shouldBe Status.NotFound
          }
        }
      }
    }
  }
} 