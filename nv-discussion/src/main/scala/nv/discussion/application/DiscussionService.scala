package nv.discussion.application

import nv.common.ddd.application.CommandService
import nv.discussion.domain.model.discussion.Discussion.Commands.{ AddComment, CreateDiscussion }
import nv.discussion.domain.model.discussion.Discussion.Events.{ CommentAdded, DiscussionCreated }
import nv.discussion.domain.model.discussion.{ Comment, Discussion, DiscussionId }

import scala.concurrent.Future

class DiscussionService(discussionCommandService: CommandService) {

  def createDiscussion(title: String, allowAnonymous: Boolean): Future[DiscussionCreated] = {
    discussionCommandService.send(CreateDiscussion(Discussion.nextId, title, allowAnonymous)).mapTo[DiscussionCreated]
  }

  def addComment(id: DiscussionId, comment: Comment): Future[CommentAdded] = {
    discussionCommandService.send(AddComment(id, comment)).mapTo[CommentAdded]
  }
}
