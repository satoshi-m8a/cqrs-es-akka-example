package nv.site.infrastructure.dao

import nv.common.ddd.infrastructure.{ DaoHelpers, DbConfig }
import nv.site.domain.model.article.ArticleId
import nv.site.domain.model.site.SiteId

import scala.concurrent.ExecutionContext

class ArticlesDao(val dbConfig: DbConfig, val sitesDao: SitesDao) extends DefaultMappers {

  import DaoHelpers._
  import dbConfig.driver.api._

  val articles = TableQuery[Articles]

  class Articles(tag: Tag) extends Table[ArticleDto](tag, n"Articles") {
    def siteId = column[SiteId](n"site_id", O.Length(36))

    def articleId = column[ArticleId](n"article_id", O.Length(36))

    def title = column[String](n"title")

    def pk = primaryKey(n"pk_site_article", (siteId, articleId))

    def site = foreignKey(n"site_id_fk", siteId, sitesDao.sites)(_.id, onDelete = ForeignKeyAction.Cascade)

    def * = (articleId, siteId, title) <> ((ArticleDto.apply _).tupled, ArticleDto.unapply)
  }

  def findBy(id: SiteId)(implicit ec: ExecutionContext): DBIO[Seq[ArticleDto]] = {
    articles.filter(_.siteId === id).result
  }

  def findById(id: ArticleId)(implicit ec: ExecutionContext): DBIO[Option[ArticleDto]] = {
    val q = articles.filter(_.articleId === id)

    q.result.map {
      case Nil ⇒ None
      case s ⇒
        s.headOption
    }
  }

}

case class ArticleDto(id: ArticleId, siteId: SiteId, title: String)