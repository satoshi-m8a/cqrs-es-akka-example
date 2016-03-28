package nv.testkit.support

import java.net.ServerSocket
import java.util
import java.util.UUID

import akka.actor.ActorSystem
import com.typesafe.config.{ ConfigFactory, ConfigValueFactory }

case class TestEnv(id: String, system: ActorSystem)

object TestEnvFactory {
  def create: TestEnv = {
    val uuid = UUID.randomUUID().toString
    val systemName = "TestSystem-" + uuid.take(5)
    val port = new ServerSocket(0).getLocalPort

    val config = ConfigFactory.load()
      .withValue("akka.remote.netty.tcp.port", ConfigValueFactory.fromAnyRef(port))
      .withValue("akka.cluster.seed-nodes", ConfigValueFactory.fromIterable(util.Arrays.asList(s"akka.tcp://$systemName@127.0.0.1:$port")))
      .withValue("akka.persistence.journal.leveldb.dir", ConfigValueFactory.fromAnyRef(s"target/journal/$uuid"))

    TestEnv(uuid, ActorSystem(systemName, config))
  }
}