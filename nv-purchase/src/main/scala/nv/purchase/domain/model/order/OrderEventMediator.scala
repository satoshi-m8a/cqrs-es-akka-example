package nv.purchase.domain.model.order

import akka.actor.Actor
import akka.cluster.pubsub.{ DistributedPubSub, DistributedPubSubMediator }
import nv.purchase.domain.model.order.Order.Events.OrderPlaced

class OrderEventMediator extends Actor {

  import DistributedPubSubMediator.Publish

  val mediator = DistributedPubSub(context.system).mediator

  override def receive: Receive = {
    case evt: OrderPlaced ⇒
      mediator ! Publish(s"OrderPlaced-${evt.id.value}", evt)
  }
}
