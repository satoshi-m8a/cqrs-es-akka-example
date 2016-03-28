package nv.site.domain.model.site

import java.util.UUID

object Site {
  def nextId: SiteId = SiteId(UUID.randomUUID().toString)
}

case class Site(id: SiteId, name: String, categoryTree: CategoryTree, members: Seq[Member]) {

  def addMember(member: Member): Site = {
    val index = members.map(_.id).indexOf(member.id)

    copy(members =
      if (index > -1) {
        this.members.updated(index, member)
      } else {
        this.members :+ member
      })

  }

}
