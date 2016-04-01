package nv.batch

import akka.actor.ActorSystem
import akka.cluster.sharding.{ ClusterSharding, ShardRegion }
import nv.common.ddd.application.{ RegionCommandProxy, RegionCommandService }
import nv.common.ddd.domain.Command
import nv.discussion.domain.model.discussion.Discussion
import nv.discussion.domain.model.discussion.Discussion.Commands.CreateDiscussion
import nv.discussion.domain.model.discussion.Discussion.Events.DiscussionCreated

import scala.concurrent.Future

object ExampleBatch {
  val extractEntityId: ShardRegion.ExtractEntityId = {
    case c: Command[_] ⇒ (c.id.value.toString, c)
  }

  val numberOfShards = 20

  val extractShardId: ShardRegion.ExtractShardId = {
    case c: Command[_] ⇒ (Math.abs(c.id.value.toString.hashCode) % numberOfShards).toString
  }

  def run(implicit system: ActorSystem): Future[DiscussionCreated] = {
    val commandService = new RegionCommandProxy("Discussion", extractEntityId, extractShardId)
    println("running")
    commandService.send(CreateDiscussion(Discussion.nextId, "Example Discussion", true)).mapTo[DiscussionCreated]
  }

}