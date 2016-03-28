package modules

import akka.actor.ActorSystem
import akka.cluster.sharding.{ ClusterSharding, ClusterShardingSettings, ShardRegion }
import nv.common.ddd.domain.Command
import nv.discussion.domain.model.discussion.Discussion

object Regions {

  val extractEntityId: ShardRegion.ExtractEntityId = {
    case c: Command[_] ⇒ (c.id.value.toString, c)
  }

  val numberOfShards = 100

  val extractShardId: ShardRegion.ExtractShardId = {
    case c: Command[_] ⇒ (Math.abs(c.id.value.toString.hashCode) % numberOfShards).toString
  }

  val regions = Map(
    "Discussion" → Discussion.props
  )

  def start(implicit system: ActorSystem) = {
    regions.foreach {
      case (name, props) ⇒
        ClusterSharding(system).start(
          typeName = name,
          entityProps = props,
          settings = ClusterShardingSettings(system),
          extractEntityId = extractEntityId,
          extractShardId = extractShardId
        )
    }
  }
}
