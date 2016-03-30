package registry

import javax.inject.Inject

import akka.actor.ActorSystem
import akka.persistence.query.PersistenceQuery
import akka.persistence.query.journal.leveldb.scaladsl.LeveldbReadJournal
import akka.persistence.query.scaladsl.{ EventsByTagQuery, ReadJournal }
import com.google.inject.ImplementedBy
import nv.analysis.application.WordCountService
import nv.common.ddd.infrastructure.dao.ProjectionProgressesDao
import nv.common.ddd.infrastructure.projection.{ ResumableProjectionUpdater, ResumableProjectionUpdaterSlick }
import nv.common.ddd.infrastructure.{ DbConfig, IOExecutorSlick }
import play.api.db.slick.DatabaseConfigProvider
import play.db.NamedDatabase
import slick.driver.JdbcProfile

@ImplementedBy(classOf[AnalysisServiceRegistryImpl])
trait AnalysisServiceRegistry {
  val pump: ResumableProjectionUpdater
  val wordCountService: WordCountService
}

class AnalysisServiceRegistryImpl @Inject() (@NamedDatabase("analysis") dbConfigProvider: DatabaseConfigProvider, actorSystem: ActorSystem) extends AnalysisServiceRegistry {

  val dbc = dbConfigProvider.get[JdbcProfile]

  val dbConfig = DbConfig(dbc.db, dbc.driver)

  val ppDao = new ProjectionProgressesDao(dbConfig)

  val slickIo = new IOExecutorSlick(dbConfig)

  lazy val pump = new ResumableProjectionUpdaterSlick {
    override val pp: ProjectionProgressesDao = ppDao
    override val io: IOExecutorSlick = slickIo
    override val readJournal: ReadJournal with EventsByTagQuery = PersistenceQuery(actorSystem)
      .readJournalFor[LeveldbReadJournal](LeveldbReadJournal.Identifier)
    override val projectionId: String = "Discussion"
  }
  val wordCountService: WordCountService = new WordCountService
}
