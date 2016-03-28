package mv.site.application

import nv.account.domain.model.account.AccountId
import nv.common.ddd.infrastructure.{IOExecutorSlick, DbConfig}
import nv.site.application.SiteService
import nv.site.infrastructure.dao.SitesDao
import nv.site.infrastructure.repository.SiteRepositorySlick
import nv.testkit.persistent.DbSpecSupport
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.Await
import scala.concurrent.duration._

class SiteServiceSpec extends WordSpecLike with Matchers with BeforeAndAfterAll with DbSpecSupport with ScalaFutures {
  val dbConfig = DbConfig(db, slick.driver.H2Driver)

  implicit val defaultPatience =
    PatienceConfig(timeout = Span(5, Seconds), interval = Span(500, Millis))

  import dbConfig.driver.api._

  override def beforeAll: Unit = {
    super.beforeAll
    Await.result(db.run(DBIO.seq(
      sitesDao.sites.schema.create
    )), Duration.Inf)
  }

  override def afterAll {
    super.afterAll
  }

  val sitesDao = new SitesDao(dbConfig)

  val siteRepository = new SiteRepositorySlick(dbConfig, sitesDao)

  val io = new IOExecutorSlick(dbConfig)

  val siteService = new SiteService(siteRepository, io)

  "SiteService" should {
    "create site" in {
      whenReady(siteService.createSite("test", AccountId("1"))) {
        r =>
          assert(r.name == "test")
      }
    }

    "create site2" in {
      whenReady(siteService.createSite("test2", AccountId("1"))) {
        r =>
          assert(r.name == "test2")
      }
    }
  }
}
