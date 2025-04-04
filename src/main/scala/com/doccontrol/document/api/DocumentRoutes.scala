package com.doccontrol.document.api

import io.circe._
import io.circe.generic.semiauto._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import java.util.UUID
import com.doccontrol.document.domain.{Document, DocumentType, Revision}
import com.doccontrol.document.model.{CreateDocumentRequest, CreateDocumentTypeRequest}
import com.doccontrol.document.service.DocumentService
import cats.effect.Async
import cats.implicits._
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import DocumentType._
import Document._
import Revision._

class DocumentRoutes[F[_]: Async](documentService: DocumentService[F]) extends Http4sDsl[F] {
  
  val routes: HttpRoutes[F] = HttpRoutes.of[F] {
    case req @ POST -> Root / "documents" =>
      for {
        createReq <- req.as[CreateDocumentRequest]
        document <- documentService.createDocument(createReq)
        response <- Created(document.asJson)
      } yield response
      
    case GET -> Root / "documents" / UUIDVar(id) =>
      documentService.getDocument(id).flatMap {
        case Some(document) => Ok(document.asJson)
        case None => NotFound()
      }

    case GET -> Root / "documents" / UUIDVar(id) / "revisions" =>
      documentService.getRevisions(id).flatMap { revisions =>
        Ok(revisions.asJson)
      }

    case GET -> Root / "documents" / UUIDVar(id) / "revisions" / UUIDVar(revisionId) =>
      documentService.getRevision(revisionId).flatMap {
        case Some(revision) => Ok(revision.asJson)
        case None => NotFound()
      }

    case req @ POST -> Root / "document-types" =>
      for {
        createReq <- req.as[CreateDocumentTypeRequest]
        docType <- documentService.createDocumentType(createReq)
        resp <- Created(docType.asJson)
      } yield resp

    case GET -> Root / "document-types" / UUIDVar(id) =>
      documentService.getDocumentType(id).flatMap {
        case Some(docType) => Ok(docType.asJson)
        case None => NotFound()
      }
  }
} 