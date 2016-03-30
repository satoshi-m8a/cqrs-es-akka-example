package presentation.model.site

import play.api.libs.json._

case class SitePm(id: String, name: String)

object SitePm {
  implicit val siteFormat = Json.format[SitePm]
}
