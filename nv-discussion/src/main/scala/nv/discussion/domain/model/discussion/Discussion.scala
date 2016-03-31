package nv.discussion.domain.model.discussion

import java.util.UUID

import akka.actor.Props
import nv.account.domain.model.account.AccountId
import nv.common.ddd.domain.{ AggregateRoot, AggregateState, Command, DomainEvent }
import nv.discussion.domain.model.discussion.Discussion.Commands._
import nv.discussion.domain.model.discussion.Discussion.Events._
import nv.discussion.domain.model.discussion.Discussion.Exceptions.{ AnonymousCommentNotAllowed, DeleteNotAllowed, EditNotAllowed }

import scala.reflect._

object Discussion {

  def props = Props[Discussion]

  def nextId: DiscussionId = DiscussionId(UUID.randomUUID().toString)

  /**
    *
    */
  object Commands {

    sealed trait DiscussionCommand extends Command[DiscussionId]

    case class CreateDiscussion(id: DiscussionId, title: String, allowAnonymous: Boolean) extends DiscussionCommand

    case class AddComment(id: DiscussionId, comment: Comment) extends DiscussionCommand

    case class EditComment(id: DiscussionId, commentId: Long, comment: Comment) extends DiscussionCommand

    case class DeleteComment(id: DiscussionId, commentId: Long, by: AccountId) extends DiscussionCommand

    /**
      * 失敗例、シリアライザとイベントアダプタで対処したらこれは消して良い
      *
      * @see nv.discussion.port.adapter.serializer.DiscussionSerializerWithProtobuf
      * @param id
      */
    case class MissedCommand(id: DiscussionId) extends DiscussionCommand

  }

  object Events {

    sealed trait DiscussionEvent extends DomainEvent {
      val id: DiscussionId
    }

    case class DiscussionCreated(id: DiscussionId, title: String, allowAnonymous: Boolean) extends DiscussionEvent

    case class CommentAdded(id: DiscussionId, commentId: Long, comment: Comment) extends DiscussionEvent

    case class CommentEdited(id: DiscussionId, commentId: Long, comment: Comment) extends DiscussionEvent

    case class CommentDeleted(id: DiscussionId, commentId: Long, by: AccountId) extends DiscussionEvent

    /**
      * 失敗例、シリアライザとイベントアダプタで対処したらこれは消して良い
      *
      * @see nv.discussion.port.adapter.serializer.DiscussionSerializerWithProtobuf
      * @param id
      */
    case class MissedEvent(id: DiscussionId) extends DiscussionEvent

  }

  object Exceptions {

    case object AnonymousCommentNotAllowed extends Exception

    case object EditNotAllowed extends Exception

    case object DeleteNotAllowed extends Exception

  }

}

class Discussion extends AggregateRoot[DiscussionState, DiscussionEvent] {
  override def domainEventClassTag: ClassTag[DiscussionEvent] = classTag[DiscussionEvent]

  override def aggregateStateClassTag: ClassTag[DiscussionState] = classTag[DiscussionState]

  override def initialState: DiscussionState = DiscussionState("", allowAnonymous = true, Map.empty)

  override def handleCommand: Receive = {
    case cmd: CreateDiscussion ⇒
      if (!initialized) {
        raise(DiscussionCreated(cmd.id, cmd.title, cmd.allowAnonymous))
      } else {
        sender() ! DiscussionCreated(cmd.id, state.title, cmd.allowAnonymous)
      }
    case cmd: AddComment ⇒
      if (cmd.comment.by.isDefined || state.allowAnonymous) {
        raise(CommentAdded(cmd.id, state.nextCommentId, cmd.comment))
      } else {
        sender() ! akka.actor.Status.Failure(AnonymousCommentNotAllowed)
      }
    case cmd: EditComment ⇒
      if (state.isOwnedBy(cmd.commentId, cmd.comment.by)) {
        raise(CommentEdited(cmd.id, cmd.commentId, cmd.comment))
      } else {
        sender() ! akka.actor.Status.Failure(EditNotAllowed)
      }
    case cmd: DeleteComment ⇒
      if (state.isOwnedBy(cmd.commentId, Some(cmd.by))) {
        raise(CommentDeleted(cmd.id, cmd.commentId, cmd.by))
      } else {
        sender() ! akka.actor.Status.Failure(DeleteNotAllowed)
      }

    /**
      * 失敗例、シリアライザとイベントアダプタで対処したらこれは消して良い
      *
      * @see nv.discussion.port.adapter.serializer.DiscussionSerializerWithProtobuf
      */
    case cmd: MissedCommand ⇒
      raise(MissedEvent(cmd.id))
  }

}

case class DiscussionState(title: String, allowAnonymous: Boolean, comments: Map[Long, Option[AccountId]]) extends AggregateState[DiscussionState, DiscussionEvent] {
  override def handle: HandleState = {
    case e: DiscussionCreated ⇒
      this.copy(title = e.title, allowAnonymous = e.allowAnonymous)
    case e: CommentAdded ⇒
      this.copy(comments = this.comments + (nextCommentId → e.comment.by))
    case e: CommentEdited ⇒
      this
    case e: CommentDeleted ⇒
      this.copy(comments = this.comments - e.commentId)
  }

  def nextCommentId: Long = {
    if (this.comments.isEmpty) {
      1
    } else {
      this.comments.keys.max + 1
    }
  }

  def isOwnedBy(commentId: Long, accountId: Option[AccountId]): Boolean = {
    (for {
      by ← accountId
      o ← comments.get(commentId)
      aid ← o
      if by == aid
    } yield {
      true
    }).getOrElse {
      false
    }
  }
}

