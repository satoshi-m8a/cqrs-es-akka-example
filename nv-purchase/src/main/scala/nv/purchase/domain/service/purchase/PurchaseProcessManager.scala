package nv.purchase.domain.service.purchase

import akka.actor.{ ActorLogging, ActorRef, ActorSystem, Props }
import akka.persistence.fsm.PersistentFSM
import akka.persistence.fsm.PersistentFSM.FSMState
import nv.account.domain.model.account.AccountId
import nv.common.ddd.domain._
import nv.market.application.ProductService
import nv.purchase.domain.model.order.{ Item, OrderId }
import nv.purchase.domain.service.purchase.PurchaseProcessManager.Commands.Purchase
import nv.purchase.domain.service.purchase.PurchaseProcessManager.Data.{ PurchaseProcessData, PurchaseRequest }
import nv.purchase.domain.service.purchase.PurchaseProcessManager.Events._
import nv.purchase.domain.service.purchase.PurchaseProcessManager.States.{ BeforeStart, PointProcessing, PurchaseCompleted, PurchaseProcessState }
import nv.purchase.domain.service.purchase.process.{ OrderProcess, PointProcess, RegistrationProcess }

import scala.concurrent.duration._
import scala.reflect._

/**
  * ポイントの利用、注文の作成、商品（記事、サイト）購入者の登録を行う。
  */
object PurchaseProcessManager {

  def props(pointWallet: ActorRef, order: ActorRef, productService: ProductService)(implicit system: ActorSystem) = {
    Props(new PurchaseProcessManager(pointWallet, order, productService))
  }

  object States {

    sealed trait PurchaseProcessState extends FSMState

    case object BeforeStart extends PurchaseProcessState {
      override def identifier: String = "before-start"
    }

    case object PointProcessing extends PurchaseProcessState {
      override def identifier: String = "point-processing"
    }

    case object OrderProcessing extends PurchaseProcessState {
      override def identifier: String = "order-processing"
    }

    case object RegistrationProcessing extends PurchaseProcessState {
      override def identifier: String = "registration-processing"
    }

    case object PurchaseCompleted extends PurchaseProcessState {
      override def identifier: String = "purchase-completed"
    }

  }

  object Commands {

    sealed trait PurchaseProcessCommand extends Command[OrderId]

    case class Purchase(id: OrderId, items: Set[Item], accountId: AccountId)

  }

  object Events {

    sealed trait PurchaseProcessEvent extends DomainEvent

    case class PurchaseStarted(request: PurchaseRequest, sender: ActorRef) extends PurchaseProcessEvent

    case class PointUseProcessed() extends PurchaseProcessEvent

    case class CancelPointUseProcessed() extends PurchaseProcessEvent

    case class OrderProcessed() extends PurchaseProcessEvent

    case class CancelOrderProcessed() extends PurchaseProcessEvent

    case class RegistrationProcessed() extends PurchaseProcessEvent

    case class CancelRegistrationProcessed() extends PurchaseProcessEvent

    case class PurchaseProcessCompleted(orderId: OrderId, accountId: AccountId) extends PurchaseProcessEvent

    case class PurchaseProcessCanceled(orderId: OrderId, accountId: AccountId) extends PurchaseProcessEvent

  }

  object Data {

    case class PurchaseRequest(orderId: OrderId, accountId: AccountId, items: Set[Item])

    case class PurchaseProcessData(request: Option[PurchaseRequest], replyTo: Option[ActorRef], pointProcessed: Boolean, orderProcessed: Boolean, registrationProcessed: Boolean)

  }

}

class PurchaseProcessManager(val pointWallet: ActorRef, val order: ActorRef, val productService: ProductService)
    extends PersistentFSM[PurchaseProcessState, PurchaseProcessData, PurchaseProcessEvent] with PointProcess with OrderProcess with RegistrationProcess with ActorLogging {
  override def domainEventClassTag: ClassTag[PurchaseProcessEvent] = classTag[PurchaseProcessEvent]

  override def persistenceId: String = self.path.parent.name + "-" + self.path.name

  override def applyEvent(domainEvent: PurchaseProcessEvent, currentData: PurchaseProcessData): PurchaseProcessData = domainEvent match {
    case evt: PurchaseStarted ⇒
      currentData.copy(request = Some(evt.request), replyTo = Some(evt.sender))
    case evt: PointUseProcessed ⇒
      currentData.copy(pointProcessed = true)
    case evt: CancelPointUseProcessed ⇒
      currentData.copy(pointProcessed = false)
    case evt: OrderProcessed ⇒
      currentData.copy(orderProcessed = true)
    case evt: CancelOrderProcessed ⇒
      currentData.copy(orderProcessed = false)
    case evt: RegistrationProcessed ⇒
      currentData.copy(registrationProcessed = true)
    case evt: CancelRegistrationProcessed ⇒
      currentData.copy(registrationProcessed = false)
    case evt: PurchaseProcessCompleted ⇒
      currentData
    case evt: PurchaseProcessCanceled ⇒
      currentData
  }

  startWith(BeforeStart, PurchaseProcessData(None, None, false, false, false))

  when(BeforeStart, 1.seconds) {
    case Event(Purchase(orderId, items, accountId), _) ⇒
      log.info("start order {}", orderId)
      val replyTo = sender()
      val request = PurchaseRequest(orderId, accountId, items)
      goto(PointProcessing) applying PurchaseStarted(request, replyTo) forMax 5.seconds andThen {
        case e ⇒
          self ! PurchaseStarted(request, replyTo)
      }
    case Event(CancelPointUseProcessed(), PurchaseProcessData(Some(r), sender, _, _, _)) ⇒
      sender.foreach(s ⇒ s ! PurchaseProcessCanceled(r.orderId, r.accountId))
      stop()
    case Event(StateTimeout, PurchaseProcessData(Some(r), sender, _, _, _)) ⇒
      sender.foreach(s ⇒ s ! PurchaseProcessCanceled(r.orderId, r.accountId))
      stop()
  }

  when(PurchaseCompleted, 5.seconds) {
    case Event(RegistrationProcessed(), PurchaseProcessData(Some(r), sender, _, _, _)) ⇒
      log.info("complete order {}", r.orderId)
      sender.foreach { s ⇒
        s ! PurchaseProcessCompleted(r.orderId, r.accountId)
      }
      stop()
    case Event(StateTimeout, _) ⇒
      stop()
  }

}

