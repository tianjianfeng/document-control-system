package com.doccontrol.document.repository

import cats.effect.IO
import com.doccontrol.document.domain.Revision
import java.util.UUID

trait RevisionRepository[F[_]] {
  def createRevision(revision: Revision): F[Revision]
  def getRevisions(documentId: UUID): F[List[Revision]]
  def getLatestRevision(documentId: UUID): F[Option[Revision]]
  def getRevision(revisionId: UUID): F[Option[Revision]]
}

object RevisionRepository {
  def apply[F[_]](implicit R: RevisionRepository[F]): RevisionRepository[F] = R
} 