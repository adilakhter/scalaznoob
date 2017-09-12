name := "scalaznoob"

version := "1.0"

scalaVersion := "2.11.6"

organization := "org.xiaon.scalaz"

resolvers ++= Seq("Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Sonatype snapshots"  at "http://oss.sonatype.org/content/repositories/snapshots/",
  "Spray Repository"    at "http://repo.spray.io",
  "Spray Nightlies"     at "http://nightlies.spray.io/")

resolvers += "Scalaz Bintray Repo" at "https://dl.bintray.com/scalaz/releases"

libraryDependencies ++= {
  Seq(
    "org.scalaz" %% "scalaz-core" % "7.1.3",
    "org.specs2" %% "specs2-core" % "3.6.2" % "test"
  )
}

initialCommands in console := "import scalaz._, Scalaz._"

scalacOptions ++= Seq(
  //  "-deprecation",
  "-encoding", "UTF-8"      // yes, this is 2 args
  , "-Yrangepos" //  "-feature",
  //  "-unchecked",
  //  "-Xfatal-warnings",
  //  "-Xlint",
  //  "-Yno-adapted-args",
  //  "-Ywarn-dead-code",        // N.B. doesn't work well with the ??? hole
  //  "-Ywarn-numeric-widen",
  //  "-Ywarn-value-discard",
  //  "-Xfuture"
)
