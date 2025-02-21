package com.doccontrol.document.domain

import java.time.Instant
import java.util.UUID

case class Document(
  id: UUID,
  title: String,
  description: Option[String],
  documentTypeId: UUID,
  projectId: UUID,
  createdAt: Instant,
  updatedAt: Instant
)

case class DocumentType(
  id: UUID,
  name: String,
  description: Option[String]
)

case class Revision(
  id: UUID,
  documentId: UUID,
  version: String,
  content: String,
  createdAt: Instant,
  createdBy: UUID // Person ID
) 