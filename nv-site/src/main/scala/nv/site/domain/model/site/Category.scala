package nv.site.domain.model.site

import java.util.UUID

case class Category(id: CategoryId, children: Seq[Category], name: String, path: String) {

  def addChildCategory(name: String, path: String): Category = {
    this.copy(children = this.children :+ Category(CategoryId(UUID.randomUUID().toString), Nil, name, path))
  }

}
