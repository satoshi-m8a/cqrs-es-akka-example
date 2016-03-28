package nv.common.ddd.domain

import akka.actor.Actor
import akka.actor.Actor.Receive
import akka.cluster.pubsub.DistributedPubSubMediator.Unsubscribe
import akka.cluster.pubsub.{ DistributedPubSub, DistributedPubSubMediator }

trait SubScribeExternalEvent {
  this: Actor ⇒

  def startSubscribe(): Unit

  def stopSubscribe(): Unit

  override def preStart(): Unit = {
    startSubscribe()
  }

  override def postStop(): Unit = {
    stopSubscribe()
  }
}

trait SubScribeRemoteEvent extends SubScribeExternalEvent {
  this: Actor ⇒

  import DistributedPubSubMediator.Subscribe

  val mediator = DistributedPubSub(context.system).mediator

  val subscribingTopics: Seq[String]

  override def startSubscribe(): Unit = {
    subscribingTopics.foreach {
      topic ⇒
        mediator ! Subscribe(topic, self)
    }
  }

  override def stopSubscribe(): Unit = {
    subscribingTopics.foreach {
      topic ⇒
        mediator ! Unsubscribe(topic, self)
    }
  }

}

trait SubScribeLocalEvent extends SubScribeExternalEvent {
  this: Actor ⇒

  val subscribingEvents: Seq[Class[DomainEvent]]

  override def startSubscribe(): Unit = {
    subscribingEvents.foreach {
      event ⇒
        context.system.eventStream.subscribe(self, event)
    }
  }

  override def stopSubscribe(): Unit = {
    subscribingEvents.foreach {
      event ⇒
        context.system.eventStream.unsubscribe(self, event)
    }
  }

}

