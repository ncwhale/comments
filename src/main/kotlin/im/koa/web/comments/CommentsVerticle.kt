package im.koa.web.comments

import io.vertx.core.json.pointer.JsonPointer
import io.vertx.kotlin.coroutines.*
import io.vertx.pgclient.PgConnectOptions
import io.vertx.pgclient.PgBuilder
import io.vertx.sqlclient.PoolOptions
import org.slf4j.LoggerFactory

class CommentsVerticle : CoroutineVerticle() {
  val logger = LoggerFactory.getLogger(HttpVerticle::class.java)
  lateinit var dbPool: io.vertx.sqlclient.Pool

  override suspend fun start() {
    // Connect to DB pool.
    val connectOptions =
      PgConnectOptions()
        .setPort(
          try {
            JsonPointer.from("/db/port").queryJson(config) as Int
          } catch (e: Exception) {
            5432
          }
        )
        .setHost(
          try {
            JsonPointer.from("/db/host").queryJson(config) as String
          } catch (e: Exception) {
            "localhost"
          }
        )
        .setDatabase(
          try {
            JsonPointer.from("/db/name").queryJson(config) as String
          } catch (e: Exception) {
            "comments"
          }
        )
        .setUser(
          try {
            JsonPointer.from("/db/user").queryJson(config) as String
          } catch (e: Exception) {
            "comments"
          }
        )
        .setPassword(
          try {
            JsonPointer.from("/db/password").queryJson(config) as String
          } catch (e: Exception) {
            "comments"
          }
        )
        .setPipeliningLimit(
          try {
            JsonPointer.from("/db/pipeliningLimit").queryJson(config) as Int
          } catch (e: Exception) {
            128
          }
        )
        .setCachePreparedStatements(true)

    val poolOptions = PoolOptions()
      .setMaxSize(
        try {
          JsonPointer.from("/db/pool/size").queryJson(config) as Int
        } catch (e: Exception) {
          5
        }
      )
      .setShared(true)
      .setName("comments-pool")

    dbPool = PgBuilder
      .pool()
      .connectingTo(connectOptions)
      .with(poolOptions)
      .using(vertx)
      .build()
  }

  override suspend fun stop() {}
}
