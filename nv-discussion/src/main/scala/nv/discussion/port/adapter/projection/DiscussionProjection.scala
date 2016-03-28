package nv.discussion.port.adapter.projection

import nv.common.ddd.infrastructure.projection.Projection

import scala.concurrent.Future

class DiscussionProjection(discussionUpdater: DiscussionUpdater, commentUpdater: CommentUpdater) extends Projection {

  val projectionId: String = "Discussion"

  def update(event: Any): Future[Any] = (discussionUpdater.handleUpdate orElse commentUpdater.handleUpdate)(event)

}
