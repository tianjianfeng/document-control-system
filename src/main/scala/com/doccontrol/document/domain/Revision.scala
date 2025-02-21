package com.doccontrol.document.domain

import java.time.Instant
import java.util.UUID
import io.circe.{Encoder, Decoder}
import io.circe.generic.semiauto._

case class Revision(
  id: UUID,
  documentId: UUID,
  version: String,
  content: String,
  createdAt: Instant,
  createdBy: UUID
)

object Revision {
  implicit val encoder: Encoder[Revision] = deriveEncoder[Revision]
  implicit val decoder: Decoder[Revision] = deriveDecoder[Revision]
} 