package com.doccontrol

import cats.effect._
import cats.implicits._
import com.doccontrol.config.AppConfig
import doobie._
import doobie.implicits._
import doobie.hikari.HikariTransactor
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import org.http4s.implicits._
import com.comcast.ip4s._
import pureconfig._
import pureconfig.generic.derivation.default._
import com.doccontrol.document.repository.{DoobieDocumentRepository, DoobieRevisionRepository}
import com.doccontrol.document.service.DocumentServiceImpl
import com.doccontrol.document.api.DocumentRoutes

object Main extends IOApp {
  
  def run(args: List[String]): IO[ExitCode] = {
    for {
      config <- IO.delay(ConfigSource.default.loadOrThrow[AppConfig])
      
      xa <- HikariTransactor.newHikariTransactor[IO](
        driverClassName = config.database.driver,
        url = config.database.url,
        user = config.database.user,
        pass = config.database.password,
        connectEC = runtime.compute
      ).allocated.map(_._1)
      
      documentRepo = DoobieDocumentRepository(xa)
      revisionRepo = DoobieRevisionRepository(xa)
      documentService = new DocumentServiceImpl[IO](documentRepo, revisionRepo)
      documentRoutes = new DocumentRoutes[IO](documentService)
      
      httpApp = Router(
        "/api" -> documentRoutes.routes
      ).orNotFound
      
      _ <- EmberServerBuilder
        .default[IO]
        .withHost(Host.fromString("0.0.0.0").get)
        .withPort(Port.fromInt(8080).get)
        .withHttpApp(httpApp)
        .build
        .useForever
    } yield ExitCode.Success
  }
} 