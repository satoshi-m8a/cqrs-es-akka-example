package nv.purchase.domain.model.order

import java.util.UUID

import akka.actor.{ ActorLogging, Props }
import nv.common.ddd.domain._
import nv.purchase.domain.model.order.Order.Commands.{ CancelOrder, PlaceOrder }
import nv.purchase.domain.model.order.Order.Events.{ OrderCanceled, OrderEvent, OrderPlaced }

import scala.reflect._

object Order {

  def props = Props[Order]

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

class Order extends AggregateRoot[OrderState, OrderEvent] with ActorLogging {
  override val domainEventClassTag: ClassTag[OrderEvent] = classTag[OrderEvent]

  override val aggregateStateClassTag: ClassTag[OrderState] = classTag[OrderState]

  override def initialState: OrderState = OrderState(Set.empty, placed = false)

  override def handleCommand: Receive = {
    case cmd: PlaceOrder ⇒
      log.info("handle place order command")
      raise(OrderPlaced(cmd.id, cmd.items))
    case cmd: CancelOrder ⇒
      if (state.placed) {
        raise(OrderCanceled(cmd.id))
      } else {
        sender() ! OrderCanceled(cmd.id)
      }
  }

}

case class OrderState(items: Set[Item], placed: Boolean) extends AggregateState[OrderState, OrderEvent] {
  override def handle: HandleState = {
    case evt: OrderPlaced ⇒
      copy(items = evt.items, placed = true)
    case evt: OrderCanceled ⇒
      copy(placed = false)
  }
}