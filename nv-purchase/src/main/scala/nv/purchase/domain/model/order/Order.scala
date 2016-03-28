package nv.purchase.domain.model.order

import java.util.UUID

import akka.actor.{ Props, ActorRef }
import nv.common.ddd.domain.{ AggregateRoot, AggregateState, Command, DomainEvent }
import nv.purchase.domain.model.order.Order.Commands.PlaceOrder
import nv.purchase.domain.model.order.Order.Events.{ OrderEvent, OrderPlaced }

import scala.reflect._

object Order {

  def props(eventMediator: ActorRef) = Props(new Order(eventMediator))

  def nextId: OrderId = OrderId(UUID.randomUUID().toString)

  object Commands {

    sealed trait OrderCommand extends Command[OrderId]

    case class PlaceOrder(id: OrderId, items: Set[Item]) extends OrderCommand

    case class CancelOrder(id: OrderId) extends OrderCommand

  }

  object Events {

    sealed trait OrderEvent extends DomainEvent

    case class OrderPlaced(id: OrderId, items: Set[Item]) extends OrderEvent

    case class OrderCanceled(id: OrderId) extends OrderEvent

  }

}

class Order(eventMediator: ActorRef) extends AggregateRoot[OrderState, OrderEvent] {
  override implicit def domainEventClassTag: ClassTag[OrderEvent] = classTag[OrderEvent]

  override implicit def aggregateStateClassTag: ClassTag[OrderState] = classTag[OrderState]

  override def initialState: OrderState = OrderState(Set.empty)

  override def handleCommand: Receive = {
    case cmd: PlaceOrder ⇒ {
      raise(OrderPlaced(cmd.id, cmd.items))
    }
  }

  override def afterEvent: ReceiveEvent = {
    case evt: OrderPlaced ⇒
      eventMediator ! evt
  }
}

case class OrderState(items: Set[Item]) extends AggregateState[OrderState, OrderEvent] {
  override def handle: HandleState = {
    case evt: OrderPlaced ⇒
      copy(items = evt.items)
  }
}