package im.koa.web.comments

import io.vertx.config.*
import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.http.HttpServer
import io.vertx.core.json.JsonObject
import org.slf4j.LoggerFactory

class MainVerticle : AbstractVerticle() {
  lateinit var server: HttpServer
  val logger = LoggerFactory.getLogger(MainVerticle::class.java)

  override fun start(startPromise: Promise<Void>) {
    // Config
    val fileStore =
        ConfigStoreOptions()
            .setType("file")
            .setOptional(true)
            .setFormat("json")
            .setConfig(JsonObject().put("path", "comments-config.json"))

    val envStore = ConfigStoreOptions().setType("env").setConfig(JsonObject().put("raw-data", true))

    val sysPropsStore =
        ConfigStoreOptions().setType("sys").setConfig(JsonObject().put("hierarchical", true))

    val options =
        ConfigRetrieverOptions().addStore(fileStore).addStore(envStore).addStore(sysPropsStore)
    val retriever = ConfigRetriever.create(vertx, options)

    retriever.getConfig().onComplete { ar ->
      if (ar.failed()) {
        // Failed to retrieve the configuration
        logger.error("Failed to retrieve the configuration", ar.cause())
      } else {
        val config = ar.result()
        logger.info("Configuration: $config")

        // Recreate vertx with config?
      }
    }
  }
}
