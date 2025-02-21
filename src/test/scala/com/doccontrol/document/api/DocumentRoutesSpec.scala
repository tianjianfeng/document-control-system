package com.doccontrol.document.api

import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import com.doccontrol.document.model.CreateDocumentRequest
import com.doccontrol.document.service.DocumentService
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.implicits._
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers
import cats.effect.unsafe.implicits.global
import com.doccontrol.document.domain.Document
import com.doccontrol.document.model.CreateDocumentRequest
import com.doccontrol.document.domain.DocumentType
import com.doccontrol.document.domain.Revision

import java.time.Instant
import java.util.UUID

class DocumentRoutesSpec extends AsyncFreeSpec with AsyncIOSpec with Matchers {
  implicit val documentDecoder: EntityDecoder[IO, Document] = jsonOf[IO, Document]

  "DocumentRoutes" - {
    "should create a document" in {
      val mockService = new DocumentService[IO] {
        def createDocument(title: String, description: Option[String], documentTypeId: UUID, projectId: UUID): IO[Document] = 
          IO.pure(Document(
            id = UUID.randomUUID(),
            title = title,
            description = description,
            documentTypeId = documentTypeId,
            projectId = projectId,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
          ))

        def getDocument(id: UUID): IO[Option[Document]] = IO.pure(None)
        def updateDocument(document: Document): IO[Option[Document]] = IO.pure(None)
        def deleteDocument(id: UUID): IO[Boolean] = IO.pure(false)
        def createDocumentType(documentType: DocumentType): IO[DocumentType] = IO.pure(documentType)
        def getDocumentType(id: UUID): IO[Option[DocumentType]] = IO.pure(None)
        def createRevision(revision: Revision): IO[Revision] = IO.pure(revision)
        def getRevisions(documentId: UUID): IO[List[Revision]] = IO.pure(List())
        def getLatestRevision(documentId: UUID): IO[Option[Revision]] = IO.pure(None)
      }

      val routes = new DocumentRoutes[IO](mockService).routes

      val createRequest = CreateDocumentRequest(
        title = "Test Document",
        description = Some("Test Description"),
        documentTypeId = UUID.randomUUID(),
        projectId = UUID.randomUUID()
      )

      val request = Request[IO](
        method = Method.POST,
        uri = uri"/documents"
      ).withEntity(createRequest.asJson)

      for {
        response <- routes.orNotFound.run(request)
        body <- response.as[Document]
      } yield {
        response.status shouldBe Status.Created
        body.title shouldBe "Test Document"
        body.description shouldBe Some("Test Description")
      }
    }
  }
} 