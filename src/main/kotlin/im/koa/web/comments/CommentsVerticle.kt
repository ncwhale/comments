package im.koa.web.comments

import io.vertx.kotlin.coroutines.*
import org.slf4j.LoggerFactory

class CommentsVerticle : CoroutineVerticle() {
  val logger = LoggerFactory.getLogger(HttpVerticle::class.java)

  override suspend fun start() {
    //TODO: Connect to DB pool.
  }

  override suspend fun stop() {}
}
