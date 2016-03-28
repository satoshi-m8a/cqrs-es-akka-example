package nv.purchase.domain.service.purchase.process

import nv.purchase.domain.model.order.Order
import nv.purchase.domain.service.purchase.PurchaseProcessManager
import nv.purchase.domain.service.purchase.PurchaseProcessManager.Data.PurchaseProcessData
import nv.purchase.domain.service.purchase.PurchaseProcessManager.Events.{ CancelOrderProcessed, OrderProcessed, PointUseProcessed }
import nv.purchase.domain.service.purchase.PurchaseProcessManager.States.{ OrderProcessing, PointProcessing, RegistrationProcessing }

import scala.concurrent.duration._

trait OrderProcess {
  this: PurchaseProcessManager[_] ⇒

  when(OrderProcessing, 1.seconds) {
    /**
      * ポイント処理(PointProcessing)から遷移してくる。
      */
    case Event(PointUseProcessed(), _) ⇒
      log.info("start order process")

      stay forMax 5.seconds andThen {
        case PurchaseProcessData(Some(r), _, true, false, false) ⇒
          order ! Order.Commands.PlaceOrder(r.orderId, r.items)
      }

    /**
      * オーダーが完了したら、登録処理(RegistrationProcessing)へ遷移する。
      */
    case Event(Order.Events.OrderPlaced(_, _), _) ⇒
      log.info("handle order placed")
      goto(RegistrationProcessing) applying OrderProcessed() andThen {
        case e ⇒
          self ! OrderProcessed()
      }

    /**
      * オーダーのキャンセルが完了したら、ポイント処理(PointProcessing)へ戻る。
      */
    case Event(Order.Events.OrderCanceled(_), _) ⇒
      goto(PointProcessing) applying CancelOrderProcessed() forMax 1.second andThen {
        case e ⇒
          self ! CancelOrderProcessed()
      }

    /**
      * タイムアウトが発生したら、オーダーをキャンセルする
      */
    case Event(StateTimeout, _) ⇒
      stay forMax 5.seconds andThen {
        case PurchaseProcessData(Some(r), _, _, _, _) ⇒
          order ! Order.Commands.CancelOrder(r.orderId)
      }
  }

}
