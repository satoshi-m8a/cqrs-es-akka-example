package presentation.service

import javax.inject.Inject
import nv.account.domain.model.account.AccountId
import nv.site.domain.model.site.SiteId
import nv.site.infrastructure.dao.SiteDto
import presentation.model.site.{ ErrorResponse, CreateSiteRequest, SitePm }
import registry.SiteServiceRegistry

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class SitePresentationService @Inject() (registry: SiteServiceRegistry) {

  implicit def siteIdToString(siteId: SiteId): String = {
    siteId.value
  }

  implicit def stringToAccountId(value: String): AccountId = {
    AccountId(value)
  }

  implicit def siteDtoToSite(dto: SiteDto): SitePm = {
    SitePm(dto.id, dto.name)
  }

  def createSite(request: CreateSiteRequest): Future[Either[ErrorResponse, SitePm]] = {
    registry.siteService.createSite(request.name, request.accountId).map {
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
}
