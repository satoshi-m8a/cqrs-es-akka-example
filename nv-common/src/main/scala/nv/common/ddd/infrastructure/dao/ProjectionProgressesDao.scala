package nv.common.ddd.infrastructure.dao

import nv.common.ddd.infrastructure.{ DaoHelpers, DbConfig }

import scala.concurrent.ExecutionContext

class ProjectionProgressesDao(val dbConfig: DbConfig) {

  import DaoHelpers._
  import dbConfig.driver.api._

  val projectionProgresses = TableQuery[ProjectionProgresses]

  class ProjectionProgresses(tag: Tag) extends Table[ProjectionProgressDto](tag, n"Projections") {
    def id = column[String](n"id", O.PrimaryKey)

    def offset = column[Long](n"progress")

    def * = (id, offset) <> ((ProjectionProgressDto.apply _).tupled, ProjectionProgressDto.unapply)
  }

  def findById(id: String)(implicit ec: ExecutionContext): DBIO[Option[ProjectionProgressDto]] = {
    val q = projectionProgresses.filter(_.id === id)

    q.result.map {
      case Nil ⇒ None
      case s ⇒
        s.headOption
    }
  }

  def insertOrUpdate(dto: ProjectionProgressDto)(implicit ec: ExecutionContext): DBIO[ProjectionProgressDto] = {
    projectionProgresses.insertOrUpdate(dto).map(_ ⇒ dto)
  }

}

case class ProjectionProgressDto(id: String, offset: Long)