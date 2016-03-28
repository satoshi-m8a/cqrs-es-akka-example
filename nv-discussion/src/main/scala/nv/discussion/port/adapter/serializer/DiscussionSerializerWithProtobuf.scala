package nv.discussion.port.adapter.serializer

import akka.serialization.SerializerWithStringManifest
import nv.account.domain.model.account.AccountId
import nv.discussion.domain.model.discussion.Discussion.Events.{ CommentAdded, DiscussionCreated }
import nv.discussion.domain.model.discussion.{ Comment, DiscussionId, DiscussionProtos }

class DiscussionSerializerWithProtobuf extends SerializerWithStringManifest {
  override def identifier: Int = 67876

  final val DiscussionCreatedManifest = classOf[DiscussionCreated].getName

  final val CommentAddedManifest = classOf[CommentAdded].getName

  override def manifest(o: AnyRef): String = o.getClass.getName

  override def fromBinary(bytes: Array[Byte], manifest: String): AnyRef = {
    manifest match {
      case DiscussionCreatedManifest ⇒
        discussionCreated(DiscussionProtos.DiscussionCreated.parseFrom(bytes))
      case CommentAddedManifest ⇒
        commentAdded(DiscussionProtos.CommentAdded.parseFrom(bytes))
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

}
