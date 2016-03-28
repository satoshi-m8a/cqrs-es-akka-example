package nv.purchase.domain.model.order

import nv.common.ddd.domain.EntityId

case class OrderId(value: String) extends EntityId[String]
