package presentation.service

import javax.inject.Inject

import nv.account.domain.model.account.AccountId
import nv.discussion.domain.model.discussion.{ Comment, DiscussionId }
import nv.discussion.port.adapter.dto.DiscussionDto
import presentation.model.discussion._
import registry.{ AccountServiceRegistry, DiscussionServiceRegistry }

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DiscussionPresentationService @Inject() (registry: DiscussionServiceRegistry, accountServiceRegistry: AccountServiceRegistry) {

  implicit def discussionIdToString(id: DiscussionId): String = {
    id.value
  }

  implicit def discussionDtoToDiscussion(dto: DiscussionDto): DiscussionPm = {
    DiscussionPm(dto.id, dto.title)
  }

  def createDiscussion(request: CreateDiscussionRequest): Future[DiscussionPm] = {
    registry.discussionService.createDiscussion(request.title, request.allowAnonymous).map {
      evt ⇒
        DiscussionPm(evt.id, evt.title)
    }
  }

  def findDiscussions(): Future[Seq[DiscussionPm]] = {
    registry.discussionQueryService.findDiscussions().map {
      discussions ⇒
        discussions.map(s ⇒ discussionDtoToDiscussion(s))
    }
  }

  def findById(discussionId: DiscussionId): Future[Option[DiscussionPm]] = {
    registry.discussionQueryService.findById(discussionId).map {
      case Some(dto) ⇒
        Some(dto)
      case _ ⇒
        None
    }
  }

  def addComment(id: DiscussionId, request: AddCommentRequest): Future[CommentPm] = {
    //TODO add accountId
    for {
      evt ← registry.discussionService.addComment(id, Comment(request.text, None))
      account ← accountServiceRegistry.accountService.getBy(Some(AccountId("todo")))
    } yield {
      CommentPm(evt.commentId, evt.comment.text, account.displayName)
    }
  }

  def getComments(id: DiscussionId): Future[Seq[CommentPm]] = {
    for {
      comments ← registry.discussionQueryService.findAllCommentsBy(id)
      accounts ← Future.sequence(comments.map(c ⇒ accountServiceRegistry.accountService.getBy(c.commentBy)))
    } yield {
      comments.zip(accounts).map {
        case (c, a) ⇒
          CommentPm(c.commentId, c.text, a.displayName)
      }
    }
  }

}
