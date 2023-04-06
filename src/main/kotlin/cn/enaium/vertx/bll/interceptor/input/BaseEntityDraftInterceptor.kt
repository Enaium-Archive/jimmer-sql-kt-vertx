package cn.enaium.vertx.bll.interceptor.input

import cn.enaium.vertx.model.common.BaseEntity
import cn.enaium.vertx.model.common.BaseEntityDraft
import org.babyfish.jimmer.kt.isLoaded
import org.babyfish.jimmer.sql.DraftInterceptor
import java.time.LocalDateTime

class BaseEntityDraftInterceptor : DraftInterceptor<BaseEntityDraft> {
  override fun beforeSave(draft: BaseEntityDraft, isNew: Boolean) {
    if (!isLoaded(draft, BaseEntity::modifiedTime)) {
      draft.modifiedTime = LocalDateTime.now()
    }
    if (isNew && !isLoaded(draft, BaseEntity::createdTime)) {
      draft.createdTime = LocalDateTime.now()
    }
  }
}
