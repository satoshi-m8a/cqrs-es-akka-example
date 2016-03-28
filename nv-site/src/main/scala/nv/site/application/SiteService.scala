package nv.site.application

import nv.account.domain.model.account.AccountId
import nv.common.ddd.infrastructure.IOExecutor
import nv.site.domain.model.site.Role.Admin
import nv.site.domain.model.site.{ Member, CategoryTree, Site, SiteRepository }

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SiteService[IO[+_]](siteRepository: SiteRepository[IO], io: IOExecutor[IO]) {
  def createSite(name: String, by: AccountId): Future[Site] = {
    val site = Site(Site.nextId, name, new CategoryTree, Seq(Member(by, Admin)))
    val action = siteRepository.save(site)
    io.run(action)
  }
}
