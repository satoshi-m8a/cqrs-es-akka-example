package nv.discussion.port.adapter.event

import akka.persistence.journal.{ EventAdapter, EventSeq }
import nv.discussion.port.adapter.serializer.EventDeserializationSkipped

class SkippedEventsAwareAdapter extends EventAdapter {
  override def manifest(event: Any) = ""

  override def toJournal(event: Any) = event

  override def fromJournal(event: Any, manifest: String) = event match {
    case EventDeserializationSkipped ⇒ EventSeq.empty

    case _                           ⇒ EventSeq(event)
  }
}