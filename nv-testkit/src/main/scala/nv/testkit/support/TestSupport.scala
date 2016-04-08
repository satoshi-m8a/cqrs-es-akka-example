package nv.testkit.support

import akka.testkit.{ ImplicitSender, TestKit, TestKitExtension }
import nv.testkit.persistent.StorageSpecSupport
import org.scalatest.concurrent.{ ScalaFutures, ScaledTimeSpans }
import org.scalatest.{ BeforeAndAfterAll, Matchers, WordSpecLike }

abstract class TestSupport(testEnv: TestEnv = TestEnvFactory.create) extends TestKit(testEnv.system) with ImplicitSender
    with WordSpecLike with Matchers with ScalaFutures with BeforeAndAfterAll with StorageSpecSupport with ScaledTimeSpans {

  override def spanScaleFactor: Double =
    TestKitExtension.get(system).TestTimeFactor

  override def afterAll {
    TestKit.shutdownActorSystem(system)
    super.afterAll
  }
}
