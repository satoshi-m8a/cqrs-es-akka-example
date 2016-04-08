package nv.site.infrastructure.repository

import nv.common.ddd.infrastructure.DbConfig
import nv.site.domain.model.article.{ Article, ArticleRepository }
import nv.site.infrastructure.dao.{ ArticleDto, ArticlesDao }
import slick.dbio.DBIO

import scala.concurrent.ExecutionContext

class ArticleRepositorySlick(dbConfig: DbConfig, articlesDao: ArticlesDao) extends ArticleRepository[DBIO] {

  override def save(article: Article)(implicit ec: ExecutionContext): DBIO[Article] = {
    import dbConfig.driver.api._

    articlesDao.articles.insertOrUpdate(ArticleDto(article.id, article.siteId, article.title)).map {
      _ ⇒
        article
    }
  }
}
