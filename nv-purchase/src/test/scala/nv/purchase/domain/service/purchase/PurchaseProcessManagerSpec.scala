package nv.purchase.domain.service.purchase

import akka.actor.{ Actor, Props }
import nv.account.domain.model.account.{ Account, AccountId }
import nv.market.application.ProductService
import nv.market.domain.model.product.ProductId
import nv.purchase.domain.model.order.{ Item, Order }
import nv.purchase.domain.model.pointwallet.PointWallet
import nv.purchase.domain.service.purchase.PurchaseProcessManager.Commands.Purchase
import nv.purchase.domain.service.purchase.PurchaseProcessManager.Events.{ PurchaseProcessCanceled, PurchaseProcessCompleted }
import nv.testkit.support.TestSupport

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

class PurchaseProcessManagerSpec extends TestSupport {

  val productService = new ProductService {
    override def cancel(items: Seq[ProductId], accountId: AccountId): Future[Seq[ProductId]] = {
      Future {
        items
      }
    }

    override def buy(items: Seq[ProductId], accountId: AccountId): Future[Seq[ProductId]] = {
      Future {
        items
      }
    }
  }

  val walletMock = system.actorOf(Props(new Actor {
    override def receive: Receive = {
      case cmd: PointWallet.Commands.CancelUsePoint ⇒
        sender() ! PointWallet.Events.UsePointCanceled(cmd.id, cmd.orderId)
    }
  }), name = "walletmock")

  val orderMock = system.actorOf(Props(new Actor {
    override def receive: Receive = {
      case cmd: Order.Commands.CancelOrder ⇒
        sender() ! Order.Events.OrderCanceled(cmd.id)
    }
  }), name = "ordermock")

  "Purchase Process" should {
    "complete order process" in {
      val orderId = Order.nextId
      val pointWallet = system.actorOf(PointWallet.props, name = "point1")
      val order = system.actorOf(Order.props, name = "order1")

      val accountId = Account.nextId
      val pp = system.actorOf(PurchaseProcessManager.props(pointWallet, order, productService), name = orderId.value)

      pp ! Purchase(orderId, Set(Item(ProductId("1"), 100, 10), Item(ProductId("2"), 300, 100)), accountId)

      expectMsg(PurchaseProcessCompleted(orderId, accountId))
    }

    "recover when fail point process" in {
      val orderId = Order.nextId
      val order = system.actorOf(Order.props, name = "order2")

      val accountId = Account.nextId
      val pp = system.actorOf(PurchaseProcessManager.props(walletMock, order, productService), name = orderId.value)

      pp ! Purchase(orderId, Set(Item(ProductId("1"), 100, 10), Item(ProductId("2"), 300, 100)), accountId)

      expectMsg(10.seconds, PurchaseProcessCanceled(orderId, accountId))
    }

    "recover when fail order process" in {
      val orderId = Order.nextId
      val pointWallet = system.actorOf(PointWallet.props, name = "point3")

      val accountId = Account.nextId
      val pp = system.actorOf(PurchaseProcessManager.props(pointWallet, orderMock, productService), name = orderId.value)

      pp ! Purchase(orderId, Set(Item(ProductId("1"), 100, 10), Item(ProductId("2"), 300, 100)), accountId)

      expectMsg(10.seconds, PurchaseProcessCanceled(orderId, accountId))
    }
  }
}
