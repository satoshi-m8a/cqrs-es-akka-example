package registry

import javax.inject.Inject

import com.google.inject.ImplementedBy
import nv.common.ddd.infrastructure.{ DbConfig, IOExecutorSlick }
import nv.site.application.{ SiteQueryService, SiteService }
import nv.site.infrastructure.dao.SitesDao
import nv.site.infrastructure.repository.SiteRepositorySlick
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

@ImplementedBy(classOf[SiteServiceRegistryImpl])
trait SiteServiceRegistry {

  val dbConfig: DbConfig

  import dbConfig.driver.api._

  val siteQueryService: SiteQueryService

  val siteService: SiteService[DBIO]
}

class SiteServiceRegistryImpl @Inject() (dbConfigProvider: DatabaseConfigProvider) extends SiteServiceRegistry {

  lazy val dbc = dbConfigProvider.get[JdbcProfile]

  lazy val dbConfig = DbConfig(dbc.db, dbc.driver)

  lazy val sitesDao = new SitesDao(dbConfig)

  lazy val slickIo = new IOExecutorSlick(dbConfig)

  lazy val siteRepository = new SiteRepositorySlick(dbConfig, sitesDao)

  lazy val siteQueryService = new SiteQueryService(sitesDao, slickIo)

  lazy val siteService = new SiteService(siteRepository, slickIo)

}