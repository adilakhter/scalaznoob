name := "scalaznoob"

version := "1.0"

scalaVersion := "2.11.8"

organization := "org.xiaon.scalaz"

tutSettings
site.settings
site.addMappingsToSiteDir(tut, "tut")

resolvers ++= Seq(
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/",
  "Spray Repository" at "http://repo.spray.io",
  "Spray Nightlies" at "http://nightlies.spray.io/",
  "Sonatype Public" at "https://oss.sonatype.org/content/groups/public/",
  "bintray/non" at "http://dl.bintray.com/non/maven"
)

resolvers += "jgit-repo" at "http://download.eclipse.org/jgit/maven"

//addCompilerPlugin("org.scalamacros" % "paradise"        % "2.1.0-M5" cross CrossVersion.full)
addCompilerPlugin("org.spire-math"  %% "kind-projector" % "0.7.1")

libraryDependencies ++= {
  Seq(
    "org.scalaz"           %% "scalaz-core"          % "7.2.15",
    "org.scalatest"        %% "scalatest"            % "2.1.3" % "test",
    "org.specs2"           %% "specs2"               % "2.4.2" % "test",
    "org.scalacheck"       %% "scalacheck"           % "1.11.3" % "test",
    "com.chuusai"          %% "shapeless"            % "2.0.0",
    "com.opencsv"          % "opencsv"               % "3.3",
    "ch.qos.logback"       % "logback-classic"       % "1.1.2",
    "com.typesafe.akka"    %% "akka-slf4j"           % "2.3.6",
    "com.google.guava"     % "guava"                 % "18.0",
    "com.indeed"           % "java-dogstatsd-client" % "2.0.7",
    "com.github.mpilquist" %% "simulacrum"           % "0.5.0",
    "com.jayway.jsonpath"  % "json-path"             % "2.3.0",
    "com.lihaoyi"          % "ammonite"              % "1.0.3" % "test" cross CrossVersion.full
  )
}

libraryDependencies += "org.eclipse.jgit" % "org.eclipse.jgit" % "4.8.0.201706111038-r"


val circeVersion = "0.9.0-M2"
libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

initialCommands in console := "import scalaz._, Scalaz._"

scalacOptions ++= Seq(
  //  "-deprecation",
  "-encoding",
  "UTF-8" // yes, this is 2 args
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

initialCommands in (Test, console) := """ammonite.Main().run()"""
