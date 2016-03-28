package nv.market.domain.model.product

import nv.common.ddd.domain.EntityId

case class ProductId(value: String) extends EntityId[String]
