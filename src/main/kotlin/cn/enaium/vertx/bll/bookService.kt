package cn.enaium.vertx.bll

import cn.enaium.vertx.asType
import cn.enaium.vertx.end
import cn.enaium.vertx.model.*
import cn.enaium.vertx.model.input.BookInput
import cn.enaium.vertx.model.input.CompositeBookInput
import cn.enaium.vertx.sqlClient
import io.vertx.ext.web.RoutingContext
import org.babyfish.jimmer.sql.kt.ast.expression.ilike
import org.babyfish.jimmer.sql.kt.ast.expression.or
import org.babyfish.jimmer.sql.kt.ast.expression.valueIn
import org.babyfish.jimmer.sql.kt.fetcher.newFetcher

fun findSimpleBooks(context: RoutingContext) {
  context.response().end(sqlClient.entities.findAll(SIMPLE_FETCHER) {
    asc(Book::name)
  })
}

fun findBooks(context: RoutingContext) {
  val pageIndex = context.request().getParam("pageIndex", "0").toInt()
  val pageSize = context.request().getParam("pageSize", "5").toInt()
  val sortCode = context.request().getParam("sortCode")
  val name = context.request().getParam("name")
  val storeName = context.request().getParam("storeName")
  val authorName = context.request().getParam("authorName")
  context.response().end(sqlClient.createQuery(Book::class) {
    name?.takeIf { it.isNotEmpty() }?.let {
      where(table.name ilike it)
    }
    storeName?.takeIf { it.isNotEmpty() }?.let {
      where(table.store.name ilike it)
    }
    authorName?.takeIf { it.isNotEmpty() }?.let {
      where(
        table.id valueIn subQuery(Author::class) {
          where(
            or(
              table.firstName ilike it,
              table.lastName ilike it
            )
          )
          select(table.books.id)
        }
      )
    }
    select(table.fetch(DEFAULT_FETCHER))
  })
}

fun findComplexBook(context: RoutingContext) {
  val id = context.request().getParam("id").toLong()
  context.response().end(sqlClient.entities.findById(COMPLEX_FETCHER, id))
}

fun saveBook(context: RoutingContext) {
  val asType = context.body().asType(BookInput::class)
  sqlClient.entities.save(asType)
  context.response().end()
}

fun saveBookComposite(context: RoutingContext) {
  val asType = context.body().asType(CompositeBookInput::class)
  sqlClient.entities.save(asType) {
    setAutoAttachingAll()
  }
  context.response().end()
}

fun deleteBook(context: RoutingContext) {
  val id = context.request().getParam("id").toLong()
  sqlClient.entities.delete(Book::class, id)
  context.response().end()
}

private val SIMPLE_FETCHER = newFetcher(Book::class).by {
  name()
}

private val DEFAULT_FETCHER = newFetcher(Book::class).by {

  allScalarFields()
  tenant(false)

  store {
    name()
  }
  authors {
    firstName()
    lastName()
  }
}

private val COMPLEX_FETCHER = newFetcher(Book::class).by {

  allScalarFields()
  tenant(false)

  store {
    allScalarFields()
  }
  authors {
    allScalarFields()
  }
}
