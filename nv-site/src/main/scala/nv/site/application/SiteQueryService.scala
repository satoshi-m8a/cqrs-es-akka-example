package nv.site.application

import nv.common.ddd.infrastructure.IOExecutorSlick
import nv.site.domain.model.site.SiteId
import nv.site.infrastructure.dao.{ SiteDto, SitesDao }

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SiteQueryService(sitesDao: SitesDao, io: IOExecutorSlick) {

  def findById(id: SiteId): Future[Option[SiteDto]] = {
    io.run {
      sitesDao.findById(id)
    }
  }

}
