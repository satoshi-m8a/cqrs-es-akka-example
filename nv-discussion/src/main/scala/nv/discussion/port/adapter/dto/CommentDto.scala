package nv.discussion.port.adapter.dto

import nv.account.domain.model.account.AccountId
import nv.discussion.domain.model.discussion.DiscussionId

case class CommentDto(id: DiscussionId, commentId: Long, text: String, commentBy: Option[AccountId])