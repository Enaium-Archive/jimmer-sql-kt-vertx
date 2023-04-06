package cn.enaium.vertx.bll

import cn.enaium.vertx.asType
import cn.enaium.vertx.end
import cn.enaium.vertx.model.BookStore
import cn.enaium.vertx.model.by
import cn.enaium.vertx.model.input.BookStoreInput
import cn.enaium.vertx.sqlClient
import io.vertx.ext.web.RoutingContext
import org.babyfish.jimmer.sql.kt.fetcher.newFetcher

fun findSimpleStores(context: RoutingContext) {
  context.response().end(sqlClient.entities.findAll(SIMPLE_FETCHER) {
    asc(BookStore::name)
  })
}

fun findStores(context: RoutingContext) {
  context.response().end(sqlClient.entities.findAll(DEFAULT_FETCHER) {
    asc(BookStore::name)
  })
}

fun findComplexStoreWithAllBooks(context: RoutingContext) {
  val id = context.request().getParam("id").toLong()
  context.response().end(sqlClient.entities.findById(WITH_ALL_BOOKS_FETCHER, id))
}

fun findComplexStoreWithNewestBooks(context: RoutingContext) {
  val id = context.request().getParam("id").toLong()
  context.response().end(sqlClient.entities.findById(WITH_NEWEST_BOOKS_FETCHER, id))
}

fun saveBookStore(context: RoutingContext) {
  val asType = context.body().asType(BookStoreInput::class)
  sqlClient.entities.save(asType)
  context.response().end()
}

fun deleteBookStore(context: RoutingContext) {
  val id = context.request().getParam("id").toLong()
  sqlClient.entities.delete(BookStore::class, id)
  context.response().end()
}

private val SIMPLE_FETCHER = newFetcher(BookStore::class).by {
  name()
}

private val DEFAULT_FETCHER = newFetcher(BookStore::class).by {
  allScalarFields()
}

private val WITH_ALL_BOOKS_FETCHER = newFetcher(BookStore::class).by {
  allScalarFields()
  avgPrice()
  books {
    allScalarFields()
    tenant(false)
    authors {
      allScalarFields()
    }
  }
}

private val WITH_NEWEST_BOOKS_FETCHER = newFetcher(BookStore::class).by {
  allScalarFields()
  avgPrice()
  newestBooks {
    allScalarFields()
    tenant(false)
    authors {
      allScalarFields()
    }
  }
}
