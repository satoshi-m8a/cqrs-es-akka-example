package nv.site.application

import nv.common.ddd.infrastructure.IOExecutorSlick
import nv.site.domain.model.site.SiteId
import nv.site.infrastructure.dao.{ ArticleDto, ArticlesDao, SiteDto, SitesDao }

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SiteQueryService(sitesDao: SitesDao, articlesDao: ArticlesDao, io: IOExecutorSlick) {

  def findById(id: SiteId): Future[Option[SiteDto]] = {
    io.run {
      sitesDao.findById(id)
    }
  }

  def findSites(): Future[Seq[SiteDto]] = {
    import sitesDao.dbConfig.driver.api._
    io.run {
      sitesDao.sites.result
    }
  }

  def findArticles(id: SiteId): Future[Seq[ArticleDto]] = {
    io.run {
      articlesDao.findBy(id)
    }
  }

}
