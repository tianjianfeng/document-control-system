package com.doccontrol.document.repository

import cats.effect.kernel.MonadCancelThrow
import com.doccontrol.document.domain.{Document, DocumentType}
import java.util.UUID

trait DocumentRepository[F[_]] {
  def create(document: Document): F[Document]
  def get(id: UUID): F[Option[Document]]
  def update(document: Document): F[Option[Document]]
  def delete(id: UUID): F[Boolean]
  def list(): F[List[Document]]
  def createDocumentType(documentType: DocumentType): F[DocumentType]
  def getDocumentType(id: UUID): F[Option[DocumentType]]
}

object DocumentRepository {
  // This constraint ensures that F can handle cancellation and errors
  def apply[F[_]: MonadCancelThrow](implicit R: DocumentRepository[F]): DocumentRepository[F] = R
} 