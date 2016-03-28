package nv.purchase.domain.service.purchase.process

import nv.purchase.domain.model.order.Item
import nv.purchase.domain.model.pointwallet.PointWallet
import nv.purchase.domain.service.purchase.PurchaseProcessManager
import nv.purchase.domain.service.purchase.PurchaseProcessManager.Data.PurchaseProcessData
import nv.purchase.domain.service.purchase.PurchaseProcessManager.Events.{ CancelPointUseProcessed, PointUseProcessed, PurchaseStarted }
import nv.purchase.domain.service.purchase.PurchaseProcessManager.States.{ BeforeStart, OrderProcessing, PointProcessing }

import scala.concurrent.duration._

trait PointProcess {
  this: PurchaseProcessManager[_] ⇒

  when(PointProcessing, 5.seconds) {
    /**
      * 初期状態(BeforeStart)から遷移してくる。
      */
    case Event(PurchaseStarted(_, _), PurchaseProcessData(Some(request), _, _, _, _)) ⇒
      log.info("start point process")

      val totalPoint = Item.getTotalPoint(request.items)

      stay forMax 10.seconds andThen {
        case PurchaseProcessData(Some(r), _, false, false, false) ⇒
          pointWallet ! PointWallet.Commands.UsePoint(r.accountId, r.orderId, totalPoint)
      }

    /**
      * ポイントの利用が完了したら、オーダー処理(OrderProcessing)に遷移する。
      */
    case Event(PointWallet.Events.PointUsed(accountI, orderId, point, _), _) ⇒
      log.info("handle point used")
      goto(OrderProcessing) applying PointUseProcessed() forMax 10.seconds andThen {
        case e ⇒
          self ! PointUseProcessed()
      }

    /**
      * キャンセルが完了したら、初期状態(BeforeStart)に戻る。
      */
    case Event(PointWallet.Events.UsePointCanceled(_, _), _) ⇒
      goto(BeforeStart) applying CancelPointUseProcessed() andThen {
        case e ⇒
          self ! CancelPointUseProcessed()
      }

    /**
      * タイムアウトが発生したら、ポイントの利用をキャンセルする
      */
    case Event(StateTimeout, _) ⇒
      stay forMax 10.seconds andThen {
        case PurchaseProcessData(Some(r), _, _, _, _) ⇒
          pointWallet ! PointWallet.Commands.CancelUsePoint(r.accountId, r.orderId)
      }
  }
}
