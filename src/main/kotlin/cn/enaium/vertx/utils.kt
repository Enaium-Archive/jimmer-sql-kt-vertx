package cn.enaium.vertx

import cn.enaium.vertx.bll.interceptor.input.BaseEntityDraftInterceptor
import cn.enaium.vertx.model.ENTITY_MANAGER
import cn.enaium.vertx.model.Gender
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.zaxxer.hikari.HikariDataSource
import io.netty.handler.codec.http.HttpHeaderNames
import io.netty.handler.codec.http.HttpHeaderValues
import io.vertx.core.Vertx
import io.vertx.core.http.HttpServerResponse
import io.vertx.ext.web.RequestBody
import io.vertx.ext.web.Route
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.babyfish.jimmer.jackson.ImmutableModule
import org.babyfish.jimmer.sql.dialect.H2Dialect
import org.babyfish.jimmer.sql.kt.newKSqlClient
import org.babyfish.jimmer.sql.runtime.ScalarProvider
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.reflect.KClass

val sqlClient = newKSqlClient {
  setConnectionManager {
    val connection =
      HikariDataSource().apply {
        jdbcUrl = "jdbc:h2:mem:test;MODE=MySQL;INIT=runscript from 'classpath:/jimmer-demo.sql'"
      }.connection
    connection.use {
      proceed(it)
    }
  }
  setDialect(H2Dialect())
  setEntityManager(ENTITY_MANAGER)
  addScalarProvider(
    ScalarProvider.enumProviderByString(Gender::class.java) {
      it.map(Gender.MALE, "M")
      it.map(Gender.FEMALE, "F")
    }
  )
  addDraftInterceptor(BaseEntityDraftInterceptor())
}

val jackson: ObjectMapper =
  jacksonObjectMapper().registerModule(ImmutableModule()).registerModule(JavaTimeModule().apply {
    addSerializer(
      LocalDateTime::class.java,
      LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    )
  })

fun HttpServerResponse.end(any: Any?) {
  this.putHeader(HttpHeaderNames.CONTENT_TYPE, "${HttpHeaderValues.APPLICATION_JSON};charset=utf-8")
    .end(any?.let { jackson.writeValueAsString(it) } ?: "{}")
}

fun <T : Any> RequestBody.asType(type: KClass<T>): T {
  return jackson.readValue(asString(), type.java)
}

@OptIn(DelicateCoroutinesApi::class)
fun Route.coroutineHandler(fn: suspend (RoutingContext) -> Unit) {
  var v = Dispatchers.Unconfined // Make intellij happy
  v = Vertx.currentContext().dispatcher()
  handler { ctx ->
    GlobalScope.launch(v) {
      try {
        fn(ctx)
      } catch (e: Exception) {
        ctx.fail(e)
      }
    }
  }
}
