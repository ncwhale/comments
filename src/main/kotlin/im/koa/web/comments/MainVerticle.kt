package im.koa.web.comments

import io.vertx.config.*
import io.vertx.core.*
import io.vertx.core.json.*
import io.vertx.kotlin.coroutines.*
import org.slf4j.LoggerFactory

class MainVerticle : CoroutineVerticle() {
  val logger = LoggerFactory.getLogger(MainVerticle::class.java)

  override suspend fun start() {
    // MainVerticle will kickstart with Config
    val dirStore =
        ConfigStoreOptions()
            .setType("directory")
            .setConfig(
                JsonObject()
                    .put("path", "config")
                    .put(
                        "filesets",
                        JsonArray()
                            .add(JsonObject().put("pattern", "*.json"))
                            .add(
                                JsonObject()
                                    .put("pattern", "*.properties")
                                    .put("format", "properties")
                            )
                    )
            )

    val envStore =
        ConfigStoreOptions().setType("env").setConfig(JsonObject().put("raw-data", false))

    val sysPropsStore =
        ConfigStoreOptions().setType("sys").setConfig(JsonObject().put("hierarchical", true))

    val options =
        ConfigRetrieverOptions().addStore(dirStore).addStore(envStore).addStore(sysPropsStore)
    val retriever = ConfigRetriever.create(vertx, options)

    val config = retriever.getConfig().coAwait()
    // logger.debug("Config: $config")

    // Deploy verticles
    vertx.deployVerticle(HttpVerticle::class.java.name, DeploymentOptions().setConfig(config))

    // Emit config change events
    retriever.listen { change ->
      // Previous configuration
      // val previous = change.getPreviousConfiguration()
      // New configuration
      val conf = change.getNewConfiguration()

      vertx.eventBus().publish("config.change", conf)
    }
  }
}
