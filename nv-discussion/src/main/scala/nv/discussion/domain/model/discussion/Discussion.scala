package nv.discussion.domain.model.discussion

import java.util.UUID

import akka.actor.Props
import nv.common.ddd.domain.{ AggregateRoot, AggregateState, Command, DomainEvent }
import nv.discussion.domain.model.discussion.Discussion.Commands.{ AddComment, CreateDiscussion }
import nv.discussion.domain.model.discussion.Discussion.Events.{ CommentAdded, DiscussionCreated, DiscussionEvent }
import nv.discussion.domain.model.discussion.Discussion.Exceptions.AnonymousCommentNotAllowed

import scala.reflect._

object Discussion {

  def props = Props[Discussion]

  def nextId: DiscussionId = DiscussionId(UUID.randomUUID().toString)

  object Commands {

    sealed trait DiscussionCommand extends Command[DiscussionId]

    case class CreateDiscussion(id: DiscussionId, title: String, allowAnonymous: Boolean) extends DiscussionCommand

    case class AddComment(id: DiscussionId, comment: Comment) extends DiscussionCommand

    case class EditComment(id: DiscussionId, commentId: Long, comment: Comment) extends DiscussionCommand

  }

  object Events {

    sealed trait DiscussionEvent extends DomainEvent {
      val id: DiscussionId
    }

    case class DiscussionCreated(id: DiscussionId, title: String, allowAnonymous: Boolean) extends DiscussionEvent

    case class CommentAdded(id: DiscussionId, commentId: Long, comment: Comment) extends DiscussionEvent

    case class CommentEdited(id: DiscussionId, commentId: Long, comment: Comment) extends DiscussionEvent

  }

  object Exceptions {

    case object AnonymousCommentNotAllowed extends Exception

  }

}

class Discussion extends AggregateRoot[DiscussionState, DiscussionEvent] {
  override def domainEventClassTag: ClassTag[DiscussionEvent] = classTag[DiscussionEvent]

  override def aggregateStateClassTag: ClassTag[DiscussionState] = classTag[DiscussionState]

  override def initialState: DiscussionState = DiscussionState("", allowAnonymous = true, 0)

  override def handleCommand: Receive = {
    case cmd: CreateDiscussion ⇒
      if (!initialized) {
        raise(DiscussionCreated(cmd.id, cmd.title, cmd.allowAnonymous))
      } else {
        sender() ! DiscussionCreated(cmd.id, state.title, cmd.allowAnonymous)
      }
    case cmd: AddComment ⇒
      if (state.allowAnonymous) {
        raise(CommentAdded(cmd.id, state.nextCommentId, cmd.comment))
      } else {
        sender() ! akka.actor.Status.Failure(AnonymousCommentNotAllowed)
      }
  }
}

case class DiscussionState(title: String, allowAnonymous: Boolean, numOfComments: Long) extends AggregateState[DiscussionState, DiscussionEvent] {
  override def handle: HandleState = {
    case e: DiscussionCreated ⇒
      this.copy(title = e.title, allowAnonymous = e.allowAnonymous)
    case e: CommentAdded ⇒
      this.copy(numOfComments = numOfComments + 1)
  }

  def nextCommentId: Long = numOfComments + 1
}

