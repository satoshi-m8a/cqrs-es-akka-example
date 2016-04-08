package nv.purchase.application

import nv.account.domain.model.account.AccountId
import nv.common.ddd.application.CommandService
import nv.purchase.domain.model.pointwallet.PointWallet.Commands.GetCurrentPoint

import scala.concurrent.Future

class PointService(pointCommandService: CommandService) {

  /**
    * コマンドサイドでクエリのように問い合わせている例
    * @param id
    * @return
    */
  def getCurrentPoint(id: AccountId): Future[Long] = {
    //現在のポイントを返す。プロセスマネージャーでキャンセル処理中の場合は正しいポイントが返ってくるかはわからない。
    pointCommandService.send(GetCurrentPoint(id)).mapTo[Long]
  }

}
