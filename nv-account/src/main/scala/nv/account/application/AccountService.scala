package nv.account.application

import nv.account.domain.model.account.{ Account, AccountId }

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class AccountService {

  //TODO
  def getBy(id: Option[AccountId]): Future[Account] = {
    Future {
      Account(AccountId("dummy"), "displayName")
    }
  }
}
