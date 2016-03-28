package nv.discussion.port.adapter.projection

import nv.common.ddd.infrastructure.IOExecutorSlick
import nv.common.ddd.infrastructure.projection.ReadModelUpdater
import nv.discussion.domain.model.discussion.Discussion.Events.{ CommentEdited, CommentAdded, DiscussionEvent }
import nv.discussion.port.adapter.dao.CommentsDao
import nv.discussion.port.adapter.dto.CommentDto

import scala.concurrent.Future

//TODO
import scala.concurrent.ExecutionContext.Implicits.global

class CommentUpdater(commentsDao: CommentsDao, io: IOExecutorSlick) extends ReadModelUpdater {
  def transformDto(dto: Option[CommentDto], event: DiscussionEvent): CommentDto = event match {
    case e: CommentAdded ⇒
      CommentDto(e.id, e.commentId, e.comment.text, e.comment.by)
    case _ ⇒
      throw new IllegalArgumentException("Unhandled Event " + event)
  }

  def insertOrUpdate(dto: CommentDto): Future[Any] = {
    io.run(for {
      result ← commentsDao.insertOrUpdate(dto)
    } yield ())
  }

  def handleUpdate: UpdateReadModel = {
    case e: CommentAdded ⇒
      insertOrUpdate(transformDto(None, e))
    case e: CommentEdited ⇒
      insertOrUpdate(transformDto(None, e))
  }
}
