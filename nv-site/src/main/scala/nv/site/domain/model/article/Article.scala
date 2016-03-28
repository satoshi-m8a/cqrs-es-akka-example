package nv.site.domain.model.article

import nv.account.domain.model.account.AccountId
import nv.discussion.domain.model.discussion.DiscussionId
import nv.site.domain.model.site.{ CategoryId, SiteId }

case class Article(
  id: ArticleId,
  siteId: SiteId,
  categoryId: Option[CategoryId],
  discussionId: Option[DiscussionId],
  title: String,
  body: String,
  writtenBy: AccountId
)
