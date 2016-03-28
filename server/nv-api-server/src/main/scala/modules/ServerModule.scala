package modules

import javax.inject.{ Inject, Singleton }

import akka.actor._
import play.api.inject.{ ApplicationLifecycle, Module }
import play.api.{ Configuration, Environment }
import registry.DiscussionServiceRegistry

import scala.concurrent.Future

//TODO

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

  Projections.start(Seq(discussionServiceRegistry.discussionProjection))

  println("Start")
}

trait ServerInterface