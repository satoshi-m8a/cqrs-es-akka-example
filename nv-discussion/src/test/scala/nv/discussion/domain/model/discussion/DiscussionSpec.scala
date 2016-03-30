package nv.discussion.domain.model.discussion

import akka.actor.Status.Failure
import nv.account.domain.model.account.AccountId
import nv.common.ddd.domain.AggregateRoot.Commands.GetState
import nv.discussion.domain.model.discussion.Discussion.Commands.{ AddComment, CreateDiscussion, DeleteComment, EditComment }
import nv.discussion.domain.model.discussion.Discussion.Events.{ CommentAdded, CommentDeleted, CommentEdited, DiscussionCreated }
import nv.discussion.domain.model.discussion.Discussion.Exceptions.EditNotAllowed
import nv.testkit.support.TestSupport

class DiscussionSpec extends TestSupport {

  "Discussion" should {
    val id = Discussion.nextId
    val discussion = system.actorOf(Discussion.props)

    "create" in {
      discussion ! CreateDiscussion(id, "test discussion", allowAnonymous = true)

      expectMsg(DiscussionCreated(id, "test discussion", true))

      discussion ! GetState

      expectMsg(DiscussionState("test discussion", true, Map.empty))
    }

    "add comment" in {
      discussion ! AddComment(id, Comment("c1", Some(AccountId("1"))))

      expectMsg(CommentAdded(id, 1, Comment("c1", Some(AccountId("1")))))

      discussion ! GetState

      expectMsg(DiscussionState("test discussion", true, Map(1L → Some(AccountId("1")))))
    }

    "add anonymous comment" in {
      discussion ! AddComment(id, Comment("c2", None))

      expectMsg(CommentAdded(id, 2, Comment("c2", None)))

      discussion ! GetState

      expectMsg(DiscussionState("test discussion", true, Map(1L → Some(AccountId("1")), 2L → None)))
    }

    "edit comment" in {
      discussion ! EditComment(id, 1, Comment("ce1", Some(AccountId("1"))))

      expectMsg(CommentEdited(id, 1, Comment("ce1", Some(AccountId("1")))))

      discussion ! GetState

      expectMsg(DiscussionState("test discussion", true, Map(1L → Some(AccountId("1")), 2L → None)))
    }

    "not edit other account's comment" in {
      discussion ! EditComment(id, 1, Comment("ce1", Some(AccountId("2"))))

      expectMsg(Failure(EditNotAllowed))

      discussion ! GetState

      expectMsg(DiscussionState("test discussion", true, Map(1L → Some(AccountId("1")), 2L → None)))
    }

    "not edit comment by anonymous user" in {
      discussion ! EditComment(id, 1, Comment("ce1", None))

      expectMsg(Failure(EditNotAllowed))

      discussion ! GetState

      expectMsg(DiscussionState("test discussion", true, Map(1L → Some(AccountId("1")), 2L → None)))
    }

    "delete comment" in {
      discussion ! DeleteComment(id, 1, AccountId("1"))

      expectMsg(CommentDeleted(id, 1, AccountId("1")))

      discussion ! GetState

      expectMsg(DiscussionState("test discussion", true, Map(2L → None)))
    }
  }
}
