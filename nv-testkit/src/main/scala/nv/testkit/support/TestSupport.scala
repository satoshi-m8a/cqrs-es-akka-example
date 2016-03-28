package nv.testkit.support

import akka.testkit.{ ImplicitSender, TestKit }
import nv.testkit.persistent.StorageSpecSupport
import org.scalatest.{ BeforeAndAfterAll, Matchers, WordSpecLike }

abstract class TestSupport(testEnv: TestEnv = TestEnvFactory.create) extends TestKit(testEnv.system) with ImplicitSender
    with WordSpecLike with Matchers with BeforeAndAfterAll with StorageSpecSupport {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
    super.afterAll
  }
}
