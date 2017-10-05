name := "play-silhouette-rest-mongo"
 
version := "1.0" 

lazy val `play-silhouette-rest-mongo` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.3"

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= Seq(
  "org.reactivemongo" %% "play2-reactivemongo" % "0.12.6-play26",
  "com.mohiva" %% "play-silhouette" % "5.0.0",
  "com.mohiva" %% "play-silhouette-persistence" % "5.0.0",
  "com.mohiva" %% "play-silhouette-password-bcrypt" % "5.0.0",
  "com.mohiva" %% "play-silhouette-crypto-jca" % "5.0.0",
  "com.mohiva" %% "play-silhouette-testkit" % "5.0.0" % "test",
  "com.iheart" %% "ficus" % "1.4.1",
  "com.typesafe.play" %% "play-mailer" % "6.0.1",
  "com.typesafe.play" %% "play-mailer-guice" % "6.0.1",
  "com.enragedginger" %% "akka-quartz-scheduler" % "1.6.1-akka-2.5.x",
  "net.codingwell" %% "scala-guice" % "4.1.0",
  "com.adrianhurt" %% "play-bootstrap" % "1.2-P26-B3",
  "com.typesafe.play" %% "play-json" % "2.6.0",
  specs2 % Test,
  ehcache,
  guice
)

unmanagedResourceDirectories in Test += (baseDirectory.value / "target/web/public/test")

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

resolvers += "iheartradio-maven" at "https://dl.bintray.com/iheartradio/maven"

resolvers += "atlassian-maven" at "https://maven.atlassian.com/content/repositories/atlassian-public"
