package nv.purchase.infrastructure.notification

import akka.actor.{ Actor, ActorLogging, ActorRef, Props }
import nv.purchase.infrastructure.email.EmailService
import nv.purchase.infrastructure.notification.EmailNotificationMaster.Commands.{ Confirm, Msg, SendOrderConfirmedEmail }

//TODO
import scala.concurrent.ExecutionContext.Implicits.global

object EmailNotificationWorker {
  def props(emailService: EmailService, singletonProxy: ActorRef) = Props(new EmailNotificationWorker(emailService, singletonProxy))
}

class EmailNotificationWorker(emailService: EmailService, singletonProxy: ActorRef) extends Actor with ActorLogging {
  override def receive: Receive = {
    case Msg(deliveryId, msg) ⇒
      msg match {
        case m: SendOrderConfirmedEmail ⇒
          emailService.sendOrderConfirmedMail(m.accountId).map { _ ⇒
            log.info("path: {} , msg: {}", self.path.name, msg)
            singletonProxy ! Confirm(deliveryId)
          }
      }

  }
}
