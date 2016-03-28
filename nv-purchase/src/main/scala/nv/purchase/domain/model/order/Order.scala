package nv.purchase.domain.model.order

import java.util.UUID

import akka.actor.{ ActorLogging, Props }
import nv.common.ddd.domain._
import nv.purchase.domain.model.order.Order.Commands.PlaceOrder
import nv.purchase.domain.model.order.Order.Events.{ OrderCanceled, OrderEvent, OrderPlaced }

import scala.reflect._

object Order {

  def props[T <: Seq[_]](eventMediator: EventMediator[T]) = Props(new Order[T](eventMediator))

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

class Order[T <: Seq[_]](eventMediator: EventMediator[T]) extends AggregateRoot[OrderState, OrderEvent] with ActorLogging {
  override val domainEventClassTag: ClassTag[OrderEvent] = classTag[OrderEvent]

  override val aggregateStateClassTag: ClassTag[OrderState] = classTag[OrderState]

  override def initialState: OrderState = OrderState(Set.empty)

  override def handleCommand: Receive = {
    case cmd: PlaceOrder ⇒ {
      log.info("handle place order command")
      raise(OrderPlaced(cmd.id, cmd.items))
    }
  }

  override def afterEvent: ReceiveEvent = {
    case evt: OrderPlaced ⇒
      eventMediator.publish(evt, s"Order-${evt.id.value}")
    case evt: OrderCanceled ⇒
      eventMediator.publish(evt, s"Order-${evt.id.value}")
  }
}

case class OrderState(items: Set[Item]) extends AggregateState[OrderState, OrderEvent] {
  override def handle: HandleState = {
    case evt: OrderPlaced ⇒
      copy(items = evt.items)
    case evt: OrderCanceled ⇒
      this
  }
}