package nv.site.domain.model

import nv.common.ddd.domain.EntityId

package object article {

  case class ArticleId(value: String) extends EntityId[String]

  case class PageId(value: String) extends EntityId[String]

}
