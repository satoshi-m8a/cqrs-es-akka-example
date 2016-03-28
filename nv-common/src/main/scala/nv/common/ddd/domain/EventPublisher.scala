package nv.common.ddd.domain

import akka.actor.{ ActorRef, ActorSystem }
import akka.cluster.pubsub.DistributedPubSubMediator.Publish

trait EventPublisher {
  def publish(event: AnyRef): Unit
}

class RemoteEventPublisher(mediator: ActorRef, topic: String) extends EventPublisher {

  override def publish(event: AnyRef): Unit = {
    mediator ! Publish(topic, event)
  }
}

class LocalEventPublisher(system: ActorSystem) extends EventPublisher {
  override def publish(event: AnyRef): Unit = {
    system.eventStream.publish(event)
  }
}