package cn.enaium.vertx.bll.resolver

import cn.enaium.vertx.model.*
import cn.enaium.vertx.sqlClient
import org.babyfish.jimmer.sql.kt.KTransientResolver
import org.babyfish.jimmer.sql.kt.ast.expression.asNonNull
import org.babyfish.jimmer.sql.kt.ast.expression.max
import org.babyfish.jimmer.sql.kt.ast.expression.tuple
import org.babyfish.jimmer.sql.kt.ast.expression.valueIn

class BookStoreNewestBooksResolver : KTransientResolver<Long, List<Long>> {
  override fun resolve(ids: Collection<Long>) = sqlClient.createQuery(BookStore::class) {
    where(
      tuple(
        table.asTableEx().books.name,
        table.asTableEx().books.edition
      ) valueIn subQuery(Book::class) {
        where(table.store.id valueIn ids)
        groupBy(table.name)
        select(
          table.name,
          max(table.edition).asNonNull()
        )
      }
    )
    select(
      table.id,
      table.asTableEx().books.id
    )
  }.execute().groupBy({ it._1 }) {
    it._2
  }
}
