package com.doccontrol.document.repository

import cats.effect.IO
import doobie._
import doobie.implicits._
import doobie.postgres.implicits._
import com.doccontrol.document.domain.{Document, DocumentType}
import com.doccontrol.repository.DoobieInstances._
import java.util.UUID

class DoobieDocumentRepository(xa: Transactor[IO]) extends DocumentRepository[IO] {
  object SQLQueries {
    def insertDocument(d: Document): Update0 =
      sql"""
        INSERT INTO documents (id, title, description, document_type_id, project_id, created_at, updated_at)
        VALUES (${d.id}, ${d.title}, ${d.description}, ${d.documentTypeId}, ${d.projectId}, ${d.createdAt}, ${d.updatedAt})
      """.update

    def selectDocument(id: UUID): Query0[Document] =
      sql"""
        SELECT id, title, description, document_type_id, project_id, created_at, updated_at
        FROM documents
        WHERE id = $id
      """.query[Document]

    def updateDocument(d: Document): Update0 =
      sql"""
        UPDATE documents
        SET title = ${d.title}, description = ${d.description}, updated_at = ${d.updatedAt}
        WHERE id = ${d.id}
      """.update

    def deleteDocument(id: UUID): Update0 =
      sql"""
        DELETE FROM documents
        WHERE id = $id
      """.update

    def listDocuments: Query0[Document] =
      sql"""
        SELECT id, title, description, document_type_id, project_id, created_at, updated_at
        FROM documents
      """.query[Document]

    def insertDocumentType(dt: DocumentType): Update0 =
      sql"""
        INSERT INTO document_types (id, name, description)
        VALUES (${dt.id}, ${dt.name}, ${dt.description})
      """.update

    def selectDocumentType(id: UUID): Query0[DocumentType] =
      sql"""
        SELECT id, name, description
        FROM document_types
        WHERE id = $id
      """.query[DocumentType]
  }

  def create(document: Document): IO[Document] =
    SQLQueries.insertDocument(document)
      .run
      .transact(xa)
      .as(document)

  def get(id: UUID): IO[Option[Document]] =
    SQLQueries.selectDocument(id)
      .option
      .transact(xa)

  def update(document: Document): IO[Option[Document]] =
    SQLQueries.updateDocument(document)
      .run
      .transact(xa)
      .map(rowsAffected => if (rowsAffected > 0) Some(document) else None)

  def delete(id: UUID): IO[Boolean] =
    SQLQueries.deleteDocument(id)
      .run
      .transact(xa)
      .map(_ > 0)

  def list(): IO[List[Document]] =
    SQLQueries.listDocuments
      .to[List]
      .transact(xa)

  def createDocumentType(documentType: DocumentType): IO[DocumentType] =
    SQLQueries.insertDocumentType(documentType)
      .run
      .transact(xa)
      .as(documentType)

  def getDocumentType(id: UUID): IO[Option[DocumentType]] =
    SQLQueries.selectDocumentType(id)
      .option
      .transact(xa)
}

object DoobieDocumentRepository {
  def apply(xa: Transactor[IO]): DocumentRepository[IO] = 
    new DoobieDocumentRepository(xa)
} 