package com.doccontrol.document.service

import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import com.doccontrol.document.model.CreateDocumentRequest
import com.doccontrol.document.repository.{DocumentRepository, RevisionRepository}
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers
import cats.effect.unsafe.implicits.global
import org.scalatest.flatspec.AnyFlatSpec
import com.doccontrol.document.domain.Revision
import com.doccontrol.document.domain.DocumentType
import com.doccontrol.document.domain.Document


import java.time.Instant
import java.util.UUID

class DocumentServiceSpec extends AsyncFreeSpec with AsyncIOSpec with Matchers {
  "DocumentService" - {
    "should create a document" in {
      val testDoc = Document(
        id = UUID.randomUUID(),
        title = "Test Document",
        description = Some("Test Description"),
        documentTypeId = UUID.randomUUID(),
        projectId = UUID.randomUUID(),
        createdAt = Instant.now(),
        updatedAt = Instant.now()
      )

      val mockRepo = new DocumentRepository[IO] {
        def create(document: Document): IO[Document] = IO.pure(document)
        def get(id: UUID): IO[Option[Document]] = IO.pure(Some(testDoc))
        def update(document: Document): IO[Option[Document]] = IO.pure(Some(document))
        def delete(id: UUID): IO[Boolean] = IO.pure(true)
        def list(): IO[List[Document]] = IO.pure(List(testDoc))
        def createDocumentType(documentType: DocumentType): IO[DocumentType] = IO.pure(documentType)
        def getDocumentType(id: UUID): IO[Option[DocumentType]] = IO.pure(None)
      }

      val mockRevisionRepo = new RevisionRepository[IO] {
        def createRevision(revision: Revision): IO[Revision] = IO.pure(revision)
        def getRevisions(documentId: UUID): IO[List[Revision]] = IO.pure(List())
        def getLatestRevision(documentId: UUID): IO[Option[Revision]] = IO.pure(None)
      }

      val service = new DocumentServiceImpl[IO](mockRepo, mockRevisionRepo)

      val request = CreateDocumentRequest(
        title = "Test Document",
        description = Some("Test Description"),
        documentTypeId = UUID.randomUUID(),
        projectId = UUID.randomUUID()
      )

      for {
        result <- service.createDocument(
          request.title,
          request.description,
          request.documentTypeId,
          request.projectId
        )
      } yield {
        result.title shouldBe request.title
        result.description shouldBe request.description
      }
    }
  }
} 