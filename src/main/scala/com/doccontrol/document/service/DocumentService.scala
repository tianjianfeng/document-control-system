package com.doccontrol.document.service

import cats.effect.IO
import com.doccontrol.document.domain.{Document, DocumentType, Revision}
import com.doccontrol.document.repository.{DocumentRepository, RevisionRepository}
import java.util.UUID
import java.time.Instant

trait DocumentService[F[_]] {
  def createDocument(title: String, description: Option[String], documentTypeId: UUID, projectId: UUID): F[Document]
  def getDocument(id: UUID): F[Option[Document]]
  def updateDocument(document: Document): F[Option[Document]]
  def deleteDocument(id: UUID): F[Boolean]
  def createDocumentType(documentType: DocumentType): F[DocumentType]
  def getDocumentType(id: UUID): F[Option[DocumentType]]
  def createRevision(revision: Revision): F[Revision]
  def getRevisions(documentId: UUID): F[List[Revision]]
  def getLatestRevision(documentId: UUID): F[Option[Revision]]
}

class DocumentServiceImpl[F[_]](
  documentRepo: DocumentRepository[F],
  revisionRepo: RevisionRepository[F]
) extends DocumentService[F] {
  
  override def createDocument(
    title: String,
    description: Option[String],
    documentTypeId: UUID,
    projectId: UUID
  ): F[Document] = {
    val document = Document(
      id = UUID.randomUUID(),
      title = title,
      description = description,
      documentTypeId = documentTypeId,
      projectId = projectId,
      createdAt = Instant.now,
      updatedAt = Instant.now
    )
    documentRepo.create(document)
  }

  override def getDocument(id: UUID): F[Option[Document]] = {
    documentRepo.get(id)
  }

  override def updateDocument(document: Document): F[Option[Document]] = {
    documentRepo.update(document)
  }

  override def deleteDocument(id: UUID): F[Boolean] = {
    documentRepo.delete(id)
  }

  override def createDocumentType(documentType: DocumentType): F[DocumentType] = {
    documentRepo.createDocumentType(documentType)
  }

  override def getDocumentType(id: UUID): F[Option[DocumentType]] = {
    documentRepo.getDocumentType(id)
  }

  override def createRevision(revision: Revision): F[Revision] = {
    revisionRepo.createRevision(revision)
  }
  
  override def getRevisions(documentId: UUID): F[List[Revision]] = {
    revisionRepo.getRevisions(documentId)
  }

  override def getLatestRevision(documentId: UUID): F[Option[Revision]] = {
    revisionRepo.getLatestRevision(documentId)
  } 

} 