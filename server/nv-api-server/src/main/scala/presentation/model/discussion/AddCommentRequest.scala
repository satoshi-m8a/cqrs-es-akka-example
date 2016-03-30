package presentation.model.discussion

import play.api.libs.json.Json

case class AddCommentRequest(text: String)

object AddCommentRequest {
  implicit val addCommentRequest = Json.reads[AddCommentRequest]
}

