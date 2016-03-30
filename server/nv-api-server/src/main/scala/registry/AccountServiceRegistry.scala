package registry

import com.google.inject.ImplementedBy
import nv.account.application.AccountService

@ImplementedBy(classOf[AccountServiceRegistryImpl])
trait AccountServiceRegistry {
  val accountService: AccountService
}

class AccountServiceRegistryImpl extends AccountServiceRegistry {
  override val accountService: AccountService = new AccountService
}
