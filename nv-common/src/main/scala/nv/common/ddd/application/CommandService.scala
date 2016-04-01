package nv.common.ddd.application

import akka.actor.{ ActorRef, ActorSystem }
import akka.cluster.sharding.{ ClusterSharding, ShardRegion }
import akka.pattern.ask
import akka.util.Timeout
import nv.common.ddd.domain.{ Command, EntityId }

import scala.concurrent.Future
import scala.concurrent.duration._

trait CommandService {
  val defaultTimeout = 60 seconds
  val recipient: ActorRef

  def send[T <: EntityId[_]](command: Command[T], t: Timeout = Timeout(defaultTimeout)): Future[Any] = recipient.ask(command)(t)
}

class RegionCommandService(regionName: String)(implicit system: ActorSystem) extends CommandService {
  lazy val recipient: ActorRef = ClusterSharding(system).shardRegion(regionName)
}

class RegionCommandProxy(regionName: String, extractEntityId: ShardRegion.ExtractEntityId, extractShardId: ShardRegion.ExtractShardId)(implicit system: ActorSystem) extends CommandService {
  lazy val recipient: ActorRef = ClusterSharding(system).startProxy(typeName = "Discussion", None, extractEntityId, extractShardId)
}