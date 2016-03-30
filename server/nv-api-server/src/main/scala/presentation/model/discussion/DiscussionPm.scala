package presentation.model.discussion

import play.api.libs.json.Json

case class DiscussionPm(id: String, title: String)

object DiscussionPm {
  implicit val discussionFormat = Json.format[DiscussionPm]
}