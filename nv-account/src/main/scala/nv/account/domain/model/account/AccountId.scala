package nv.account.domain.model.account

import nv.common.ddd.domain.EntityId

case class AccountId(value: String) extends EntityId[String]
