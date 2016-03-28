package nv.common.ddd.infrastructure.projection

import scala.concurrent.Future

trait ReadModelUpdater {
  type UpdateReadModel = PartialFunction[Any, Future[Any]]

  def handleUpdate: UpdateReadModel
}
