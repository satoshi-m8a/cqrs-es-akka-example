package nv.discussion.port.adapter.dao

import nv.common.ddd.infrastructure.{ DaoHelpers, DbConfig }
import nv.discussion.domain.model.discussion.DiscussionId
import nv.discussion.port.adapter.dto.DiscussionDto

import scala.concurrent.ExecutionContext

class DiscussionsDao(val dbConfig: DbConfig) extends DefaultMappers {

  import DaoHelpers._
  import dbConfig.driver.api._

  val discussions = TableQuery[Discussions]

  class Discussions(tag: Tag) extends Table[DiscussionDto](tag, n"Discussions") {
    def id = column[DiscussionId](n"id", O.PrimaryKey, O.Length(36))

    def title = column[String](n"title")

    def allowAnonymous = column[Boolean](n"allow_anonymous")

    def * = (id, title, allowAnonymous) <> ((DiscussionDto.apply _).tupled, DiscussionDto.unapply)
  }

  def findById(id: DiscussionId)(implicit ec: ExecutionContext): DBIO[Option[DiscussionDto]] = {
    val q = discussions.filter(_.id === id)

    q.result.map {
      case Nil ⇒ None
      case s ⇒
        s.headOption
    }
  }

  def insertOrUpdate(dto: DiscussionDto)(implicit ec: ExecutionContext): DBIO[DiscussionDto] = {
    discussions.insertOrUpdate(dto).map(_ ⇒ dto)
  }

}
