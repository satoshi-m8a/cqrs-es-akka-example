package nv.site.domain.model.site

import scala.concurrent.ExecutionContext

trait SiteRepository[IO[+_]] {

  def save(site: Site)(implicit ec: ExecutionContext): IO[Site]
}
