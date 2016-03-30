package nv.common.ddd.application

import akka.actor.{ ActorSystem, ActorRef }
import akka.cluster.sharding.ClusterSharding
import akka.util.Timeout
import nv.common.ddd.domain.{ Command, EntityId }

import scala.concurrent.Future
import scala.concurrent.duration._
import akka.pattern.ask

trait CommandService {
  val defaultTimeout = 60 seconds
  val recipient: ActorRef

  def send[T <: EntityId[_]](command: Command[T], t: Timeout = Timeout(defaultTimeout)): Future[Any] = recipient.ask(command)(t)
}

class RegionCommandService(regionName: String)(implicit system: ActorSystem) extends CommandService {
  lazy val recipient: ActorRef = ClusterSharding(system).shardRegion(regionName)
}