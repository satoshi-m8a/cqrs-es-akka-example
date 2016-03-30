package presentation.model.discussion

import play.api.libs.json.Json

case class CommentPm(id: Long, text: String, name: String)

object CommentPm {
  implicit val comment = Json.format[CommentPm]
}

