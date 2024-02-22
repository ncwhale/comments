package im.koa.web.comments

import io.vertx.core.AbstractVerticle
import io.vertx.core.http.HttpServer
import io.vertx.core.Promise
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.*
import io.vertx.kotlin.core.json.*

class MainVerticle : AbstractVerticle() {
  lateinit var server : HttpServer

  override fun start(startPromise: Promise<Void>) {
    // Create a Router
    val router = Router.router(vertx)

    // Serve webjars as lib
    router.get("/lib/*").handler(StaticHandler.create("META-INF/resources/webjars"))

    // Serve static files
    router.get("/static/*").handler(StaticHandler.create(FileSystemAccess.RELATIVE, "static"))

    // Handler for all missing routes
    router.route().handler { context ->
      // Get the address of the request
      val address = context.request().connection().remoteAddress().toString()
      val method = context.request().method().toString()
      val path = context.request().path()
      println("Unhandled $method $path @$address")
      // Get the query parameter "name"
      // val queryParams = context.queryParams()
      // val name = queryParams.get("name") ?: "unknown"
      // Write a json response
      context.response().headers().add("content-type", "text/plain")
      context.response().end("")
    }

    server = vertx
      .createHttpServer()
      .requestHandler(router)
        // .requestHandler { req ->
        //   req.response().putHeader("content-type", "text/plain").end("Hello from Vert.x!")
        // }
        .listen(8888) { http ->
          if (http.succeeded()) {
            startPromise.complete()
            println("HTTP server started on port 8888")
          } else {
            startPromise.fail(http.cause())
          }
        }
  }

  override fun stop(stopPromise: Promise<Void>) {
    println("HTTP server stopped")
    server.close()
    stopPromise.complete()
  }
}
