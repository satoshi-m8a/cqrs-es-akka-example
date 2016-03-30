package modules

import javax.inject.{ Inject, Singleton }

import akka.actor._
import nv.analysis.infrastructure.pump.DiscussionEventPump
import play.api.inject.{ ApplicationLifecycle, Module }
import play.api.{ Configuration, Environment }
import registry.AnalysisServiceRegistry

import scala.concurrent.Future

class ServerModule extends Module {
  def bindings(
    environment: Environment,
    configuration: Configuration
  ) = Seq(
    bind[ServerInterface].to[Server].eagerly
  )
}

@Singleton
class Server @Inject() (lifecycle: ApplicationLifecycle, serviceRegistry: AnalysisServiceRegistry, implicit val system: ActorSystem) extends ServerInterface {
  lifecycle.addStopHook(() ⇒ Future.successful {
    system.terminate
  })

  system.actorOf(DiscussionEventPump.props(serviceRegistry.wordCountService, serviceRegistry.pump), name = "DiscussionPump")

}

trait ServerInterface