package nv.account.infrastructure.dao

import nv.account.domain.model.account.AccountId
import nv.common.ddd.infrastructure.{ DaoHelpers, DbConfig }

class AccountsDao(val dbConfig: DbConfig) extends DefaultMappers {

  import DaoHelpers._
  import dbConfig.driver.api._

  val accounts = TableQuery[Accounts]

  class Accounts(tag: Tag) extends Table[AccountDto](tag, n"Accounts") {
    def id = column[AccountId](n"id", O.PrimaryKey, O.Length(36))

    def name = column[String](n"name")

    def * = (id, name) <> ((AccountDto.apply _).tupled, AccountDto.unapply)
  }

}

case class AccountDto(id: AccountId, name: String)