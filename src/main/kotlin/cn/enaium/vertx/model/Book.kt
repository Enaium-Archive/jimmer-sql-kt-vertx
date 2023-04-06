package cn.enaium.vertx.model

import cn.enaium.vertx.model.common.TenantAware
import org.babyfish.jimmer.sql.*
import java.math.BigDecimal

@Entity
interface Book : TenantAware {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: Long

  @Key
  val name: String

  @Key
  val edition: Int
  val price: BigDecimal

  @ManyToOne
  val store: BookStore?

  @ManyToMany
  @JoinTable(
    name = "BOOK_AUTHOR_MAPPING",
    joinColumnName = "BOOK_ID",
    inverseJoinColumnName = "AUTHOR_ID"
  )
  val authors: List<Author>

  // -----------------------------
  // Optional properties
  // -----------------------------

  // Optional property `storeId`
  // If this property is deleted, please add `BookInput.Mapper.toBookStore(Long)`
  @IdView
  val storeId: Long?

  // Optional property `authorIds`
  // If this property is deleted, please add `BookInputMapper.toAuthor(Long)`
  @IdView("authors")
  val authorIds: List<Long>
}
