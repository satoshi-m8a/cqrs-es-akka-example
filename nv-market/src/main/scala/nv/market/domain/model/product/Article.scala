package nv.market.domain.model.product

import nv.account.domain.model.account.AccountId

case class Article(id: ProductId, sellerId: AccountId, buyers: Seq[AccountId]) extends Product
