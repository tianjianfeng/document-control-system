package com.doccontrol.document.domain

import java.util.UUID
import io.circe.syntax._
import io.circe.{Encoder, Decoder}
import io.circe.generic.semiauto._

case class DocumentType(
  id: UUID,
  name: String,
  description: Option[String]
)

object DocumentType {
  implicit val encoder: Encoder[DocumentType] = deriveEncoder[DocumentType]
  implicit val decoder: Decoder[DocumentType] = deriveDecoder[DocumentType]
}
