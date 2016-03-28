package nv.common.ddd.infrastructure

import slick.driver.JdbcProfile

case class DbConfig(db: JdbcProfile#Backend#Database, driver: JdbcProfile)
