package cn.enaium.vertx.bll.resolver

import cn.enaium.vertx.model.BookStore
import cn.enaium.vertx.model.`books?`
import cn.enaium.vertx.model.id
import cn.enaium.vertx.model.price
import cn.enaium.vertx.sqlClient
import org.babyfish.jimmer.sql.kt.KTransientResolver
import org.babyfish.jimmer.sql.kt.ast.expression.avg
import org.babyfish.jimmer.sql.kt.ast.expression.coalesce
import org.babyfish.jimmer.sql.kt.ast.expression.valueIn
import java.math.BigDecimal

class BookStoreAvgPriceResolver : KTransientResolver<Long, BigDecimal> {
  override fun resolve(ids: Collection<Long>) = sqlClient.createQuery(BookStore::class) {
    where(table.id valueIn ids)
    groupBy(table.id)
    select(
      table.id,
      avg(table.asTableEx().`books?`.price).coalesce(BigDecimal.ZERO)
    )
  }.execute().associateBy({ it._1 }) {
    it._2
  }
}
