package com.doccontrol.document.api

import io.circe._
import io.circe.generic.semiauto._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import java.util.UUID
import com.doccontrol.document.domain.Document
import com.doccontrol.document.service.DocumentService
import cats.effect.Async
import org.http4s.implicits._
import org.http4s.dsl.impl.OptionalQueryParamDecoderMatcher
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import cats.implicits.toFlatMapOps
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import cats.Applicative.ops.toAllApplicativeOps
import com.doccontrol.document.domain.Revision
class DocumentRoutes[F[_]: Async](documentService: DocumentService[F]) extends Http4sDsl[F] {
  
  implicit val documentEncoder: Encoder[Document] = deriveEncoder
  implicit val documentDecoder: Decoder[Document] = deriveDecoder

  implicit val revisionEncoder: Encoder[Revision] = deriveEncoder
  implicit val revisionDecoder: Decoder[Revision] = deriveDecoder
  
  case class CreateDocumentRequest(
    title: String,
    description: Option[String],
    documentTypeId: UUID,
    projectId: UUID
  )
  
  implicit val createDocumentRequestDecoder: Decoder[CreateDocumentRequest] = deriveDecoder
  
  val routes: HttpRoutes[F] = HttpRoutes.of[F] {
    case req @ POST -> Root / "documents" =>
      for {
        createReq <- req.as[CreateDocumentRequest]
        document <- documentService.createDocument(
          createReq.title,
          createReq.description,
          createReq.documentTypeId,
          createReq.projectId
        )
        response <- Created(document)
      } yield response
      
    case GET -> Root / "documents" / UUIDVar(id) =>
      documentService.getDocument(id).flatMap {
        case Some(document) => Ok(document)
        case None => NotFound()
      }
    case GET -> Root / "documents" / UUIDVar(id) / "revisions" =>
      documentService.getRevisions(id).flatMap { revisions =>
        Ok(revisions)
      }
    // ... other routes ...
  }
} 