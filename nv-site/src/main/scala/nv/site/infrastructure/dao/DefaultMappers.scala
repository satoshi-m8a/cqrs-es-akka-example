package nv.site.infrastructure.dao

import nv.common.ddd.infrastructure.DbConfig
import nv.site.domain.model.site.{ Role, SiteId }

trait DefaultMappers {

  val dbConfig: DbConfig

  import dbConfig.driver.api._

  implicit val roleMapper = MappedColumnType.base[Role, String](
    e ⇒ e.code,
    s ⇒ Role.fromString(s)
  )

  implicit val siteIdMapper = MappedColumnType.base[SiteId, String](
    e ⇒ e.value,
    s ⇒ SiteId(s)
  )
}
