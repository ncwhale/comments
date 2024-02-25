package im.koa.web.comments

import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.StaticHandler
import io.vertx.kotlin.coroutines.*
import org.slf4j.LoggerFactory

class HttpVerticle : CoroutineVerticle() {
  val logger = LoggerFactory.getLogger(MainVerticle::class.java)

  override suspend fun start() {
    // Create a Router
    val router = Router.router(vertx)

    // Serve webjars as lib
    router.get("/lib/*").handler(StaticHandler.create("META-INF/resources/webjars"))

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

    val httpServer =
        vertx
            .createHttpServer()
            .requestHandler(router)
            .listen(config.getInteger("http.port", 8888))
            .coAwait()
    logger.info("HTTP server started on port ${httpServer.actualPort()}")
  }

  override suspend fun stop() {
    logger.info("HTTP server stopped")
  }
}
