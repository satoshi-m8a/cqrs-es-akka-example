package nv.site.domain.model.site

import nv.account.domain.model.account.AccountId

/**
  * 管理者や編集者など、サイトの管理に関わるメンバー
  * @param id
  * @param role
  */
case class Member(id: AccountId, role: Role)
