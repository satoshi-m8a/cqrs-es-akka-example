package nv.purchase.domain.model.pointwallet

import akka.actor.{ ActorLogging, Props }
import nv.account.domain.model.account.AccountId
import nv.common.ddd.domain._
import nv.purchase.domain.model.order.OrderId
import nv.purchase.domain.model.pointwallet.PointWallet.Commands.{ CancelUsePoint, ChargePoint, UsePoint }
import nv.purchase.domain.model.pointwallet.PointWallet.Events.{ PointCharged, PointUsed, PointWalletEvent, UsePointCanceled }

import scala.reflect._

/**
  * ポイントのチャージ、利用を管理する
  */
object PointWallet {

  def props = Props[PointWallet]

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

class PointWallet extends AggregateRoot[PointWalletState, PointWalletEvent] with ActorLogging {
  override val domainEventClassTag: ClassTag[PointWalletEvent] = classTag[PointWalletEvent]

  override val aggregateStateClassTag: ClassTag[PointWalletState] = classTag[PointWalletState]

  override def initialState: PointWalletState = PointWalletState()

  override def handleCommand: Receive = {
    case cmd: ChargePoint ⇒
    case cmd: UsePoint ⇒
      //TODO
      raise(PointUsed(cmd.id, cmd.orderId, cmd.point, 999))
    case cmd: CancelUsePoint ⇒
      //TODO
      log.info("cancel point")
      raise(UsePointCanceled(cmd.id, cmd.orderId))
  }

}

case class PointWalletState() extends AggregateState[PointWalletState, PointWalletEvent] {
  override def handle: HandleState = {
    case evt: PointCharged ⇒
      this
    case evt: PointUsed ⇒
      this
    case evt: UsePointCanceled ⇒
      this
  }
}