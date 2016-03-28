package nv.discussion.port.adapter.dao

import nv.common.ddd.infrastructure.DbConfig
import nv.discussion.domain.model.discussion.DiscussionId

trait DefaultMappers {
  val dbConfig: DbConfig

  import dbConfig.driver.api._

  implicit val discussionIdMapper = MappedColumnType.base[DiscussionId, String](
    e ⇒ e.value,
    s ⇒ DiscussionId(s)
  )
}
