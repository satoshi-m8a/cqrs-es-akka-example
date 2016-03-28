package nv.common.ddd.domain

import akka.actor.{ ActorRef, ActorSystem }
import akka.cluster.pubsub.DistributedPubSubMediator
import akka.cluster.pubsub.DistributedPubSubMediator.Unsubscribe

trait EventMediator[E <: Seq[_]] {
  def startSubscribe(topics: E, subscriber: ActorRef): Unit

  def stopSubscribe(topics: E, subscriber: ActorRef): Unit
}

class RemoteEventMediator(mediator: ActorRef) extends EventMediator[Seq[String]] {

  import DistributedPubSubMediator.Subscribe

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
}