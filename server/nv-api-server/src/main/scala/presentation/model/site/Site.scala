package presentation.model.site

import play.api.libs.json._

case class Site(id: String, name: String)

object Site {
  implicit val siteFormat = Json.format[Site]
}
