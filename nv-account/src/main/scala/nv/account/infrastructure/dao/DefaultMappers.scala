package nv.account.infrastructure.dao

import nv.account.domain.model.account.AccountId
import nv.common.ddd.infrastructure.DbConfig

trait DefaultMappers {

  val dbConfig: DbConfig

  import dbConfig.driver.api._

  implicit val accountIdMapper = MappedColumnType.base[AccountId, String](
    e ⇒ e.value,
    s ⇒ AccountId(s)
  )
}
