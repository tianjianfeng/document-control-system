package com.doccontrol.document.domain

import java.time.Instant
import java.util.UUID
import io.circe.{Encoder, Decoder}
import io.circe.generic.semiauto._

case class Document(
  id: UUID,
  title: String,
  description: Option[String],
  documentTypeId: UUID,
  projectId: UUID,
  createdAt: Instant,
  updatedAt: Instant
)

object Document {
  implicit val encoder: Encoder[Document] = deriveEncoder[Document]
  implicit val decoder: Decoder[Document] = deriveDecoder[Document]
}
