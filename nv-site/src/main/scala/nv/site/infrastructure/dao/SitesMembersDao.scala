package nv.site.infrastructure.dao

import nv.account.domain.model.account.AccountId
import nv.account.infrastructure.dao.AccountsDao
import nv.common.ddd.infrastructure.{ DaoHelpers, DbConfig }
import nv.site.domain.model.site.{ SiteId, Role }

import nv.account.infrastructure.dao.{ DefaultMappers ⇒ SiteDefaultMappers }

class SitesMembersDao(val dbConfig: DbConfig, val sitesDao: SitesDao, val accountsDao: AccountsDao) extends DefaultMappers with SiteDefaultMappers {

  import DaoHelpers._

  import dbConfig.driver.api._

  val sites = TableQuery[SitesMembers]

  class SitesMembers(tag: Tag) extends Table[SitesMemberDto](tag, n"SitesMembers") {
    def siteId = column[SiteId](n"site_id", O.Length(36))

    def accountId = column[AccountId](n"accountId", O.Length(36))

    def role = column[Role](n"role", O.Length(10))

    def pk = primaryKey(n"pk_site_member", (siteId, accountId))

    def * = (siteId, accountId, role) <> ((SitesMemberDto.apply _).tupled, SitesMemberDto.unapply)

    def site = foreignKey(n"site_id_fk", siteId, sitesDao.sites)(_.id, onDelete = ForeignKeyAction.Cascade)

    def account = foreignKey(n"account_id_fk", accountId, accountsDao.accounts)(_.id, onDelete = ForeignKeyAction.Cascade)

  }

}

case class SitesMemberDto(siteId: SiteId, accountId: AccountId, role: Role)