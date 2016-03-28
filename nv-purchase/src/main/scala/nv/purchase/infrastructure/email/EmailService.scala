package nv.purchase.infrastructure.email

import nv.account.domain.model.account.AccountId

import scala.concurrent.Future

trait EmailService {

  def sendOrderConfirmedMail(to: AccountId): Future[String]

}
