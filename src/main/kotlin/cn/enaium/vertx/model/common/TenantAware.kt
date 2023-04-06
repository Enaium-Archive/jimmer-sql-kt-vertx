package cn.enaium.vertx.model.common

import org.babyfish.jimmer.sql.MappedSuperclass

@MappedSuperclass
interface TenantAware : BaseEntity {

  val tenant: String
}
