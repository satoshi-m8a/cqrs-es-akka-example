package nv.site.infrastructure.repository

import nv.common.ddd.infrastructure.DbConfig
import nv.site.domain.model.site.{ Site, SiteRepository }
import nv.site.infrastructure.dao.{ SiteDto, SitesDao }
import slick.dbio.DBIO

import scala.concurrent.ExecutionContext

class SiteRepositorySlick(dbConfig: DbConfig, sitesDao: SitesDao) extends SiteRepository[DBIO] {

  override def save(site: Site)(implicit ec: ExecutionContext): DBIO[Site] = {
    import dbConfig.driver.api._

    sitesDao.sites.insertOrUpdate(SiteDto(site.id, site.name)).map {
      _ ⇒
        site
    }
  }
}
