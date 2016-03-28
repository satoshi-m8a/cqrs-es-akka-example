package nv.testkit.persistent

import org.scalatest.BeforeAndAfterAll

trait DbSpecSupport {
  this: BeforeAndAfterAll ⇒

  import slick.driver.H2Driver.api._

  val db = Database.forConfig("h2mem")

  override def afterAll {
    db.close()
  }

}