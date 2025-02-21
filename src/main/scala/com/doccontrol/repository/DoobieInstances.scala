package com.doccontrol.repository

import doobie._
import doobie.implicits._
import doobie.postgres.implicits._
import doobie.util.meta.Meta
import doobie.util.{Get, Put}
import com.doccontrol.document.domain.{Document, DocumentType, Revision}
import java.time.Instant
import java.util.UUID
import cats.syntax.all._
// import doobie.postgres.JavaTime._ // Correct import for Java Time instances

object DoobieInstances {
  // Import doobie's implicit Meta instances
  import doobie.postgres.implicits.UuidType  // For UUID

  // Read instances for domain models
  implicit val documentRead: Read[Document] = {
    Read[(
      UUID,        // id
      String,      // title
      Option[String], // description
      UUID,        // documentTypeId
      UUID,        // projectId
      Instant,     // createdAt
      Instant      // updatedAt
    )].map { case (id, title, description, documentTypeId, projectId, createdAt, updatedAt) =>
      Document(
        id = id,
        title = title,
        description = description,
        documentTypeId = documentTypeId,
        projectId = projectId,
        createdAt = createdAt,
        updatedAt = updatedAt
      )
    }
  }

  implicit val documentTypeRead: Read[DocumentType] = {
    Read[(UUID, String, Option[String])].map { case (id, name, description) =>
      DocumentType(
        id = id,
        name = name,
        description = description
      )
    }
  }

  implicit val revisionRead: Read[Revision] = {
    Read[(UUID, UUID, String, String, Instant, UUID)].map { case (id, documentId, version, content, createdAt, createdBy) =>
      Revision(
        id = id,
        documentId = documentId,
        version = version,
        content = content,
        createdAt = createdAt,
        createdBy = createdBy
      )
    }
  }
} 