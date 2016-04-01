package nv.batch

import akka.actor.ActorSystem
import akka.cluster.Cluster

import scala.concurrent.Await
import scala.concurrent.duration._

object Main extends App {

  implicit val system = ActorSystem("application")

  val cluster = Cluster(system)

  /**
    * バッチ実行時のみクラスタにジョインする。
    * バッチ終了後にleaveする。
    */
  cluster.registerOnMemberUp {
    val f = ExampleBatch.run

    val r = Await.result(f, 120.seconds)

    println("stop")
    println(r)

    cluster.leave(cluster.selfAddress)
  }
  cluster.registerOnMemberRemoved(system.terminate())
}
