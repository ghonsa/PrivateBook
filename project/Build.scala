import sbt._

object ApplicationBuild extends Build {

  val appName         = "PrivateBook"
  val appVersion      = "0.1-SNAPSHOT"

  val appDependencies = Seq(
    "com.google.inject" % "guice" % "3.0",
    "javax.inject" % "javax.inject" % "1",
    "org.reactivemongo" %% "reactivemongo" % "0.10.0",
    "org.reactivemongo" %% "play2-reactivemongo" % "0.10.2",
    "org.mindrot" % "jbcrypt" % "0.3m",
    "org.mockito" % "mockito-core" % "1.9.5" % "test",
    "com.amazonaws" % "aws-java-sdk" % "1.9.13",
    "com.github.nscala-time" %% "nscala-time" % "1.8.0"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here
   )

}