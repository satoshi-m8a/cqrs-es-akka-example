package controllers

import javax.inject.Inject

import nv.account.domain.model.account.AccountId
import nv.site.domain.model.site.SiteId
import play.api.libs.json.Json
import presentation.model.site.{ Site, ErrorResponse, CreateSiteRequest }
import presentation.service.SitePresentationService
import registry.SiteServiceRegistry
import nv.site.application.SiteQueryService
import play.api.mvc.{ Action, Controller }

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SiteApi @Inject() (registry: SiteServiceRegistry, sitePresentationService: SitePresentationService) extends Controller {

  def createSite = Action.async(parse.json) {
    request ⇒
      request.body.validate[CreateSiteRequest].map {
        req ⇒
          sitePresentationService.createSite(req).map {
            case Right(site) ⇒
              Ok(Json.toJson(site))
            case Left(error) ⇒
              BadRequest("")
          }
      }.recoverTotal {
        case e ⇒
          Future.successful(BadRequest("error"))
      }
  }

  def getSite(id: String) = Action.async {

    sitePresentationService.findById(SiteId(id)).map {
      case Some(site) ⇒
        Ok(Json.toJson(site))
      case _ ⇒
        NotFound("not found")
    }
  }

}
