package presentation.model.discussion

import play.api.libs.json.Json

case class Discussion(id: String, title: String)

object Discussion {
  implicit val discussionFormat = Json.format[Discussion]
}