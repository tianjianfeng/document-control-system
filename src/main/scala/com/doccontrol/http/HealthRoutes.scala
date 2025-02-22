package com.doccontrol.http

import cats.effect.IO
import org.http4s.HttpRoutes
import org.http4s.dsl.io._

object HealthRoutes {
  def routes: HttpRoutes[IO] = {
    HttpRoutes.of[IO] {
      case GET -> Root / "health" =>
        Ok("OK")
    }
  }
} 