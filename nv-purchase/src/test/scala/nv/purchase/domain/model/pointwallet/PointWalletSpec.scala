package nv.purchase.domain.model.pointwallet

import nv.account.domain.model.account.Account
import nv.common.ddd.domain.AggregateRoot.Commands.GetState
import nv.purchase.domain.model.order.Order
import nv.purchase.domain.model.pointwallet.PointWallet.Commands.{ CancelUsePoint, ChargePoint, UsePoint }
import nv.purchase.domain.model.pointwallet.PointWallet.Events.{ UsePointCanceled, PointCharged, PointUsed }
import nv.testkit.support.TestSupport

class PointWalletSpec extends TestSupport {

  val id = Account.nextId
  val wallet = system.actorOf(PointWallet.props)

  val orderId = Order.nextId
  val orderId2 = Order.nextId

  "PointWallet" should {
    "charge points" in {
      wallet ! ChargePoint(id, 100)

      expectMsg(PointCharged(id, 100, 100))

      wallet ! GetState

      expectMsg(PointWalletState(100, Map.empty))
    }
    "pull points" in {

      wallet ! UsePoint(id, orderId, 10)

      expectMsg(PointUsed(id, orderId, 10, 90))

      wallet ! GetState

      expectMsg(PointWalletState(90, Map(orderId → 10)))
    }

    "pull points2" in {

      wallet ! UsePoint(id, orderId2, 30)

      expectMsg(PointUsed(id, orderId2, 30, 60))

      wallet ! GetState

      expectMsg(PointWalletState(60, Map(orderId → 10, orderId2 → 30)))
    }
    "cancel using point" in {
      wallet ! CancelUsePoint(id, orderId)

      expectMsg(UsePointCanceled(id, orderId))

      wallet ! GetState

      expectMsg(PointWalletState(70, Map(orderId2 → 30)))
    }
    "ignore same cancel request" in {
      wallet ! CancelUsePoint(id, orderId)

      expectMsg(UsePointCanceled(id, orderId))

      wallet ! GetState

      expectMsg(PointWalletState(70, Map(orderId2 → 30)))
    }
  }
}
