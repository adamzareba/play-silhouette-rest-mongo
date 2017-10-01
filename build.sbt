name := "play-silhouette-rest-mongo"
 
version := "1.0" 

lazy val `play-silhouette-rest-mongo` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.11"

libraryDependencies ++= Seq(cache , ws , specs2 % Test )

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= Seq(
  "org.reactivemongo" %% "play2-reactivemongo" % "0.11.14",
  "com.mohiva" %% "play-silhouette" % "4.0.0",
  "com.mohiva" %% "play-silhouette-persistence" % "4.0.0",
  "com.mohiva" %% "play-silhouette-password-bcrypt" % "4.0.0",
  "com.mohiva" %% "play-silhouette-crypto-jca" % "4.0.0",
  "com.mohiva" %% "play-silhouette-testkit" % "4.0.0" % "test",
  "com.iheart" %% "ficus" % "1.2.6",
  "com.adrianhurt" %% "play-bootstrap" % "1.1.2-P25-B3-SNAPSHOT"
)

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

resolvers += "iheartradio-maven" at "https://dl.bintray.com/iheartradio/maven"

resolvers += "atlassian-maven" at "https://maven.atlassian.com/content/repositories/atlassian-public"
