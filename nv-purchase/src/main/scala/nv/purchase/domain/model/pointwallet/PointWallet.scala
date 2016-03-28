package nv.purchase.domain.model.pointwallet

import nv.account.domain.model.account.AccountId
import nv.common.ddd.domain.{ Command, AggregateState, DomainEvent, AggregateRoot }
import nv.purchase.domain.model.order.OrderId
import nv.purchase.domain.model.pointwallet.PointWallet.Commands.{ UsePoint, ChargePoint }
import nv.purchase.domain.model.pointwallet.PointWallet.Events.{ PointUsed, PointCharged, PointWalletEvent }

import scala.reflect._

/**
  * ポイントのチャージ、利用を管理する
  */
object PointWallet {

  object Commands {

    sealed trait PointWalletCommand extends Command[AccountId]

    case class ChargePoint(id: AccountId, point: Long) extends PointWalletCommand

    case class UsePoint(id: AccountId, orderId: OrderId, point: Long) extends PointWalletCommand

    case class CancelUsePoint(id: AccountId, orderId: OrderId) extends PointWalletCommand

  }

  object Events {

    sealed trait PointWalletEvent extends DomainEvent

    case class PointCharged(id: AccountId) extends PointWalletEvent

    case class PointUsed(id: AccountId, orderId: OrderId, point: Long, remainedPoint: Long) extends PointWalletEvent

    case class UsePointCanceled(id: AccountId, orderId: OrderId) extends PointWalletEvent

  }

}

class PointWallet extends AggregateRoot[PointWalletState, PointWalletEvent] {
  override implicit def domainEventClassTag: ClassTag[PointWalletEvent] = classTag[PointWalletEvent]

  override implicit def aggregateStateClassTag: ClassTag[PointWalletState] = classTag[PointWalletState]

  override def initialState: PointWalletState = PointWalletState()

  override def handleCommand: Receive = {
    case cmd: ChargePoint ⇒
    case cmd: UsePoint    ⇒
  }

}

case class PointWalletState() extends AggregateState[PointWalletState, PointWalletEvent] {
  override def handle: HandleState = {
    case evt: PointCharged ⇒
      this
    case evt: PointUsed ⇒
      this
  }
}