package cn.enaium.vertx.bll

import cn.enaium.vertx.asType
import cn.enaium.vertx.end
import cn.enaium.vertx.model.*
import cn.enaium.vertx.sqlClient
import io.vertx.ext.web.RoutingContext
import org.babyfish.jimmer.sql.kt.ast.expression.asc
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.babyfish.jimmer.sql.kt.ast.expression.ilike
import org.babyfish.jimmer.sql.kt.fetcher.newFetcher
fun findSimpleAuthors(context: RoutingContext) {
  context.response().end(sqlClient.entities.findAll(SIMPLE_FETCHER) {
    asc(Author::firstName)
    asc(Author::lastName)
  })
}

fun findAuthors(context: RoutingContext) {
  val sortCode = context.request().getParam("sortCode")
  val firstName = context.request().getParam("firstName")
  val lastName = context.request().getParam("lastName")
  val gender = context.request().getParam("gender")
  context.response().end(sqlClient.createQuery(Author::class) {
    firstName?.takeIf { it.isNotEmpty() }?.let {
      where(table.firstName ilike it)
    }
    lastName?.takeIf { it.isNotEmpty() }?.let {
      where(table.lastName ilike it)
    }
    gender?.takeIf { it.isNotEmpty() }?.let {
      where(table.gender eq Gender.valueOf(it))
    }
    sortCode?.takeIf { it.isNotEmpty() }?.let {

//      orderBy(Order.makeOrders(table, ""))
      orderBy(table.firstName.asc(), table.lastName.asc())
    }
    select(table.fetch(DEFAULT_FETCHER))
  }.execute())
}

fun findComplexAuthor(context: RoutingContext) {
  val id = context.request().getParam("id").toLong()
  context.response().end(sqlClient.entities.findById(COMPLEX_FETCHER, id))
}

fun saveAuthor(context: RoutingContext) {
  val asType = context.body().asType(Author::class)
  sqlClient.entities.save(asType)
  context.response().end()
}

fun deleteAuthor(context: RoutingContext) {
  val id = context.request().getParam("id").toLong()
  sqlClient.entities.delete(Author::class, id)
  context.response().end()
}

private val SIMPLE_FETCHER = newFetcher(Author::class).by {
  firstName()
  lastName()
}

private val DEFAULT_FETCHER = newFetcher(Author::class).by {
  allScalarFields()
}

private val COMPLEX_FETCHER = newFetcher(Author::class).by {
  allScalarFields()
  books {
    allScalarFields()
    store {
      allScalarFields()
    }
  }
}
