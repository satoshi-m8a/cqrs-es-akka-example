package nv.purchase.infrastructure.notification

import akka.actor.ActorSystem
import akka.testkit.TestProbe
import nv.account.domain.model.account.AccountId
import nv.common.ddd.infrastructure.projection.ResumableProjectionUpdater
import nv.purchase.domain.model.order.Order
import nv.purchase.domain.service.purchase.PurchaseProcessManager.Events.PurchaseProcessCompleted
import nv.purchase.infrastructure.email.EmailService
import nv.testkit.support.TestSupport

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ ExecutionContext, Future }

class EmailNotificationSpec extends TestSupport {

  "EmailNotification" should {
    "work concurrently" in {

      val probe = TestProbe()

      val emailService = new EmailService {

        override def sendOrderConfirmedMail(to: AccountId): Future[String] = Future {
          val content = "content" + to.value
          probe.testActor ! content
          content
        }
      }

      val resumableUpdater = new ResumableProjectionUpdater {
        override def startProjection(update: (Any) ⇒ Future[Any], parallelism: Int)(implicit system: ActorSystem, ec: ExecutionContext): Unit = {
          update(PurchaseProcessCompleted(Order.nextId, AccountId("1")))
          update(PurchaseProcessCompleted(Order.nextId, AccountId("2")))
        }

        override val projectionId: String = "emaster"
      }

      val proxy = system.actorOf(EmailNotificationMaster.proxyProps("emaster"))

      val router = system.actorOf(EmailNotificationMaster.routerProps(emailService, proxy), name = "erouter")

      val master = system.actorOf(EmailNotificationMaster.props(router, resumableUpdater), name = "emaster")

      probe.expectMsgAllOf("content1", "content2")
    }
  }

}
