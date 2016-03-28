package nv.common.ddd.infrastructure

object DaoHelpers {

  implicit class NameHelper(val sc: StringContext) extends AnyVal {
    def n(args: Any*): String = {
      sc.s(args: _*).toUpperCase
    }
  }

}
