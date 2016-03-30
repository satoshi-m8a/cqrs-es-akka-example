package modules

import akka.actor.ActorSystem
import akka.cluster.sharding.{ ClusterSharding, ClusterShardingSettings, ShardRegion }
import nv.common.ddd.domain.Command
import nv.discussion.domain.model.discussion.Discussion

object Regions {

  val extractEntityId: ShardRegion.ExtractEntityId = {
    case c: Command[_] ⇒ (c.id.value.toString, c)
  }

  val numberOfShards = 20

  val extractShardId: ShardRegion.ExtractShardId = {
    case c: Command[_] ⇒ (Math.abs(c.id.value.toString.hashCode) % numberOfShards).toString
  }

  def start(implicit system: ActorSystem) = {

    val roles = system.settings.config.getStringList("akka.cluster.roles")

    /**
      *  指定のロールのときだけ、ShardRegionを立ち上げる、それ以外の場合はプロキシを立ち上げる。
      */
    if (roles.contains("discussion-command-backend")) {
      ClusterSharding(system).start(
        typeName = "Discussion",
        entityProps = Discussion.props,
        settings = ClusterShardingSettings(system),
        extractEntityId = extractEntityId,
        extractShardId = extractShardId
      )
    } else {
      ClusterSharding(system).startProxy(typeName = "Discussion", None, extractEntityId, extractShardId)
    }
  }
}
