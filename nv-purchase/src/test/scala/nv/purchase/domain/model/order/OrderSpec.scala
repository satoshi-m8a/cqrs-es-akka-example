package nv.purchase.domain.model.order

import akka.actor.Props
import nv.common.ddd.domain.AggregateRoot.Commands.GetState
import nv.market.domain.model.product.ProductId
import nv.purchase.domain.model.order.Order.Commands.{ CancelOrder, PlaceOrder }
import nv.purchase.domain.model.order.Order.Events.{ OrderCanceled, OrderPlaced }
import nv.testkit.support.TestSupport

class OrderSpec extends TestSupport {

  "Order" should {
    val id = Order.nextId
    val order = system.actorOf(Props[Order])

    "place" in {
      order ! PlaceOrder(id, Set(Item(ProductId("1"), 100, 10), Item(ProductId("2"), 300, 20)))

      expectMsg(OrderPlaced(id, Set(Item(ProductId("1"), 100, 10), Item(ProductId("2"), 300, 20))))

      order ! GetState

      expectMsg(OrderState(Set(Item(ProductId("1"), 100, 10), Item(ProductId("2"), 300, 20)), placed = true))
    }
    "cancel" in {
      order ! CancelOrder(id)

      expectMsg(OrderCanceled(id))

      order ! GetState

      expectMsg(OrderState(Set(Item(ProductId("1"), 100, 10), Item(ProductId("2"), 300, 20)), placed = false))
    }
    "ignore same cancel message" in {
      order ! CancelOrder(id)

      expectMsg(OrderCanceled(id))

      order ! GetState

      expectMsg(OrderState(Set(Item(ProductId("1"), 100, 10), Item(ProductId("2"), 300, 20)), placed = false))
    }
  }
}
