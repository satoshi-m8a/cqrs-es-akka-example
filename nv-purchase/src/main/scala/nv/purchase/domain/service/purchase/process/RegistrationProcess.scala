package nv.purchase.domain.service.purchase.process

import nv.purchase.domain.service.purchase.PurchaseProcessManager
import nv.purchase.domain.service.purchase.PurchaseProcessManager.Data.PurchaseProcessData
import nv.purchase.domain.service.purchase.PurchaseProcessManager.Events.{ CancelRegistrationProcessed, RegistrationProcessed, OrderProcessed }
import nv.purchase.domain.service.purchase.PurchaseProcessManager.States.{ OrderProcessing, PurchaseCompleted, RegistrationProcessing }

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

trait RegistrationProcess {
  this: PurchaseProcessManager[_] ⇒

  case object BuyCompleted

  case object CancelCompleted

  when(RegistrationProcessing, 1.seconds) {
    /**
      * オーダー処理(OrderProcessing)から遷移してくる。
      */
    case Event(OrderProcessed(), _) ⇒
      log.info("start registration process")
      stay forMax 3.seconds andThen {
        case PurchaseProcessData(Some(r), _, true, true, false) ⇒
          productService.buy(r.items.map(_.id).toSeq, r.accountId).map {
            ids ⇒
              self ! BuyCompleted
          }
      }

    /**
      * 登録処理が完了したら、購入完了状態(PurchaseCompleted)へ遷移する。
      */
    case Event(BuyCompleted, _) ⇒
      goto(PurchaseCompleted) applying RegistrationProcessed() forMax 3.seconds andThen {
        case e ⇒
          self ! RegistrationProcessed()
      }

    /**
      * 登録処理のキャンセルが完了したら、オーダー処理(OrderProcessing)へ戻る。
      */
    case Event(CancelCompleted, _) ⇒
      goto(OrderProcessing) applying CancelRegistrationProcessed()

    /**
      * タイムアウトが発生したら、 登録処理をキャンセルする。
      */
    case Event(StateTimeout, _) ⇒
      stay forMax 10.seconds andThen {
        case PurchaseProcessData(Some(r), _, _, _, _) ⇒
          productService.buy(r.items.map(_.id).toSeq, r.accountId).map {
            ids ⇒
              self ! CancelCompleted
          }
      }

  }
}