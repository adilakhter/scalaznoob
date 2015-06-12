name := "scalaznoob"

version := "1.0"

scalaVersion := "2.11.6"

organization := "org.xiaon.scalaz"

resolvers ++= Seq("Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Sonatype snapshots"  at "http://oss.sonatype.org/content/repositories/snapshots/",
  "Spray Repository"    at "http://repo.spray.io",
  "Spray Nightlies"     at "http://nightlies.spray.io/")

libraryDependencies ++= {
  Seq(
    "org.scalaz" %% "scalaz-core" % "7.0.6",
    "org.scalatest"     %% "scalatest" % "2.1.3" % "test",
    "org.specs2" %% "specs2" % "2.4.2" % "test",
    "org.scalacheck" %% "scalacheck" % "1.11.3" % "test",
    "com.chuusai" %% "shapeless" % "2.0.0",
    "com.opencsv" % "opencsv" % "3.3",
    "ch.qos.logback"         % "logback-classic"        % "1.1.2",
    "com.typesafe.akka"      %% "akka-slf4j"            % "2.3.6",
    "com.google.guava"       % "guava"                  % "18.0",
    "com.indeed"             % "java-dogstatsd-client"  % "2.0.7"

  )
}

initialCommands in console := "import scalaz._, Scalaz._"

scalacOptions ++= Seq(
  //  "-deprecation",
  "-encoding", "UTF-8"      // yes, this is 2 args
  //  "-feature",
  //  "-unchecked",
  //  "-Xfatal-warnings",
  //  "-Xlint",
  //  "-Yno-adapted-args",
  //  "-Ywarn-dead-code",        // N.B. doesn't work well with the ??? hole
  //  "-Ywarn-numeric-widen",
  //  "-Ywarn-value-discard",
  //  "-Xfuture"
)
