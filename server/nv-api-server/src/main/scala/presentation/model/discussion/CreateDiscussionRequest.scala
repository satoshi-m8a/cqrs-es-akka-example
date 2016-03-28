package presentation.model.discussion

import play.api.libs.json.Json

case class CreateDiscussionRequest(title: String, allowAnonymous: Boolean)

object CreateDiscussionRequest {
  implicit val createDiscussionRequest = Json.reads[CreateDiscussionRequest]
}
