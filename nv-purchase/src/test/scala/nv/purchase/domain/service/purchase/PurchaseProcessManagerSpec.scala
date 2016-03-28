package nv.purchase.domain.service.purchase

import nv.account.domain.model.account.{ Account, AccountId }
import nv.common.ddd.domain.RemoteEventMediator
import nv.market.application.ProductService
import nv.market.domain.model.product.ProductId
import nv.purchase.domain.model.order.{ Item, Order }
import nv.purchase.domain.model.pointwallet.PointWallet
import nv.purchase.domain.service.purchase.PurchaseProcessManager.Commands.Purchase
import nv.purchase.domain.service.purchase.PurchaseProcessManager.Events.PurchaseProcessCompleted
import nv.testkit.support.TestSupport

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class PurchaseProcessManagerSpec extends TestSupport {

  "Purchase Process" should {
    "complete order process" in {

      val remoteEventMediator = new RemoteEventMediator(system)

      val pointWallet = system.actorOf(PointWallet.props(remoteEventMediator))
      val order = system.actorOf(Order.props(remoteEventMediator))
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

      val orderId = Order.nextId
      val accountId = Account.nextId

      val pp = system.actorOf(PurchaseProcessManager.remoteProps(pointWallet, order, productService), name = orderId.value)
      watch(pp)

      pp ! Purchase(orderId, Set(Item(ProductId("1"), 100, 10), Item(ProductId("2"), 300, 100)), accountId)

      expectMsg(PurchaseProcessCompleted(orderId, accountId))
    }
  }
}
