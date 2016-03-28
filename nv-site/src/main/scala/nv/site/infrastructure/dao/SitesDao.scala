package nv.site.infrastructure.dao

import nv.common.ddd.infrastructure.{ DaoHelpers, DbConfig }
import nv.site.domain.model.site.SiteId

import scala.concurrent.ExecutionContext

class SitesDao(val dbConfig: DbConfig) extends DefaultMappers {
  import DaoHelpers._
  import dbConfig.driver.api._

  val sites = TableQuery[Sites]

  class Sites(tag: Tag) extends Table[SiteDto](tag, n"Sites") {
    def id = column[SiteId](n"id", O.PrimaryKey, O.Length(36))

    def name = column[String](n"name")

    def * = (id, name) <> ((SiteDto.apply _).tupled, SiteDto.unapply)
  }

  def findById(id: SiteId)(implicit ec: ExecutionContext): DBIO[Option[SiteDto]] = {
    val q = sites.filter(_.id === id)

    q.result.map {
      case Nil ⇒ None
      case s ⇒
        s.headOption
    }
  }

}

case class SiteDto(id: SiteId, name: String)