package registry

import javax.inject.Inject

import akka.actor.ActorSystem
import akka.persistence.query.PersistenceQuery
import akka.persistence.query.journal.leveldb.scaladsl.LeveldbReadJournal
import akka.persistence.query.scaladsl.{ EventsByTagQuery, ReadJournal }
import com.google.inject.ImplementedBy
import nv.common.ddd.application.RegionCommandService
import nv.common.ddd.infrastructure.dao.ProjectionProgressesDao
import nv.common.ddd.infrastructure.projection.{ ResumableProjectionUpdater, ResumableProjectionUpdaterSlick }
import nv.common.ddd.infrastructure.{ DbConfig, IOExecutorSlick }
import nv.discussion.application.{ DiscussionQueryService, DiscussionService }
import nv.discussion.port.adapter.dao.{ CommentsDao, DiscussionsDao }
import nv.discussion.port.adapter.projection.{ CommentUpdater, DiscussionProjection, DiscussionUpdater }
import play.api.db.slick.DatabaseConfigProvider
import play.db.NamedDatabase
import slick.driver.JdbcProfile

@ImplementedBy(classOf[DiscussionServiceRegistryImpl])
trait DiscussionServiceRegistry {

  val discussionService: DiscussionService

  val discussionQueryService: DiscussionQueryService

  val discussionProjection: DiscussionProjection

  val discussionProjectionUpdater: ResumableProjectionUpdater
}

class DiscussionServiceRegistryImpl @Inject() (@NamedDatabase("discussion") dbConfigProvider: DatabaseConfigProvider, actorSystem: ActorSystem) extends DiscussionServiceRegistry {

  val dbc = dbConfigProvider.get[JdbcProfile]

  val dbConfig = DbConfig(dbc.db, dbc.driver)

  val discussionsDao = new DiscussionsDao(dbConfig)

  val commentsDao = new CommentsDao(dbConfig, discussionsDao)

  val ppDao = new ProjectionProgressesDao(dbConfig)

  val slickIo = new IOExecutorSlick(dbConfig)

  val discussionUpdater = new DiscussionUpdater(discussionsDao, slickIo)

  val commentUpdater = new CommentUpdater(commentsDao, slickIo)

  val discussionCommandService = new RegionCommandService("Discussion")(actorSystem)

  lazy val discussionService: DiscussionService = new DiscussionService(discussionCommandService)

  lazy val discussionQueryService: DiscussionQueryService = new DiscussionQueryService(discussionsDao, slickIo)

  lazy val discussionProjection: DiscussionProjection = new DiscussionProjection(discussionUpdater, commentUpdater)

  lazy val readJournal = PersistenceQuery(actorSystem)
    .readJournalFor[LeveldbReadJournal](LeveldbReadJournal.Identifier)

  val discussionProjectionUpdater = new ResumableProjectionUpdaterSlick {
    override val projectionId: String = discussionProjection.projectionId
    override val io: IOExecutorSlick = slickIo
    override val pp: ProjectionProgressesDao = ppDao
    override val readJournal: ReadJournal with EventsByTagQuery = PersistenceQuery(actorSystem)
      .readJournalFor[LeveldbReadJournal](LeveldbReadJournal.Identifier)
  }
}
