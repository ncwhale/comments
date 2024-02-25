package im.koa.web.comments

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.http.HttpServer
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.*
import io.vertx.kotlin.core.json.*
import org.slf4j.LoggerFactory

class MainVerticle : AbstractVerticle() {
  lateinit var server: HttpServer
  val logger = LoggerFactory.getLogger(MainVerticle::class.java)

  override fun start(startPromise: Promise<Void>) {
    // Create a Router
    val router = Router.router(vertx)

    // Serve webjars as lib
    router.get("/lib/*").handler(StaticHandler.create("META-INF/resources/webjars"))

    // Serve static files
    router.get("/static/*").handler(StaticHandler.create(FileSystemAccess.RELATIVE, "static"))

    // Handler for all missing request routes
    router.route().handler { context ->
      // Get the address of the request
      val request = context.request()
      val address = request.connection().remoteAddress().toString()
      val method = request.method().toString()
      val path = request.path()
      logger.info("Unhandled $method $path @$address")

      // Write an empty string response
      context.response().setStatusCode(204).putHeader("content-type", "text/plain").end()
    }

    server =
        vertx
            .createHttpServer()
            .requestHandler(router)
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
