package nv.discussion.application

import nv.common.ddd.infrastructure.IOExecutorSlick
import nv.discussion.domain.model.discussion.DiscussionId
import nv.discussion.port.adapter.dao.DiscussionsDao
import nv.discussion.port.adapter.dto.DiscussionDto

import scala.concurrent.Future
//TODO
import scala.concurrent.ExecutionContext.Implicits.global

class DiscussionQueryService(discussionsDao: DiscussionsDao, io: IOExecutorSlick) {

  def findById(id: DiscussionId): Future[Option[DiscussionDto]] = {
    io.run {
      discussionsDao.findById(id)
    }
  }

}
