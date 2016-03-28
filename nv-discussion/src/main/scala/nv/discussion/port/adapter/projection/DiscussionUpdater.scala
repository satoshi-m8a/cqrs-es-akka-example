package nv.discussion.port.adapter.projection

import nv.common.ddd.infrastructure.IOExecutorSlick
import nv.common.ddd.infrastructure.projection.ReadModelUpdater
import nv.discussion.domain.model.discussion.Discussion.Events.{ DiscussionCreated, DiscussionEvent }
import nv.discussion.port.adapter.dao.DiscussionsDao
import nv.discussion.port.adapter.dto.DiscussionDto

import scala.concurrent.Future

//TODO
import scala.concurrent.ExecutionContext.Implicits.global

class DiscussionUpdater(discussionsDao: DiscussionsDao, io: IOExecutorSlick) extends ReadModelUpdater {

  def transformDto(discussion: Option[DiscussionDto], event: DiscussionEvent): DiscussionDto = event match {
    case e: DiscussionCreated ⇒
      DiscussionDto(e.id, e.title, e.allowAnonymous)
    case _ ⇒
      throw new IllegalArgumentException("Unhandled Event " + event)
  }

  def insertOrUpdate(dto: DiscussionDto): Future[Any] = {
    io.run(for {
      result ← discussionsDao.insertOrUpdate(dto)
    } yield ())
  }

  def handleUpdate: UpdateReadModel = {
    case e: DiscussionCreated ⇒
      insertOrUpdate(transformDto(None, e))
  }

}
