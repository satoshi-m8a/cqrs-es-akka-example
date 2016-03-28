package nv.market.application

import nv.account.domain.model.account.AccountId
import nv.market.domain.model.product.ProductId

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait ProductService {
  def buy(items: Seq[ProductId], accountId: AccountId): Future[Seq[ProductId]]

  def cancel(items: Seq[ProductId], accountId: AccountId): Future[Seq[ProductId]]
}

class ProductServiceImpl extends ProductService {
  def buy(items: Seq[ProductId], accountId: AccountId): Future[Seq[ProductId]] = {
    //TODO
    Future {
      items
    }
  }

  override def cancel(items: Seq[ProductId], accountId: AccountId): Future[Seq[ProductId]] = {
    //TODO
    Future {
      items
    }
  }
}
