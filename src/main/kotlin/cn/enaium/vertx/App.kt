package cn.enaium.vertx

import cn.enaium.vertx.bll.*
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.ErrorHandler
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.await

class App : CoroutineVerticle() {

  override suspend fun start() {
    val router = Router.router(vertx)

    router.get("/author/simpleList").coroutineHandler { findSimpleAuthors(it) }
    router.get("/author/list").coroutineHandler { findAuthors(it) }
    router.get("/author/:id").coroutineHandler { findComplexAuthor(it) }
    router.put("/author").handler(BodyHandler.create()).coroutineHandler { saveAuthor(it) }
    router.delete("/author/:id").coroutineHandler { deleteAuthor(it) }

    router.get("/book/simpleList").coroutineHandler { findSimpleBooks(it) }
    router.get("/book/list").coroutineHandler { findBooks(it) }
    router.get("/book/:id").coroutineHandler { findComplexBook(it) }
    router.put("/book").handler(BodyHandler.create()).coroutineHandler { saveBook(it) }
    router.put("/book/composite").handler(BodyHandler.create()).coroutineHandler { saveBookComposite(it) }
    router.delete("/book/:id").coroutineHandler { deleteBook(it) }

    router.get("/bookStore/simpleList").coroutineHandler { findSimpleStores(it) }
    router.get("/bookStore/list").coroutineHandler { findStores(it) }
    router.get("/booksStore/:id/withAllBooks").coroutineHandler { findComplexStoreWithAllBooks(it) }
    router.get("/booksStore/:id/withNewestBooks").coroutineHandler { findComplexStoreWithNewestBooks(it) }
    router.put("/booksStore").handler(BodyHandler.create()).coroutineHandler { saveBookStore(it) }
    router.delete("/booksStore/:id").coroutineHandler { deleteBookStore(it) }

    router.get("/tree/roots/recursive").coroutineHandler { findRootTrees(it) }
    router.put("/tree/root/recursive").handler(BodyHandler.create()).coroutineHandler { saveTree(it) }
    router.delete("/tree/tree/:id").coroutineHandler { deleteTree(it) }

    router.route().last().failureHandler(ErrorHandler.create(vertx, true))

    vertx.createHttpServer()
      .requestHandler(router).exceptionHandler { it.printStackTrace() }
      .listen(8888).await()
    println("HTTP server started on port 8888")
  }
}
