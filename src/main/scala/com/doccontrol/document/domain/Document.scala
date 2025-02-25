package com.doccontrol.document.domain

import java.time.Instant
import java.util.UUID
import io.circe.{Encoder, Decoder}
import io.circe.generic.semiauto._
import com.doccontrol.document.model.CreateDocumentRequest

case class Document(
  id: UUID,
  title: String,
  description: Option[String],
  documentTypeId: UUID,
  projectId: UUID,
  latestRevisionId: UUID,
  createdAt: Instant,
  updatedAt: Instant
)

object Document {
  implicit val encoder: Encoder[Document] = deriveEncoder[Document]
  implicit val decoder: Decoder[Document] = deriveDecoder[Document]

  def createDocument(createReq: CreateDocumentRequest, documentId: UUID, latestRevisionId: UUID): Document = {
    Document(
      id = documentId,
      title = createReq.title,
      description = createReq.description,
      documentTypeId = createReq.documentTypeId,
      projectId = createReq.projectId,
      latestRevisionId = latestRevisionId,
      createdAt = Instant.now,
      updatedAt = Instant.now
    )
  }
}
