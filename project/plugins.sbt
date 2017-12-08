logLevel := Level.Warn

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
resolvers += Resolver.sonatypeRepo("releases")

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.7")
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.1")