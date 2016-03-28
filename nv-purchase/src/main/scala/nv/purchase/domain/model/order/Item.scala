package nv.purchase.domain.model.order

import nv.market.domain.model.product.ProductId

case class Item(id: ProductId, price: Long, amount: Long)

object Item {
  def getTotalPoint(items: Set[Item]): Long = {
    items.map(i ⇒ i.price * i.amount).sum
  }
}
