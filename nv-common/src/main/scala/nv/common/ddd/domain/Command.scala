package nv.common.ddd.domain

import java.time.Instant
import java.util.UUID

trait Command[ID <: EntityId[_]] {
  val id: ID

  val timestamp: Instant = Instant.now()
  val commandId: String = UUID.randomUUID().toString
}
