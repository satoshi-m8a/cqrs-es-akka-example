package nv.purchase.infrastructure.notification

import akka.actor._
import akka.cluster.routing.{ ClusterRouterPool, ClusterRouterPoolSettings }
import akka.cluster.singleton.{ ClusterSingletonManager, ClusterSingletonManagerSettings, ClusterSingletonProxy, ClusterSingletonProxySettings }
import akka.pattern.ask
import akka.persistence.{ AtLeastOnceDelivery, PersistentActor }
import akka.routing.RoundRobinPool
import akka.util.Timeout
import nv.account.domain.model.account.AccountId
import nv.common.ddd.infrastructure.projection.ResumableProjectionUpdater
import nv.purchase.domain.model.order.OrderId
import nv.purchase.domain.service.purchase.PurchaseProcessManager.Events.PurchaseProcessCompleted
import nv.purchase.infrastructure.email.EmailService
import nv.purchase.infrastructure.notification.EmailNotificationMaster.Commands.{ Confirm, Msg, SendOrderConfirmedEmail, Start }
import nv.purchase.infrastructure.notification.EmailNotificationMaster.Events.{ Evt, MsgConfirmed, MsgSent }

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object EmailNotificationMaster {

  def props(router: ActorRef, projectionUpdater: ResumableProjectionUpdater)(implicit system: ActorSystem) = ClusterSingletonManager.props(
    singletonProps = Props(new EmailNotificationMaster(router, projectionUpdater)),
    terminationMessage = PoisonPill,
    settings = ClusterSingletonManagerSettings(system)
  )

  def routerProps(emailService: EmailService, singletonProxy: ActorRef): Props = {
    ClusterRouterPool(RoundRobinPool(0), ClusterRouterPoolSettings(
      totalInstances = 100, maxInstancesPerNode = 10,
      allowLocalRoutees = true, useRole = None
    )).props(EmailNotificationWorker.props(emailService, singletonProxy))
  }

  def proxyProps(singletonName: String)(implicit system: ActorSystem) = ClusterSingletonProxy.props(
    singletonManagerPath = s"/user/$singletonName",
    settings = ClusterSingletonProxySettings(system)
  )

  object Commands {

    case class SendOrderConfirmedEmail(orderId: OrderId, accountId: AccountId)

    case class Msg(deliveryId: Long, msg: SendOrderConfirmedEmail)

    case class Confirm(deliveryId: Long)

    private[notification] case object Start

  }

  object Events {

    sealed trait Evt

    case class MsgSent(msg: SendOrderConfirmedEmail) extends Evt

    case class MsgConfirmed(deliveryId: Long) extends Evt

  }

}

/**
  * memo:手動で送る場合は、 proxyを介して呼び出す。
  */
class EmailNotificationMaster(router: ActorRef, projectionUpdater: ResumableProjectionUpdater) extends PersistentActor with AtLeastOnceDelivery {

  implicit val timeout = Timeout(5.seconds)

  override def persistenceId: String = self.path.name

  override def receiveCommand: Receive = {
    case s: SendOrderConfirmedEmail ⇒
      persist(MsgSent(s)) {
        evt ⇒
          updateState(evt)
          sender() ! evt
      }
    case Confirm(deliveryId) ⇒ persist(MsgConfirmed(deliveryId))(updateState)
    case Start ⇒
      implicit val system = context.system
      projectionUpdater.startProjection {
        case evt: PurchaseProcessCompleted ⇒
          self ? SendOrderConfirmedEmail(evt.orderId, evt.accountId)
      }
  }

  override def receiveRecover: Receive = {
    case evt: Evt ⇒ updateState(evt)
  }

  def updateState(evt: Evt): Unit = evt match {
    case MsgSent(s) ⇒
      deliver(router.path)(deliveryId ⇒ Msg(deliveryId, s))
    case MsgConfirmed(deliveryId) ⇒
      confirmDelivery(deliveryId)
  }

  override def preStart = {
    self ! Start
  }

}
