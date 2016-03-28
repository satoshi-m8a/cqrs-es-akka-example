package nv.discussion.port.adapter.dto

import nv.discussion.domain.model.discussion.DiscussionId

case class DiscussionDto(id: DiscussionId, title: String, allowAnonymous: Boolean)