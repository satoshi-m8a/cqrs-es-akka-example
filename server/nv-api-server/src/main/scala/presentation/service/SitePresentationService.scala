package presentation.service

import javax.inject.Inject

import nv.account.domain.model.account.AccountId
import nv.site.domain.model.article.ArticleId
import nv.site.domain.model.site.SiteId
import nv.site.infrastructure.dao.{ ArticleDto, SiteDto }
import presentation.model.site.{ ArticlePm, CreateSiteRequest, ErrorResponse, SitePm }
import registry.SiteServiceRegistry

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SitePresentationService @Inject() (registry: SiteServiceRegistry) {

  implicit def siteIdToString(siteId: SiteId): String = {
    siteId.value
  }

  implicit def articleIdToString(articleId: ArticleId): String = {
    articleId.value
  }

  implicit def stringToAccountId(value: String): AccountId = {
    AccountId(value)
  }

  implicit def siteDtoToSite(dto: SiteDto): SitePm = {
    SitePm(dto.id, dto.name)
  }

  implicit def articleDtoToArticle(dto: ArticleDto): ArticlePm = {
    ArticlePm(dto.id, dto.title)
  }

  def createSite(request: CreateSiteRequest): Future[Either[ErrorResponse, SitePm]] = {
    //TODO
    registry.siteService.createSite(request.name, AccountId("todo")).map {
      evt ⇒
        Right(SitePm(evt.id, evt.name))
    }.recover {
      //TODO
      case e: Throwable ⇒
        Left(ErrorResponse())
    }
  }

  def findById(id: SiteId): Future[Option[SitePm]] = {
    registry.siteQueryService.findById(id).map {
      case Some(dto) ⇒
        Some(dto)
      case _ ⇒
        None
    }
  }

  def findSites(): Future[Seq[SitePm]] = {
    registry.siteQueryService.findSites().map(_.map(siteDtoToSite))
  }

  def findArticles(id: SiteId): Future[Seq[ArticlePm]] = {
    registry.siteQueryService.findArticles(id).map {
      articles ⇒
        articles.map(articleDtoToArticle)
    }
  }
}
