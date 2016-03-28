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

    case class PointCharged(id: AccountId, chargedPoint: Long, remainedPoint: Long) extends PointWalletEvent

    case class PointUsed(id: AccountId, orderId: OrderId, usedPoint: Long, remainedPoint: Long) extends PointWalletEvent

    case class UsePointCanceled(id: AccountId, orderId: OrderId) extends PointWalletEvent

  }

}

class PointWallet extends AggregateRoot[PointWalletState, PointWalletEvent] with ActorLogging {
  override val domainEventClassTag: ClassTag[PointWalletEvent] = classTag[PointWalletEvent]

  override val aggregateStateClassTag: ClassTag[PointWalletState] = classTag[PointWalletState]

  override def initialState: PointWalletState = PointWalletState(0L, Map.empty)

  override def handleCommand: Receive = {
    case cmd: ChargePoint ⇒
      raise(PointCharged(cmd.id, cmd.point, state.currentPoint + cmd.point))
    case cmd: UsePoint ⇒
      raise(PointUsed(cmd.id, cmd.orderId, cmd.point, state.currentPoint - cmd.point))
    case cmd: CancelUsePoint ⇒
      if (state.orderPoint.keys.exists(_ == cmd.orderId)) {
        raise(UsePointCanceled(cmd.id, cmd.orderId))
      } else {
        //オーダーが見つからないかキャンセル済みであれば、キャンセル済みと返事する。
        sender() ! UsePointCanceled(cmd.id, cmd.orderId)
      }
  }

}

case class PointWalletState(currentPoint: Long, orderPoint: Map[OrderId, Long]) extends AggregateState[PointWalletState, PointWalletEvent] {
  override def handle: HandleState = {
    case evt: PointCharged ⇒
      copy(currentPoint = evt.remainedPoint)
    case evt: PointUsed ⇒
      copy(currentPoint = evt.remainedPoint, orderPoint = this.orderPoint + (evt.orderId → evt.usedPoint))
    case evt: UsePointCanceled ⇒
      orderPoint.get(evt.orderId) match {
        case Some(point) ⇒
          copy(currentPoint = this.currentPoint + point, orderPoint = this.orderPoint - evt.orderId)
        case _ ⇒
          this
      }
  }
}