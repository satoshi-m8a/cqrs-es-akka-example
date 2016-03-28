package nv.common.ddd.infrastructure.projection

import akka.actor.ActorSystem
import akka.persistence.query.scaladsl.{ EventsByTagQuery, ReadJournal }
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import nv.common.ddd.infrastructure.IOExecutorSlick
import nv.common.ddd.infrastructure.dao.{ ProjectionProgressesDao, ProjectionProgressDto }

import scala.concurrent.{ ExecutionContext, Future }

trait Projection {
  val projectionId: String

  def update(event: Any): Future[Any]

}

trait ResumableProjectionUpdater {
  val projectionId: String

  def startProjection(update: (Any) ⇒ Future[Any], parallelism: Int = 1)(implicit system: ActorSystem, ec: ExecutionContext): Unit
}

trait ResumableProjectionUpdaterSlick extends ResumableProjectionUpdater {
  val pp: ProjectionProgressesDao
  val io: IOExecutorSlick
  val readJournal: ReadJournal with EventsByTagQuery

  def startProjection(update: (Any) ⇒ Future[Any], parallelism: Int = 1)(implicit system: ActorSystem, ec: ExecutionContext) = {
    implicit val mat = ActorMaterializer()(system)
    io.run(pp.findById(projectionId)).map {
      progressOpt ⇒
        val progress = progressOpt.getOrElse(ProjectionProgressDto(projectionId, 0))
        readJournal
          .eventsByTag(projectionId, progress.offset)
          .mapAsync(parallelism) { envelope ⇒
            update(envelope.event).map(_ ⇒ envelope.offset)
          }
          .mapAsync(1) { offset ⇒ io.run(pp.insertOrUpdate(progress.copy(offset = offset))) }
          .runWith(Sink.ignore)
    }
  }
}