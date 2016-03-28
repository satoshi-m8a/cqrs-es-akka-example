logLevel := Level.Warn

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("org.scalariform" % "sbt-scalariform" % "1.6.0")

addSbtPlugin("com.github.gseitz" % "sbt-protobuf" % "0.5.1")

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.5.0")