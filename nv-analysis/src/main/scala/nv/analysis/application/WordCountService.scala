package nv.analysis.application

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

//TODO
class WordCountService {

  def count(word: String): Future[Unit] = {
    Future {
      println(word)
    }
  }
}
