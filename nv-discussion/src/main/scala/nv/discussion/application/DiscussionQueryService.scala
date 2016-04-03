package nv.discussion.application

import nv.common.ddd.infrastructure.IOExecutorSlick
import nv.discussion.domain.model.discussion.DiscussionId
import nv.discussion.port.adapter.dao.{ CommentsDao, DiscussionsDao }
import nv.discussion.port.adapter.dto.{ CommentDto, DiscussionDto }

import scala.concurrent.Future

//TODO
import scala.concurrent.ExecutionContext.Implicits.global

class DiscussionQueryService(discussionsDao: DiscussionsDao, commentsDao: CommentsDao, io: IOExecutorSlick) {

  def findById(id: DiscussionId): Future[Option[DiscussionDto]] = {
    io.run {
      discussionsDao.findById(id)
    }
  }

  //TODO
  def findDiscussions(): Future[Seq[DiscussionDto]] = {
    import discussionsDao.dbConfig.driver.api._
    io.run {
      discussionsDao.discussions.result
    }
  }

  def findAllCommentsBy(id: DiscussionId): Future[Seq[CommentDto]] = {
    io.run(commentsDao.findAllBy(id))
  }

}
