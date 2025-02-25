package com.doccontrol.document.model

import java.util.UUID
import io.circe.{Encoder, Decoder}
import io.circe.generic.semiauto._  

case class CreateDocumentRequest(
  title: String,
  description: Option[String],
  documentTypeId: UUID,
  projectId: UUID,
  userId: UUID
)

object CreateDocumentRequest {
  implicit val encoder: Encoder[CreateDocumentRequest] = deriveEncoder
  implicit val decoder: Decoder[CreateDocumentRequest] = deriveDecoder
}