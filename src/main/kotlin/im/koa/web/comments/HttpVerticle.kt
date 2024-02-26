package im.koa.web.comments

import io.vertx.core.json.JsonArray
import io.vertx.core.json.pointer.JsonPointer
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.CorsHandler
import io.vertx.ext.web.handler.StaticHandler
import io.vertx.kotlin.coroutines.*
import org.slf4j.LoggerFactory

class HttpVerticle : CoroutineVerticle() {
  val logger = LoggerFactory.getLogger(HttpVerticle::class.java)

  override suspend fun start() {
    // Create a Router
    val router = Router.router(vertx)

    // CORS handler, we need support HTMX headers
    val corsHandler = CorsHandler.create()

    try {
      val origins = JsonPointer.from("/http/cors/origins").queryJson(config) as JsonArray

      val origins_list = mutableListOf<String>()

      origins.forEach { site -> origins_list.add(site as String) }

      corsHandler.addOrigins(origins_list)
    } catch (e: Exception) {
      logger.warn("Invalid CORS origins config, skipped")
    }

    // Serve webjars as lib
    router.get("/lib/*").handler(StaticHandler.create("META-INF/resources/webjars"))

    // Handler for all missing request routes
    router.route().handler(corsHandler).handler { context ->
      // Get the address of the request
      val request = context.request()
      val address = request.connection().remoteAddress().toString()
      val method = request.method().toString()
      val path = request.path()
      logger.info("Unhandled $method $path @$address")

      // Write an empty string response
      context.response().setStatusCode(204).putHeader("content-type", "text/plain").end()
    }

    val host =
        try {
          JsonPointer.from("/http/host").queryJson(config) as String
        } catch (e: Exception) {
          logger.debug("Invalid host config: $e")
          logger.warn("Invalid host config, using default localhost")
          "localhost"
        }
    val port =
        try {
          JsonPointer.from("/http/port").queryJson(config) as Int
        } catch (e: Exception) {
          logger.debug("Invalid port config: $e")
          logger.warn("Invalid port config, using default 8888")
          8888
        }

    val httpServer = vertx.createHttpServer().requestHandler(router).listen(port, host).coAwait()
    logger.info("HTTP server started: http://$host:${httpServer.actualPort()}/")
  }

  override suspend fun stop() {
    logger.info("HTTP server stopped")
  }
}
