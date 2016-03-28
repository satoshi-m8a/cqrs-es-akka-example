package nv.common.ddd.domain

trait EntityId[T] {
  val value: T
}