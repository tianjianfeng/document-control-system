package com.doccontrol.document.repository

import cats.effect.IO
import doobie._
import doobie.implicits._
import doobie.postgres.implicits._
import com.doccontrol.document.domain.Revision
import com.doccontrol.repository.DoobieInstances._
import java.util.UUID

class DoobieRevisionRepository(xa: Transactor[IO]) extends RevisionRepository[IO] {
  object SQLQueries {
    def insertRevision(r: Revision): Update0 =
      sql"""
        INSERT INTO revisions (id, document_id, version, content, created_at, created_by)
        VALUES (${r.id}, ${r.documentId}, ${r.version}, ${r.content}, ${r.createdAt}, ${r.createdBy})
      """.update

    def selectRevisions(documentId: UUID): Query0[Revision] =
      sql"""
        SELECT id, document_id, version, content, created_at, created_by
        FROM revisions
        WHERE document_id = $documentId
        ORDER BY created_at ASC
      """.query[Revision]

    def selectLatestRevision(documentId: UUID): Query0[Revision] =
      sql"""
        SELECT id, document_id, version, content, created_at, created_by
        FROM revisions
        WHERE document_id = $documentId
        ORDER BY created_at DESC
        LIMIT 1
      """.query[Revision]

    def selectRevision(revisionId: UUID): Query0[Revision] =
      sql"""
        SELECT id, document_id, version, content, created_at, created_by
        FROM revisions
        WHERE id = $revisionId
      """.query[Revision]
  }

  def createRevision(revision: Revision): IO[Revision] =
    SQLQueries.insertRevision(revision)
      .run
      .transact(xa)
      .as(revision)

  def getRevisions(documentId: UUID): IO[List[Revision]] =
    SQLQueries.selectRevisions(documentId)
      .to[List]
      .transact(xa)

  def getLatestRevision(documentId: UUID): IO[Option[Revision]] =
    SQLQueries.selectLatestRevision(documentId)
      .option
      .transact(xa)

  def getRevision(revisionId: UUID): IO[Option[Revision]] =
    SQLQueries.selectRevision(revisionId)
      .option
      .transact(xa)
}

object DoobieRevisionRepository {
  def apply(xa: Transactor[IO]): RevisionRepository[IO] = 
    new DoobieRevisionRepository(xa)
} 