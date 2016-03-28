package nv.common.ddd.domain

import java.time.Instant

trait DomainEvent {
  val timestamp: Instant = Instant.now()
}
