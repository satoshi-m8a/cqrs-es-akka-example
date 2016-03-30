package nv.analysis.infrastructure.pump

import akka.actor._
import akka.cluster.singleton.{ ClusterSingletonManager, ClusterSingletonManagerSettings, ClusterSingletonProxy, ClusterSingletonProxySettings }
import akka.pattern.ask
import akka.util.Timeout
import nv.analysis.application.WordCountService
import nv.analysis.infrastructure.pump.DiscussionEventPump.Start
import nv.common.ddd.infrastructure.projection.ResumableProjectionUpdater
import nv.discussion.domain.model.discussion.Discussion.Events.CommentAdded

import scala.concurrent.Future
import scala.concurrent.duration._

object DiscussionEventPump {
  def props(wordCountService: WordCountService, projectionUpdater: ResumableProjectionUpdater)(implicit system: ActorSystem) = ClusterSingletonManager.props(
    singletonProps = Props(new DiscussionEventPump(wordCountService, projectionUpdater)),
    terminationMessage = PoisonPill,
    settings = ClusterSingletonManagerSettings(system)
  )

  def proxyProps(singletonName: String)(implicit system: ActorSystem) = ClusterSingletonProxy.props(
    singletonManagerPath = s"/user/$singletonName",
    settings = ClusterSingletonProxySettings(system)
  )

  case object Start

}

class DiscussionEventPump(wordCountService: WordCountService, projectionUpdater: ResumableProjectionUpdater) extends Actor {

  implicit val timeout = Timeout(5.seconds)

  override def preStart = {
    self ! Start
  }

  override def receive: Receive = {
    case Start ⇒
      implicit val system = context.system
      import context.dispatcher
      projectionUpdater.startProjection {
        case evt: CommentAdded ⇒
          self ? evt
      }
    case evt: CommentAdded ⇒
      import context.dispatcher
      val replyTo = sender()
      wordCountService.count(evt.comment.text).map { e ⇒ replyTo ! e }

  }
}
