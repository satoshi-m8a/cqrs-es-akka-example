package nv.discussion.port.adapter.event

import akka.persistence.journal.{ Tagged, WriteEventAdapter }
import nv.discussion.domain.model.discussion.Discussion.Events.DiscussionEvent

class DiscussionEventAdapter extends WriteEventAdapter {

  override def manifest(event: Any): String = ""

  val tags = Set("Discussion")

  override def toJournal(event: Any): Any = event match {
    case e: DiscussionEvent ⇒
      Tagged(event, tags)
    case _ ⇒
      event
  }
}
