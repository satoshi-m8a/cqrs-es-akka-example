package nv.market.domain.model.product

import nv.account.domain.model.account.AccountId

trait Product {
  val id: ProductId
  val sellerId: AccountId
  val buyers: Seq[AccountId]
}
