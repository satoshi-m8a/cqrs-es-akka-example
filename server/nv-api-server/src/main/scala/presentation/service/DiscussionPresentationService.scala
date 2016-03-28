package presentation.service

import javax.inject.Inject

import nv.discussion.domain.model.discussion.DiscussionId
import nv.discussion.port.adapter.dto.DiscussionDto
import presentation.model.discussion.{ Discussion, CreateDiscussionRequest }
import registry.DiscussionServiceRegistry

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class DiscussionPresentationService @Inject() (registry: DiscussionServiceRegistry) {

  implicit def discussionIdToString(id: DiscussionId): String = {
    id.value
  }

  implicit def discussionDtoToDiscussion(dto: DiscussionDto): Discussion = {
    Discussion(dto.id, dto.title)
  }

  def createDiscussion(request: CreateDiscussionRequest): Future[Discussion] = {
    registry.discussionService.createDiscussion(request.title, request.allowAnonymous).map {
      evt ⇒
        Discussion(evt.id, evt.title)
    }
  }

  def findById(discussionId: DiscussionId): Future[Option[Discussion]] = {
    registry.discussionQueryService.findById(discussionId).map {
      case Some(dto) ⇒
        Some(dto)
      case _ ⇒
        None
    }
  }

}
