package nv.testkit.persistent

import java.io.File

import akka.testkit.TestKit
import org.iq80.leveldb.util.FileUtils
import org.scalatest.BeforeAndAfterAll

trait StorageSpecSupport {
  this: TestKit with BeforeAndAfterAll ⇒

  val storageLocations = List(
    new File(system.settings.config.getString("akka.persistence.journal.leveldb.dir")),
    new File(system.settings.config.getString("akka.persistence.snapshot-store.local.dir"))
  )

  override def afterAll {
    storageLocations foreach FileUtils.deleteRecursively
  }

}
