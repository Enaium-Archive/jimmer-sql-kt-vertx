package cn.enaium.vertx.bll

import cn.enaium.vertx.asType
import cn.enaium.vertx.end
import cn.enaium.vertx.model.TreeNode
import cn.enaium.vertx.model.by
import cn.enaium.vertx.model.input.RecursiveTreeInput
import cn.enaium.vertx.model.name
import cn.enaium.vertx.model.parent
import cn.enaium.vertx.sqlClient
import io.vertx.ext.web.RoutingContext
import org.babyfish.jimmer.kt.new
import org.babyfish.jimmer.sql.kt.ast.expression.ilike
import org.babyfish.jimmer.sql.kt.ast.table.isNull
import org.babyfish.jimmer.sql.kt.fetcher.newFetcher

fun findRootTrees(context: RoutingContext) {
  val rootName = context.request().getParam("rootName")
  context.response().end(sqlClient.createQuery(TreeNode::class) {
    where(table.parent.isNull())
    rootName?.takeIf { it.isNotEmpty() }?.let {
      where(table.name ilike rootName)
    }
    select(table.fetch(RECURSIVE_FETCHER))
  }.execute())
}

fun saveTree(context: RoutingContext) {
  val asType = context.body().asType(RecursiveTreeInput::class)
  val treeNode = new(TreeNode::class).by(asType.toEntity()) {
    parent = null
  }
  sqlClient.entities.save(treeNode) {
    setAutoAttachingAll()
  }
  context.response().end()
}

fun deleteTree(context: RoutingContext) {
  val id = context.request().getParam("id").toLong()
  sqlClient.entities.delete(TreeNode::class, id)
  context.response().end()
}

private val RECURSIVE_FETCHER = newFetcher(TreeNode::class).by {
  allScalarFields()
  childNodes({
    recursive()
  }) {
    allScalarFields()
  }
}
