package nv.common.ddd.domain

import akka.actor.{ ActorRef, ActorSystem }
import akka.cluster.client.ClusterClient.Publish
import akka.cluster.pubsub.DistributedPubSubMediator.Unsubscribe
import akka.cluster.pubsub.{ DistributedPubSub, DistributedPubSubMediator }

trait EventMediator[E <: Seq[_]] {
  def startSubscribe(topics: E, subscriber: ActorRef): Unit

  def stopSubscribe(topics: E, subscriber: ActorRef): Unit

  def publish(event: AnyRef, topic: String): Unit
}

class RemoteEventMediator(system: ActorSystem) extends EventMediator[Seq[String]] {

  import DistributedPubSubMediator.Subscribe

  val mediator = DistributedPubSub(system).mediator

  override def startSubscribe(topics: Seq[String], subscriber: ActorRef): Unit = {
    topics.foreach { t ⇒
      mediator ! Subscribe(t, subscriber)
    }
  }

  override def stopSubscribe(topics: Seq[String], subscriber: ActorRef): Unit = {
    topics.foreach { t ⇒
      mediator ! Unsubscribe(t, subscriber)
    }
  }

  override def publish(event: AnyRef, topic: String): Unit = {
    mediator ! Publish(topic, event)
  }
}

class LocalEventMediator(system: ActorSystem) extends EventMediator[Seq[Class[_]]] {
  override def startSubscribe(topics: Seq[Class[_]], subscriber: ActorRef): Unit = {
    topics.foreach { t ⇒
      system.eventStream.subscribe(subscriber, t)
    }
  }

  override def stopSubscribe(topics: Seq[Class[_]], subscriber: ActorRef): Unit = {
    topics.foreach { t ⇒
      system.eventStream.unsubscribe(subscriber, t)
    }
  }

  override def publish(event: AnyRef, topic: String): Unit = {
    system.eventStream.publish(event)
  }
}