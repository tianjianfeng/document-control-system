package com.doccontrol.document.model

import io.circe.{Encoder, Decoder}
import io.circe.generic.semiauto._  

case class CreateDocumentTypeRequest(
  name: String,
  description: Option[String]
)

object CreateDocumentTypeRequest {
  implicit val encoder: Encoder[CreateDocumentTypeRequest] = deriveEncoder
  implicit val decoder: Decoder[CreateDocumentTypeRequest] = deriveDecoder
}