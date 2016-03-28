package nv.account.domain.model.account

import java.util.UUID

object Account {
  def nextId: AccountId = AccountId(UUID.randomUUID().toString)
}

class Account {

}
