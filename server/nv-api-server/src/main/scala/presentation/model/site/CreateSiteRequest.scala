package presentation.model.site

import play.api.libs.json.Json

case class CreateSiteRequest(name: String)

object CreateSiteRequest {
  implicit val createSiteRequestRead = Json.reads[CreateSiteRequest]
}
