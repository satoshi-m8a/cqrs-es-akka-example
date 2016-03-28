package nv.site.domain.model.site

sealed abstract class Role {
  def code: String
}

object Role {
  def fromString(s: String) = s match {
    case Admin.code  ⇒ Admin
    case Writer.code ⇒ Writer
    case _           ⇒ Unknown
  }

  case object Admin extends Role {
    override val code = "admin"
  }

  case object Writer extends Role {
    override val code = "writer"
  }

  case object Unknown extends Role {
    override val code = ""
  }

}