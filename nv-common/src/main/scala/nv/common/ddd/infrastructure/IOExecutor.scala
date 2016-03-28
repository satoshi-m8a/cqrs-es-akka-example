package nv.common.ddd.infrastructure

import slick.dbio.DBIO

import scala.concurrent.Future

trait IOExecutor[IO[+_]] {

  def run[B](action: IO[B]): Future[B]

}

class IOExecutorSlick(dbConfig: DbConfig) extends IOExecutor[DBIO] {
  override def run[B](action: DBIO[B]): Future[B] = {
    dbConfig.db.run(action)
  }
}