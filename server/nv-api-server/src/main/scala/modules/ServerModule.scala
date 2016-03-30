package modules

import javax.inject.{ Inject, Singleton }

import akka.actor._
import play.api.inject.{ ApplicationLifecycle, Module }
import play.api.{ Configuration, Environment }
import registry.DiscussionServiceRegistry

import scala.concurrent.Future

//TODO
import scala.concurrent.ExecutionContext.Implicits.global

class ServerModule extends Module {
  def bindings(
    environment: Environment,
    configuration: Configuration
  ) = Seq(
    bind[ServerInterface].to[Server].eagerly
  )
}

@Singleton
class Server @Inject() (lifecycle: ApplicationLifecycle, discussionServiceRegistry: DiscussionServiceRegistry, implicit val system: ActorSystem) extends ServerInterface {
  lifecycle.addStopHook(() ⇒ Future.successful {
    system.terminate
  })

  Regions.start

  /**
    * 指定のロールのときだけ、プロジェクションを立ち上げる。
    */
  val roles = system.settings.config.getStringList("akka.cluster.roles")
  if (roles.contains("discussion-projection")) {
    discussionServiceRegistry.discussionProjectionUpdater.startProjection(discussionServiceRegistry.discussionProjection.update)
  }
}

trait ServerInterface