package nv.discussion.port.adapter.projection

import akka.persistence.query.PersistenceQuery
import akka.persistence.query.journal.leveldb.scaladsl.LeveldbReadJournal
import akka.persistence.query.scaladsl.{ EventsByTagQuery, ReadJournal }
import akka.util.Timeout
import nv.common.ddd.infrastructure.dao.ProjectionProgressesDao
import nv.common.ddd.infrastructure.projection.ResumableProjectionUpdaterSlick
import nv.common.ddd.infrastructure.{ DbConfig, IOExecutorSlick }
import nv.discussion.domain.model.discussion.Discussion.Commands.CreateDiscussion
import nv.discussion.domain.model.discussion.{ Discussion, DiscussionId }
import nv.discussion.port.adapter.dao.{ CommentsDao, DiscussionsDao }
import nv.discussion.port.adapter.dto.DiscussionDto
import nv.testkit.persistent.DbSpecSupport
import nv.testkit.support.TestSupport

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class DiscussionProjectionSpec extends TestSupport with DbSpecSupport {

  implicit val timeout: Timeout = Timeout(3.seconds)

  val dbConfig = DbConfig(db, slick.driver.H2Driver)

  import dbConfig.driver.api._

  override def beforeAll = {
    super.beforeAll

    val schemas = ddao.discussions.schema ++ cdao.comments.schema ++ ppdao.projectionProgresses.schema
    Await.result(db.run(DBIO.seq(
      schemas.create
    )), Duration.Inf)
  }

  override def afterAll {
    super.afterAll
  }

  val sio = new IOExecutorSlick(dbConfig)

  val ddao = new DiscussionsDao(dbConfig)
  val discussionUpdater = new DiscussionUpdater(ddao, sio)

  val cdao = new CommentsDao(dbConfig, ddao)

  val commentUpdater = new CommentUpdater(cdao, sio)

  val ppdao = new ProjectionProgressesDao(dbConfig)

  "DiscussionProjection" must {
    val id = DiscussionId("1")
    val actorRef = system.actorOf(Discussion.props, name = id.value)
    watch(actorRef)

    "update" in {

      val projection = new DiscussionProjection(discussionUpdater, commentUpdater)

      val projectionUpdater = new ResumableProjectionUpdaterSlick {
        override val projectionId: String = projection.projectionId
        override val io: IOExecutorSlick = sio
        override val pp: ProjectionProgressesDao = ppdao
        override val readJournal: ReadJournal with EventsByTagQuery = PersistenceQuery(system)
          .readJournalFor[LeveldbReadJournal](LeveldbReadJournal.Identifier)
      }

      projectionUpdater.startProjection(projection.update)

      actorRef ! CreateDiscussion(id, "title", true)

      awaitAssert {
        val f = sio.run(ddao.findById(id))
        f.futureValue should ===(Some(DiscussionDto(id, "title", true)))
      }
    }
  }

}