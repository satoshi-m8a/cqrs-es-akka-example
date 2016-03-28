package nv.discussion.port.adapter.dao

import nv.account.domain.model.account.AccountId
import nv.account.infrastructure.dao.{ AccountsDao, DefaultMappers ⇒ AccountDefaultMappers }
import nv.common.ddd.infrastructure.{ DaoHelpers, DbConfig }
import nv.discussion.domain.model.discussion.DiscussionId
import nv.discussion.port.adapter.dto.CommentDto

import scala.concurrent.ExecutionContext

class CommentsDao(val dbConfig: DbConfig, val discussionsDao: DiscussionsDao) extends DefaultMappers with AccountDefaultMappers {

  import DaoHelpers._
  import dbConfig.driver.api._

  val comments = TableQuery[Comments]

  class Comments(tag: Tag) extends Table[CommentDto](tag, n"Comments") {
    def discussionId = column[DiscussionId](n"discussion_id", O.Length(36))

    def commentId = column[Long](n"comment_id")

    def text = column[String](n"text")

    def commentBy = column[Option[AccountId]](n"comment_by")

    def pk = primaryKey(n"pk_discussion_comment", (discussionId, commentId))

    def discussion = foreignKey(n"discussion_id_fk", discussionId, discussionsDao.discussions)(_.id, onDelete = ForeignKeyAction.Cascade)

    def * = (discussionId, commentId, text, commentBy) <> ((CommentDto.apply _).tupled, CommentDto.unapply)
  }

  def insertOrUpdate(dto: CommentDto)(implicit ec: ExecutionContext): DBIO[CommentDto] = {
    comments.insertOrUpdate(dto).map(_ ⇒ dto)
  }

}

