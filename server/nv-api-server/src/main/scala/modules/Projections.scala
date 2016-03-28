package modules

import akka.actor.ActorSystem
import akka.persistence.query.PersistenceQuery
import akka.persistence.query.journal.leveldb.scaladsl.LeveldbReadJournal
import akka.stream.ActorMaterializer
import nv.common.ddd.infrastructure.projection.Projection

import scala.concurrent.ExecutionContext.Implicits.global

object Projections {
  def start(projections: Seq[Projection])(implicit system: ActorSystem) = {

    val readJournal = PersistenceQuery(system)
      .readJournalFor[LeveldbReadJournal](LeveldbReadJournal.Identifier)
    implicit val mat = ActorMaterializer()(system)

    projections.foreach {
      p ⇒
        p.startProjection(readJournal)
    }

  }
}
