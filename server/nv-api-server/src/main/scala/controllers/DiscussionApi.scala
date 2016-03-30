package controllers

import javax.inject.Inject

import nv.discussion.domain.model.discussion.Discussion.Exceptions.AnonymousCommentNotAllowed
import nv.discussion.domain.model.discussion.DiscussionId
import play.api.libs.json.Json
import play.api.mvc.{ Action, Controller }
import presentation.model.discussion.{ AddCommentRequest, CreateDiscussionRequest }
import presentation.service.DiscussionPresentationService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DiscussionApi @Inject() (discussionPresentationService: DiscussionPresentationService) extends Controller {

  def createDiscussion = Action.async(parse.json) {
    request ⇒
      request.body.validate[CreateDiscussionRequest].map {
        req ⇒
          discussionPresentationService.createDiscussion(req).map {
            discussion ⇒
              Ok(Json.toJson(discussion))
          }

      }.recoverTotal {
        case e ⇒
          Future.successful(BadRequest(""))
      }
  }

  def getDiscussion(id: String) = Action.async {
    discussionPresentationService.findById(DiscussionId(id)).map {
      case Some(discussion) ⇒
        Ok(Json.toJson(discussion))
      case _ ⇒
        NotFound("not found")
    }
  }

  def addComment(id: String) = Action.async(parse.json) {
    request ⇒
      request.body.validate[AddCommentRequest].map {
        req ⇒
          discussionPresentationService.addComment(DiscussionId(id), req).map {
            comment ⇒
              Ok(Json.toJson(comment))
          }.recover {
            case AnonymousCommentNotAllowed ⇒
              BadRequest("")
          }

      }.recoverTotal {
        case e ⇒
          Future.successful(BadRequest(""))
      }
  }

  def getComments(id: String) = Action.async {
    request ⇒
      discussionPresentationService.getComments(DiscussionId(id)).map {
        comments ⇒
          Ok(Json.toJson(comments))
      }
  }
}
