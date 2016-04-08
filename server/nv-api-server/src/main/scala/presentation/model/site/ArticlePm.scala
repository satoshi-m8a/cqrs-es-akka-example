package presentation.model.site

import play.api.libs.json.Json

case class ArticlePm(id: String, title: String)

object ArticlePm {
  implicit val articleFormat = Json.format[ArticlePm]
}
