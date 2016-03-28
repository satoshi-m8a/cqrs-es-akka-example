package nv.discussion.domain.model.discussion

import nv.account.domain.model.account.AccountId

case class Comment(text: String, by: Option[AccountId])
