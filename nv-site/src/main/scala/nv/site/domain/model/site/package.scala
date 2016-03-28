package nv.site.domain.model

import nv.common.ddd.domain.EntityId

package object site {

  case class SiteId(value: String) extends EntityId[String]

  case class CategoryId(value: String) extends EntityId[String]

}
