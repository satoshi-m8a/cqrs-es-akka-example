import com.typesafe.sbt.SbtScalariform
import com.typesafe.sbt.SbtScalariform.ScalariformKeys
import com.typesafe.sbt.packager.archetypes.JavaAppPackaging
import play.sbt.PlayImport._
import play.sbt.{PlayLayoutPlugin, PlayScala}
import sbt.Keys._
import sbt._
import sbtprotobuf.{ProtobufPlugin => PB}

import scalariform.formatter.preferences._

object Build extends sbt.Build {

  val akkaVersion = "2.4.2"
  val scalaTestVersion = "2.2.6"
  val slickVersion = "3.1.1"

  val commonSettings = Seq(
    scalaVersion := "2.11.7",
    organization := "nv"
  ) ++ SbtScalariform.scalariformSettings ++ Seq(
    ScalariformKeys.preferences := ScalariformKeys.preferences.value
      .setPreference(AlignSingleLineCaseStatements, true)
      .setPreference(DoubleIndentClassDeclaration, true)
      .setPreference(RewriteArrowSymbols, true)
      .setPreference(PlaceScaladocAsterisksBeneathSecondAsterisk, true)
  )

  val commonDependencies = Seq(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-persistence" % akkaVersion,
      "com.typesafe.akka" %% "akka-persistence-query-experimental" % akkaVersion,
      "com.typesafe.akka" %% "akka-cluster-sharding" % akkaVersion,
      "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,
      "com.typesafe.akka" %% "akka-persistence-cassandra" % "0.11",
      "com.typesafe.akka" %% "akka-distributed-data-experimental" % akkaVersion,
      "org.iq80.leveldb" % "leveldb" % "0.7",
      "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8",
      "com.typesafe.slick" %% "slick" % slickVersion,
      "ch.qos.logback" % "logback-classic" % "1.1.6",
      "com.h2database" % "h2" % "1.4.191" % "test",
      "org.scalatest" %% "scalatest" % scalaTestVersion % "test",
      "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test")
  )

  val playCommonSettings = Seq(
    libraryDependencies ++= Seq(
      cache,
      ws,
      filters,
      "com.typesafe.play" %% "play-slick" % "2.0.0",
      "com.typesafe.play" %% "play-slick-evolutions" % "2.0.0",
      "com.h2database" % "h2" % "1.4.191"
    )
  )

  lazy val root = Project(
    id = "nv-cms",
    base = file("."),
    settings = commonSettings ++ commonDependencies
  )
    .aggregate(common, apiServer, site, discussion, account, market, purchase, analysis, buildServer, testkit)
    .dependsOn(apiServer, analysisServer)

  // onLoad in Global := (Command.process("project nv-server", _: State)) compose (onLoad in Global).value

  lazy val common = Project(
    id = "nv-common",
    base = file("nv-common"),
    settings = commonSettings ++ commonDependencies
  )

  lazy val testkit = Project(
    id = "nv-testkit",
    base = file("nv-testkit"),
    settings = commonSettings ++ commonDependencies ++ Seq(
      libraryDependencies ++= Seq(
        "org.scalatest" %% "scalatest" % scalaTestVersion
      )
    )
  )

  lazy val site = Project(
    id = "nv-site",
    base = file("nv-site"),
    settings = commonSettings ++ commonDependencies
  ).dependsOn(common, account, discussion, testkit % "test")

  lazy val discussion = Project(
    id = "nv-discussion",
    base = file("nv-discussion"),
    settings = commonSettings ++ commonDependencies ++ PB.protobufSettings
  ).dependsOn(common, account, testkit % "test")

  lazy val account = Project(
    id = "nv-account",
    base = file("nv-account"),
    settings = commonSettings ++ commonDependencies
  ).dependsOn(common, testkit % "test")

  lazy val market = Project(
    id = "nv-market",
    base = file("nv-market"),
    settings = commonSettings ++ commonDependencies
  ).dependsOn(common, site, testkit % "test")

  lazy val purchase = Project(
    id = "nv-purchase",
    base = file("nv-purchase"),
    settings = commonSettings ++ commonDependencies
  ).dependsOn(common, site, account, market, testkit % "test")

  lazy val analysis = Project(
    id = "nv-analysis",
    base = file("nv-analysis"),
    settings = commonSettings ++ commonDependencies
  ).dependsOn(market, discussion, testkit % "test")

  lazy val buildServer = Project(
    id = "nv-build-server",
    base = file("server/nv-build-server"),
    settings = commonSettings ++ commonDependencies
  ).enablePlugins(PlayScala)
    .disablePlugins(PlayLayoutPlugin)
    .dependsOn(common, site, discussion, testkit % "test")

  lazy val apiServer = Project(
    id = "nv-api-server",
    base = file("server/nv-api-server"),
    settings = commonSettings ++ commonDependencies ++ playCommonSettings
  ).enablePlugins(PlayScala)
    .disablePlugins(PlayLayoutPlugin)
    .dependsOn(common, site, discussion, account, purchase)

  lazy val analysisServer = Project(
    id = "nv-analysis-server",
    base = file("server/nv-analysis-server"),
    settings = commonSettings ++ commonDependencies ++ playCommonSettings
  ).enablePlugins(PlayScala)
    .disablePlugins(PlayLayoutPlugin)
    .dependsOn(common, site, discussion, account, purchase, analysis)

  lazy val batch = Project(
    id = "nv-batch",
    base = file("batch/nv-batch"),
    settings = commonSettings ++ commonDependencies
  ).enablePlugins(JavaAppPackaging)
    .dependsOn(common, site, discussion, account, purchase, analysis)
}