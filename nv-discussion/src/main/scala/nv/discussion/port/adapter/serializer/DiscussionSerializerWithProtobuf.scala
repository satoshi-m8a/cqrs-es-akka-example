package nv.discussion.port.adapter.serializer

import akka.serialization.SerializerWithStringManifest
import nv.account.domain.model.account.AccountId
import nv.discussion.domain.model.discussion.Discussion.Events._
import nv.discussion.domain.model.discussion.{ Comment, DiscussionId, DiscussionProtos }

case object EventDeserializationSkipped

class DiscussionSerializerWithProtobuf extends SerializerWithStringManifest {
  override def identifier: Int = 67876

  final val DiscussionCreatedManifest = classOf[DiscussionCreated].getName

  final val CommentAddedManifest = classOf[CommentAdded].getName

  final val CommentEditedManifest = classOf[CommentEdited].getName

  final val CommentDeletedManifest = classOf[CommentDeleted].getName

  final val MissedEventManifest = classOf[MissedEvent].getName

  //ここに不要なイベントを列記していく
  val SkipEventManifestsEvents = Set(
    "nv.discussion.domain.model.discussion.Discussion$Events$MissedEvent"
  )

  override def manifest(o: AnyRef): String = o.getClass.getName

  override def fromBinary(bytes: Array[Byte], manifest: String): AnyRef = {
    manifest match {
      case m if SkipEventManifestsEvents.contains(m) ⇒
        EventDeserializationSkipped
      case DiscussionCreatedManifest ⇒
        discussionCreated(DiscussionProtos.DiscussionCreated.parseFrom(bytes))
      case CommentAddedManifest ⇒
        commentAdded(DiscussionProtos.CommentAdded.parseFrom(bytes))
      case CommentEditedManifest ⇒
        commentEdited(DiscussionProtos.CommentEdited.parseFrom(bytes))
      case CommentDeletedManifest ⇒
        commentDeleted(DiscussionProtos.CommentDeleted.parseFrom(bytes))
      case MissedEventManifest ⇒
        missedEvent(DiscussionProtos.MissedEvent.parseFrom(bytes))
      case _ ⇒
        throw new IllegalArgumentException("Unable to handle manifest: " + manifest)
    }
  }

  override def toBinary(o: AnyRef): Array[Byte] = o match {
    case s: DiscussionCreated ⇒
      DiscussionProtos.DiscussionCreated.newBuilder
        .setId(s.id.value)
        .setTitle(s.title)
        .setAllowAnonymous(s.allowAnonymous)
        .build().toByteArray
    case s: CommentAdded ⇒
      DiscussionProtos.CommentAdded.newBuilder
        .setId(s.id.value)
        .setCommentId(s.commentId)
        .setText(s.comment.text)
        .setCommentBy(s.comment.by.map(_.value).getOrElse(""))
        .build().toByteArray
    case s: CommentEdited ⇒
      DiscussionProtos.CommentEdited.newBuilder
        .setId(s.id.value)
        .setCommentId(s.commentId)
        .setText(s.comment.text)
        .setCommentBy(s.comment.by.map(_.value).getOrElse(""))
        .build().toByteArray
    case s: CommentDeleted ⇒
      DiscussionProtos.CommentDeleted.newBuilder
        .setId(s.id.value)
        .setCommentId(s.commentId)
        .setBy(s.by.value)
        .build().toByteArray
    case s: MissedEvent ⇒
      DiscussionProtos.MissedEvent.newBuilder
        .setId(s.id.value)
        .build().toByteArray
  }

  private def discussionCreated(p: DiscussionProtos.DiscussionCreated): DiscussionCreated = {
    DiscussionCreated(DiscussionId(p.getId), p.getTitle, p.getAllowAnonymous)
  }

  private def commentAdded(p: DiscussionProtos.CommentAdded): CommentAdded = {
    val commentBy = if (p.getCommentBy.isEmpty) {
      None
    } else {
      Some(AccountId(p.getCommentBy))
    }
    CommentAdded(DiscussionId(p.getId), p.getCommentId, Comment(p.getText, commentBy))
  }

  private def commentEdited(p: DiscussionProtos.CommentEdited): CommentEdited = {
    val commentBy = if (p.getCommentBy.isEmpty) {
      None
    } else {
      Some(AccountId(p.getCommentBy))
    }
    CommentEdited(DiscussionId(p.getId), p.getCommentId, Comment(p.getText, commentBy))
  }

  private def commentDeleted(p: DiscussionProtos.CommentDeleted): CommentDeleted = {
    CommentDeleted(DiscussionId(p.getId), p.getCommentId, AccountId(p.getBy))
  }

  private def missedEvent(p: DiscussionProtos.MissedEvent): MissedEvent = {
    MissedEvent(DiscussionId(p.getId))
  }

}
