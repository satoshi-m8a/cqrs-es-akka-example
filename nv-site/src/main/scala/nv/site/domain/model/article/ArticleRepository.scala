package nv.site.domain.model.article

import scala.concurrent.ExecutionContext

trait ArticleRepository[IO[+_]] {
  def save(article: Article)(implicit ec: ExecutionContext): IO[Article]
}
